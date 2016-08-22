package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class MainService extends IntentService {

    private class MediaScannerClient implements MediaScannerConnectionClient {

        private Queue<File> mFiles = new LinkedList<File>();
        private MediaScannerConnection mConnection;
        private Object mBarrier;

        public MediaScannerClient(Database.Task[] tasks, Object barrier) {
            int length = tasks.length;
            for (int i = 0; i < length; i++) {
                mFiles.add(new File(tasks[i].getPath()));
            }
            mBarrier = barrier;
        }

        public void setConnection(MediaScannerConnection connection) {
            mConnection = connection;
        }

        public void onMediaScannerConnected() {
            scanNext();
        }

        public void onScanCompleted(String path, Uri uri) {
            scanNext();
        }

        private void pushFiles(File[] files) {
            for (File file: files) {
                String fmt = "Pushed %s.";
                Log.i(LOG_TAG, String.format(fmt, file.getAbsolutePath()));
                mFiles.offer(file);
            }
        }

        private void scanNext() {
            File file;
            while (((file = mFiles.poll()) != null) && file.isDirectory()) {
                String fmt = "Directory found: %s";
                Log.i(LOG_TAG, String.format(fmt, file.getAbsolutePath()));
                pushFiles(file.listFiles());
            }
            if (file == null) {
                mConnection.disconnect();
                synchronized (mBarrier) {
                    mBarrier.notify();
                }
                String msg = "Scanning ended.";
                Log.i(LOG_TAG, msg);
                return;
            }
            String path = file.getAbsolutePath();
            Log.i(LOG_TAG, String.format("File found: %s", path));
            mConnection.scanFile(path, null);
        }
    }

    private class DisplayToast implements Runnable {

        private String mMessage;

        public DisplayToast(String message) {
            mMessage = message;
        }

        public void run() {
            Context context = MainService.this;
            Toast.makeText(context, mMessage, Toast.LENGTH_LONG).show();
        }
    }

    public static final String EXTRA_IDS = "ids";

    private static final String LOG_TAG = "service";

    private Handler mHandler = new Handler();

    public MainService() {
        super("service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Database database = Util.readDatabase(this);
        int[] ids = intent.getIntArrayExtra(EXTRA_IDS);
        int length = ids.length;
        Database.Task[] tasks = new Database.Task[length];
        for (int i = 0; i < length; i++) {
            tasks[i] = database.getTask(ids[i]);
        }

        Object barrier = new Object();
        MediaScannerClient client = new MediaScannerClient(tasks, barrier);
        MediaScannerConnection connection = new MediaScannerConnection(this,
                                                                       client);
        client.setConnection(connection);
        connection.connect();
        showToast("The scanning task started.");
        try {
            synchronized (barrier) {
                barrier.wait();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        showToast("The scanning task finished.");
    }

    private void showToast(String message) {
        String name = getResources().getString(R.string.app_name);
        String s = String.format("%s: %s", name, message);
        mHandler.post(new DisplayToast(s));
    }
}