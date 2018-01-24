package com.insigma.mvc.dao.common.log;

import com.insigma.mvc.model.SEmailLog;
import com.insigma.mvc.model.SErrorLog;
import com.insigma.mvc.model.SInfo;
import com.insigma.mvc.model.SJobLog;
import com.insigma.mvc.model.SLog;
import com.insigma.mvc.model.SSmsLog;
import com.insigma.mvc.model.SUserLog;


/**
 * ��־��¼mapper
 *
 * @author wengsh
 */
public interface ApiLogMapper {

    void saveLogInfo(SLog slog);

    void saveUserLog(SUserLog sUserLog);

    void saveSysErrorLog(SErrorLog sErrorLog);

    void saveSInfo(SInfo sinfo);

    /**
     * ������ŷ�����־
     * @param sSmsLog
     */
    void saveSSmsLog(SSmsLog sSmsLog);

    /**
     * �����ʼ�������־
     * @param sEmailLog
     */
    void saveSEmailLog(SEmailLog sEmailLog);


    /**
     * ����ִ����־
     * @param SJobLog
     */
    void saveJobLog(SJobLog SJobLog);
}
