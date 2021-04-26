package test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 异步线程下载图片
 */
public class StorePicture extends AsyncTask<String,Void, Bitmap> {

    private Context context;
    private String url;
    private String path;
    private int imageName;

    public static InputStream inputstream = null;


    public StorePicture(Context context, String url, String path, int imageName) {
        this.context = context;
        this.url = url;
        this.path = path;
        this.imageName = imageName;
    }

    public StorePicture(String url) {
        this.url = url;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap = null;
        bitmap = GetImageInputStream(url);
        return bitmap;
    }

    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) {
            SaveImage(context,bitmap,path,imageName);
        }
    }

    /**
     * 获取网络图片
     *
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public static Bitmap GetImageInputStream(String imageurl) {
        URL url;
        HttpURLConnection httpURLConnection = null;
        Bitmap bitmap = null;

        try {
            url = new URL(imageurl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(8000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);
            inputstream = httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputstream);
            inputstream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存位图到本地
     *
     * @param context
     * @param bitmap
     * @param pathDir 本地路径
     */
    public static void SaveImage(Context context,Bitmap bitmap,String pathDir,int imageName) {
        if (bitmap != null) {
            File file = new File(pathDir);
            FileOutputStream fileOutputStream = null;
            if (!file.exists()) {
                file.mkdir();
            }
            try {
                fileOutputStream = new FileOutputStream(pathDir + "/" +imageName + ".ipg" );
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
