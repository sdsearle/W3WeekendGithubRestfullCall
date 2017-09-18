package com.example.admin.w3weekendgithubrestfullcall;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.admin.w3weekendgithubrestfullcall.FollowingFollowersModel.Response;
import com.example.admin.w3weekendgithubrestfullcall.RepoModel.RepoResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FollowersFollowingActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    Handler handler;
    private OkHttpClient client;
    public List<Response> myResponse;
    private LinearLayoutManager layoutManager;
    private DefaultItemAnimator itemAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_following);

        Intent intent = getIntent();
        String repoURL = intent.getStringExtra("BASE_URL");

        client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(repoURL).build();

        /*final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {

                List<Response> repolist = (List<Response>) message.getData().getSerializable("RepoArray");
                RecyclerView rvList = (RecyclerView) findViewById(R.id.rvFollowList);
                rvList.setAdapter(new FollowersRecyclerViewAdapter(getBaseContext(), myResponse));
                layoutManager = new LinearLayoutManager(getBaseContext());
                itemAnimator = new DefaultItemAnimator();
                rvList.setLayoutManager(layoutManager);
                rvList.setItemAnimator(itemAnimator);

                return false;
            }
        });*/

        new Thread(new Runnable() {


            @Override
            public void run() {
                try {
                    String response = client.newCall(request).execute().body().string();
                    Log.d(TAG, "run: " + response);

                    //Bundle bundle = new Bundle();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Response>>() {
                    }.getType();
                    myResponse = (List<Response>) gson.fromJson(response, listType);

                    Log.d(TAG, "run: " + myResponse);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            RecyclerView rvList = (RecyclerView) findViewById(R.id.rvFollowList);
                            rvList.setAdapter(new FollowersRecyclerViewAdapter(getBaseContext(), myResponse));
                            layoutManager = new LinearLayoutManager(getBaseContext());
                            itemAnimator = new DefaultItemAnimator();
                            rvList.setLayoutManager(layoutManager);
                            rvList.setItemAnimator(itemAnimator);
                        }
                    });

                    /*bundle.putSerializable("FollowArray", (Serializable) myResponse);

                    //send message
                    Message message = new Message();
                    message.setData(bundle);
                    handler.sendMessage(message);*/


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}

