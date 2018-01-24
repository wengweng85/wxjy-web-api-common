package com.insigma.mvc.controller;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.github.pagehelper.PageInfo;
import com.insigma.dto.AjaxReturnMsg;


/**
 * mvc帮助类，主要用于包装controller以及serviceimp层返回的数据
 * 将其包装成类返回成json格式
 *
 * @author wengsh
 */
public class MvcHelper {

    public AjaxReturnMsg validate(BindingResult result) {
	    // 字段校验，没有顺序
        FieldError fielderror = result.getFieldErrors().get(result.getErrorCount() - 1);
        AjaxReturnMsg dto = new AjaxReturnMsg();
        dto.setSuccess(false);
        dto.setMessage(fielderror.getDefaultMessage());
        dto.setObj(fielderror.getField());
        return dto;
    }

    /**
     * 成功返回
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
     * 成功返回
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
     * 成功返回
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
     * 成功返回
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
     * 成功返回
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
     * 成功返回
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
     * 错误返回
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
     * 错误返回
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
     * 错误返回
     *
     * @param messagecode 业务错误码
     * @param message     业务错误说明
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
     * 错误返回
     *
     * @param messagecode 业务错误码
     * @param message     业务错误说明
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
     * 错误返回
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
     * 错误返回
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
     * 将ajax返回dto返回成字符串
     *
     * @param msg
     * @return
     */
    public String AjaxMsgtoString(AjaxReturnMsg msg) {
        return JSONObject.fromObject(msg).toString();
    }

}
