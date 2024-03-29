package com.example.inclass05_amad;

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

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShoppingActivity extends AppCompatActivity implements AddToCartInterface{

    private Button goToCartButton;
    private RecyclerView shoppingItemsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BeaconManager beaconManager;
    private BeaconRegion region;
    OkHttpClient client,client1;

    private ArrayList<Product> ShoppingItemArrayList;
    private ArrayList<Product> SelectedItemsArrayList;
    ShoppingItemAdapter shoppingItemAdapter;
    Gson gson = new Gson();

    @Override
    protected void onPostResume() {

        SelectedItemsArrayList=new ArrayList<>();
        for(int i=0;i<ShoppingItemArrayList.size();i++)
            ShoppingItemArrayList.get(i).isAdded=false;
        shoppingItemsRecyclerView.setAdapter(new ShoppingItemAdapter(this,ShoppingItemArrayList,this));
        super.onPostResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        setTitle("Shop");

        client = new OkHttpClient();
        client1 = new OkHttpClient();

        ShoppingItemArrayList = new ArrayList<>();
        SelectedItemsArrayList = new ArrayList<>();

        goToCartButton = findViewById(R.id.goToCartBtn);
        shoppingItemsRecyclerView = findViewById(R.id.shoppingItemsRV);

        shoppingItemsRecyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(ShoppingActivity.this);
        shoppingItemsRecyclerView.setLayoutManager(layoutManager);

        ShoppingItemArrayList.clear();
        shoppingItemAdapter = new ShoppingItemAdapter(this,ShoppingItemArrayList,ShoppingActivity.this);
        shoppingItemsRecyclerView.setAdapter(shoppingItemAdapter);


        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Log.d("List of beacons :", "onBeaconsDiscovered: "+list.get(0).getMajor()+ ' ' +list.get(0).getMinor());
                    Beacon nearestBeacon = list.get(0);

                    if (nearestBeacon.getMinor() == 44931  && nearestBeacon.getMajor() == 41072){
                        ShoppingItemArrayList.clear();
                        getShoppingListByRegion("grocery");
                    }
                    else if(nearestBeacon.getMinor() == 29902  && nearestBeacon.getMajor() == 30476){
                        ShoppingItemArrayList.clear();
                        getShoppingListByRegion("produce");
                    }
                    else {
                        ShoppingItemArrayList.clear();
                        getShoppingItems();
                    }

                }
                else{
                    ShoppingItemArrayList.clear();
                    getShoppingItems();
                }
            }
        });
        region = new BeaconRegion("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        goToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String SelectedItems = gson.toJson(SelectedItemsArrayList);
                Intent goToCartIntent = new Intent(ShoppingActivity.this, AddToCartActivity.class);
                goToCartIntent.putExtra("list_as_string", SelectedItems);
                startActivity(goToCartIntent);
            }
        });
    }

    private void getShoppingListByRegion(String region) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();

        try {
            Log.d("region is", "getShoppingListByRegion: "+region);
            jsonObject.put("region",region);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString=jsonObject.toString();
        RequestBody requestBody = RequestBody.create(JSON, jsonString);

        Request request = new Request.Builder()
                .url("http://ec2-3-17-204-58.us-east-2.compute.amazonaws.com:4000/support/getProductsByRegion")
                .post(requestBody)
                .build();

        client1.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                try {
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i=0;i< jsonArray.length();i++) {
                        Product product = gson.fromJson(jsonArray.getString(i), Product.class);
                        ShoppingItemArrayList.add(product);
                        Log.d("shopping list", "onResponse: "+ShoppingItemArrayList);
                    }

                    shoppingItemAdapter.notifyDataSetChanged();
                    
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            shoppingItemsRecyclerView.setAdapter(new ShoppingItemAdapter(ShoppingActivity.this,
//                                    ShoppingItemArrayList,ShoppingActivity.this));
//                        }
//                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }


    private void getShoppingItems() {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();

        Request request = new Request.Builder()
                .url("http://ec2-3-17-204-58.us-east-2.compute.amazonaws.com:4000/support/getProducts")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                try {
                        JSONArray jsonArray = new JSONArray(json);
                        for (int i=0;i< jsonArray.length();i++) {
                        Product product = gson.fromJson(jsonArray.getString(i), Product.class);
                        ShoppingItemArrayList.add(product);
                    }
                    shoppingItemAdapter.notifyDataSetChanged();


//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            shoppingItemsRecyclerView.setAdapter(new ShoppingItemAdapter(ShoppingActivity.this,
//                                    ShoppingItemArrayList,ShoppingActivity.this));
//                        }
//                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void addToCart(Product product) {
        SelectedItemsArrayList.add(product);
    }

    @Override
    public void removeFromCart(Product product) {
        SelectedItemsArrayList.remove(product);
    }
}
