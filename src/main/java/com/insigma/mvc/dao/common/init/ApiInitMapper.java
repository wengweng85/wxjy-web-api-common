package com.insigma.mvc.dao.common.init;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.insigma.mvc.model.Aa01;
import com.insigma.mvc.model.CodeType;
import com.insigma.mvc.model.CodeValue;


/**
 * @author wengsh
 */
public interface ApiInitMapper {

	CodeType  getCodeTypeInfo(String code_type);
	
    List<CodeType> getInitcodetypeList();

    List<CodeValue> getInitCodeValueList(String code_type);

    CodeValue getByParentName(String code_name);

    List<CodeValue> getChildrenByParentId(@Param("code_type") String code_type,  @Param("code_value") String code_value);

    List<Aa01> getAa01List();
}
