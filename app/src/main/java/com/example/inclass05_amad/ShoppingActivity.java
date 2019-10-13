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

public class ShoppingActivity extends AppCompatActivity implements AddToCartInterface{

    private Button goToCartButton;
    private RecyclerView shoppingItemsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Product> ShoppingItemArrayList;
    private ArrayList<Product> SelectedItemsArrayList;

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

        ShoppingItemArrayList = new ArrayList<>();
        SelectedItemsArrayList = new ArrayList<>();

        goToCartButton = findViewById(R.id.goToCartBtn);
        shoppingItemsRecyclerView = findViewById(R.id.shoppingItemsRV);

        shoppingItemsRecyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(ShoppingActivity.this);
        shoppingItemsRecyclerView.setLayoutManager(layoutManager);
        getShoppingItems();

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
                shoppingItemsRecyclerView.setAdapter(new ShoppingItemAdapter(this,ShoppingItemArrayList,this));
            }
            catch (Exception e){
                System.out.println("Exception occured : "+e.toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return;
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
