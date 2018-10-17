package com.laurensius_dede_suhardiman.fireearlywarningsystem;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.appcontroller.AppController;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.fragments.FragmentMonitoring;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.fragments.FragmentNotifikasi;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.fragments.FragmentTentang;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FireApp extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;

    private BottomNavigationView navigation;
    private Dialog dialBox;

    public static String val_suhu = "";
    public static String val_asap = "";
    public static String val_api = "";
    public static String cat_suhu = "";
    public static String cat_asap = "";
    public static String cat_api = "";
    public static String str_notifikasi = "";
    public static String tipe_user ="";
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_app);

        sharedPreferences = getApplicationContext().getSharedPreferences("APP_FIRE", 0);
        editorPreferences = sharedPreferences.edit();

        tipe_user = sharedPreferences.getString("sf_tipe",null);

        dialBox = createDialogBox();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_monitoring:
                        selectedFragment = FragmentMonitoring.newInstance();
                        break;
                    case R.id.navigation_notifikasi:
                        selectedFragment = FragmentNotifikasi.newInstance();
                        break;
                    case R.id.navigation_tentang:
                        selectedFragment = FragmentTentang.newInstance();
                        break;
                    case R.id.navigation_logout:
                        dialBox.show();
                }

                if(selectedFragment != null){
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fl_master, selectedFragment);
                    transaction.commit();
                }

                return true;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_master, FragmentMonitoring.newInstance());
        transaction.commit();

        if(tipe_user.equals("0") ){
            Boolean status_service = isMyServiceRunning(ServiceNotificationAdmin.class);
            if(status_service == false){
                startService(new Intent(getBaseContext(), ServiceNotificationAdmin.class));
            }
        }
        
        if(tipe_user.equals("1") ){
            Boolean status_service = isMyServiceRunning(ServiceNotification.class);
            if(status_service == false){
                startService(new Intent(getBaseContext(), ServiceNotification.class));
                final Handler handler = new Handler();
                TimerTask doAsynchronousTask = new TimerTask() {
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
                timer.schedule(doAsynchronousTask, 0, 5000);
            }    
        }
        


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Dialog createDialogBox(){
        dialBox = new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Apakah Anda yakin akan keluar dari aplikasi ?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(tipe_user.equals("0") ){
                            stopService(new Intent(getBaseContext(), ServiceNotificationAdmin.class));
                        }else
                        if(tipe_user.equals("1") ){
                            stopService(new Intent(getBaseContext(), ServiceNotification.class));
                        }
                        timer.cancel();
                        editorPreferences.clear();
                        editorPreferences.commit();
                        Intent i = new Intent(FireApp.this, Login.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialBox.dismiss();
                    }
                })
                .create();
        return dialBox;
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void loadMonitoring(){
        Random r = new Random();
        String angka_random = String.valueOf(r.nextInt(9999999 - 11111) + 11111);
        String tag_req_monitoring = "FIRE_MONITORING";
        String url = getResources().getString(R.string.api_url_user)
                .concat(sharedPreferences.getString("sf_api_key",null).concat("/"))
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
            val_suhu = data.getJSONArray("recent").getJSONObject(0).getString("val_suhu");
            val_asap = data.getJSONArray("recent").getJSONObject(0).getString("val_asap");
            val_api = data.getJSONArray("recent").getJSONObject(0).getString("val_api");
            cat_suhu = data.getJSONArray("recent").getJSONObject(0).getString("cat_suhu");
            cat_asap = data.getJSONArray("recent").getJSONObject(0).getString("cat_asap");
            cat_api = data.getJSONArray("recent").getJSONObject(0).getString("cat_api");
            if(!cat_suhu.equals("Normal") || !cat_asap.equals("Normal") || !cat_api.equals("Normal")){
                str_notifikasi = data.getJSONArray("notif").getJSONObject(0).getString("pesan_notifikasi").toString();
            }else{
                str_notifikasi = "Semua sensor dalam keadaan normal";
            }
        }catch (JSONException e){
            Log.d("Bandeng Notification", e.getMessage());
        }
    }

}
