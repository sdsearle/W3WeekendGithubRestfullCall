package com.example.admin.w3weekendgithubrestfullcall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.admin.w3weekendgithubrestfullcall.RepoModel.RepoResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RepoActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    Handler handler;
    private OkHttpClient client;
    public List<RepoResponse> myResponse;
    private LinearLayoutManager layoutManager;
    private DefaultItemAnimator itemAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

        Intent intent = getIntent();
        String repoURL = intent.getStringExtra("BASE_URL");

        client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(repoURL).build();

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {

                List<RepoResponse> repolist = (List<RepoResponse>) message.getData().getSerializable("RepoArray");
                RecyclerView rvList = (RecyclerView) findViewById(R.id.rvRepoList);
                rvList.setAdapter(new RepoRecyclerViewAdapter(getBaseContext(), myResponse));
                layoutManager = new LinearLayoutManager(getBaseContext());
                itemAnimator = new DefaultItemAnimator();
                rvList.setLayoutManager(layoutManager);
                rvList
                        .setItemAnimator(itemAnimator);

                return false;
            }
        });

        new Thread(new Runnable() {


            @Override
            public void run() {
                try {
                    String response = client.newCall(request).execute().body().string();
                    Log.d(TAG, "run: " + response);

                    Bundle bundle = new Bundle();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<RepoResponse>>(){}.getType();
                    myResponse = (List<RepoResponse>) gson.fromJson(response, listType);

                    Log.d(TAG, "run: " + myResponse.get(1));

                    bundle.putSerializable("RepoArray", (Serializable) myResponse);

                    //get obj info from github
                   /* String repoName = myResponse.getName();
                    bundle.putStringArrayList("RepoName",repoName);

                    String repoDescription = myResponse.getDescription();
                    bundle.putString("RepoDescription",repoDescription);

                    String repoLastUpdate = myResponse.getUpdatedAt();*/



                    //send message
                    Message message = new Message();
                    message.setData(bundle);
                    handler.sendMessage(message);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
