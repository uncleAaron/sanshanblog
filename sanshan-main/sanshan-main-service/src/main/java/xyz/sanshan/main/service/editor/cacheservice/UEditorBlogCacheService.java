package xyz.sanshan.main.service.editor.cacheservice;

import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import xyz.sanshan.main.pojo.entity.UEditorBlogDO;
import xyz.sanshan.main.service.BaseServiceImpl;

import java.util.List;

/**
 * @author sanshan
 * www.85432173@qq.com
 * DO存到缓存中
 */
@Service
public class UEditorBlogCacheService extends BaseServiceImpl<UEditorBlogDO> {

    @Override
    public List<UEditorBlogDO> queryAll() {
        return super.queryAll();
    }



    @Cacheable({"ueditor-blog"})
    @Override
    public List<UEditorBlogDO> queryListByWhere(UEditorBlogDO example) {
        return super.queryListByWhere(example);
    }

    @Cacheable(value = {"ueditor-blog"},key = "'ueditor-blog:'+#a0")
    @Override
    public UEditorBlogDO queryById(Long id) {
        return super.queryById(id);
    }


    @Override
    public PageInfo<UEditorBlogDO> queryPageListByWhere(UEditorBlogDO example, Integer page, Integer rows) {
        return super.queryPageListByWhere(example, page, rows);
    }


    @Override
    public Integer save(UEditorBlogDO uEditorBlog) {
        return super.save(uEditorBlog);
    }


    @CacheEvict(value = {"ueditor-blog"}, key = "'ueditor-blog:'+#a0")
    @Override
    public Integer deleteById(Long id) {
        return super.deleteById(id);
    }


    @CachePut(value = {"ueditor-blog"},key = "'ueditor-blog:'+#a0.id")
    @Override
    public UEditorBlogDO update(UEditorBlogDO uEditorBlogDO) {
        return super.update(uEditorBlogDO);
    }

    @Override
    @CachePut(value = {"ueditor-blog"},key = "'ueditor-blog:'+#a0.id")
    public UEditorBlogDO updateSelective(UEditorBlogDO uEditorBlogDO) {
        super.updateSelective(uEditorBlogDO);
        //cache注解是通过切面实现的 调用同一类中的方法不会用到缓存 直接访问数据库获取
        return queryById(uEditorBlogDO.getId());
    }

}
