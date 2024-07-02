package com.parksystems.nanoScientificSymposium.common.auth.config;


import com.parksystems.nanoScientificSymposium.common.auth.filter.JwtVerificationFilter;
import com.parksystems.nanoScientificSymposium.common.auth.handler.MemberAuthenticationEntryPoint;
//import com.parksystems.nanoScientificSymposium.common.auth.handler.OAuth2memberSuccessHandler;
import com.parksystems.nanoScientificSymposium.common.auth.jwt.JwtTokenizer;
import com.parksystems.nanoScientificSymposium.common.auth.utils.CustomAuthorityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {


    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .cors(withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
//                .authenticationEntryPoint(new MemberAuthenticationEntryPoint(jwtTokenizer,authorityUtils))
                .and()
//                .apply(new CustomFilterConfigurer())
//                .and()
                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().authenticated()
                                .anyRequest().permitAll()
                )
                .logout()
                .logoutSuccessUrl("http://localhost:3000");
        return http.build();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://nsseventche ckin.serveo.net","chrome-extension://ggnhohnkfcpcanfekomdkjffnfcjnjam","http://jxy.me","http://localhost:3000","http://localhost:8080","127.0.0.1"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PATCH", "DELETE","OPTION"));
        configuration.addAllowedHeader("*");
        configuration.setExposedHeaders(Arrays.asList("*","Authorization","Refresh"));
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer,HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer,authorityUtils);

            builder.addFilterAfter(jwtVerificationFilter, OAuth2LoginAuthenticationFilter.class);
        }
    }
}
