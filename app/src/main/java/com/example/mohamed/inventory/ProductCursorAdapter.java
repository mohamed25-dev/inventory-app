package com.example.mohamed.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.inventory.data.ProductContract;

import static com.example.mohamed.inventory.data.ProductContract.*;

public class ProductCursorAdapter extends CursorAdapter {
    private final Context mContext;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView;
        TextView priceTextView;
        TextView quantityTextView;
        Button sellButton;

        final String name      = cursor.getString(cursor.getColumnIndex(ProductEntry.NAME_COL));
        final String supplier  = cursor.getString(cursor.getColumnIndex(ProductEntry.SUPPLIER_COL));
        final int id           = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        final int price        = cursor.getInt(cursor.getColumnIndex(ProductEntry.PRICE_COL));
        final int quantity     = cursor.getInt(cursor.getColumnIndex(ProductEntry.QUANTITU_COL));

        nameTextView     = view.findViewById(R.id.name_text_view);
        nameTextView.setText(name);

        priceTextView    = view.findViewById(R.id.price_text_view);
        priceTextView.setText(String.valueOf(price));

        quantityTextView = view.findViewById(R.id.quantity_text_view);
        quantityTextView.setText(String.valueOf(quantity));


        sellButton       = view.findViewById(R.id.sell_button);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.QUANTITU_COL));;
                if (newQuantity > 0) {
                    --newQuantity;

                    ContentValues values = new ContentValues();

                    values.put(ProductEntry.NAME_COL, name);
                    values.put(ProductEntry.SUPPLIER_COL, supplier);
                    values.put(ProductEntry.PRICE_COL, price);
                    values.put(ProductEntry.QUANTITU_COL, newQuantity);

                   mContext.getContentResolver().update(
                            ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id), values,
                            null, null);
                   context.getContentResolver().notifyChange(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id), null);
                } else {
                    Toast.makeText(mContext, "Sorry Not Available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = EditorActivity.newIntent(mContext, id);
                mContext.startActivity(intent);
            }
        });
    }
}
