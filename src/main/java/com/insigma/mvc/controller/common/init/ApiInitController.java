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
 * 参数控制器
 * @author 
 *
 */
@RestController
@Api(description = "公共接口-参数管理及数据字典管理")
public class ApiInitController extends MvcHelper {

    @Resource
    private ApiInitService apiinitservice;

    /**
     * 获取参数类别列表
     * @return
     * @throws AppException
     */
    @ApiOperation(value = "获取参数类别列表", notes = "获取参数类别列表", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/codetype/getInitcodetypeList", method = RequestMethod.GET)
    public AjaxReturnMsg<List<CodeType>> getInitcodetypeList() throws AppException {
        return apiinitservice.getInitcodetypeList();
    }


    /**
     * 根据参数类别获取该类参数的列表
     * @param code_type
     * @return
     * @throws AppException
     */
    @ApiOperation(value = "根据参数类别获取该类参数的列表", notes = "根据参数类别获取该类参数的列表，如参数类别为AAC011，返回学历信息的列表",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code_type", value = "参数类别的值", required = true, paramType = "path")
    })
    @RequestMapping(value = "/codetype/getInitCodeValueList/{code_type}", method = RequestMethod.GET)
    public AjaxReturnMsg<List<CodeValue>> getInitCodeValueList(@PathVariable String code_type) throws AppException {
        return apiinitservice.getInitCodeValueList(code_type);
    }


    /**
     * 根据父类获取子类
     * @param code_type
     * @param code_value
     * @return
     * @throws AppException
     */
    @ApiOperation(value = "根据父类获取子类", notes = "根据父类获取子类",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code_type", value = "参数类别的值", required = true, paramType = "query"),
            @ApiImplicitParam(name = "code_value", value = "参数值", required = true, paramType = "query")
    })
    @RequestMapping(value = "/codetype/getChildrenByParentId", method = RequestMethod.GET)
    public AjaxReturnMsg<List<CodeValue>> getChildrenByParentId(String code_type, String code_value) throws Exception {
        code_value = new String(code_value.getBytes("iso-8859-1"), "utf-8");
        return apiinitservice.getChildrenByParentId(code_type, code_value);
    }


    /**
     * 根据代码类型获取多级代码JSON数据
     *
     * @param code_type
     * @return
     * @throws AppException
     */
    @ApiOperation(value = "根据代码类型获取多级代码JSON格式数据", notes = "根据代码类型获取多级代码JSON格式数据",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code_type", value = "参数类别", required = true, paramType = "path")
    })
    @RequestMapping(value = "/codetype/getMulticodeValuebyType/{code_type}", method = RequestMethod.GET)
    public AjaxReturnMsg<List<CodeValue>> getMulticodeValuebyType(@PathVariable String code_type) throws AppException {
        return apiinitservice.getMulticodeValuebyType(code_type);
    }

    /**
     * 根据代码类型获取代码值同时可以通过过滤条件过滤
     *
     * @param code_type
     * @return
     * @throws AppException
     */
    @ApiOperation(value = "根据代码类型获取代码值同时可以通过过滤条件过滤", notes = "根据代码类型获取代码值同时可以通过过滤条件过滤",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/codetype/getInitCodeValueListByFilter", method = RequestMethod.POST)
    public AjaxReturnMsg<List<CodeValue>> getInitCodeValueListByFilter(CodeType code_type) throws AppException {
        return apiinitservice.getInitCodeValueListByFilter(code_type);
    }
}
