package com.example.oya.inventoryapp.utils;

public final class Constants {

    public final static String RELATION_TYPE = "relationType";

    public final static String CLIENT = "Client";

    public final static String SUPPLIER = "Supplier";

    public final static String TRANSACTION_TYPE = "transactionType";

    public final static String DELIVERY = "delivery";

    public final static String ACQUISITION = "acquisition";

    public final static String ADD_PRODUCT_FRAGMENT = "addProductFragment";

    public final static String ADD_TRANSACTION_FRAGMENT = "addTransactionFragment";

    //Loader ids

    public final static int PRODUCT_LOADER_ID = 1234;

    public final static int SINGLE_PRODUCT_LOADER = 1235;

    public final static int TRANSACTION_LOADER_ID = 4567;

    public final static int ENTERPRISE_LOADER_ID = 7894;

    public final static int SINGLE_ENTERPRISE_LOADER = 7895;

    //Keys for intent extras

    public final static String REQUEST_CODE = "requestCode";

    public final static String PRODUCT_URI = "productUri";

    public final static String PRODUCT_ID = "productId";

    public final static String PRODUCT_NAME = "productName";

    public final static String ENTERPRISE_URI = "enterpriseUri";

    public final static String IS_FAB_CLICKED = "isFabClicked";

    //Provider name for saving the image

    public static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider";

    //Request codes:

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final int REQUEST_STORAGE_PERMISSION = 2;

    public static final int PICK_IMAGE_REQUEST = 3;

    //Keys used for SharedPreferences

    public static final String FIRST_TAPPROMPT_IS_SHOWN = "firstTapPromptShown";

    public static final String SECOND_TAPPROMPT_IS_SHOWN = "secondTapPromptShown";

    public static final String THIRD_TAPPROMPT_IS_SHOWN = "thirdTapPromptShown";

}
