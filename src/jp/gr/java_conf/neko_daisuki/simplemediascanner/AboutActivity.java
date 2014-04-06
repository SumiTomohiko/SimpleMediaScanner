package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String fmt = readTemplate();
        String html = String.format(fmt, getVersion());
        WebView view = new WebView(this);
        view.loadData(URLEncoder.encode(html).replaceAll("\\+", "%20"), "text/html", "UTF-8");
        setContentView(view);
    }

    private String readTemplate() {
        StringBuilder buffer = new StringBuilder();
        InputStream in;
        try {
            in = getAssets().open("license.html");
            try {
                Reader reader = new InputStreamReader(in);
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
        android.util.Log.d("x", buffer.toString());
        return buffer.toString();
    }

    private String getVersion() {
        PackageManager pm = getPackageManager();
        String name = getPackageName();
        int flags = PackageManager.GET_INSTRUMENTATION;

        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(name, flags);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "???";
        }

        return pi.versionName;
    }
}

// vim: tabstop=4 shiftwidth=4 expandtab softtabstop=4
