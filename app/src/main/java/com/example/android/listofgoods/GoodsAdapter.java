package com.example.android.listofgoods;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.listofgoods.date.GoodsContract.GoodsEntry;

public class GoodsAdapter extends CursorAdapter{

    GoodsAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(
                R.layout.item_main_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = new ViewHolder();
        vh.imageView = view.findViewById(R.id.main_image);
        vh.name = view.findViewById(R.id.main_name);
        vh.supplier = view.findViewById(R.id.main_supplier);
        vh.quantity = view.findViewById(R.id.main_quantity);
        vh.price = view.findViewById(R.id.main_price);

        String name = cursor.getString(cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_NAME));
        String supplier = cursor.getString(cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_SUPPLIER));
        int quantity = cursor.getInt(cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_QUANTITY));
        int price = cursor.getInt(cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_PRICE));
        String imageString = cursor.getString(cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_IMAGE));

        vh.name.setText(name);
        vh.supplier.setText(supplier);
        vh.quantity.setText(String.valueOf(quantity));
        vh.price.setText(String.valueOf(price));

        if (imageString == null) {
            vh.imageView.setImageResource(R.drawable.no_image);
        } else {
            // 将保存在数据库的位图字符串转换为位图，并使用
            byte[] bytes = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            vh.imageView.setImageBitmap(bitmap);
        }
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView name;
        TextView supplier;
        TextView quantity;
        TextView price;
    }
}
