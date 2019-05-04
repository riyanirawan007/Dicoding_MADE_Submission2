package app.android.rynz.imoviecatalog.broadcaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.Calendar;

import app.android.rynz.imoviecatalog.R;
import app.android.rynz.imoviecatalog.util.lib.EasyNotification;
import app.android.rynz.imoviecatalog.util.ExtraKeys;
import app.android.rynz.imoviecatalog.view.ui.HomeActivity;

public class AlarmReceiver extends BroadcastReceiver
{
    public static final String TYPE_DAILY_REMINDER="DailyReminderAlarm";
    public static final int DAILY_REMINDER_NOTIFICATION_ID =77;

    public AlarmReceiver()
    {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String msg=intent.getStringExtra(ExtraKeys.EXTRA_ALARM_MSG);
        String type=intent.getStringExtra(ExtraKeys.EXTRA_ALARM_TYPE);
        String title=context.getResources().getString(R.string.notification_daily_reminder_title);
        Intent intentHome=new Intent(context,HomeActivity.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                |Intent.FLAG_ACTIVITY_CLEAR_TOP
                |Intent.FLAG_ACTIVITY_SINGLE_TOP
                |Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,DAILY_REMINDER_NOTIFICATION_ID,intentHome,PendingIntent.FLAG_UPDATE_CURRENT);
        new EasyNotification()
                .with(context,title,msg, DAILY_REMINDER_NOTIFICATION_ID)
                .setContentIntent(pendingIntent)
                .show();
    }

    public void setDailyReminder(View v, Context context, String type, String time, String message){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ExtraKeys.EXTRA_ALARM_MSG, message);
        intent.putExtra(ExtraKeys.EXTRA_ALARM_TYPE, type);
        String timeArray[] = time.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        calendar.set(Calendar.SECOND, 0);
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(context, DAILY_REMINDER_NOTIFICATION_ID, intent, 0);
        if(alarmManager!=null)alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Snackbar.make(v,context.getResources().getString(R.string.info_daily_reminder_setup),3500).show();
    }

    public void cancelDailyReminder(View v, Context context, String type){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_NOTIFICATION_ID, intent, 0);
        if(alarmManager!=null) alarmManager.cancel(pendingIntent);
        Snackbar.make(v,context.getResources().getString(R.string.info_daily_reminder_cancel),3500).show();
    }
}
