package com.tools.security.clean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.tools.security.bean.FileCacheBean;
import com.tools.security.common.SecurityApplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Author：wushuangshuang on 16/10/21 15:55
 * Function：文件操作工具类
 */
public class FileUtils {
    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值

    /**
     * sd卡是否可读写
     *
     * @return
     */
    public static boolean isSDCardAvaiable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 指定路径文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        boolean result = false;
        try {
            File file = new File(filePath);
            result = file.exists();
            file = null;
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 如果filePath表示的是一个文件，则删除文件。如果filePath表示的是一个目录，则删除目录及目录下的子目录和文件
     *
     * @param filePath 文件路径
     */
    public static void delFile(String filePath) {
        if (null == filePath) {
            return;
        }
        File file = new File(filePath);
        if (file != null && file.exists()) { // 文件是否存在
            if (file.isFile()) { // 如果是文件
                file.delete();
            } else if (file.isDirectory()) { // 如果是目录
                File[] subFiles = file.listFiles();
                for (int i = 0; i < subFiles.length; i++) {
                    File subFile = subFiles[i];
                    if (subFile.isDirectory()) {
                        delFile(subFile.getAbsolutePath()); // 递归调用del方法删除子目录和子文件
                    }
                    subFile.delete();
                }
                file.delete();
            }
        }
    }

    //根据传入的文件大小，获取到文件的大小单位
    public static String[] getAppOccupyMemorySizeUint(float size) {
        String[] memorySize = new String[2];
        try {
            if (size < 1024) {
                memorySize[0] = "" + size;
                memorySize[1] = "KB";
            } else if (size < 1024 * 1024) {
                memorySize[0] = size / 1024 + "";
                memorySize[1] = "MB";
            } else {
                memorySize[0] = size / (1024 * 1024) + "";
                memorySize[1] = "GB";
            }
            return memorySize;
        } catch (Exception e) {

        }

        memorySize[0] = "1";
        memorySize[1] = "B";

        return memorySize;
    }


    //根据传入的文件大小，获取到文件的大小单位
    public static String[] getAppOccupyMemorySizeUint2(float size) {
        String[] memorySize = new String[2];
        try {
            if (size < 1024) {
                memorySize[0] = "" + size;
                memorySize[1] = "B";
            } else if (size < 1024 * 1024) {
                memorySize[0] = "" + size / 1024;
                memorySize[1] = "KB";
            } else if (size < 1024 * 1024 * 1024) {
                memorySize[0] = size / 1024 / 1024 + "";
                memorySize[1] = "MB";
            } else {
                memorySize[0] = size / (1024 * 1024 * 1024) + "";
                memorySize[1] = "GB";
            }
            return memorySize;

        } catch (Exception e) {

        }

        memorySize[0] = "1";
        memorySize[1] = "B";

        return memorySize;
    }


    /**
     * 获取文件属性
     *
     * @param fileName
     * @return
     */
    public static String getFileOption(final String fileName) {
        String command = "ls -l " + fileName;
        StringBuffer sbResult = new StringBuffer();
        try {
            Process proc = Runtime.getRuntime().exec(command);
            InputStream input = proc.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String tmpStr = null;
            while ((tmpStr = br.readLine()) != null) {
                sbResult.append(tmpStr);
            }
            if (input != null) {
                input.close();
            }
            if (br != null) {
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sbResult.toString();
    }

    /**
     * 拷贝文件到指定目录
     *
     * @param src
     * @param dstDir 目标目录， 尾部带路径分隔符
     */
    public static void copyFile2Dir(String src, String dstDir) {
        if (!isFileExist(src)) {
            return;
        }
        File srcFile = new File(src);
        String fileName = srcFile.getName();
        copyFile(src, dstDir + fileName);
    }

    /**
     * @param path   ：文件路径
     * @param append ：若存在是否插入原文件
     * @return
     */
    public static File createNewFile(String path, boolean append) {
        if (null == path) {
            return null;
        }
        File newFile = new File(path);
        if (!append) {
            if (newFile.exists()) {
                newFile.delete();
            }
        }
        if (!newFile.exists()) {
            try {
                File parent = newFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                newFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newFile;
    }


    /**
     * 拷贝文件
     *
     * @param src
     * @param dst
     */
    public static void copyFile(String src, String dst) {
        if (!isFileExist(src)) {
            return;
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;

        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(FileUtils.createNewFile(dst, false));

            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e2) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e2) {
                }
            }
        }
    }

    /**
     * 读取res/raw目录下的txt文件
     *
     * @param context
     * @param rawResName   raw文件名称(不带后缀)
     * @param defaultValue 默认值
     * @return
     */
    public static String readRawTxt(Context context, String rawResName, String defaultValue) {
        // 从资源获取流
        Resources res = context.getResources();
        int rawResId = res.getIdentifier(rawResName, "raw", context.getPackageName());
        return readRawTxt(context, rawResId, defaultValue);
    }

    /**
     * 读取res/raw目录下的txt文件
     *
     * @param context
     * @param rawResId     raw文件资源id
     * @param defaultValue 默认值
     * @return
     */
    public static String readRawTxt(Context context, int rawResId, String defaultValue) {
        String rawTxtString = defaultValue;
        if (null == context) {
            return rawTxtString;
        }

        // 从资源获取流
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(rawResId);
        } catch (Exception e) {
            e.printStackTrace();
            return rawTxtString;
        }
        try {
            byte[] buffer = new byte[64];
            int len = is.read(buffer); // 读取流内容
            if (len > 0) {
                rawTxtString = new String(buffer, 0, len).trim(); // 生成字符串
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rawTxtString;
    }

    public static boolean deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    private static int copy(Reader input, Writer output) throws IOException {
        char[] buffer = new char[1024 * 4];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("获取失败");
        }
        return formatFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("获取失败");
        }
        return formatFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Logger.e("文件不存在");
        }

        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double formatFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df
                        .format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 获取apk包的信息：版本号，名称，图标等
     *
     * @param absPath apk包的绝对路径
     */
    public static ApkInfo getApkDrawable(String absPath) {
        try {
            ApkInfo info = new ApkInfo();
            PackageManager pm = SecurityApplication.getInstance().getPackageManager();
            PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
            if (pkgInfo != null) {
                ApplicationInfo appInfo = pkgInfo.applicationInfo;
            /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
                appInfo.sourceDir = absPath;
                appInfo.publicSourceDir = absPath;
                String appName = pm.getApplicationLabel(appInfo).toString();// 得到应用名
                String packageName = appInfo.packageName; // 得到包名
                String version = pkgInfo.versionName; // 得到版本信息
            /* icon1和icon2其实是一样的 */
                Drawable icon = pm.getApplicationIcon(appInfo);// 得到图标信息

                info.setApkName(appName);
                info.setApkPkgName(packageName);
                info.setApkVersion(version);

                if (version.equals(RunningAppsUtil.getInstalledPackageInfo(packageName).versionName)) {
                    info.setIsInstalled(true);
                } else {
                    info.setIsInstalled(false);
                }
                return info;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 清理全部文件
     *
     * @param junks
     */
    public static void freeJunkInfos(List<FileCacheBean> junks) {
        if (junks == null || junks.size() == 0) {
            return;
        }

        for (FileCacheBean info : junks) {
            if (info == null || info.getCachePath() == null) {
                continue;
            }
            File file = new File(info.getCachePath());
            if (file != null && file.exists()) {
                file.delete();
            }
        }
        junks = null;
    }

}
