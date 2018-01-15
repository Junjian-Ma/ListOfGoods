package com.example.android.listofgoods.date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.listofgoods.date.GoodsContract.GoodsEntry;

public class GoodsDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = GoodsDbHelper.class.getName();

    private static final String DATABASE_NAME = "GoodsDate.db";

    private static final int DATABASE_VERSION = 1;

    GoodsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_GOODS_TABLE = "CREATE TABLE "
                + GoodsEntry.TABLE_NAME + " ("
                + GoodsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GoodsEntry.COLUMN_GOODS_ID + " TEXT NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_MAIN + " INTEGER NOT NULL DEFAULT 0, "
                + GoodsEntry.COLUMN_GOODS_NAME + " TEXT NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_REMARKS + " TEXT, "
                + GoodsEntry.COLUMN_GOODS_SUPPLIER + " TEXT NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_PHONE_NUMBER + " INTEGER NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_TRANSPORT + " INTEGER NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + GoodsEntry.COLUMN_GOODS_SELL_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + GoodsEntry.COLUMN_GOODS_PRICE + " INTEGER NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_SELL_PRICE + " INTEGER NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_TIME + " TEXT, "
                + GoodsEntry.COLUMN_GOODS_IMAGE + " TEXT);";
        Log.i(LOG_TAG, "SQL_CREATE_GOODS_TABLE : " + SQL_CREATE_GOODS_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_GOODS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
