package com.insigma.mvc.controller;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.github.pagehelper.PageInfo;
import com.insigma.dto.AjaxReturnMsg;


/**
 * mvc�����࣬��Ҫ���ڰ�װcontroller�Լ�serviceimp�㷵�ص�����
 * �����װ���෵�س�json��ʽ
 *
 * @author wengsh
 */
public class MvcHelper {

    public AjaxReturnMsg validate(BindingResult result) {
	    // �ֶ�У�飬û��˳��
        FieldError fielderror = result.getFieldErrors().get(result.getErrorCount() - 1);
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setSuccess(false);
        dto.setMessage(fielderror.getDefaultMessage());
        dto.setObj(fielderror.getField());
        return dto;
    }

    /**
     * �ɹ�����
     *
     * @param message
     * @return
     */
    public AjaxReturnMsg success(String message) {
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setSuccess(true);
        dto.setMessage(message);
        return dto;
    }


    /**
     * �ɹ�����
     *
     * @param message
     * @param object
     * @return
     */
    public AjaxReturnMsg success(String message, Object object) {
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setSuccess(true);
        dto.setMessage(message);
        dto.setObj(object);
        return dto;
    }

    /**
     * �ɹ�����
     *
     * @param object
     * @return
     */
    public AjaxReturnMsg success(Object object) {
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setSuccess(true);
        dto.setObj(object);
        return dto;
    }

    /**
     * �ɹ�����
     *
     * @param pageinfo
     * @return
     */
    public AjaxReturnMsg success(PageInfo pageinfo) {
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setSuccess(true);
        dto.setObj(pageinfo);
        dto.setTotal(pageinfo.getTotal());
        return dto;
    }

    public AjaxReturnMsg success(PageInfo pageinfo, String message) {
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setSuccess(true);
        dto.setMessage(message);
        dto.setObj(pageinfo);
        dto.setTotal(pageinfo.getTotal());
        return dto;
    }

    /**
     * �ɹ�����
     *
     * @param pageinfo
     * @return
     */
    public AjaxReturnMsg success2(PageInfo pageinfo) {
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setSuccess(true);
        dto.setObj(pageinfo.getList());
        dto.setTotal(pageinfo.getTotal());
        return dto;
    }

    /**
     * �ɹ�����
     *
     * @param pageinfo
     * @return
     */
    public Map<String, Object> success_hashmap_response(PageInfo pageinfo) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("total", pageinfo.getTotal());
        map.put("rows", pageinfo.getList());
        return map;
    }


    /**
     * ���󷵻�
     *
     * @param message
     * @return
     */
    public AjaxReturnMsg error(String message) {
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setSuccess(false);
        dto.setMessage(message);
        return dto;
    }

    /**
     * ���󷵻�
     *
     * @param message
     * @param obj
     * @return
     */
    public AjaxReturnMsg error(String message, Object obj) {
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setSuccess(false);
        dto.setMessage(message);
        dto.setObj(obj);
        return dto;
    }

    /**
     * ���󷵻�
     *
     * @param messagecode ҵ�������
     * @param message     ҵ�����˵��
     * @return
     */
    public AjaxReturnMsg<String> error(String messagecode, String message) {
        AjaxReturnMsg<String> dto = new AjaxReturnMsg<>();
        dto.setCode(messagecode);
        dto.setSuccess(false);
        dto.setMessage(message);
        return dto;
    }

    /**
     * ���󷵻�
     *
     * @param messagecode ҵ�������
     * @param message     ҵ�����˵��
     * @param object
     * @return
     */
    public AjaxReturnMsg error(String messagecode, String message, Object object) {
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setCode(messagecode);
        dto.setSuccess(false);
        dto.setMessage(message);
        dto.setObj(object);
        return dto;
    }

    /**
     * ���󷵻�
     *
     * @param object
     * @return
     */
    public AjaxReturnMsg error(Object object) {
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setSuccess(false);
        dto.setObj(object);
        return dto;
    }

    /**
     * ���󷵻�
     *
     * @param e
     * @return
     */
    public AjaxReturnMsg error(Exception e) {
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setSuccess(false);
        dto.setMessage(e.getLocalizedMessage());
        return dto;
    }

    /**
     * ��ajax����dto���س��ַ���
     *
     * @param msg
     * @return
     */
    public String AjaxMsgtoString(AjaxReturnMsg msg) {
        return JSONObject.fromObject(msg).toString();
    }

}
