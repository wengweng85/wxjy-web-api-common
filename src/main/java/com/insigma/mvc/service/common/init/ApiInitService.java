package com.insigma.mvc.service.common.init;

import java.util.List;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.model.Aa01;
import com.insigma.mvc.model.CodeType;
import com.insigma.mvc.model.CodeValue;


/**
 * Ö÷Ò³service
 *
 * @author wengsh
 */
public interface ApiInitService {

    AjaxReturnMsg<List<CodeType>> getInitcodetypeList();

    AjaxReturnMsg<List<CodeValue>> getInitCodeValueList(String code_type);

    AjaxReturnMsg<List<CodeValue>> getChildrenByParentId(String code_type, String code_value);

    AjaxReturnMsg<List<CodeValue>> getMulticodeValuebyType(String code_type);

    AjaxReturnMsg<List<Aa01>> getAa01List();
    
    AjaxReturnMsg<List<CodeValue>> getInitCodeValueListByFilter(CodeType codetype);
}
