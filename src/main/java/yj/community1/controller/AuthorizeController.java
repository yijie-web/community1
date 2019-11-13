package yj.community1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import yj.community1.dto.AcessTokenDTO;
import yj.community1.dto.GithubUser;
import yj.community1.mapper.UserMapper;
import yj.community1.model.User;
import yj.community1.provider.GithubProvider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/*
Created by yj on 2019/10/26
 */
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
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state, HttpServletRequest request,
                           HttpServletResponse response){
        AcessTokenDTO acessTokenDTO= new AcessTokenDTO();
        acessTokenDTO.setClient_id(clientId);
        //acessTokenDTO.setClient_id("6dbcad6b37b368f82178");
        acessTokenDTO.setClient_secret(clientSecret);
        //acessTokenDTO.setClient_secret("f8f28647ea6ae2ec67fc4e88ce1d11f5795f6c13");
        acessTokenDTO.setCode(code);
        acessTokenDTO.setRedirect_uri(redirectUri);
        //acessTokenDTO.setRedirect_uri("http://localhost:8080/callback");
        acessTokenDTO.setState(state);
        String accessToken=githubProvider.getAccessToken(acessTokenDTO);
        GithubUser githubUser=githubProvider.getUser(accessToken);
        if(githubUser != null && githubUser.getId() != null){
            User user=new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarurl(githubUser.getAvatar_url());
            userMapper.Insert(user);
            response.addCookie(new Cookie("token",user.getToken()));
            //登录成功，写cookie和session
            //request.getSession().setAttribute("githubUser",githubUser);
            return "redirect:/";
        }else{
            //登录失败，重新登录
            return "redirect:/";
        }
        //System.out.printf(githubUser.getName());
          }
}
