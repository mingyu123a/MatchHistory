package com.example.test.service;

import com.example.test.repository.UserRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.client.RestTemplate;

@Service
public class KakaoService {
    @Autowired
    private UserRepository userRepository;


    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect_uri}")
    private String KAKAO_REDIRECT_URL;

    private final static String KAKAO_AUTH_URI = "https://kauth.kakao.com";
    //Authorization 코드 받기 위한 도메인
    private final static String KAKAO_API_URI = "https://kapi.kakao.com";
    //Access Token 받은 후 유저 정보 받기 위한 도메인
    public void getKakaoInfo(String code) throws Exception {
        //얘는 KakaoController의 호출로 넘어옴, KakaoDTO class의 함수

        if (code == null) throw new Exception("Failed get authorization code");

        String accessToken = "";
        String refreshToken = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", KAKAO_CLIENT_ID);
            params.add("client_secret", KAKAO_CLIENT_SECRET);
            params.add("redirect_uri", KAKAO_REDIRECT_URL);
            params.add("code", code);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    KAKAO_AUTH_URI + "/oauth/token",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            String responseBody = responseEntity.getBody();
            System.out.println(responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            accessToken = jsonNode.get("access_token").asText();
            refreshToken = jsonNode.get("refresh_token").asText();
            System.out.println("access_token: " + accessToken);
            System.out.println("refresh_token: " + refreshToken);

//                HttpSession session = request.getSession();
//                session.setAttribute("accessToken", accessToken);
//                //session.setAttribute("refreshToken", refreshToken);
//                //리프토큰은 세션에 저장X

        } catch (Exception e) {
                System.out.println(e);
            }
            getUserInfoWithToken(accessToken, refreshToken);
               //리턴해서 이제 accessToken을 파라미터로 아래 함수 호출
    }

    private void getUserInfoWithToken(String accessToken, String refreshToken) throws Exception {
        //HttpHeader 생성
        //curl -v -X GET "https://kapi.kakao.com/v2/user/me" \
        //  -H "Authorization: Bearer ${ACCESS_TOKEN}"
        //위 양식에 따라 요청,
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "Content-type: application/x-www-form-urlencoded;charset=utf-8");

        //HttpHeader 담기, 위의 과정과 동일
        RestTemplate restTem = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTem.exchange(
                KAKAO_API_URI + "/v2/user/me",
                HttpMethod.POST,
                httpEntity,
                String.class
        );
        System.out.println(response);

        String responseBody = response.getBody();
        System.out.println(responseBody);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        JsonNode jsonNodeAccount = jsonNode.get("kakao_account");
        String email = jsonNodeAccount.get("email").asText();
        JsonNode jsonNodeNickname = jsonNode.get("properties");
        String nickname = jsonNodeNickname.get("nickname").asText();

        System.out.println(email);
        System.out.println(nickname);

//        Response 데이터 JSON 파싱
//        JSONParser jsonParser = new JSONParszzer();
//        JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());
//        JSONObject account = (JSONObject) jsonObj.get("kakao_account");
//      JSONObject profile = (JSONObject) account.get("profile");
//        System.out.println("json: "+ jsonObj);
//
//        String email = String.valueOf(account.get("email"));
//        String nickname = String.valueOf(profile.get("nickname"));
//        String profileImgUrl = String.valueOf(profile.get("profile_image_url"));
//
//        KakaoDTO kakaoUser = KakaoDTO.builder()
//                .email(email)
//                .nickname(nickname)
//                .profileImgUrl(profileImgUrl)
//                .build();
//
//        return saveKakaoUser(kakaoUser, refreshToken);
//    }
//    private UserEntity saveKakaoUser(KakaoDTO kakaoUser, String refreshToken) {
//        System.out.println("save User in UserEntity repo");
//        Optional<UserEntity> existingUserEntity = UserRepository.findByEmail(kakaoUser.getEmail()); //repo메서드로 조회
//        if (existingUserEntity.isPresent()) {
//            UserEntity UserEntity = existingUserEntity.get();   //기존정보 get해서
//            UserEntity.setNickname(kakaoUser.getNickname());    //닉네임 업뎃
//            UserEntity.setRefreshToken(refreshToken);   //refreshToken업뎃
//            UserEntity.setProfileImgUrl(kakaoUser.getProfileImgUrl());  //프로필 아이콘 업데이트
//            return UserEntityRepository.save(UserEntity);  //저장
//        } else {
//            UserEntity newUserEntity = UserEntity.builder() //빌더로 설정
//                    .mpw("1111") // 비번1111고정
//                    .email(kakaoUser.getEmail()) //
//                    .nickname(kakaoUser.getNickname()) // 카카오 닉네임
//                    .refreshToken(refreshToken) // 리프레시 토큰
//                    //.roleSet(Collections.singleton(UserEntityRole.USER)) // 역할 설정
//                    .profileImgUrl(kakaoUser.getProfileImgUrl())
//                    .build();
//            return UserRepository.save(newUserEntity);
    }
}