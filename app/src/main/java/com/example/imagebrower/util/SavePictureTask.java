package com.example.imagebrower.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class SavePictureTask extends AsyncTask<List<String>, Void, List<Bitmap>> {

    public final OnResultCallback<List<Bitmap>> onResultCallback;
    private MyDatabaseHelper myDatabaseHelper;

    public SavePictureTask(OnResultCallback<List<Bitmap>> onResultCallback) {
        this.onResultCallback = onResultCallback;
    }

    /**
     * 访问照片网址，
     * @param params
     * @return bitmap list
     */

    @Override
    protected List<Bitmap> doInBackground(List<String>... params) {
        //要访问的图片链接
        URL pictureUrl;
        List<Bitmap> results = new ArrayList<>();
        List<String> urls = params[0];

        myDatabaseHelper = new MyDatabaseHelper
                (MyApplication.getContext(),"PictureURL.db",null,1);
        SQLiteDatabase sqLiteDatabase =  myDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (int i = 0; i < urls.size(); i++) {
            try {
                pictureUrl = new URL(urls.get(i));
                //建立网络连接
                URLConnection urlConnection = pictureUrl.openConnection();
                InputStream inputstream = urlConnection.getInputStream();
                //中转站，现将图片数据放到byteArrayoutStream中
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputstream.read(buf)) != -1) {
                    byteArrayOutputStream.write(buf, 0, len);
                }
                inputstream.close();

                byte[] imageData = byteArrayOutputStream.toByteArray();

                //存储在本地
                FileOutputStream fileOutputStream = MyApplication.getContext().
                        openFileOutput(ParseUrlUtil.getImageNameFromUrl(urls.get(i)), Context.MODE_APPEND);
                fileOutputStream.write(imageData);

                //图片转化为bitmap，并返回
                results.add(BitmapFactory.decodeStream(new ByteArrayInputStream(imageData)));

                fileOutputStream.close();
                byteArrayOutputStream.close();

                //数据库存储图片存储路径
                contentValues.put("pictureurl",
                        "/data/data/com.example.imagebrower/files/" + ParseUrlUtil.getImageNameFromUrl(urls.get(i)));

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        //增加数据
        sqLiteDatabase.insert("PictureURL",null, contentValues);

        return results;
    }

    @Override
    protected void onPostExecute(List<Bitmap> bitmapResult) {
        super.onPostExecute(bitmapResult);
        onResultCallback.onResult(bitmapResult);
    }


    //回调接口
    public interface OnResultCallback<T> {
        void onResult(@Nullable T data);
    }

}
