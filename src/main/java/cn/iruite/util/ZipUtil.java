package cn.iruite.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public class ZipUtil {
    private static final int buffer = 2048;


    /**
     * 解压zip
     *
     * @param zipPath      zip文件路径 必传
     * @param saveFilePath 如果为空那么解压到zipPath的当前目录,不为空解压到指定目录
     */
    public static void unZip(String zipPath, String saveFilePath) {
        int count = -1;
        String savepath = "";
        File file = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        //保存解压文件目录
        if (StringUtils.isNotBlank(saveFilePath)) {
            savepath = new File(saveFilePath) + File.separator;
        } else {
            savepath = new File(zipPath).getParent() + File.separator;
        }
        new File(savepath).mkdir(); //创建保存目录
        ZipFile zipFile = null;
        try {
            //解决中文乱码问题  格式有GBK  UTF8
            zipFile = new ZipFile(zipPath, "GBK");
            Enumeration<?> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                byte buf[] = new byte[buffer];
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String filename = entry.getName();
                boolean ismkdir = false;
                //检查此文件是否带有文件夹
                if (filename.lastIndexOf("/") != -1) {
                    ismkdir = true;
                }
                filename = savepath + filename;
                //如果是文件夹先创建
                if (entry.isDirectory()) {
                    file = new File(filename);
                    file.mkdirs();
                    continue;
                }
                file = new File(filename);
                if (!file.exists()) {
                    //如果是目录先创建
                    if (ismkdir) {
                        //目录先创建
                        new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs();
                    }
                }
                //创建文件
                file.createNewFile();
                is = zipFile.getInputStream((ZipArchiveEntry) entry);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, buffer);
                while ((count = is.read(buf)) > -1) {
                    bos.write(buf, 0, count);
                }
                bos.flush();
                bos.close();
                fos.close();
                is.close();
            }
            zipFile.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 压缩文件
     * @param sourceDir 原文件目录
     * @param zipFile 压缩后的文件名称
     * @dirFlag zip文件中第一层是否包含一级目录，true包含；false没有
     * @throws Exception
     */
    public static void doZip(String sourceDir, String zipFile,boolean dirFlag) throws Exception{
        File targetFile = new File(zipFile);
        OutputStream os= Files.newOutputStream(targetFile.toPath());
        BufferedOutputStream bos = new BufferedOutputStream(os);
        ZipOutputStream zos = new ZipOutputStream(bos);
        File file = new File(sourceDir);
        String basePath = null;
        if (file.isDirectory()) {
            basePath = file.getPath();
        } else {
            basePath = file.getParent();
        }
        zipFile(file, basePath, zos,dirFlag);
        zos.closeEntry();
        zos.close();
    }

    /**
     * 压缩文件
     * @param source
     * @param basePath
     * @param zos
     * @param dirFlag  zip文件中第一层是否包含一级目录，true包含；false没有
     * @throws Exception
     */
    private static void zipFile(File source, String basePath, ZipOutputStream zos, boolean dirFlag) throws Exception {
        File[] files = new File[0];
        if (source.isDirectory()) {
            files = source.listFiles();
        } else {
            files = new File[1];
            files[0] = source;
        }
        File basePathFile = new File(basePath);
        String directoryName = basePathFile.getName();
        String pathName;
        byte[] buf = new byte[1024];
        int length = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                pathName = file.getPath().substring(basePath.length() + 1)+ "/";
                if (dirFlag) {
                    pathName = directoryName + File.separator + pathName;
                }
                zos.putNextEntry(new ZipEntry(pathName));
                zipFile(file, basePath, zos, dirFlag);
            } else {
                pathName = file.getPath().substring(basePath.length() + 1);
                if (dirFlag) {
                    pathName = directoryName + File.separator + pathName;
                }
                InputStream is = Files.newInputStream(file.toPath());
                BufferedInputStream bis = new BufferedInputStream(is);
                zos.putNextEntry(new ZipEntry(pathName));
                while ((length = bis.read(buf)) > 0) {
                    zos.write(buf, 0, length);
                }
                is.close();
            }
        }
    }

}

