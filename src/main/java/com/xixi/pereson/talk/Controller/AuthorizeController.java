package com.xixi.pereson.talk.Controller;

import com.xixi.pereson.talk.Model.Users;
import com.xixi.pereson.talk.Service.UserService;
import com.xixi.pereson.talk.dto.AccessTokendto;
import com.xixi.pereson.talk.dto.GithubUserdto;
import com.xixi.pereson.talk.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @Auther: xixi-98
 * @Date: 2019/12/15 20:11
 * @Description:
 */
@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;
    @Resource
    private UserService userServiceImpl;

    @Value("${AccessTokenfdto.Client_id}")
    private String Client_id;
    @Value("${AccessTokenfdto.Client_secret}")
    private String Client_secret;
    @Value("${AccessTokenfdto.Redirect_uri}")
    private String Redirect_uri;

    /**
    * @Description:  站点得到github回传的信息，包含code 与stateAccessTokendto
     * 用来装配站点再次访问github中需要的数据。可以考虑使用软编码配合构造方法去完成对应内容的注入
     *  将对象注入到作为参数传递为githubProvider 进行模拟浏览器客户端的post请求。  githubProvider返回一个accesstoken的信息
     *  将信息传递给githubProvider 调用方法模拟浏览器客户端的get请求，获得用户信息
    * @Param:  code state
    * @return: index.html 返回初始登录界面
    * @Author: xixi
    * @Date: 2019/12/16
    */
    @RequestMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state, HttpSession session,
                           HttpServletResponse response){

        AccessTokendto accessTokendto = new AccessTokendto(Client_id,Client_secret,code,Redirect_uri,state);

/*      accessTokendto.setClient_id(Client_id);
        accessTokendto.setClient_secret(Client_secret);
        accessTokendto.setCode(code);
        accessTokendto.setState(state);
        accessTokendto.setRedirect_uri(Redirect_uri);*/

        String accessToken = githubProvider.getAccessToken(accessTokendto);

        GithubUserdto githubuser = githubProvider.getUser(accessToken);
        if(githubuser != null && !githubuser.equals("")){
            String token = UUID.randomUUID().toString();
            Users user = new Users();
            user.setAccountid(String.valueOf(githubuser.getId()));
            user.setName(githubuser.getName());
            user.setToekn(token);
            user.setCreatedate(System.currentTimeMillis());
            user.setUpdatedate(user.getCreatedate());
            userServiceImpl.insUser(user);
            Cookie cookie=new Cookie("token",token);
            response.addCookie(cookie);


            return "redirect:/";
        }else{
            return "redirect:/";
        }

    }



}