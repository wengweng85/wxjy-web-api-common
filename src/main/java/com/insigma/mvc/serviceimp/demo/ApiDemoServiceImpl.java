package com.insigma.mvc.serviceimp.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.insigma.common.listener.AppConfig;
import com.insigma.common.util.StringUtil;
import com.insigma.dto.OpType;
import com.insigma.mvc.model.Param;
import com.insigma.mvc.model.SFileRecord;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.MvcHelper;
import com.insigma.mvc.dao.demo.ApiDemoMapper;
import com.insigma.mvc.model.DemoAc01;
import com.insigma.mvc.service.common.fileupload.ApiFileUploadService;
import com.insigma.mvc.service.demo.ApiDemoService;
import org.springframework.web.multipart.MultipartFile;


/**
 * demo ac01 service impl
 * @author wengsh
 *
 */

@Service
public class ApiDemoServiceImpl extends MvcHelper implements ApiDemoService {

	@Resource
	private ApiDemoMapper apidemoMapper;
	
	@Resource
	private ApiFileUploadService fileLoadService;
	
	/**
	 * ��ҳ��ѯ
	 */
	
	@Override
	public AjaxReturnMsg<HashMap<String,Object>> getDemoAc01List(DemoAc01 ac01) {
		PageHelper.offsetPage(ac01.getOffset(), ac01.getLimit());
		if(StringUtils.isNotEmpty(ac01.getAac011())){
			ac01.setA_aac011(ac01.getAac011().split(","));
		}
		List<DemoAc01> list=apidemoMapper.getDemoAc01List(ac01);
		PageInfo<DemoAc01> pageinfo = new PageInfo<DemoAc01>(list);
		return this.success_hashmap_response_rc_fm(pageinfo);
	}

	/**
	 * ͨ��idɾ��demo����
	 */
	@Override
	@Transactional
	public AjaxReturnMsg<String> deleteDemoById(String aac001) {
		int deletenum=apidemoMapper.deleteByPrimaryKey(aac001);
		if(deletenum==1){
			return this.success("ɾ���ɹ�");
		}else{
			return this.error("ɾ��ʧ��,��ȷ�������Ѿ���ɾ����");
		}
	
	}

	
	/**
	 * ����ɾ��
	 */
	@Override
	@Transactional
	public AjaxReturnMsg<String> batDeleteDemoData(DemoAc01 ac01) {
		String [] ids=ac01.getSelectnodes().split(",");
		int batdeletenum=apidemoMapper.batDeleteData(ids);
		if(batdeletenum==ids.length){
			return this.success("����ɾ���ɹ�");
		}else{
			return this.success("����ɾ���ɹ�,������ʧ�ܵ�����,����");
		}
	}

	/**
	 * ͨ�����˱�Ż�ȡ��Ϣ
	 */
	@Override
	public DemoAc01 getDemoById(String aac001) {
		return apidemoMapper.selectByPrimaryKey(aac001);
	}

	/**
	 * ����
	 */
	@Override
	@Transactional
	public AjaxReturnMsg<String> saveDemoData(DemoAc01 ac01) {
		//�ж����֤�����Ƿ��ظ�
		int aac002num=apidemoMapper.selectByAac002(ac01);
		if(aac002num>0){
			return this.error("�����֤����"+ac01.getAac002()+"�Ѿ����ڣ������ظ�,��������ȷ�ĺ���");
		}
				
		//�ж��Ƿ��Ǹ���
		if(StringUtils.isEmpty(ac01.getAac001())){
			int insertnum= apidemoMapper.insertSelective (ac01);
			if(insertnum==1){
				return this.success("�����ɹ�");
			}else{
				return this.error("����ʧ��,��ȷ�ϴ����Ѿ���ɾ��");
			}
		}else{
			int updatenum=apidemoMapper.updateByPrimaryKey(ac01);
			//���¸��˸����ļ���Ϣ
			Map<String,Object> map =new HashMap<String,Object>();
			map.put("file_bus_id", ac01.getAac001());
			if(ac01.getSelectnodes()!=null){
				map.put("bus_uuids",ac01.getSelectnodes().split(","));
				fileLoadService.batupdateBusIdByBusUuidArray(map);
			}
			if(updatenum==1){
				return this.success("���³ɹ�");
			}else{
				return this.error("����ʧ��,��ȷ�ϴ����Ѿ���ɾ��");
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public DemoAc01 getDemoNameById(String aac001) {
		return apidemoMapper.selectNameByPrimaryKey(aac001);
	}

	
	/**
	 * ͨ��ҵ��id���ļ�id����
	 */
	@Override
	public AjaxReturnMsg<String> updateDemoAc01DemBusUuid(DemoAc01 ac01) {
		int updatenum= apidemoMapper.updateDemoAc01DemBusUuid(ac01);
		if(updatenum==1){
			return this.success("���³ɹ�");
		}else{
			return this.error("����ʧ��");
		}
	}

	
	/**
	 * ͨ��ҵ��id���ļ�id�ϴ��ļ���¼
	 */
	@Override
	public AjaxReturnMsg<String> deletefile(DemoAc01 ac01) {
		fileLoadService.deleteFileByBusUuid(ac01.getBus_uuid());		
		ac01.setBus_uuid("");
		int updatenum=apidemoMapper.updateDemoAc01DemBusUuid(ac01);
		if(updatenum==1){
			return this.success("ɾ���ɹ�");
		}else{
			return this.error("ɾ��ʧ��");
		}
	}

	/**
	 * �ϴ��ļ�
	 *
	 * @param file_bus_id
	 * @param file_name
	 * @return
	 */
	@Override
	public AjaxReturnMsg uploadFile(String userid, String file_bus_id, String file_name, MultipartFile multipartFile) {
		try {
			//����ͼƬ���ļ���������ͬʱ����ͼƬ��¼
			SFileRecord sFileRecord = fileLoadService.uploadFile(multipartFile, Param.DEMO_IMG, file_name,
					file_bus_id);
			return this.success(sFileRecord);
		} catch (Exception e) {
			return this.error(e.getMessage());
		}
	}

}
