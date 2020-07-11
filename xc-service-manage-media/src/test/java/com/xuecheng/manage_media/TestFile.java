package com.xuecheng.manage_media;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 祭音丶 on 2020/4/28.
 */
public class TestFile {

    //测试文件分块上传
    @Test
    public void tsetChunk() throws IOException {
        //创建读取的文件对象
        File file = new File("F:\\xc_project\\ffmpeg_test\\test.mp4");
        //创建读取到后的存储地址
        String chunkUrl = "F:\\xc_project\\ffmpeg_test\\chunk\\";
        //声明每块文件读取的大小
        long chunkSize = 1 * 1024 * 1024;
        //声明分的块数
        long chunkFileNum = (long) Math.ceil(file.length() * 1.0/chunkSize);

        //创建读文件的对象
        RandomAccessFile raf_read = new RandomAccessFile(file, "r");
        //创建缓冲区
        byte[] b = new byte[1024];
        //循环读文件
        for (int i = 0; i < chunkFileNum; i++) {
            //块文件
            File chunkFile = new File(chunkUrl + i);
            //创建向块文件的写对象
            RandomAccessFile raf_write = new RandomAccessFile(chunkFile, "rw");
            //创建控制读取刻度的len对象
            int len = -1;
            while ((len=raf_read.read(b))!=-1){
                raf_write.write(b,0,len);
                //如果块大小达到1M，开始写下一块
                if (chunkFile.length() >= chunkSize){
                    break;
                }
            }
            raf_write.close();
        }
        raf_read.close();
    }

    //文件合并
    @Test
    public void testMerge() throws IOException {
        //分快文件目录
        File chunkFolder = new File("F:\\xc_project\\ffmpeg_test\\chunk\\");
        //合并文件地址
        File mergeFile = new File("F:\\xc_project\\ffmpeg_test\\test_merge.mp4");
        //创建新的合并文件
        mergeFile.createNewFile();

        //获取目录下的文件集合
        File[] files = chunkFolder.listFiles();
        //文件列表排序
        List<File> listFile = Arrays.asList(files);
        Collections.sort(listFile, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                    return 1;
                }
                return -1;
            }
        });
        //创建文件读取对象
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        byte[] b = new byte[1024];
        for (File file : listFile) {
            //创建读文件的对象
            RandomAccessFile raf_read = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len =  raf_read.read(b))!=-1){
                raf_write.write(b,0,len);
            }
            raf_read.close();
        }
        raf_write.close();
    }
}
