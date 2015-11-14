package com.acpcoursera.diabetesmanagment.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.provider.DmContract;
import com.acpcoursera.diabetesmanagment.receiver.CheckInPublisher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/*
debugging in ADB:
>adb shell dumpsys alarm > dump.txt
 */

public class RemindersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = RemindersFragment.class.getSimpleName();

    private static final int TEST_LOADER = 0;

    private CursorAdapter mAdapter;
    private AlarmManager mAlarmManager;
    private ListView mReminders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_reminders, container, false);

        getLoaderManager().initLoader(TEST_LOADER, null, this);

        mAdapter = new ReminderAdapter(getActivity(), null, 0);
        mReminders = (ListView) rootView.findViewById(R.id.reminders_list_view);
        mReminders.setAdapter(mAdapter);

        setHasOptionsMenu(true);

        mAlarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        initObligatoryReminders();

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case TEST_LOADER:
                return new CursorLoader(
                        getActivity(),
                        DmContract.Reminders.buildRemindersUri(),
                        new String[]
                                {
                                        DmContract.Reminders._ID,
                                        DmContract.Reminders.HOUR_OF_DAY,
                                        DmContract.Reminders.MINUTE,
                                        DmContract.Reminders.IS_ENABLED
                                },
                        null,
                        null,
                        null
                );
            default:
                break;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_reminder, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reminder:
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog dialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                addNewReminder(hourOfDay, minute);
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                dialog.show();
                break;
            default:
                break;
        }
        return false;
    }

    private void initObligatoryReminders() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(DmContract.Reminders.CONTENT_URI, null, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            addNewReminder(10, 0);
            addNewReminder(15, 0);
            addNewReminder(21, 0);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void addNewReminder(int hourOfDay, int minute) {

        ContentResolver cr = getActivity().getContentResolver();
        ContentValues cvs = new ContentValues();
        cvs.put(DmContract.Reminders.HOUR_OF_DAY, hourOfDay);
        cvs.put(DmContract.Reminders.MINUTE, minute);
        cvs.put(DmContract.Reminders.IS_ENABLED, 1);
        Uri newReminderPath = cr.insert(DmContract.Reminders.CONTENT_URI, cvs);
        if (newReminderPath != null) {
            Cursor cursor = cr.query(newReminderPath, new String[]{DmContract.Reminders._ID},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int reminderId = cursor.getInt(0);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                Intent intent = new Intent(getActivity(), CheckInPublisher.class);
                PendingIntent alarmIntent = PendingIntent
                        .getBroadcast(getActivity(), reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);
                cursor.close();
            }
        }
    }

    private void editReminder(int id, int hourOfDay, int minute) {

        ContentResolver cr = getActivity().getContentResolver();
        ContentValues cvs = new ContentValues();
        cvs.put(DmContract.Reminders.HOUR_OF_DAY, hourOfDay);
        cvs.put(DmContract.Reminders.MINUTE, minute);
        int rowsUpdated = cr.update(DmContract.Reminders.buildReminderUri(Integer.toString(id)),
                cvs, null, null);
        if (rowsUpdated != 0) {

            Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                Intent intent = new Intent(getActivity(), CheckInPublisher.class);
                PendingIntent alarmIntent = PendingIntent
                        .getBroadcast(getActivity(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }

    private void deleteReminder(int id) {

        ContentResolver cr = getActivity().getContentResolver();
        int rowsDeleted = cr.delete(DmContract.Reminders.buildReminderUri(Integer.toString(id)),
                null, null);
        if (rowsDeleted != 0) {
            Intent intent = new Intent(getActivity(), CheckInPublisher.class);
            PendingIntent alarmIntent = PendingIntent
                    .getBroadcast(getActivity(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.cancel(alarmIntent);
        }
    }

    private void enableReminder(int id, boolean enabled) {

        ContentResolver cr = getActivity().getContentResolver();
        ContentValues cvs = new ContentValues();
        cvs.put(DmContract.Reminders.IS_ENABLED, enabled);

        Uri reminderUri = DmContract.Reminders.buildReminderUri(Integer.toString(id));
        int rowsUpdated = cr.update(reminderUri, cvs, null, null);
        if (rowsUpdated != 0) {
            Cursor cursor = cr.query(reminderUri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {

                Intent intent = new Intent(getActivity(), CheckInPublisher.class);
                PendingIntent alarmIntent = PendingIntent
                        .getBroadcast(getActivity(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (enabled) {

                    int hourOfDay = cursor
                            .getInt(cursor.getColumnIndexOrThrow(DmContract.Reminders.HOUR_OF_DAY));
                    int minute = cursor
                            .getInt(cursor.getColumnIndexOrThrow(DmContract.Reminders.MINUTE));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, alarmIntent);
                }
                else {
                    mAlarmManager.cancel(alarmIntent);
                }

            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private class ReminderAdapter extends CursorAdapter {

        private final Calendar mCalendar;

        public ReminderAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            mCalendar = Calendar.getInstance();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.reminder_item, parent, false);
        }

        @Override
        public void bindView(final View view, Context context, Cursor cursor) {
            TextView reminderId = (TextView) view.findViewById(R.id.reminder_id);
            ImageView reminderIcon = (ImageView) view.findViewById(R.id.reminder_icon);
            TextView reminderTime = (TextView) view.findViewById(R.id.reminder_time);
            Switch reminderEnabled = (Switch) view.findViewById(R.id.reminder_switch);

            final int id = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Reminders._ID));
            int hourOfDay = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Reminders.HOUR_OF_DAY));
            int minute = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Reminders.MINUTE));
            int isEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Reminders.IS_ENABLED));

            reminderId.setText(reminderId.getText());

            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
            reminderTime.setText(dateFormat.format(mCalendar.getTime()));

            reminderEnabled.setOnCheckedChangeListener(null); // this prevents positive feedback
            reminderEnabled.setChecked(isEnabled != 0);
            reminderIcon.setImageResource(isEnabled != 0 ?
                    R.drawable.ic_alarm_on : R.drawable.ic_alarm_off);

            // listeners
            ImageView reminderEdit = (ImageView) view.findViewById(R.id.reminder_edit);
            reminderEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    TimePickerDialog dialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    editReminder(id, hourOfDay, minute);
                                }
                            },
                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    dialog.show();
                }
            });

            ImageView reminderDelete = (ImageView) view.findViewById(R.id.reminder_delete);
            reminderDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteReminder(id);
                }
            });

            reminderEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    enableReminder(id, isChecked);
                }
            });

        }
    };

    private class ReminderOnClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



        }
    };

}
