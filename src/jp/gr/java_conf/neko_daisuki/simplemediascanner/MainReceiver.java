package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MainReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PeriodicalUtil.schedule(context, Util.readDatabase(context));
    }
}