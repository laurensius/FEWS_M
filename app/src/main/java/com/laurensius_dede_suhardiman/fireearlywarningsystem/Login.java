package com.laurensius_dede_suhardiman.fireearlywarningsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius_dede_suhardiman.fireearlywarningsystem.appcontroller.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Login extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;

    private TextView tvNotifikasi;
    private EditText etNamaPengguna, etKataSandi;
    private Button btnMasuk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("APP_FIRE", 0);
        editorPreferences = sharedPreferences.edit();
        String sf_username = sharedPreferences.getString("sf_username",null);
        String sf_nama = sharedPreferences.getString("sf_nama",null);
        if(sf_username != null && sf_nama != null){
            Intent i = new Intent(Login.this,FireApp.class);
            startActivity(i);
            finish();
        }
        etNamaPengguna = (EditText)findViewById(R.id.et_username);
        etKataSandi = (EditText)findViewById(R.id.et_password);
        btnMasuk = (Button)findViewById(R.id.btn_login);
        tvNotifikasi = (TextView) findViewById(R.id.tv_notif_login);
        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etNamaPengguna.getText().toString();
                String password = etKataSandi.getText().toString();
                validateLogin(username,password);
            }
        });
    }

    void validateLogin(String username, String password){
        if(username.equals("") ||
                password.equals("")){
            Toast.makeText(
                    getApplicationContext(),
                    "Semua bidang harus diisi",
                    Toast.LENGTH_SHORT).show();
            tvNotifikasi.setVisibility(View.VISIBLE);
            tvNotifikasi.setText("Semua bidang harus diisi");
            tvNotifikasi.setBackgroundColor(Color.parseColor("#CCCC00"));
        }else{
            loginProcess(username,password);
        }
    }

    void loginProcess(String username,String password){

        Random r = new Random();
        String angka_random = String.valueOf(r.nextInt(9999999 - 11111) + 11111);

        String tag_req_login = "FIRE_LOGIN";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,getResources().getString(R.string.api_login).concat(angka_random).concat("/"), parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Fire Login :",response.toString());
                        pDialog.dismiss();
                        validateLoginResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        tvNotifikasi.setVisibility(View.VISIBLE);
                        tvNotifikasi.setText("Terjadi kendala saat terhubung ke server.");
                        tvNotifikasi.setBackgroundColor(Color.parseColor("#CC0000"));
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_req_login);
    }

    void validateLoginResponse(JSONObject jsonObject){
        try{
            String severity = jsonObject.getString("severity");
            String message = jsonObject.getString("message");
            JSONArray data = jsonObject.getJSONArray("data");
            if(severity.equals("success")){
                tvNotifikasi.setVisibility(View.VISIBLE);
                tvNotifikasi.setText(message);
                tvNotifikasi.setBackgroundColor(Color.parseColor("#00CC00"));
                editorPreferences.putString("sf_username",data.getJSONObject(0).getString("username"));
                editorPreferences.putString("sf_nama",data.getJSONObject(0).getString("nama"));
                editorPreferences.putString("sf_tipe",data.getJSONObject(0).getString("tipe"));
                editorPreferences.putString("sf_api_key",data.getJSONObject(0).getString("api_key"));
                editorPreferences.commit();
//                if(data.getJSONObject(0).getString("tipe").equals("0")){ //admin
//                    startService(new Intent(getBaseContext(), ServiceNotification.class));
//                }else{ //user
//                    startService(new Intent(getBaseContext(), ServiceNotificationAdmin.class));
//                }

                Intent i = new Intent(Login.this, FireApp.class);
                startActivity(i);
                finish();
            }else
            if(severity.equals("warning")){
                tvNotifikasi.setVisibility(View.VISIBLE);
                tvNotifikasi.setText(message);
                tvNotifikasi.setBackgroundColor(Color.parseColor("#CCCC00"));
            }else
            if(severity.equals("danger")){
                tvNotifikasi.setVisibility(View.VISIBLE);
                tvNotifikasi.setText(message);
                tvNotifikasi.setBackgroundColor(Color.parseColor("#CC0000"));
            }
        }catch (JSONException e){
            tvNotifikasi.setVisibility(View.VISIBLE);
            tvNotifikasi.setText("Terjadi kendala saat terhubung ke server.");
            tvNotifikasi.setBackgroundColor(Color.parseColor("#CC0000"));
        }
    }
}
