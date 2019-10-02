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
import android.widget.TextView;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AddToCartActivity extends AppCompatActivity implements CartQuantityInterface{
    private Button payButton;
    private TextView totalAmountValue;
    private OkHttpClient client, client1;
    private String clientToken;
    private RecyclerView addToCartRecyclerView;
    private RecyclerView.LayoutManager layoutManager;


    ArrayList<Product> SelectedItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);
        setTitle("Cart");

        client = new OkHttpClient();
        client1 = new OkHttpClient();

        payButton = findViewById(R.id.payBtn);
        totalAmountValue =findViewById(R.id.totalAmountValue);
        addToCartRecyclerView = findViewById(R.id.cartItemsRecyclerView);

        addToCartRecyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(AddToCartActivity.this);
        addToCartRecyclerView.setLayoutManager(layoutManager);

        SelectedItemList = new ArrayList<>();
        String SelectedItemsString = getIntent().getStringExtra("list_as_string");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Product>>(){}.getType();
        SelectedItemList = gson.fromJson(SelectedItemsString, type);

        addToCartRecyclerView.setAdapter(new AddItemToCartAdapter(this,SelectedItemList,this));
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
                startActivityForResult(dropInRequest.getIntent(AddToCartActivity.this), 1);
            }
        });

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

    @Override
    public void getTotal(Product product, int position) {
        SelectedItemList.get(position).setQuantity(product.quantity);
        calculateTotal();
    }

    private void calculateTotal() {
        Double totalAmt=0.0;
        DecimalFormat df = new DecimalFormat("####0.00");

        for(Product product:SelectedItemList)
        {    Double price=Double.parseDouble(product.getPrice());
            Double discount=Double.parseDouble(product.getDiscount());
            Double discountedPrice=((100-discount)*price)/100;
            totalAmt+=discountedPrice*product.getQuantity();
        }
        totalAmountValue.setText("$ "+(df.format(totalAmt)));
    }
}
