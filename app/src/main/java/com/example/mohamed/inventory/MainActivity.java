package com.example.mohamed.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mohamed.inventory.data.ProductContract;
import com.example.mohamed.inventory.data.ProductDbHelper;

import java.util.ArrayList;
import java.util.List;

import static com.example.mohamed.inventory.data.ProductContract.*;

public class MainActivity extends AppCompatActivity {
    private ProductDbHelper mDbHelper;
    private ProductCursorAdapter mAdapter;
    private ListView mProductList;
    public static List<Product> mProducts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProductList = findViewById(R.id.product_list);

        updateUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add_product:
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
                return true;
            default:
                throw new IllegalArgumentException("Error !!");
        }
    }

    private void updateUI () {
        Cursor cursor = getContentResolver().query(ProductEntry.CONTENT_URI, null, null, null, null);

        mDbHelper = new ProductDbHelper(this);
        mProducts = new ArrayList<>();
        mProducts = getProducts();

        if (mAdapter == null) {
            mAdapter = new ProductCursorAdapter(this, cursor);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        mProductList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private List<Product> getProducts() {
        Cursor cursor = getContentResolver().query(ProductEntry.CONTENT_URI, null, null, null, null);
        List<Product> products = new ArrayList<>();

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ProductEntry.NAME_COL));
            String supplier = cursor.getString(cursor.getColumnIndex(ProductEntry.SUPPLIER_COL));
            int price = cursor.getInt(cursor.getColumnIndex(ProductEntry.PRICE_COL));
            int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.QUANTITU_COL));
            int id = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));

            Product product = new Product();
            product.setName(name);
            product.setSupplier(supplier);
            product.setQuantity(quantity);
            product.setPrice(price);
            product.setId(id);

            products.add(product);
        }

        return products;
    }
}
