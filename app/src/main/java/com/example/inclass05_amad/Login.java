package com.example.inclass05_amad;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Login extends AppCompatActivity {

    private EditText emailLogin, passwordLogin;
    private Button loginBtn, signUpBtn;
    OkHttpClient client;
    static String TOKEN=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        client = new OkHttpClient();
        emailLogin = findViewById(R.id.emailEditText);
        passwordLogin = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.signInBtn);
        signUpBtn = findViewById(R.id.signUpBtn);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String StoredToken = sharedPreferences.getString("Token",null);

        if (StoredToken != null){
            if (!StoredToken.equals("")){
                Intent intent = new Intent(Login.this, UserProfile.class);
                startActivity(intent);
            }
        }


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    login(emailLogin.getText().toString(),passwordLogin.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent = new Intent(Login.this, SignUp.class);
                startActivity(signupIntent);
            }
        });
    }

    private void login(final String email, String password) throws JSONException{

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email",email);
        jsonObject.put("password",password);

        String jsonString=jsonObject.toString();

        RequestBody requestBody = RequestBody.create(JSON, jsonString);


        Request request = new Request.Builder()
                .url("http://ec2-3-17-204-58.us-east-2.compute.amazonaws.com:4000/users/authenticate")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {

                    String json = response.body().string();
                    JSONObject root = new JSONObject(json);

                    String status= root.getString("email");
                    if(status.equals(email)) {
                        TOKEN = root.getString("token");
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString("Token", TOKEN);
                        editor.putString("UserID",root.getString("_id"));
                        editor.putString("CustomerID",root.getString("customerID"));
                        editor.apply();

                        Intent intent = new Intent(Login.this, UserProfile.class);
                        startActivity(intent);
                        finish();

                    }

                    else
                    {
                        Looper.prepare();
                        Toast.makeText(Login.this, "Login is not successful.", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        });
    }


}
