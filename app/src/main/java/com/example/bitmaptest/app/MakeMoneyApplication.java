package com.example.bitmaptest.app;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.bitmaptest.utils.FileUtil;

import java.io.File;

public class MakeMoneyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String[] perms = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String p : perms) {
            int f = ContextCompat.checkSelfPermission(this, p);
            if (f != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        File file = new File("/mnt/sdcard/dxp2020/");
        FileUtil.deleteFolder(file.getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
