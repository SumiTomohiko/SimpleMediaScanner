package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditActivity extends Activity {

    private class OkButtonOnClickListener implements View.OnClickListener {

        public void onClick(View _) {
            ok();
        }
    }

    private class CancelButtonOnClickListener implements View.OnClickListener {

        public void onClick(View _) {
            cancel();
        }
    }

    public static final String EXTRA_KEY_DIRECTORY = "directory";

    private Directory mDirectory;
    private EditText mDirectoryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mDirectoryEditText = (EditText)findViewById(R.id.directory_text);

        Object o = getIntent().getSerializableExtra(EXTRA_KEY_DIRECTORY);
        mDirectory = (Directory)o;
        mDirectoryEditText.setText(mDirectory.path);

        View okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(new OkButtonOnClickListener());
        View cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new CancelButtonOnClickListener());
    }

    private void ok() {
        Directory directory = new Directory();
        directory.id = mDirectory.id;
        directory.path = mDirectoryEditText.getText().toString();

        Intent i = getIntent();
        i.putExtra(EXTRA_KEY_DIRECTORY, directory);
        close(RESULT_OK, i);
    }

    private void cancel() {
        close(RESULT_CANCELED, null);
    }

    private void close(int resultCode, Intent i) {
        setResult(resultCode, i);
        finish();
    }
}

/**
 * vim: tabstop=4 shiftwidth=4 expandtab softtabstop=4
 */
