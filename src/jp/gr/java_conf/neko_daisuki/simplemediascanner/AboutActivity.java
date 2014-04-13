package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        showVersion();
        showLicense();
    }

    private void showLicense() {
        StringBuilder buffer = new StringBuilder();

        try {
            InputStream in = getAssets().open("COPYING");
            try {
                InputStreamReader reader = new InputStreamReader(in);
                try {
                    BufferedReader br = new BufferedReader(reader);
                    try {
                        String line;
                        while ((line = br.readLine()) != null) {
                            buffer.append(line);
                            buffer.append("\n");
                        }
                    }
                    finally {
                        br.close();
                    }
                }
                finally {
                    reader.close();
                }
            }
            finally {
                in.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        TextView view = (TextView)findViewById(R.id.license);
        view.setText(buffer.toString());
    }

    private void showVersion() {
        PackageManager pm = getPackageManager();
        String name = getPackageName();
        int flags = PackageManager.GET_INSTRUMENTATION;

        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(name, flags);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        TextView view = (TextView)findViewById(R.id.version);
        view.setText(pi.versionName);
    }
}