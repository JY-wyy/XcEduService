package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 祭音丶 on 2020/4/24.
 */
@Service
public class EsCourseService {
    @Value("${xuecheng.course.index}")
    private String index;
    @Value("${xuecheng.media.index}")
    private String media_index;
    @Value("${xuecheng.course.type}")
    private String type;
    @Value("${xuecheng.media.type}")
    private String media_type;
    @Value("${xuecheng.course.source_field}")
    private String source_field;
    @Value("${xuecheng.media.source_field}")
    private String media_source_field;
    @Autowired
    RestHighLevelClient restHighLevelClient;

    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        if(courseSearchParam == null){
            courseSearchParam = new CourseSearchParam();
        }
        //创建搜索的请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //指定类型
        searchRequest.types(type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //过滤源字段
        String[] split = source_field.split(",");
        searchSourceBuilder.fetchSource(split,new String[]{});
        //创建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();



        //按条件查询
        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "description", "teachplan")
                    .minimumShouldMatch("70%")
                    .field("name", 10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }


        //根据一级分类查询
        if (StringUtils.isNotEmpty(courseSearchParam.getMt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        //根据二级分类
        if (StringUtils.isNotEmpty(courseSearchParam.getSt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        //根据难度等级
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }


        //设置boolQueryBuilder到searchSourceBuilder
        searchSourceBuilder.query(boolQueryBuilder);
        //设置分页参数
        if(page <= 0){
            page = 1;
        }
        if (size <=0){
            size = 12;
        }
        int from = (page-1)*size;
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        //设置高亮显示

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='highlight'>");
        highlightBuilder.postTags("</font>");
        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);

        //创建list，以及QueryResult 用于构建方法的返回值
        List<CoursePub> coursePubs = new ArrayList<>();
        QueryResult<CoursePub> coursePubQueryResult = new QueryResult<>();
        try {
            //执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //取出查询结果
            SearchHits searchHit = searchResponse.getHits();
            long totalHits = searchHit.getTotalHits();
            //设置返回值的total
            coursePubQueryResult.setTotal(totalHits);
            //取出源文件数组
            SearchHit[] hits = searchHit.getHits();
            //遍历数组 拿出具体内容
            for (SearchHit hit : hits) {
                CoursePub coursePub = new CoursePub();
                //源文档
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出id
                String id = (String) sourceAsMap.get("id");
                //设置id
                coursePub.setId(id);
                //取出name
                String name = (String) sourceAsMap.get("name");
                //取出高亮字段
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields != null){
                    HighlightField highlightFieldName = highlightFields.get("name");
                    if (highlightFieldName != null){
                        Text[] fragments = highlightFieldName.getFragments();
                        StringBuffer stringBuffer = new StringBuffer();
                        for (Text fragment : fragments) {
                            stringBuffer.append(fragment);
                        }
                        name = stringBuffer.toString();
                    }
                }
                coursePub.setName(name);
                //图片
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                //价格
                Double price = null;
                try {
                    if(sourceAsMap.get("price")!=null ){
                        price = (Double) sourceAsMap.get("price");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice(price);
                //旧价格
                Double price_old = null;
                try {
                    if(sourceAsMap.get("price_old")!=null ){
                        price_old = (Double) sourceAsMap.get("price_old");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(price_old);
                //将构建好的对象添加到list
                coursePubs.add(coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        coursePubQueryResult.setList(coursePubs);
        QueryResponseResult<CoursePub> queryResponseResult = new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,coursePubQueryResult);

        return queryResponseResult;
    }

    public Map<String, CoursePub> getall(String id) {
        //定义索引的请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //指定索引类型
        searchRequest.types(type);

        //定义索引构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //定义查询方法
        searchSourceBuilder.query(QueryBuilders.termsQuery("id",id));
        //设置构造器到请求对象
        searchRequest.source(searchSourceBuilder);
        //最终要返回的对象
        Map<String,CoursePub> map = new HashMap<>();
        try {
            //使用客户端查询
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            SearchHit[] hits1 = hits.getHits();
            for (SearchHit hit : hits1) {
                CoursePub coursePub = new CoursePub();
                //返回的源文件信息
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //课程id
                String courseId = (String) sourceAsMap.get("id");
                String name = (String) sourceAsMap.get("name");
                String grade = (String) sourceAsMap.get("grade");
                String charge = (String) sourceAsMap.get("charge");
                String pic = (String) sourceAsMap.get("pic");
                String description = (String) sourceAsMap.get("description");
                String teachplan = (String) sourceAsMap.get("teachplan");
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setPic(pic);
                coursePub.setGrade(grade);
                coursePub.setTeachplan(teachplan);
                coursePub.setDescription(description);
                coursePub.setCharge(charge);
                map.put(courseId,coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds) {
        //定义索引的请求对象
        SearchRequest searchRequest = new SearchRequest(media_index);
        //指定类型
        searchRequest.types(media_type);
        //创建索引的构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置搜索方式以及搜索字段
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));
        //过滤源字段
        String[] includes = media_source_field.split(",");
        searchSourceBuilder.fetchSource(includes,new String[]{});

        searchRequest.source(searchSourceBuilder);
        //为接收搜索到的数据总条数，以及数据集合做准备
        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        long total = 0;
        try {
            //使用客户端搜索
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            //取出总记录数以及数据信息
            total = hits.totalHits;
            SearchHit[] hits1 = hits.getHits();
            for (SearchHit hit : hits1) {
                TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出课程计划媒资信息
                String courseid = (String) sourceAsMap.get("courseid");
                String media_id = (String) sourceAsMap.get("media_id");
                String media_url = (String) sourceAsMap.get("media_url");
                String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");
                //构建对象
                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setTeachplanId(teachplan_id);
                //添加对象到数据列表
                teachplanMediaPubList.add(teachplanMediaPub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //构建返回对象
        QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
        queryResult.setList(teachplanMediaPubList);
        queryResult.setTotal(total);
        QueryResponseResult<TeachplanMediaPub> queryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
}
