package com.insigma.mvc.service.sysmanager.login;


import java.util.List;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.model.SPermission;
import com.insigma.mvc.model.SRole;
import com.insigma.mvc.model.SUser;
import com.insigma.mvc.model.SysLoginInf;


/**
 * 登录service接口
 * @author wengsh
 *
 */
public interface ApiWebLoginService {
	/**
	 * 通过用户名获取会员表信息
	 * @param username
	 * @return
	 * @throws Exception 
	 */
	public AjaxReturnMsg<SUser> getUserAndGroupInfo(String loginname,String password) ;
	
	/**
	 * 通过用户id获取用户角色集合
	 * @param userid
	 * @return 角色集合
	 * @throws Exception 
	 */
	public AjaxReturnMsg<List<SRole>> findRolesStr(String loginname) ;
	
	/**
	 * 通过用户id获取用户权限集合
	 * @param userid
	 * @return 权限集合
	 * @throws Exception 
	 */
	public AjaxReturnMsg<List<SPermission>> findPermissionStr(String loginname) ;
	
	
	public void saveLoginHashInfo(SysLoginInf inf);
	
	
	public List<SysLoginInf> findLoginInfoByhashcode(String loginhash);
	

}
