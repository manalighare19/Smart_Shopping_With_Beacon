package com.example.inclass03_amad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> {
    static ArrayList<Product> ShoppingItemArrayList;
    static Product product;
    Context mContext;

    public ShoppingItemAdapter(Context context,ArrayList<Product> ShoppingItemArraylist1)
    {
        this.ShoppingItemArrayList=ShoppingItemArraylist1;
        this.mContext = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item_row,parent,false);
        ShoppingItemAdapter.ViewHolder viewHolder=new ShoppingItemAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        product= ShoppingItemArrayList.get(position);
        if (product!=null){
            holder.productName.setText(product.getName());
            holder.productPrice.setText("$"+product.getPrice());
            File f = new File("drawable://" + product.getPhoto());
            System.out.println("file path : "+f.getPath());
            Picasso.get().load(f).into(holder.productImage);


        }

    }

    @Override
    public int getItemCount() {
        return ShoppingItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productPrice;
        TextView productName;
        ImageView productImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.itemNameTextView);
            productPrice = itemView.findViewById(R.id.itemPriceTextView);
            productImage = itemView.findViewById(R.id.itemImageView);
        }
    }
}
