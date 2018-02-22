package com.example.android.listofgoods.date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.listofgoods.date.GoodsContract.GoodsEntry;

public class GoodsDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = GoodsDbHelper.class.getName();

    private static final String DATABASE_NAME = "GoodsData.db";

    private static final int DATABASE_VERSION = 1;

    GoodsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String columns = GoodsEntry._ID + ", "
                + GoodsEntry.COLUMN_GOODS_ID + ", "
                + GoodsEntry.COLUMN_GOODS_MAIN + ", "
                + GoodsEntry.COLUMN_GOODS_NAME + ", "
                + GoodsEntry.COLUMN_GOODS_REMARKS + ", "
                + GoodsEntry.COLUMN_GOODS_SUPPLIER + ", "
                + GoodsEntry.COLUMN_GOODS_PHONE_NUMBER + ", "
                + GoodsEntry.COLUMN_GOODS_TRANSPORT + ", "
                + GoodsEntry.COLUMN_GOODS_QUANTITY + ", "
                + GoodsEntry.COLUMN_GOODS_SELL_QUANTITY + ", "
                + GoodsEntry.COLUMN_GOODS_PRICE + ", "
                + GoodsEntry.COLUMN_GOODS_SELL_PRICE + ", "
                + GoodsEntry.COLUMN_GOODS_TIME + ", "
                + GoodsEntry.COLUMN_GOODS_IMAGE;

        switch (i) {
            case 1:
                createTable(db);
            case 2:
                upgradeTable(db, columns);
                break;
            default:
                break;
        }
        Log.i(LOG_TAG, "===============  u p");
    }

    private void createTable(SQLiteDatabase db) {
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

        db.execSQL(SQL_CREATE_GOODS_TABLE);
    }

    private void upgradeTable(SQLiteDatabase db, String columns) {
        String tableName_temp = GoodsEntry.TABLE_NAME + "_temp";
        String sql = "ALTER TABLE " + GoodsEntry.TABLE_NAME + " RENAME TO " + tableName_temp;
        db.execSQL(sql);

        String SQL_CREATE_GOODS_TABLE = "CREATE TABLE "
                + GoodsEntry.TABLE_NAME + " ("
                + GoodsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GoodsEntry.COLUMN_GOODS_ID + " TEXT NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_MAIN + " INTEGER NOT NULL DEFAULT 0, "
                + GoodsEntry.COLUMN_GOODS_NAME + " TEXT NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_REMARKS + " TEXT, "
                + GoodsEntry.COLUMN_GOODS_SUPPLIER + " TEXT NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_PHONE_NUMBER + " TEXT NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_TRANSPORT + " INTEGER NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + GoodsEntry.COLUMN_GOODS_SELL_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + GoodsEntry.COLUMN_GOODS_PRICE + " INTEGER NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_SELL_PRICE + " INTEGER NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_TIME + " TEXT, "
                + GoodsEntry.COLUMN_GOODS_IMAGE + " TEXT);";
        db.execSQL(SQL_CREATE_GOODS_TABLE);

        sql = "INSERT INTO " + GoodsEntry.TABLE_NAME
                + " (" + columns + ")"
                + " SELECT "
                + columns
                + " FROM " + tableName_temp;
        db.execSQL(sql);

        String dropTable = "DROP TABLE IF EXISTS " + tableName_temp;
        db.execSQL(dropTable);
    }
}
