package com.insigma.mvc.dao.common.fileupload;

import java.util.List;
import java.util.Map;

import com.insigma.mvc.model.SFileRecord;
import com.insigma.mvc.model.SFileType;
import com.insigma.mvc.model.SysExcelBatch;

/**
 * liuds
 * 文件相关Mapper
 * Created by LENOVO on 2017/8/30.
 */
public interface ApiFileUploadMapper {

    /**
     * 获取文件类型信息
     * @param businesstype
     * @return
     */
    SFileType getFileTypeInfo(String businesstype);

    /**
     * 保存文件记录
     * @param sfilerecord
     */
    void saveFileRecord( SFileRecord sfilerecord);

    /**
     * 保存业务记录
     * @param sfilerecord
     */
    void saveBusRecord(SFileRecord sfilerecord);

    /**
     * 更新业务记录
     * @param sfilerecord
     */
    void updateBusRecord(SFileRecord sfilerecord);

    /**
     * 通过文件md5查询文件
     * @param file_md5
     * @return
     */
    SFileRecord getFileUploadRecordByFileMd5(String file_md5);

    /**
     * 通过业务id查询文件
     * @param bus_uuid
     * @return
     */
    SFileRecord getBusFileRecordByBusId(String bus_uuid);

    /**
     * 获取文件列表信息
     * @param sFileRecord
     * @return
     */
    List<SFileRecord> getBusFileRecordListByBusId(SFileRecord sFileRecord);

    /**
     * 通过文件业务编号删除文件业务记录
     * @param bus_uuid
     * @return
     */
    int deleteFileByBusUuid(String bus_uuid);

    /**
     * 批量删除数据
     * @param ids
     * @return
     */
    int batDeleteData(String[] ids);

    /**
     *  通过文件id数组更新业务id及业务状态为有效状态
     * @return
     */
    int batupdateBusIdByBusUuidArray(Map<String,Object> map);

    /**
     * 通过文件编号删除文件
     * @param file_uuid
     * @return
     */
    int deleteFileByFileUuid(String file_uuid);
    
    /**
	 * 保存excel文件上传记录表
	 * @param sfilerecord
	 */
	public void saveExelBatch(SysExcelBatch sExcelBatch);
	
	
	/**
	 * 更新文件记录表
	 * @param sExcelBatch
	 */
	public void updateExelBatch(SysExcelBatch sExcelBatch);
	
	/**
	 * 通过id获取批次号
	 * @param id
	 * @return
	 */
	public SysExcelBatch getExcelBatchById(String id);
	
	
	public SysExcelBatch getExcelBatchByNumber(String number);
	/**
	 * 分页查询
	 * @param sExcelBatch
	 * @return
	 */
	public List<SysExcelBatch> getExcelBatchList(SysExcelBatch sExcelBatch);
	
	/**
	 * 通过批次号删除批次信息
	 * @param number
	 * @return
	 */
	public int deleteExcelBatchByNumber(String number);
	
	
	
	public int updateExelBatchErrorFilePath(SysExcelBatch sExcelBatch);
}
