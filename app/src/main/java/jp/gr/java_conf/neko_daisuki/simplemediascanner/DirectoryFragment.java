package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DirectoryFragment extends DialogFragment {

    public interface OnSelectedListener {

        public void onSelected(DirectoryFragment fragment, String path);
    }

    private static class FileComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.isDirectory() && !rhs.isDirectory()) {
                return -1;
            }
            if (!lhs.isDirectory() && rhs.isDirectory()) {
                return 1;
            }
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    private class OnClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            mListener.onSelected(DirectoryFragment.this, mPath);
        }
    }

    private class Adapter extends BaseAdapter {

        private class OnClickListener implements View.OnClickListener {

            private int mPosition;

            public OnClickListener(int position) {
                mPosition = position;
            }

            @Override
            public void onClick(View v) {
                String path = String.format("%s/%s", mPath, getName(mPosition));
                File file = new File(path);
                String canonicalPath;
                try {
                    canonicalPath = file.getCanonicalPath();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Context context = getActivity();
                    String message = e.getMessage();
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    return;
                }
                setPath(canonicalPath.equals("") ? "/" : canonicalPath);
                mLabel.setText(mPath);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mFiles.length + 1;
        }

        @Override
        public Object getItem(int position) {
            return getName(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;   // N/A
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                return getView(position, makeView(position), parent);
            }
            TextView view = (TextView)convertView;
            view.setOnClickListener(getListener(position));
            view.setTypeface(Typeface.DEFAULT, getStyle(position));
            view.setText(getName(position));
            return view;
        }

        private View makeView(int position) {
            return new TextView(getActivity());
        }

        private View.OnClickListener getListener(int position) {
            if (position == 0) {
                return new OnClickListener(position);
            }
            boolean isDirectory = mFiles[position - 1].isDirectory();
            return isDirectory ? new OnClickListener(position) : null;
        }

        private int getStyle(int position) {
            if (position == 0) {
                return Typeface.NORMAL;
            }
            return mFiles[position - 1].isDirectory() ? Typeface.NORMAL
                                                      : Typeface.ITALIC;
        }

        private String getName(int position) {
            return position == 0 ? ".." : mFiles[position - 1].getName();
        }
    }

    private static final String KEY_INITIAL_DIRECTORY = "initial_directory";
    private static final Comparator<File> COMPARATOR = new FileComparator();

    // documents
    private String mPath;
    private File[] mFiles;
    private OnSelectedListener mListener;

    // views
    private BaseAdapter mAdapter;
    private TextView mLabel;

    public static DialogFragment newInstance(String initialDirectory) {
        DialogFragment fragment = new DirectoryFragment();
        Bundle args = new Bundle();
        args.putString(KEY_INITIAL_DIRECTORY, initialDirectory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnSelectedListener)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String path = getArguments().getString(KEY_INITIAL_DIRECTORY);
        setPath(new File(path).isDirectory() ? path
                                             : Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        String name = Context.LAYOUT_INFLATER_SERVICE;
        Object o = context.getSystemService(name);
        LayoutInflater inflater = (LayoutInflater)o;
        View view = inflater.inflate(R.layout.fragment_directory, null);
        mLabel = (TextView)view.findViewById(R.id.label);
        mLabel.setText(mPath);
        AbsListView list = (AbsListView)view.findViewById(R.id.list);
        mAdapter = new Adapter();
        list.setAdapter(mAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.positive, new OnClickListener());
        builder.setNegativeButton(R.string.negative, null);
        builder.setTitle("Select a directory");
        builder.setView(view);

        return builder.create();
    }

    private void setPath(String path) {
        mPath = path;
        File[] files = new File(mPath).listFiles();
        Arrays.sort(files, COMPARATOR);
        mFiles = files;
    }
}