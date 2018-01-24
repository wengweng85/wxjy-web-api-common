package com.insigma.mvc.dao.sysmanager.login;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.insigma.mvc.model.SGroup;
import com.insigma.mvc.model.SPermission;
import com.insigma.mvc.model.SRole;
import com.insigma.mvc.model.SUser;
import com.insigma.mvc.model.SysLoginInf;


/**
 * 登录service接口
 * @author wengsh
 *
 */
public interface ApiWebLoginMapper {
	/**
	 * 通过用户名获取会员表信息
	 * @param username
	 * @return 用户表
	 * @ 
	 */
	public SUser getUserAndGroupInfo(@Param("loginname") String loginname,@Param("password")  String password)  ;
	
	
	/**
	 * 获取当前机构的行政区划信息
	 * @param groupid
	 * @return
	 * @
	 */
	public List<SGroup> getGroupAreaInfo(String groupid)   ;
	
	
	/**
	 * 通过用户id获取用户角色集合
	 * @param userid
	 * @return 角色集合
	 * @ 
	 */
	public List<SRole> findRolesStr(String loginname) ;
	
	
	/**
	 * 通过用户id获取用户权限集合
	 * @param userid
	 * @return 权限集合
	 * @ 
	 */
	public List<SPermission> findPermissionStr(String loginname) ;
	
	
	/**
	 * 保存hashinfo
	 * @param hashinfo
	 */
	public void saveLoginHashInfo(SysLoginInf inf);
	
	
	public List<SysLoginInf> findLoginInfoByhashcode(String loginhash);

}
