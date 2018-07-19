package com.zqu.pa.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zqu.pa.common.ServerResponse;
import com.zqu.pa.common.UserLoginException;
import com.zqu.pa.entity.User;
import com.zqu.pa.service.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private User user;

    /**
     * 测试
     * @return
     */
    @ResponseBody
    @RequestMapping("/user")
    @RequiresPermissions("adminwork")
    public User getUser() {
        return userService.getUser();
    }

    /**
     * 测试
     * @return
     */
    @ResponseBody
    @RequestMapping("/user1")
    public ServerResponse<User> getStatusUser() {
        user = userService.getUser();
        //ServerResponse<User> serverResponse = ServerResponse.createBySuccess("成功信息", user);
        ServerResponse<User> serverResponse = ServerResponse.createByErrorCodeMessage(404, "错误信息");
        return serverResponse;
    }

    /**
     * 用户账户是否存在测试
     * @param request
     * @return
     */

    @RequestMapping(value="/login.do",method=RequestMethod.GET)
    public String login(HttpServletRequest request)throws Exception {
        String message;
        //如果登陆失败从request中获取认证异常信息，shiroLoginFailure就是shiro异常类的全限定名
        String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
        //根据shiro返回的异常类路径判断，抛出指定异常信息
        if(exceptionClassName!=null){
            if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
                //最终会抛给异常处理器
                throw new UserLoginException("用户不存在");
            } else if (IncorrectCredentialsException.class.getName().equals(
                    exceptionClassName)) {
                throw new UserLoginException("用户名/密码错误");
            } else if("randomCodeError".equals(exceptionClassName)){
                throw new UserLoginException("验证码错误 ");
            }else {
                throw new Exception();
            }
        }
        //此方法不处理登陆成功（认证成功），shiro认证成功会自动跳转到上一个请求路径
        //登陆失败还到login页面
        return "redirect:/views/testLogin.html";
    }

    /**
     * 登录验证
     * @param from_user
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/login",method=RequestMethod.POST)
    public String login(User from_user) {
        String userId = from_user.getUserId();
        String password = from_user.getPassword();
        Subject subject = SecurityUtils.getSubject(); 
        UsernamePasswordToken token = new UsernamePasswordToken(userId, password); 
        try { 
            subject.login(token);
            Session session=subject.getSession();
            session.setAttribute("subject", subject);
            return "yes";
        } catch (UnknownAccountException ua) {
            return "userId does not exist";
        } catch (IncorrectCredentialsException ic) {
            return "userId or password is error";
        }
    }
}
