package com.insigma.mvc.controller.common.fileupload;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.controller.MvcHelper;
import com.insigma.mvc.model.Param;
import com.insigma.mvc.model.SFileRecord;
import com.insigma.mvc.service.common.fileupload.ApiFileUploadService;
import com.insigma.resolver.AppException;

/**
 * 文件上传
 * Created by liuds on 2017/9/11.
 */
@RestController
@Api(description = "公共接口-文件上传")
public class ApiFileUploadController extends MvcHelper {

    @Resource
    private ApiFileUploadService apifileUploadService;

    /**
     * 获取文件类型参数
     *
     * @return
     */
    @RequestMapping(value = "/api/file/getFileType/{businessType}", method = RequestMethod.GET)
	@ApiOperation(value = " 获取文件类型参数", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "businessType", value = "文件类型", required = true, paramType = "path")
    })
    public AjaxReturnMsg getFileType(@PathVariable String businessType) throws Exception {
        return apifileUploadService.getFileType(businessType);
    }


    /**
     * 上传文件
     *
     * @return
     */
    @RequestMapping(value = "/api/file/uploadFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = " 上传文件", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxReturnMsg uploadFile(HttpServletRequest request,@RequestParam("uploadFile") MultipartFile multipartFile) throws Exception {
        String file_bus_id = request.getParameter("file_bus_id");
        String file_bus_type = request.getParameter("file_bus_type");
        String file_bus_name = request.getParameter("file_name");
        return this.success(apifileUploadService.uploadFile(multipartFile, file_bus_type, file_bus_name, file_bus_id));
    }
    
    

    /**
     * 文件下载
     *
     * @param bus_uuid
     * @param response
     * @throws AppException
     */
    @RequestMapping(value = "/api/common/download/{bus_uuid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiOperation(value = " 文件下载", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(@PathVariable String bus_uuid, HttpServletResponse response) {
        try {
            SFileRecord filerecord = apifileUploadService.getFileInfo(bus_uuid).getObj();
            if (filerecord == null) {
                return;
            }
            byte[] temp = apifileUploadService.download(filerecord.getFile_path());
            if (temp == null) {
                throw new Exception("下载错误,不存在的id");
            }
            //此行代码是防止中文乱码的关键！！
            response.setHeader("Content-disposition", "attachment; filename=" +
                    URLEncoder.encode(filerecord.getFile_name(), "utf-8"));
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(temp));
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            //新建一个2048字节的缓冲区
            byte[] buff = new byte[2048];
            int bytesRead = 0;
            while ((bytesRead = bis.read(buff, 0, buff.length)) != -1) {
                bos.write(buff, 0, bytesRead);
            }
            bos.flush();
            bis.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过人员id获取文件列表
     *
     * @param sFileRecord
     * @return
     */
    @RequestMapping(value = "/api/files", method = RequestMethod.POST)
    @ApiOperation(value = " 通过人员id获取文件列表", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxReturnMsg getUserListByGroupid(SFileRecord sFileRecord) throws Exception {
        return apifileUploadService.getFileList(sFileRecord);
    }

    /**
     * 通过业务id获取文件记录表
     *
     * @param bus_uuid
     * @return
     */
    @RequestMapping(value = "/api/file/{bus_uuid}", method = RequestMethod.GET)
    @ApiOperation(value = "通过业务id获取文件记录表", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "bus_uuid", value = "业务id", required = true, paramType = "path")
    })
    public AjaxReturnMsg getUserListByGroupid(@PathVariable String bus_uuid) throws Exception {
        return apifileUploadService.getFileInfo(bus_uuid);
    }


    /**
     * 通过id删除文件信息
     *
     * @param bus_uuid
     * @return
     */
    @RequestMapping(value = "/api/file/{bus_uuid}", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id删除文件信息", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "bus_uuid", value = "业务id", required = true, paramType = "path")
    })
    public AjaxReturnMsg deleteFileByid(@PathVariable String bus_uuid) {
        return apifileUploadService.deleteFileByBusUuid(bus_uuid);
    }
    
    
    /**
     * excel文件上传
     *
     * @param request
     * @param response
     * @return
     * @throws AppException
     */
    @RequestMapping(value = "/api/file/uploadExcelFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = " 上传excel文件", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxReturnMsg  excelupload(HttpServletRequest request,@RequestParam("uploadFile") MultipartFile multipartFile) throws Exception {
		//excel上传文件类型
    	String excel_batch_excel_type=request.getParameter("excel_batch_excel_type");
    	//excel解析列数
    	String mincolumns=request.getParameter("mincolumns");
    	String excel_bs_class_name=Param.FUPING_EXCEL_UPLOAD_CLASS_NAME;
        return apifileUploadService.uploadExcelFile(multipartFile,excel_batch_excel_type,mincolumns,excel_bs_class_name);
    }

}
