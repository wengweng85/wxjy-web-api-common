package com.insigma.fastdfsclient;

import java.io.IOException;
import java.io.InputStream;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

/**
 * fastdfs文件上传服务
 *
 * @author wengsh
 */
public class Fastdfs {

    private static Fastdfs fastdfs;

    private static TrackerClient trackerClient = null;
    private static TrackerServer trackerServer = null;
    private static StorageClient storageClient = null;
    private static StorageServer storageServer = null;

    private Fastdfs() {

    }

    /**
     * 单例模式
     *
     * @return
     */
    public static Fastdfs getInstance() {
        if (fastdfs == null) {
            fastdfs = new Fastdfs();
            try {
                System.out.println("读取配置文件");
                //读取配置文件
                //获取FastDFS客户端的配置文件和初始化环境
                InputStream in = Fastdfs.class.getClassLoader().getResourceAsStream("fdfs_client.properties");
                ClientGlobal.init(in);
                trackerClient = new TrackerClient();
                trackerServer = trackerClient.getConnection();
                storageClient = new StorageClient(trackerServer, storageServer);
                trackerServer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fastdfs;
    }


    /**
     * 文件上传
     *
     * @param byteFile
     * @param ext_file 文件类型如jpg、doc
     * @return
     * @throws MyException
     * @throws IOException
     */
    public String uploadFile(String groupname, byte[] byteFile, String ext_file) throws MyException, IOException {
        //利用字节流上传文件
        StringBuffer sbPath = new StringBuffer();
        String[] strings = storageClient.upload_file(groupname, byteFile, ext_file, null);
        for (String string : strings) {
            sbPath.append("/" + string);
        }
        // 全路径  
        System.out.println(sbPath);
        return sbPath.toString();
    }

    /**
     * 文件上传
     *
     * @param byteFile
     * @param ext_file 文件类型如jpg、doc
     * @return
     * @throws MyException
     * @throws IOException
     */
    public String uploadFile(byte[] byteFile, String ext_file) throws MyException, IOException {
        //利用字节流上传文件
        StringBuffer sbPath = new StringBuffer();
        String[] strings = storageClient.upload_file(byteFile, ext_file, null);
        for (String string : strings) {
            sbPath.append("/" + string);
        }
        // 全路径  
        System.out.println(sbPath);
        return sbPath.toString();
    }

    /**
     * 文件上传
     *
     * @param filePath 文件全路径
     * @param ext_file 文件类型如jpg、doc
     * @return
     * @throws MyException
     * @throws IOException
     */
    public String uploadFile(String filePath, String ext_file) throws MyException, IOException {
        StringBuffer sbPath = new StringBuffer();
        String[] strings = storageClient.upload_file(filePath, ext_file, null);
        for (String string : strings) {
            sbPath.append("/" + string);
        }
        // 全路径  
        System.out.println(sbPath);
        return sbPath.toString();
    }

    /**
     * 文件上传
     *
     * @param filePath
     * @return
     * @throws MyException
     * @throws IOException
     */
    public String uploadFile(String filePath) throws MyException, IOException {
        if (filePath.contains(".")) {
            String ext_file = filePath.substring(filePath.lastIndexOf(".") + 1);
            return uploadFile(filePath, ext_file);
        } else {
            System.out.println("Fail to upload file, because the format of filename is illegal.");
            throw new MyException("Fail to upload file, because the format of filename is illegal.");
        }
    }

    /**
     * 文件上传
     *
     * @param filePath          本地文件路径
     * @param slave_prefix_name 从文件名前缀
     * @return
     * @throws MyException
     * @throws IOException
     */
    public String[] uploadMasterAndSlaveFile(String filePath, String slave_prefix_name) throws MyException, IOException {
        String[] returnstrs = new String[2];
        if (filePath.contains(".")) {
            String ext_file = filePath.substring(filePath.lastIndexOf(".") + 1);
            //主文件上传
            String[] strings = storageClient.upload_file(filePath, ext_file, null);
            returnstrs[0] = strings[0];
            returnstrs[1] = strings[1];
            System.out.println("主文件路径" + "/" + strings[0] + "/" + strings[1]);
            returnstrs[0] = "/" + strings[0] + "/" + strings[1];

            //从文件上传
            String[] strings_slave = storageClient.upload_file(strings[0], strings[1], slave_prefix_name, filePath, ext_file, null);
            System.out.println("从文件路径" + "/" + strings_slave[0] + "/" + strings_slave[1]);
            returnstrs[1] = "/" + strings_slave[0] + "/" + strings_slave[1];

            return returnstrs;
        } else {
            System.out.println("Fail to upload file, because the format of filename is illegal.");
            throw new MyException("Fail to upload file, because the format of filename is illegal.");
        }
    }

    /**
     * 文件下载
     *
     * @return
     * @throws MyException
     * @throws IOException
     */
    public byte[] downloadFile(String filepath) throws MyException, IOException {
        int index_first = filepath.indexOf("/");
        int index_second = filepath.indexOf("/", index_first + 1);
        byte[] b = storageClient.download_file(filepath.substring(index_first + 1, index_second), filepath.substring(index_second + 1));
        System.out.println(b);
        return b;
    }

    /**
     * 文件删除
     *
     * @return int  0代表删除成功  2删除失败
     * @throws MyException
     * @throws IOException
     */
    public int deleteFile(String filepath) throws MyException, IOException {
        int index_first = filepath.indexOf("/");
        int index_second = filepath.indexOf("/", index_first + 1);
        int i = storageClient.delete_file(filepath.substring(index_first + 1, index_second), filepath.substring(index_second + 1));
        System.out.println(i == 0 ? "删除成功" : "删除失败:" + i);
        return i;
    }
}
