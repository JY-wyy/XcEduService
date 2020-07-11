package com.xuecheng.manage_course.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import com.xuecheng.manage_course.service.CourseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by 祭音丶 on 2020/4/16.
 *
 * 课程管理Service实现类
 */
@Service
public class CourseServiceImp implements CourseService {
    @Resource
    TeachplanMapper teachplanMapper;
    @Resource
    TeachplanRepository teachplanRepository;

    @Resource
    CourseBaseRepository courseBaseRepository;

    @Resource
    CourseMapper courseMapper;

    @Resource
    CourseMarketRepository courseMarketRepository;


    @Resource
    CoursePicRepository coursePicRepository;

    @Resource
    CmsPageClient cmsPageClient;

    @Resource
    CoursePubRepository coursePubRepository;

    @Resource
    TeachplanMediaRepository teachplanMediaRepository;


    @Resource
    TeachplanMediaPubRepository teachplanMediaPubRepository;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;


    //课程计划查询
    @Override
    public TeachplanNode findTeachplanList(String courseId) {
        TeachplanNode teachplanNode = teachplanMapper.selectTeachplan(courseId);
        return teachplanNode;
    }

    //添加课程计划
    @Override
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        //简单判断参数
        if (teachplan == null ||
            StringUtils.isEmpty(teachplan.getCourseid())||
            StringUtils.isEmpty(teachplan.getPname())){
            ExceptionCest.cest(CommonCode.INVALID_PARAM);
        }
        //取出页面传入的id 与 parentId
        String courseid = teachplan.getCourseid();
        String parentid = teachplan.getParentid();
        //判断前端是否选择了父节点
        if (StringUtils.isEmpty(parentid)) {
            //未选择
            parentid = this.getTeachplanRoot(teachplan.getCourseid());      //将返回的父节点课程的id 作为根节点id
        }
        Optional<Teachplan> optional = teachplanRepository.findById(parentid);//根据根节点id 查询根节点课程详情
        Teachplan teachplan1 = optional.get();
        String grade = teachplan1.getGrade();       //返回父节点的层级信息
        //构建添加对象
        Teachplan teachplanNew = new Teachplan();
        BeanUtils.copyProperties(teachplan,teachplanNew);
        teachplanNew.setCourseid(courseid);
        teachplanNew.setParentid(parentid);
        if (grade.equals("1")){
            teachplanNew.setGrade("2"); //设置节点级别
        }else {
            teachplanNew.setGrade("3");
        }
        teachplanRepository.save(teachplanNew);
        return new ResponseResult(CommonCode.SUCCESS);
    }



    //根据课程id查询课程的根节点，如果查不到要自动添加根节点
    private String getTeachplanRoot(String courseId){
        //查询课程名称
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()){
            return null;
        }
        CourseBase courseBase = optional.get();
        //查询课程的根节点信息
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size() <= 0){    //如果未查到 证明为新加课程，需要创建根节点信息
            //添加根节点记录
            Teachplan teachplan = new Teachplan();
            teachplan.setParentid("0");
            teachplan.setGrade("1");
            teachplan.setPname(courseBase.getName());       //根据课程id查询到的课程名称
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");
            Teachplan save = teachplanRepository.save(teachplan);
            return save.getId();
        }
        return teachplanList.get(0).getId();        //如果查到 证明已有课程，直接返回课程id即可
    }


    //课程查询
    @Override
    public QueryResponseResult<CourseInfo> findCourseList(String company_id , Integer page, Integer size, CourseListRequest courseListRequest) {
        if (courseListRequest == null){
            courseListRequest = new CourseListRequest();
        }
        if (page <= 0){
            page = 0;
        }
        if (size <= 0){
            size = 20;
        }
        //将公司id参数传入dao
        courseListRequest.setCompanyId(company_id);
        PageHelper.startPage(page,size);
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        long total = courseListPage.getTotal();
        List<CourseInfo> result = courseListPage.getResult();

        QueryResult<CourseInfo> queryResult = new QueryResult<CourseInfo>();
        queryResult.setList(result);
        queryResult.setTotal(total);


        return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS,queryResult);
    }

    @Override
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        if (courseBase == null){
            ExceptionCest.cest(CommonCode.INVALID_PARAM);
        }
        //课程状态 默认未发布
        courseBase.setStatus("202001");
        CourseBase save = courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS,save.getId());
    }

    //获取课程的基本信息
    @Override
    public CourseBase getCourseBaseById(String courseId) {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCest.cest(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        Optional<CourseBase> byId = courseBaseRepository.findById(courseId);
        if (byId.isPresent()){
            CourseBase courseBase = byId.get();
            return courseBase;
        }
        return null;
    }

    //修改课程的基本信息
    @Override
    @Transactional
    public ResponseResult updateCourseBase(String id, CourseBase courseBase) {
        CourseBase courseBaseById = this.getCourseBaseById(id);
        if (courseBaseById == null){
            ExceptionCest.cest(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        //修改课程信息
        courseBaseById.setName(courseBase.getName());
        courseBaseById.setMt(courseBase.getMt());
        courseBaseById.setSt(courseBase.getSt());
        courseBaseById.setGrade(courseBase.getGrade());
        courseBaseById.setStudymodel(courseBase.getStudymodel());
        courseBaseById.setUsers(courseBase.getUsers());
        courseBaseById.setDescription(courseBase.getDescription());

        //储存
        CourseBase save = courseBaseRepository.save(courseBaseById);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    public CourseMarket getCourseMarketById(String courseId) {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCest.cest(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if (optional.isPresent()){
            CourseMarket courseMarket = optional.get();
            return courseMarket;
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        if (StringUtils.isEmpty(id)) {
            ExceptionCest.cest(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        CourseMarket marketById = this.getCourseMarketById(id);
        if (marketById != null){
            marketById.setCharge(courseMarket.getCharge());
            marketById.setStartTime(courseMarket.getStartTime());//课程有效期，开始时间
            marketById.setEndTime(courseMarket.getEndTime());//课程有效期，结束时间
            marketById.setPrice(courseMarket.getPrice());
            marketById.setQq(courseMarket.getQq());
            marketById.setValid(courseMarket.getValid());
            courseMarketRepository.save(marketById);
        }else{
            marketById = new CourseMarket();
            BeanUtils.copyProperties(courseMarket,marketById);
            marketById.setId(id);
            courseMarketRepository.save(marketById);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        CoursePic coursePic = null;
        if (picOptional.isPresent()){
            coursePic = picOptional.get();
        }
        if (coursePic == null){
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if (picOptional.isPresent()){
            CoursePic coursePic = picOptional.get();
            return coursePic;
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        long l = coursePicRepository.deleteByCourseid(courseId);
        if (l >0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
            return new ResponseResult(CommonCode.FAIL);
    }

    @Override
    public CourseView getCourseView(String id) {
        CourseView courseView = new CourseView();

        //设置课程基础信息
        CourseBase courseBase = this.getCourseBaseById(id);
        courseView.setCourseBase(courseBase);

        //设置课程营销信息
        CourseMarket courseMarket = this.getCourseMarketById(id);
        courseView.setCourseMarket(courseMarket);

        //设置课程图片信息
        TeachplanNode teachplanNode = this.findTeachplanList(id);
        courseView.setTeachplanNode(teachplanNode);

        //设置课程图片信息
        CoursePic coursePic = this.findCoursePic(id);
        courseView.setCoursePic(coursePic);
        return courseView;
    }

    @Override
    public CoursePublishResult preview(String id) {
        //调用封装页面信息的方法，得到页面对象
        CmsPage cmsPage = this.getCmsPage(id);
        //远程调用cms，储存页面信息
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if (!cmsPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //拼装页面预览的url
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        String url = previewUrl+pageId;
        //返回CoursePublishResult对象
        return new CoursePublishResult(CommonCode.SUCCESS,url);
    }

    @Override
    @Transactional
    public CoursePublishResult publish(String id) {
        //调用封装页面信息的方法，得到页面对象
        CmsPage cmsPage = this.getCmsPage(id);
        //远程调配用Cms的一键发布接口
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!cmsPostPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //调用更改课程状态的方法，更改课程发布状态为已发布
        CourseBase courseBase = this.saveCoursePubState(id);
        if (courseBase == null){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //保存索引信息
        //创建CoursePub对象
        CoursePub coursePub = this.createCoursePub(id);

        //保存对象到数据库
        CoursePub coursePubSave = this.saveCoursePub(id, coursePub);
        if (coursePubSave == null){
            ExceptionCest.cest(CommonCode.FAIL);
        }


        String pageUrl = cmsPostPageResult.getPageUrl();
        //向课程媒资信息表中更新数据
        this.saveTeachplanMediaPub(id);
        //返回对象
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }

    //向TeachplanMediaPub中保存课程媒资信息
    private void saveTeachplanMediaPub(String courseId){
        //先删除TeachplanMediaPub中对应的课程媒资信息
        teachplanMediaPubRepository.deleteByCourseId(courseId);

        //在TeachplanMedia中查询课程信息
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);

        //取出集合中的数据
        List<TeachplanMediaPub> teachplanMediaPubs = new ArrayList<>();
        //空值判断
        if (teachplanMediaList != null){
            //循环遍历取出查到的数据
            for (TeachplanMedia teachplanMedia : teachplanMediaList) {
                //构建存储到TeachplanMediaPub中的对象
                TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
                //将其拷贝到teachplanMediaPub中
                BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
                //添加时间戳信息
                teachplanMediaPub.setTimestamp(new Date());
                //将每一条teachplanMediaPub信息存储到集合中
                teachplanMediaPubs.add(teachplanMediaPub);
            }
        }else{
            return;
        }
        //将集合存储到数据库
        teachplanMediaPubRepository.saveAll(teachplanMediaPubs);
    }

    //课程计划与媒资文件的关联
    @Override
    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
        //对传入参数进行非空判断
        if (teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())){
            ExceptionCest.cest(CommonCode.INVALID_PARAM);
        }
        //根据传入参数的课程计划id查询课程计划信息
        Optional<Teachplan> optional = teachplanRepository.findById(teachplanMedia.getTeachplanId());
        if (!optional.isPresent()){
            //查不到证明此课程计划未添加
            ExceptionCest.cest(CommonCode.INVALID_PARAM);
        }
        //查到后取出课程级别并判断
        Teachplan teachplan = optional.get();
        String gradeId = teachplan.getGrade();
        //级别不为三级不进行添加
        if (!gradeId.equals("3")){
            ExceptionCest.cest(CommonCode.INVALID_PARAM);
        }
        TeachplanMedia one = null;
        //根据查到的课程计划id查询课程计划与视频的绑定情况
        Optional<TeachplanMedia> byId = teachplanMediaRepository.findById(teachplan.getId());
        if (byId.isPresent()){
            //不为空 已经有视频绑定
            one = byId.get();
        }else {
            //为空 无视频绑定
            one = new TeachplanMedia();
        }

        //构建修改对象
        one.setCourseId(teachplanMedia.getCourseId());
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        one.setMediaId(teachplanMedia.getMediaId());
        one.setMediaUrl(teachplanMedia.getMediaUrl());
        one.setTeachplanId(teachplanMedia.getTeachplanId());

        //保存修改对象到数据库
        teachplanMediaRepository.save(one);
        //返回操作成功
        return ResponseResult.SUCCESS();
    }

    //保存索引对象
    private CoursePub saveCoursePub(String id,CoursePub coursePub){
        if (id.isEmpty()){
            ExceptionCest.cest(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        CoursePub coursePubNew = null;
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if (coursePubOptional.isPresent()) {
            coursePubNew = coursePubOptional.get();

        }else{
            coursePubNew = new CoursePub();
        }
        BeanUtils.copyProperties(coursePub,coursePubNew);
        //设置主键
        coursePubNew.setId(id);
        //设置时间戳
        coursePubNew.setTimestamp(new Date());
        //设置发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(format);
        //保存
        coursePubRepository.save(coursePubNew);
        return coursePubNew;

    }

    //创建索引对象的方法
    private CoursePub createCoursePub(String id){
        CoursePub coursePub = new CoursePub();
        //课程图片信息的存储
        CoursePic coursePic = this.findCoursePic(id);
        BeanUtils.copyProperties(coursePic,coursePub);
        //课程基本信息的存储
        CourseBase courseBaseById = this.getCourseBaseById(id);
        BeanUtils.copyProperties(courseBaseById,coursePub);
        //课程营销信息的存储
        CourseMarket courseMarketById = this.getCourseMarketById(id);
        BeanUtils.copyProperties(courseMarketById,coursePub);
        //课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectTeachplan(id);
        String teachplanString = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(teachplanString);
        return coursePub;
    }

    //准备页面
    private CmsPage getCmsPage(String id){
        //查询课程
        CourseBase courseBaseById = this.getCourseBaseById(id);
        //拼装cmsPage对象
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre+id);//数据模型url
        cmsPage.setPageName(id+".html");//页面名称
        cmsPage.setPageAliase(courseBaseById.getName());//页面别名，就是课程名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id
        return cmsPage;
    }
    //更改发布状态
    private CourseBase saveCoursePubState(String id){
        //查询课程
        CourseBase courseBaseById = this.getCourseBaseById(id);
        courseBaseById.setStatus("202002");
        CourseBase save = courseBaseRepository.save(courseBaseById);
        return save;
    }
}
