package com.insigma.mvc.serviceimp.common.jms.listener.queue;

import javax.annotation.Resource;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.insigma.dto.MessageType;
import com.insigma.mvc.model.SInfo;
import com.insigma.mvc.model.SLog;
import com.insigma.mvc.model.SUserLog;
import com.insigma.mvc.service.common.log.ApiLogService;

/**
 * 消息接收服务 日志及站内信
 * @author wengsh
 *
 */
public class JmsQueueMessageListener implements MessageListener {
	
     @Resource
     private ApiLogService logservice;
    /**
     * 接收消息
     */
    @Override
    public void onMessage(Message message) {
        try{
	        // 如果是文本消息
	        if (1==2&&message instanceof TextMessage) {
	        	  String text=((TextMessage) message).getText();
	        	  try
	        	  {
	        	     //将字符串转换成jsonObject对象
	        	     JSONObject myJsonObject =JSONObject.fromObject(text);
	        	    //如果消息类型是接口日志
	        	     if(myJsonObject.get("messagetype").equals(MessageType.MESSAGE_TYPE_LOG.getCode())){
	        	    	 System.out.println("接收到一条接口日志消息");
	        	    	 SLog slog = (SLog) JSONObject.toBean(myJsonObject, SLog.class);
				         logservice.log(slog);
	        	     }
	        	     //用户日志 
	        	     else if(myJsonObject.get("messagetype").equals(MessageType.MESSAGE_TYPE_USERLOG.getCode())){
	        	    	 System.out.println("接收到一条用户日志消息");
						 SUserLog sUserLog = (SUserLog) JSONObject.toBean(myJsonObject, SUserLog.class);
						 logservice.userLog(sUserLog);
	        	     }
	        	     //站内信
	        	     else if(myJsonObject.get("messagetype").equals(MessageType.MESSAGE_TYPE_INFO.getCode())){
	        	    	 System.out.println("接收到一条站内信消息");
	        	    	 SInfo sinfo = (SInfo) JSONObject.toBean(myJsonObject, SInfo.class);
						 logservice.sInfo(sinfo);
	        	     }
	        	  }
	        	  catch (JSONException e)
	        	  {
	        		  e.printStackTrace();
	        	  }
	        }
	
	        // 如果是Map消息
	        if (message instanceof MapMessage) {
	            MapMessage mapmessage = (MapMessage) message;
	        }
	
	        // 如果是Object消息
	        if (message instanceof ObjectMessage) {
	            ObjectMessage om = (ObjectMessage) message;
	        }
	
	        // 如果是bytes消息
	        if (message instanceof BytesMessage) {
	            byte[] b = new byte[1024];
	            int len = -1;
	            BytesMessage bm = (BytesMessage) message;
	            while ((len = bm.readBytes(b)) != -1) {
	                System.out.println(new String(b, 0, len));
	            }
	        }
	
	        // 如果是Stream消息
	        if (message instanceof StreamMessage) {
	            StreamMessage sm = (StreamMessage) message;
	            System.out.println(sm.readString());
	            System.out.println(sm.readInt());
	        }
        }catch(JMSException e){
        	e.printStackTrace();
        }
    }
}
