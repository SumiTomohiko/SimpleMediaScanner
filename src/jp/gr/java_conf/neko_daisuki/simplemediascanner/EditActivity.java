package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class EditActivity extends FragmentActivity implements ScheduleFragment.OnScheduleGivenListener {

    private class Adapter extends BaseAdapter {

        private class ScheduleComparator implements Comparator<Database.Schedule> {

            @Override
            public int compare(Database.Schedule lhs, Database.Schedule rhs) {
                if (lhs.isDaily() && !rhs.isDaily()) {
                    return 1;
                }
                if (!lhs.isDaily() && rhs.isDaily()) {
                    return -1;
                }
                int n = lhs.getHour() - rhs.getHour();
                if (n != 0) {
                    return n;
                }
                return lhs.getMinute() - rhs.getMinute();
            }
        }

        private class CheckBoxListener implements CompoundButton.OnCheckedChangeListener {

            private int mScheduleId;

            public CheckBoxListener(int scheduleId) {
                mScheduleId = scheduleId;
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mDatabase.addScheduleToTask(mId, mScheduleId);
            }
        }

        // documents
        private Database.Schedule[] mSchedules = new Database.Schedule[0];

        // helpers
        private LayoutInflater mInflater;
        private Comparator<Database.Schedule> mComparator = new ScheduleComparator();

        public Adapter() {
            String name = Context.LAYOUT_INFLATER_SERVICE;
            mInflater = (LayoutInflater)getSystemService(name);
        }

        @Override
        public void notifyDataSetChanged() {
            mSchedules = mDatabase.getSchedules();
            Arrays.sort(mSchedules, mComparator);
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mSchedules.length;
        }

        @Override
        public Object getItem(int position) {
            return mSchedules[position];
        }

        @Override
        public long getItemId(int position) {
            return mSchedules[position].getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                return getView(position, makeView(), parent);
            }
            initializeView(position, convertView);
            return convertView;
        }

        private boolean contains(int[] a, int key) {
            int length = a.length;
            for (int i = 0; i < length; i++) {
                if (a[i] == key) {
                    return true;
                }
            }
            return false;
        }

        private void initializeView(int position, View view) {
            Database.Schedule schedule = mSchedules[position];
            int scheduleId = schedule.getId();

            CompoundButton checkBox = (CompoundButton)view.findViewById(R.id.checkbox);
            int[] ids = mDatabase.getScheduleIdsOfTask(mId);
            checkBox.setChecked(contains(ids, scheduleId));
            checkBox.setOnCheckedChangeListener(new CheckBoxListener(scheduleId));

            boolean isDaily = schedule.isDaily();
            String hour = isDaily ? String.format(Locale.ROOT, "%02d", schedule.getHour())
                                  : " *";
            int minute = schedule.getMinute();
            TextView scheduleText = (TextView)view.findViewById(R.id.schedule_text);
            scheduleText.setText(String.format("%s:%02d", hour, minute));
        }

        private View makeView() {
            return mInflater.inflate(R.layout.row_schedule, null);
        }
    }

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

    private class AddScheduleButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            DialogFragment fragment = new ScheduleFragment();
            fragment.show(getSupportFragmentManager(), "new schedule");
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
    private Adapter mAdapter;

    // helpers
    private Proc mProc;

    @Override
    public void onScheduleGiven(ScheduleFragment fragment, int hour,
                                int minute) {
        mDatabase.addSchedule(hour, minute);
        mAdapter.notifyDataSetChanged();
    }

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

        View addScheduleButton = findViewById(R.id.add_schedule_button);
        addScheduleButton.setOnClickListener(new AddScheduleButtonOnClickListener());
        View okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(new OkayButtonOnClickListener());
        View cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new CancelButtonOnClickListener());

        mAdapter = new Adapter();
        int id = R.id.schedule_list;
        AbsListView scheduleList = (AbsListView)findViewById(id);
        scheduleList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}