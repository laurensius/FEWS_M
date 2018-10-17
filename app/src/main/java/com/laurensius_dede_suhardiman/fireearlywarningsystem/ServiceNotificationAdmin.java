package com.laurensius_dede_suhardiman.fireearlywarningsystem;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.adapter.AdapterClient;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.appcontroller.AppController;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.model.Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceNotificationAdmin extends Service {

    Timer timer = new Timer();
    Boolean init = true;
    String recent_notif = "";

    int unnormal_counter = 0;
    int recent_counter = 0;

    public ServiceNotificationAdmin(){}

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("UNSUPPORTED OPERATION EXCEPTION");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Handler handler = new Handler();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            loadNotification();
                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public void loadNotification(){
        Random r = new Random();
        String angka_random = String.valueOf(r.nextInt(9999999 - 11111) + 11111);
        String tag_req_notification = "FIRE_SERVICE";
        String url = getResources().getString(R.string.api_url_admin_monitorig)
                .concat(angka_random).concat("/");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response On Service : ", response.toString());
                        notificationChecker(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("FIRE ERROR", error.toString());
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_notification);
    }

    void notificationChecker(JSONObject data){
        try {
            JSONArray monitoring = data.getJSONArray("monitoring");

            if(monitoring.length() > 0){
                for(int x=0;x<monitoring.length();x++){
                    if(!monitoring.getJSONObject(x).getString("cat_suhu").equals("Normal")){
                        unnormal_counter++;
                    }
                    if(!monitoring.getJSONObject(x).getString("cat_asap").equals("Normal")){
                        unnormal_counter++;
                    }
                    if(!monitoring.getJSONObject(x).getString("cat_api").equals("Normal")){
                        unnormal_counter++;
                    }
                }
                if((recent_counter < unnormal_counter) && !init){
                    Log.d("notifikasi ","Buat notifikasi");
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext(), "notify_001");
                    Intent ii = new Intent(getApplicationContext(), FireApp.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);
                    NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                    bigText.bigText("Notifikasi");
                    bigText.setBigContentTitle("Notifikasi");
                    bigText.setSummaryText("Terjadi kondisi tidak normal pada sebuah kios");
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                    mBuilder.setContentTitle("Notifikasi");
                    mBuilder.setContentText("Terjadi kondisi tidak normal pada sebuah kios");
                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                    mBuilder.setStyle(bigText);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("notify_001",
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_HIGH);
                        mNotificationManager.createNotificationChannel(channel);
                    }
                    mNotificationManager.notify(0, mBuilder.build());
                }
            }
            recent_counter = unnormal_counter;
            unnormal_counter = 0;
            init = false;
        }catch (JSONException e){
            Log.d("FIRE Notification", e.getMessage().toString());
        }
    }

}