package com.example.android.listofgoods.date;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class GoodsContract {

    static final String CONTENT_AUTHORITY = "com.example.android.listofgoods";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_GOODS = "goods";

    private GoodsContract() {
    }

    public static class GoodsEntry implements BaseColumns {
        // 指定 MIME 列表类型常量
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_GOODS;

        // 指定 MIME 个数类型常量
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.ANY_CURSOR_ITEM_TYPE + "/" + CONTENT_AUTHORITY + PATH_GOODS;

        static final String TABLE_NAME = "goods";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GOODS);

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_GOODS_ID = "goods_id";
        public static final String COLUMN_GOODS_MAIN = "main";
        public static final String COLUMN_GOODS_NAME = "name";
        public static final String COLUMN_GOODS_REMARKS = "remarks";// 备注
        public static final String COLUMN_GOODS_SUPPLIER = "supplier";// 供应商
        public static final String COLUMN_GOODS_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_GOODS_TRANSPORT = "transport";// 运输
        public static final String COLUMN_GOODS_QUANTITY = "quantity";// 当前数量
        public static final String COLUMN_GOODS_SELL_QUANTITY = "sell_quantity";// 出售时的数量
        public static final String COLUMN_GOODS_PRICE = "price";// 价格
        public static final String COLUMN_GOODS_SELL_PRICE = "sell_price";// 出售价格
        public static final String COLUMN_GOODS_TIME = "time";
        public static final String COLUMN_GOODS_IMAGE = "image";

        public static final int COLUMN_TRANSPORT_LAND = 0;// 陆运
        public static final int COLUMN_TRANSPORT_AIR = 1;// 空运
        public static final int COLUMN_TRANSPORT_SEA = 2;// 海运
    }
}
