package com.example.mohamed.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    private ProductContract() {

    }

    public static final class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_PRODUCTS);
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/"+ CONTENT_AUTHORITY +"/"+ PATH_PRODUCTS;

        public static final String TABLE_NAME = "products";

        public static final String _ID          = BaseColumns._ID;
        public static final String NAME_COL     = "name";
        public static final String PRICE_COL    = "price";
        public static final String IMAGE_COL    = "image";
        public static final String QUANTITU_COL = "quantity";
        public static final String SUPPLIER_COL = "supplier";

    }
}
