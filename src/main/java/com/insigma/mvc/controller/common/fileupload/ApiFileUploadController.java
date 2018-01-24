package com.insigma.mvc.controller.common.fileupload;

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
import com.insigma.mvc.model.SFileRecord;
import com.insigma.mvc.service.common.fileupload.ApiFileUploadService;
import com.insigma.resolver.AppException;

/**
 * �ļ��ϴ�
 * Created by liuds on 2017/9/11.
 */
@RestController
public class ApiFileUploadController extends MvcHelper {

    @Resource
    private ApiFileUploadService apifileUploadService;

    /**
     * ��ȡ�ļ����Ͳ���
     *
     * @return
     */
    @RequestMapping(value = "/api/file/getFileType/{businessType}", method = RequestMethod.GET)
    public AjaxReturnMsg getFileType(@PathVariable String businessType) throws Exception {
        return apifileUploadService.getFileType(businessType);
    }


    /**
     * �ϴ��ļ�
     *
     * @return
     */
    @RequestMapping(value = "/api/file/uploadFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxReturnMsg uploadFile(HttpServletRequest request,@RequestParam("uploadFile") MultipartFile multipartFile) throws Exception {
        String file_bus_id = request.getParameter("file_bus_id");
        String file_bus_type = request.getParameter("file_bus_type");
        String file_bus_name = request.getParameter("file_name");
        return this.success(apifileUploadService.uploadFile(multipartFile, file_bus_type, file_bus_name, file_bus_id));
    }

    /**
     * �ļ�����
     *
     * @param bus_uuid
     * @param response
     * @throws AppException
     */
    @RequestMapping(value = "/api/common/download/{bus_uuid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(@PathVariable String bus_uuid, HttpServletResponse response) {
        try {
            SFileRecord filerecord = apifileUploadService.getFileInfo(bus_uuid).getObj();
            if (filerecord == null) {
                return;
            }
            byte[] temp = apifileUploadService.download(filerecord.getFile_path());
            if (temp == null) {
                throw new Exception("���ش���,�����ڵ�id");
            }
            //���д����Ƿ�ֹ��������Ĺؼ�����
            response.setHeader("Content-disposition", "attachment; filename=" +
                    URLEncoder.encode(filerecord.getFile_name(), "utf-8"));
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(temp));
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            //�½�һ��2048�ֽڵĻ�����
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
     * ͨ����Աid��ȡ�ļ��б�
     *
     * @param sFileRecord
     * @return
     */
    @RequestMapping(value = "/api/file/getFileList", method = RequestMethod.POST)
    public AjaxReturnMsg getUserListByGroupid(SFileRecord sFileRecord) throws Exception {
        return apifileUploadService.getFileList(sFileRecord);
    }

    /**
     * ͨ��ҵ��id��ȡ�ļ���¼��
     *
     * @param bus_uuid
     * @return
     */
    @RequestMapping(value = "/api/file/getFileInfo/{bus_uuid}", method = RequestMethod.GET)
    public AjaxReturnMsg getUserListByGroupid(@PathVariable String bus_uuid) throws Exception {
        return apifileUploadService.getFileInfo(bus_uuid);
    }


    /**
     * ͨ��idɾ���ļ���Ϣ
     *
     * @param bus_uuid
     * @return
     */
    @RequestMapping(value = "/api/file/deleteById/{bus_uuid}", method = RequestMethod.GET)
    public AjaxReturnMsg deleteFileByid(@PathVariable String bus_uuid) {
        return apifileUploadService.deleteFileByBusUuid(bus_uuid);
    }

}
