package com.insigma.mvc.serviceimp.common.fileupload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.insigma.common.excel.XLSXCovertCSVReader;
import com.insigma.common.listener.AppConfig;
import com.insigma.common.util.DateUtil;
import com.insigma.common.util.FileUtil;
import com.insigma.common.util.RandomNumUtil;
import com.insigma.common.util.StringUtil;
import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.component.appcontext.MyApplicationContextUtil;
import com.insigma.mvc.controller.MvcHelper;
import com.insigma.mvc.dao.common.fileupload.ApiFileUploadMapper;
import com.insigma.mvc.model.SFileRecord;
import com.insigma.mvc.model.SFileType;
import com.insigma.mvc.model.SysExcelBatch;
import com.insigma.mvc.service.common.excel.ExcelVS;
import com.insigma.mvc.service.common.fileupload.ApiFileUploadService;
import com.insigma.resolver.AppException;

/**
 * Created by LENOVO on 2017/8/28.
 */
@Service
@Transactional
public class ApiFileUploadServiceImpl extends MvcHelper implements ApiFileUploadService {

    @Value("${localdir}")
    private String localdir;

    @Resource
    private ApiFileUploadMapper fileUploadMapper;
    
	@Resource(name = "taskExecutor")  
	private TaskExecutor taskExecutor; 
    
    Log log = LogFactory.getLog(ApiFileUploadServiceImpl.class);

    @Override
    public SFileRecord uploadFile(MultipartFile multipartFile, String file_bus_type, String file_bus_name,String file_bus_id) throws Exception {
        SFileRecord sfilerecord = null;
        if (StringUtil.isNotEmpty(file_bus_name)) {
            file_bus_name = URLDecoder.decode(file_bus_name, "utf-8");
        }

        SFileType sFileType = fileUploadMapper.getFileTypeInfo(file_bus_type);
        if (sFileType == null) {
            throw new Exception("文件类型编号不存在");
        }

        long MAX_SIZE = (long) (sFileType.getFileMaxSize() * 1024 * 1024);
        try {
            if (multipartFile.getSize() > MAX_SIZE) {
                throw new Exception("文件尺寸超过规定大小:" + sFileType.getFileMaxSize() + "M");
            }
            // 得到去除路径的文件名
            String originalFilename = multipartFile.getOriginalFilename();
            int indexofdoute = originalFilename.lastIndexOf(".");
            if (indexofdoute < 0) {
                throw new Exception("文件格式错误");
            }
            // 文件的后缀
            String endfix = originalFilename.substring(indexofdoute).toLowerCase();
            String[] arr = AppConfig.getProperties("limitFileType").split(",");
            if (!Arrays.asList(arr).contains(endfix)) {
                throw new Exception("文件格式不正确,请确认");
            }

            //上传并记录日志
            if (StringUtil.isNotEmpty(file_bus_id)) {
                SFileRecord condition = new SFileRecord();
                condition.setFile_bus_id(file_bus_id);
                condition.setFile_bus_type(file_bus_type);
                //查询已上传文件列表
                List<SFileRecord> list_file = fileUploadMapper.getBusFileRecordListByBusId(condition);

                //如果未上传过文件
                if (list_file.size() == 0) {
                    sfilerecord = upload(originalFilename, file_bus_type, file_bus_id, multipartFile.getInputStream());
                    sfilerecord.setFile_bus_name(file_bus_name);
                    fileUploadMapper.saveBusRecord(sfilerecord);
                    return sfilerecord;
                }

                if (sFileType.getFileMaxNum() == 1) {
                    //如果只能上传一个文件，则删除原有文件，更新原有记录
                    sfilerecord = upload(originalFilename, file_bus_type, file_bus_id, multipartFile.getInputStream());
                    SFileRecord oldFile = list_file.get(0);
                    //删除原有文件
                    deleteFile(oldFile);

                    sfilerecord.setBus_uuid(oldFile.getBus_uuid());
                    //更新原有记录
                    sfilerecord.setFile_bus_name(file_bus_name);
                    fileUploadMapper.updateBusRecord(sfilerecord);
                    return sfilerecord;
                } else if (list_file.size() > sFileType.getFileMaxNum()) {
                    // 如果上传的文件已达到最大文件数，则提示
                    throw new Exception("已达到最大上传文件数");
                } else {
                    sfilerecord = upload(originalFilename, file_bus_type, file_bus_id, multipartFile.getInputStream());
                    sfilerecord.setFile_bus_name(file_bus_name);
                    fileUploadMapper.saveBusRecord(sfilerecord);
                    return sfilerecord;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 处理文件尺寸过大异常
            if (e instanceof FileUploadBase.SizeLimitExceededException) {
                throw new Exception("文件尺寸超过规定大小:" + sFileType.getFileMaxSize() + "M");
            }
            throw new Exception(e.getMessage());
        }
        return sfilerecord;
    }

    @Override
    public AjaxReturnMsg getFileType(String businessType) {
        SFileType sFileType = fileUploadMapper.getFileTypeInfo(businessType);
        return this.success(sFileType);
    }

    /**
     * 上传文件
     *
     * @param originalFilename
     * @param file_bus_type
     * @param file_bus_id
     * @param in
     * @return
     * @throws Exception
     */
    public SFileRecord upload(String originalFilename, String file_bus_type, String file_bus_id, InputStream in) throws Exception {
        /** 拷贝输入流 */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = in.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        in.close();
        /**复制成两个输入流，一个用于计算md5,一个用于生成文件*/
        //InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
        InputStream is2 = new ByteArrayInputStream(baos.toByteArray());
        //String file_md5 = DigestUtils.md5Hex(is1);
        //is1.close();

        //SFileRecord sfilerecord = fileUploadMapper.getFileUploadRecordByFileMd5(file_md5);
        /** 如果通过md5判断文件，已经在服务器上存在此文件，不重复保存 **/
        //if (sfilerecord == null) {
        SFileRecord sfilerecord = new SFileRecord();

        /** 构建图片保存的目录 **/
        /** 当前月份 **/
        String fileDir = "/fileroot/" + file_bus_type + "/" + DateUtil.dateToString(new Date(), "yyyyMM");//yyyyMM
        /** 根据真实路径创建目录 **/
        File fileuploadDir = new File(localdir + fileDir);
        if (!fileuploadDir.exists()) {
            fileuploadDir.mkdirs();
        }

        int indexofdoute = originalFilename.lastIndexOf(".");

        /** 新文件名按日期+随机生成 */
        String prefix = DateUtil.dateToString(new Date(), "yyyyMMddHHmmss") + RandomNumUtil.getRandomString(6);
        //文件后缀
        String endfix = originalFilename.substring(indexofdoute).toLowerCase();
        sfilerecord.setFile_type(endfix);//文件类型

        String new_file_name = prefix + endfix;//新生成的文件名
        sfilerecord.setFile_name(new_file_name);//文件名

        String file_rel_path = fileDir + "/" + prefix + endfix;//相对路径
        String filepath = localdir + file_rel_path;//绝对路径
        File file = new File(filepath);

        sfilerecord.setFile_path(filepath);
        sfilerecord.setFile_rel_path(file_rel_path);
        sfilerecord.setFile_status("1");//有效
        OutputStream os = new FileOutputStream(file);
        int bytesRead = 0;
        buffer = new byte[8192];
        while ((bytesRead = is2.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        os.close();
        is2.close();
        baos.close();

        sfilerecord.setFile_length(file.length());
        sfilerecord.setFile_type(sfilerecord.getFile_name().substring(sfilerecord.getFile_name().lastIndexOf(".") + 1));
        //sfilerecord.setFile_md5(file_md5);// 文件MD5计算器
        //保存文件记录
        fileUploadMapper.saveFileRecord(sfilerecord);
        //sysCodeTypeService.setSelectCacheData("FILENAME");
        //}

        sfilerecord.setFile_bus_name(originalFilename);// 文件原名
        sfilerecord.setFile_bus_type(file_bus_type);
        sfilerecord.setFile_bus_id(file_bus_id);
        //保存业务记录
        //fileUploadMapper.saveBusRecord(sfilerecord);
        return sfilerecord;
    }


    /**
     * 通过业务id获取文件列表
     */
    @Override
    public AjaxReturnMsg<Map<String, Object>> getFileList(SFileRecord sFileRecord) {
        List<SFileRecord> list=fileUploadMapper.getBusFileRecordListByBusId(sFileRecord);
        PageInfo<SFileRecord> pageinfo = new PageInfo<SFileRecord>(list);
        return this.success(pageinfo);
    }

    /**
     * 下载
     */
    @Override
    public byte[] download(String file_path) {
        InputStream data = null;
        try {
            data = new FileInputStream(file_path);
            int size = data.available();
            byte[] buffer = new byte[size];
            IOUtils.read(data, buffer);
            return buffer;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(data);
        }
    }


    /**
     * 得到文件信息
     */
    @Override
    public AjaxReturnMsg<SFileRecord> getFileInfo(String bus_uuid) {
        return this.success(fileUploadMapper.getBusFileRecordByBusId(bus_uuid));
    }

    /**
     * 删除文件业务记录（包括文件记录和真实文件）
     *
     * @param bus_uuid
     * @return
     */
    @Override
    public AjaxReturnMsg deleteFileByBusUuid(String bus_uuid) {
        SFileRecord sFileRecord = fileUploadMapper.getBusFileRecordByBusId(bus_uuid);
        if (sFileRecord == null) {
            return this.error("文件记录不存在");
        }

        //删除文件业务记录
        fileUploadMapper.deleteFileByBusUuid(bus_uuid);
        //删除文件记录和真实文件
        deleteFile(sFileRecord);

        return this.success("删除成功");
    }

    /**
     * 删除文件记录和真实文件
     *
     * @param sfilerecord
     */
    public void deleteFile(SFileRecord sfilerecord) {
        try {
            //删除文件记录
            fileUploadMapper.deleteFileByFileUuid(sfilerecord.getFile_uuid());
            //删除真实文件
            FileUtil.delFileOnExist(sfilerecord.getFile_path());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public AjaxReturnMsg batupdateBusIdByBusUuidArray(Map<String, Object> map) {
        fileUploadMapper.batupdateBusIdByBusUuidArray(map);
        return this.success("更新成功");
    }
    
   

	/**
	 * 
	 * @param multipartFile
	 * @param excel_batch_excel_type
	 * @param excel_bs_class_name
	 * @param minColumns
	 * @return
	 * @throws Exception
	 */
	@Override
	public AjaxReturnMsg uploadExcelFile(MultipartFile multipartFile, String excel_batch_excel_type, String minColumns,String excel_bs_class_name) throws Exception {
		long MAX_SIZE = 100* 1024 * 1024L;//100M
    	try {
	        if (multipartFile.getSize() > MAX_SIZE) {
	        	return this.error( "文件尺寸超过规定大小:" + MAX_SIZE / 1024 / 1024 + "M");
	        } else {
	            // 得到去除路径的文件名
	            String originalFilename = multipartFile.getOriginalFilename();
		        return this.success(uploadexcel(originalFilename,excel_batch_excel_type,minColumns,excel_bs_class_name,multipartFile.getInputStream()));
	        }
        } catch (Exception e) {
        	e.printStackTrace();
			// 处理文件尺寸过大异常
			return this.error(e.getMessage());
        }
	}
	
	
	 /**
     * excel文件上传
     * @originalFilename 原始文件名
     * @excel_batch_excel_type 批处理文件类型
     *    minColumns 最小宽度
     *    excel_vs_bean_name 处理类名
     */
	public String uploadexcel(String originalFilename,final String excel_batch_excel_type,  String minColumns , String excel_vs_bean_name, InputStream in) throws AppException {
		// TODO Auto-generated method stub
	    final SysExcelBatch sexcelbatch = new SysExcelBatch();
	    File file=null;
	    try{
				/** 当前月份 **/
				String ym = new SimpleDateFormat("yyyyMM").format(new Date());
				/** 根据真实路径创建目录 **/
				File fileuploadDir = new File(localdir + "/" + ym);
				if (!fileuploadDir.exists()) {
					fileuploadDir.mkdirs();
				}
				int indexofdoute = originalFilename.lastIndexOf(".");
				/**文件名及后缀*/
				String prefix = originalFilename.substring(0, indexofdoute);
				String endfix = originalFilename.substring(indexofdoute).toLowerCase();
				/**新文件名按日期到毫秒*/
				prefix = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				String filepath=localdir + "/" + ym + "/"+prefix+endfix;
				file=new File(filepath);
				sexcelbatch.setExcel_batch_file_path(filepath);
				sexcelbatch.setExcel_batch_file_name(originalFilename);
				OutputStream os = new FileOutputStream(file);
				int bytesRead = 0;
				byte [] buffer = new byte[8192];
				while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				os.flush();
				os.close();
				in.close();
				//批次号为当前时间到毫秒
				sexcelbatch.setExcel_batch_number(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
				//记录excel相关信息 包括文件大小、业务类型、获取行数、上传状态等
				sexcelbatch.setExcel_batch_file_length(file.length());
				sexcelbatch.setExcel_batch_excel_type(excel_batch_excel_type);
				sexcelbatch.setExcel_batch_aae011("");
				sexcelbatch.setExcel_batch_status("0");//转换xlsx
				//保存文件记录
				fileUploadMapper.saveExelBatch(sexcelbatch);
				
				//第一步 将excel转换成cvs格式
				final List<String[]> list = XLSXCovertCSVReader.readerExcel( filepath, "Sheet1", new Integer(minColumns).intValue());
				if(list==null){
					sexcelbatch.setExcel_batch_status("4");//发生异常
					sexcelbatch.setExcel_batch_rt_msg("所用的excel格式不正确,请确定excel第一列sheet名字为Sheet1");
					fileUploadMapper.updateExelBatch(sexcelbatch);
					throw new AppException("所用的excel格式不正确,请确定excel第一列sheet名字为Sheet1");
				}
				//行总数
				sexcelbatch.setExcel_batch_total_count(new Long(list.size()));
				sexcelbatch.setExcel_batch_data_status(10);//数据导入状态10%
				sexcelbatch.setExcel_batch_status("1");//导入临时表
				//更新文件记录
				fileUploadMapper.updateExelBatch(sexcelbatch);
				
			    final ExcelVS excelvs=(ExcelVS) MyApplicationContextUtil.getContext().getBean(excel_vs_bean_name);
				
				//开启线程执行
				taskExecutor.execute(new Runnable() {  
				    @Override  
				    public void run() {  
				        // TODO Auto-generated method stub  
				        try {  
				        	//数据处理
							//if(excel_batch_excel_type.equals("sxpt_excel_imp")){
								//数据保存到临时表
								log.info("保存到临时表开始时间"+new Date().toLocaleString());
								Date start=new Date();
								//第二步 导入临时表
								excelvs.executeListToTemp(list, sexcelbatch);
								Date end=new Date();
								Long cost=end.getTime()-start.getTime();
								log.info("保存到临时表开始结束"+new Date().toLocaleString()+"花费"+cost/1000+"s");
								//第三步 调用过程处理数据
								excelvs.executeProc(sexcelbatch);
							//}
				        } catch (Exception e) {  
				            // TODO Auto-generated catch block  
				            e.printStackTrace();  
				        }  
				    }  
				});  
			}catch(Exception e){
				e.printStackTrace();
				sexcelbatch.setExcel_batch_status("4");//发生异常
				if(e.getMessage().equals(minColumns)){
					sexcelbatch.setExcel_batch_rt_msg("所用的excel格式不正确,数据列超过要求的"+minColumns+"列,请确认");
				}else{
					sexcelbatch.setExcel_batch_rt_msg(e.getMessage());
				}
				fileUploadMapper.updateExelBatch(sexcelbatch);
				throw new AppException(e.getMessage());
			}finally{
		         if(file.exists()){
		        	file.delete();
		         }
			}
		   return JSONObject.fromObject(sexcelbatch).toString();

	}

}
