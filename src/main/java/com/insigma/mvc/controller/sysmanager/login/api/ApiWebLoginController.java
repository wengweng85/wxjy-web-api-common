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
 * ��¼controller
 * @author Administrator
 *
 */
@RestController
@Api(description = "�����ӿ�-��¼����")
public class ApiWebLoginController extends MvcHelper {
	
	Log log=LogFactory.getLog(ApiWebLoginController.class);
	

	@Resource
	private ApiWebLoginService webloginservice; 
	
	/**
	 * ��ȡ��¼��Ա��������Ϣ
	 * getUserAndGroupInfo
	 * @param request
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getUserAndGroupInfo/{loginname}/{password}",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	@ApiOperation(value = "��ȡ��¼��Ա��������Ϣ", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxReturnMsg<SUser> getUserAndGroupInfo(HttpServletRequest request,@PathVariable String loginname,@PathVariable String password) throws Exception {
		//ת����json���󲢵���Զ�̽ӿ�
		return webloginservice.getUserAndGroupInfo(loginname,password);
	}
	
	/**
	 * ��ȡ��¼��ԱȨ���б�
	 * findPermissionStr
	 * @param request
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "��ȡ��¼��ԱȨ���б�", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/api/findPermissionStr/{username}",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	public AjaxReturnMsg<List<SPermission>> findPermissionStr(HttpServletRequest request,@PathVariable String username) throws Exception {
		return webloginservice.findPermissionStr(username);
	}
	
	/**
	 * ��ȡ��¼��Ա��ɫ�б�
	 * @param request
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/findRolesStr/{username}",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	@ApiOperation(value = "��ȡ��¼��Ա��ɫ�б�", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxReturnMsg<List<SRole>> findRolesStr(HttpServletRequest request,@PathVariable String username) throws Exception {
		return webloginservice.findRolesStr(username);
	}
}
