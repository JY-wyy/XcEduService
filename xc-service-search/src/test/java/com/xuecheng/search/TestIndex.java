package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 祭音丶 on 2020/4/22.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {
    @Resource
    RestHighLevelClient client;

    @Resource
    RestClient restClient;

    //创建索引库
    @Test
    public void tsetCreateIndex() throws IOException {
        //创建索引对象，并设置索引名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
        //设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards",1).put("number_of_replicas",0));
        createIndexRequest.mapping("doc","{\n" +
                "  \"properties\": {\n" +
                "    \"name\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"description\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"price\": {\n" +
                "      \"type\": \"float\",\n" +
                "      \"index\": false\n" +
                "    },\n" +
                "    \"studymodel\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    }\n" +
                "  }\n" +
                "}", XContentType.JSON);
        CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
        boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();
        System.out.println(shardsAcknowledged);
    }

    //删除索引库
    @Test
    public void tsetDeleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
        DeleteIndexResponse delete = client.indices().delete(deleteIndexRequest);
        boolean acknowledged = delete.isAcknowledged();
        System.out.println(acknowledged);
    }


    //添加文档
    @Test
    public void testAddDoc() throws IOException {
        //准备json数据
        Map<String,Object> jsonMap=new HashMap<>();
        jsonMap.put("name","springcloud实战");
        jsonMap.put("description","本课程主要从四个章节进行讲解：1.微服务架构入门2.springcloud基础入门3.实战SpringBoot4.注册中心eureka。");
        jsonMap.put("studymodel","201001");
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy‐MM‐ddHH:mm:ss");
        jsonMap.put("timestamp",dateFormat.format(new Date()));
        jsonMap.put("price",5.6f);
        //创建文档添加对象
        IndexRequest indexRequest = new IndexRequest("xc_course","doc");
        indexRequest.source(jsonMap);
        IndexResponse index = client.index(indexRequest);
        DocWriteResponse.Result result = index.getResult();
        System.out.println(result);
    }

    //查询文档
    @Test
    public void testGetDoc() throws IOException {
        //创建文档查询对象
        GetRequest getIndexRequest = new GetRequest("xc_course",
                                                    "doc",
                                                    "zGDqn3EBofVKCQtHSY3l");
        GetResponse documentFields = client.get(getIndexRequest);
        boolean exists = documentFields.isExists();
        Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
        System.out.println(sourceAsMap);
    }
}
