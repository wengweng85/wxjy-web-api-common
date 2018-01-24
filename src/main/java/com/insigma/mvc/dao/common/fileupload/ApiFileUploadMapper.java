package com.insigma.mvc.dao.common.fileupload;

import java.util.List;
import java.util.Map;

import com.insigma.mvc.model.SFileRecord;
import com.insigma.mvc.model.SFileType;

/**
 * liuds
 * �ļ����Mapper
 * Created by LENOVO on 2017/8/30.
 */
public interface ApiFileUploadMapper {

    /**
     * ��ȡ�ļ�������Ϣ
     * @param businesstype
     * @return
     */
    SFileType getFileTypeInfo(String businesstype);

    /**
     * �����ļ���¼
     * @param sfilerecord
     */
    void saveFileRecord( SFileRecord sfilerecord);

    /**
     * ����ҵ���¼
     * @param sfilerecord
     */
    void saveBusRecord(SFileRecord sfilerecord);

    /**
     * ����ҵ���¼
     * @param sfilerecord
     */
    void updateBusRecord(SFileRecord sfilerecord);

    /**
     * ͨ���ļ�md5��ѯ�ļ�
     * @param file_md5
     * @return
     */
    SFileRecord getFileUploadRecordByFileMd5(String file_md5);

    /**
     * ͨ��ҵ��id��ѯ�ļ�
     * @param bus_uuid
     * @return
     */
    SFileRecord getBusFileRecordByBusId(String bus_uuid);

    /**
     * ��ȡ�ļ��б���Ϣ
     * @param sFileRecord
     * @return
     */
    List<SFileRecord> getBusFileRecordListByBusId(SFileRecord sFileRecord);

    /**
     * ͨ���ļ�ҵ����ɾ���ļ�ҵ���¼
     * @param bus_uuid
     * @return
     */
    int deleteFileByBusUuid(String bus_uuid);

    /**
     * ����ɾ������
     * @param ids
     * @return
     */
    int batDeleteData(String[] ids);

    /**
     *  ͨ���ļ�id�������ҵ��id��ҵ��״̬Ϊ��Ч״̬
     * @return
     */
    int batupdateBusIdByBusUuidArray(Map<String,Object> map);

    /**
     * ͨ���ļ����ɾ���ļ�
     * @param file_uuid
     * @return
     */
    int deleteFileByFileUuid(String file_uuid);
}