package hungry.redball.alram;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import hungry.redball.LoadingActivity;
import hungry.redball.aStatic.StaticMethod;
import hungry.redball.aStatic.StaticPref;
import hungry.redball.alram.model.PrefInfo;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent 
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
public class RepeatReceiver extends WakefulBroadcastReceiver {
    public static final String TAG = "RepeatReceiver";
    Calendar c_now, c_until;
    private AlarmReceiver alarm;

    public static final int TEMPPLUS=1000000;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "RepeatReceiver 리시버님 받았습니다");
        alarm = new AlarmReceiver();
        setTime();
        setAlarmFromJson(context);

    }
    //END INCLUDE(onReceive)

    public void setTime(){
        //1.c_now
        //현재 시간을 받는다
        c_now = Calendar.getInstance();

        //테스팅용
        //c_now.set(2016, 0, 6, 22, 40);

        //*until initialize
        c_until = Calendar.getInstance();
        c_until.setTime(c_now.getTime());

        String pNow_kor= fomatedDate(c_now.getTime());

        //2.c_until set
        c_until.add(Calendar.DAY_OF_MONTH, 1);
        String pUntil_kor = fomatedDate(c_until.getTime());

        Log.e(TAG, "알람 장전." + pNow_kor + " ~" + pUntil_kor);
    }
    //END INCLUDE(setTime)

    public void setAlarm(Context context, int REQUESTCODE) {
        Log.e(TAG, "set RepeatAlarm" + REQUESTCODE);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RepeatReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(context, REQUESTCODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    AlarmManager.INTERVAL_DAY, p);
        }else{
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    AlarmManager.INTERVAL_DAY, p);
        }
    }
    // END_INCLUDE(setAlarm)

    private String fomatedDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        return format.format(date).toString();
    }
    // END_INCLUDE(fomatedDate)

    public void setAlarmFromJson(Context context){
        //json을 캘린더로 변경한 다음 비교하자 그럼 캘린더가 알아서 비교해주겠지 .
        //팀한글화하기
        JSONObject eTokJson=null;
        String eTok=StaticMethod.loadJSONFromAsset("teamEtoK.json", context);
        try{
            eTokJson=new JSONObject(eTok);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            ArrayList<String> savedTeamArray=(StaticPref.loadPref_prefTeam(context));
            Log.e(TAG, savedTeamArray.toString());

            String loadMatchInfo=StaticPref.loadPref_String(context,TAG, LoadingActivity.JSON_MATCH);
            JSONArray ja=new JSONArray(loadMatchInfo);

            for(int i=0;i<ja.length(); i++){

                boolean teamOk=false;
                JSONObject row=ja.getJSONObject(i);
                Calendar c_json=setJsonCal(row);
                String home=row.get("home").toString();
                String away=row.get("away").toString();
                int code=(int)row.get("code");

                //즐찾된 팀 걸러내기
                for(int j=0;j<savedTeamArray.size();j++){
                    if(savedTeamArray.get(j).compareTo(home)==0 ||
                            savedTeamArray.get(j).compareTo(away)==0){
                        teamOk=true;
                    }
                }

               /* if(StaticMethod.TT_RepeatReceiver); //실험 즐찾팀에 상관없이 모든 알림받기.
                     teamOk=true;*/

                //매치번호 팀 걸러내기 로도 true 주면 된다.
                if(teamOk
                        && c_json.compareTo(c_now)==1 && c_json.compareTo(c_until)==-1){
                    add_teamPref(context, code, home, away);
                    //0알람
                    // json - 현재시간 밀리세컨드를 구한다.
                    long ms=c_json.getTimeInMillis()-c_now.getTimeInMillis()-1000*60*20;

                    //홈 어웨이 한글화하기
                    home=eTokJson.get(home).toString();
                    away=eTokJson.get(away).toString();

                    String msg=home+" vs "+away;

                    ms=c_json.getTimeInMillis()-c_now.getTimeInMillis()-1000*60*20;
                    alarm.setAlarm(context, code, ms, 0, msg);

                    //1알람
                    long ms1=c_json.getTimeInMillis()-c_now.getTimeInMillis()+1000*60*135;

//                    //테스팅용
//                    ms1=testSet(c_json, c_now);

                    //code에 temppuls변수를 더해준다.
                    alarm.setAlarm(context, code+RepeatReceiver.TEMPPLUS, ms1, 1, null);
                }
            } //end for

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //END INCLUDE(setAlarmFromJson)

    private void add_teamPref(Context context,int RequestCode,String home, String away){
        Map<Integer, PrefInfo> prefInfo= StaticPref.loadPref_prefInfo(context);

        PrefInfo p=new PrefInfo();
        p.sethTeam(home);
        p.setaTeam(away);

        if(!prefInfo.containsKey(RequestCode)) {
            prefInfo.put(RequestCode, p);
            StaticPref.savePref_prefInfo(context, prefInfo);
        }
    }

    private Calendar setJsonCal(JSONObject row)throws Exception{
        JSONObject date=row.getJSONObject("date");
        int year=StaticMethod.sToi(date.get("year").toString());
        int month=StaticMethod.sToi(date.get("month").toString());
        int day=StaticMethod.sToi(date.get("day").toString());
        String time=row.get("time").toString();
        int hour=StaticMethod.sToi(time.split(":")[0]);
        int minute=StaticMethod.sToi(time.split(":")[1]);
        //캘린더의 month가 0~ 11 이기 떄문에 -1을 해줍니다.
        Calendar c=Calendar.getInstance();
        c.set(year, month - 1, day, hour, minute);
        return c;
    }

    // 즐찾설정 액티비티에서 쓰는거요
    public void setAddedTeamAlarm(Context context, Set<String> addedSet){
        alarm = new AlarmReceiver();
        //json을 캘린더로 변경한 다음 비교하자 그럼 캘린더가 알아서 비교해주겠지 .
        //팀한글화하기
        JSONObject eTokJson=null;
        String eTok=StaticMethod.loadJSONFromAsset("teamEtoK.json", context);
        try{
            eTokJson=new JSONObject(eTok);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            String loadMatchInfo=StaticPref.loadPref_String(context,TAG, LoadingActivity.JSON_MATCH);
            JSONArray ja=new JSONArray(loadMatchInfo);
            for(int i=0;i<ja.length(); i++){
                //추가된 팀만 추가
                boolean teamOk=false;
                JSONObject row=ja.getJSONObject(i);
                Calendar c_json=setJsonCal(row);
                String home=row.get("home").toString();
                String away=row.get("away").toString();
                int code=(int)row.get("code");

                //추가된 팀만 추가
                if(addedSet.contains(home) || addedSet.contains(away))
                        teamOk=true;

                if(teamOk
                        && c_json.compareTo(c_now)==1 && c_json.compareTo(c_until)==-1){
                    //관심팀 액티비티에 추가하기
                    add_teamPref(context, code, home, away);
                    //0알람
                    // json - 현재시간 밀리세컨드를 구한다.
                    long ms=c_json.getTimeInMillis()-c_now.getTimeInMillis()-1000*60*20;

                    //홈 어웨이 한글화하기
                    home=eTokJson.get(home).toString();
                    away=eTokJson.get(away).toString();

                    String msg=home+" vs "+away;
                    alarm.setAlarm(context, code, ms, 0, msg);

                    //1알람
                    long ms1=c_json.getTimeInMillis()-c_now.getTimeInMillis()+1000*60*135;

//                    //테스팅용
//                    ms1=testSet(c_json, c_now);

                    //code에 temppuls변수를 더해준다.
                    alarm.setAlarm(context, code+RepeatReceiver.TEMPPLUS, ms1, 1, null);
                }
            } //end for

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //END INCLUDE(setAlarmFromJson)

    // 경기일정에서 알람 추가 할 때 쓰는거요.
    public void setAlarmFromFixtures(Context context, int code){

        alarm = new AlarmReceiver();
        System.out.println(code+"를 찾습니다.");

        try{
            String loadMatchInfo=StaticPref.loadPref_String(context,TAG, LoadingActivity.JSON_MATCH);
            JSONArray ja=new JSONArray(loadMatchInfo);
            for(int i=0;i<ja.length(); i++){
                JSONObject row=ja.getJSONObject(i);
                Calendar c_json=setJsonCal(row);
                String jHome=row.get("home").toString();
                String jAway=row.get("away").toString();
                int jCode=(int)row.get("code");

                if(jCode==code
                        && c_json.compareTo(c_now)==1 && c_json.compareTo(c_until)==-1){
                    System.out.println("만족함"+c_json.getTime()+"  "+c_now.getTime());
                    //관심팀 액티비티에 추가하기
                    add_teamPref(context, code, jHome, jAway);
                    //0알람
                    // json - 현재시간 밀리세컨드를 구한다.
                    long ms=c_json.getTimeInMillis()-c_now.getTimeInMillis()-1000*60*20;

                    String msg=jHome+" vs "+jAway;
                    alarm.setAlarm(context, code, ms, 0, msg);

                    //1알람
                    long ms1=c_json.getTimeInMillis()-c_now.getTimeInMillis()+1000*60*135;

//                    //테스팅용
//                    ms1=testSet(c_json, c_now);

                    //code에 temppuls변수를 더해준다.
                    alarm.setAlarm(context, code+RepeatReceiver.TEMPPLUS, ms1, 1, null);
                    break;
                }

            } //end for

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //END INCLUDE(setAlarmFromJson)
    private long testSet(Calendar c_json, Calendar c_now){
        return c_json.getTimeInMillis()-c_now.getTimeInMillis()-1000*60*19;
    }
}
