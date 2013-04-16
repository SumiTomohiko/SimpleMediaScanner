package jp.gr.java_conf.neko_daisuki.contentprovidermaintainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private static class Audio {

        public String data;
        public String duration;
        public String mime_type;

        public String toString() {
            return String.format("audio: data=%s, duration=%s, mime_type=%s", data, duration, mime_type);
        }
    }

    private class RunButtonOnClickListener implements View.OnClickListener {

        public void onClick(View view) {
            updateContentProvider();
        }
    }

    private static final String LOG_TAG = "contentprovidermaintainer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button runButton = (Button)findViewById(R.id.run_button);
        runButton.setOnClickListener(new RunButtonOnClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private List<Audio> retrieveAudio(String path) {
        List<Audio> list = new ArrayList<Audio>();

        MediaMetadataRetriever data = new MediaMetadataRetriever();
        try {
            Log.i(LOG_TAG, String.format("Retrieving %s.", path));

            data.setDataSource(path);
            Audio audio = new Audio();
            audio.data = path;
            audio.duration = data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            audio.mime_type = data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            list.add(audio);
        }
        catch (RuntimeException _) {
            Log.i(LOG_TAG, String.format("%s seems not audio. Ignored.", path));
        }
        finally {
            data.release();
        }

        return list;
    }

    private List<Audio> listAudio(String directory) {
        List<Audio> list = new ArrayList<Audio>();
        for (File file: new File(directory).listFiles()) {
            list.addAll(retrieveAudio(file.getAbsolutePath()));
        }

        return list;
    }

    private void insertRecord(Audio audio) {
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(audio.data);
        ContentResolver resolver = getContentResolver();
        String[] columns = new String[] { "_id" };
        String selection = String.format("%s=?", MediaStore.Audio.AudioColumns.DATA);
        String[] args = new String[] { audio.data };
        Cursor c = resolver.query(uri, columns, selection, args, null);
        try {
            if (0 < c.getCount()) {
                return;
            }
        }
        finally {
            c.close();
        }

        Log.i(LOG_TAG, String.format("Inserting audio of %s", audio));

        ContentValues row = new ContentValues();
        row.put(MediaStore.Audio.AudioColumns.DATA, audio.data);
        row.put(MediaStore.Audio.AudioColumns.DURATION, audio.duration);
        row.put(MediaStore.Audio.AudioColumns.MIME_TYPE, audio.mime_type);
        resolver.insert(uri, row);
    }

    private void updateContentProvider() {
        Log.i(LOG_TAG, "Updating the content provider started.");

        for (Audio audio: listAudio("/mnt/sdcard/radio")) {
            insertRecord(audio);
        }

        Log.i(LOG_TAG, "Updating ended.");
    }
}

/**
 * vim: tabstop=4 shiftwidth=4 expandtab softtabstop=4
 */
