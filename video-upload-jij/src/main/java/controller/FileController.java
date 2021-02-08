package controller;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import entity.FileSystem;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

@RestController
public class FileController {

    @Autowired
    private Redisson redisson;

    @Bean
    public Redisson redisson(){
        Config config = new Config();
        //使用单个redis服务器
        config.useSingleServer().setAddress("redis://121.4.196.77:6379").setPassword("JIjun@666").setDatabase(0);
        //使用集群redis
        //config.useClusterServers().setScanInterval(2000).addNodeAddress("redis://121.4.196.77:6379","redis://121.4.196.77:6379").setPassword("JIjun@666");
        System.out.println("开启redision成功");
        return (Redisson)Redisson.create(config);
    }
    private  final String UPLOAD_LOCK_STR = "uoload-lock-str";

    @RequestMapping("/upload")
    //MultipartHttpServletRequest：是httpservletRequest的强化版本，不仅可以装文本信息，还可以装图片文件信息
    public FileSystem uplaod(Model model,MultipartHttpServletRequest request){
        System.out.println("上传文件中。。。");
        FileSystem fileSystem = new FileSystem();

        // 从页面请求中，获取上传的文件对象
        MultipartFile file = request.getFile("file");
        //开启分布式锁
        if(ObjectUtil.isNotNull(fileSystem)){
            //通过redis  获取锁
            RLock rlock = redisson.getLock(UPLOAD_LOCK_STR);
            //上锁 (过期时间为30s)
            rlock.lock(60, TimeUnit.SECONDS);
            //上传文件
            fileSystem = uploadFileToFastDFS(file);
            //释放锁
            rlock.unlock();
        }
        return fileSystem;
    }

    public FileSystem uploadFileToFastDFS(MultipartFile file){
        String oldFileName = null;
        String type = null;
        File toSaveFile = null;
        String newFilePath = null;
        String fileId = null;
        try {
            /* 1、把文件保存到web服务器*/

            // 从文件对象中获取 文件的原始名称
            oldFileName = file.getOriginalFilename();
            // 通过字符串截取的方式，从文件原始名中获取文件的后缀 1.jpg
            type = oldFileName.substring(oldFileName.lastIndexOf(".") + 1);
            // 为了避免文件因为同名而覆盖，生成全新的文件名
            String newFileName = UUID.randomUUID().toString() + "." + type;
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
            fileId = client.upload_file1(newFilePath, type, list);
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
        System.out.println("文件上传完毕");
        return fileSystem;
    }
}