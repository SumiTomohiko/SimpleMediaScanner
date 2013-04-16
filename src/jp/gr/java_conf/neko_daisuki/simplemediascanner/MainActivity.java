package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;

import android.app.Activity;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private class MediaScannerClient implements MediaScannerConnectionClient {

        private Deque<File> mFiles = new ArrayDeque<File>();

        public void onMediaScannerConnected() {
            Log.i(LOG_TAG, "Connected to the service.");
            pushAll(mDirectories);
            scanNext();
        }

        public void onScanCompleted(String path, Uri uri) {
            Log.i(LOG_TAG, String.format("Scanning %s was completed.", path));
            scanNext();
        }

        private void pushAll(String[] directories) {
            int len = directories.length;
            File[] files = new File[len];
            for (int i = 0; i < len; i++) {
                files[i] = new File(mDirectories[i]);
            }
            pushAll(files);
        }

        private void pushAll(File[] files) {
            for (File file: files) {
                String fmt = "Pushed %s.";
                Log.i(LOG_TAG, String.format(fmt, file.getAbsolutePath()));

                mFiles.push(file);
            }
        }

        private void scanNext() {
            File file = null;
            while (!mFiles.isEmpty() && ((file = mFiles.pop()).isDirectory())) {
                String fmt = "Directory found: %s";
                Log.i(LOG_TAG, String.format(fmt, file.getAbsolutePath()));

                pushAll(file.listFiles());
            }
            if (file == null) {
                mConnection.disconnect();
                Log.i(LOG_TAG, "Scanning ended.");
                return;
            }
            String path = file.getAbsolutePath();
            Log.i(LOG_TAG, String.format("File found: %s", path));
            mConnection.scanFile(path, null);
        }
    }

    private class RunAllButtonOnClickListener implements View.OnClickListener {

        public void onClick(View _) {
            startScanning();
        }
    }

    private static final String LOG_TAG = "simplemediascanner";

    private String[] mDirectories = {
        "/mnt/sdcard/radio",
        "/mnt/sdcard/music" };
    private MediaScannerConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button runButton = (Button)findViewById(R.id.run_button);
        runButton.setOnClickListener(new RunAllButtonOnClickListener());

        MediaScannerClient client = new MediaScannerClient();
        mConnection = new MediaScannerConnection(this, client);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void startScanning() {
        Log.i(LOG_TAG, "Scanning started.");

        mConnection.connect();
    }
}

/**
 * vim: tabstop=4 shiftwidth=4 expandtab softtabstop=4
 */
