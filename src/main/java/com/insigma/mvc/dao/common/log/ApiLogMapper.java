package com.insigma.mvc.dao.common.log;

import com.insigma.mvc.model.SEmailLog;
import com.insigma.mvc.model.SErrorLog;
import com.insigma.mvc.model.SInfo;
import com.insigma.mvc.model.SJobLog;
import com.insigma.mvc.model.SLog;
import com.insigma.mvc.model.SSmsLog;
import com.insigma.mvc.model.SUserLog;


/**
 * 日志记录mapper
 *
 * @author wengsh
 */
public interface ApiLogMapper {

    void saveLogInfo(SLog slog);

    void saveUserLog(SUserLog sUserLog);

    void saveSysErrorLog(SErrorLog sErrorLog);

    void saveSInfo(SInfo sinfo);

    /**
     * 保存短信发送日志
     * @param sSmsLog
     */
    void saveSSmsLog(SSmsLog sSmsLog);

    /**
     * 保存邮件发送日志
     * @param sEmailLog
     */
    void saveSEmailLog(SEmailLog sEmailLog);


    /**
     * 任务执行日志
     * @param SJobLog
     */
    void saveJobLog(SJobLog SJobLog);
}
