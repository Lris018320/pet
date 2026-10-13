package com.pet.platform.config;

import com.pet.platform.interceptor.AdminInterceptor;
import com.pet.platform.interceptor.JwtInterceptor;
import com.pet.platform.interceptor.MerchantInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：跨域、静态资源、拦截器
 *
 * 注意：本应用 context-path 为 /api，前端访问 /api/uploads/** 取上传文件。
 *      拦截器路径相对 context-path，因此写 /admin/** 即对应 /api/admin/**。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Autowired
    private MerchantInterceptor merchantInterceptor;

    @Value("${pet.upload.path}")
    private String uploadPath;

    @Value("${pet.upload.url-prefix}")
    private String urlPrefix;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 后台路径：先校验登录，再校验管理员角色
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/register",
                        "/admin/login",
                        "/merchant/login",
                        "/merchant/register",
                        "/info/list",
                        "/info/**",
                        "/breed/list",
                        "/breed/**",
                        "/product/list",
                        "/product/*",
                        "/adoption/list",
                        "/adoption/*",
                        "/post/list",
                        "/post/*",
                        "/upload-session/**",
                        "/uploads/**"
                );
        // 2. 管理员拦截器仅作用于后台接口
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login");

        // 3. 商家拦截器仅作用于商家接口
        registry.addInterceptor(merchantInterceptor)
                .addPathPatterns("/merchant/**")
                .excludePathPatterns("/merchant/login", "/merchant/register");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源映射：/uploads/** -> 本地 uploadPath
        String location = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
        registry.addResourceHandler(urlPrefix + "/**")
                .addResourceLocations("file:" + location);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
