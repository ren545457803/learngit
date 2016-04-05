package com.example.admin.mytest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by admin on 2016-02-15.
 */
public class Test extends AndroidTestCase {
    static String TAG = "Test";
    public static SQLiteDatabase database;
    private final static String DBName = "/data/data/com.example.admin.mytest/databases/" + "com.zto.pdaunity";

    public void test() {

        Log.d(TAG, "--程序开始吧");

        File file = new File(DBName);
        if (!file.exists()) {
            try {
                createDatabase(getContext());
            } catch (IOException e) {
                Log.d(TAG,"--我擦");
                e.printStackTrace();
            }
        }

        database = DBTool.getInstance(getContext())
                .getReadableDatabase();

        search();

    }

    private void createDatabase(Context context) throws IOException {
        InputStream is = context.getAssets().open("com.zto.pdaunity");
        OutputStream os = new FileOutputStream(new File(DBName));

        Log.d(TAG, "开始写");
        byte[] buffer = new byte[1024];
        for (int length = is.read(buffer); length > 0; ) {
            os.write(buffer, 0, length);
            length = is.read(buffer);
        }

        is.close();
        os.close();
    }

    private void search() {
        Cursor cursor = database.query("tmsScanData", null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String five = cursor.getString(5);
            Log.d(TAG, "--five:" + five);
        }
        Log.d(TAG, "--读取结束:");
    }


    private void woca(){
        new woca(){
            @Override
            public void ca() {

            }
        };
    }

    interface woca{
        void ca();
    }
}
