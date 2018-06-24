package com.example.mohamed.inventory;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mohamed.inventory.data.ProductDbHelper;

import java.io.IOException;

import static com.example.mohamed.inventory.data.ProductContract.*;

public class EditorActivity extends AppCompatActivity {
    public static final String TAG = EditorActivity.class.getSimpleName();
    public static final String EXTRA_POSITION = "extra_position";
    public static final int PICK_IMAGE_REQUEST = 1;
    private EditText mNameEditText;
    private EditText mSupplierEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private ImageView mProductImage;

    private ProductDbHelper mDbHelper;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mDbHelper = new ProductDbHelper(this);

        mNameEditText     = findViewById(R.id.name_edit_text);
        mSupplierEditText = findViewById(R.id.supplier_edit_text);
        mQuantityEditText = findViewById(R.id.quantity_edit_text);
        mPriceEditText    = findViewById(R.id.price_edit_text);
        mProductImage     = findViewById(R.id.product_image_view);

        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            Toast.makeText(this, "Extra is Null", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "We Have Extra :)", Toast.LENGTH_SHORT).show();
            int position = extras.getInt(EXTRA_POSITION);
            showData(position);
        }

        mProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickImage = new Intent();
                pickImage.setType("image/*");
                pickImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pickImage, "Choose Image"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_activity, menu);
        MenuItem item = menu.findItem(R.id.delete);
        if (getIntent().getExtras() == null) {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int position = -1;
        if (hasExtras()) {
            position = getIntent().getExtras().getInt(EXTRA_POSITION);
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.delete:
                showDeleteDialog(position);
                return true;
            case R.id.save:
                if (hasExtras()) {
                    int updated = getContentResolver().update(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, position), createContentValues(), null, null);
                    if (updated == 0) {
                        Toast.makeText(this, R.string.update_failed, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.update_succeeded, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    getContentResolver().insert(ProductEntry.CONTENT_URI, createContentValues());
                }
                finish();
                return true;
            default:
                throw new IllegalArgumentException("Error Unknown ID !!");
        }
    }

    public static Intent newIntent (Context context, int position) {
        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(EXTRA_POSITION, position);
        return intent;
    }

    private Product readData() {
        String name     = mNameEditText.getText().toString();
        String supplier = mSupplierEditText.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(supplier)) {
            return null;
        }

        int price       = Integer.valueOf(mPriceEditText.getText().toString());
        int quantity    = Integer.valueOf(mQuantityEditText.getText().toString());

        Product product = new Product();
        product.setName(name);
        product.setSupplier(supplier);
        product.setPrice(price);
        product.setQuantity(quantity);

        return product;
    }

    private Product showData(int position) {
        Product product = mDbHelper.getProduct(position);

        mNameEditText.setText(product.getName());
        mSupplierEditText.setText(product.getSupplier());
        mQuantityEditText.setText(String.valueOf(product.getQuantity()));
        mPriceEditText.setText(String.valueOf(product.getPrice()));


        return product;
    }

    private boolean hasExtras() {
        return getIntent().getExtras() != null;
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Confirm Deletion ?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int deletedItems = getContentResolver().delete(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, position), null, null);
                        if (deletedItems == 0) {
                            Toast.makeText(EditorActivity.this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditorActivity.this, R.string.delete_succeeded, Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });

        dialog.create().show();
    }

    private ContentValues createContentValues () {
        Product product = readData();
        if (readData() == null) {
            finish();
        }
        ContentValues values = new ContentValues();

        String name     = product.getName();
        String supplier = product.getSupplier();
        int price       = product.getPrice();
        int quantity    = product.getQuantity();

        values.put(ProductEntry.NAME_COL, name);
        values.put(ProductEntry.SUPPLIER_COL, supplier);
        values.put(ProductEntry.PRICE_COL, price);
        values.put(ProductEntry.QUANTITU_COL, quantity);

        return values;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            if (data != null) {
                Uri uri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    mProductImage.setImageURI(uri);
                    mProductImage.invalidate();
                    Log.d(TAG, "onActivityResult: Image Chosen");
                } catch (IOException e) {
                    Log.e(TAG, "onActivityResult: IO Exception ");
                }
            } else {
                Log.d(TAG, "onActivityResult: data is null");
            } 
        } else {
            Log.d(TAG, "onActivityResult: something went wrong");
        }
    }
}
