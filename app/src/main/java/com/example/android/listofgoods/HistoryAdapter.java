package com.example.android.listofgoods;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.listofgoods.date.GoodsContract.GoodsEntry;

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
        ViewHolder vh = new ViewHolder();
        vh.mAboutQuantity = view.findViewById(R.id.edit_quantity_about);
        vh.mAboutPrice = view.findViewById(R.id.edit_price_about);
        vh.mAboutTime = view.findViewById(R.id.edit_time_about);

        int quantityIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_SELL_QUANTITY);
        int priceIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_SELL_PRICE);
        int timeIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_TIME);

        String quantity = cursor.getString(quantityIndex);
        String price = cursor.getString(priceIndex);
        String time = cursor.getString(timeIndex);

        vh.mAboutQuantity.setText(quantity);
        vh.mAboutPrice.setText(price);
        vh.mAboutTime.setText(time);
    }

    private static class ViewHolder {
        TextView mAboutQuantity;
        TextView mAboutPrice;
        TextView mAboutTime;
    }
}
