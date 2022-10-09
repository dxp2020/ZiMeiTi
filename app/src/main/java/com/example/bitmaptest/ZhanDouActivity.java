package com.example.bitmaptest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 新中国十大特级战斗英雄
 */
public class ZhanDouActivity extends Activity {

    private static final String TAG = "MainActivity";
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private FrameLayout v_bitmap_view;
    private Button btn_run;
    private Button btn_read;
    private List<String[]> list = new ArrayList<>();
    private int index;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_zhandou);

        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        v_bitmap_view = findViewById(R.id.v_bitmap_view);
        btn_run = findViewById(R.id.btn_run);
        btn_read = findViewById(R.id.btn_read);

        findViewById(R.id.btn_0).setOnClickListener(view -> {
            request();
        });

        btn_1.setOnClickListener(view -> {
            handlerBtn1();
        });
        btn_2.setOnClickListener(view -> {
            handlerBtn2();
        });
        btn_3.setOnClickListener(view -> {
            handlerBtn3();
        });

        btn_read.setOnClickListener(v -> {
            btn_run.setEnabled(true);
            index = 0;
            list.clear();
            readInfo();
        });

        btn_run.setOnClickListener(v -> {
            handlePic(v);
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                index++;
                if (index > list.size()) {
                    return;
                }
                String[] data = list.get(index - 1);
                TextView tv_num = v_bitmap_view.findViewById(R.id.tv_num);
                TextView tv_war = v_bitmap_view.findViewById(R.id.tv_war);
                TextView tv_name = v_bitmap_view.findViewById(R.id.tv_name);
                TextView tv_desc = v_bitmap_view.findViewById(R.id.tv_desc);
                ImageView iv_icon = v_bitmap_view.findViewById(R.id.iv_icon);

                tv_num.setText(String.valueOf(index));
                tv_war.setText(data[1]);
                tv_name.setText(data[2]);
                tv_desc.setText(data[3]);
                iv_icon.setImageResource(getImageResource(index));


                new Thread(() -> savePicture("/mnt/sdcard/dxp2020/" + index + ".png")).start();
            }
        }
    };

    private int getImageResource(int id) {
        String imgName = "a" + id;
        return getResources().getIdentifier(imgName, "mipmap", getPackageName());
    }

    private void handlePic(View view) {
        index = 0;
        view.setEnabled(false);
        handler.sendEmptyMessage(0);
    }

    public void readInfo() {
        InputStream inputStream = null;
        InputStreamReader isReader = null;
        BufferedReader reader = null;
        try {
            //获取文件中的内容
            inputStream = getResources().openRawResource(R.raw.version1);
            //将文件中的字节转换为字符
            isReader = new InputStreamReader(inputStream, "UTF-8");
            //使用bufferReader去读取字符
            reader = new BufferedReader(isReader);
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] arr = line.split("&");
                list.add(arr);
            }
            Log.i(TAG, "list-->" + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                isReader.close();
                inputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public void savePicture(String name) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = createBitmapFromView(v_bitmap_view);

        File file = new File(name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            handler.sendEmptyMessageDelayed(0, 500);
        }
    }

    public static Bitmap createBitmapFromView(View view) {
        //是ImageView直接获取
        if (view instanceof ImageView) {
            Drawable drawable = ((ImageView) view).getDrawable();
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        }
        view.clearFocus();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            canvas.setBitmap(null);
        }
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void request() {
        String[] perms = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String p : perms) {
            int f = ContextCompat.checkSelfPermission(this, p);
            Log.d("---", String.format("%s - %d", p, f));
            if (f != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(perms, 0XCF);
                break;
            }
        }
    }

    private void handlerBtn1() {
        new Thread(() -> {
            Bitmap bitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
            final int w = bitmap.getWidth();
            final int h = bitmap.getHeight();
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    bitmap.setPixel(i, j, Color.argb(0, 0, 0, 0));
                }
            }
            saveFile(bitmap, "/mnt/sdcard/dxp2020/", "纯透明.png");
        }).start();
    }

    private void handlerBtn2() {
        new Thread(() -> {
            Bitmap bitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
            final int w = bitmap.getWidth();
            final int h = bitmap.getHeight();
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    if ((i % 2 == 0 && j % 2 == 0) || ((i % 2 == 1 && j % 2 == 1))) {
                        bitmap.setPixel(i, j, Color.argb(0xBF, 255, 255, 255));
                    } else {
                        bitmap.setPixel(i, j, Color.argb(0, 0, 0, 0));
                    }
                }
            }
            saveFile(bitmap, "/mnt/sdcard/dxp2020/", "相隔像素透明.png");
        }).start();
    }

    public void handlerBtn3() {
        new Thread(() -> {
            Bitmap bitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
            final int w = bitmap.getWidth();
            final int h = bitmap.getHeight();
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    if ((i % 5 == 0 && j % 5 == 0)) {
                        bitmap.setPixel(i, j, Color.argb(0xFF, 255, 255, 255));
                    } else {
                        bitmap.setPixel(i, j, Color.argb(0, 0, 0, 0));
                    }
                }
            }
            saveFile(bitmap, "/mnt/sdcard/dxp2020/", "相隔像素透明-加大.png");
        }).start();
    }

    /**
     * 将Bitmap转换成文件
     */
    public static File saveFile(Bitmap bm, String path, String fileName) {
        File myCaptureFile = null;
        try {
            File dirFile = new File(path);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
            myCaptureFile = new File(path, fileName);
            myCaptureFile.createNewFile();

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myCaptureFile;
    }

}