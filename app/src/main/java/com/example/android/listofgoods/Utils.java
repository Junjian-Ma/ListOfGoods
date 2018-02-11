package com.example.android.listofgoods;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

class Utils {
    void dialPhoneNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy/MM/dd, HH:mm", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }
}
