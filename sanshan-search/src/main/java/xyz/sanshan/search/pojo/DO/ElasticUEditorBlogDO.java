package xyz.sanshan.search.pojo.DO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@NoArgsConstructor
@Data
@ToString
@Document(indexName = "blogs",type = "ueditor")
public class ElasticUEditorBlogDO extends ElasticBaseBlogDO implements Serializable{

    private static final long serialVersionUID = -7380613830753506958L;

    @Id
    private Long id;


}
