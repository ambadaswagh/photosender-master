package com.cwidanage.photoftp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.cwidanage.photoftp.models.Fotografi;
import com.cwidanage.photoftp.photocorrupt.util.DetectPhotoCorrupt;
import com.cwidanage.photoftp.repository.FotografiRepository;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Chathura Widanage
 */
@EntityScan(
        basePackages = {"com.cwidanage.photoftp.models"}
)
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ImportResource("classpath:spring.xml")
//@ConfigurationProperties(prefix = "photoftp.auth")
public class Application {

/*    private String username;
    private String password;
*/
	
	@Autowired FotografiRepository fotografiRepository;
	
	
    public static void main(String[] args) throws IOException {

    	        SpringApplication.run(Application.class, args);
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new javax.servlet.Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {

            }

            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                HttpServletRequest req = (HttpServletRequest) servletRequest;
                String servletPath = req.getServletPath();
                
                String auth = req.getHeader("Authorization");
                
                Optional<Fotografi> user = null;
                String username = null, password = null;
                if(auth != null && auth.trim().length() > 0 && !auth.equals("false") && auth.indexOf(":") > 0) {
                    username = auth.split(":")[0];
                    password = auth.split(":")[1];
                }

                if(username != null) {
                    user = fotografiRepository.findByUsername(username);
                }
                
                if(user != null && user.isPresent() && user.get().getpassword().equals(password)) {
                	servletRequest.setAttribute("USER_ATTRIBUTE", user.get());
                	filterChain.doFilter(servletRequest, servletResponse);
                }else if(req.getMethod().equals("OPTIONS") || servletPath.startsWith("/api/thumb")) {
                	filterChain.doFilter(servletRequest, servletResponse);
                }else {
                    HttpServletResponse response = (HttpServletResponse) servletResponse;
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }                              
            
            }

            @Override
            public void destroy() {

            }
        });
        registration.addUrlPatterns("/api/*");
        registration.setName("requestFilter");
        registration.setOrder(1);
        return registration;
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods(
                                HttpMethod.GET.toString(),
                                HttpMethod.POST.toString(),
                                HttpMethod.PUT.toString(),
                                HttpMethod.DELETE.toString());
            }
        };
    }

/*    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }*/
}
