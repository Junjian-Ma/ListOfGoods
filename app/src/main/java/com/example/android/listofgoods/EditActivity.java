package com.example.android.listofgoods;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.listofgoods.date.GoodsContract.GoodsEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private LinearLayout mDisplay_layout;
    private LinearLayout mEdit_layout;

    private TextView mNameView;
    private TextView mGoodsIdView;
    private TextView mSupplierView;
    private TextView mPhoneNumberView;
    private TextView mTransportView;
    private TextView mQuantityView;
    private TextView mPriceView;
    private TextView mRemarksView;

    private EditText mName_edit;
    private EditText mSupplier_edit;
    private EditText mPhone_number_edit;
    private EditText mQuantity_edit;
    private EditText mPrice_edit;
    private EditText mRemarks_edit;

    private Spinner mSpinnerView;
    private ImageView mImageView;
    private Button mBuyButton;
    private Button mSellButton;
    private Button mSellAboutButton;

    private FloatingActionButton mFloatingActionButton;

    private static final int PICK_IMAGE = 1;
    private static final int PICK_URI = 0;
    private static final int GOODS_EDITOR = 0;
    private static final String GOODS_COUNT = "GoodsCount";

    // 设定一个 boolean 来判断是否用户已经更改输入框等的内容
    private boolean mGoodsHasChanged = false;
    // 设定一个 boolean 来判断是否为 编辑模式
    private boolean mIsEditing = false;

    private int mGoodsIdNumber;
    private String mGoodsId = null;
    private int mTransportId = 0;
    private int mQuantity;
    private Uri mCursorGoodsUri;
    private static Bitmap mImageBitmap;
    private static final String LOG_TAG = EditActivity.class.getName();

    private Utils mUtils;

    // 使用 OnTouchListener 检查输入框等是否发生了变化。
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mGoodsHasChanged = true;
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUtils = new Utils();

        // 把标题设置为空
        setTitle("");
        // 设置所有部件的 ID
        startFindViewById();

        // 传递单个货物的 Uri
        Intent intent = getIntent();
        mCursorGoodsUri = intent.getData();
        Log.i(LOG_TAG, String.valueOf(mCursorGoodsUri));
        mGoodsIdNumber = intent.getIntExtra(GOODS_COUNT, 0) + 1;

        // 判断 mCursorGoodsUri 是否为空，为空则说明当前为 “新建”， 反之为 “编辑”
        if (mCursorGoodsUri == null) {
            toggleEditMode(false);
            mIsEditing = true;
        } else {
            toggleEditMode(true);
            mIsEditing = false;
            getLoaderManager().initLoader(GOODS_EDITOR, null, this);
        }

        // 设置 “编辑或保存” 按钮参数
        FloatingActionButton editOrSave = findViewById(R.id.edit_or_save);
        editOrSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsEditing) {
                    mFloatingActionButton.setImageResource(android.R.drawable.ic_menu_add);// 改变图标
                    saveDate(true);
                    mGoodsHasChanged = !mGoodsHasChanged;
                } else {
                    mFloatingActionButton.setImageResource(android.R.drawable.ic_menu_save);
                }
                toggleEditMode(mIsEditing);// 切换为编辑模式或者显示模式
                mIsEditing = !mIsEditing;
            }
        });

        //设置 “图片” 按钮的参数
        FloatingActionButton setImage = findViewById(R.id.new_goods);
        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        PICK_IMAGE);
            }
        });
        setupSpinner();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.i(LOG_TAG, "======= onResume =======" + mCursorGoodsUri);
//    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_buy:
                mUtils.composeEmail(this, mNameView.getText().toString().trim());
                break;
            case R.id.button_sell:
                sellGoods();
                break;
            case R.id.button_sell_about:
                Intent intent = new Intent(this, AboutSellActivity.class);
                intent.putExtra("goods_id", mGoodsId);
                intent.setData(mCursorGoodsUri);
                startActivityForResult(intent, PICK_URI);
                break;
            default:
                break;
        }

    }

    private void startFindViewById() {
        mDisplay_layout = findViewById(R.id.include_display);
        mEdit_layout = findViewById(R.id.include_edit);

        mNameView = findViewById(R.id.title_text);
        mGoodsIdView = findViewById(R.id.goods_id_number);
        mSupplierView = findViewById(R.id.supplier_text);
        mPhoneNumberView = findViewById(R.id.phone_number_text);
        mTransportView = findViewById(R.id.transport_text);
        mQuantityView = findViewById(R.id.quantity_text);
        mPriceView = findViewById(R.id.price_text);
        mRemarksView = findViewById(R.id.remarks_text);

        mName_edit = findViewById(R.id.title_edit);
        mSupplier_edit = findViewById(R.id.supplier_edit);
        mPhone_number_edit = findViewById(R.id.phone_edit);
        mQuantity_edit = findViewById(R.id.quantity_edit);
        mPrice_edit = findViewById(R.id.price_edit);
        mRemarks_edit = findViewById(R.id.remarks_edit);

        mSpinnerView = findViewById(R.id.transport_edit);

        mImageView = findViewById(R.id.goods_image);

        mBuyButton = findViewById(R.id.button_buy);
        mSellButton = findViewById(R.id.button_sell);
        mSellAboutButton = findViewById(R.id.button_sell_about);

        mFloatingActionButton = findViewById(R.id.new_goods);

        mName_edit.setOnTouchListener(mTouchListener);
        mSupplier_edit.setOnTouchListener(mTouchListener);
        mPhone_number_edit.setOnTouchListener(mTouchListener);
        mQuantity_edit.setOnTouchListener(mTouchListener);
        mPrice_edit.setOnTouchListener(mTouchListener);
        mRemarks_edit.setOnTouchListener(mTouchListener);
        mSpinnerView.setOnTouchListener(mTouchListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE:
                Uri imageUri = data.getData();
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), imageUri
                    );
                    saveImage();
                    mImageView.setImageBitmap(mImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case PICK_URI:
                if (resultCode == RESULT_OK) {
                    mCursorGoodsUri = data.getData();
                    getLoaderManager().restartLoader(
                            GOODS_EDITOR,
                            null,
                            this);
                    Log.i(LOG_TAG, "======= onResume =======" + mCursorGoodsUri);
                }
                break;
            default:
                break;
        }
    }

    // 字符串转换为位图
    Bitmap StringToBitmap(String encodedString) {
        byte[] encodedByte = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodedByte, 0, encodedByte.length);
    }

    // 位图转换为字符串
    String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public void saveImage() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GoodsEntry.COLUMN_GOODS_IMAGE, BitmapToString(mImageBitmap));
        if (mCursorGoodsUri != null) {
            int rowsUpdate = getContentResolver().update(mCursorGoodsUri,
                    contentValues, null, null);
            if (rowsUpdate == 0) {
                Toast.makeText(this, R.string.updateImageError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toggleEditMode(boolean isEditing) {
        if (!isEditing) {
            // 内容文本隐形
            mNameView.setVisibility(View.INVISIBLE);
            mDisplay_layout.setVisibility(View.INVISIBLE);
            mRemarksView.setVisibility(View.INVISIBLE);
            mBuyButton.setVisibility(View.INVISIBLE);
            mSellButton.setVisibility(View.INVISIBLE);
            mSellAboutButton.setVisibility(View.INVISIBLE);
            // 编辑框显示
            mName_edit.setVisibility(View.VISIBLE);
            mEdit_layout.setVisibility(View.VISIBLE);
            mRemarks_edit.setVisibility(View.VISIBLE);
        } else {
            //内容文本显示
            mNameView.setVisibility(View.VISIBLE);
            mDisplay_layout.setVisibility(View.VISIBLE);
            mRemarksView.setVisibility(View.VISIBLE);
            mBuyButton.setVisibility(View.VISIBLE);
            mSellButton.setVisibility(View.VISIBLE);
            mSellAboutButton.setVisibility(View.VISIBLE);
            // 编辑框隐形
            mName_edit.setVisibility(View.INVISIBLE);
            mEdit_layout.setVisibility(View.INVISIBLE);
            mRemarks_edit.setVisibility(View.INVISIBLE);
        }
    }

    private void setupSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_transport, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinnerView.setAdapter(adapter);

        mSpinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.land))) {
                        mTransportId = GoodsEntry.COLUMN_TRANSPORT_LAND;
                    } else if (selection.equals(getString(R.string.air))) {
                        mTransportId = GoodsEntry.COLUMN_TRANSPORT_AIR;
                    } else {
                        mTransportId = GoodsEntry.COLUMN_TRANSPORT_SEA;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mTransportId = GoodsEntry.COLUMN_TRANSPORT_LAND;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                showDelete();
                return true;
            case android.R.id.home:
                if (!mGoodsHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener onClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };
                showUnsavedChangesDialog(onClickListener);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mGoodsHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(onClickListener);
    }

    public void saveDate(boolean isNewData) {
        String name = mName_edit.getText().toString().trim();
        String supplier = mSupplier_edit.getText().toString().trim();
        String phoneNumber = mPhone_number_edit.getText().toString().trim();
        String quantity = mQuantity_edit.getText().toString().trim();
        String price = mPrice_edit.getText().toString().trim();
        String remarks = mRemarks_edit.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.nameTextIsNull, Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(supplier)) {
            Toast.makeText(this, R.string.supplierTextIsNull, Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, R.string.phoneNumberTextIsNull, Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(quantity)) {
            Toast.makeText(this, R.string.quantityTextIsNull, Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, R.string.priceTextIsNull, Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取系统时间
        String time = mUtils.getNowTime();

        ContentValues contentValues = new ContentValues();
        contentValues.put(GoodsEntry.COLUMN_GOODS_NAME, name);
        contentValues.put(GoodsEntry.COLUMN_GOODS_MAIN, true);
        contentValues.put(GoodsEntry.COLUMN_GOODS_REMARKS, remarks);
        contentValues.put(GoodsEntry.COLUMN_GOODS_SUPPLIER, supplier);
        contentValues.put(GoodsEntry.COLUMN_GOODS_PHONE_NUMBER, phoneNumber);
        contentValues.put(GoodsEntry.COLUMN_GOODS_TRANSPORT, mTransportId);
        contentValues.put(GoodsEntry.COLUMN_GOODS_PRICE, price);
        contentValues.put(GoodsEntry.COLUMN_GOODS_SELL_PRICE, price);// 默认出售价格与定价一致
        contentValues.put(GoodsEntry.COLUMN_GOODS_TIME, time);

        if (mImageBitmap != null) {
            contentValues.put(GoodsEntry.COLUMN_GOODS_IMAGE, BitmapToString(mImageBitmap));
        }

        // 如果 uri 为空，代表是 “新建”。则改变 id 的值。否则，id 不变。
        if (mCursorGoodsUri == null) {
            if (mGoodsIdNumber < 10) {
                mGoodsId = String.format(getString(R.string.gIdTwoZero), mGoodsIdNumber);
            } else if (mGoodsIdNumber < 100) {
                mGoodsId = String.format(getString(R.string.gIdOneZero), mGoodsIdNumber);
            } else if (mGoodsIdNumber >= 100) {
                mGoodsId = String.format(getString(R.string.gIdZeroZero), mGoodsIdNumber);
            }
        }
        contentValues.put(GoodsEntry.COLUMN_GOODS_ID, mGoodsId);

        if (isNewData) {
            int sellQuantity = 0; // 出售数量默认是 0
            contentValues.put(GoodsEntry.COLUMN_GOODS_SELL_QUANTITY, sellQuantity);

            contentValues.put(GoodsEntry.COLUMN_GOODS_QUANTITY, quantity);

            if (mCursorGoodsUri == null) {
                Uri newUri = this.getContentResolver().insert(GoodsEntry.CONTENT_URI, contentValues);
                if (newUri == null) {
                    Toast.makeText(this,
                            R.string.insertTextError,
                            Toast.LENGTH_SHORT).show();
                }
                finish();
            } else {
                int rowsUpdate = this.getContentResolver().update(mCursorGoodsUri,
                        contentValues, null, null);
                if (rowsUpdate == 0) {
                    Toast.makeText(this, R.string.updateTextError, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            int sellQuantity = 1; // 按出售按钮时，默认出售 1 的数量
            contentValues.put(GoodsEntry.COLUMN_GOODS_SELL_QUANTITY, sellQuantity);
            contentValues.put(GoodsEntry.COLUMN_GOODS_QUANTITY, mQuantity);
            setGoodsMainFalse(contentValues);
        }
    }

    private void setGoodsMainFalse(ContentValues contentValues) {
        // 创建一个新的 values 对象，更新 COLUMN_GOODS_MAIN 的值为 false
        // 主 Activity 是根据 COLUMN_GOODS_MAIN 的值是否为 true 来决定是否显示
        // 不能使用 contentValues 来更新 COLUMN_GOODS_MAIN， values 仅仅用来更新 COLUMN_GOODS_MAIN
        ContentValues values = new ContentValues();
        values.put(GoodsEntry.COLUMN_GOODS_MAIN, false);
        int rowsUpdate = getContentResolver().update(mCursorGoodsUri,
                values, null, null);
        if (rowsUpdate == 0) {
            Log.i(LOG_TAG, getString(R.string.updateTextError));
        }

        // insert 新的一行，并设置 COLUMN_GOODS_MAIN 值为 true。
        contentValues.put(GoodsEntry.COLUMN_GOODS_MAIN, true);
        mCursorGoodsUri = getContentResolver().insert(GoodsEntry.CONTENT_URI, contentValues);
        if (mCursorGoodsUri == null) {
            Toast.makeText(this, R.string.sellOne, Toast.LENGTH_SHORT).show();
        }

        getLoaderManager().restartLoader(GOODS_EDITOR, null, this);
    }

    private void sellGoods() {
        if (mQuantity <= 0) {
            Toast.makeText(this, R.string.quantityIsZero, Toast.LENGTH_SHORT).show();
            return;
        }
        mQuantity = mQuantity - 1;

        mQuantityView.setText(String.valueOf(mQuantity));
        mQuantity_edit.setText(String.valueOf(mQuantity));

        saveDate(false);
    }

    private void showDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否删除当前的产品");
        builder.setPositiveButton("是的，删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteGoods();
            }
        });
        builder.setNegativeButton("不了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteGoods() {
        String selection = GoodsEntry.COLUMN_GOODS_ID + "=?";
        String[] selectionArgs = {String.valueOf(mGoodsId)};
        // 因为有出售历史的记录，必须删除当前“行”与相应的出售历史
        // 根据 mGoodsId 判断是否为同一产品
        if (mCursorGoodsUri != null) {
            int rowsDelete = getContentResolver().delete(
                    GoodsEntry.CONTENT_URI,
                    selection,
                    selectionArgs);

            if (rowsDelete == 0) {
                Toast.makeText(this, R.string.deleteError, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.deleteDone, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("有未保存的更改，是否直接退出");
        builder.setPositiveButton("直接退出", onClickListener);
        builder.setNegativeButton("继续编辑", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        return new CursorLoader(this,
                mCursorGoodsUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int goodsIdIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_ID);
            int nameIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_NAME);
            int supplierIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_SUPPLIER);
            int phoneIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_PHONE_NUMBER);
            int quantityIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_QUANTITY);
            int priceIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_PRICE);
            int remarkIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_REMARKS);
            int imageIdIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_IMAGE);

            mGoodsId = cursor.getString(goodsIdIndex);
            String nameText = cursor.getString(nameIndex);
            String supplierText = cursor.getString(supplierIndex);
            String phoneNumberText = String.valueOf(cursor.getInt(phoneIndex));
            mQuantity = cursor.getInt(quantityIndex);
            String priceText = String.valueOf(cursor.getInt(priceIndex));
            String remarkText = cursor.getString(remarkIndex);
            String imageString = cursor.getString(imageIdIndex);

            mGoodsIdView.setText(mGoodsId);
            mNameView.setText(nameText);
            mSupplierView.setText(supplierText);
            mPhoneNumberView.setText(phoneNumberText);
            mPriceView.setText(priceText);

            mRemarksView.setText(remarkText);

            mName_edit.setText(nameText);
            mSupplier_edit.setText(supplierText);
            mPhone_number_edit.setText(phoneNumberText);
            mPrice_edit.setText(priceText);
            mRemarks_edit.setText(remarkText);

            String quantityString = String.valueOf(mQuantity);
            mQuantityView.setText(quantityString);
            mQuantity_edit.setText(quantityString);

            if (imageString == null) {
                mImageView.setImageResource(R.drawable.no_image);
            } else {
                mImageBitmap = StringToBitmap(imageString);
                mImageView.setImageBitmap(mImageBitmap);
            }

            switch (mTransportId) {
                case GoodsEntry.COLUMN_TRANSPORT_AIR:
                    mTransportView.setText(getString(R.string.air));
                    mSpinnerView.setSelection(GoodsEntry.COLUMN_TRANSPORT_AIR);
                    break;
                case GoodsEntry.COLUMN_TRANSPORT_SEA:
                    mTransportView.setText(getString(R.string.sea));
                    mSpinnerView.setSelection(GoodsEntry.COLUMN_TRANSPORT_SEA);
                    break;
                default:
                    mTransportView.setText(getString(R.string.land));
                    mSpinnerView.setSelection(GoodsEntry.COLUMN_TRANSPORT_LAND);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
