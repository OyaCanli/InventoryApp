package com.example.oya.inventoryapp.data;

import android.provider.BaseColumns;

public final class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {
    }

    public static final class ProductEntry implements BaseColumns {

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
    }

    public static final class EnterpriseEntry implements BaseColumns {

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


}
