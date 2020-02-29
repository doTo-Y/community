package com.example.community.community.controller;

import com.example.community.community.dto.AccesstokenDto;
import com.example.community.community.dto.GithubUser;
import com.example.community.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code")String code,
                           @RequestParam(name="state")String state){
        AccesstokenDto accesstokenDto = new AccesstokenDto();
        accesstokenDto.setClient_id(clientId);
        accesstokenDto.setClient_secret(clientSecret);
        accesstokenDto.setRedirect_uri(redirectUri);
        accesstokenDto.setCode(code);
        accesstokenDto.setState(state);
        String accessToken = githubProvider.getAccessToken(accesstokenDto);
        GithubUser user = githubProvider.getUser(accessToken);
        System.out.println(user.getName());
        return "index";

    }

}
