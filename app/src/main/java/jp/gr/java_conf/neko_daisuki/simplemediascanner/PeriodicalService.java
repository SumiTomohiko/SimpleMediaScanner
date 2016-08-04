package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import android.app.IntentService;
import android.content.Intent;

public class PeriodicalService extends IntentService {

    public static final String EXTRA_IDS = "ids";

    public PeriodicalService() {
        super("periodical service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int[] ids = intent.getExtras().getIntArray(EXTRA_IDS);
        ServiceUtil.startMainService(this, ids);
        PeriodicalUtil.schedule(this, Util.readDatabase(this));
    }
}