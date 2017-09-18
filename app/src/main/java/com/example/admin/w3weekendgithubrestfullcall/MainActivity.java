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

        svUserName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
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

                            //Bundle bundle = new Bundle();

                            Gson gson = new Gson();
                            myResponse = gson.fromJson(response, Response.class);

                            Log.d(TAG, "run: " + myResponse.getLogin());

                            //get obj info from github
                            final String userName = myResponse.getLogin();

                            final String repoAmnt = myResponse.getPublicRepos() + "";
                            final String followersAmnt = myResponse.getFollowers() + "";
                            final String followingAmnt = myResponse.getFollowing() + "";

                            Bitmap bm = null;
                            try {
                                InputStream inputStream = new URL(myResponse.getAvatarUrl()).openStream();
                                bm = BitmapFactory.decodeStream(inputStream);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            final Bitmap finalBm = bm;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(userName == null){
                                        Toast.makeText(MainActivity.this, "No User Found", Toast.LENGTH_SHORT).show();
                                        currentURL = "";
                                        ivUserPic.setImageDrawable(getResources().getDrawable(R.drawable.githubicon));
                                        tvUserName.setText("");
                                    }
                                    else {
                                        tvUserName.setText(userName);
                                        btnRepo.setText("Repositories: " + repoAmnt);
                                        btnFollowers.setText("Followers: " + followersAmnt);
                                        btnFollowing.setText("Following: " + followingAmnt);
                                        ivUserPic.setImageBitmap(finalBm);
                                    }
                                }
                            });

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
