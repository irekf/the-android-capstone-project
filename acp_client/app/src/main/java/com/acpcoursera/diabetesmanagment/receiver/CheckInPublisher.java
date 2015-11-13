package com.acpcoursera.diabetesmanagment.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.ui.CheckInActivity;

public class CheckInPublisher extends BroadcastReceiver {

    private static String TAG = CheckInPublisher.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager
                = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent checkInIntent = new Intent(context, CheckInActivity.class);
        checkInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, checkInIntent, 0);

        notificationManager.notify(0, createCheckInNotification(context, pendingIntent));

    }

    private Notification createCheckInNotification(Context context, PendingIntent intent) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Time to check in.");
        builder.setSmallIcon(R.drawable.abc_ic_menu_paste_mtrl_am_alpha);
        builder.setContentIntent(intent);
        builder.setAutoCancel(true);
        return builder.build();
    }

}
