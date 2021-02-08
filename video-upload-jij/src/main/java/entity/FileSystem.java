package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileSystem implements Serializable {
    private String fileId;
    private String filePath;
    private String fileName;
}
