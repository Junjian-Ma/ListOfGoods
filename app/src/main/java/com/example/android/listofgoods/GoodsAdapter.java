package com.example.android.listofgoods;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.listofgoods.date.GoodsContract.GoodsEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoodsAdapter extends CursorAdapter {

    private static final String LOG_TAG = GoodsAdapter.class.getName();

    private Uri mCursorGoodsUri;
    private Context mContext;

    private String mGoodsId;
    private int mMain;
    private String mName;
    private String mSupplier;
    private String mPhoneNumber;
    private int mTransport;
    private int mQuantity;
    private int mSellQuantity;
    private int mPrice;
    private int mSellPrice;
    private String mRemarks;
    private String mImageId;
    private String mTime;

    private int mButtonId;

    GoodsAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(
                R.layout.item_main_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder vh = new ViewHolder(view);

        int goodsIdIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_ID);
        int mainIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_MAIN);
        int nameIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_NAME);
        int remarkIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_REMARKS);
        int supplierIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_SUPPLIER);
        int phoneIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_PHONE_NUMBER);
        int transportIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_TRANSPORT);
        int quantityIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_QUANTITY);
        int sellQuantityIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_SELL_QUANTITY);
        int priceIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_PRICE);
        int sellPriceIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_SELL_PRICE);
        int imageIdIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_IMAGE);
        int timeIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_TIME);

        mGoodsId = cursor.getString(goodsIdIndex);
        mMain = cursor.getInt(mainIndex);
        mName = cursor.getString(nameIndex);
        mSupplier = cursor.getString(supplierIndex);
        mPhoneNumber = cursor.getString(phoneIndex);
        mTransport = cursor.getInt(transportIndex);
        mQuantity = cursor.getInt(quantityIndex);
        mSellQuantity = cursor.getInt(sellQuantityIndex);
        mPrice = cursor.getInt(priceIndex);
        mSellPrice = cursor.getInt(sellPriceIndex);
        mRemarks = cursor.getString(remarkIndex);
        mImageId = cursor.getString(imageIdIndex);
        mTime = cursor.getString(timeIndex);

        mButtonId = cursor.getInt(cursor.getColumnIndex(GoodsEntry._ID));

        String supplierText = String.format(context.getString(R.string.supplierList_main), mSupplier);
        String quantityText = String.format(context.getString(R.string.remainingList_main), mQuantity);
        String priceText = String.format(context.getString(R.string.pricingList_main), mPrice);

        vh.nameText.setText(mName);
        vh.supplierText.setText(supplierText);
        vh.quantityText.setText(String.valueOf(quantityText));
        vh.priceText.setText(String.valueOf(priceText));

        vh.sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCursorGoodsUri = ContentUris.withAppendedId(
                        GoodsEntry.CONTENT_URI,
                        mButtonId);
                Log.i(LOG_TAG, "Uri ===== " + mCursorGoodsUri);
                sellGoods();
            }
        });

        if (mImageId == null) {
            vh.imageView.setImageResource(R.drawable.no_image);
        } else {
            // 将保存在数据库的位图字符串转换为位图，并使用
            byte[] bytes = Base64.decode(mImageId, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            vh.imageView.setImageBitmap(bitmap);
        }

    }

    private void sellGoods() {
        if(mQuantity == 0) {
            Toast.makeText(mContext, R.string.soldOut, Toast.LENGTH_SHORT).show();
        } else {
            mQuantity = mQuantity - 1;
            saveGoods();
        }
    }

    private void saveGoods() {
        ContentValues values = new ContentValues();
        values.put(GoodsEntry.COLUMN_GOODS_MAIN, false);
        int rowsUpdate = mContext.getContentResolver().update(
                mCursorGoodsUri, values, null, null);
        if (rowsUpdate == 0) {
            Toast.makeText(mContext, R.string.updateTextError, Toast.LENGTH_SHORT).show();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(GoodsEntry.COLUMN_GOODS_ID, mGoodsId);
        contentValues.put(GoodsEntry.COLUMN_GOODS_MAIN, mMain);
        contentValues.put(GoodsEntry.COLUMN_GOODS_NAME, mName);
        contentValues.put(GoodsEntry.COLUMN_GOODS_REMARKS, mRemarks);
        contentValues.put(GoodsEntry.COLUMN_GOODS_SUPPLIER, mSupplier);
        contentValues.put(GoodsEntry.COLUMN_GOODS_PHONE_NUMBER, mPhoneNumber);
        contentValues.put(GoodsEntry.COLUMN_GOODS_TRANSPORT, mTransport);
        contentValues.put(GoodsEntry.COLUMN_GOODS_QUANTITY, mQuantity);
        contentValues.put(GoodsEntry.COLUMN_GOODS_SELL_QUANTITY, mSellQuantity);
        contentValues.put(GoodsEntry.COLUMN_GOODS_PRICE, mPrice);
        contentValues.put(GoodsEntry.COLUMN_GOODS_SELL_PRICE, mSellPrice);
        contentValues.put(GoodsEntry.COLUMN_GOODS_TIME, mTime);
        contentValues.put(GoodsEntry.COLUMN_GOODS_IMAGE, mImageId);

        mCursorGoodsUri = mContext.getContentResolver().insert(GoodsEntry.CONTENT_URI, contentValues);
    }

    static class ViewHolder {
        @BindView(R.id.main_image)
        ImageView imageView;
        @BindView(R.id.main_name)
        TextView nameText;
        @BindView(R.id.main_supplier)
        TextView supplierText;
        @BindView(R.id.main_quantity)
        TextView quantityText;
        @BindView(R.id.main_price)
        TextView priceText;
        @BindView(R.id.main_item_sell_button)
        Button sellButton;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
