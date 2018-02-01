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
	 * 分页查询
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
	 * 通过id删除demo数据
	 */
	@Override
	@Transactional
	public AjaxReturnMsg<String> deleteDemoById(String aac001) {
		int deletenum=apidemoMapper.deleteByPrimaryKey(aac001);
		if(deletenum==1){
			return this.success("删除成功");
		}else{
			return this.error("删除失败,请确定此人已经被删除了");
		}
	
	}

	
	/**
	 * 批量删除
	 */
	@Override
	@Transactional
	public AjaxReturnMsg<String> batDeleteDemoData(DemoAc01 ac01) {
		String [] ids=ac01.getSelectnodes().split(",");
		int batdeletenum=apidemoMapper.batDeleteData(ids);
		if(batdeletenum==ids.length){
			return this.success("批量删除成功");
		}else{
			return this.success("批量删除成功,但存在失败的数据,请检查");
		}
	}

	/**
	 * 通过个人编号获取信息
	 */
	@Override
	public DemoAc01 getDemoById(String aac001) {
		return apidemoMapper.selectByPrimaryKey(aac001);
	}

	/**
	 * 保存
	 */
	@Override
	@Transactional
	public AjaxReturnMsg<String> saveDemoData(DemoAc01 ac01) {
		//判断身份证号码是否重复
		int aac002num=apidemoMapper.selectByAac002(ac01);
		if(aac002num>0){
			return this.error("此身份证号码"+ac01.getAac002()+"已经存在，不能重复,请输入正确的号码");
		}
				
		//判断是否是更新
		if(StringUtils.isEmpty(ac01.getAac001())){
			int insertnum= apidemoMapper.insertSelective (ac01);
			if(insertnum==1){
				return this.success("新增成功");
			}else{
				return this.error("更新失败,请确认此人已经被删除");
			}
		}else{
			int updatenum=apidemoMapper.updateByPrimaryKey(ac01);
			//更新个人附件文件信息
			Map<String,Object> map =new HashMap<String,Object>();
			map.put("file_bus_id", ac01.getAac001());
			if(ac01.getSelectnodes()!=null){
				map.put("bus_uuids",ac01.getSelectnodes().split(","));
				fileLoadService.batupdateBusIdByBusUuidArray(map);
			}
			if(updatenum==1){
				return this.success("更新成功");
			}else{
				return this.error("更新失败,请确认此人已经被删除");
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
	 * 通过业务id和文件id更新
	 */
	@Override
	public AjaxReturnMsg<String> updateDemoAc01DemBusUuid(DemoAc01 ac01) {
		int updatenum= apidemoMapper.updateDemoAc01DemBusUuid(ac01);
		if(updatenum==1){
			return this.success("更新成功");
		}else{
			return this.error("更新失败");
		}
	}

	
	/**
	 * 通过业务id及文件id上传文件记录
	 */
	@Override
	public AjaxReturnMsg<String> deletefile(DemoAc01 ac01) {
		fileLoadService.deleteFileByBusUuid(ac01.getBus_uuid());		
		ac01.setBus_uuid("");
		int updatenum=apidemoMapper.updateDemoAc01DemBusUuid(ac01);
		if(updatenum==1){
			return this.success("删除成功");
		}else{
			return this.error("删除失败");
		}
	}

	/**
	 * 上传文件
	 *
	 * @param file_bus_id
	 * @param file_name
	 * @return
	 */
	@Override
	public AjaxReturnMsg uploadFile(String userid, String file_bus_id, String file_name, MultipartFile multipartFile) {
		try {
			//保存图片到文件服务器，同时保存图片记录
			SFileRecord sFileRecord = fileLoadService.uploadFile(multipartFile, Param.DEMO_IMG, file_name,
					file_bus_id);
			return this.success(sFileRecord);
		} catch (Exception e) {
			return this.error(e.getMessage());
		}
	}

}
