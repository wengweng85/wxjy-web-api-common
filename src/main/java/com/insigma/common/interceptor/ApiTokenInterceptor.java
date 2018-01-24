package com.insigma.common.interceptor;

import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.insigma.common.jwt.JWT;
import com.insigma.common.sysuser.SysUserUtil;
import com.insigma.dto.AjaxReturnMsg;
import com.insigma.dto.SysCode;
import com.insigma.mvc.model.SUser;
import com.insigma.mvc.model.SysApiInterface;
import com.insigma.mvc.service.common.log.ApiLogService;

/**
 * ApiInterceptor
 *
 * @author wengsh
 */
public class ApiTokenInterceptor extends HandlerInterceptorAdapter {

    private Log log = LogFactory.getLog(ApiInterceptor.class);
    private static int OVERTIME = 500;

    @Resource
    private ApiLogService logservice;

    private NamedThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("StopWatch-StartTime");
    
    private NamedThreadLocal<SysApiInterface> apiInterfaceThreadLocal = new NamedThreadLocal<>("apiInterfaceThreadLocal");

    private static final String USERIDNAME = "userid";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long beginTime = System.currentTimeMillis();//1、开始时间
        startTimeThreadLocal.set(beginTime);//线程绑定变量（该数据只有当前请求的线程可见）
        if (handler instanceof HandlerMethod) {
            String token = request.getHeader("Authorization") == null ? "" : request.getHeader("Authorization");
            //生定向到重复提交页面
            AjaxReturnMsg<String> dto = new AjaxReturnMsg<>();
            if ("".equals(token)) {
                PrintWriter writer = response.getWriter();
                dto.setSuccess(false);
                dto.setSyscode(SysCode.SYS_TOKEN_EMPTY.getCode());
                dto.setMessage(SysCode.SYS_TOKEN_EMPTY.getName());
                writer.write(JSONObject.fromObject(dto).toString());
                writer.flush();
                //writer.close();
                return false;
            } else {
            	if(token.length()>7){
            		  SUser suser = JWT.unsign(token.substring(7, token.length()), SUser.class);
                      if (suser != null) {
                          suser.setToken(token.substring(7, token.length()));
                          //将当前登录信息设置到本地线程变量中
                          SysUserUtil.setCurrentUser(suser);
                          //如果request中的userid不为空且与token中的userid不一致
                          if (null != request.getParameter(USERIDNAME) && !request.getParameter(USERIDNAME).equals(suser.getUserid())) {
                              PrintWriter writer = response.getWriter();
                              dto.setSuccess(false);
                              dto.setSyscode(SysCode.SYS_USERID_ERROR.getCode());
                              dto.setMessage(SysCode.SYS_USERID_ERROR.getName());
                              writer.write(JSONObject.fromObject(dto).toString());
                              writer.flush();
                              //writer.close();
                              return false;
                          }
                      } else {
                          PrintWriter writer = response.getWriter();
                          dto.setSuccess(false);
                          dto.setSyscode(SysCode.SYS_TOKEN_ERROR.getCode());
                          dto.setMessage(SysCode.SYS_TOKEN_ERROR.getName());
                          writer.write(JSONObject.fromObject(dto).toString());
                          writer.flush();
                          //writer.close();
                          return false;
                      }
            	}else {
                    PrintWriter writer = response.getWriter();
                    dto.setSuccess(false);
                    dto.setSyscode(SysCode.SYS_TOKEN_ERROR.getCode());
                    dto.setMessage(SysCode.SYS_TOKEN_ERROR.getName());
                    writer.write(JSONObject.fromObject(dto).toString());
                    writer.flush();
                    //writer.close();
                    return false;
                }
            }
            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }

    /**
     * afterCompletion
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
     
    }

}
