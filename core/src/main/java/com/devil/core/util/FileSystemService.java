package com.devil.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author Devil
 * @version 1.0
 * @date 2020/6/6 12:45
 * @Desc HDFS文件操作类
 */
@Slf4j
public class FileSystemService {

    private FileSystem fileSystem;

    private Configuration conf = null;

    private static final String SEPARATOR = "/";
    /**
     * 默认的HDFS路径，比如：hdfs://192.168.253.129:9000
     */
    private String defaultHDFSUri;

    public FileSystemService(Configuration conf, String defaultHDFSUri) throws IOException {
        this.conf = conf;
        this.defaultHDFSUri = defaultHDFSUri;
        this.fileSystem = new Path(defaultHDFSUri).getFileSystem(conf);
    }

    /**
     * 创建HDFS目录
     *
     * @param path 创建路径 类似/hbase/tmp
     * @return 是否创建成功
     */
    public boolean mkdir(String path) {
        // 1. 判断路径是否存在
        if (checkExists(path)) {
            return true;
        }
        // 2.生成上传路径 HDFS服务+路径
        String absolutionPath = generateRealPath(path);
        // 3. 创建目录
        try {
            return fileSystem.mkdirs(new Path(absolutionPath));
        } catch (IOException e) {
            log.error(MessageFormat.format("创建HDFS目录失败，path:{0}", path), e);
            return false;
        }
    }

    /**
     * 上传文件
     *
     * @param sourcePath 源路径
     * @param uploadPath 上传相对路径
     */
    public void uploadFile(String sourcePath, String uploadPath) {
        this.uploadFile(sourcePath, uploadPath, true, false);
    }

    /**
     * 上床文件
     *
     * @param sourcePath 源路径
     * @param uploadPath 上传相对路径
     * @param overwrite  是否覆盖
     * @param delSrc     是否删除源文件
     */
    public void uploadFile(String sourcePath, String uploadPath, boolean overwrite, boolean delSrc) {
        Path source = new Path(sourcePath);
        Path HDFSPath = new Path(generateRealPath(uploadPath));
        try {
            fileSystem.copyFromLocalFile(delSrc, overwrite, source, HDFSPath);
        } catch (IOException e) {
            log.error(MessageFormat.format("上传{0}文件到HDFS下{1}目录失败", sourcePath, uploadPath), e);
        }
    }

    /**
     * 将文件下载下来 转换成byte便于传给前端
     *
     * @param path HDFS路径
     * @return byte数组
     */
    public byte[] downloadToBytes(String path) {
        Path HDFSPath = new Path(generateRealPath(path));
        InputStream inputStream = null;
        try {
            FileSystem f = new Path(defaultHDFSUri).getFileSystem(conf);
            inputStream = f.open(HDFSPath);
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error(MessageFormat.format("下载HDFS下{0}文件目录失败", HDFSPath), e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 下载文件到本地
     *
     * @param downloadFile 下载文件的HDFS相对路径 例如/hdfs/a.jpg
     * @param destFile     下载到本地的位置D://a.jpg
     */
    public void downloadFile(String downloadFile, String destFile) {
        Path HDFSPath = new Path(generateRealPath(downloadFile));
        Path destPath = new Path(destFile);
        try {
            FileSystem f = new Path(defaultHDFSUri).getFileSystem(conf);
            f.copyToLocalFile(HDFSPath, destPath);
        } catch (IOException e) {
            log.error(MessageFormat.format("下载HDFS下{0}文件到本地{1}目录失败", HDFSPath, destPath), e);
        }
    }

    /**
     * 上传MultipartFile到指定路径下
     *
     * @param fileInputStream 上传文件流至HDFS
     * @param uploadPath      上传路径
     * @param filename        文件名称
     */
    public void uploadFile(InputStream fileInputStream, String uploadPath, String filename) {
        Path absolutionPath = new Path(generateFileName(generateRealPath(uploadPath), filename));
        try {
            FSDataOutputStream fsDataOutputStream = fileSystem.create(absolutionPath);
//            IOUtils.copy(fileInputStream, fsDataOutputStream);
            fileInputStream = new BufferedInputStream(fileInputStream);
            org.apache.hadoop.io.IOUtils.copyBytes(fileInputStream, fsDataOutputStream, conf);
            fsDataOutputStream.close();
        } catch (IOException e) {
            log.error(MessageFormat.format("上传{0}文件到{1}目录失败", filename, uploadPath), e);
            org.apache.hadoop.io.IOUtils.closeStream(fileInputStream);
        }
    }

    /**
     * 生成文件绝对路径
     *
     * @param filepath 相对路径
     * @param filename 文件名称
     * @return
     */
    private String generateFileName(String filepath, String filename) {
        if (filepath.endsWith("/")) {
            return filepath + filename;
        } else {
            return filepath + SEPARATOR + filename;
        }
    }

    /**
     * 删除HDFS上文件或者目录
     *
     * @param deleteFile HDFS文件地址　/hdfs/a.png /hdfs 会删除目录下所有文件
     */
    public boolean deleteFile(String deleteFile) {
        Path HDFSPath = new Path(deleteFile);
        try {
            return fileSystem.delete(HDFSPath, true);
        } catch (IOException e) {
            log.error(MessageFormat.format("删除HDFS下{0}文件失败", HDFSPath), e);
            return false;
        }
    }

    /**
     * 生成HDFS的绝对路径 使FileSystem直接访问它
     *
     * @param path 相对路径
     * @return 绝对路径
     */
    private String generateRealPath(String path) {
        String HDFSPath = defaultHDFSUri;
        if (path.startsWith(SEPARATOR)) {
            HDFSPath += path;
        } else {
            HDFSPath = HDFSPath + SEPARATOR + defaultHDFSUri;
        }
        return HDFSPath;
    }

    /**
     * 重命名文件
     *
     * @param sourceFile 重命名文件地址 /home/a.png
     * @param newName    新命名文件的相对地址 /home/b_new.png
     * @return 是否重命名成功
     */
    public boolean rename(String sourceFile, String newName) {
        Path sourcePath = new Path(sourceFile);
        Path newPath = new Path(newName);
        try {
            return fileSystem.rename(sourcePath, newPath);
        } catch (IOException e) {
            log.error(MessageFormat.format("重命名HDFS下{0}文件至{1}失败", sourcePath, newPath), e);
            return false;
        }
    }

    /**
     * 判断这个文件在HDFS在是否存在
     *
     * @param targetPath 目标相对路径 类似/home/package
     * @return 是否存在
     */
    private boolean checkExists(String targetPath) {
        String absolutionPath = generateRealPath(targetPath);
        Path path = new Path(absolutionPath);
        try {
            return fileSystem.exists(path);
        } catch (IOException e) {
            log.error(MessageFormat.format("'判断文件或者目录是否在HDFS下上面存在'失败，path:{0}", path), e);
            return false;
        }
    }

    /**
     * 列出指定目录下的所有文件或者文件夹信息
     *
     * @param path       指定路径 /home
     * @param pathFilter 路径过滤器 可以根据条件进行查询过滤
     * @return
     */
    public List<Map<String, Object>> listFiles(String path, PathFilter pathFilter) {
        List<Map<String, Object>> res = new ArrayList<>();
        if (!checkExists(path)) {
            return res;
        }
        String HDFSPath = generateRealPath(path);
        FileStatus[] statuses;
        //根据Path过滤器查询
        try {
            if (pathFilter != null) {
                statuses = fileSystem.listStatus(new Path(HDFSPath), pathFilter);
            } else {
                statuses = fileSystem.listStatus(new Path(HDFSPath));
            }
            if (statuses != null) {
                for (FileStatus status : statuses) {
                    //每个文件的属性
                    Map<String, Object> fileMap = new HashMap<>(2);

                    fileMap.put("path", status.getPath().toString());
                    fileMap.put("isDir", status.isDirectory());
                    res.add(fileMap);
                }
            }
        } catch (IOException e) {
            log.error(MessageFormat.format("'获取{0}目录下所有文件或者文件夹失败", HDFSPath), e);
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        FileSystemService fileSystemService = new FileSystemService(HBaseConnection.getInstance().getConfiguration(),
                "hdfs://192.168.253.129:9000");
//        fileSystemService.mkdir("/test");
        long start = System.currentTimeMillis();
        String file = "C:\\Users\\bodenai\\Downloads\\cuda_10.1.243_426.00_win10.exe";
        File file1 = new File(file);
        fileSystemService.uploadFile(new FileInputStream(file1), "/test", file1.getName());
        long end = System.currentTimeMillis();
        System.out.println("上传大小为" + file1.length() + "B的文件耗时" + (end - start) + "ms");
//        long start = System.currentTimeMillis();
//        String file = "D:\\hadoop-2.10.0.tar";
//        File file1 = new File(file);
//        fileSystemService.downloadFile("/test/hadoop-2.10.0.tar", "D:\\hadoop\\" + file1.getName());
//        long end = System.currentTimeMillis();
//        System.out.println("下载大小为" + file1.length() + "B的文件耗时" + (end - start) + "ms");
//        System.out.println(fileSystemService.rename("/test/hadoop-2.10.0.tar","/test/hadoop-2.10.1.tar"));
//        List<Map<String, Object>> maps = fileSystemService.listFiles("/", null);
//        System.out.println(maps);
//        fileSystemService.downloadFile("/test2.txt", "D:\\hadoop\\test2.txt");
//        fileSystemService.deleteFile("/test");
    }
}
