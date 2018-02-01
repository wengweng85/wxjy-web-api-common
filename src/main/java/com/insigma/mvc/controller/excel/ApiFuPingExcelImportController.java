package com.insigma.mvc.controller.excel;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.MvcHelper;
import com.insigma.mvc.model.Ac60ExcelTemp;
import com.insigma.mvc.model.SysExcelBatch;
import com.insigma.mvc.service.common.excel.ApiFuPingExcelImportService;
import com.insigma.mvc.service.common.fileupload.ApiFileUploadService;

/**
 * 贫困人员信息管理-导入
 * @author pangyuying
 *
 */
@RestController
@Api(description = "excel文件上传测试")
@RequestMapping("/api")
public class ApiFuPingExcelImportController extends MvcHelper{
	
	@Resource
	private ApiFuPingExcelImportService fupingexcelImportService;
	
	@Resource
	private ApiFileUploadService apifileLoadService;
	

	/**
	 * 列表查询
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "获取excel上传文件列表", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/excel/batchs",method = RequestMethod.POST)
	public AjaxReturnMsg<HashMap<String,Object>> getList(HttpServletRequest request,Model model, SysExcelBatch sExcelBatch ) throws Exception {
		return fupingexcelImportService.getList(sExcelBatch);
	}
	
	
	/**
	 * 列表查询 导入数据总体情况 
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "导入数据总体情况 ", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/excel/total",method = RequestMethod.POST)
	public AjaxReturnMsg<HashMap<String,Object>> queryPovertyDataTotalByexcelBatchNumber(HttpServletRequest request,Model model, Ac60ExcelTemp ac60ExcelTemp ) throws Exception {
		return fupingexcelImportService.queryPovertyDataTotalByexcelBatchNumber(ac60ExcelTemp);
	}
	
	/**
	 * 列表查询 导入数据明细情况 
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "导入数据明细情况  ", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/excel/detail",method = RequestMethod.POST)
	public AjaxReturnMsg<HashMap<String,Object>> getPovertyImprtDataList(HttpServletRequest request,Model model, Ac60ExcelTemp ac60ExcelTemp ) throws Exception {
		return fupingexcelImportService.getPovertyImprtDataList(ac60ExcelTemp);
	}
	
	/**
	 * 删除临时表数据
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "删除临时表数据  ", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/excel/{excel_batch_number}",method = RequestMethod.DELETE)
	public AjaxReturnMsg<String> deleteTempDataByNumber(HttpServletRequest request ,@PathVariable String excel_batch_number) throws Exception {
		return fupingexcelImportService.deleteTempDataByNumber(excel_batch_number);
	}
   
}
