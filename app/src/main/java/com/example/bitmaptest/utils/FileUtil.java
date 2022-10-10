package com.example.bitmaptest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Zach on 2021/12/31 13:53
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    public static void saveBitmap(Context mContext, String filePath, Bitmap bm) {
//        String path = mContext.getFilesDir() + "/images/";
        Log.d(TAG, "Save Path=" + filePath);

        if (!fileIsExist(new File(filePath).getParent())) {
            Log.d(TAG, "TargetPath isn't exist");
        } else {
            File saveFile = new File(filePath);

            try {
                FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);
                saveImgOut.flush();
                saveImgOut.close();
                Log.d(TAG, "The picture is save to your phone!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean fileIsExist(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return true;
        } else {
            return file.mkdirs();
        }
    }

    public static boolean copyAssetFile(Context context, String assetPath, String path) {
        boolean flag = false;
        int BUFFER = 10240;
        InputStream inputStream = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        byte b[] = null;
        try {
            inputStream = context.getAssets().open(assetPath);
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                if (inputStream != null && inputStream.available() == file.length()) {
                    flag = true;
                } else
                    file.delete();
            }
            if (!flag) {
                in = new BufferedInputStream(inputStream, BUFFER);
                boolean isOK = file.createNewFile();
                if (in != null && isOK) {
                    out = new BufferedOutputStream(new FileOutputStream(file), BUFFER);
                    b = new byte[BUFFER];
                    int read = 0;
                    while ((read = in.read(b)) > 0) {
                        out.write(b, 0, read);
                    }
                    flag = true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param filePath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        if (files == null) {
            return false;
        }
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }


}
