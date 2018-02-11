package com.example.android.listofgoods;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.listofgoods.date.GoodsContract.GoodsEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryAdapter extends CursorAdapter {

    HistoryAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(
                R.layout.item_sell_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = new ViewHolder(view);

        int quantityIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_SELL_QUANTITY);
        int priceIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_SELL_PRICE);
        int timeIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_TIME);

        String quantity = cursor.getString(quantityIndex);
        String price = cursor.getString(priceIndex);
        String time = cursor.getString(timeIndex);

        vh.aboutQuantity.setText(quantity);
        vh.aboutPrice.setText(price);
        vh.aboutTime.setText(time);
    }

    static class ViewHolder {
        @BindView(R.id.edit_quantity_about) TextView aboutQuantity;
        @BindView(R.id.edit_price_about) TextView aboutPrice;
        @BindView(R.id.edit_time_about) TextView aboutTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
