package com.laurensius_dede_suhardiman.fireearlywarningsystem.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laurensius_dede_suhardiman.fireearlywarningsystem.R;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.model.Client;

import java.util.List;

public class AdapterClient extends RecyclerView.Adapter<AdapterClient.HolderClient> {
    List<Client> listClient;
    public AdapterClient(List<Client> listClient){
        this.listClient =listClient;
    }

    @Override
    public HolderClient onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_client,viewGroup,false);
        HolderClient holderClient = new HolderClient(v);
        return holderClient;
    }

    @Override
    public void onBindViewHolder(HolderClient holderClient,int i){
        holderClient.tvNamaClient.setText("Nama Client : " + listClient.get(i).getNama());
        holderClient.tvCatSuhu.setText("Status suhu : " + listClient.get(i).getCat_suhu());
        holderClient.tvCatAsap.setText("Status asap : " + listClient.get(i).getCat_asap());
        holderClient.tvCatApi.setText("Status api  : " + listClient.get(i).getCat_api());
    }

    @Override
    public int getItemCount(){
        return listClient.size();
    }

    public Client getItem(int position){
        return listClient.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderClient extends  RecyclerView.ViewHolder{
        CardView cvClient;
        TextView tvNamaClient, tvCatSuhu, tvCatAsap, tvCatApi;

        HolderClient(View itemView){
            super(itemView);
            cvClient = (CardView) itemView.findViewById(R.id.cv_client);
            tvNamaClient = (TextView)itemView.findViewById(R.id.tv_nama_client);
            tvCatSuhu = (TextView)itemView.findViewById(R.id.tv_stat_suhu);
            tvCatAsap = (TextView)itemView.findViewById(R.id.tv_stat_asap);
            tvCatApi = (TextView)itemView.findViewById(R.id.tv_stat_api);
        }
    }
}