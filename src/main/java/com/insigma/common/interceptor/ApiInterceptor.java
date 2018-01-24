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

import com.insigma.common.sysuser.SysUserUtil;
import com.insigma.dto.AjaxReturnMsg;
import com.insigma.dto.MessageType;
import com.insigma.dto.SysCode;
import com.insigma.mvc.component.log.LogUtil;
import com.insigma.mvc.model.SLog;
import com.insigma.mvc.model.SysApiChannel;
import com.insigma.mvc.model.SysApiInterface;
import com.insigma.mvc.service.api.API_MGMT_001_001.SysApiInterfacService;
import com.insigma.mvc.service.api.API_MGMT_001_002.SysApiChannelService;
import com.insigma.mvc.service.common.log.ApiLogService;

/**
 * ApiInterceptor
 *
 * @author wengsh
 */
public class ApiInterceptor extends HandlerInterceptorAdapter {

    private Log log = LogFactory.getLog(ApiInterceptor.class);

    private static int OVERTIME = 500;

    @Resource
    private ApiLogService logservice;
    
    @Resource
    private SysApiChannelService sysApiChannelService;
    
    @Resource
    private SysApiInterfacService sysApiInterfacService;

    private NamedThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<>("StopWatch-StartTime");

    private NamedThreadLocal<SysApiInterface> apiInterfaceThreadLocal = new NamedThreadLocal<>("apiInterfaceThreadLocal");
    

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	//����ķ����ַ
    	String url=request.getRequestURI().toString();
    	
        long beginTime = System.currentTimeMillis();//1����ʼʱ��
        startTimeThreadLocal.set(beginTime);//�̰߳󶨱�����������ֻ�е�ǰ������߳̿ɼ���
        if (handler instanceof HandlerMethod) {
            String appkey = request.getHeader("appkey") == null ? "" : request.getHeader("appkey");
            //�������ظ��ύҳ��
            AjaxReturnMsg<String> dto = new AjaxReturnMsg<String>();
            if ("".equals(appkey)) {
                PrintWriter writer = response.getWriter();
                dto.setSuccess(false);
                dto.setSyscode(SysCode.SYS_APPKEY_EMPTY.getCode());
                dto.setMessage(SysCode.SYS_APPKEY_EMPTY.getName());
                writer.write(JSONObject.fromObject(dto).toString());
                writer.flush();
                //writer.close();
                return false;
            }else if (!validateAppKeyIsValid(appkey)) {
                PrintWriter writer = response.getWriter();
                dto.setSuccess(false);
                dto.setSyscode(SysCode.SYS_APPKEY_ERROR.getCode());
                dto.setMessage(SysCode.SYS_APPKEY_ERROR.getName());
                writer.write(JSONObject.fromObject(dto).toString());
                writer.flush();
               // writer.close();
                return false;
            }/*else {
            	SysApiInterface sysApiInterface=validateUrlIsValid(url,appkey);
            	apiInterfaceThreadLocal.set(sysApiInterface);
            	if(sysApiInterface==null){
            		log.info("�����uri="+url);
            		PrintWriter writer = response.getWriter();
                    dto.setSuccess("false");
                    dto.setSyscode(SysCode.SYS_SERVICEURL_ERROR.getCode());
                    dto.setMessage(SysCode.SYS_SERVICEURL_ERROR.getName());
                    writer.write(JSONObject.fromObject(dto).toString());
                    writer.flush();
                    
                    // writer.close();
                    return false;           	}
            }*/
            return true;
        }
        return super.preHandle(request, response, handler);
    }

    /**
     * �ж�appkey�Ƿ���Ч
     *
     * @param key
     * @param keylist
     * @return
     */
    public boolean validateAppKeyIsValid(String key) {
    	SysApiChannel sysApiChannel=sysApiChannelService.getApiChannelByAppkey(key);
    	if(sysApiChannel!=null){
    		if(sysApiChannel.getChannelStatus().equals("1")){
    			return true;
    		}else{
    			return false;
    		}
    	}else{
    		return false;
    	}
    }
    
    /**
     * �ж��Ƿ��ж�Ӧ����ķ���Ȩ��
     *
     * @param key
     * @param keylist
     * @return
     */
    public SysApiInterface validateUrlIsValid(String url,String appkey) {
    	SysApiInterface sysApiInterface =sysApiInterfacService.selectByUrl(url, appkey);
    	if(sysApiInterface!=null){
    		if(sysApiInterface.getInterfaceStatus().equals("1")){
    			return sysApiInterface;
    		}else{
    			return null;
    		}
    	}else{
    		return null;
    	}
    }

    /**
     * afterCompletion
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //2������ʱ��
        long endTime = System.currentTimeMillis();
        //�õ��̰߳󶨵ľֲ���������ʼʱ�䣩
        long beginTime = startTimeThreadLocal.get();
        //3�����ĵ�ʱ��
        long consumeTime = endTime - beginTime;
        //�˴���Ϊ����ʱ�䳬��500���������Ϊ������
        if (consumeTime > OVERTIME) {
            log.info(String.format("%s consume %d millis", request.getRequestURI(), consumeTime));
        }
        SysApiInterface sysApiInterface= apiInterfaceThreadLocal.get();
        if(sysApiInterface!=null){
	            SLog slog = LogUtil.parseRequestToLog(request, response, ex);
	            slog.setAppkey(sysApiInterface.getChannelId());//��������
	            slog.setInterfacetype(sysApiInterface.getInterfaceId());//�������
	            slog.setMessage("�ӿڷ���-����Ҫ����token");
	            slog.setCost(Long.toString(consumeTime));
	            slog.setSuccess("true");
	            slog.setUserid(SysUserUtil.getCurrentUser()!=null?SysUserUtil.getCurrentUser().getUserid():"");
	            slog.setToken(SysUserUtil.getCurrentUser()!=null?SysUserUtil.getCurrentUser().getToken():"");
	            slog.setMessagetype(MessageType.MESSAGE_TYPE_LOG.getCode());//ϵͳ��־
	            logservice.sendJmsLogMessage(JSONObject.fromObject(slog).toString());
            }
        }
}
