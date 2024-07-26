package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Qianlk
 */
public class MinioTest {

    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.137.12:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    //上传文件
    @Test
    public  void upload() {
        // 获取拓展名
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".mp4");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }


        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .object("001/test001.mp4")//添加子目录
                    .filename("D:\\个人文件\\研究生工作材料\\衢州体彩备案材料\\45d4792a6c15740577bea61bb6bd2aca.mp4")
                    .contentType(mimeType)//默认根据扩展名确定文件内容类型，也可以指定
                    .build();
            minioClient.uploadObject(testbucket);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }
    }

    @Test
    public void delete(){
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket("testbucket")
                            .object("001/test001.mp4")
                            .build());
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除失败");
        }
    }

    //查询文件
    @Test
    public void getFile() {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("001/test001.mp4")
                .build();
        try(
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
                FileOutputStream outputStream =
                        new FileOutputStream(new File("D:\\Temp\\1_2.mp4"));
        ) {
            IOUtils.copy(inputStream,outputStream);

            // 校验文件的完整性 (源文件md5和本地md5对比)
            String source = DigestUtils.md5Hex(inputStream);
            String target = DigestUtils.md5Hex(Files.newInputStream(Paths.get("D:\\Temp\\1_2.mp4")));

            System.out.println("source: " + source);
            System.out.println("target: " + target);
            if (source.equals(target)) {
                System.out.println("md5一致: " + target);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 测试minio上传分块
    @Test
    void testUploadChunk() {
        String chunkFolderPath = "D:\\Temp\\bigfile_test\\chunk\\";
        File chunkFolder = new File(chunkFolderPath);
        //分块文件
        File[] files = chunkFolder.listFiles();
        //将分块文件上传至minio
        for (int i = 0; i < files.length; i++) {
            try {
                UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                        .bucket("testbucket")
                        .object("chunk/" + i)
                        .filename(files[i].getAbsolutePath()).build();
                minioClient.uploadObject(uploadObjectArgs);
                System.out.println("上传分块成功"+i);
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 合并分块
    @Test
    void testMerge() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
         // 合并分块文件
        List<ComposeSource> source = Stream.iterate(0, i -> ++i)
                .limit(5)
                .map(i -> ComposeSource.builder()
                        .bucket("testbucket")
                        .object("chunk/".concat(Integer.toString(i)))
                        .build()
                ).collect(Collectors.toList());

        //
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge01.mp4")
                .sources(source).build();
        minioClient.composeObject(composeObjectArgs);
    }


    // 清除分块文件
    @Test
    public void testRemoveObjects(){
        //合并分块完成将分块文件清除
        List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                .limit(5)
                .map(i -> new DeleteObject("chunk/".concat(Integer.toString(i))))
                .collect(Collectors.toList());

        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("testbucket")
                .objects(deleteObjects)
                .build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        results.forEach(r->{
            DeleteError deleteError = null;
            try {
                deleteError = r.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void testFile() {
        File file = new File("D:\\Temp\\bigfile_test\\chunk\\0");
        System.out.println(file.getAbsolutePath());
    }

    @Test
    void testCopy() {
        int[] a = new int[] {1,2,3,0,0,0};
        int[] b = new int[] {1,2,2,3,5,6};
        System.arraycopy(b, 0, a, 0, 6);
    }
}
