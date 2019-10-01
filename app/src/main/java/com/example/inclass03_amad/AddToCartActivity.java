package com.example.inclass03_amad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AddToCartActivity extends AppCompatActivity {
    private RecyclerView addToCartRecyclerView;
    private RecyclerView.LayoutManager layoutManager;


    ArrayList<Product> SelectedItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);
        setTitle("Cart");

        addToCartRecyclerView = findViewById(R.id.cartItemsRecyclerView);
        addToCartRecyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(AddToCartActivity.this);
        addToCartRecyclerView.setLayoutManager(layoutManager);

        SelectedItemList = new ArrayList<>();
        String SelectedItemsString = getIntent().getStringExtra("list_as_string");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Product>>(){}.getType();
        SelectedItemList = gson.fromJson(SelectedItemsString, type);

        addToCartRecyclerView.setAdapter(new AddItemToCartAdapter(this,SelectedItemList));


    }
}
