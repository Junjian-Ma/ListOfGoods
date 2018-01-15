package com.example.android.listofgoods;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import com.example.android.listofgoods.date.GoodsContract.GoodsEntry;

public class AboutSellActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_sell);

        historyAdapter = new HistoryAdapter(this, null);
        ListView listView = findViewById(R.id.about_sell_list);
        listView.setAdapter(historyAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                GoodsEntry._ID,
                GoodsEntry.COLUMN_GOODS_ID,
                GoodsEntry.COLUMN_GOODS_NAME,
                GoodsEntry.COLUMN_GOODS_REMARKS,
                GoodsEntry.COLUMN_GOODS_SUPPLIER,
                GoodsEntry.COLUMN_GOODS_PHONE_NUMBER,
                GoodsEntry.COLUMN_GOODS_TRANSPORT,
                GoodsEntry.COLUMN_GOODS_QUANTITY,
                GoodsEntry.COLUMN_GOODS_SELL_QUANTITY,
                GoodsEntry.COLUMN_GOODS_PRICE,
                GoodsEntry.COLUMN_GOODS_SELL_PRICE,
                GoodsEntry.COLUMN_GOODS_TIME,
                GoodsEntry.COLUMN_GOODS_IMAGE};
        return new CursorLoader(this,
                GoodsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        historyAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        historyAdapter.swapCursor(null);
    }
}
