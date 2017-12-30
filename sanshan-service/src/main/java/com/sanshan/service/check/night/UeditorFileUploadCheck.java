package com.sanshan.service.check.night;

import com.sanshan.service.editor.UeditorFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 */
@Component
@Slf4j
public class UeditorFileUploadCheck {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UeditorFileService ueditorFileService;

    public void check(){
       Map<String,Integer> fileQuoteMap= redisTemplate.opsForHash().entries(UeditorFileService.UEDITOR_UPLOAD_FILE);
       //首先检查引用为0的 然后查看引用0的在UEDITOR_UPLOAD_TEMP_FILE缓存中是否存在
        for(Map.Entry<String,Integer> entry:fileQuoteMap.entrySet()){
            if (entry.getValue().equals(0)) {
                Boolean contain = redisTemplate.opsForValue().get(UeditorFileService.UEDITOR_UPLOAD_TEMP_FILE+entry.getKey()) != null ? true : false;
                //不包含的情况
                if (!contain){
                    ueditorFileService.deleteFile(entry.getKey());
                    redisTemplate.opsForHash().delete(UeditorFileService.UEDITOR_UPLOAD_FILE, entry.getKey());
                    redisTemplate.opsForSet().remove(UeditorFileService.UEDITOR_TMEP_FILENAME_SET, entry.getKey());
                }
            }
        }
    }


}