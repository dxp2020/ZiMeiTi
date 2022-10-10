package com.example.bitmaptest.app;

import android.app.Application;

import com.example.bitmaptest.utils.FileUtil;

import java.io.File;

public class MakeMoneyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        File file = new File("/mnt/sdcard/dxp2020/");
        FileUtil.deleteFolder(file.getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
