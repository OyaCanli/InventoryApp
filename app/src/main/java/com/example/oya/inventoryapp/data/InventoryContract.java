package com.example.oya.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    public static final String AUTHORITY = "com.example.oya.inventoryapp";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_PRODUCTS = "products";

    static final String PATH_ENTERPRISES = "enterprises";

    public static final String PATH_TRANSACTIONS = "transactions";


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {
    }

    public static final class ProductEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_PRODUCTS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        //Type: TEXT
        public final static String TABLE_NAME = "products";

        //Type: INTEGER
        public final static String _ID = BaseColumns._ID;

        //Type: TEXT
        public final static String PRODUCT_NAME = "productName";

        //Type: REAL
        public final static String SALE_PRICE = "productSalePrice";

        //Type: INTEGER
        public final static String QUANTITY_IN_STOCK = "quantityInStock";

        //Type: TEXT
        public final static String SUPPLIER_NAME = "supplierName";

        //Image file path. Type: Text
        public final static String IMAGE_FILE_PATH = "imageFilePath";
    }

    public static final class EnterpriseEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_ENTERPRISES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_ENTERPRISES;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ENTERPRISES);

        //Type: INTEGER
        public final static String _ID = BaseColumns._ID;

        //Type: TEXT
        public final static String TABLE_NAME = "enterprises";

        //Type: TEXT
        public final static String ENTERPRISE_NAME = "enterpriseName";

        //Type: TEXT
        public final static String ENTERPRISE_PHONE = "enterprisePhone";

        //Type: TEXT
        public final static String ENTERPRISE_ADDRESS = "enterpriseAddress";

        //Type: TEXT
        public final static String ENTERPRISE_EMAIL = "enterpriseEmail";

        //Type: TEXT
        public final static String ENTERPRISE_CONTACT_PERSON = "enterpriseContactPerson";

        //Type: TEXT
        public final static String RELATION_TYPE = "relationType";
    }


    public static final class TransactionEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TRANSACTIONS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TRANSACTIONS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TRANSACTIONS);

        //Type: INTEGER
        public final static String _ID = BaseColumns._ID;

        //Type: TEXT
        public final static String TABLE_NAME = "transactions";

        //Type: TEXT
        public final static String ENTERPRISE_NAME = "enterpriseName";

        //Type: TEXT
        public final static String PRODUCT_NAME = "productName";

        //Type: TEXT
        public final static String QUANTITY = "quantity";

        //Type: TEXT
        public final static String PRICE = "price";

        //Type: TEXT
        public final static String TRANSACTION_DATE = "transactionDate";

        //Type: TEXT
        public final static String TRANSACTION_TYPE = "transactionType";
    }

}
