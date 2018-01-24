package com.insigma.mvc.serviceimp.common.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.insigma.common.sysuser.SysUserUtil;
import com.insigma.common.util.ClientInfoUtil;
import com.insigma.common.util.IPUtil;
import com.insigma.common.util.StringUtil;
import com.insigma.common.util.UUIDGenerator;
import com.insigma.dto.AjaxReturnMsg;
import com.insigma.dto.Device;
import com.insigma.dto.OpType;
import com.insigma.mvc.controller.MvcHelper;
import com.insigma.mvc.dao.common.log.ApiLogMapper;
import com.insigma.mvc.model.SEmailLog;
import com.insigma.mvc.model.SErrorLog;
import com.insigma.mvc.model.SInfo;
import com.insigma.mvc.model.SLog;
import com.insigma.mvc.model.SSmsLog;
import com.insigma.mvc.model.SUserLog;
import com.insigma.mvc.service.common.jms.JmsProducerService;
import com.insigma.mvc.service.common.log.ApiLogService;

/**
 * @author wengsh
 */

@Service
public class ApiLogServiceImpl extends MvcHelper implements ApiLogService {

    @Resource
    private ApiLogMapper apilogMapper;

    @Resource
    private JmsProducerService jmsProducerService;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:SSS");

    /**
     * �ӿ���־
     */
    @Override
    @Transactional
    public String log(SLog slog) {
        apilogMapper.saveLogInfo(slog);
        return slog.getLogid();
    }


    /**
     * Ӧ���쳣��־
     */
    @Override
    public String sysErrorLog(Exception e, HttpServletRequest request) {
        SErrorLog sysLog = new SErrorLog();
        if (e.getMessage() != null) {
            sysLog.setMessage(e.getMessage().length() > 500 ? e.getMessage().substring(0, 499) : e.getMessage());
        }
        sysLog.setStackmsg(getStackMsg(e));
        sysLog.setExceptiontype(e.getClass().getName());
        String ip = IPUtil.getClientIpAddr(request);
        /*IPSeekerUtil util=new IPSeekerUtil();*/
        sysLog.setIpaddr(ip);
        /*String country=util.getIpCountry(ip);
        sysLog.setIpaddr(country+"("+ip+")");*/
        sysLog.setUsergent(request.getHeader("user-agent"));
        StringBuffer url = request.getRequestURL();
        if (request.getQueryString() != null && !("").equals(request.getQueryString())) {
            url.append("?" + request.getQueryString());
        }
        sysLog.setUrl(url.toString());
        String cookie = "";
        if (request.getCookies() != null) {
            Cookie[] cookies = request.getCookies();
            for (int i = 0; i < cookies.length; i++) {
                Cookie tempcookie = cookies[i];
                cookie += tempcookie.getName() + ":" + tempcookie.getValue();
            }
            sysLog.setCookie(cookie.length() > 500 ? cookie.substring(0, 499) : cookie);
        }
        apilogMapper.saveSysErrorLog(sysLog);
        return sysLog.getLogid();
    }

    /**
     * ����վ����
     *
     * @param sinfo
     * @return
     */
    @Override
    public String sInfo(SInfo sinfo) {
        if (StringUtil.isEmpty(sinfo.getInfocontent())) {
            System.out.println("��Ϣ���ݲ���Ϊ��");
            return null;
        }
        if (StringUtil.isEmpty(sinfo.getInfotype())) {
            System.out.println("��Ϣ���Ͳ���Ϊ��");
            return null;
        }
        if (StringUtil.isEmpty(sinfo.getUserkind())) {
            System.out.println("�û����ʹ��벻��Ϊ��");
            return null;
        }
        if (StringUtil.isEmpty(sinfo.getEcd001()) && StringUtil.isEmpty(sinfo.getEec001())) {
            System.out.println("�ռ��˱�Ų���Ϊ��");
            return null;
        }

        sinfo.setInfocontent(sinfo.getInfocontent().length() > 500
                ? sinfo.getInfocontent().substring(0, 499)
                : sinfo.getInfocontent());
        sinfo.setIs_read("0");
        sinfo.setAae100("1");

        apilogMapper.saveSInfo(sinfo);
        return sinfo.getInfocode();
    }



    /**
     * �û���־��¼,ҵ������ɹ�ʱ��¼
     *
     * @param optype
     * @param message
     */
    @Override
    public String userLog(OpType optype, String message) {
        return nativeSaveUserLog(optype, SysUserUtil.getCurrentUser() == null ? "" : SysUserUtil.getCurrentUser().getUserid(), message, false, null, "");
    }


    /**
     * �û���־��¼,ҵ�����ʧ��ʱ��¼
     *
     * @param optype
     * @param message
     * @return
     */
    @Override
    public String userErrorLog(OpType optype, String message) {
        return nativeSaveUserLog(optype, SysUserUtil.getCurrentUser() == null ? "" : SysUserUtil.getCurrentUser().getUserid(), message, true, null, "");
    }

    @Override
    public String userLog(OpType optype, String userid, String message) {
        return nativeSaveUserLog(optype, userid, message, false, null, "");
    }

    @Override
    public String userLog(OpType optype, String userid, String message, Date logsttime, String eec117, String eec118, String eec119) {
        return nativeSaveUserLog(optype, userid, message, false, logsttime, "");
    }

    @Override
    public String userErrorLog(OpType optype, String userid, String message) {
        return nativeSaveUserLog(optype, userid, message, true, null, "");
    }

    /**
     * ���������û�������־
     *
     * @param sUserLog
     * @return
     */
    @Override
    public String userLog(SUserLog sUserLog) {
        //����û�������־����Ϊ�գ���ϵͳ����һ��
        if(StringUtil.isEmpty(sUserLog.getLogid())){
            sUserLog.setLogid(UUIDGenerator.generate());
        }

        if (StringUtil.isEmpty(sUserLog.getMessage())) {
            System.out.println("��־���ݲ���Ϊ��");
            return null;
        }
        if (StringUtil.isEmpty(sUserLog.getOpstatus())) {
            System.out.println("ҵ�����״̬����Ϊ��");
            return null;
        }
        if (StringUtil.isEmpty(sUserLog.getAae012())) {
            System.out.println("�����˻����벻��Ϊ��");
            return null;
        }
        if (StringUtil.isEmpty(sUserLog.getOptype())) {
            System.out.println("�������ʹ��벻��Ϊ��");
            return null;
        }
        if (StringUtil.isEmpty(sUserLog.getLogetime_s())) {
            System.out.println("�û�������־��¼����ʱ�䲻��Ϊ��");
            return null;
        } else {
            try {
                String d = sdf.format(Long.parseLong(sUserLog.getLogetime_s()));
                sUserLog.setLogetime(sdf.parse(d));
            } catch (Exception e) {
                System.out.println("�û�������־��¼����ʱ���ʽ�쳣");
                return null;
            }
        }

        sUserLog.setMessage(sUserLog.getMessage().length() > 500 ? sUserLog.getMessage().substring(0, 499) : sUserLog.getMessage());
        apilogMapper.saveUserLog(sUserLog);
        return sUserLog.getLogid();
    }

    /**
     * �����û�������־
     *
     * @param optype    ��������
     * @param userid    �����û�����
     * @param logsttime �û������ռǼ�¼��ʼʱ��
     * @param message   �û���������
     * @param iserror   �û�����״̬
     * @param opreason  �û�����ʧ��ԭ������
     * @return
     */
    private String nativeSaveUserLog(OpType optype, String userid, String message, boolean iserror, Date logsttime, String opreason) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String eec117 = request.getHeader("osName");
        String eec118 = request.getHeader("browserName");
        String eec119 = request.getHeader("ip");

        //��ȡ����ϵͳ���������ip��ַ
        Device device = ClientInfoUtil.getDevice(request);
        if(StringUtil.isEmpty(eec117)){
            eec117 = device.getEec117();
        }
        if(StringUtil.isEmpty(eec118)){
            eec118 = device.getEec118();
        }
        if(StringUtil.isEmpty(eec119)){
            eec119 = device.getEec119();
        }


        SUserLog sUserLog = new SUserLog();
        sUserLog.setLogid(UUIDGenerator.generate());
        sUserLog.setOptype(optype.getCode());
        sUserLog.setAae011(userid);
        sUserLog.setLogstime(logsttime);
        sUserLog.setLogetime(new Date());
        sUserLog.setMessage(message.length() > 500 ? message.substring(0, 499) : message);
        sUserLog.setOpstatus(iserror ? "0" : "1");
        sUserLog.setOpreason(opreason);
        sUserLog.setEec117(eec117);
        sUserLog.setEec118(eec118);
        sUserLog.setEec119(eec119);
        apilogMapper.saveUserLog(sUserLog);

        return sUserLog.getLogid();
    }


    /**
     * ���쳣��ӡ����
     *
     * @param e
     * @return
     */
    private static String getStackMsg(Exception e) {
        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
        return "";
    }

    /**
     * ������Ϣ
     */
    @Override
    public void sendJmsLogMessage(String message) {
        jmsProducerService.sendMessage(message);
    }

    /**
     * ���Ͷ��ż��ʼ���Ϣ
     */
    @Override
    public void sendSmsEmailLogMessage(String message) {
        jmsProducerService.sendMessage(message);
    }
   
    /**
     * ������ŷ�����־
     * @param sSmsLog
     * @return
     * @throws Exception
     */
    @Override
    public AjaxReturnMsg saveSSmsLog(SSmsLog sSmsLog) {
        try {
            apilogMapper.saveSSmsLog(sSmsLog);
        }catch (Exception e){
            e.printStackTrace();
            this.error("������־ʧ�ܣ�ʧ��ԭ��"+e.getMessage());
        }
        return this.success("������־�ɹ�");
    }
    /**
     * �����ʼ�������־
     * @param sEmailLog
     * @return
     * @throws Exception
     */
    @Override
    public AjaxReturnMsg saveSEmailLog(SEmailLog sEmailLog) {
        try {
            apilogMapper.saveSEmailLog(sEmailLog);
        }catch (Exception e){
            e.printStackTrace();
            this.error("������־ʧ�ܣ�ʧ��ԭ��"+e.getMessage());
        }
        return this.success("������־�ɹ�");
    }

}