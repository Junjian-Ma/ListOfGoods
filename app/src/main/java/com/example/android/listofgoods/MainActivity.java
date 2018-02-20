package com.example.android.listofgoods;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.android.listofgoods.date.GoodsContract.GoodsEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int GOODS_LOADER = 0;
    private static final String GOODS_COUNT = "GoodsCount";

    private GoodsAdapter goodsAdapter;
    private int goodsCount;

    @BindView(R.id.main_list)
    ListView listView;
    @BindView(R.id.main_no_data)
    ImageView imageView;
    @BindView(R.id.new_goods)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(GOODS_COUNT, goodsCount);
                Log.i(LOG_TAG, "====== mGoodsCount is " + goodsCount);
                startActivity(intent);
            }
        });

        goodsAdapter = new GoodsAdapter(this, null);
        listView.setDividerHeight(0);
        listView.setAdapter(goodsAdapter);
        listView.setEmptyView(imageView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Uri currentGoodsUri = ContentUris.withAppendedId(GoodsEntry.CONTENT_URI, l);
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.setData(currentGoodsUri);
                Log.i(LOG_TAG, "currentGoodsUri ===== " + currentGoodsUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(GOODS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                GoodsEntry._ID,
                GoodsEntry.COLUMN_GOODS_ID,
                GoodsEntry.COLUMN_GOODS_MAIN,
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

        String selection = GoodsEntry.COLUMN_GOODS_MAIN + "=?";
        String[] selectionArgs = {String.valueOf(1)};

        return new CursorLoader(this,
                GoodsEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        goodsAdapter.swapCursor(cursor);
        goodsCount = goodsAdapter.getCount();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        goodsAdapter.swapCursor(null);
    }
}
