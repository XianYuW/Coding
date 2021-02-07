package test;

import org.csource.common.MyException;
import org.csource.fastdfs.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件查询
 */
public class QueryTest {
    public static void main(String[] args) {
        try {
            //加载配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建tracker客户端
            TrackerClient trackerClientclient = new TrackerClient();
            //通过tracker客户端获取链接服务器并返回
            TrackerServer trackerServer = trackerClientclient.getConnection();
            //声明storage服务
            StorageServer server = null;
            //定义storage客户端
            StorageClient1 client = new StorageClient1(trackerServer,server);
            FileInfo fileInfo = client.query_file_info1("group1/M00/00/00/rBEAB2AfoT-AfVEBAA069dAWjDw236.png");
            if(fileInfo != null){
                System.out.println("fileInfo = " + fileInfo);

            }else{
                System.out.println("查无此文件！");
            }
            trackerServer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
