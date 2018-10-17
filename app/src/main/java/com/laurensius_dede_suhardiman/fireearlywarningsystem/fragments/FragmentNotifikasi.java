package com.laurensius_dede_suhardiman.fireearlywarningsystem.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.FireApp;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.R;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.adapter.AdapterNotifikasi;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.appcontroller.AppController;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.model.Notif;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentNotifikasi extends Fragment {

    private TextView tvNotifikasi;
    private LinearLayout llNotifUser, llNotifAdmin, llNotifAdminNull;
    private ImageView ivNotifikasi;

    private RecyclerView rvNotif;
    private AdapterNotifikasi adapterNotif  = null;
    RecyclerView.LayoutManager mLayoutManager;
    List<Notif> listNotif = new ArrayList<>();


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;



    Timer timer = new Timer();

    int found = 0;


    public FragmentNotifikasi() {}

    public static FragmentNotifikasi newInstance() {
        FragmentNotifikasi fragment = new FragmentNotifikasi();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterNotifikasi = inflater.inflate(R.layout.fragment_notifikasi, container, false);
        llNotifUser= (LinearLayout)inflaterNotifikasi.findViewById(R.id.ll_notif_user);
        ivNotifikasi = (ImageView)inflaterNotifikasi.findViewById(R.id.iv_notifikasi);
        tvNotifikasi = (TextView)inflaterNotifikasi.findViewById(R.id.tv_notifikasi);
        llNotifAdmin = (LinearLayout)inflaterNotifikasi.findViewById(R.id.ll_notif_admin);
        rvNotif = (RecyclerView)inflaterNotifikasi.findViewById(R.id.rv_notif);
        llNotifAdminNull = (LinearLayout)inflaterNotifikasi.findViewById(R.id.ll_notif_admin_null);
        return inflaterNotifikasi;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("APP_FIRE", 0);
        editorPreferences = sharedPreferences.edit();

        if(FireApp.tipe_user.equals("0")){
            llNotifUser.setVisibility(View.GONE);
            llNotifAdmin.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            TimerTask doDataToUI = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                loadNotifikasi();
                            } catch (Exception e) {
                            }
                        }
                    });
                }
            };
            timer.schedule(doDataToUI, 0, 5000);
        }else
        if(FireApp.tipe_user.equals("1")){
            llNotifUser.setVisibility(View.VISIBLE);
            llNotifAdmin.setVisibility(View.GONE);
            final Handler handler = new Handler();
            TimerTask doDataToUI = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {

                                if(!FireApp.cat_suhu.equals("Normal") || !FireApp.cat_asap.equals("Normal") || !FireApp.cat_api.equals("Normal")  ){
                                    ivNotifikasi.setImageResource(R.mipmap.warning);

                                    String notif_suhu = "";
                                    String notif_asap = "";
                                    String notif_api = "";
                                    if(!FireApp.cat_suhu.equals("Normal")){
                                        notif_suhu = String.valueOf(" Kondisi suhu diatas normal, suhu saat ini ").concat(String.valueOf(FireApp.val_suhu)).concat(" °C.");
                                    }

                                    if(!FireApp.cat_asap.equals("Normal")){
                                        notif_asap = String.valueOf(" Terdeteksi asap di toko / kios Anda.");
                                    }

                                    if(!FireApp.cat_api.equals("Normal")){
                                        notif_api= String.valueOf(" Terdeteksi api di toko / kios Anda.");
                                    }

                                    tvNotifikasi.setText(notif_suhu.concat(notif_asap).concat(notif_api));

                                }else{
                                    ivNotifikasi.setImageResource(R.mipmap.normal);
                                    tvNotifikasi.setText(getResources().getString(R.string.notif_normal));
                                }
                            } catch (Exception e) {
                            }
                        }
                    });
                }
            };
            timer.schedule(doDataToUI, 0, 5000);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        timer.cancel();
    }

    private void loadNotifikasi(){
        Random r = new Random();
        String angka_random = String.valueOf(r.nextInt(9999999 - 11111) + 11111);
        String tag_req_notif_user= "NTF_USR";
        String url = getResources().getString(R.string.api_url_admin_monitorig)
                .concat(angka_random).concat("/");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response On Fire: ", response.toString());
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("FIRE ERROR", error.toString());
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_notif_user);
    }

    private void parseData(JSONObject data){
        try {
            JSONArray monitoring = data.getJSONArray("monitoring");
            if(monitoring.length() > 0){
                listNotif.clear();
                rvNotif.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                rvNotif.setLayoutManager(mLayoutManager);
                adapterNotif= new AdapterNotifikasi(listNotif);
                adapterNotif.notifyDataSetChanged();
                rvNotif.setAdapter(adapterNotif);

                for(int x=0;x<monitoring.length();x++){

                    try{
                        if(!monitoring.getJSONObject(x).getString("cat_suhu").equals("Normal") ||
                                !monitoring.getJSONObject(x).getString("cat_asap").equals("Normal") ||
                                !monitoring.getJSONObject(x).getString("cat_api").equals("Normal")  ) {

                            String notif_suhu = "";
                            String notif_asap = "";
                            String notif_api = "";
                            if(!monitoring.getJSONObject(x).getString("cat_suhu").equals("Normal")){
                                notif_suhu = String.valueOf(" Kondisi suhu diatas normal, suhu saat ini ").concat(monitoring.getJSONObject(x).getString("val_suhu")).concat(" °C.");
                            }

                            if(!monitoring.getJSONObject(x).getString("cat_asap").equals("Normal")){
                                notif_asap = String.valueOf(" Terdeteksi asap di toko / kios Anda.");
                            }

                            if(!monitoring.getJSONObject(x).getString("cat_api").equals("Normal")){
                                notif_api= String.valueOf(" Terdeteksi api di toko / kios Anda.");
                            }

                            String str_notif = notif_suhu.concat(notif_asap).concat(notif_api);

                            listNotif.add(
                                    new Notif(
                                            monitoring.getJSONObject(x).getString("id"),
                                            monitoring.getJSONObject(x).getString("nama"),
                                            monitoring.getJSONObject(x).getString("nama_toko"),
                                            monitoring.getJSONObject(x).getString("alamat_toko"),
                                            str_notif
                                    )
                            );

                            found++;
                        }

                    }catch (Exception e){

                    }


                    if(found > 0){
                        llNotifAdminNull.setVisibility(View.GONE);
                        rvNotif.setVisibility(View.VISIBLE);
                    }else{
                        llNotifAdminNull.setVisibility(View.VISIBLE);
                        rvNotif.setVisibility(View.GONE);
                    }


                }
                adapterNotif.notifyDataSetChanged();
            }
        }catch (JSONException e){
            Log.d("Fire Notification", e.getMessage().toString());
        }
    }


}
