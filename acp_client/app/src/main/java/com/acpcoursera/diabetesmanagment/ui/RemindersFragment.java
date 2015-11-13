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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TimePicker;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.provider.DmContract;
import com.acpcoursera.diabetesmanagment.receiver.CheckInPublisher;

import java.util.Calendar;

public class RemindersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = RemindersFragment.class.getSimpleName();

    private static final int TEST_LOADER = 0;

    private SimpleCursorAdapter mAdapter;
    private AlarmManager mAlarmManager;
    private ListView mReminders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_reminders, container, false);

        getLoaderManager().initLoader(TEST_LOADER, null, this);

        mReminders = (ListView) rootView.findViewById(R.id.reminders_list_view);

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.reminder_item,
                null,
                new String[]
                        {
                                DmContract.Reminders._ID,
                                DmContract.Reminders.HOUR_OF_DAY,
                                DmContract.Reminders.MINUTE,
                                DmContract.Reminders.IS_ENABLED
                        },
                new int[]
                        {
                                R.id.reminder_id,
                                R.id.reminder_hour_of_day,
                                R.id.reminder_minute,
                                R.id.reminder_is_enabled
                        },
                0
        );

        mReminders.setAdapter(mAdapter);

        setHasOptionsMenu(true);

        initObligatoryReminders();
        mAlarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

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
                        .getBroadcast(getActivity(), reminderId, intent, 0);
                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);
                cursor.close();
            }
        }
    }

}
