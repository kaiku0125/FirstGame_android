package org.kevin.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int POSITION = 1;
    private Button login, reg, test;
    private EditText edit_userID, edit_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login);
        reg = findViewById(R.id.reg);
        test = findViewById(R.id.test);
        edit_userID = findViewById(R.id.edit_userID);
        edit_password = findViewById(R.id.edit_password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(edit_userID.getText().toString(), edit_password.getText().toString());
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: ddd");
                reg(edit_userID.getText().toString(), edit_password.getText().toString());
            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(String userID, String password){
        String url = "https://cfc283ab5580.ngrok.io" +  "/mygame/1";
//        String url = "http://127.0.0.1:5000/mygame/Kevin";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "onFailure: GGGGGGG");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toast_msg("server fail");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String strResponse = response.body().string();
                if(strResponse == null)
                    return;

                try {
                    JSONObject json = new JSONObject(strResponse);
                    Log.e(TAG, "onResponse: " + json.toString());
                    String server_userID = json.optString("user_id");
                    String server_password = json.optString("password");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(userID.equals(server_userID) && password.equals(server_password)){
                                toast_msg("登入成功");
                                Intent intent = new Intent(LoginActivity.this, SecondActivity.class);
                                startActivity(intent);
                            }else{
                                toast_msg("登入失敗");
                            }
                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void reg(String userID, String password){
        String url = "https://cfc283ab5580.ngrok.io" +  "/mygame/" + POSITION;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("user_id", userID)
                .add("password", password);
        FormBody formBody = builder.build();
        Request request = new Request.Builder().url(url).put(formBody).build();
        Log.e(TAG, "========================Call Server========================");
        Log.e(TAG, "urlAPI:"+url);
        Log.e(TAG, "=====================Data In the Post=====================");
        Log.e(TAG, "{");
        for(int i=0; i<formBody.size(); i++)
            Log.e(TAG, "  " + "\"" + formBody.name(i) + "\"" + " : " + "\"" + formBody.value(i) + "\"" + ",");
        Log.e(TAG, "}");
        Log.e(TAG, "=====================Data In Post End=====================");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "onFailure: GGGGGGG");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toast_msg("server fail");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String strResponse = response.body().string();
                if(strResponse == null)
                    return;

                try {
                    JSONObject json = new JSONObject(strResponse);
                    Log.e(TAG, "onResponse: " + json.toString());

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void toast_msg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}