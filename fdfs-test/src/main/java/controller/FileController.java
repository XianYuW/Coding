package controller;

import cn.hutool.core.lang.UUID;
import entity.FileSystem;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.FileOutputStream;

@RestController
public class FileController {

    @RequestMapping("/upload")
    //MultipartHttpServletRequest：是httpservletRequest的强化版本，不仅可以装文本信息，还可以装图片文件信息
    public FileSystem upload(MultipartHttpServletRequest request){
        String oldFileName = null;
        String hou = null;
        File toSaveFile = null;
        String newFilePath = null;
        String fileId = null;
        try {
            /* 1、把文件保存到web服务器*/
            // 从页面请求中，获取上传的文件对象
            MultipartFile file = request.getFile("file");
            // 从文件对象中获取 文件的原始名称
            oldFileName = file.getOriginalFilename();
            // 通过字符串截取的方式，从文件原始名中获取文件的后缀 1.jpg
            hou = oldFileName.substring(oldFileName.lastIndexOf(".") + 1);
            // 为了避免文件因为同名而覆盖，生成全新的文件名
            String newFileName = UUID.randomUUID().toString() + "." + hou;
            // 创建web服务器保存文件的目录(预先创建好D:/upload目录，否则系统找不到路径，会抛异常)
            toSaveFile = new File("F:/temp/upload/" + newFileName);
            // 将路径转换成文件
            file.transferTo(toSaveFile);
            // 获取服务器的绝对路径
            newFilePath = toSaveFile.getAbsolutePath();

            /* 2、把文件从web服务器上传到FastDFS*/
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storageServer = null;
            StorageClient1 client = new StorageClient1(trackerServer,
                    storageServer);
            NameValuePair[] list = new NameValuePair[1];
            list[0] = new NameValuePair("fileName",oldFileName);
            fileId = client.upload_file1(newFilePath, hou, list);
            trackerServer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 封装fileSystem数据对象
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setFileName(oldFileName);
        fileSystem.setFilePath(fileId);
        //已经上传到FastDFS上，通过fileId来访问图片，所以fileId即为文件路径
        return fileSystem;
    }

    @RequestMapping("/download")
    //MultipartHttpServletRequest：是httpservletRequest的强化版本，不仅可以装文本信息，还可以装图片文件信息
    public FileSystem download(@RequestBody String field_id){
        try {
            System.out.println(field_id);
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
            field_id = field_id.trim();
            System.out.println("第二次获取到的:"+field_id);


            //获取文件--获取到的是字节流的形式
            byte[] bytes = client.download_file1(field_id);
            //将字节流转化为文件保存
            FileOutputStream outputStream = new FileOutputStream(new File("F:\\temp\\"+ UUID.randomUUID()+".png"));
            outputStream.write(bytes);
            outputStream.close();
            trackerServer.close();
            System.out.println("下载完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
