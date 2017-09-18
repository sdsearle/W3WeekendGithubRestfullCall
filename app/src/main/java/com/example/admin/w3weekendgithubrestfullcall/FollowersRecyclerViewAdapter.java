package com.example.admin.w3weekendgithubrestfullcall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.admin.w3weekendgithubrestfullcall.FollowingFollowersModel.Response;
import com.example.admin.w3weekendgithubrestfullcall.RepoModel.RepoResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by admin on 9/16/2017.
 */

public class FollowersRecyclerViewAdapter extends RecyclerView.Adapter<FollowersRecyclerViewAdapter.ViewHolder> {

    private final List<Response> mValues;


    public FollowersRecyclerViewAdapter(Context context, List<Response> items) {
        mValues = items;

    }

    @Override
    public FollowersRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_followers_following, parent, false);
        return new FollowersRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FollowersRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.tvUserName.setText(mValues.get(position).getLogin());
        holder.ivUserPic.setVisibility(View.INVISIBLE);
        holder.pbUserPic.setVisibility(View.VISIBLE);

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                byte[] byteArray = message.getData().getByteArray("UserPic");
                Bitmap bm = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                holder.ivUserPic.setImageBitmap(bm);
                holder.ivUserPic.setVisibility(View.VISIBLE);
                holder.pbUserPic.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = null;
                try {
                    InputStream inputStream = new URL(mValues.get(position).getAvatarUrl()).openStream();
                    bm = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bundle bundle = new Bundle();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG,100,baos);
                bundle.putByteArray("UserPic",baos.toByteArray());

                Message message = new Message();
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();


        /*holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvUserName;
        public final ImageView ivUserPic;
        private final ProgressBar pbUserPic;
        public Response mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            ivUserPic = view.findViewById(R.id.ivUserPic);
            pbUserPic = view.findViewById(R.id.pbUserPic);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.getLogin() + "'";
        }
    }
}