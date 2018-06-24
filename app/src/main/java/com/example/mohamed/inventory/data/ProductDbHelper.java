package com.example.mohamed.inventory.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.mohamed.inventory.Product;
import com.example.mohamed.inventory.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.mohamed.inventory.data.ProductContract.*;

public class ProductDbHelper extends SQLiteOpenHelper {
    public final String TAG = getClass().getSimpleName();
    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase mReadableDB;
    private SQLiteDatabase mWritableDB;
    private Context mContext;


    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_DATABASE = "CREATE TABLE " + ProductEntry.TABLE_NAME +
                " ( " +
                ProductEntry._ID + " INTEGER PRIMARY KEY," +
                ProductEntry.NAME_COL + " TEXT NOT NULL," +
                ProductEntry.PRICE_COL + " INTEGER NOT NULL DEFAULT 0,"+
                ProductEntry.QUANTITU_COL + " INTEGER NOT NULL DEFAULT 0,"+
                ProductEntry.SUPPLIER_COL + " TEXT,"+
                ProductEntry.IMAGE_COL + " INTEGER ) ";

        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insert(Product product) {
        long newRowId = 0;
        ContentValues values = new ContentValues();

        try {

            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }

            String name     = product.getName();
            String supplier = product.getSupplier();
            int price       = product.getPrice();
            int quantity    = product.getQuantity();

            values.put(ProductEntry.NAME_COL, name);
            values.put(ProductEntry.SUPPLIER_COL, supplier);
            values.put(ProductEntry.PRICE_COL, price);
            values.put(ProductEntry.QUANTITU_COL, quantity);

            newRowId = mWritableDB.insert(ProductEntry.TABLE_NAME, null, values);

            if (newRowId == -1) {
                Toast.makeText(mContext,
                        R.string.insertion_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext,
                        R.string.insertion_succeeded, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "insert: Error in Insertion");
        }

        return newRowId;
    }

    public int delete(int id) {
        int rowsDeleted = 0;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            String where = ProductEntry._ID + " = ?";
            String [] whereArgs = new String[] {String.valueOf(id)};

            rowsDeleted = mWritableDB.delete(ProductEntry.TABLE_NAME, where, whereArgs);

            if (rowsDeleted == 0) {
                Toast.makeText(mContext,
                        R.string.delete_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext,
                        R.string.delete_succeeded, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "delete: Deletion Failed" );
        }

        return rowsDeleted;
    }

    public List<Product> query () {
        List<Product> products = new ArrayList<>();

        String [] projection =
                new String[] {
                        ProductEntry._ID,
                        ProductEntry.NAME_COL,
                        ProductEntry.PRICE_COL,
                        ProductEntry.SUPPLIER_COL,
                        ProductEntry.QUANTITU_COL,
                        ProductEntry.IMAGE_COL
                };

        Cursor cursor = null;
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }

            cursor = mReadableDB.query(
                    ProductEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null);

            while (cursor.moveToNext()) {
                Product product = new Product();

                String name = cursor.getString(cursor.getColumnIndex(ProductEntry.NAME_COL));
                String supplier = cursor.getString(cursor.getColumnIndex(ProductEntry.SUPPLIER_COL));
                int price = cursor.getInt(cursor.getColumnIndex(ProductEntry.PRICE_COL));
                int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.QUANTITU_COL));
                //int image       = cursor.getInt(cursor.getColumnIndex(ProductEntry.IMAGE_COL));
                int id = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));

                product.setName(name);
                product.setSupplier(supplier);
                product.setQuantity(quantity);
                product.setPrice(price);
                product.setId(id);

                products.add(product);
            }

        } catch (Exception e) {

            Log.e(TAG, "query: in Query");

        } finally {
            cursor.close();
            return products;
        }
    }

    public Product getProduct (int position) {
        Product product = new Product();

        String [] projection =
                new String[] {
                        ProductEntry._ID,
                        ProductEntry.NAME_COL,
                        ProductEntry.PRICE_COL,
                        ProductEntry.SUPPLIER_COL,
                        ProductEntry.QUANTITU_COL,
                        ProductEntry.IMAGE_COL
                };

        Cursor cursor = null;

        String [] selectionArgs = new String[] {Integer.toString(position)};
        String selection = ProductEntry._ID + " = ?";

        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }

            cursor = mReadableDB.query(
                    ProductEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);

            while (cursor.moveToNext()) {


                String name = cursor.getString(cursor.getColumnIndex(ProductEntry.NAME_COL));
                String supplier = cursor.getString(cursor.getColumnIndex(ProductEntry.SUPPLIER_COL));
                int price = cursor.getInt(cursor.getColumnIndex(ProductEntry.PRICE_COL));
                int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.QUANTITU_COL));
                //int image       = cursor.getInt(cursor.getColumnIndex(ProductEntry.IMAGE_COL));
                int id = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));

                product.setName(name);
                product.setSupplier(supplier);
                product.setQuantity(quantity);
                product.setPrice(price);
                product.setId(id);

            }

        } catch (Exception e) {

            Log.e(TAG, "query: in Query");

        } finally {
            cursor.close();
            return product;
        }
    }

    public int update(Product product) {

        String name = product.getName();
        String supplier = product.getSupplier();
        int price = product.getPrice();
        int quantity = product.getQuantity();
        int id = product.getId();

        ContentValues values = new ContentValues();

        values.put(ProductEntry.NAME_COL, name);
        values.put(ProductEntry.SUPPLIER_COL, supplier);
        values.put(ProductEntry.PRICE_COL, price);
        values.put(ProductEntry.QUANTITU_COL, quantity);

        int updatedRows = 0;

        String [] selectionArgs = new String[] {String.valueOf(id)};
        String selection = ProductEntry._ID + " = ?";

        try {
            if (mReadableDB == null) {
                mReadableDB = getWritableDatabase();
            }

            updatedRows = mReadableDB.update(
                    ProductEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );

            if (updatedRows == 0) {
                Toast.makeText(mContext, "Update Failed", Toast.LENGTH_SHORT).show();
            } else if (updatedRows > 0){
                Toast.makeText(mContext, "Updated Successfully", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {

        }
        return updatedRows;
    }
}
