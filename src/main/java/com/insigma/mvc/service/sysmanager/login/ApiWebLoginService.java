package com.insigma.mvc.service.sysmanager.login;


import java.util.List;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.model.SPermission;
import com.insigma.mvc.model.SRole;
import com.insigma.mvc.model.SUser;
import com.insigma.mvc.model.SysLoginInf;


/**
 * ��¼service�ӿ�
 * @author wengsh
 *
 */
public interface ApiWebLoginService {
	/**
	 * ͨ���û�����ȡ��Ա����Ϣ
	 * @param username
	 * @return
	 * @throws Exception 
	 */
	public AjaxReturnMsg<SUser> getUserAndGroupInfo(String loginname,String password) ;
	
	/**
	 * ͨ���û�id��ȡ�û���ɫ����
	 * @param userid
	 * @return ��ɫ����
	 * @throws Exception 
	 */
	public AjaxReturnMsg<List<SRole>> findRolesStr(String loginname) ;
	
	/**
	 * ͨ���û�id��ȡ�û�Ȩ�޼���
	 * @param userid
	 * @return Ȩ�޼���
	 * @throws Exception 
	 */
	public AjaxReturnMsg<List<SPermission>> findPermissionStr(String loginname) ;
	
	
	public void saveLoginHashInfo(SysLoginInf inf);
	
	
	public List<SysLoginInf> findLoginInfoByhashcode(String loginhash);
	

}
