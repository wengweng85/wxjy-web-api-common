package com.insigma.mvc.controller.common.log;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.controller.MvcHelper;
import com.insigma.mvc.model.SEmailLog;
import com.insigma.mvc.model.SSmsLog;
import com.insigma.mvc.service.common.log.ApiLogService;

/**
 * ��־��ҳcontoller
 * Created by liuds on 2017/11/15.
 */
@RestController
@Api(description = "�����ӿ�-��־����")
public class ApiLogController extends MvcHelper {
    
	@Resource
    private ApiLogService apilogService;

    /**
     * ������ŷ�����־
     * @param sSmsLog
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/api/common/saveSSmsLog", method = RequestMethod.POST)
    @ApiOperation(value = "������ŷ�����־", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxReturnMsg saveSSmsLog(SSmsLog sSmsLog) throws Exception {
        return apilogService.saveSSmsLog(sSmsLog);
    }

    /**
     * �����ʼ�������־
     * @param sEmailLog
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/api/common/saveSEmailLog", method = RequestMethod.POST)
    @ApiOperation(value = "�����ʼ�������־", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxReturnMsg saveSEmailLog(SEmailLog sEmailLog) throws Exception {
        return apilogService.saveSEmailLog(sEmailLog);
    }
}
