package test;


import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

public class UploadTest {
    public static void main(String[] args) {
        //加载配置文件
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建tracker客户端
            TrackerClient client = new TrackerClient();
            //通过tracker客户端获取链接服务器并返回
            TrackerServer server = client.getConnection();
            //声明storage服务
            StorageServer storageServer = null;
            //定义storage客户端
            StorageClient1 storageClient1 = new StorageClient1(server, storageServer);
            //定义文件元信息
            NameValuePair[] nameValuePairs = new NameValuePair[2];
            nameValuePairs[0] = new NameValuePair("fileName","2.png");
            nameValuePairs[1] = new NameValuePair("fileName","4.png");

            String[] pngs = storageClient1.upload_file("F:\\4.png", "png", nameValuePairs);
            for (String png : pngs) {
                System.out.println("fileID = " + png);
            }
            //todo group1/M00/00/00/rBEAB2AfiliAY34CAA069dAWjDw615.png
            //    group1/M00/00/00/rBEAB2AfjL2AI1VaACxu-ZPNvX0638.png
            /*
                group1：一台服务器，就是一个组
                M00： store_path0 ----> /home/fastdfs/fdfs_storage/data
                00/00：两级数据目录
            */
            server.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
