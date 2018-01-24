package com.insigma.mvc.serviceimp.common.init;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.controller.MvcHelper;
import com.insigma.mvc.dao.common.init.ApiInitMapper;
import com.insigma.mvc.model.Aa01;
import com.insigma.mvc.model.CodeType;
import com.insigma.mvc.model.CodeValue;
import com.insigma.mvc.service.common.init.ApiInitService;

/**
 * @author wengsh
 */
@Service
@Transactional
public class ApiInitServiceImpl extends MvcHelper implements ApiInitService {

    @Resource
    private ApiInitMapper apiinitMapper;

    private static Pattern p = Pattern.compile("[\u4e00-\u9fa5]");

    @Override
    public AjaxReturnMsg<List<CodeType>> getInitcodetypeList() {
        return this.success(apiinitMapper.getInitcodetypeList());
    }

    @Override
    public AjaxReturnMsg<List<CodeValue>> getInitCodeValueList(String code_type) {
        return this.success(apiinitMapper.getInitCodeValueList(code_type));
    }

    @Override
    public AjaxReturnMsg<List<CodeValue>> getChildrenByParentId(String code_type, String code_value) {
        if ("AAB301".equals(code_type) && isContainChinese(code_value)) { // 地区为中文，如“杭州市”
            code_value = apiinitMapper.getByParentName(code_value).getCode_value();
        }
        return this.success(apiinitMapper.getChildrenByParentId(code_type, code_value));
    }

    @Override
    public AjaxReturnMsg<List<CodeValue>> getMulticodeValuebyType(String code_type) {
        //第一级代码
        CodeType codetype = apiinitMapper.getCodeTypeInfo(code_type);
        if (codetype.getCode_root_value() == null) {
            return null;
        }
        return this.success(getChildredCodeValueList(code_type, codetype.getCode_root_value()));
    }

    /**
     * 获取aa01表数据
     * @return
     */
    @Override
    public AjaxReturnMsg<List<Aa01>> getAa01List() {
        return this.success(apiinitMapper.getAa01List());
    }

    /**
     * 多级代码递归获取
     *
     * @param code_type
     * @param code_value
     * @return
     */
    private List<CodeValue> getChildredCodeValueList(String code_type, String code_value) {
        List<CodeValue> codevaluelist = apiinitMapper.getChildrenByParentId(code_type, code_value);
        if (codevaluelist != null) {
            for (int i = 0; i < codevaluelist.size(); i++) {
                CodeValue codevalue = codevaluelist.get(i);
                codevalue.setChildren(getChildredCodeValueList(code_type, codevalue.getCode_value()));
                codevaluelist.set(i, codevalue);
            }
        }
        return codevaluelist;
    }

    private static boolean isContainChinese(String str) {
        Matcher matcher = p.matcher(str);
        return matcher.find();
    }
}