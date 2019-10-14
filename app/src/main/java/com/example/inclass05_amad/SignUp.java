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
import android.os.Bundle;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SignUp extends AppCompatActivity {

    private EditText firstName, lastName, email, password, confirmPassword, address;
    private Button registerBtn,cancelBtn;
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        client = new OkHttpClient();
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.emailID);
        password = findViewById(R.id.passwordID);
        confirmPassword = findViewById(R.id.confirmPasswordID);
        address = findViewById(R.id.address);

        registerBtn = findViewById(R.id.registerID);
        cancelBtn = findViewById(R.id.cancelID);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cancelIntent = new Intent(SignUp.this, Login.class);
                startActivity(cancelIntent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEverythingFilledAndValid()) {
                    try {
                        register(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(),
                                password.getText().toString(), address.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void register(String fname, String lname, String email, String password,String address) throws JSONException {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("email",email);
        jsonObject.put("password",password);
        jsonObject.put("firstName",fname);
        jsonObject.put("lastName",lname);
        jsonObject.put("address",address);

        String jsonString=jsonObject.toString();
        RequestBody rbody = RequestBody.create(JSON, jsonString);


        Request request = new Request.Builder()
                .url("http://ec2-3-17-204-58.us-east-2.compute.amazonaws.com:4000/users/register")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .post(rbody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                String json = response.body().string();
                if(json.equals("{}")) {
                    Intent intent = new Intent(SignUp.this,Login.class);
                    startActivity(intent);
                    finish();
                }

                else
                {
                    Looper.prepare();
                    Toast.makeText(SignUp.this, "Registration was unsuccessful", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }


            }


        });
    }

    public boolean isEverythingFilledAndValid() {
        int i = 0;

        if (firstName.getText().toString().equals("")){
            firstName.setError("Please enter first name");
            i= 1;
        }

        if (lastName.getText().toString().equals("")){
            lastName.setError("Please enter last name");
            i= 1;
        }

        if (address.getText().toString().equals("")){
            address.setError("Please enter address");
            i= 1;
        }

        if (email.getText().toString().equals("")) {
            email.setError("Please enter email");
            i = 1;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("Invalid Email ID");
            i = 1;
        }

        if (password.getText().toString().equals("")) {
            password.setError("Please enter password");
            i = 1;
        }

        if (confirmPassword.getText().toString().equals("")) {
            confirmPassword.setError("Please enter password");
            i = 1;
        }

        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            confirmPassword.setError("Passwords do not match, please retype");
            i = 1;
        }

        if (i == 0) {
            return true;
        } else {
            return false;
        }
    }

    }
