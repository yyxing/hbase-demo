package com.devil.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Devil
 * @version 1.0
 * @date 2020/6/9 14:32
 */
@Slf4j
public class FileUtil {

    /**
     * 一个分片大小 以MB标识
     */
    private Integer chunkSize;

    /**
     * 对于大文件的流进行分片处理 秒传 断点传续
     * @param file 输入的完整文件 第一步split merge
     */
    public void splitUpload(File file){
        // TODO 计算HASH值 判断HOS是否包含这个文件 若存在则直接返回成功

        // 分片处理
        try {
            // 构造文件路径 测试时放到user的/temp下
            Path basePath = Paths.get("D:\\Temp");
            // 生成文件夹
            Files.createDirectories(basePath);
            // 进行分片操作

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
