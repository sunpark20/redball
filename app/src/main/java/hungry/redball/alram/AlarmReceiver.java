package hungry.redball.alram;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent 
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    public static final String TAG = "AlarmReceiver";
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
  
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("CALL_AlarmReceiver")){
            int when=intent.getIntExtra("when", -1);
            long msecond=intent.getLongExtra("msecond", -1);
            String msg=intent.getStringExtra("msg");
            int RequestCode=intent.getIntExtra("RequestCode", -1);

            if(when==1)  //code에 temppuls변수를 다시 빼서 code를 되돌린다.
                RequestCode-=RepeatReceiver.TEMPPLUS;

            //확인용
            Log.e(TAG, "알람 리시버 받았숑" + " msecond: " + msecond + "when: " + when + "RequestCode: "+RequestCode+"\n" + msg);
            Intent service = new Intent(context.getApplicationContext(), SchedulingService.class);
            service.putExtra("when", when);
            service.putExtra("msecond", msecond);
            service.putExtra("msg", msg);
            service.putExtra("RequestCode", RequestCode);

            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, service);
        }
    }

    public void setAlarm(Context context, int RequestCode,long msecond, int when, String msg) {
        //set하는것 프린터 (확인용)
        Log.e(TAG, "setAlarm" + "msecond: " + msecond + " when: " + when + "request: " + RequestCode +
                "\n" + msg);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("CALL_AlarmReceiver");
        intent.putExtra("msecond", msecond);
        intent.putExtra("when", when);
        intent.putExtra("msg", msg);
        intent.putExtra("RequestCode", RequestCode);
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(context, RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Here are some examples of ELAPSED_REALTIME_WAKEUP:
        // Wake up the device to fire a one-time alarm in one minute.
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + msecond, alarmIntent);
                 
//          // Wake up the device to fire the alarm in 30 minutes, and every 30 minutes
//          // after that.
//          alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                  AlarmManager.INTERVAL_HALF_DAY,
//                  AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);

//        //based on calendar
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        // Set the alarm's trigger time to 8:30 a.m.
//        calendar.set(Calendar.HOUR_OF_DAY, 8);
//        calendar.set(Calendar.MINUTE, 30);
//        // Set the alarm to fire at approximately 8:30 a.m., according to the device's

//        // clock, and to repeat once a day.
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
//

//        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
//        // device is rebooted.
//        ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
//        PackageManager pm = context.getPackageManager();
//
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);


//        //유저가 알람을 캔슬하면 이렇게 설정하면 된다.
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);

    }
    // END_INCLUDE(set_alarm)

    /**
     * Cancels the alarm.
     * @param context
     */

    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context, int RequestCode) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("CALL_AlarmReceiver");
        PendingIntent p = PendingIntent.getBroadcast(context, RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(p);
        Log.e(TAG, "cancel alarm, request: " + RequestCode);
    }
    // END_INCLUDE(cancelAlarm)

    /*// BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }

        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(cancel_alarm)*/
}
