package com.example.inclass03_amad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> {
    static ArrayList<Product> ShoppingItemArrayList;
    static Product product;
    Context mContext;
    AddToCartInterface addToCartInterface;

    public ShoppingItemAdapter(Context context,ArrayList<Product> ShoppingItemArraylist1,AddToCartInterface addToCartInterface1)
    {
        this.ShoppingItemArrayList=ShoppingItemArraylist1;
        this.mContext = context;
        this.addToCartInterface = addToCartInterface1;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item_row,parent,false);
        ShoppingItemAdapter.ViewHolder viewHolder=new ShoppingItemAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        product= ShoppingItemArrayList.get(position);
        if (product!=null){
            holder.addToCartInterface = addToCartInterface;
            holder.productName.setText(product.getName());
            holder.productPrice.setText("$"+product.getPrice());

            if (product.getPhoto() != null) {
                String uri = "@drawable/"+product.getPhoto().substring(0,product.getPhoto().indexOf('.'));
                int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
                Drawable res = mContext.getResources().getDrawable(imageResource);
                holder.productImage.setImageDrawable(res);
            }else {
                String uri = "@drawable/" + "no_image_found";
                int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
                Drawable res = mContext.getResources().getDrawable(imageResource);
                holder.productImage.setImageDrawable(res);
            }

            if (product.isAdded == true){
                holder.addToCartBtn.setBackground(ContextCompat.getDrawable(mContext, R.drawable.custom_button_black));
                holder.addToCartBtn.setText("Added");
            }else if(product.isAdded == false){
                holder.addToCartBtn.setBackground(ContextCompat.getDrawable(mContext, R.drawable.custom_button));
                holder.addToCartBtn.setText("Add to Cart");
            }

            double discount=Double.parseDouble(product.getDiscount());
            double price=Double.parseDouble(product.getPrice());
            DecimalFormat df = new DecimalFormat("####0.00");
            if(discount>0)
            {
                double discounted_price=((100.00-discount)*price)/100;
                holder.discountPrice.setText("$"+String.valueOf(df.format(discounted_price)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return ShoppingItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productPrice, discountPrice;
        TextView productName;
        ImageView productImage;
        Button addToCartBtn;
        AddToCartInterface addToCartInterface;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.itemNameTextView);
            productPrice = itemView.findViewById(R.id.itemPriceTextView);
            productImage = itemView.findViewById(R.id.itemImageView);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);
            discountPrice = itemView.findViewById(R.id.discountPriceTextView);

            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: call Add to Cart API from here
                    if (ShoppingItemArrayList.get(getAdapterPosition()).isAdded == false){
                        ShoppingItemArrayList.get(getAdapterPosition()).isAdded = true;
                        addToCartBtn.setBackground(ContextCompat.getDrawable(mContext, R.drawable.custom_button_black));
                        addToCartBtn.setText("Added");
                        product.setQuantity(1);
                        Log.d("quantity is", "onClick: "+product.getQuantity());
                        addToCartInterface.addToCart(ShoppingItemArrayList.get(getAdapterPosition()));
                    }else {
                        ShoppingItemArrayList.get(getAdapterPosition()).isAdded = false;
                        addToCartBtn.setBackground(ContextCompat.getDrawable(mContext, R.drawable.custom_button));
                        addToCartBtn.setText("Add to Cart");
                        product.setQuantity(0);
                        Log.d("quantity is", "onClick: "+product.getQuantity());
                        addToCartInterface.removeFromCart(ShoppingItemArrayList.get(getAdapterPosition()));
                    }


                }
            });

        }
    }
}
