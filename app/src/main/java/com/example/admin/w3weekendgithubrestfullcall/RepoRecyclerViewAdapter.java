package com.example.admin.w3weekendgithubrestfullcall;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.w3weekendgithubrestfullcall.RepoModel.RepoResponse;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * TODO: Replace the implementation with code for your data type.
 */
public class RepoRecyclerViewAdapter extends RecyclerView.Adapter<RepoRecyclerViewAdapter.ViewHolder> {

    private final List<RepoResponse> mValues;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public RepoRecyclerViewAdapter(Context context, List<RepoResponse> items) {
        mValues = items;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_repo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvRepoName.setText(mValues.get(position).getName());
        holder.tvRepoDesc.setText(mValues.get(position).getDescription());
        String time = mValues.get(position).getUpdatedAt();
        time = time.substring(0,10);
        Log.d("TAG", "onBindViewHolder: " + time);
        try {
            Date date1 = df.parse(time);
            Log.d("TAG", "onBindViewHolder: " + date1);
            String result = df.format(date1);
            holder.tvRepoUpdate.setText(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }



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
        public final TextView tvRepoName;
        public final TextView tvRepoDesc;
        public final TextView tvRepoUpdate;
        public RepoResponse mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvRepoName = (TextView) view.findViewById(R.id.tvRepoName);
            tvRepoDesc = (TextView) view.findViewById(R.id.tvRepoDescription);
            tvRepoUpdate = (TextView) view.findViewById(R.id.tvRepoLastUpdate);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.getName() + "'";
        }
    }
}
