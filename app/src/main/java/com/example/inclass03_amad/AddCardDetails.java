package com.example.inclass03_amad;

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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.braintreepayments.api.Json;
import com.developer.kalert.KAlertDialog;
import com.simplify.android.sdk.CardEditor;
import com.simplify.android.sdk.CardToken;
import com.simplify.android.sdk.Simplify;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AddCardDetails extends AppCompatActivity{
    Simplify simplify;
    OkHttpClient client;
    String amount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_details);
        setTitle("Card details");
        simplify = new Simplify();
        client = new OkHttpClient();

        simplify.setApiKey("sbpb_OGI3MjliOTUtYWMyZS00ZTE4LWFmYTgtNTdkZDQ1NTBhZGFm");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final String customerID = sharedPreferences.getString("CustomerID",null);

        // init card editor
        final CardEditor cardEditor = (CardEditor) findViewById(R.id.card_editor);
        final Button checkoutButton = (Button) findViewById(R.id.checkoutBtn);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                amount= null;
                Log.d("hello", "onCreate: "+amount);
            } else {
                amount= extras.getString("amount");
                Log.d("heyy", "onCreate: "+amount);
            }
        } else {
            amount= (String) savedInstanceState.getSerializable("amount");
            Log.d("you there", "onCreate: "+amount);
        }
        // add state change listener
        cardEditor.addOnStateChangedListener(new CardEditor.OnStateChangedListener() {
            @Override
            public void onStateChange(CardEditor cardEditor) {
                //isValid() == true : card editor contains valid and complete card information
                checkoutButton.setEnabled(cardEditor.isValid());
            }
        });
        // add checkout button click listener
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a card token
                simplify.createCardToken(cardEditor.getCard(), new CardToken.Callback() {
                    @Override
                    public void onSuccess(CardToken cardToken) {
                        String url = "http://ec2-3-17-204-58.us-east-2.compute.amazonaws.com:4000/simplify/transaction";
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("customerID",customerID);
                            jsonObject.put("amount",amount);
                            Log.d("amount is", "onSuccess: "+amount);

                            JSONObject cardObject = new JSONObject();
                            cardObject.put("expMonth",cardEditor.getCard().getExpMonth());
                            cardObject.put("expYear",cardEditor.getCard().getExpYear());
                            cardObject.put("cvc",cardEditor.getCard().getCvc());
                            cardObject.put("number",cardEditor.getCard().getNumber());
                            jsonObject.put("card",cardObject);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String jsonString=jsonObject.toString();
                        RequestBody requestBody = RequestBody.create(JSON, jsonString);

                        final Request request = new Request.Builder()
                             .url(url)
                             .post(requestBody)
                             .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new KAlertDialog(AddCardDetails.this, KAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Payment was successful")
                                                    .setContentText("Thank you for shopping with us")
                                                    .setCustomImage(R.drawable.custom_img)
                                                    .setConfirmText("Okay")
                                                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                                        @Override
                                                        public void onClick(final KAlertDialog sDialog) {
                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    sDialog.dismissWithAnimation();

                                                                }
                                                            }, 3000);
                                                            Intent okayIntent =  new Intent(AddCardDetails.this,ShoppingActivity.class);
                                                            startActivity(okayIntent);
                                                            finish();
                                                        }


                                                    })
                                                    .show();
                                        }
                                    });


                                }


                        });
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        // ...
                    }
                });
            }
        });
    }
}
