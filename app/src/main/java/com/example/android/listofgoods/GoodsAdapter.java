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

//    private String mGoodsId;
//    private int mMain;
//    private String mName;
//    private String mSupplier;
//    private String mPhoneNumber;
//    private int mTransport;
//    private int mQuantity;
//    private int mSellQuantity;
//    private int mPrice;
//    private int mSellPrice;
//    private String mRemarks;
//    private String mImageId;
//    private String mTime;
//
//    private int mButtonId;

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
        final GoodBean goodBean = new GoodBean();

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

        goodBean.mGoodsId = cursor.getString(goodsIdIndex);
        goodBean.mMain = cursor.getInt(mainIndex);
        goodBean.mName = cursor.getString(nameIndex);
        goodBean.mSupplier = cursor.getString(supplierIndex);
        goodBean.mPhoneNumber = cursor.getString(phoneIndex);
        goodBean.mTransport = cursor.getInt(transportIndex);
        goodBean.mQuantity = cursor.getInt(quantityIndex);
        goodBean.mSellQuantity = cursor.getInt(sellQuantityIndex);
        goodBean.mPrice = cursor.getInt(priceIndex);
        goodBean.mSellPrice = cursor.getInt(sellPriceIndex);
        goodBean.mRemarks = cursor.getString(remarkIndex);
        goodBean.mImageId = cursor.getString(imageIdIndex);
        goodBean.mTime = cursor.getString(timeIndex);

        goodBean.mButtonId = cursor.getInt(cursor.getColumnIndex(GoodsEntry._ID));

        String supplierText = String.format(context.getString(R.string.supplierList_main), goodBean.mSupplier);
        String quantityText = String.format(context.getString(R.string.remainingList_main), goodBean.mQuantity);
        String priceText = String.format(context.getString(R.string.pricingList_main), goodBean.mPrice);

        vh.nameText.setText(goodBean.mName);
        vh.supplierText.setText(supplierText);
        vh.quantityText.setText(String.valueOf(quantityText));
        vh.priceText.setText(String.valueOf(priceText));

        vh.sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCursorGoodsUri = ContentUris.withAppendedId(
                        GoodsEntry.CONTENT_URI,
                        goodBean.mButtonId);
                Log.i(LOG_TAG, "Uri ===== " + mCursorGoodsUri);
                sellGoods(goodBean);
            }
        });

        if (goodBean.mImageId == null) {
            vh.imageView.setImageResource(R.drawable.no_image);
        } else {
            // 将保存在数据库的位图字符串转换为位图，并使用
            byte[] bytes = Base64.decode(goodBean.mImageId, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            vh.imageView.setImageBitmap(bitmap);
        }
    }

    private void sellGoods(GoodBean goodBean) {
        if(goodBean.mQuantity == 0) {
            Toast.makeText(mContext, R.string.soldOut, Toast.LENGTH_SHORT).show();
        } else {
            goodBean.mQuantity = goodBean.mQuantity - 1;
            saveGoods(goodBean);
        }
    }

    private void saveGoods(GoodBean goodBean) {
        ContentValues values = new ContentValues();
        values.put(GoodsEntry.COLUMN_GOODS_MAIN, false);
        int rowsUpdate = mContext.getContentResolver().update(
                mCursorGoodsUri, values, null, null);
        if (rowsUpdate == 0) {
            Toast.makeText(mContext, R.string.updateTextError, Toast.LENGTH_SHORT).show();
        }

        goodBean.mSellQuantity = 1;

        ContentValues contentValues = new ContentValues();
        contentValues.put(GoodsEntry.COLUMN_GOODS_ID, goodBean.mGoodsId);
        contentValues.put(GoodsEntry.COLUMN_GOODS_MAIN, goodBean.mMain);
        contentValues.put(GoodsEntry.COLUMN_GOODS_NAME, goodBean.mName);
        contentValues.put(GoodsEntry.COLUMN_GOODS_REMARKS, goodBean.mRemarks);
        contentValues.put(GoodsEntry.COLUMN_GOODS_SUPPLIER, goodBean.mSupplier);
        contentValues.put(GoodsEntry.COLUMN_GOODS_PHONE_NUMBER, goodBean.mPhoneNumber);
        contentValues.put(GoodsEntry.COLUMN_GOODS_TRANSPORT, goodBean.mTransport);
        contentValues.put(GoodsEntry.COLUMN_GOODS_QUANTITY, goodBean.mQuantity);
        contentValues.put(GoodsEntry.COLUMN_GOODS_SELL_QUANTITY, goodBean.mSellQuantity);
        contentValues.put(GoodsEntry.COLUMN_GOODS_PRICE, goodBean.mPrice);
        contentValues.put(GoodsEntry.COLUMN_GOODS_SELL_PRICE, goodBean.mSellPrice);
        contentValues.put(GoodsEntry.COLUMN_GOODS_TIME, goodBean.mTime);
        contentValues.put(GoodsEntry.COLUMN_GOODS_IMAGE, goodBean.mImageId);

        mCursorGoodsUri = mContext.getContentResolver().insert(GoodsEntry.CONTENT_URI, contentValues);
    }

    public class GoodBean {
        int mButtonId;
        String mGoodsId;
        int mMain;
        String mName;
        String mSupplier;
        String mPhoneNumber;
        int mTransport;
        int mQuantity;
        int mSellQuantity;
        int mPrice;
        int mSellPrice;
        String mRemarks;
        String mImageId;
        String mTime;
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
