package com.example.inclass03_amad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AddItemToCartAdapter extends RecyclerView.Adapter<AddItemToCartAdapter.ViewHolder> {

    static ArrayList<Product> SelectedItemsArrayList;
    static Product product;
    Context mContext;

    String[] quantityItems = new String[]{"1","2","3","4","5","6","7","8","9","10"};
    CartQuantityInterface cartQuantityInterface;

    public AddItemToCartAdapter(Context context,ArrayList<Product> SelectedItemsArrayList1, CartQuantityInterface cartQuantityInterface1)
    {
        this.SelectedItemsArrayList=SelectedItemsArrayList1;
        this.mContext = context;
        this.cartQuantityInterface = cartQuantityInterface1;
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

            holder.cartQuantityInterface = cartQuantityInterface;
            holder.productName.setText(product.getName());
            double discount=Double.parseDouble(product.getDiscount());
            double price=Double.parseDouble(product.getPrice());
            DecimalFormat df = new DecimalFormat("####0.00");
            if(discount>0)
            {
                double discounted_price=((100.00-discount)*price)/100;
                holder.productPrice.setText("$"+String.valueOf(df.format(discounted_price)));
            }

            if (product.getPhoto() != null) {
                String uri = "@drawable/"+product.getPhoto().substring(0,product.getPhoto().indexOf('.'));
                int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
                Drawable res = mContext.getResources().getDrawable(imageResource);
                holder.productImage.setImageDrawable(res);

            }else{
                String uri = "@drawable/"+"no_image_found";
                int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
                Drawable res = mContext.getResources().getDrawable(imageResource);
                holder.productImage.setImageDrawable(res);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_item, quantityItems);
            holder.quantitySpinner.setAdapter(adapter);
            holder.quantitySpinner.setSelection(0);
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
        CartQuantityInterface cartQuantityInterface;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.itemNameTextView);
            productPrice = itemView.findViewById(R.id.itemPriceTextView);
            productImage = itemView.findViewById(R.id.itemImageView);
            quantitySpinner = itemView.findViewById(R.id.quantitySpinner);

            quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    quantitySpinner.setSelection(i);
                    Log.d("quantity is:", "onItemSelected: "+(i+1));
                    SelectedItemsArrayList.get(getAdapterPosition()).setQuantity(i+1);
                    cartQuantityInterface.getTotal(SelectedItemsArrayList.get(getAdapterPosition()),getAdapterPosition());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }
    }
}


