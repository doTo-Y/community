package com.example.community.community.controller;

import com.example.community.community.dto.AccesstokenDto;
import com.example.community.community.dto.GithubUser;
import com.example.community.community.mapper.UserMapper;
import com.example.community.community.model.User;
import com.example.community.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private UserMapper userMapper;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code")String code,
                           @RequestParam(name="state")String state,
                            HttpServletRequest request,
                            HttpServletResponse response){
        AccesstokenDto accesstokenDto = new AccesstokenDto();
        accesstokenDto.setClient_id(clientId);
        accesstokenDto.setClient_secret(clientSecret);
        accesstokenDto.setRedirect_uri(redirectUri);
        accesstokenDto.setCode(code);
        accesstokenDto.setState(state);
        String accessToken = githubProvider.getAccessToken(accesstokenDto);
        GithubUser githubUser = githubProvider.getUser(accessToken);


        if(githubUser!=null){
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            response.addCookie(new Cookie("token",token));
            return "redirect:/";
        }
        else{
            //登录失败，重新登录
            return "redirect:/";
        }

    }

}
