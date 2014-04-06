package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditActivity extends Activity {

    private interface Proc {

        public void run(Database database, String path);
    }

    private class AddProc implements Proc {

        @Override
        public void run(Database database, String path) {
            database.addTask(path);
        }
    }

    private class EditProc implements Proc {

        @Override
        public void run(Database database, String path) {
            database.editTask(mId, path);
        }
    }

    private class OkayButtonOnClickListener implements View.OnClickListener {

        public void onClick(View _) {
            mProc.run(mDatabase, mDirectoryEditText.getText().toString());
            Util.writeDatabase(EditActivity.this, mDatabase);
            finish();
        }
    }

    private class CancelButtonOnClickListener implements View.OnClickListener {

        public void onClick(View _) {
            finish();
        }
    }

    public static final String EXTRA_ID = "id";

    // documents
    private Database mDatabase;
    private int mId;

    // views
    private EditText mDirectoryEditText;

    // helpers
    private Proc mProc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mId = getIntent().getIntExtra(EXTRA_ID, -1);
        mDatabase = Util.readDatabase(this);
        mProc = mId != -1 ? new EditProc() : new AddProc();

        mDirectoryEditText = (EditText)findViewById(R.id.directory_text);
        String path = mId != -1 ? mDatabase.getTask(mId).getPath() : "";
        mDirectoryEditText.setText(path);

        View okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(new OkayButtonOnClickListener());
        View cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new CancelButtonOnClickListener());
    }
}