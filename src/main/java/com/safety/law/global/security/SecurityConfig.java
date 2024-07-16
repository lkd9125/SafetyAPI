package com.safety.law.global.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.security.model.UserType;
import com.safety.law.global.security.oauth.CustomOauth2UserService;
import com.safety.law.global.security.oauth.CustomOauthSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    private final UsersRepository usersRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    private final CustomLoginFailureHandler customLoginFailureHandler;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;

    private final CustomOauth2UserService customOauth2UserService;

    private final CustomOauthSuccessHandler customOauthSuccessHandler;

    private final String[] PERMIT_URL = {
        "/**"
    };  

    private final String[] AUTHENTICATION_URL = {
        "/v1/law/search",
        "/v1/law/search/count", 
        "/v1/law/detail/*",
        "/v1/law/category",
        "/v1/law/comment",
        "/v1/law/history", 
        "/v1/law/ranking/keyword", 
        "/v1/law/enhance/search", 
        "/v1/law/change-list", 

        "/v1/user/update",
        "/v1/user/delete",
        "/v1/user/profile",
        "/v1/user/logout",

        "/v1/board/create",
        "/v1/board/list",
        "/v1/board/delete/*",
        "/v1/board/update",
        "/v1/board/comment/create",
        "/v1/board/comment/*",
        "/v1/board/comment/delete/*",
        "/v1/board/comment/update",
        "/v1/board/save-delete",
        "/v1/board/*",
    };  

    private final String[] COMPANY_AUTH = {
        // "/company/**"
    };

    private final String[] ADMIN_AUTH = {
        "/v1/admin/**",
        "/v1/system/**",
        "/v1/common/test/**",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            .formLogin(formLogin -> formLogin.disable())
            .authorizeHttpRequests(authorizeRequests -> {
                authorizeRequests
                .requestMatchers(AUTHENTICATION_URL).hasAnyAuthority(UserType.USER.getValue(), UserType.COMPANY.getValue(),UserType.ADMIN.getValue())
                .requestMatchers(COMPANY_AUTH).hasAnyAuthority(UserType.COMPANY.getValue(),UserType.ADMIN.getValue())
                .requestMatchers(ADMIN_AUTH).hasAnyAuthority(UserType.ADMIN.getValue())
                .requestMatchers(PERMIT_URL).permitAll()
                ;
                // .anyRequest().authenticated();
            })
            .oauth2Login(oauth -> oauth
                // .userInfoEndpoint(c -> c.userService(customOauth2UserService))
                // .successHandler(customOauthSuccessHandler)
                .redirectionEndpoint(endpoint -> endpoint.baseUri("/local/oauth2/code/*"))
                .userInfoEndpoint(endpoint -> endpoint.userService(customOauth2UserService))
                .successHandler(customOauthSuccessHandler)
            )
            .addFilter(jwtAdminAuthenticationFilter())
            .addFilter(jwtAuthenticationFilter())
            .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> {
                exception.accessDeniedHandler(this.customAccessDeniedHandler);
                exception.authenticationEntryPoint(this.customAuthenticationEntryPointHandler);  
            })
            .build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "Content-Type", "Authorization", "X-XSRF-token", "Accept"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    @Bean
    public CustomAuthentication customAuthentication(){
        return new CustomAuthentication(userDetailsService, passwordEncoder());        
    }

    @Bean
    public CustomAdminAuthentication customAdminAuthentication(){
        return new CustomAdminAuthentication(userDetailsService, passwordEncoder());        
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(){
        return new JwtAuthorizationFilter(jwtTokenProvider, usersRepository);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManagerBean(), customAuthentication(), jwtTokenProvider, usersRepository);
        jwtAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler);
        jwtAuthenticationFilter.setAuthenticationFailureHandler(customLoginFailureHandler);

        return jwtAuthenticationFilter;
    }

    @Bean
    public JwtAdminAuthenticationFilter jwtAdminAuthenticationFilter() throws Exception {
        JwtAdminAuthenticationFilter jwtAdminAuthenticationFilter = new JwtAdminAuthenticationFilter(authenticationManagerBean(), customAdminAuthentication(), jwtTokenProvider, usersRepository);
        jwtAdminAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler);
        jwtAdminAuthenticationFilter.setAuthenticationFailureHandler(customLoginFailureHandler);

        return jwtAdminAuthenticationFilter;
    }

}
