package com.lsz.crawler.ForumCrawer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by asus on 2017/4/20.
 */
public class FileUtil {
    /** 压缩单个文件*/
    public static File ZipFile(String filepath ,String zippath) {
        try {
            File file = new File(filepath);
            File zipFile = new File(zippath);
            InputStream input = new FileInputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            zipOut.putNextEntry(new ZipEntry(file.getName()));
            int temp = 0;
            while((temp = input.read()) != -1){
                zipOut.write(temp);
            }
            input.close();
            zipOut.close();
            return new File(zippath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static File ZipFile2(File  file ,String zippath) {
        try {
            File zipFile = new File(zippath);
            InputStream input = new FileInputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            zipOut.putNextEntry(new ZipEntry(file.getName()));
            int temp = 0;
            while((temp = input.read()) != -1){
                zipOut.write(temp);
            }
            input.close();
            zipOut.close();
            return new File(zippath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
