package com.example.android.listofgoods;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Utils {

    private Utils() {

    }

    static void dialPhoneNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    static String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy/MM/dd, HH:mm", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }

    static boolean isMobile(final String str) {
        Pattern p = Pattern.compile("^[1][3-9][0-9]{9}$"); // 验证手机号
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
