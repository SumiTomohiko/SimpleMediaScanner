package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import android.content.Context;
import android.content.Intent;

public class ServiceUtil {

    public static void startMainService(Context context, int[] ids) {
        Intent intent = new Intent(context, MainService.class);
        intent.putExtra(MainService.EXTRA_IDS, ids);
        context.startService(intent);
    }
}