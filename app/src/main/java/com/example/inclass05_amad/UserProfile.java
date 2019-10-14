package com.example.inclass05_amad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UserProfile extends AppCompatActivity {

    private TextView firstNameTextView, lastNameTextView, addressTextView, emailTextView;
    private Button editButton, shopButton;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setTitle("User Profile");

        client = new OkHttpClient();
        firstNameTextView = findViewById(R.id.firstNameTextView);
        lastNameTextView = findViewById(R.id.lastNameTextView);
        addressTextView = findViewById(R.id.addressTextView);
        emailTextView = findViewById(R.id.emailTextView);

        editButton = findViewById(R.id.editBtn);
        shopButton = findViewById(R.id.shopBtn);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String token = sharedPreferences.getString("Token",null);
        String customerID = sharedPreferences.getString("CustomerID",null);
        final String userID = sharedPreferences.getString("UserID",null);


        String url = "http://ec2-3-17-204-58.us-east-2.compute.amazonaws.com:4000/users/current";
        final Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .get()
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
                    final JSONObject root = new JSONObject(json);
                    if(root.getString("_id").equals(userID)) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    firstNameTextView.setText(root.getString("firstName"));
                                    lastNameTextView.setText(root.getString("lastName"));
                                    addressTextView.setText(root.getString("address"));
                                    emailTextView.setText(root.getString("email"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }

                    else
                    {

                        Looper.prepare();
                        Toast.makeText(UserProfile.this, "Update is not successful.", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        });

        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shopIntent = new Intent(UserProfile.this,ShoppingActivity.class);
                startActivity(shopIntent);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfileIntent = new Intent(UserProfile.this, EditUserProfile.class);
                startActivity(editProfileIntent);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Token", null);
                editor.putString("UserID",null);
                editor.putString("CustomerID",null);
                editor.apply();

                finish();
                Intent logout_intent = new Intent(UserProfile.this, Login.class);
                startActivity(logout_intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
