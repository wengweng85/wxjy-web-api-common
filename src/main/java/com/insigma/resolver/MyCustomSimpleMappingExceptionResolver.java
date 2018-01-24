package com.insigma.resolver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.dto.SysCode;
import com.insigma.mvc.service.common.log.ApiLogService;


public class MyCustomSimpleMappingExceptionResolver  extends  SimpleMappingExceptionResolver {
	Log log=LogFactory.getLog(MyCustomSimpleMappingExceptionResolver.class);


	@Resource
    ApiLogService logService;
	
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
    	//�Ƿ��ǿ���ģʽ,������ǿ�������ģʽ�Ļ�������ӡ����̨��־
        e.printStackTrace();
        if(!( e instanceof IOException)){
	     	   //������־
	         logService.sysErrorLog(e, request);
	     }
        // Expose ModelAndView for chosen error view.
        String viewName = determineViewName(e, request);
        if (null != viewName) {// JSP��ʽ����
            if (!(request.getHeader("accept").contains("application/json") || (request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").contains("XMLHttpRequest")))) {
            	// ��������첽����
                // Apply HTTP status code for error views, if specified.
                // Only apply it if we're processing a top-level request.
                Integer statusCode = determineStatusCode(request, viewName);
                if (statusCode != null) {
                    applyStatusCodeIfPossible(request, response, statusCode);
                }
                return getModelAndView(viewName, e, request);
            } else {// JSON��ʽ����
                try {
                    PrintWriter writer = response.getWriter();
                    AjaxReturnMsg<String> dto = new AjaxReturnMsg<String>();
                    dto.setSuccess(false);
                    dto.setSyscode(SysCode.SYS_API_EXCEPTION.getCode());
                    dto.setMessage(e.getMessage());
                    writer.write(JSONObject.fromObject(dto).toString());
                    writer.flush();
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        } else {
        	 try {
                 PrintWriter writer = response.getWriter();
                 AjaxReturnMsg<String> dto = new AjaxReturnMsg<String>();
                 dto.setSuccess(false);
                 dto.setSyscode(SysCode.SYS_API_EXCEPTION.getCode());
                 dto.setMessage(e.getMessage());
                 writer.write(JSONObject.fromObject(dto).toString());
                 writer.flush();
                 writer.close();
             } catch (IOException ex) {
                 ex.printStackTrace();
             }
             return null;
        }
    }

}
