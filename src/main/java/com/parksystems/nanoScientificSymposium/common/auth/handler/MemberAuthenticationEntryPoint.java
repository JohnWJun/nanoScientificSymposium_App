package com.parksystems.nanoScientificSymposium.common.auth.handler;


import com.parksystems.nanoScientificSymposium.common.auth.jwt.JwtTokenizer;
import com.parksystems.nanoScientificSymposium.common.auth.utils.CustomAuthorityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {



        Exception exception = (Exception) request.getAttribute("exception");

        if (exception instanceof ExpiredJwtException) {



             if(request.getHeader("Refresh") != null ){

                 log.info(request.getHeader("Refresh"));

             Map<String, Object> claims = verifyJws(request);
             String username = (String) claims.get("sub");
             List<String> roles = authorityUtils.createRoles(username);
             claims.put("roles", roles);

             Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
             Date expirationForRefresh = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
             String base64EncodedSecretKey = jwtTokenizer.encodedBasedSecretKey(jwtTokenizer.getSecretKey());
             String newAccessToken = jwtTokenizer.generateAccessToken(claims,username,expiration, base64EncodedSecretKey);
             String newRefreshToken =  jwtTokenizer.generateRefreshToken(username,expirationForRefresh,base64EncodedSecretKey);

             setAuthenticationToContext(claims);

             response.setHeader("Authorization",newAccessToken);
             response.setHeader("Refresh",newRefreshToken);
             response.setStatus(HttpServletResponse.SC_FOUND);
                 log.info("please retry with the new Token");
                 log.info(newAccessToken);

             }else if (request.getHeader("Refresh") == null ){
                 log.info("please send the Refresh Token");
                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
             }

        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Set the unauthorized status code
            logExceptionMessage(authException, exception);
        }

    }

    private void setAuthenticationToContext(Map<String, Object> claims) {
        String username = (String) claims.get("sub");
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List)claims.get("roles"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(username,null,authorities);


        System.out.println(username);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }



    private URI createURI(String accessToken, String refreshToken) {
        MultiValueMap<String,String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);

        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(3000)
                .path("/mytokens")//redirect 받기 위한 주소
                .queryParams(queryParams)
                .build().toUri();
    }

    private Map<String, Object> verifyJws(HttpServletRequest request) {
        String jws = request.getHeader("Refresh").replace("Bearer ", "");
        String base64EncodedSecretKey = jwtTokenizer.encodedBasedSecretKey(jwtTokenizer.getSecretKey());
        Map<String,Object> claims = jwtTokenizer.getClaims(jws,base64EncodedSecretKey).getBody();
        return claims;
    }

    private void logExceptionMessage(AuthenticationException authException, Exception exception) {
        String message = exception != null ? exception.getMessage() : authException.getMessage();
        log.warn("Unauthorized error happened: {}", message);
    }

}

