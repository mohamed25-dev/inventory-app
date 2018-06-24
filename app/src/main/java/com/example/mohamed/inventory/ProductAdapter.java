package com.example.mohamed.inventory;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.inventory.data.ProductContract;
import com.example.mohamed.inventory.data.ProductDbHelper;

import java.util.List;

import static com.example.mohamed.inventory.data.ProductContract.*;

public class ProductAdapter extends ArrayAdapter<Product>{
    private List<Product> mProducts;
    private Context mContext;
    private ProductDbHelper mDbHelper;

    public ProductAdapter(@NonNull Context context, List<Product> products, ProductDbHelper dbHelper) {
        super(context, 0);
        mProducts = products;
        mContext  = context;
        mDbHelper = dbHelper;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final Product currentProduct = mProducts.get(position);
        View view = convertView;

        TextView nameTextView;
        TextView priceTextView;
        TextView quantityTextView;
        Button   sellButton;

        ImageView productImage;
        ImageView shoppingImage;

        if (view == null) {
            view = LayoutInflater.from(mContext).
                    inflate(R.layout.product_list_item, parent, false);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = EditorActivity.newIntent(mContext, currentProduct.getId());
                mContext.startActivity(intent);
            }
        });

        nameTextView = view.findViewById(R.id.name_text_view);
        nameTextView.setText(currentProduct.getName());

        priceTextView = view.findViewById(R.id.price_text_view);
        priceTextView.setText(Integer.toString(currentProduct.getPrice())+ " $");

        quantityTextView = view.findViewById(R.id.quantity_text_view);
        quantityTextView.setText(Integer.toString(currentProduct.getQuantity()));

        sellButton = view.findViewById(R.id.sell_button);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = currentProduct.getQuantity();
                if (quantity > 0) {
                    currentProduct.setQuantity(--quantity);

                    ContentValues values = new ContentValues();

                    values.put(ProductEntry.NAME_COL, currentProduct.getName());
                    values.put(ProductEntry.SUPPLIER_COL, currentProduct.getSupplier());
                    values.put(ProductEntry.PRICE_COL, currentProduct.getPrice());
                    values.put(ProductEntry.QUANTITU_COL, quantity);

                   getContext().getContentResolver().update(
                            ContentUris.withAppendedId(ProductEntry.CONTENT_URI, currentProduct.getId()), values,
                            null, null);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Sorry Not Available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        productImage = view.findViewById(R.id.product_image);
        shoppingImage = view.findViewById(R.id.shopping_cart_image);

//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                mDbHelper.delete(currentProduct.getId());
//                return true;
//            }
//        });

        return view;
    }

    @Override
    public int getCount() {
        return mProducts.size();
    }

    public void setProducts(List<Product> products) {
        mProducts = products;
    }
}
