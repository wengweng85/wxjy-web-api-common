package com.insigma.mvc.serviceimp.sysmanager.login;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.insigma.common.jwt.JWT;
import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.MvcHelper;
import com.insigma.mvc.dao.sysmanager.login.ApiWebLoginMapper;
import com.insigma.mvc.model.SPermission;
import com.insigma.mvc.model.SRole;
import com.insigma.mvc.model.SUser;
import com.insigma.mvc.model.SysLoginInf;
import com.insigma.mvc.service.sysmanager.login.ApiWebLoginService;


/**
 * 登录service接口
 * @author wengsh
 *
 */

@Service
public class ApiWebLoginServiceImpl  extends MvcHelper implements ApiWebLoginService {

	private Log log=LogFactory.getLog(ApiWebLoginServiceImpl.class);
	//登录dao
	@Resource
	private ApiWebLoginMapper apiwebloginmapper;
	
	@Override
	public AjaxReturnMsg<SUser> getUserAndGroupInfo(String loginname,String password)  {
		SUser suser=apiwebloginmapper.getUserAndGroupInfo(loginname, password);
		if(suser!=null){
			//给用户jwt加密生成token
	        String token = JWT.sign(suser);
	        log.info("jwt token=" + token);
	        suser.setToken(token);
			return this.success(suser);
		}else{
			return this.error("用户名或密码错误");
		}
	}

	@Override
	public AjaxReturnMsg<List<SRole>> findRolesStr(String loginname)  {
		List<SRole> list= apiwebloginmapper.findRolesStr(loginname);
		return this.success(list);
	}

	@Override
	public AjaxReturnMsg<List<SPermission>>  findPermissionStr(String loginname)  {
		List<SPermission> list=apiwebloginmapper.findPermissionStr(loginname);
		return this.success(list);
	}

	@Override
	public void saveLoginHashInfo(SysLoginInf inf) {
		apiwebloginmapper.saveLoginHashInfo(inf);
	}

	@Override
	public List<SysLoginInf> findLoginInfoByhashcode(String loginhash) {
		return apiwebloginmapper.findLoginInfoByhashcode(loginhash);
	}


}
