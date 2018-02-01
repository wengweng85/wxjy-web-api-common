package com.insigma.mvc.serviceimp.common.excel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insigma.common.excel.ExcelExport;
import com.insigma.common.util.JvmMemoryUtil;
import com.insigma.common.util.UUIDGenerator;
import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.MvcHelper;
import com.insigma.mvc.dao.common.excel.ApiFuPingExcelImportMapper;
import com.insigma.mvc.dao.common.fileupload.ApiFileUploadMapper;
import com.insigma.mvc.model.Ac60ExcelTemp;
import com.insigma.mvc.model.ExcelExportModel;
import com.insigma.mvc.model.SysExcelBatch;
import com.insigma.mvc.service.common.excel.ApiFuPingExcelImportService;
import com.insigma.mvc.service.common.excel.ExcelVS;
import com.insigma.resolver.AppException;

/**
 * 扶贫数据导入导出
 * 
 * @author wengsh
 *
 */

@Service(value="fuPingExcelImportService")
public class ApiFuPingExcelImportServiceImpl extends MvcHelper implements ApiFuPingExcelImportService,ExcelVS {

	private static Log log=LogFactory.getLog(ApiFuPingExcelImportServiceImpl.class);
	
	@Resource
	private ApiFileUploadMapper apiFileUploadMapper;

	 @Autowired 
	private SqlSessionTemplate sqlSessionTemplate;
	 
	 @Resource
	private ApiFuPingExcelImportMapper excelImportMapper;
	 
	 @Resource(name = "taskExecutor")  
	private TaskExecutor taskExecutor; 

	// excel标准列名
	private static String[] STANDARD_TITLES = new String[] {"","姓名*,20,String","身份证号*,18,String" };
	//[序号, 姓名, 身份证号码, 户口所在地, 性别, 年龄, 民族, 政治面貌, 文化程度, 健康状况, 劳动技能, 户主姓名, 主要致贫原因, 联系电话, 是否是在校生, 就业情况, null, null, null, null, null, null, null, 公益性岗位或公益专岗安置情况, null, null, null, null, 培训情况, null, null, null, null, null, null, null, 创业情况, null, null, null, null, null, null, null, null, null, 未就业情况, null, null, null, 提供就业服务情况, null, null, null, null, 政策落实情况, null, null, null, null]
	/**
	 * 分页面查询
	 */
	@Override
	public AjaxReturnMsg<HashMap<String,Object>> getList(SysExcelBatch sExcelBatch) {
		PageHelper.offsetPage(sExcelBatch.getOffset(), sExcelBatch.getLimit());
		List<SysExcelBatch> list = apiFileUploadMapper.getExcelBatchList(sExcelBatch);
		PageInfo<SysExcelBatch> pageinfo = new PageInfo<SysExcelBatch>(list);
		return this.success_hashmap_response_rc_fm(pageinfo);
	}

	/**
	 * 数据处理 增加到临时表
	 */
	@Override
	public void executeListToTemp(List<String[]> list, SysExcelBatch sexcelbatch) throws AppException {
		JvmMemoryUtil.showJvmMemory();
		/**2 将数据导入到临时表中*/
		if(list.size()>1){
			// 根据第一列是否与标准格式匹配，判断导入的excel格式是否正确
			String[] cells = list.get(0);
			// 是否匹配
			boolean eq = true;
			// excel列循环
			for (int j = 0; j < cells.length; j++) {
				if (!cells[j].equals(STANDARD_TITLES[j+1].split(",")[0])) {
					eq = false;
					break;
				}
			}
			// 如果不匹配
			if (!eq) {
				//throw new AppException("所用的excel导入模板格式不对,不能导入,请确认格式是否正确");
			}
		}else{
			sexcelbatch.setExcel_batch_status("4");//发生异常
			sexcelbatch.setExcel_batch_rt_msg("所用的excel没有业务数据,请确认格式是否正确");
			apiFileUploadMapper.updateExelBatch(sexcelbatch);
			throw new AppException("所用的excel没有业务数据,请确认格式是否正确");
		}
		// 从第二行开始取数据

		// 新获取一个模式为BATCH，自动提交为false的session
		// 如果自动提交设置为true,将无法控制提交的条数，改为最后统一提交，可能导致内存溢出
		SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
		// 通过新的session获取mapper
		ApiFuPingExcelImportMapper povertyMapper = session.getMapper(ApiFuPingExcelImportMapper.class);
		try {
			// excel行循环
			for (int i = 1; i < list.size(); i++) {
				String[] tempstr = list.get(i);
				Ac60ExcelTemp ac60temp = new Ac60ExcelTemp();
				ac60temp.setExcel_batch_number(sexcelbatch.getExcel_batch_number());//导入临时表之导入批次号
				ac60temp.setExcel_rownum(new Long(i));
				ac60temp.setExcel_temp_id(UUIDGenerator.generate());
				//基本信息 14
				ac60temp.setAac003(tempstr[1]); // 导入临时表之姓名
				if(null!=tempstr[2]){
				     ac60temp.setAac002(tempstr[2].replace("'", "")); // 导入临时表之身份证号
				}
				ac60temp.setAac010(tempstr[3]);	// 导入临时表之劳动者户口所在地
				ac60temp.setAac004(tempstr[4]);	//	导入临时表之性别
				ac60temp.setAac007(tempstr[5]);	//	导入临时表之年龄
				ac60temp.setAac005(tempstr[6]);	//	导入临时表之民族
				ac60temp.setAac024(tempstr[7]);	//	导入临时表之政治面貌
				ac60temp.setAac011(tempstr[8]);	//	导入临时表之学历代码
				ac60temp.setAac033(tempstr[9]);	//	导入临时表之健康状况
				ac60temp.setAae015(tempstr[10]);	//	导入临时表之劳动技能
				ac60temp.setAac029(tempstr[11]);	//	导入临时表之户主姓名
				ac60temp.setAac031(tempstr[12]);	//	导入临时表之主要致贫原因
				if(null!=tempstr[13]){
					ac60temp.setAae006(tempstr[13].replace("'", ""));	//	导入临时表之联系电话
				}
				ac60temp.setAac030(tempstr[14]);	//	导入临时表之是否在校生
				//就业情况 8
				ac60temp.setAdc001(tempstr[15]);	//	导入临时表之就业新式
				ac60temp.setAdc002(tempstr[16]);	//	导入临时表之就业地域
				ac60temp.setAdc003(tempstr[17]);	//	导入临时表之就业地
				ac60temp.setAdc004(tempstr[18]);	//	导入临时表之就业工种
				ac60temp.setAdc005(tempstr[19]);	//	导入临时表之就业时间
				ac60temp.setAdc006(tempstr[20]);	//	导入临时表之是否签订劳动合同或协议
				ac60temp.setAdc007(tempstr[21]);	//	导入临时表之是否参加社会保险
				ac60temp.setAdc008(tempstr[22]);	//	导入临时表之月均工资
				//公益性岗位或公益专岗安置情况 5
				ac60temp.setAdc009(tempstr[23]);	//	导入临时表之岗位类型
				ac60temp.setAdc010(tempstr[24]);	//	导入临时表之公益性岗位安置地址
				ac60temp.setAdc011(tempstr[25]);	//	导入临时表之公益性岗位安置单位名称
				ac60temp.setAdc012(tempstr[26]);	//	导入临时表之安置时间
				ac60temp.setAdc013(tempstr[27]);	//	导入临时表之岗位名称
				//培训情况 7
				ac60temp.setAdc014(tempstr[28]);	//	导入临时表之是否参加就业培训
				ac60temp.setAdc015(tempstr[29]);	//	导入临时表之培训类型
				ac60temp.setAdc016(tempstr[30]);   //	导入临时表之培训开始时间
				ac60temp.setAdc017(tempstr[31]);	//	导入临时表之培训截止时间
				ac60temp.setAdc018(tempstr[32]);	//	导入临时表之培训类别
				ac60temp.setAdc019(tempstr[33]);   //	导入临时表之取得证书
				ac60temp.setAdc020(tempstr[34]);	//	导入临时表之培训补贴（元）
				//创业情况 11
				ac60temp.setAdc021(tempstr[35]);	//	导入临时表之是否就业
				ac60temp.setAdc022(tempstr[36]);	//	导入临时表之创业时间
				ac60temp.setAdc023(tempstr[37]);	//	导入临时表之是否参加创业培训
				ac60temp.setAdc024(tempstr[38]);	//	导入临时表之创业类型
				ac60temp.setAdc025(tempstr[39]);	//	导入临时表之企业或实体地址
				ac60temp.setAdc026(tempstr[40]);	//	导入临时表之企业或实体名称
				ac60temp.setAdc027(tempstr[41]);	//	导入临时表之产业类别
				ac60temp.setAdc028(tempstr[42]);	//	导入临时表之是否享受小额担保贷款
				ac60temp.setAdc029(tempstr[43]);	//	导入临时表之贷款金额（万元）
				ac60temp.setAdc030(tempstr[44]);	//	导入临时表之吸纳劳动力人数
				ac60temp.setAdc031(tempstr[45]);	//	导入临时表之是否在工商部门注册登记
				//未就业情况 4
				ac60temp.setAdc032(tempstr[46]);	//	导入临时表之是否有就业愿望
				ac60temp.setAdc033(tempstr[47]);	//	导入临时表之就业意向地
				ac60temp.setAdc034(tempstr[48]);	//	导入临时表之期望月薪（元）
				ac60temp.setAdc035(tempstr[49]);	//	导入临时表之就业服务需求
				
				//提供就业服务情况 5
				ac60temp.setAdc036(tempstr[50]);	//	导入临时表之提供就业政策咨询次数(次)
				ac60temp.setAdc037(tempstr[51]);	//	导入临时表之提供就业信息次数（次）
				ac60temp.setAdc038(tempstr[52]);	//	导入临时表之提供职业指导与介绍次数（次）
				ac60temp.setAdc039(tempstr[53]);	//	导入临时表之提供培训次数（次）
				ac60temp.setAdc040(tempstr[54]);	//	导入临时表之提供创业服务次数(次)
				//政策落实情况 4
				ac60temp.setAdc041(tempstr[55]);	//	导入临时表之是否享受职业介绍补贴
				ac60temp.setAdc042(tempstr[56]);	//	导入临时表之是否享受社会保险补贴
				ac60temp.setAdc043(tempstr[57]);	//	导入临时表之是否享受岗位补贴
				ac60temp.setAdc044(tempstr[58]);	//	导入临时表之是否享受其他政策扶持
				
				povertyMapper.insertExcelTempData(ac60temp);
				if (i % 5000 == 0 || i == list.size() - 1) {
					// 手动每1000个一提交，提交后无法回滚
					session.commit();
					// 清理缓存，防止溢出
					session.clearCache();
					JvmMemoryUtil.showJvmMemory();
					//更新当前导入数据状态 10%+20%的百份比 导入临时表给30%的分段比例
					int statusnumber=new Float((i+0f)/(list.size()+0f)*20).intValue()+10;
					sexcelbatch.setExcel_batch_data_status(statusnumber);
					apiFileUploadMapper.updateExelBatch(sexcelbatch);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 没有提交的数据可以回滚
			session.rollback();
		} finally {
			session.close();
		}
		//行总数
		sexcelbatch.setExcel_batch_total_count(new Long(list.size()));
		sexcelbatch.setExcel_batch_status("2");//解析临时表
		//更新文件记录
		apiFileUploadMapper.updateExelBatch(sexcelbatch);
		
	}
	/**
	 * 执行数据处理过程
	 * @param executeProc
	 */
	@Override
	public void executeProc(final SysExcelBatch  sexcelbatch) throws  AppException{
		/**3 调用过程处理数据*/
		//获取批次号
		final SysExcelBatch newsexcelbatch=apiFileUploadMapper.getExcelBatchById(sexcelbatch.getExcel_batch_id());
		//开启线程执行
		taskExecutor.execute(new Runnable() {  
		    @Override  
		    public void run() {  
		        // TODO Auto-generated method stub  
		        try {  
		    		    SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(false);
		    		   // 通过新的session获取mapper
		    		   ApiFuPingExcelImportMapper povertyMapper = session.getMapper(ApiFuPingExcelImportMapper.class);
		        	    Date start=new Date();
						log.info("调用过程开始时间"+new Date().toLocaleString());
						//调用过程处理数据
						povertyMapper.executePovertyData(newsexcelbatch);
						Date end=new Date();
						Long cost=end.getTime()-start.getTime();
						log.info("调用过程结束时间"+new Date().toLocaleString()+"花费"+cost/1000+"s");
						//执行是否成功
						if(newsexcelbatch.getExcel_batch_rt_code() .equals("1")){
							
						}else{
							log.info("导入数据失败,失败原因"+newsexcelbatch.getExcel_batch_rt_msg());
						}
		        } catch (Exception e) {  
		            // TODO Auto-generated catch block  
		            e.printStackTrace();  
		        }  
		    }  
		});  
	}

	/**
	 * 通用批次号查询导入数据
	 */
	@Override
	public  AjaxReturnMsg<HashMap<String, Object>> queryPovertyDataTotalByexcelBatchNumber(Ac60ExcelTemp ac60ExcelTemp) {
		List<Ac60ExcelTemp> list = excelImportMapper.queryPovertyDataTotalByexcelBatchNumber(ac60ExcelTemp);;
		PageInfo<Ac60ExcelTemp> pageinfo = new PageInfo<Ac60ExcelTemp>(list);
		return this.success_hashmap_response_rc_fm(pageinfo);
	}
	
	/**
	 * 通用批次号查询导入数据
	 */
	@Override
	public AjaxReturnMsg<HashMap<String, Object>>  getPovertyImprtDataList(Ac60ExcelTemp ac60ExcelTemp) {
		PageHelper.offsetPage(ac60ExcelTemp.getOffset(), ac60ExcelTemp.getLimit());
		List<Ac60ExcelTemp> list = excelImportMapper.queryPovertyDataByexcelBatchNumber(ac60ExcelTemp);;
		PageInfo<Ac60ExcelTemp> pageinfo = new PageInfo<Ac60ExcelTemp>(list);
		return this.success_hashmap_response_rc_fm(pageinfo);
	}

	
	/**
	 * 删除批次及相关信息
	 */
	@Override
	@Transactional
	public AjaxReturnMsg<String> deleteTempDataByNumber(String number) {
		// TODO Auto-generated method stub
		int result=apiFileUploadMapper.deleteExcelBatchByNumber(number);
		if(result==1){
			excelImportMapper.deleteTempDataByNumber(number);
		}
		if(result==1){
			return this.success("删除成功");
		}else{
			return this.error("删除失败");
		}
	}

	/**
	 * 查询excel导出数据
	 */
	@Override
	public List<ExcelExportModel> queryExportDataByNumber(String number) {
		return excelImportMapper.queryExportDataByNumber(number);
	}

	/**
	 * 导出数据数据
	 */
	@Override
	public AjaxReturnMsg<String>  exportData(final String excel_batch_number) {
		// TODO Auto-generated method stub
		final SysExcelBatch  sExcelBatch = apiFileUploadMapper.getExcelBatchByNumber(excel_batch_number);
    	if(null!=sExcelBatch.getExcel_error_file_download()&&sExcelBatch.getExcel_error_file_download().equals("0")){
    		 return this.success("正在生成.....,待生成成功后到批次列表中下载,请勿频繁操作");
    	}
    	//开启线程执行
		taskExecutor.execute(new Runnable() {  
		    @Override  
		    public void run() {  
	            sExcelBatch.setExcel_error_file_download("0");
	            apiFileUploadMapper.updateExelBatchErrorFilePath(sExcelBatch);
		        // TODO Auto-generated method stub  
		        try {  
		        	//查询excel导出数据开始
					log.info("查询excel导出数据开始"+new Date().toLocaleString());
					Date start=new Date();
		        	List<ExcelExportModel> list=	excelImportMapper.queryExportDataByNumber(excel_batch_number);
		        	Date end=new Date();
					Long cost=end.getTime()-start.getTime();
					log.info("查询excel导出数据开始"+new Date().toLocaleString()+"花费="+cost+"数据量="+list.size());
		        	ExcelExport<ExcelExportModel> ee= new ExcelExport<ExcelExportModel>();  
		            String[] headers =
		            {
		            	"原数据行数"
		            	,"姓名"
		            	,"身份证号"
		            	,"劳动者户口所在地"
		            	,"性别"
		            	,"民族"
		            	,"年龄"
		            	,"健康状况"
		            	,"政治面貌"
		            	,"学历代码"
		            	,"联系电话"
		            	,"劳动技能"
		            	,"户主姓名"
		            	,"是否在校生"
		            	,"主要致贫原因"
		            	,"就业新式"
		            	,"就业地域"
		            	,"就业地"
		            	,"就业工种"
		            	,"就业时间"
		            	,"是否签订劳动合同或协议"
		            	,"是否参加社会保险"
		            	,"月均工资"
		            	,"岗位类型"
		            	,"公益性岗位安置地址"
		                ,"公益性岗位安置单位名称"
		            	,"安置时间"
		                ,"岗位名称"
		            	,"是否参加就业培训"
		            	,"培训类型"
		            	,"培训开始时间"
		            	,"培训截止时间"
		            	,"培训类别"
		            	,"取得证书"
		            	,"培训补贴（元）"
		            	,"是否就业"
		            	,"创业时间"
		            	,"是否参加创业培训"
		            	,"创业类型"
		            	,"企业或实体地址"
		            	,"企业或实体名称"
		            	,"产业类别"
		            	,"是否享受小额担保贷款"
		            	,"贷款金额（万元）"
		            	,"吸纳劳动力人数"
		            	,"是否在工商部门注册登记"
		            	,"是否有就业愿望"
		                ,"就业意向地"
		            	,"期望月薪（元）"
		            	,"就业服务需求"
		            	,"提供就业政策咨询次数(次)"
		            	,"之提供就业信息次数（次）"
		            	,"提供职业指导与介绍次数（次）"
		            	,"提供培训次数（次）"
		            	,"提供创业服务次数(次)"
		            	,"是否享受职业介绍补贴"
		            	,"是否享受社会保险补贴"
		            	,"是否享受岗位补贴"
		            	,"导入临时表之是否享受其他政策扶持"
		            	,"是否正确数据"
		            };
		            String filepath=ee.exportCvs(headers,list,sExcelBatch.getExcel_batch_file_name());  
		            sExcelBatch.setExcel_error_file_path(filepath);
		            sExcelBatch.setExcel_error_file_download("1");
		            apiFileUploadMapper.updateExelBatchErrorFilePath(sExcelBatch);
		        } catch (Exception e) {  
		            // TODO Auto-generated catch block  
		            e.printStackTrace();  
		        }  
		    }  
		});  
		return this.success("正在生成.....,待生成成功后到批次列表中下载,请勿频繁操作");
	}

	@Override
	public Ac60ExcelTemp queryImpDataById(String aac002) {
		return excelImportMapper.queryImpDataById(aac002);
	}
}