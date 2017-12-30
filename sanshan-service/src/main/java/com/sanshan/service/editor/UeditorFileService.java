package com.sanshan.service.editor;

import com.sanshan.service.upload.QiniuStorageManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *TODO 将这里缓存的数据延迟存入到数据库中
 * 保持不需要缓存的可用性设计
 */
@Slf4j
@Service
public class UeditorFileService {


    @Autowired
    private QiniuStorageManager qiniuStorageManager;

    @Autowired
    private RedisTemplate redisTemplate;

    public static  final String UEDITOR_UPLOAD_TEMP_FILE="ueditor_upload:tmep_file:";

    public static final String UEDITOR_TMEP_FILENAME_SET = "ueditor_upload:temp_file_set:";

    public  static  final  String UEDITOR_UPLOAD_FILE="ueditor_upload:file:";

    public static final String UEDITOR_UPLOAD_ID_FILE_MAP="ueditor_upload:id_file_map:";

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

    private ExecutorService pool = new ThreadPoolExecutor(0, 4,
            3, TimeUnit.MINUTES,
            new SynchronousQueue<Runnable>(),(r)->{
        Thread t = new Thread(r);
        t.setName("ueditor-Checkfile-thread:"+POOL_NUMBER.incrementAndGet());
        return t;
    });

    /**
     *  存入图片到七牛云
     * @param content  文件
     * @param filename 文件名
     * @param type 类型
     * @param metedata 元数据 元数据其实就是数据的数据 其他附带的一些信息
     */
    public void saveFile(InputStream content, String filename, String type, Object metedata) {
        qiniuStorageManager.ueditorUpload(content, filename,type,metedata);
        //上传过的暂时文件 12小时候后从这个缓存中消失 代表这个暂存文件只存在12小时
        redisTemplate.opsForValue().set(UEDITOR_UPLOAD_TEMP_FILE+filename,filename,12, TimeUnit.HOURS);
        //将暂存文件名存入到一个专门的Set中
        redisTemplate.opsForSet().add(UEDITOR_TMEP_FILENAME_SET,filename);
        //上传过文件的列表 一天更新一次 在晚上1点凌晨进行比对
        // 如果是在UEDITOR_UPLOAD_TEMP_FILE缓存没有 这一个缓存中有的文件就进行审核 值为0的代表0人引用 需要将这条缓存中以及暂存文件名的set中也删除
        redisTemplate.opsForHash().put(UEDITOR_UPLOAD_FILE, filename, 0);
    }

    /**
     * 检测Ueditor内容中的文件 并且提交到缓存中作记录
     *
     * @param id      Ueditor的博客ID
     * @param content 文件
     */
    protected void checkUeditorContentFile(Long id, String content) {
        pool.execute(() -> {
            Set<String> filenameSet = redisTemplate.opsForSet().members(UEDITOR_TMEP_FILENAME_SET);
            String[] filenames = filenameSet.toArray(new String[]{});
            List<String> blogFiles = new LinkedList<>();
            for (int i = 0; i < filenames.length; i++) {
                String filename= filenames[i];
                Boolean contain = StringUtils.contains(content, filename);
                if (contain) {
                    //增加一个博客的引用
                    redisTemplate.opsForHash().increment(UEDITOR_UPLOAD_FILE, filename, 1);
                    blogFiles.add(filename);
                }
            }
            //在ID与文件对应表中进行关联
            redisTemplate.opsForHash().put(UEDITOR_UPLOAD_ID_FILE_MAP, id, blogFiles);

        });
    }

    /**
     * 删除博客ID对应表 并且减少文件的引用数
     * @param blogId
     */
    public void deleteContentContainsFile(Long blogId) {
        pool.execute(()->{
            List<String> filenames;
            filenames = (List<String>) redisTemplate.opsForHash().get(UEDITOR_UPLOAD_ID_FILE_MAP, blogId);
            //删除该博客ID文件对应表
            redisTemplate.opsForHash().delete(UEDITOR_UPLOAD_ID_FILE_MAP, blogId);
            for (int i = 0; i <filenames.size() ; i++) {
                String filename = filenames.get(i);
                redisTemplate.opsForHash().increment(UEDITOR_UPLOAD_FILE, filename, -1);
            }
        });
    }


    /**
     * 删除图片
     * @param filename 文件名
     */
    public void deleteFile(String filename){
        qiniuStorageManager.ueditorDeleteFile(filename);
    }


}