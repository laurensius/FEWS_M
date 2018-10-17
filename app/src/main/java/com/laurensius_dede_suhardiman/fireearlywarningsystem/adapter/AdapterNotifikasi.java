package com.laurensius_dede_suhardiman.fireearlywarningsystem.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laurensius_dede_suhardiman.fireearlywarningsystem.R;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.model.Notif;

import java.util.List;

public class AdapterNotifikasi extends RecyclerView.Adapter<AdapterNotifikasi.HolderNotif> {
    List<Notif> listNotif;
    public AdapterNotifikasi(List<Notif> listNotif){
        this.listNotif =listNotif;
    }

    @Override
    public HolderNotif onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_notif,viewGroup,false);
        HolderNotif holderNotif = new HolderNotif(v);
        return holderNotif;
    }

    @Override
    public void onBindViewHolder(HolderNotif holderNotif,int i){
        holderNotif.tvNama.setText("Nama Pemilik : " + listNotif.get(i).getNama());
        holderNotif.tvNamaToko.setText("Nama Toko : " + listNotif.get(i).getNama_toko());
        holderNotif.tvAlamatToko.setText("Lokasi : " + listNotif.get(i).getAlamat_toko());
        holderNotif.tvNotif.setText("Notifikasi : " + listNotif.get(i).getNotifikasi());
    }

    @Override
    public int getItemCount(){
        return listNotif.size();
    }

    public Notif getItem(int position){
        return listNotif.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderNotif extends  RecyclerView.ViewHolder{
        CardView cvNotif;
        TextView tvNama, tvNamaToko, tvAlamatToko, tvNotif;

        HolderNotif(View itemView){
            super(itemView);
            cvNotif = (CardView) itemView.findViewById(R.id.cv_client);
            tvNama= (TextView)itemView.findViewById(R.id.tv_nama);
            tvNamaToko= (TextView)itemView.findViewById(R.id.tv_nama_toko);
            tvAlamatToko = (TextView)itemView.findViewById(R.id.tv_alamat_toko);
            tvNotif = (TextView)itemView.findViewById(R.id.tv_notifikasi);
        }
    }
}