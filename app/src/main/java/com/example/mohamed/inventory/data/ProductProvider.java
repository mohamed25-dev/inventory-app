package com.example.mohamed.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.mohamed.inventory.R;

import static com.example.mohamed.inventory.data.ProductContract.*;

public class ProductProvider extends ContentProvider {
    public static final String TAG = ProductProvider.class.getSimpleName();
    public static final int PRODUCTS   = 100;
    public static final int PRODUCT_ID = 101;
    private ProductDbHelper mDbHelper;

    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper   = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return queryProducts(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int matcher = sUriMatcher.match(uri);
        switch (matcher) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("uri is not supported "+uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return deleteProduct(uri, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        selection = ProductEntry._ID + " = ?";
        selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

        return updateProduct(uri, values, selection, selectionArgs);
    }

    private Uri insertProduct(Uri uri, ContentValues values) {


        checkContentValue(values);

        SQLiteDatabase mWritableDB = mDbHelper.getWritableDatabase();
        long id = mWritableDB.insert(ProductEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(TAG, getContext().getString(R.string.insertion_failed));
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String [] selectionArgs) {

        checkContentValue(values);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int matcher = sUriMatcher.match(uri);
        switch (matcher) {

            case PRODUCTS:
                return database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
            case PRODUCT_ID:
                return database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
            case UriMatcher.NO_MATCH:
                Log.e(TAG, "updateProduct: Not matcher uri");
                return 0;
            default:
                throw new IllegalArgumentException("Error in handling the matcher");
        }

    }

    private int deleteProduct (@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int matcher = sUriMatcher.match(uri);

        switch (matcher){
            case PRODUCT_ID:
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                database                = mDbHelper.getWritableDatabase();

                selection     = ProductEntry._ID + " = ?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                return database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
            case PRODUCTS :
                Log.e(TAG, "deleteProduct: unsupported Uri "+uri);
                return 0;
            case UriMatcher.NO_MATCH:
                Log.e(TAG, "deleteProduct: unknown Uri "+uri);
            default:
                throw new IllegalArgumentException("Error in the Matcher!!");
        }

    }

    private Cursor queryProducts(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        final int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case PRODUCTS:
                return database.query(ProductEntry.TABLE_NAME, null, null, null, null, null, null);
            case PRODUCT_ID:
                selection     = ProductEntry._ID + "= ?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return database.query(ProductEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
            case UriMatcher.NO_MATCH:
                Log.e(TAG, "query: No Matcher Uri");
                return null;
            default:
                throw new IllegalArgumentException("unsupported Uri " + uri);
        }
    }

    //used to check whether the values in a content value are valid or not;
    private void checkContentValue (ContentValues values) {
        String name = values.getAsString(ProductEntry.NAME_COL);
        if (name == null) {
            throw new IllegalArgumentException("Name cant be null");
        }

        String supplier = values.getAsString(ProductEntry.SUPPLIER_COL);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cant be null");
        }

        Integer quantity = values.getAsInteger(ProductEntry.QUANTITU_COL);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Quantity cant be less than one");
        }

        Integer price = values.getAsInteger(ProductEntry.PRICE_COL);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("price cant be less than one");
        }
    }
}
