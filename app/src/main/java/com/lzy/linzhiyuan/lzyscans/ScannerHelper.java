package com.lzy.linzhiyuan.lzyscans;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2016/4/29.
 */
public class ScannerHelper {

    public static final String ACTION_SCANNER_SEND_BARCODE = "com.seuic.gaojie";
    public static final String ACTION_SCANNER_ENABLED = "com.android.scanner.ENABLED";
    public static final String KEY_DATA = "scannerdata";
    public static final String KEY_ENABLED = "enabled";

    public static void sendBroadcast(Context context, String action , String key, boolean value){
        try{
            Intent intent = new Intent(action);
            intent.putExtra(key, value);
            context.sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
