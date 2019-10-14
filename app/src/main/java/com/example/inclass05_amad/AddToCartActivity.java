package com.example.inclass05_amad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AddToCartActivity extends AppCompatActivity implements CartQuantityInterface{
    private Button payButton;
    private TextView totalAmountValue;
    private RecyclerView addToCartRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    double sendAmt=0.0;
    DecimalFormat df = new DecimalFormat("####0.00");

    ArrayList<Product> SelectedItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);
        setTitle("Cart");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
//        String token = sharedPreferences.getString("Token",null);
//        String customerID = sharedPreferences.getString("CustomerID",null);
//        final String userID = sharedPreferences.getString("UserID",null);


        payButton = findViewById(R.id.payBtn);
        totalAmountValue =findViewById(R.id.totalAmountValue);
        addToCartRecyclerView = findViewById(R.id.cartItemsRecyclerView);

        addToCartRecyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(AddToCartActivity.this);
        addToCartRecyclerView.setLayoutManager(layoutManager);

        SelectedItemList = new ArrayList<>();
        final String SelectedItemsString = getIntent().getStringExtra("list_as_string");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Product>>(){}.getType();
        SelectedItemList = gson.fromJson(SelectedItemsString, type);

        addToCartRecyclerView.setAdapter(new AddItemToCartAdapter(this,SelectedItemList,this));

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sendAmt!=0.0) {
                    Intent checkoutIntent = new Intent(AddToCartActivity.this, AddCardDetails.class);
                    checkoutIntent.putExtra("amount", String.valueOf(sendAmt));
                    startActivity(checkoutIntent);
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new KAlertDialog(AddToCartActivity.this)
                                    .setTitleText("Sorry, cart is empty.")
                                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                        @Override
                                        public void onClick(KAlertDialog kAlertDialog) {
                                            finish();
                                        }
                                    })
                                    .show();

                        }
                    });
                }
                Log.d("send amount is", "onClick: "+sendAmt);
            }
        });

    }



    @Override
    public void getTotal(Product product, int position) {
        SelectedItemList.get(position).setQuantity(product.quantity);
        calculateTotal();
    }

    private void calculateTotal() {

        double totalAmt=0.0;
        for(Product product:SelectedItemList)
        {   double price=Double.parseDouble(product.getPrice());
            double discount=Double.parseDouble(product.getDiscount());
            double discountedPrice=((100-discount)*price)/100;
            totalAmt+=discountedPrice*product.getQuantity();
        }
        totalAmountValue.setText("$ "+(df.format(totalAmt)));
        sendAmt = totalAmt*100;
    }
}
