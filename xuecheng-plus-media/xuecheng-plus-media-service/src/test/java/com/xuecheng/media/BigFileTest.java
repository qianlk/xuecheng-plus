package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 断点续传方法
 *
 * @author Qianlk
 */
public class BigFileTest {

    // 测试文件分块
    @Test
    void testChunk() throws IOException {

        // 读取源文件
        File sourceFile = new File("D:\\个人文件\\研究生工作材料\\绍兴体彩审核材料\\11.mp4");
        // 分块目录
        String chunkPath = "D:\\Temp\\bigfile_test\\chunk\\";
        File chunkFolder = new File(chunkPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        // 分块大小
        long chunkSize = 1024 * 1024;
        // 分块数量 (向上取整)
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);

        // 缓冲区大小
        byte[] bytes = new byte[1024];
        // randomaccessfile访问文件
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");
        // 分块
        for (int i = 0; i < chunkNum; i++) {
            // 创建分块文件
            File file = new File(chunkPath + i);
            if (file.exists()) {
                file.delete();
            }
            boolean newFile = file.createNewFile();
            if (newFile) {
                // 向分块文件中写入文件
                RandomAccessFile raf_rw = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_r.read(bytes)) != -1) {
                    raf_rw.write(bytes, 0, len);
                    if (file.length() >= chunkSize) {
                        break;
                    }
                }
                raf_rw.close();
            }
        }
        raf_r.close();
    }

    // 测试文件合并

    @Test
    void testMerge() throws IOException {
        // 源文件
        File sourceFile = new File("D:\\个人文件\\研究生工作材料\\绍兴体彩审核材料\\11.mp4");
        // 块文件目录
        File chunkFolder = new File("D:\\Temp\\bigfile_test\\chunk\\");
        // 合并文件
        File mergeFile = new File("D:\\Temp\\bigfile_test\\merge\\merge.mp4");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        // 创建合并文件
        mergeFile.createNewFile();
        // 读写流
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        // 指针指向文件顶端
        raf_write.seek(0);
        // 写缓存区
        byte[] bytes = new byte[1024];

        // 获取分块列表
        File[] files = chunkFolder.listFiles();
        List<File> fileList = Arrays.asList(files);
        // 分块按文件名称排序
//        Collections.sort(fileList, (o1, o2) -> Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName()));
        Collections.sort(fileList, Comparator.comparingInt(o -> Integer.parseInt(o.getName())));

        // 合并文件
        for (File chunkFile : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_read.read(bytes)) != -1) {
                raf_write.write(bytes, 0, len);
            }
            raf_read.close();
        }
        raf_write.close();

        //校验文件
        try (

                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                FileInputStream mergeFileStream = new FileInputStream(mergeFile);

        ) {
            //取出原始文件的md5
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);
            //取出合并文件的md5进行比较
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
            if (originalMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
            } else {
                System.out.println("合并文件失败");
            }
        }
    }

    @Test
    void testChunk02() throws IOException {

        // 读取源文件
        File sourceFile = new File("D:\\Temp\\test.mp4");
        // 分块目录
        String chunkPath = "D:\\Temp\\bigfile_test\\chunk\\";
        File chunkFolder = new File(chunkPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        } else {
            deleteFileInFolder(chunkFolder);
        }
        // 分块大小
        long chunkSize = 1024 * 1024 * 5;
        // 分块数量 (向上取整)
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);

        // 缓冲区大小
        byte[] bytes = new byte[1024];
        // randomaccessfile访问文件
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");
        // 分块
        for (int i = 0; i < chunkNum; i++) {
            // 创建分块文件
            File file = new File(chunkPath + i);
            if (file.exists()) {
                file.delete();
            }
            boolean newFile = file.createNewFile();
            if (newFile) {
                // 向分块文件中写入文件
                RandomAccessFile raf_rw = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_r.read(bytes)) != -1) {
                    raf_rw.write(bytes, 0, len);
                    if (file.length() >= chunkSize) {
                        break;
                    }
                }
                raf_rw.close();
            }
        }
        raf_r.close();
    }

    private void deleteFileInFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFileInFolder(folder);
                } else {
                    file.delete();
                }
            }
        }
    }
}