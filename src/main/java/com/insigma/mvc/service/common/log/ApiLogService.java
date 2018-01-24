package com.insigma.mvc.service.common.log;

import javax.servlet.http.HttpServletRequest;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.dto.OpType;
import com.insigma.mvc.model.*;

import java.util.Date;


/**
 * ��־����
 *
 * @author wengsh
 */
public interface ApiLogService {

    String log(SLog slog);

    /**
     * ��־��¼,��¼������־
     *
     * @param optype
     * @param message
     */
    String userLog(OpType optype, String message);

    /**
     * �û���־��¼,ҵ������ɹ�ʱ��¼
     *
     * @param optype
     * @param message
     */
    String userLog(OpType optype, String userid, String message);
    String userLog(OpType optype, String userid, String message, Date logsttime, String eec117, String eec118, String eec119);


    /**
     * �û���־��¼,ҵ�����ʧ��ʱ��¼
     *
     * @param optype
     * @param message
     * @return
     */
    String userErrorLog(OpType optype, String message);

    /**
     * �û���־��¼,ҵ�����ʧ��ʱ��¼
     *
     * @param userid
     * @param optype
     * @param message
     * @return
     */
    String userErrorLog(OpType optype, String userid, String message);


    /**
     * �����û�������־
     *
     * @param sUserLog
     */
    String userLog(SUserLog sUserLog);

    String sysErrorLog(Exception e, HttpServletRequest request);

    /**
     * ����վ����
     *
     * @param sinfo
     * @return
     */
    String sInfo(SInfo sinfo);
 
    /**
     * ������־����Ϣ��վ����
     *
     * @param message
     */
    void sendJmsLogMessage(String message);

    /**
     * ���Ͷ��š��ʼ���Ϣ
     *
     * @param message
     */
    void sendSmsEmailLogMessage(String message);


    /**
     * ������ŷ�����־
     * @param sSmsLog
     * @return
     * @throws Exception
     */
    AjaxReturnMsg saveSSmsLog(SSmsLog sSmsLog);
    /**
     * �����ʼ�������־
     * @param sEmailLog
     * @return
     * @throws Exception
     */
    AjaxReturnMsg saveSEmailLog(SEmailLog sEmailLog);
}
