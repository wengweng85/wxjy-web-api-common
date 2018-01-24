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
 * ��Ϣ���շ��� ��־��վ����
 * @author wengsh
 *
 */
public class JmsQueueMessageListener implements MessageListener {
	
     @Resource
     private ApiLogService logservice;
    /**
     * ������Ϣ
     */
    @Override
    public void onMessage(Message message) {
        try{
	        // ������ı���Ϣ
	        if (1==2&&message instanceof TextMessage) {
	        	  String text=((TextMessage) message).getText();
	        	  try
	        	  {
	        	     //���ַ���ת����jsonObject����
	        	     JSONObject myJsonObject =JSONObject.fromObject(text);
	        	    //�����Ϣ�����ǽӿ���־
	        	     if(myJsonObject.get("messagetype").equals(MessageType.MESSAGE_TYPE_LOG.getCode())){
	        	    	 System.out.println("���յ�һ���ӿ���־��Ϣ");
	        	    	 SLog slog = (SLog) JSONObject.toBean(myJsonObject, SLog.class);
				         logservice.log(slog);
	        	     }
	        	     //�û���־ 
	        	     else if(myJsonObject.get("messagetype").equals(MessageType.MESSAGE_TYPE_USERLOG.getCode())){
	        	    	 System.out.println("���յ�һ���û���־��Ϣ");
						 SUserLog sUserLog = (SUserLog) JSONObject.toBean(myJsonObject, SUserLog.class);
						 logservice.userLog(sUserLog);
	        	     }
	        	     //վ����
	        	     else if(myJsonObject.get("messagetype").equals(MessageType.MESSAGE_TYPE_INFO.getCode())){
	        	    	 System.out.println("���յ�һ��վ������Ϣ");
	        	    	 SInfo sinfo = (SInfo) JSONObject.toBean(myJsonObject, SInfo.class);
						 logservice.sInfo(sinfo);
	        	     }
	        	  }
	        	  catch (JSONException e)
	        	  {
	        		  e.printStackTrace();
	        	  }
	        }
	
	        // �����Map��Ϣ
	        if (message instanceof MapMessage) {
	            MapMessage mapmessage = (MapMessage) message;
	        }
	
	        // �����Object��Ϣ
	        if (message instanceof ObjectMessage) {
	            ObjectMessage om = (ObjectMessage) message;
	        }
	
	        // �����bytes��Ϣ
	        if (message instanceof BytesMessage) {
	            byte[] b = new byte[1024];
	            int len = -1;
	            BytesMessage bm = (BytesMessage) message;
	            while ((len = bm.readBytes(b)) != -1) {
	                System.out.println(new String(b, 0, len));
	            }
	        }
	
	        // �����Stream��Ϣ
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
