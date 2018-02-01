package com.insigma.mvc.controller.demo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.MvcHelper;
import com.insigma.mvc.model.DemoAc01;
import com.insigma.mvc.model.DemoAc01Group;
import com.insigma.mvc.service.demo.ApiDemoService;
import org.springframework.web.multipart.MultipartFile;


/**
 * demo测试程序
 * @author wengsh
 *
 */
@RestController
@Api(description = "测试")
@RequestMapping("/api/demo")
public class ApiDemoController extends MvcHelper<DemoAc01> {
	
	@Resource
	private ApiDemoService apidemoService;
	
	/**
	 * 获取人员列表
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "获取人员列表", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/ac01s",method = RequestMethod.POST)
	public AjaxReturnMsg<HashMap<String, Object>> getAc01List(HttpServletRequest request, DemoAc01 ac01 ) throws Exception {
		return apidemoService.getDemoAc01List(ac01);
	}
	
	
	/**
	 * 根据id删除人员
	 * @param request
	 * @param model
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "根据id删除人员", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/ac01/{id}",method = RequestMethod.DELETE)
	public AjaxReturnMsg<String> deleteDemoDataById(HttpServletRequest request,@PathVariable String id){
		return apidemoService.deleteDemoById(id);
	}
	
	/**
	 * 根据id批量删除人员
	 * @param request
	 * @param model
	 * @param ac01
	 * @return
	 */
	@ApiOperation(value = "根据id批量删除人员", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/ac01/delete/bat",method = RequestMethod.POST)
	public AjaxReturnMsg<String> batDeleteDemodata(HttpServletRequest request,DemoAc01 ac01){
		return apidemoService.batDeleteDemoData(ac01);
	}
	
	

	/**
	 * 根据id获取人员信息
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "根据id获取人员信息", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/ac01/{id}",method = RequestMethod.GET)
	public AjaxReturnMsg<DemoAc01> getDemoById(HttpServletRequest request,@PathVariable String id) throws Exception {
		return this.success(apidemoService.getDemoById(id));
	}
	
	/**
	 * 根据id获取人员信息
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "根据id获取人员信息返回中文", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/ac01/name/{id}",method = RequestMethod.GET)
	public AjaxReturnMsg<DemoAc01> getDemoNameById(HttpServletRequest request,@PathVariable String id) throws Exception {
		return this.success(apidemoService.getDemoNameById(id));
	}
	
	
	
	/**
	 * @param ac01
	 * @return
	 */
	@ApiOperation(value = "新增或更新人员", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value="/ac01/put",method = RequestMethod.POST)
	public AjaxReturnMsg<String> savedata(@Validated({DemoAc01Group.Add.class}) DemoAc01 ac01, BindingResult result) throws Exception {
		//检验输入
		if (result.hasErrors()){
			return validate(result);
		}
		return apidemoService.saveDemoData(ac01);
	}

	/**
	 * 上传文件
	 *
	 * @param file_bus_id
	 * @param file_name
	 * @param multipartFile
	 * @return
	 */
	@ApiOperation(value = "上传文件", notes = "上传文件", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "file_bus_id", value = "基本信息id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "file_name", value = "文件名称，没有后缀", required = true, paramType = "query")
	})
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public AjaxReturnMsg uploadFile(String file_bus_id, String file_name,String userid,
									@RequestParam("uploadFile") MultipartFile multipartFile) {
		return apidemoService.uploadFile(userid, file_bus_id, file_name, multipartFile);
	}
}
