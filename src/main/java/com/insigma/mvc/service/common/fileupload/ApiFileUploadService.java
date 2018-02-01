package com.insigma.mvc.service.common.fileupload;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.model.SFileRecord;

/**
 * Created by liuds on 2017/8/28.
 */
public interface ApiFileUploadService {


    SFileRecord uploadFile(MultipartFile multipartFile, String file_bus_type, String file_bus_name,String file_bus_id) throws Exception;

    AjaxReturnMsg uploadExcelFile(MultipartFile multipartFile,String excel_upload_type,String minColumns,String excel_bs_class_name) throws Exception;  
    
    /**
     * 获取文件类型参数
     *
     * @param businessType
     * @return
     */
    AjaxReturnMsg getFileType(String businessType);

    AjaxReturnMsg<SFileRecord> getFileInfo(String file_uuid);

    byte[] download(String file_path);

    AjaxReturnMsg getFileList(SFileRecord sFileRecord);

    AjaxReturnMsg deleteFileByBusUuid(String file_uuid);

    AjaxReturnMsg batupdateBusIdByBusUuidArray(Map<String, Object> map);
}
