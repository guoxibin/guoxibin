package com.example.imagebrower.util;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.imagebrower.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @autohr 安卓开发——郭锡滨
 */

public class BrowserMainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private DogAdapter dogAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;//刷新
    private RecyclerView recyclerView;
    //线程池
    private final int CORE_POOL_SIZE = 50;
    private final int MAXIMUM = 100;
    private final long keepAliveTime = 1L;
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM,
            keepAliveTime,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    private AsyncTask<List<String>, ?, ?> asyncTask;

    //网址上只用了一张图片，运行太多图片，加载图片速度会很慢，故用一张
    private static final String ADDRESS = "http://shibe.online/api/shibes?count=1&httpsUrls=false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_main);

        sendRequestWithHttpURLConnection();//发送网络请求

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));//图片展示列数


        Toolbar toolbar = findViewById(R.id.toolbar);//标题栏
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.draw_layout);//滑动菜单

        NavigationView navigationView = findViewById(R.id.menu_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);//获取刷新实例
        swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);//刷新时圈圈的颜色
        //重新刷新
        swipeRefreshLayout.setOnRefreshListener(this::refreshDogs);

        /**
         * 滑动菜单中navigationViewB部分
         */
        navigationView.setCheckedItem(R.id.information_profile);//首先初始化点击菜单中的profile项
        //navigationView的点击事件
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.information_profile:
                                Toast.makeText(BrowserMainActivity.this, "I wish you a profit",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.information_peoples:
                                Toast.makeText(BrowserMainActivity.this, "Talent boom",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.information_location:
                                Toast.makeText(BrowserMainActivity.this, "Home is where you go",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.information_event:
                                Toast.makeText(BrowserMainActivity.this, "I wish you all the best",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.information_settings:
                                Toast.makeText(BrowserMainActivity.this, "Set up a treasure chest",
                                        Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                });

    }

    /**
     * 发送网络请求
     */
    private void sendRequestWithHttpURLConnection() {

        threadPoolExecutor.execute(() -> {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(ADDRESS);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(8000);
                httpURLConnection.setReadTimeout(8000);

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }

                parseJSONWithJSONObject(response.toString());

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        });
    }

    /**
     * 解析json数据
     * @param jsonData
     */
    private  void parseJSONWithJSONObject(String jsonData) {
        try {
            asyncTask = new SavePictureTask(data -> {
                Log.d("this", "load success and data is null?" + (data == null));
                if (data != null) {
                    dogAdapter = new DogAdapter(data);
                    recyclerView.setAdapter(dogAdapter);
                }
            });
            JSONArray jsonArray = new JSONArray(jsonData);
            List<String> urls = new ArrayList<>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                urls.add(jsonArray.getString(i));
            }
            asyncTask.execute(urls);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下拉刷新
     */
    private void refreshDogs() {
        threadPoolExecutor.execute(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //切换为主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendRequestWithHttpURLConnection();
                        dogAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            });

    }


}