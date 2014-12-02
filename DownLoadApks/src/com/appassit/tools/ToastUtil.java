package com.appassit.tools;

import android.widget.Toast;

import com.appassit.activitys.SLAppication;

/**
 * Created by Ho on 2014/6/25.
 */
public class ToastUtil {

    /**
     * 系统Google提示
     */

    public static void showShort(int resid) {
        Toast.makeText(SLAppication.getContext(), resid, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(String msg) {
        Toast.makeText(SLAppication.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(int resid) {
        Toast.makeText(SLAppication.getContext(), resid, Toast.LENGTH_LONG).show();
    }

    public static void showLong(String msg) {
        Toast.makeText(SLAppication.getContext(), msg, Toast.LENGTH_LONG).show();
    }

    
}
