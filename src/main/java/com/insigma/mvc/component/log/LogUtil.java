package com.insigma.mvc.component.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.CoyoteOutputStream;
import org.apache.catalina.connector.OutputBuffer;
import org.apache.tomcat.util.buf.ByteChunk;

import com.insigma.common.util.EhCacheUtil;
import com.insigma.common.util.IPUtil;
import com.insigma.mvc.model.CodeValue;
import com.insigma.mvc.model.SLog;

public class LogUtil {
	
	

    /**
     * �ļ���¼
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    public static SLog parseRequestToLog(HttpServletRequest request, HttpServletResponse response, Exception e) {
        SLog slog = new SLog();
            /*if(e!=null&&e.getMessage()!=null){
	        	 slog.setMessage(e.getMessage().length()>500?e.getMessage().substring(0,499):e.getMessage()); 
	        	 slog.setExceptiontype(e.getClass().getName());
	        	 slog.setStackmsg(getStackMsg(e));
	        }*/
        //������Ϣ
        slog.setUrl(request.getRequestURI());
        slog.setMethod(request.getMethod());
        slog.setQueryparam(getQueryParam(request));
        String ip = IPUtil.getClientIpAddr(request);
        slog.setIpaddr(ip);
        slog.setAppkey(request.getHeader("appkey"));
        slog.setUsergent(request.getHeader("user-agent"));
        slog.setReferer(request.getHeader("Referer")==null?"":request.getHeader("Referer"));
        StringBuffer url = new StringBuffer(request.getRequestURI().toString());
        slog.setUrl(url.toString());
        Cookie[] cookies = request.getCookies();
        StringBuffer sb = new StringBuffer("");
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                sb.append(cookie.getName());
                sb.append(":");
                sb.append(cookie.getValue());
            }
            slog.setCookie(sb.length() > 500 ? sb.substring(0, 499) : sb.toString());
        }else{
        	slog.setCookie("");
        }
        //response��Ϣ
        try {
            //slog.setResponsemsg(parseResponse(response));
            if(e!=null){
                slog.setExceptiontype(e.getClass().getName());
            }else{
                slog.setExceptiontype("");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return slog;
    }

    /**
     * @param response
     * @throws Exception
     */
    public static String parseResponse(HttpServletResponse response) throws Exception {
        String val = "";
        if(!response.isCommitted()){
            // ��ȡ��Ӧ��
            CoyoteOutputStream os = (CoyoteOutputStream) response.getOutputStream();
            // ȡ���������Ӧ��Class����
            Class<CoyoteOutputStream> c = CoyoteOutputStream.class;
            // ȡ���������е�OutputBuffer���󣬸ö����¼��Ӧ���ͻ��˵�����
            Field fs = c.getDeclaredField("ob");
            if (fs.getType().toString().endsWith("OutputBuffer")) {
                fs.setAccessible(true);// ���÷���ob���Ե�Ȩ��
                OutputBuffer ob = (OutputBuffer) fs.get(os);// ȡ��ob
                Class<OutputBuffer> cc = OutputBuffer.class;
                Field ff = cc.getDeclaredField("outputChunk");// ȡ��OutputBuffer�е������
                ff.setAccessible(true);
                if (ff.getType().toString().endsWith("ByteChunk")) {
                    ByteChunk bc = (ByteChunk) ff.get(ob);// ȡ��byte��
                    val = new String(bc.getBytes(), "UTF-8");// ���յ�ֵ
                }
            }
        }
        return val;
    }



    /**
     * ���쳣��ӡ����
     *
     * @param e
     * @return
     */
    private static String getStackMsg(Exception e) {
        if (e == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }


    /**
     * �õ��������
     *
     * @param request
     * @return
     */
    public static String getQueryParam(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer();
        Map map = request.getParameterMap();
        Set keSet = map.entrySet();
        for (Iterator itr = keSet.iterator(); itr.hasNext(); ) {
            Map.Entry me = (Map.Entry) itr.next();
            Object ok = me.getKey();
            Object ov = me.getValue();
            String[] value = new String[1];
            if (ov instanceof String[]) {
                value = (String[]) ov;
            } else {
                value[0] = ov.toString();
            }
            for (String k: value) {
                sb.append(ok);
                sb.append("=");
                sb.append(k);
                sb.append("&");
            }
        }
        return sb.length() > 0 ? sb.deleteCharAt(sb.length() - 1).toString() : "";
    }

}
