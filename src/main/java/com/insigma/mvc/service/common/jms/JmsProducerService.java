package com.insigma.mvc.service.common.jms;

import java.io.Serializable;

/**
 * jms��Ϣ����
 *
 * @author wengsh
 */
public interface JmsProducerService {

    /**
     * ��Ĭ�϶��з�����־
     */
    public void sendMessage(final String msg);


    /**
     * ���Ͷ��ż��ʼ�
     */
    public void sendSmsEmailMessage(final String msg);

    /**
     * ��ָ��Destination����map��Ϣ
     *
     * @param message
     */
    public void sendMapMessage(final String message);

    /**
     * ��ָ��Destination�������л��Ķ���
     *
     * @param object      object �������л�
     */
    public void sendObjectMessage(final Serializable object);

    /**
     * ��ָ��Destination�����ֽ���Ϣ
     *
     * @param bytes
     */
    public void sendBytesMessage(final byte[] bytes);

    /**
     * ��Ĭ�϶��з���Stream��Ϣ
     */
    public void sendStreamMessage();
}