package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsSpinner;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class ScheduleFragment extends DialogFragment {

    public interface OnScheduleGivenListener {

        public void onScheduleGiven(ScheduleFragment fragment, int hour,
                                    int minute);
    }

    private class DailyRadioOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        private abstract class Proc {

            public abstract void run();
        }

        private class EnableProc extends Proc {

            @Override
            public void run() {
                mHourSpinner.setAdapter(mHourAdapter);
            }
        }

        private class DisableProc extends Proc {

            private SpinnerAdapter mAdapter;

            public DisableProc() {
                int id = android.R.layout.simple_spinner_item;
                ArrayAdapter<String> adapter;
                adapter = new ArrayAdapter<String>(mContext, id);
                adapter.add("*");
                mAdapter = adapter;
            }

            @Override
            public void run() {
                mHourSpinner.setAdapter(mAdapter);
            }
        }

        private Context mContext;
        private AbsSpinner mHourSpinner;
        private Proc mEnableProc;
        private Proc mDisableProc;

        public DailyRadioOnCheckedChangeListener(Context context,
                                                 AbsSpinner hourSpinner) {
            mContext = context;
            mHourSpinner = hourSpinner;
            mEnableProc = new EnableProc();
            mDisableProc = new DisableProc();
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            mHourSpinner.setEnabled(isChecked);
            (isChecked ? mEnableProc : mDisableProc).run();
        }
    }

    private class NegativeButtonOnClickListener implements DialogInterface.OnClickListener {

        public void onClick(DialogInterface dialog, int which) {
        }
    }

    private class PositiveButtonOnClickListener implements DialogInterface.OnClickListener {

        public void onClick(DialogInterface dialog, int which) {
            boolean isChecked = mDailyRadio.isChecked();
            int hour = isChecked ? mHourSpinner.getSelectedItemPosition()
                                 : Database.Schedule.HOUR_WILDCARD;
            int minute = mMinuteSpinner.getSelectedItemPosition();
            mListener.onScheduleGiven(ScheduleFragment.this, hour, minute);
        }
    }

    private CompoundButton mDailyRadio;
    private AbsSpinner mHourSpinner;
    private AbsSpinner mMinuteSpinner;

    private OnScheduleGivenListener mListener;
    private SpinnerAdapter mHourAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnScheduleGivenListener)activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        mHourAdapter = makeIntegerAdapter(context, 24);

        Object service = context.getSystemService(EditActivity.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = (LayoutInflater)service;
        View view = inflater.inflate(R.layout.fragment_schedule, null);
        mHourSpinner = (AbsSpinner)view.findViewById(R.id.hour_spinner);
        mHourSpinner.setAdapter(mHourAdapter);
        mMinuteSpinner = (AbsSpinner)view.findViewById(R.id.minute_spinner);
        initializeSpinner(context, mMinuteSpinner, 60);
        mDailyRadio = (CompoundButton)view.findViewById(R.id.daily_radio);
        mDailyRadio.setChecked(true);
        CompoundButton.OnCheckedChangeListener l;
        l = new DailyRadioOnCheckedChangeListener(context, mHourSpinner);
        mDailyRadio.setOnCheckedChangeListener(l);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setNegativeButton(R.string.negative,
                                  new NegativeButtonOnClickListener());
        builder.setPositiveButton(R.string.positive,
                                  new PositiveButtonOnClickListener());
        builder.setTitle("New schedule");
        builder.setView(view);

        return builder.create();
    }

    private ArrayAdapter<?> makeIntegerAdapter(Context context, int max) {
        int id = android.R.layout.simple_spinner_item;
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(context, id);
        for (int i = 0; i < max; i++) {
            adapter.add(Integer.valueOf(i));
        }
        return adapter;
    }

    private void initializeSpinner(Context context, View spinner, int max) {
        ((Spinner)spinner).setAdapter(makeIntegerAdapter(context, max));
    }
}