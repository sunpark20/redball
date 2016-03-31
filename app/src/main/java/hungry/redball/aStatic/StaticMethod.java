package hungry.redball.aStatic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by soy on 2015-11-26.
 */
public class StaticMethod {
    private static PowerManager.WakeLock sCpuWakeLock;
    //플레이어 저장하는 변수.
    private static JSONArray[] jArr=new JSONArray[5];
    public static synchronized JSONArray getJ(int i){
        return StaticMethod.jArr[i];
    }
    public static synchronized void setJ(JSONArray jArr, int i){
        StaticMethod.jArr[i]= jArr;
    }

    public static JSONArray[] jArr_team=new JSONArray[5];




    //시간측정
    static private long start,end;



    public static void startTime(){
        start=System.currentTimeMillis();
    }
    public static void endTime(){
        end = System.currentTimeMillis();
        System.out.println( "------------------실행 시간 : " + ( end - start )/1000.0 );
    }


    public static void fToast(Context context, String string){
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }
    //네트워크 연결상태 체크 메소드
    public static boolean isNetworkConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        return false;
    }

    //파일 저장
    public static void saveJsonFile(Context context, JSONObject jsonObject) throws Exception{
        String state = Environment.getExternalStorageState();
        String externalPath = null;
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            externalPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else if (state.equals(Environment.MEDIA_UNMOUNTED)) {
            Toast.makeText(context, "MEDIA_UNMOUNTED", Toast.LENGTH_SHORT).show();
        } else if (state.equals(Environment.MEDIA_UNMOUNTABLE)) {
            Toast.makeText(context, "MEDIA_UNMOUNTABLE", Toast.LENGTH_SHORT).show();
        }
        String dirName = context.getPackageName();
        File file = new File(externalPath + "/" + dirName);
        file.mkdir();
        String path = externalPath + "/" + dirName;
        PrintWriter writer = new PrintWriter(path + "/league_team_korean.json");
        String str=jsonObject.toString();
        writer.print((Object)str);
        writer.close();
        Log.e("파일 만들기", "파일만들기 완료");
    }

    //파일 읽기
    public static String loadJSONFromAsset(String fileName, Context c) {
        String json = null;
        try {
            InputStream is = c.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    public static void acquireCpuWakeLock(Context context) {
        Log.e("PushWakeLock", "Acquiring cpu wake lock");
        Log.e("PushWakeLock", "wake sCpuWakeLock = " + sCpuWakeLock);

        if (sCpuWakeLock != null) {
            return;
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "hello");

        sCpuWakeLock.acquire();
    }

    public static void releaseCpuLock() {
        Log.e("PushWakeLock", "Releasing cpu wake lock");
        Log.e("PushWakeLock", "relase sCpuWakeLock = " + sCpuWakeLock);

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }

    public static int sToi(String str){
        int newInt=Integer.parseInt(str);
        return newInt;
    }
    public static String iTos(int i){
        String s=String.valueOf(i);
        return s;
    }

    public static Calendar setJsonCal(JSONObject row)throws Exception {
        JSONObject date = row.getJSONObject("date");
        int year = StaticMethod.sToi(date.get("year").toString());
        int month = StaticMethod.sToi(date.get("month").toString());
        int day = StaticMethod.sToi(date.get("day").toString());
        String time = row.get("time").toString();
        int hour = StaticMethod.sToi(time.split(":")[0]);
        int minute = StaticMethod.sToi(time.split(":")[1]);

        //타임존 세팅
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Belfast"));
        //캘린더의 month가 0~ 11 이기 떄문에 -1을 해줍니다.
        cal.set(year, month - 1, day, hour, minute);
        return cal;
    }



    public static JSONArray concatJsonArray(JSONArray... arrs)
            throws JSONException {
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.length(); i++) {
                result.put(arr.get(i));
            }
        }
        return result;
    }

    public static JSONArray RemoveJSONArray( JSONArray jarray,int pos) {

        JSONArray Njarray=new JSONArray();
        try{
            for(int i=0;i<jarray.length();i++){
                if(i!=pos)
                    Njarray.put(jarray.get(i));
            }
        }catch (Exception e){e.printStackTrace();}
        return Njarray;
    }
}