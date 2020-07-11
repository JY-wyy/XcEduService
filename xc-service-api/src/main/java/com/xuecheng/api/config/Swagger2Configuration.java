package com.xuecheng.api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//
@Configuration
@EnableSwagger2
public class Swagger2Configuration {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .tags(new Tag("cms页面管理接口", "cms页面管理接口，提供页面的增、删、改、查"),getTags())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xuecheng"))
                .paths(PathSelectors.any())
                .build();
    }
    private Tag[] getTags() {
        Tag[] tags = {
                new Tag("cms配置管理接口", "cms配置管理接口，提供数据模型的，管理，查询接口"),
                new Tag("课程管理接口", "课程管理接口，提供课程的增、删、改、查"),
                new Tag("数据字典接口", "提供数据字典接口的管理、查询功能"),
                new Tag("课程分类管理", "课程分类管理"),
                new Tag("文件管理接口","文件管理接口，提供文件的增、删、改、查"),
                new Tag("课程搜索管理接口","课程搜索"),
                new Tag("媒资管理接口","媒资管理接口，提供文件上传，文件处理等接口"),
                new Tag("媒资文件管理接口","媒资文件管理接口，处理媒资文件"),
                new Tag("录播课程学习管理接口","录播课程学习管理"),
                new Tag("用户认证接口","用户登陆与退出管理"),
                new Tag("用户中心管理接口","用户中心")
        };
        return tags;
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("学成网api文档")
                .description("学成网api文档")
//                .termsOfServiceUrl("/")
                .version("1.0")
                .build();
    }

}
