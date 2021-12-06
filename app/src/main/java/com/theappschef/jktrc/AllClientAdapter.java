package com.theappschef.jktrc;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class AllClientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final int EMPTY_VIEW = 77777;
    private final Activity mActivity;
    ArrayList<ClientModel> mList;

    public AllClientAdapter(Activity mActivity, ArrayList<ClientModel> categoryList) {
        this.mActivity = mActivity;
        mList = categoryList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);

        return new MyViewHolder(layoutInflater.inflate(R.layout.item_client, parent, false));

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final MyViewHolder itemView = (MyViewHolder) holder;
//        ArrayList<String> arrayList = mList.get(position);
        ClientModel clientModel=mList.get(position);
        try {


            itemView.client_id.setText(clientModel.getClient_id());
            itemView.name.setText(clientModel.getName());
            itemView.email.setText(clientModel.getEmail());
            itemView.phone.setText(clientModel.getPhone());
            itemView.company.setText(clientModel.getCompany());
            itemView.work_area.setText(clientModel.getWork_area());
            itemView.material.setText(clientModel.getMaterial());
            itemView.specified.setText(clientModel.getSpecified());
        itemView.sample.setText(clientModel.getSample());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() > 0 ? mList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView client_id, name, email, phone, company, work_area, material, specified, sample;


        MyViewHolder(View view) {
            super(view);
            client_id = view.findViewById(R.id.client_id);
            name = view.findViewById(R.id.name);
            email = view.findViewById(R.id.email);
            phone = view.findViewById(R.id.phone);
            company = view.findViewById(R.id.company);
            work_area = view.findViewById(R.id.work_area);
            material = view.findViewById(R.id.material);
            specified = view.findViewById(R.id.specified);
            sample = view.findViewById(R.id.sample);
        }
    }

}



