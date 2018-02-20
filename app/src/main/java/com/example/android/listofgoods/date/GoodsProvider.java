package com.example.android.listofgoods.date;

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

import com.example.android.listofgoods.date.GoodsContract.GoodsEntry;
import com.example.android.listofgoods.R;

public class GoodsProvider extends ContentProvider {

    private static final String LOG_TAG = ContentProvider.class.getName();

    private static final int GOODS = 1;
    private static final int GOODS_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(GoodsContract.CONTENT_AUTHORITY, GoodsEntry.TABLE_NAME, GOODS);
        sUriMatcher.addURI(GoodsContract.CONTENT_AUTHORITY, GoodsEntry.TABLE_NAME + "/#", GOODS_ID);
    }

    private GoodsDbHelper mGoodsDbHelper;

    @Override
    public boolean onCreate() {
        mGoodsDbHelper = new GoodsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mGoodsDbHelper.getReadableDatabase();

        Cursor cursor;

        final int match = sUriMatcher.match(uri);

        if (getContext() == null) {
            Log.i(LOG_TAG, "getContext() is null");
            return null;
        }

        switch (match) {
            case GOODS:
                cursor = database.query(GoodsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case GOODS_ID:
                selection = GoodsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(GoodsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException(
                        getContext().getString(R.string.cannotQuery) + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                return GoodsEntry.CONTENT_LIST_TYPE;
            case GOODS_ID:
                return GoodsEntry.CONTENT_ITEM_TYPE;
            default:
                if (getContext() != null) {
                    throw new IllegalStateException(
                            getContext().getString(R.string.unKnownUri) + uri);
                } else {
                    Log.i(LOG_TAG, "getContext() is null");
                }
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                return insertGoods(uri, contentValues);
            default:
                if (getContext() == null) {
                    Log.i(LOG_TAG, "getContext() is null");
                    return null;
                }
                throw new IllegalArgumentException(
                        getContext().getString(R.string.insertNotSupported) + uri);
        }
    }

    private Uri insertGoods(Uri uri, ContentValues contentValues) {
        if (getContext() == null) {
            Log.i(LOG_TAG, "getContext() is null");
            return null;
        }
        String goodsId = contentValues.getAsString(GoodsEntry.COLUMN_GOODS_ID);
        if (goodsId == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.goodsIdNull));
        }
        Boolean goodsMain = contentValues.getAsBoolean(GoodsEntry.COLUMN_GOODS_MAIN);
        if (goodsMain == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.goodsIdNull));
        }
        String name = contentValues.getAsString(GoodsEntry.COLUMN_GOODS_NAME);
        if (name == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.nameIsNull));
        }
        String supplier = contentValues.getAsString(GoodsEntry.COLUMN_GOODS_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.supplierIsNull));
        }
        String phoneNumber = contentValues.getAsString(GoodsEntry.COLUMN_GOODS_PHONE_NUMBER);
        if (phoneNumber == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.numberIsNull));
        }
        Integer transport = contentValues.getAsInteger(GoodsEntry.COLUMN_GOODS_TRANSPORT);
        if (transport == null || !isValidTransport(transport)) {
            throw new IllegalArgumentException(getContext().getString(R.string.transportIsNull));
        }
        Integer quantity = contentValues.getAsInteger(GoodsEntry.COLUMN_GOODS_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException(getContext().getString(R.string.quantityIsNull));
        }
        Integer sellQuantity = contentValues.getAsInteger(GoodsEntry.COLUMN_GOODS_SELL_QUANTITY);
        if (sellQuantity != null && sellQuantity < 0) {
            throw new IllegalArgumentException(getContext().getString(R.string.sellQuantityIsNull));
        }
        Integer price = contentValues.getAsInteger(GoodsEntry.COLUMN_GOODS_PRICE);
        if (price == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.priceIsNull));
        }
        Integer sellPrice = contentValues.getAsInteger(GoodsEntry.COLUMN_GOODS_SELL_PRICE);
        if (sellPrice == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.sellPriceIsNull));
        }

        SQLiteDatabase database = mGoodsDbHelper.getWritableDatabase();
        long id = database.insert(GoodsEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, getContext().getString(R.string.insertError) + uri);
            return null;
        }
        // 后台自动更新加载
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private boolean isValidTransport(int transport) {
        return transport == GoodsEntry.COLUMN_TRANSPORT_LAND ||
                transport == GoodsEntry.COLUMN_TRANSPORT_AIR ||
                transport == GoodsEntry.COLUMN_TRANSPORT_SEA;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mGoodsDbHelper.getWritableDatabase();
        int rowsDelete;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                rowsDelete = database.delete(GoodsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case GOODS_ID:
                selection = GoodsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDelete = database.delete(GoodsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                if (getContext() == null) {
                    Log.i(LOG_TAG, "getContext() is null");
                    return 0;
                }
                throw new IllegalArgumentException(
                        getContext().getString(R.string.deleteError) + uri);
        }
        // 如果更新的行数不等于 0，后台自动更新加载
        if (rowsDelete != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDelete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                return updateGoods(uri, contentValues, selection, selectionArgs);
            case GOODS_ID:
                selection = GoodsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateGoods(uri, contentValues, selection, selectionArgs);
            default:
                if (getContext() == null) {
                    Log.i(LOG_TAG, "getContext() is null");
                    return 0;
                }
                throw new IllegalArgumentException(
                        getContext().getString(R.string.updateError) + uri);
        }
    }

    private int updateGoods(Uri uri, ContentValues contentValues,
                            String selection, String[] selectionArgs) {
        if (getContext() == null) {
            Log.i(LOG_TAG, "getContext() is null");
            return 0;
        }
        if (contentValues.containsKey(GoodsEntry.COLUMN_GOODS_ID)) {
            String goodsId = contentValues.getAsString(GoodsEntry.COLUMN_GOODS_ID);
            if (goodsId == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.goodsIdNull));
            }
        }
        if (contentValues.containsKey(GoodsEntry.COLUMN_GOODS_MAIN)) {
            Boolean goodsMain = contentValues.getAsBoolean(GoodsEntry.COLUMN_GOODS_MAIN);
            if (goodsMain == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.goodsIdNull));
            }
        }
        if (contentValues.containsKey(GoodsEntry.COLUMN_GOODS_NAME)) {
            String name = contentValues.getAsString(GoodsEntry.COLUMN_GOODS_NAME);
            if (name == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.nameIsNull));
            }
        }
        if (contentValues.containsKey(GoodsEntry.COLUMN_GOODS_SUPPLIER)) {
            String supplier = contentValues.getAsString(GoodsEntry.COLUMN_GOODS_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.supplierIsNull));
            }
        }
        if (contentValues.containsKey(GoodsEntry.COLUMN_GOODS_PHONE_NUMBER)) {
            String phoneNumber = contentValues.getAsString(GoodsEntry.COLUMN_GOODS_PHONE_NUMBER);
            if (phoneNumber == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.numberIsNull));
            }
        }
        if (contentValues.containsKey(GoodsEntry.COLUMN_GOODS_TRANSPORT)) {
            Integer transport = contentValues.getAsInteger(GoodsEntry.COLUMN_GOODS_TRANSPORT);
            if (transport == null || !isValidTransport(transport)) {
                throw new IllegalArgumentException(getContext().getString(R.string.transportIsNull));
            }
        }
        if (contentValues.containsKey(GoodsEntry.COLUMN_GOODS_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(GoodsEntry.COLUMN_GOODS_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException(getContext().getString(R.string.quantityIsNull));
            }
        }
        if (contentValues.containsKey(GoodsEntry.COLUMN_GOODS_QUANTITY)) {
            Integer sellQuantity = contentValues.getAsInteger(GoodsEntry.COLUMN_GOODS_SELL_QUANTITY);
            if (sellQuantity != null && sellQuantity < 0) {
                throw new IllegalArgumentException(getContext().getString(R.string.sellQuantityIsNull));
            }
        }
        if (contentValues.containsKey(GoodsEntry.COLUMN_GOODS_PRICE)) {
            Integer price = contentValues.getAsInteger(GoodsEntry.COLUMN_GOODS_PRICE);
            if (price == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.priceIsNull));
            }
        }
        if (contentValues.containsKey(GoodsEntry.COLUMN_GOODS_SELL_PRICE)) {
            Integer sell_price = contentValues.getAsInteger(GoodsEntry.COLUMN_GOODS_SELL_PRICE);
            if (sell_price == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.sellPriceIsNull));
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mGoodsDbHelper.getWritableDatabase();
        int rowsUpdate = database.update(GoodsEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs);

        if (rowsUpdate != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdate;

    }
}
