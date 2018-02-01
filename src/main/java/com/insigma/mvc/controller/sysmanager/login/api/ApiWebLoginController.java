package com.insigma.mvc.controller.sysmanager.login.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.MvcHelper;
import com.insigma.mvc.model.SPermission;
import com.insigma.mvc.model.SRole;
import com.insigma.mvc.model.SUser;
import com.insigma.mvc.service.sysmanager.login.ApiWebLoginService;


/**
 * 登录controller
 * @author Administrator
 *
 */
@RestController
@Api(description = "公共接口-登录管理")
public class ApiWebLoginController extends MvcHelper {
	
	Log log=LogFactory.getLog(ApiWebLoginController.class);
	

	@Resource
	private ApiWebLoginService webloginservice; 
	
	/**
	 * 获取登录人员及机构信息
	 * getUserAndGroupInfo
	 * @param request
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getUserAndGroupInfo/{loginname}/{password}",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	@ApiOperation(value = "获取登录人员及机构信息", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxReturnMsg<SUser> getUserAndGroupInfo(HttpServletRequest request,@PathVariable String loginname,@PathVariable String password) throws Exception {
		//转换成json对象并调用远程接口
		return webloginservice.getUserAndGroupInfo(loginname,password);
	}
	
	/**
	 * 获取登录人员权限列表
	 * findPermissionStr
	 * @param request
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "获取登录人员权限列表", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/api/findPermissionStr/{username}",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	public AjaxReturnMsg<List<SPermission>> findPermissionStr(HttpServletRequest request,@PathVariable String username) throws Exception {
		return webloginservice.findPermissionStr(username);
	}
	
	/**
	 * 获取登录人员角色列表
	 * @param request
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/findRolesStr/{username}",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	@ApiOperation(value = "获取登录人员角色列表", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxReturnMsg<List<SRole>> findRolesStr(HttpServletRequest request,@PathVariable String username) throws Exception {
		return webloginservice.findRolesStr(username);
	}
}
