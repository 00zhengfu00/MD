package cn.jiangzehui.mds.util;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class T {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private static Intent intent = new Intent();


    public static void show(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void open(Context context, Class<?> classs) {
        context.startActivity(intent.setClass(context, classs));
    }

    public static void open(Context context, Class<?> classs, String... value) {
        for (int i = 1; i <= value.length; i++) {

            if(i%2==0){
                intent.putExtra(value[i-2], value[i-1]);
            }

        }
        context.startActivity(intent.setClass(context, classs));
    }


    public static String getImei(Context context) {
        TelephonyManager mtm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        return mtm.getDeviceId();
    }


    public static String getTime() {

        return sdf.format(new Date());
    }


}
