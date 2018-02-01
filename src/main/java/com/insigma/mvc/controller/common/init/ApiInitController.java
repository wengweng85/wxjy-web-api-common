package com.insigma.mvc.controller.common.init;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.controller.MvcHelper;
import com.insigma.mvc.model.CodeType;
import com.insigma.mvc.model.CodeValue;
import com.insigma.mvc.service.common.init.ApiInitService;
import com.insigma.resolver.AppException;

/**
 * ����������
 * @author 
 *
 */
@RestController
@Api(description = "�����ӿ�-�������������ֵ����")
public class ApiInitController extends MvcHelper {

    @Resource
    private ApiInitService apiinitservice;

    /**
     * ��ȡ��������б�
     * @return
     * @throws AppException
     */
    @ApiOperation(value = "��ȡ��������б�", notes = "��ȡ��������б�", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/codetype/getInitcodetypeList", method = RequestMethod.GET)
    public AjaxReturnMsg<List<CodeType>> getInitcodetypeList() throws AppException {
        return apiinitservice.getInitcodetypeList();
    }


    /**
     * ���ݲ�������ȡ����������б�
     * @param code_type
     * @return
     * @throws AppException
     */
    @ApiOperation(value = "���ݲ�������ȡ����������б�", notes = "���ݲ�������ȡ����������б���������ΪAAC011������ѧ����Ϣ���б�",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code_type", value = "��������ֵ", required = true, paramType = "path")
    })
    @RequestMapping(value = "/codetype/getInitCodeValueList/{code_type}", method = RequestMethod.GET)
    public AjaxReturnMsg<List<CodeValue>> getInitCodeValueList(@PathVariable String code_type) throws AppException {
        return apiinitservice.getInitCodeValueList(code_type);
    }


    /**
     * ���ݸ����ȡ����
     * @param code_type
     * @param code_value
     * @return
     * @throws AppException
     */
    @ApiOperation(value = "���ݸ����ȡ����", notes = "���ݸ����ȡ����",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code_type", value = "��������ֵ", required = true, paramType = "query"),
            @ApiImplicitParam(name = "code_value", value = "����ֵ", required = true, paramType = "query")
    })
    @RequestMapping(value = "/codetype/getChildrenByParentId", method = RequestMethod.GET)
    public AjaxReturnMsg<List<CodeValue>> getChildrenByParentId(String code_type, String code_value) throws Exception {
        code_value = new String(code_value.getBytes("iso-8859-1"), "utf-8");
        return apiinitservice.getChildrenByParentId(code_type, code_value);
    }


    /**
     * ���ݴ������ͻ�ȡ�༶����JSON����
     *
     * @param code_type
     * @return
     * @throws AppException
     */
    @ApiOperation(value = "���ݴ������ͻ�ȡ�༶����JSON��ʽ����", notes = "���ݴ������ͻ�ȡ�༶����JSON��ʽ����",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code_type", value = "�������", required = true, paramType = "path")
    })
    @RequestMapping(value = "/codetype/getMulticodeValuebyType/{code_type}", method = RequestMethod.GET)
    public AjaxReturnMsg<List<CodeValue>> getMulticodeValuebyType(@PathVariable String code_type) throws AppException {
        return apiinitservice.getMulticodeValuebyType(code_type);
    }

    /**
     * ���ݴ������ͻ�ȡ����ֵͬʱ����ͨ��������������
     *
     * @param code_type
     * @return
     * @throws AppException
     */
    @ApiOperation(value = "���ݴ������ͻ�ȡ����ֵͬʱ����ͨ��������������", notes = "���ݴ������ͻ�ȡ����ֵͬʱ����ͨ��������������",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/codetype/getInitCodeValueListByFilter", method = RequestMethod.POST)
    public AjaxReturnMsg<List<CodeValue>> getInitCodeValueListByFilter(CodeType code_type) throws AppException {
        return apiinitservice.getInitCodeValueListByFilter(code_type);
    }
}
