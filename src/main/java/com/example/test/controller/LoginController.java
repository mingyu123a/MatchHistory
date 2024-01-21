package com.example.test.controller;

import com.example.test.service.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/callback")
public class LoginController{

    @Autowired
    private KakaoService kakaoService;
    @GetMapping("")
    public void getKakaoAuthCode(HttpServletRequest request)throws Exception{
        String authCode = request.getParameter("code");
        System.out.println("Authrization code:"+authCode);
        kakaoService.getKakaoInfo(authCode);
    }
}
