package com.example.android.listofgoods;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.listofgoods.date.GoodsContract.GoodsEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutSellActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private HistoryAdapter historyAdapter;

    @BindView(R.id.pricing_text)
    TextView pricingText;
    @BindView(R.id.remaining_text)
    TextView remainingText;
    @BindView(R.id.diy_quantity_edit)
    EditText writeQuantity;
    @BindView(R.id.diy_price_edit)
    EditText writePrice;

    @BindView(R.id.about_sell_list)
    ListView listView;
    @BindView(R.id.no_has_history_text)
    TextView noHasHistory;
    @BindView(R.id.history_title_text)
    LinearLayout historyTitle;

    private static final int GOODS_ITEM = 0;
    private static final int GOODS_LIST = 1;
    private static final String LOG_TAG = AboutSellActivity.class.getName();

    private Uri mCursorGoodsUri;
    private String mGoodsId;
    private int mMain;
    private String mName;
    private String mRemarks;
    private String mSupplier;
    private int mPhoneNumber;
    private int mTransport;
    private int mQuantity;
    private int mSellQuantity;
    private int mPrice;
    private int mSellPrice;
    private String mImageId;
    private String mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_sell);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mGoodsId = intent.getStringExtra("goods_id");
        mCursorGoodsUri = intent.getData();

        historyAdapter = new HistoryAdapter(this, null);

        listView.setAdapter(historyAdapter);
        listView.setEmptyView(noHasHistory);
        if (listView.getVisibility() != View.VISIBLE) {
            historyTitle.setVisibility(View.GONE);
        } else {
            historyTitle.setVisibility(View.VISIBLE);
        }

        getLoaderManager().initLoader(GOODS_LIST, null, this);
        if (mCursorGoodsUri != null) {
            getLoaderManager().initLoader(GOODS_ITEM, null, this);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_diy_sell:
                sellAndSave();
                break;
            default:
                break;
        }
    }

    private void sellAndSave() {
        String writeQuantityString = writeQuantity.getText().toString();
        String writePriceString = writePrice.getText().toString();

        Utils utils = new Utils();
        if (!writeQuantityString.equals("")
                && !writePriceString.equals("")
                && utils.isInteger(writePriceString)
                && utils.isInteger(writePriceString)) {
            mSellQuantity = Integer.valueOf(writeQuantityString);
            mSellPrice = Integer.valueOf(writePriceString);

            if (mSellQuantity == 0 || mSellPrice == 0) {
                Toast.makeText(this,
                        R.string.canNoBeZero,
                        Toast.LENGTH_SHORT).show();
            } else if (mQuantity == 0) {
                Toast.makeText(this,
                        R.string.soldOut,
                        Toast.LENGTH_SHORT).show();
                return;
            } else if (mQuantity < mSellQuantity) {
                Toast.makeText(this,
                        R.string.writeOverflow,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            mQuantity = mQuantity - mSellQuantity;
        } else {
            Toast.makeText(
                    this,
                    R.string.editTextIsNull,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        saveData();
    }

    private void saveData() {
        ContentValues values = new ContentValues();
        values.put(GoodsEntry.COLUMN_GOODS_MAIN, false);
        int rowsUpdate = getContentResolver().update(
                mCursorGoodsUri, values, null, null);
        if (rowsUpdate == 0) {
            Toast.makeText(this, R.string.updateTextError, Toast.LENGTH_SHORT).show();
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
        contentValues.put(GoodsEntry.COLUMN_GOODS_IMAGE, mImageId);
        contentValues.put(GoodsEntry.COLUMN_GOODS_TIME, mTime);
        mCursorGoodsUri = getContentResolver().insert(GoodsEntry.CONTENT_URI, contentValues);

        getLoaderManager().restartLoader(GOODS_ITEM, null, this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setData(mCursorGoodsUri);
        setResult(RESULT_OK, intent);
        Log.i(LOG_TAG, "===== onBackPressed =====" + mCursorGoodsUri);

        finish();
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

        if (i == GOODS_LIST) {
            String selection = GoodsEntry.COLUMN_GOODS_SELL_QUANTITY + ">0 AND "
                    + GoodsEntry.COLUMN_GOODS_ID + "=?";
            String[] selectionArgs = {String.valueOf(mGoodsId)};
            return new CursorLoader(this,
                    GoodsEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
        } else {
            return new CursorLoader(this,
                    mCursorGoodsUri,
                    projection,
                    null,
                    null,
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == GOODS_LIST) {
            historyAdapter.swapCursor(cursor);
        } else {
            if (cursor.moveToFirst()) {
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
                mPhoneNumber = cursor.getInt(phoneIndex);
                mTransport = cursor.getInt(transportIndex);
                mQuantity = cursor.getInt(quantityIndex);
                mSellQuantity = cursor.getInt(sellQuantityIndex);
                mPrice = cursor.getInt(priceIndex);
                mSellPrice = cursor.getInt(sellPriceIndex);
                mRemarks = cursor.getString(remarkIndex);
                mImageId = cursor.getString(imageIdIndex);
                mTime = cursor.getString(timeIndex);

                remainingText.setText(String.format(getString(R.string.remainingString), mQuantity));
                pricingText.setText(String.format(getString(R.string.pricingString), mPrice));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        historyAdapter.swapCursor(null);
    }
}
