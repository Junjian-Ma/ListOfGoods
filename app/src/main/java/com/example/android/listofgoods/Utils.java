package com.example.android.listofgoods;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class Utils {
//    private static final String[] PROJECTION = {
//            GoodsEntry._ID,
//            GoodsEntry.COLUMN_GOODS_ID,
//            GoodsEntry.COLUMN_GOODS_NAME,
//            GoodsEntry.COLUMN_GOODS_REMARKS,
//            GoodsEntry.COLUMN_GOODS_SUPPLIER,
//            GoodsEntry.COLUMN_GOODS_PHONE_NUMBER,
//            GoodsEntry.COLUMN_GOODS_TRANSPORT,
//            GoodsEntry.COLUMN_GOODS_QUANTITY,
//            GoodsEntry.COLUMN_GOODS_SELL_QUANTITY,
//            GoodsEntry.COLUMN_GOODS_PRICE,
//            GoodsEntry.COLUMN_GOODS_SELL_PRICE,
//            GoodsEntry.COLUMN_GOODS_TIME,
//            GoodsEntry.COLUMN_GOODS_IMAGE};

//    static String[] getProjection() {
//        return PROJECTION;
//    }

    void composeEmail(Context context, String data) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, (String[]) null);
        intent.putExtra(Intent.EXTRA_SUBJECT, data);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy/MM/dd, HH:mm", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }
}
