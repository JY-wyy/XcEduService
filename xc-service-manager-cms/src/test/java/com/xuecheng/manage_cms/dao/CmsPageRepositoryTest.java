package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.domain.system.SysDictionaryValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by 祭音丶 on 2020/4/9.
 * 测试Cms的Dao
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {
    @Resource
    CmsPageRepository cmsPageRepository;

    @Resource
    SysDictionaryDao sysDictionaryDao;
    //测试分页查询
    @Test
    public void findPageTest(){
        int page = 0;   //从第一页开始
        int size = 10;  //每页查10条
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> pages = cmsPageRepository.findAll(pageable);
    }

    //测试条件查询
    @Test
    public void findAllTest(){
        int page = 0;   //从第一页开始
        int size = 10;  //每页查10条
        Pageable pageable = PageRequest.of(page,size);  //定义分页查询的分页对象
        CmsPage cmsPage = new CmsPage();        //新建用于条件匹配的CmsPage对象
        cmsPage.setPageName("in");                 //赋值条件
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()       //构建条件匹配器，并定义，匹配规则
                .withMatcher("pageName",ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);      //新建条件查询的参数，根据CmsPage对象的相关属性条件查询(默认精确匹配)
        Page<CmsPage> pages = cmsPageRepository.findAll(example,pageable);  //使用findAll方法的条件查询参数Example
        List<CmsPage> content = pages.getContent();
        System.out.println(content);
    }

    @Test
    public void testFindBydType(){
        SysDictionary bydType = sysDictionaryDao.findBydType("200");
        List<SysDictionaryValue> dValue = bydType.getDValue();
        System.out.println(dValue);
    }

}
