package test;

import org.csource.common.MyException;
import org.csource.fastdfs.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件下载
 */
public class DownloadTest {
    public static void main(String[] args) {
        try {
            //加载配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建tracker客户端
            TrackerClient trackerClient = new TrackerClient();
            //通过tracker客户端获取链接服务器并返回
            TrackerServer trackerServer = trackerClient.getConnection();
            //声明storage服务
            StorageServer storageServer = null;
            //定义storage客户端
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);

            //获取文件--获取到的是字节流的形式
            byte[] bytes = client.download_file1("group1/M00/00/00/rBEAB2AfiliAY34CAA069dAWjDw615.png");
            //将字节流转化为文件保存
            File file;
            FileOutputStream outputStream = new FileOutputStream(new File("F:\\temp\\"+ UUID.randomUUID()+".png"));
            outputStream.write(bytes);
            outputStream.close();
            trackerServer.close();
            System.out.println("下载完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
