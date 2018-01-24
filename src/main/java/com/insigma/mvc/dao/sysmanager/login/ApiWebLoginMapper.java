package com.insigma.mvc.dao.sysmanager.login;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.insigma.mvc.model.SGroup;
import com.insigma.mvc.model.SPermission;
import com.insigma.mvc.model.SRole;
import com.insigma.mvc.model.SUser;
import com.insigma.mvc.model.SysLoginInf;


/**
 * ��¼service�ӿ�
 * @author wengsh
 *
 */
public interface ApiWebLoginMapper {
	/**
	 * ͨ���û�����ȡ��Ա����Ϣ
	 * @param username
	 * @return �û���
	 * @ 
	 */
	public SUser getUserAndGroupInfo(@Param("loginname") String loginname,@Param("password")  String password)  ;
	
	
	/**
	 * ��ȡ��ǰ����������������Ϣ
	 * @param groupid
	 * @return
	 * @
	 */
	public List<SGroup> getGroupAreaInfo(String groupid)   ;
	
	
	/**
	 * ͨ���û�id��ȡ�û���ɫ����
	 * @param userid
	 * @return ��ɫ����
	 * @ 
	 */
	public List<SRole> findRolesStr(String loginname) ;
	
	
	/**
	 * ͨ���û�id��ȡ�û�Ȩ�޼���
	 * @param userid
	 * @return Ȩ�޼���
	 * @ 
	 */
	public List<SPermission> findPermissionStr(String loginname) ;
	
	
	/**
	 * ����hashinfo
	 * @param hashinfo
	 */
	public void saveLoginHashInfo(SysLoginInf inf);
	
	
	public List<SysLoginInf> findLoginInfoByhashcode(String loginhash);

}
