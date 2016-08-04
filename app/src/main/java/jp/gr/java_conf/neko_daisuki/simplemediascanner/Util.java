package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

class Util {

    public static File getApplicationDirectory() {
        File directory = Environment.getExternalStorageDirectory();
        String path = directory.getAbsolutePath();
        return new File(joinPath(path, ".simple-media-scanner"));
    }

    public static File getLogDirectory() {
        return new File(getApplicationDirectory(), "log");
    }

    public static Database readDatabase(Context context) {
        Database database = new Database();
        try {
            database.read(getApplicationDirectory());
        }
        catch (IOException e) {
            showException(context, "Cannot read the database", e);
        }
        return database;
    }

    public static void writeDatabase(Context context, Database database) {
        try {
            database.write(getApplicationDirectory());
        }
        catch (IOException e) {
            showException(context, "Cannot write the database", e);
        }
    }

    public static void showException(Context context, String msg, Throwable e) {
        e.printStackTrace();
        String s = String.format("%s: %s", msg, e.getMessage());
        Toast.makeText(context, String.format(s), Toast.LENGTH_LONG).show();
    }

    public static String joinPath(String s, String t) {
        return String.format("%s%s%s", s, File.separator, t);
    }
}