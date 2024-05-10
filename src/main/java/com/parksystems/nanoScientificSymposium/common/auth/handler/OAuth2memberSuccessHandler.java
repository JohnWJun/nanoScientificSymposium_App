//package com.parksystems.nanoScientificSymposium.common.auth.handler;
//
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.net.URI;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Component
//public class OAuth2memberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final JwtTokenizer jwtTokenizer;
//    private final CustomAuthorityUtils authorityUtils;
//    private final MemberService memberService;
//
//
//    public OAuth2memberSuccessHandler(JwtTokenizer jwtTokenizer, CustomAuthorityUtils authorityUtils, MemberService memberService) {
//        this.jwtTokenizer = jwtTokenizer;
//        this.authorityUtils = authorityUtils;
//        this.memberService = memberService;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        var oAuth2User = (OAuth2User) authentication.getPrincipal();
//        String email = String.valueOf(oAuth2User.getAttributes().get("email"));
//        String pic = String.valueOf(oAuth2User.getAttributes().get("picture"));
//        List<String> authorities = authorityUtils.createRoles(email);
//        if (memberService.isExistsEmail(email)){
//            saveMember(email,pic);
//            log.info("Google을 통해 유저: "+email+"이(가) 등록되었습니다.");
//        }
//        redirect(request,response,email,authorities);
//    }
//
//    private void redirect(HttpServletRequest request, HttpServletResponse response, String username,List<String> authorities) throws IOException{
//
//        String accessToken = delegateAccessToken(username, authorities);
//        String refreshToken = delegateRefreshToken(username);
//
//        String uri = createURI(accessToken,refreshToken).toString();
//
//        getRedirectStrategy().sendRedirect(request,response,uri);
//    }
//
//    public void redirectWithNewToken(HttpServletRequest request, HttpServletResponse response,String uri) throws IOException{
//        getRedirectStrategy().sendRedirect(request,response,uri);
//    }
//
//    private URI createURI(String accessToken, String refreshToken) {
//        MultiValueMap<String,String> queryParams = new LinkedMultiValueMap<>();
//        queryParams.add("access_token", accessToken);
//        queryParams.add("refresh_token", refreshToken);
//
//        return UriComponentsBuilder.newInstance()
//                .scheme("http")
//                .host("localhost")
//                .port(3000)
//                .path("/mytoken")//redirect 받기 위한 주소
//                .queryParams(queryParams)
//                .build().toUri();
//    }
//
//    private String delegateAccessToken(String username, List<String> authorities) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("username",username);
//        claims.put("roles", authorities);
//
//        String subject = username;
//
//        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
//
//        String base64EncodedSecretKey = jwtTokenizer.encodedBasedSecretKey(jwtTokenizer.getSecretKey());
//        String accessToken = jwtTokenizer.generateAccessToken(claims,subject,expiration,base64EncodedSecretKey);
//        return accessToken;
//    }
//
//    private String delegateRefreshToken(String username) {
//
//        String subject = username;
//
//        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
//
//        String base64EncodedSecretKey = jwtTokenizer.encodedBasedSecretKey(jwtTokenizer.getSecretKey());
//        String refreshToken = jwtTokenizer.generateRefreshToken(subject,expiration,base64EncodedSecretKey);
//        return refreshToken;
//    }
//
//    private void saveMember(String email, String pic) {
//        if (memberService.isExistsEmail(email)) {
//            Member member = new Member();
//            List<String> roles = authorityUtils.createRoles(email);
//            member.setRoles(roles);
//            member.setEmail(email);
//            member.setStatusImg(pic);
//            memberService.createMember(member);
//        }
//    }
//}
