package com.sanshan.service.editor;

import com.github.pagehelper.PageInfo;
import com.sanshan.pojo.dto.MarkDownBlogDTO;
import com.sanshan.pojo.entity.MarkDownBlogDO;
import com.sanshan.service.convent.MarkDownEditorConvert;
import com.sanshan.service.editor.CacheService.MarkDownBlogCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarkDownBlogService {
    @Autowired
    private   MarkDownBlogCacheService cacheService;

    /**
     * DTO查询
     *
     * @return
     */
    public List<MarkDownBlogDTO> queryDtoAll() {
        return MarkDownEditorConvert.doToDtoList(cacheService.queryAll());
    }


    /**
     * 通过DTO查询
     * @param tag
     * @return
     */
    public List<MarkDownBlogDTO> queryByTag(String tag){
        MarkDownBlogDO markDownBlogDO = new MarkDownBlogDO();
        markDownBlogDO.setTag(tag);
        return MarkDownEditorConvert.doToDtoList(cacheService.queryByTag(markDownBlogDO));
    }


    public List<MarkDownBlogDTO> queryByTitle(String title) {
        MarkDownBlogDO markDownBlogDO = new MarkDownBlogDO();
        markDownBlogDO.setTitle(title);
        return MarkDownEditorConvert.doToDtoList(cacheService.queryByTitle(markDownBlogDO));
    }

    /**
     * DTO查询
     * @param example 查询条件
     * @return
     */
    public List<MarkDownBlogDTO> queryDtoListByWhere(MarkDownBlogDO example) {
        return MarkDownEditorConvert.doToDtoList(cacheService.queryListByWhere(example));
    }


    /** DTO查询
     *
     */
    public MarkDownBlogDTO queryDtoById(Long id){
        MarkDownBlogDO markDownBlogDO = new MarkDownBlogDO();
        markDownBlogDO.setId(id);
        return MarkDownEditorConvert.doToDto(cacheService.queryOne(markDownBlogDO));
    }

    /**
     * DTO查询
     *
     * @param example 条件
     * @param page    页数
     * @param rows    行数
     * @return
     */
    public PageInfo<MarkDownBlogDTO> queryDtoPageListByWhere(MarkDownBlogDO example, Integer page, Integer rows) {
        PageInfo<MarkDownBlogDO> markDownBlogDOPageInfo = cacheService.queryPageListByWhere(example, page, rows);
        return MarkDownEditorConvert.doToDtoPage(markDownBlogDOPageInfo);
    }

    public Integer saveDO(MarkDownBlogDO markDownBlog) {
        return cacheService.save(markDownBlog);
    }


    public MarkDownBlogDTO updateDO(MarkDownBlogDO markDownBlogDO){
        return MarkDownEditorConvert.doToDto(cacheService.update(markDownBlogDO));
    }


    public Boolean  updateSelectiveDO(MarkDownBlogDO markDownBlogDO){
        MarkDownEditorConvert.doToDto(cacheService.updateSelective(markDownBlogDO));
        return true;
    }


    public Integer deleteDOById(Long id) {
        return cacheService.deleteById(id);
    }

}
