package com.insigma.mvc.service.common.log;

import javax.servlet.http.HttpServletRequest;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.dto.OpType;
import com.insigma.mvc.model.*;

import java.util.Date;


/**
 * 日志服务
 *
 * @author wengsh
 */
public interface ApiLogService {

    String log(SLog slog);

    /**
     * 日志记录,记录正常日志
     *
     * @param optype
     * @param message
     */
    String userLog(OpType optype, String message);

    /**
     * 用户日志记录,业务操作成功时记录
     *
     * @param optype
     * @param message
     */
    String userLog(OpType optype, String userid, String message);
    String userLog(OpType optype, String userid, String message, Date logsttime, String eec117, String eec118, String eec119);


    /**
     * 用户日志记录,业务操作失败时记录
     *
     * @param optype
     * @param message
     * @return
     */
    String userErrorLog(OpType optype, String message);

    /**
     * 用户日志记录,业务操作失败时记录
     *
     * @param userid
     * @param optype
     * @param message
     * @return
     */
    String userErrorLog(OpType optype, String userid, String message);


    /**
     * 保存用户操作日志
     *
     * @param sUserLog
     */
    String userLog(SUserLog sUserLog);

    String sysErrorLog(Exception e, HttpServletRequest request);

    /**
     * 保存站内信
     *
     * @param sinfo
     * @return
     */
    String sInfo(SInfo sinfo);
 
    /**
     * 发送日志、消息、站内信
     *
     * @param message
     */
    void sendJmsLogMessage(String message);

    /**
     * 发送短信、邮件消息
     *
     * @param message
     */
    void sendSmsEmailLogMessage(String message);


    /**
     * 保存短信发送日志
     * @param sSmsLog
     * @return
     * @throws Exception
     */
    AjaxReturnMsg saveSSmsLog(SSmsLog sSmsLog);
    /**
     * 保存邮件发送日志
     * @param sEmailLog
     * @return
     * @throws Exception
     */
    AjaxReturnMsg saveSEmailLog(SEmailLog sEmailLog);
}
