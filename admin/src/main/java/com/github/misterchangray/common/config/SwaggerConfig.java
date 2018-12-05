package com.github.misterchangray.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置swagger2.6.1
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 3/22/2018.
 */
@Configuration      //让Spring来加载该类配置
@EnableWebMvc       //启用Mvc，非springboot框架需要引入注解@EnableWebMvc
@EnableSwagger2     //启用Swagger2
@ComponentScan(basePackages = "com.github.misterchangray")
public class SwaggerConfig {
    @Value("${swagger2.enabled}")
    private boolean enabled;

    @Bean
    public Docket createRestApi() {
        //统一增加权限验证字段
        List<Parameter> params = new ArrayList<Parameter>();
        ParameterBuilder tokenParam = new ParameterBuilder();
        tokenParam.name("Authorization").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(true).build();
        params.add(tokenParam.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enabled)
                .apiInfo(apiInfo()).select()
                        //扫描指定包中的swagger注解
                        //.apis(RequestHandlerSelectors.basePackage("com.github.misterchangray.controller"))
                        //扫描所有有注解的api，用这种方式更灵活
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(params);
    }

    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("基础平台 RESTful APIs")
                .description("基础平台 RESTful 风格的接口文档，内容详细，极大的减少了前后端的沟通成本，同时确保代码与文档保持高度一致，极大的减少维护文档的时间。")
                .version("1.0.0")
                .termsOfServiceUrl("https://github.com/MisterChangRay")
                .build();
    }
}
