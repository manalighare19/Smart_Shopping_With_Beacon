package com.example.inclass03_amad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nimbusds.jose.Header;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ShoppingActivity extends AppCompatActivity {

    private Button payButton;
    private OkHttpClient client, client1;
    private String clientToken;
    private RecyclerView shoppingItemsRecyclerView;
    private RecyclerView.Adapter shoppingItemAdapter;
    private RecyclerView.LayoutManager layoutManager;
    Gson gson = new Gson();

    private ArrayList<Product> ShoppingItemArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        setTitle("Shop");

        ShoppingItemArrayList = new ArrayList<>();

        client = new OkHttpClient();
        client1 = new OkHttpClient();
        payButton = findViewById(R.id.payBtn);

        shoppingItemsRecyclerView = findViewById(R.id.shoppingItemsRV);
        shoppingItemsRecyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(ShoppingActivity.this);
        shoppingItemsRecyclerView.setLayoutManager(layoutManager);
        getShoppingItems();

        String url = "http://ec2-3-17-204-58.us-east-2.compute.amazonaws.com:4000/brain/token";
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JSONObject root = null;
                try {
                    root = new JSONObject(json);
                    clientToken= root.getString("clientToken");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });



        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DropInRequest dropInRequest = new DropInRequest()
                        .clientToken(clientToken);
                startActivityForResult(dropInRequest.getIntent(ShoppingActivity.this), 1);
            }
        });
    }

    private void getShoppingItems() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("items.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray parsedResults = jsonObject.getJSONArray("results");

                for (int i=0;i< parsedResults.length();i++) {
                    Product product = gson.fromJson(parsedResults.getString(i), Product.class);
                    System.out.println("JSON object : "+parsedResults.getString(i));
                    ShoppingItemArrayList.add(product);
                }

                System.out.println("Arraylist count is  :"+ShoppingItemArrayList.size());
                shoppingItemsRecyclerView.setAdapter(new ShoppingItemAdapter(this,ShoppingItemArrayList));

            }
            catch (Exception e){
                System.out.println("Exception occured : "+e.toString());
            }
            Log.d("items are:", "getShoppingItems: ");


            Product product = gson.fromJson(json, Product.class);
           // Log.d("Product is:", "getShoppingItems: "+product.getName());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                // use the result to update your UI and send the payment method nonce to your server
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("paymentMethodNonce",result.getPaymentMethodNonce().getNonce());
                    String jsonString=jsonObject.toString();
                    RequestBody requestBody = RequestBody.create(JSON, jsonString);
                    String url = "http://ec2-3-17-204-58.us-east-2.compute.amazonaws.com:4000/brain/sandbox";
                    final Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    client1.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d("Responce is:", "onResponse: "+response);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }

    }


}
