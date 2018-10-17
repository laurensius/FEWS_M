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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.FireApp;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.R;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.adapter.AdapterClient;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.appcontroller.AppController;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.model.Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentMonitoring extends Fragment {

    private TextView tvSuhu, tvAsap, tvApi;
    private LinearLayout llMonUser, llMonAdmin;

    private RecyclerView rvClient;
    private AdapterClient adapterClient  = null;
    RecyclerView.LayoutManager mLayoutManager;
    List<Client> listClient= new ArrayList<>();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;



    Timer timer = new Timer();

    public FragmentMonitoring() {}

    public static FragmentMonitoring newInstance() {
        FragmentMonitoring fragment = new FragmentMonitoring();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterMonitoring = inflater.inflate(R.layout.fragment_monitoring, container, false);
        llMonUser = (LinearLayout)inflaterMonitoring.findViewById(R.id.ll_mon_user);
        tvSuhu = (TextView)inflaterMonitoring.findViewById(R.id.tv_suhu);
        tvAsap = (TextView)inflaterMonitoring.findViewById(R.id.tv_asap);
        tvApi = (TextView)inflaterMonitoring.findViewById(R.id.tv_api);
        llMonAdmin = (LinearLayout)inflaterMonitoring.findViewById(R.id.ll_mon_admin);
        rvClient = (RecyclerView)inflaterMonitoring.findViewById(R.id.rv_client);
        return inflaterMonitoring;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("APP_FIRE", 0);
        editorPreferences = sharedPreferences.edit();

        if(FireApp.tipe_user.equals("0")){
            llMonUser.setVisibility(View.GONE);
            llMonAdmin.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            TimerTask doDataToUI = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                loadMonitoring();
                            } catch (Exception e) {
                            }
                        }
                    });
                }
            };
            timer.schedule(doDataToUI, 0, 5000);
        }else
        if(FireApp.tipe_user.equals("1")){
            llMonUser.setVisibility(View.VISIBLE);
            llMonAdmin.setVisibility(View.GONE);
            final Handler handler = new Handler();
            TimerTask doDataToUI = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                tvSuhu.setText(FireApp.val_suhu.concat(" °C"));
                                tvAsap.setText(FireApp.cat_asap);
                                tvApi.setText(FireApp.cat_api);
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

    private void loadMonitoring(){
        Random r = new Random();
        String angka_random = String.valueOf(r.nextInt(9999999 - 11111) + 11111);
        String tag_req_monitoring = "FIRE_MONITORING";
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_monitoring);
    }

    private void parseData(JSONObject data){
        try {
            JSONArray monitoring = data.getJSONArray("monitoring");
            if(monitoring.length() > 0){
                listClient.clear();
                rvClient.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                rvClient.setLayoutManager(mLayoutManager);
                adapterClient= new AdapterClient(listClient);
                adapterClient.notifyDataSetChanged();
                rvClient.setAdapter(adapterClient);
                for(int x=0;x<monitoring.length();x++){
                    listClient.add(
                            new Client(
                                    monitoring.getJSONObject(x).getString("id"),
                                    monitoring.getJSONObject(x).getString("nama"),
                                    monitoring.getJSONObject(x).getString("nama_toko"),
                                    monitoring.getJSONObject(x).getString("alamat_toko"),
                                    monitoring.getJSONObject(x).getString("api_key"),
                                    monitoring.getJSONObject(x).getString("cat_suhu"),
                                    monitoring.getJSONObject(x).getString("cat_asap"),
                                    monitoring.getJSONObject(x).getString("cat_api")
                            )
                    );
                }
                adapterClient.notifyDataSetChanged();
            }
        }catch (JSONException e){
            Log.d("Bandeng Notification", e.getMessage().toString());
        }
    }

}
