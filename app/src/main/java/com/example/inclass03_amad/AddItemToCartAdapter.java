package com.example.inclass03_amad;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AddItemToCartAdapter extends RecyclerView.Adapter<AddItemToCartAdapter.ViewHolder> {

    static ArrayList<Product> SelectedItemsArrayList;
    static Product product;
    Context mContext;

    Integer[] quantityItems = new Integer[]{1,2,3,4,5,6,7,8,9,10};

    public AddItemToCartAdapter(Context context,ArrayList<Product> SelectedItemsArrayList1)
    {
        this.SelectedItemsArrayList=SelectedItemsArrayList1;
        this.mContext = context;
    }

    @NonNull
    @Override
    public AddItemToCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.addtocart_item_row,parent,false);
        AddItemToCartAdapter.ViewHolder viewHolder=new AddItemToCartAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddItemToCartAdapter.ViewHolder holder, int position) {
        product= SelectedItemsArrayList.get(position);
        if (product!=null){

            holder.productName.setText(product.getName());
            holder.productPrice.setText("$"+product.getPrice());

            ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(mContext,android.R.layout.simple_spinner_item, quantityItems);
            holder.quantitySpinner.setAdapter(adapter);

            Picasso.get().load("drawable://" + product.getPhoto())
                    .config(Bitmap.Config.RGB_565)
                    .fit().centerCrop()
                    .into(holder.productImage);


        }

    }

    @Override
    public int getItemCount() {
        return SelectedItemsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productPrice;
        TextView productName;
        ImageView productImage;
        Spinner quantitySpinner;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.itemNameTextView);
            productPrice = itemView.findViewById(R.id.itemPriceTextView);
            productImage = itemView.findViewById(R.id.itemImageView);
            quantitySpinner = itemView.findViewById(R.id.quantitySpinner);

        }
    }
}


