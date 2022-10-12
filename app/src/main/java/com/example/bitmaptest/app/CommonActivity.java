package com.example.bitmaptest.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bitmaptest.R;

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

public class CommonActivity extends Activity {

    private static final String TAG = "CommonActivity";
    private FrameLayout v_bitmap_view;
    private Button btn_run;
    private Button btn_export;
    private Button btn_switch;
    private TextView tv_fengmian;
    private List<String[]> list = new ArrayList<>();
    private int index;
    private int templateIndex = -1;
    private String title;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);

        v_bitmap_view = findViewById(R.id.v_bitmap_view);
        btn_run = findViewById(R.id.btn_run);
        btn_export = findViewById(R.id.btn_export);
        tv_fengmian = findViewById(R.id.tv_fengmian);
        btn_switch = findViewById(R.id.btn_switch);

        findViewById(R.id.btn_0).setOnClickListener(view -> {
            request();
        });

        btn_export.setOnClickListener(view -> {
            tv_fengmian.setText(title);
            exportCover();
        });

        btn_run.setOnClickListener(v -> {
            handlePic(v);
        });

        btn_switch.setOnClickListener(v -> {
            btn_run.setEnabled(true);
            index = 0;
            list.clear();
            readInfo();

            templateIndex++;
            templateIndex %= 4;
            View view = null;
            switch (templateIndex) {
                case 0:
                    view = View.inflate(CommonActivity.this, R.layout.layout_common_item_1, null);
                    break;
                case 1:
                    view = View.inflate(CommonActivity.this, R.layout.layout_common_item_2, null);
                    break;
                case 2:
                    view = View.inflate(CommonActivity.this, R.layout.layout_common_item_3, null);
                    break;
                case 3:
                    view = View.inflate(CommonActivity.this, R.layout.layout_common_item_4, null);
                    break;
            }
            if (view != null) {
                v_bitmap_view.removeAllViews();
                v_bitmap_view.addView(view);
            }
        });

        new Handler().postDelayed(() -> btn_switch.performClick(), 500);
    }

    private void exportCover() {
        v_bitmap_view.setVisibility(View.GONE);
        tv_fengmian.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                Thread.sleep(2000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = createBitmapFromView(tv_fengmian);

            File file = new File("/mnt/sdcard/dxp2020/0.png");
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
                handler.sendEmptyMessageDelayed(1, 500);
            }
        }).start();
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
                TextView tv_chen_wei = v_bitmap_view.findViewById(R.id.tv_chen_wei);
                TextView tv_line1 = v_bitmap_view.findViewById(R.id.tv_line1);
                TextView tv_line2 = v_bitmap_view.findViewById(R.id.tv_line2);
                TextView tv_line3 = v_bitmap_view.findViewById(R.id.tv_line3);
                ImageView iv_icon = v_bitmap_view.findViewById(R.id.iv_icon);

                iv_icon.setImageResource(getImageResource(data[0]));
                tv_num.setText(String.valueOf(index));
                if (TextUtils.isEmpty(data[3])) {
                    tv_chen_wei.setVisibility(View.GONE);
                } else {
                    tv_chen_wei.setVisibility(View.VISIBLE);
                    tv_chen_wei.setText(data[3]);
                }
                tv_line1.setText(data[1]);
                tv_line2.setText(data[2].replace("\\n", "\n"));
                tv_line3.setText(data[4]);


                new Thread(() -> savePicture("/mnt/sdcard/dxp2020/" + index + ".png")).start();
            } else if (msg.what == 1) {
                tv_fengmian.setText("不朽中华魂!");
                new Thread(() -> {
                    try {
                        Thread.sleep(2000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = createBitmapFromView(tv_fengmian);

                    File file = new File("/mnt/sdcard/dxp2020/end.png");
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
                        handler.sendEmptyMessageDelayed(2, 500);
                    }
                }).start();
            } else if (msg.what == 2) {
                v_bitmap_view.setVisibility(View.VISIBLE);
                tv_fengmian.setVisibility(View.GONE);
            }
        }
    };

    private int getImageResource(String imgName) {
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
        title = null;
        try {
            //获取文件中的内容
            inputStream = getResources().openRawResource(R.raw.version1);
            //将文件中的字节转换为字符
            isReader = new InputStreamReader(inputStream, "UTF-8");
            //使用bufferReader去读取字符
            reader = new BufferedReader(isReader);
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (title == null) {
                    title = line;
                } else {
                    String[] arr = line.split("&");
                    list.add(arr);
                }
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
