package com.example.admin.w3weekendgithubrestfullcall;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.admin.w3weekendgithubrestfullcall.FollowingFollowersModel.Response;

import java.util.List;

/**
 * Created by admin on 9/16/2017.
 */

public class FollowersRecyclerViewAdapter extends RecyclerView.Adapter<FollowersRecyclerViewAdapter.ViewHolder> {

    private final List<Response> mValues;
    Context context;


    public FollowersRecyclerViewAdapter(Context context, List<Response> items) {
        mValues = items;
        this.context = context;
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
        holder.ivUserPic.setVisibility(View.VISIBLE);
        Glide.with(context).load(mValues.get(position).getAvatarUrl()).into(holder.ivUserPic);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvUserName;
        public final ImageView ivUserPic;
        public Response mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            ivUserPic = view.findViewById(R.id.ivUserPic);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.getLogin() + "'";
        }
    }
}