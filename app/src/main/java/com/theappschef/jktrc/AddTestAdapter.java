package com.theappschef.jktrc;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class AddTestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final int EMPTY_VIEW = 77777;
    private final Activity mActivity;
    ArrayList<ArrayList<String>> mList;

    public AddTestAdapter(Activity mActivity, ArrayList<ArrayList<String>> categoryList) {
        this.mActivity = mActivity;
        mList = categoryList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);

            return new MyViewHolder(layoutInflater.inflate(R.layout.item_test, parent, false));

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            final MyViewHolder itemView = (MyViewHolder) holder;
            ArrayList<String> arrayList=mList.get(position);
            itemView.material.setText(arrayList.get(0));
        itemView.testName.setText(arrayList.get(1));
        itemView.testPerformed.setText(arrayList.get(2));
        itemView.status.setText(arrayList.get(3));
        itemView.edit.setOnClickListener(v -> {

            ((DashboardActivity) mActivity).showUpdateStatus(position);
        });
        }


    @Override
    public int getItemCount() {
        return mList.size() > 0 ? mList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(mList.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView material,testName,testPerformed,status;
        ImageView edit;

        MyViewHolder(View view) {
            super(view);
            material = (TextView) itemView.findViewById(R.id.material);
            testName =  itemView.findViewById(R.id.testName);
            testPerformed =  itemView.findViewById(R.id.testPerformed);
            status=  itemView.findViewById(R.id.status);
            edit=itemView.findViewById(R.id.edit);
        }
    }

}



