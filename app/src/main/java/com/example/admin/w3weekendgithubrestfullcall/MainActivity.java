package com.example.admin.w3weekendgithubrestfullcall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.w3weekendgithubrestfullcall.UserModel.Response;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "https://api.github.com/users/";
    public static final String TAG = "TAG";
    public String currentURL = "";

    TextView tvUserName;
    ImageView ivUserPic;
    SearchView svUserName;
    Button btnRepo, btnFollowers, btnFollowing;

    private OkHttpClient client;
    private Response myResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUserName = (TextView) findViewById(R.id.tvUserName);
        ivUserPic = (ImageView) findViewById(R.id.ivUserPic);

        svUserName = (SearchView) findViewById(R.id.svUserName);

        btnRepo = (Button) findViewById(R.id.btnRepo);
        btnFollowers = (Button) findViewById(R.id.btnFollowers);
        btnFollowing = (Button) findViewById(R.id.btnFollowing);


        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                String s = message.getData().getString("UserName");
                if(s == null){
                    Toast.makeText(MainActivity.this, "No User Found", Toast.LENGTH_SHORT).show();
                    currentURL = "";
                    ivUserPic.setImageDrawable(getResources().getDrawable(R.drawable.githubicon));
                    tvUserName.setText("");
                    return  false;
                }
                tvUserName.setText(s);
                String rAmnt = message.getData().getString("repoAmnt");
                String followersAmnt = message.getData().getString("followersAmnt");;
                String followingAmnt = message.getData().getString("followingAmnt");;
                btnRepo.setText("Repositories: " + rAmnt);
                btnFollowers.setText("Followers: " + followersAmnt);
                btnFollowing.setText("Following: " + followingAmnt);

                byte[] byteArray = message.getData().getByteArray("UserPic");
                if(byteArray != null) {
                    Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    ivUserPic.setImageBitmap(bm);
                }
                return false;
            }
        });
        svUserName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                client = new OkHttpClient();
                currentURL = BASE_URL + s;

                final Request request = new Request.Builder()
                        .url(currentURL).build();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String response = client.newCall(request).execute().body().string();
                            Log.d(TAG, "run: " + response);

                            Bundle bundle = new Bundle();

                            Gson gson = new Gson();
                            myResponse = gson.fromJson(response, Response.class);

                            Log.d(TAG, "run: " + myResponse.getLogin());

                            //get obj info from github
                            String userName = myResponse.getLogin();
                            bundle.putString("UserName",userName);

                            String repoAmnt = myResponse.getPublicRepos() + "";
                            String followersAmnt = myResponse.getFollowers() + "";
                            String followingAmnt = myResponse.getFollowing() + "";

                            bundle.putString("repoAmnt",repoAmnt);
                            bundle.putString("followersAmnt",followersAmnt);
                            bundle.putString("followingAmnt",followingAmnt);

                            //bundle.putString("UserPic", myResponse.getAvatarUrl());
                            Bitmap bm = null;
                            try {
                                InputStream inputStream = new URL(myResponse.getAvatarUrl()).openStream();
                                bm = BitmapFactory.decodeStream(inputStream);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            if(bm != null) {
                                bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                bundle.putByteArray("UserPic", baos.toByteArray());
                            }

                            //send message
                            Message message = new Message();
                            message.setData(bundle);
                            handler.sendMessage(message);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });




    }

    public void toActivity(View view) {
        if(currentURL != "") {
            switch (view.getId()) {

                case R.id.btnRepo:
                    Intent repoIntent = new Intent(this, RepoActivity.class);
                    repoIntent.putExtra("BASE_URL", currentURL + "/repos");
                    startActivity(repoIntent);
                    break;

                case R.id.btnFollowers:
                    Intent followersIntent = new Intent(this, FollowersFollowingActivity.class);
                    followersIntent.putExtra("BASE_URL", currentURL + "/followers");
                    followersIntent.putExtra("FOLLOW", "/followers");
                    startActivity(followersIntent);
                    break;

                case R.id.btnFollowing:
                    Intent followingIntent = new Intent(this, FollowersFollowingActivity.class);
                    followingIntent.putExtra("BASE_URL", currentURL + "/following");
                    followingIntent.putExtra("FOLLOW", "/following");
                    startActivity(followingIntent);
                    break;

            }
        }
        else{
            Toast.makeText(this, "Please provide a user", Toast.LENGTH_SHORT).show();
        }
    }
}
