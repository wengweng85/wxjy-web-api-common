package com.insigma.mvc.service.common.jms;

import java.io.Serializable;

/**
 * jms消息服务
 *
 * @author wengsh
 */
public interface JmsProducerService {

    /**
     * 向默认队列发送日志
     */
    public void sendMessage(final String msg);


    /**
     * 发送短信及邮件
     */
    public void sendSmsEmailMessage(final String msg);

    /**
     * 向指定Destination发送map消息
     *
     * @param message
     */
    public void sendMapMessage(final String message);

    /**
     * 向指定Destination发送序列化的对象
     *
     * @param object      object 必须序列化
     */
    public void sendObjectMessage(final Serializable object);

    /**
     * 向指定Destination发送字节消息
     *
     * @param bytes
     */
    public void sendBytesMessage(final byte[] bytes);

    /**
     * 向默认队列发送Stream消息
     */
    public void sendStreamMessage();
}