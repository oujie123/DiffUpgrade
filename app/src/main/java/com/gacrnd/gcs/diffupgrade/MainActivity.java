package com.gacrnd.gcs.diffupgrade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import dalvik.system.PathClassLoader;

/**
 *
 *  在https://www.sweetscape.com/010editor/repository/templates/中去下载DEX.bt模板，
 *  010 Editor就可以参看dex文件
 */
public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testDiff();
    }

    public void testDiff() {
        File file = copy4Assets();
        PathClassLoader classLoader = new PathClassLoader(file.getAbsolutePath(), getClassLoader());
        try {
            Class<?> test = classLoader.loadClass("Test");
            Method main = test.getDeclaredMethod("test");
            main.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File copy4Assets() {
        File externalFilesDir = getExternalFilesDir("");
        File dexFile = new File(externalFilesDir, "new.dex");
        if (!dexFile.exists()) {
            BufferedInputStream is = null;
            BufferedOutputStream os = null;
            try {
                is = new BufferedInputStream(getAssets().open("new2.dex"));
                os = new BufferedOutputStream(new FileOutputStream(dexFile));
                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dexFile;

    }

    public void patch(View view) {
        File newFile = new File(getExternalFilesDir("apk"),"app.apk");
        File patch = new File(getExternalFilesDir("apk"),"patch");
        if (patch.exists()){
            int result = DiffUpgradeUtils.patch(getApplicationInfo().sourceDir,newFile.getAbsolutePath(),patch.getAbsolutePath());
            if (result == 0){
                installApk(newFile);
            }
        }
    }

    private void installApk(File newFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 7.0+以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", newFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(newFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }
}
