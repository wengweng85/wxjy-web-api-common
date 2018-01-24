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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.insigma.common.listener.AppConfig;
import com.insigma.common.util.DateUtil;
import com.insigma.common.util.FileUtil;
import com.insigma.common.util.RandomNumUtil;
import com.insigma.common.util.StringUtil;
import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.controller.MvcHelper;
import com.insigma.mvc.dao.common.fileupload.ApiFileUploadMapper;
import com.insigma.mvc.model.SFileRecord;
import com.insigma.mvc.model.SFileType;
import com.insigma.mvc.service.common.fileupload.ApiFileUploadService;

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

    @Override
    public SFileRecord uploadFile(MultipartFile multipartFile, String file_bus_type, String file_bus_name,String file_bus_id) throws Exception {
        SFileRecord sfilerecord = null;
        if (StringUtil.isNotEmpty(file_bus_name)) {
            file_bus_name = URLDecoder.decode(file_bus_name, "utf-8");
        }

        SFileType sFileType = fileUploadMapper.getFileTypeInfo(file_bus_type);
        if (sFileType == null) {
            throw new Exception("�ļ����ͱ�Ų�����");
        }

        long MAX_SIZE = (long) (sFileType.getFileMaxSize() * 1024 * 1024);
        try {
            if (multipartFile.getSize() > MAX_SIZE) {
                throw new Exception("�ļ��ߴ糬���涨��С:" + sFileType.getFileMaxSize() + "M");
            }
            // �õ�ȥ��·�����ļ���
            String originalFilename = multipartFile.getOriginalFilename();
            int indexofdoute = originalFilename.lastIndexOf(".");
            if (indexofdoute < 0) {
                throw new Exception("�ļ���ʽ����");
            }
            // �ļ��ĺ�׺
            String endfix = originalFilename.substring(indexofdoute).toLowerCase();
            String[] arr = AppConfig.getProperties("limitFileType").split(",");
            if (!Arrays.asList(arr).contains(endfix)) {
                throw new Exception("�ļ���ʽ����ȷ,��ȷ��");
            }

            //�ϴ�����¼��־
            if (StringUtil.isNotEmpty(file_bus_id)) {
                SFileRecord condition = new SFileRecord();
                condition.setFile_bus_id(file_bus_id);
                condition.setFile_bus_type(file_bus_type);
                //��ѯ���ϴ��ļ��б�
                List<SFileRecord> list_file = fileUploadMapper.getBusFileRecordListByBusId(condition);

                //���δ�ϴ����ļ�
                if (list_file.size() == 0) {
                    sfilerecord = upload(originalFilename, file_bus_type, file_bus_id, multipartFile.getInputStream());
                    sfilerecord.setFile_bus_name(file_bus_name);
                    fileUploadMapper.saveBusRecord(sfilerecord);
                    return sfilerecord;
                }

                if (sFileType.getFileMaxNum() == 1) {
                    //���ֻ���ϴ�һ���ļ�����ɾ��ԭ���ļ�������ԭ�м�¼
                    sfilerecord = upload(originalFilename, file_bus_type, file_bus_id, multipartFile.getInputStream());
                    SFileRecord oldFile = list_file.get(0);
                    //ɾ��ԭ���ļ�
                    deleteFile(oldFile);

                    sfilerecord.setBus_uuid(oldFile.getBus_uuid());
                    //����ԭ�м�¼
                    sfilerecord.setFile_bus_name(file_bus_name);
                    fileUploadMapper.updateBusRecord(sfilerecord);
                    return sfilerecord;
                } else if (list_file.size() > sFileType.getFileMaxNum()) {
                    // ����ϴ����ļ��Ѵﵽ����ļ���������ʾ
                    throw new Exception("�Ѵﵽ����ϴ��ļ���");
                } else {
                    sfilerecord = upload(originalFilename, file_bus_type, file_bus_id, multipartFile.getInputStream());
                    sfilerecord.setFile_bus_name(file_bus_name);
                    fileUploadMapper.saveBusRecord(sfilerecord);
                    return sfilerecord;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // �����ļ��ߴ�����쳣
            if (e instanceof FileUploadBase.SizeLimitExceededException) {
                throw new Exception("�ļ��ߴ糬���涨��С:" + sFileType.getFileMaxSize() + "M");
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
     * �ϴ��ļ�
     *
     * @param originalFilename
     * @param file_bus_type
     * @param file_bus_id
     * @param in
     * @return
     * @throws Exception
     */
    public SFileRecord upload(String originalFilename, String file_bus_type, String file_bus_id, InputStream in) throws Exception {
        /** ���������� */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = in.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        in.close();
        /**���Ƴ�������������һ�����ڼ���md5,һ�����������ļ�*/
        //InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
        InputStream is2 = new ByteArrayInputStream(baos.toByteArray());
        //String file_md5 = DigestUtils.md5Hex(is1);
        //is1.close();

        //SFileRecord sfilerecord = fileUploadMapper.getFileUploadRecordByFileMd5(file_md5);
        /** ���ͨ��md5�ж��ļ����Ѿ��ڷ������ϴ��ڴ��ļ������ظ����� **/
        //if (sfilerecord == null) {
        SFileRecord sfilerecord = new SFileRecord();

        /** ����ͼƬ�����Ŀ¼ **/
        /** ��ǰ�·� **/
        String fileDir = "/fileroot/" + file_bus_type + "/" + DateUtil.dateToString(new Date(), "yyyyMM");//yyyyMM
        /** ������ʵ·������Ŀ¼ **/
        File fileuploadDir = new File(localdir + fileDir);
        if (!fileuploadDir.exists()) {
            fileuploadDir.mkdirs();
        }

        int indexofdoute = originalFilename.lastIndexOf(".");

        /** ���ļ���������+������� */
        String prefix = DateUtil.dateToString(new Date(), "yyyyMMddHHmmss") + RandomNumUtil.getRandomString(6);
        //�ļ���׺
        String endfix = originalFilename.substring(indexofdoute).toLowerCase();
        sfilerecord.setFile_type(endfix);//�ļ�����

        String new_file_name = prefix + endfix;//�����ɵ��ļ���
        sfilerecord.setFile_name(new_file_name);//�ļ���

        String file_rel_path = fileDir + "/" + prefix + endfix;//���·��
        String filepath = localdir + file_rel_path;//����·��
        File file = new File(filepath);

        sfilerecord.setFile_path(filepath);
        sfilerecord.setFile_rel_path(file_rel_path);
        sfilerecord.setFile_status("1");//��Ч
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
        //sfilerecord.setFile_md5(file_md5);// �ļ�MD5������
        //�����ļ���¼
        fileUploadMapper.saveFileRecord(sfilerecord);
        //sysCodeTypeService.setSelectCacheData("FILENAME");
        //}

        sfilerecord.setFile_bus_name(originalFilename);// �ļ�ԭ��
        sfilerecord.setFile_bus_type(file_bus_type);
        sfilerecord.setFile_bus_id(file_bus_id);
        //����ҵ���¼
        //fileUploadMapper.saveBusRecord(sfilerecord);
        return sfilerecord;
    }


    /**
     * ͨ��ҵ��id��ȡ�ļ��б�
     */
    @Override
    public AjaxReturnMsg<Map<String, Object>> getFileList(SFileRecord sFileRecord) {
        /*List<SFileRecord> list=fileUploadMapper.getBusFileRecordListByBusId(sFileRecord);
        PageInfo<SFileRecord> pageinfo = new PageInfo<SFileRecord>(list);
        return this.success_hashmap_response(pageinfo);*/
        return null;
    }

    /**
     * ����
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
     * �õ��ļ���Ϣ
     */
    @Override
    public AjaxReturnMsg<SFileRecord> getFileInfo(String bus_uuid) {
        return this.success(fileUploadMapper.getBusFileRecordByBusId(bus_uuid));
    }

    /**
     * ɾ���ļ�ҵ���¼�������ļ���¼����ʵ�ļ���
     *
     * @param bus_uuid
     * @return
     */
    @Override
    public AjaxReturnMsg deleteFileByBusUuid(String bus_uuid) {
        SFileRecord sFileRecord = fileUploadMapper.getBusFileRecordByBusId(bus_uuid);
        if (sFileRecord == null) {
            return this.error("�ļ���¼������");
        }

        //ɾ���ļ�ҵ���¼
        fileUploadMapper.deleteFileByBusUuid(bus_uuid);
        //ɾ���ļ���¼����ʵ�ļ�
        deleteFile(sFileRecord);

        return this.success("ɾ���ɹ�");
    }

    /**
     * ɾ���ļ���¼����ʵ�ļ�
     *
     * @param sfilerecord
     */
    public void deleteFile(SFileRecord sfilerecord) {
        try {
            //ɾ���ļ���¼
            fileUploadMapper.deleteFileByFileUuid(sfilerecord.getFile_uuid());
            //ɾ����ʵ�ļ�
            FileUtil.delFileOnExist(sfilerecord.getFile_path());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public AjaxReturnMsg batupdateBusIdByBusUuidArray(Map<String, Object> map) {
        fileUploadMapper.batupdateBusIdByBusUuidArray(map);
        return this.success("���³ɹ�");
    }

}