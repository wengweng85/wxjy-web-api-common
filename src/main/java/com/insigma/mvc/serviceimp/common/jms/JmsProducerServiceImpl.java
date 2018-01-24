package com.insigma.mvc.serviceimp.common.jms;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.StreamMessage;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.insigma.common.util.CodeValueUtil;
import com.insigma.mvc.model.Param;
import com.insigma.mvc.model.SLog;
import com.insigma.mvc.service.common.jms.JmsProducerService;
import com.insigma.mvc.service.common.log.ApiLogService;

/**
 * jms��Ϣ����
 *
 * @author wengsh
 *
 */
@Service
public class JmsProducerServiceImpl implements JmsProducerService {

	private Log log = LogFactory.getLog(JmsProducerServiceImpl.class);

	@Resource(name = "jmsQueueTemplate")
    private JmsTemplate jmsTemplate;
	
	@Resource(name = "jmsSmsEmailQueueTemplate")
    private JmsTemplate jmsSmsEmailQueueTemplate;
	
	@Resource
	private ApiLogService apilogservice;

	/**
	 * ��Ĭ�϶��з�����Ϣ
	 */
	@Override
    public void sendMessage(final String msg) {
		if(!messageSwitch()) {
			//System.out.println("��Ϣ���п���δ�������޷�����ӿڷ�����־");
			//���ַ���ת����jsonObject����
   	         JSONObject myJsonObject =JSONObject.fromObject(msg);
			 SLog slog = (SLog) JSONObject.toBean(myJsonObject, SLog.class);
	         apilogservice.log(slog);
			return;
		}else{
			String destination = jmsTemplate.getDefaultDestination().toString();
			log.info("�����" + destination + "��������Ϣ------------" + msg);
			jmsTemplate.send(new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(msg);
				}
			});
		}
	
	}
	
	/**
	 * ���Ͷ��ż��ʼ���Ϣ
	 */
	@Override
	public void sendSmsEmailMessage(final String msg) {
		if(!messageSwitch()) {
			System.out.println("��Ϣ���п���δ�������޷�֪ͨ�ͻ��˷��Ͷ��Ż��ʼ�");
			return;
		}
		String destination = jmsSmsEmailQueueTemplate.getDefaultDestination().toString();
		log.info("�����" + destination + "��������Ϣ------------" + msg);
		jmsSmsEmailQueueTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(msg);
			}
		});
	}


	/**
	 * ��ָ��Destination����map��Ϣ
	 *
	 * @param message
	 */
	@Override
    public void sendMapMessage(final String message) {
		if(!messageSwitch()) {
			System.out.println("��Ϣ���п���δ����");
			return;
		}
		String destination = jmsTemplate.getDefaultDestination().toString();
		jmsTemplate.send(destination, new MessageCreator() {
			@Override
            public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("msgId", message);
				return mapMessage;
			}
		});
		System.out.println("springJMS send map message...");
	}

	/**
	 * ��ָ��Destination�������л��Ķ���
	 *
	 * @param object object �������л�
	 */
	@Override
    public void sendObjectMessage(final Serializable object) {
		if(!messageSwitch()) {
			System.out.println("��Ϣ���п���δ����");
			return;
		}
		String destination = jmsTemplate.getDefaultDestination().toString();
		jmsTemplate.send(destination, new MessageCreator() {
			@Override
            public Message createMessage(Session session) throws JMSException {
				return session.createObjectMessage(object);
			}
		});
		System.out.println("springJMS send object message...");
	}

	/**
	 * ��ָ��Destination�����ֽ���Ϣ
	 *
	 * @param bytes
	 */
	@Override
    public void sendBytesMessage(final byte[] bytes) {
		if(!messageSwitch()) {
			System.out.println("��Ϣ���п���δ����");
			return;
		}
		String destination = jmsTemplate.getDefaultDestination().toString();
		jmsTemplate.send(destination, new MessageCreator() {
			@Override
            public Message createMessage(Session session) throws JMSException {
				BytesMessage bytesMessage = session.createBytesMessage();
				bytesMessage.writeBytes(bytes);
				return bytesMessage;

			}
		});
		System.out.println("springJMS send bytes message...");
	}

	/**
	 * ��Ĭ�϶��з���Stream��Ϣ
	 */
	@Override
    public void sendStreamMessage() {
		if(!messageSwitch()) {
			System.out.println("��Ϣ���п���δ����");
			return;
		}
		String destination = jmsTemplate.getDefaultDestination().toString();
		jmsTemplate.send(destination, new MessageCreator() {
			@Override
            public Message createMessage(Session session) throws JMSException {
				StreamMessage message = session.createStreamMessage();
				message.writeString("stream string");
				message.writeInt(11111);
				return message;
			}
		});
		System.out.println("springJMS send Strem message...");
	}
	
	/**
	 * ��Ϣ���п���
	 * @return
	 */
	public boolean messageSwitch(){
		if(Param.MESSAGE_SWITCH_ON.equals(CodeValueUtil.getAaa005Byaaa001(Param.MESSAGE_SWITCH))){
			return true;
		}else {
			return false;
		}
	}
}