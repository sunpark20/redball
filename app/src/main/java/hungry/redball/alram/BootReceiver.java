package hungry.redball.alram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the alarm, the receiver is enabled.
 * When the user cancels the alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
// BEGIN_INCLUDE(autostart)
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Log.e("BootReceiver", "반복 스케줄 동작합니다.");
            RepeatReceiver repeatAlarm = new RepeatReceiver();
            //Context context, int RequestCode
            repeatAlarm.setAlarm(context, 0);

            /*//실험
            AlarmReceiver alarm= new AlarmReceiver();
            for(int i=0;i<10;i++)
                alarm.setAlarm(context, i, i*1000 ,0, null);

            for(int i=5;i<10;i++)
                alarm.cancelAlarm(context, i);

            //실험
            for(int i=0;i<10;i++)
                alarm.setAlarm(context, i, i*1000 ,0, null);*/
        }
    }
}
//END_INCLUDE(autostart)
