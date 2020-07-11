package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.SearchPhaseResult;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 祭音丶 on 2020/4/22.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {
    @Resource
    RestHighLevelClient client;

    @Resource
    RestClient restClient;

  //全局查询索引库
    @Test
    public void testSearchAll() throws IOException, ParseException {
        //创建查询对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //创建搜索条件构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //添加搜索条件 查询全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字段过滤，第一个展示内容，第二个不展示内容
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索对象中添加构造内容
        searchRequest.source(searchSourceBuilder);
        //client执行搜索
        SearchResponse search = client.search(searchRequest);
        //取出相应的数据具体值
        //搜索的结果
        SearchHits hits = search.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] hits1 = hits.getHits();
        //日期格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历文档
        for (SearchHit hit : hits1) {
            //拿到文档id
            int i = hit.docId();
            //将文档中的数据放入map中
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }

    }


    //分页查询索引库
    @Test
    public void testSearchPage() throws IOException, ParseException {
        //创建查询对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //创建搜索条件构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //添加搜索条件 查询全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字段过滤，第一个展示内容，第二个不展示内容
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //设置分页参数
        int page = 1;
        int size = 1;
        int from = (page-1)*size;
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        //向搜索对象中添加构造内容
        searchRequest.source(searchSourceBuilder);
        //client执行搜索
        SearchResponse search = client.search(searchRequest);
        //取出相应的数据具体值
        //搜索的结果
        SearchHits hits = search.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] hits1 = hits.getHits();
        //日期格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历文档
        for (SearchHit hit : hits1) {
            //拿到文档id
            int i = hit.docId();
            //将文档中的数据放入map中
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }

    }


    //条件查询(term 精确查询 不分词)索引库
    @Test
    public void testSearchTerm() throws IOException, ParseException {
        //创建查询对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //创建搜索条件构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //添加搜索条件 查询全部
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置源字段过滤，第一个展示内容，第二个不展示内容
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //设置分页参数
        int page = 1;
        int size = 1;
        int from = (page-1)*size;
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        //向搜索对象中添加构造内容
        searchRequest.source(searchSourceBuilder);
        //client执行搜索
        SearchResponse search = client.search(searchRequest);
        //取出相应的数据具体值
        //搜索的结果
        SearchHits hits = search.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] hits1 = hits.getHits();
        //日期格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历文档
        for (SearchHit hit : hits1) {
            //拿到文档id
            int i = hit.docId();
            //将文档中的数据放入map中
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }

    }


    //根据id查询索引库
    @Test
    public void testSearchTermQueryById() throws IOException, ParseException {
        //创建查询对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //创建搜索条件构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] ids = new String[]{"1","2"};
        //添加搜索条件 查询全部
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
        //设置源字段过滤，第一个展示内容，第二个不展示内容
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索对象中添加构造内容
        searchRequest.source(searchSourceBuilder);
        //client执行搜索
        SearchResponse search = client.search(searchRequest);
        //取出相应的数据具体值
        //搜索的结果
        SearchHits hits = search.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] hits1 = hits.getHits();
        //日期格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历文档
        for (SearchHit hit : hits1) {
            //拿到文档id
            int i = hit.docId();
            //将文档中的数据放入map中
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }

    }


    //matchQuery(查询条件分词)查询索引库
    @Test
    public void testSearchMatchQuery() throws IOException, ParseException {
        //创建查询对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //创建搜索条件构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //添加搜索条件 查询description中含有 "spring开发框架"中 百分之八十以上词语的内容
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架")
                                                .minimumShouldMatch("80%"));
        //设置源字段过滤，第一个展示内容，第二个不展示内容
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索对象中添加构造内容
        searchRequest.source(searchSourceBuilder);
        //client执行搜索
        SearchResponse search = client.search(searchRequest);
        //取出相应的数据具体值
        //搜索的结果
        SearchHits hits = search.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] hits1 = hits.getHits();
        //日期格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历文档
        for (SearchHit hit : hits1) {
            //拿到文档id
            int i = hit.docId();
            //将文档中的数据放入map中
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }

    }



    //MultiQuery(提升权重并在多个域中查询)查询索引库
    @Test
    public void testSearchMultiQuery() throws IOException, ParseException {
        //创建查询对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //创建搜索条件构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //添加搜索条件 查询description中含有 "spring开发框架"中 百分之八十以上词语的内容
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring java","name","description")
                                                                    .minimumShouldMatch("50%").field("name",10));
        //设置源字段过滤，第一个展示内容，第二个不展示内容
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索对象中添加构造内容
        searchRequest.source(searchSourceBuilder);
        //client执行搜索
        SearchResponse search = client.search(searchRequest);
        //取出相应的数据具体值
        //搜索的结果
        SearchHits hits = search.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] hits1 = hits.getHits();
        //日期格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历文档
        for (SearchHit hit : hits1) {
            //拿到文档id
            int i = hit.docId();
            //将文档中的数据放入map中
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }

    }

    //BoolQuery(多查询构造器组合查询)查询索引库
    @Test
    public void testSearchBoolQuery() throws IOException, ParseException {
        //创建查询对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //创建搜索条件构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //添加搜索条件 查询description中含有 "spring开发框架"中 百分之八十以上词语的内容
        //创建MultiMatchQuery
        MultiMatchQueryBuilder field = QueryBuilders.multiMatchQuery("spring java", "name", "description")
                .minimumShouldMatch("50%").field("name", 10);
        //创建TermQuery
        TermQueryBuilder studymodel1 = QueryBuilders.termQuery("studymodel", "201001");
        //创建BoolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //将前两个添加到BoolQuery
        boolQueryBuilder.must(field);
        boolQueryBuilder.must(studymodel1);
        searchSourceBuilder.query(boolQueryBuilder);
        //设置源字段过滤，第一个展示内容，第二个不展示内容
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索对象中添加构造内容
        searchRequest.source(searchSourceBuilder);
        //client执行搜索
        SearchResponse search = client.search(searchRequest);
        //取出相应的数据具体值
        //搜索的结果
        SearchHits hits = search.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] hits1 = hits.getHits();
        //日期格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历文档
        for (SearchHit hit : hits1) {
            //拿到文档id
            int i = hit.docId();
            //将文档中的数据放入map中
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }

    }

    //Filter
    @Test
    public void testSearchFilter() throws IOException, ParseException {
        //创建查询对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //创建搜索条件构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //添加搜索条件 查询description中含有 "spring开发框架"中 百分之八十以上词语的内容
        //创建MultiMatchQuery
        MultiMatchQueryBuilder field = QueryBuilders.multiMatchQuery("spring java", "name", "description")
                .minimumShouldMatch("50%").field("name", 10);
        //创建BoolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //将前两个添加到BoolQuery
        boolQueryBuilder.must(field);

        //创建filter,过滤studymodel不为201001的数据
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        //创建filter,过滤价格区间不在范围的数据
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));

        searchSourceBuilder.query(boolQueryBuilder);
        //设置源字段过滤，第一个展示内容，第二个不展示内容
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索对象中添加构造内容
        searchRequest.source(searchSourceBuilder);
        //client执行搜索
        SearchResponse search = client.search(searchRequest);
        //取出相应的数据具体值
        //搜索的结果
        SearchHits hits = search.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] hits1 = hits.getHits();
        //日期格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历文档
        for (SearchHit hit : hits1) {
            //拿到文档id
            int i = hit.docId();
            //将文档中的数据放入map中
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }

    }

    //Sort 排序
    @Test
    public void testSearchSort() throws IOException, ParseException {
        //创建查询对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //创建搜索条件构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //创建BoolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //创建filter,过滤价格区间不在范围的数据
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));

        //载入构造器
        searchSourceBuilder.query(boolQueryBuilder);

        //设置排序 按studymodel降序排
        searchSourceBuilder.sort("studymodel", SortOrder.DESC);
        //设置排序 按价格升序排序
        searchSourceBuilder.sort("price",SortOrder.ASC);

        //设置源字段过滤，第一个展示内容，第二个不展示内容
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索对象中添加构造内容
        searchRequest.source(searchSourceBuilder);
        //client执行搜索
        SearchResponse search = client.search(searchRequest);
        //取出相应的数据具体值
        //搜索的结果
        SearchHits hits = search.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] hits1 = hits.getHits();
        //日期格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历文档
        for (SearchHit hit : hits1) {
            //拿到文档id
            int i = hit.docId();
            //将文档中的数据放入map中
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }

    }


    //Highlight
    @Test
    public void testSearchHighlight() throws IOException, ParseException {
        //创建查询对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //创建搜索条件构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //添加搜索条件 查询description中含有 "spring开发框架"中 百分之八十以上词语的内容
        //创建MultiMatchQuery
        MultiMatchQueryBuilder field = QueryBuilders.multiMatchQuery("开发框架", "name", "description")
                .minimumShouldMatch("50%").field("name", 10);
        //创建BoolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //将前两个添加到BoolQuery
        boolQueryBuilder.must(field);

        //创建filter,过滤studymodel不为201001的数据
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        //创建filter,过滤价格区间不在范围的数据
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));

        //添加搜索条件
        searchSourceBuilder.query(boolQueryBuilder);

        //创建高亮对象
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置高亮格式
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");
        //设置高亮检索对象
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        //添加高亮
        searchSourceBuilder.highlighter(highlightBuilder);

        //设置源字段过滤，第一个展示内容，第二个不展示内容
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //向搜索对象中添加构造内容
        searchRequest.source(searchSourceBuilder);
        //client执行搜索
        SearchResponse search = client.search(searchRequest);
        //取出相应的数据具体值
        //搜索的结果
        SearchHits hits = search.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] hits1 = hits.getHits();
        //日期格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历文档
        for (SearchHit hit : hits1) {
            //拿到文档id
            int i = hit.docId();
            //将文档中的数据放入map中
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //拿到高亮对象 集合
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null){
                //取出在name字段中高亮的结果对象
                HighlightField nameHighlightField = highlightFields.get("name");
                if (nameHighlightField!=null){
                    //取出结果对象中的内容
                    Text[] fragments = nameHighlightField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text fragment : fragments) {
                        stringBuffer.append(fragment);
                    }
                    name = stringBuffer.toString();
                }
            }
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }

    }
}
