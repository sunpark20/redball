package hungry.redball.alram;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import hungry.redball.LoadingActivity;
import hungry.redball.MainActivity;
import hungry.redball.R;
import hungry.redball.aStatic.StaticMethod;
import hungry.redball.aStatic.StaticPref;
import hungry.redball.alram.model.PrefInfo;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SchedulingService extends IntentService {
    public SchedulingService() {
        super("SchedulingService");
    }
    
    public static final String TAG = "SchedulingService";
    // An ID used to post the notification.
    //실행할 때 마다 만드려면  id 값 ++ 하세요.
    public static final int NOTIFICATION_ID = 1;
    // The string the app searches for in the Google home page content. If the app finds 
    // the string, it indicates the presence of a doodle.  
    public static final String SEARCH_STRING = "doodle";
    // The Google home page URL from which the app fetches content.
    // You can find a list of other Google domains with possible doodles here:
    // http://en.wikipedia.org/wiki/List_of_Google_domains
    public static final String URL = "http://www.google.com";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    int when;
    long msecond;

    @Override
    protected void onHandleIntent(Intent intent) {

        when=intent.getIntExtra("when", -1);
        msecond=intent.getLongExtra("msecond", -1);

        if(when==0){
            String msg=intent.getStringExtra("msg");
            StaticMethod.acquireCpuWakeLock(this);
            sendNotification(msg);
        }else if(when==1){
            int RequestCode=intent.getIntExtra("RequestCode", -1);
            //여러개도 받을 수 있도록 세팅해놓음 (현재는 1씩 받아서 쓰네요)

            //알람이 울릴 때 해당 경기를 지워준다.
            //관심팀 해쉬맵 로드
            Map<Integer, PrefInfo> prefInfo= StaticPref.loadPref_prefInfo(getApplicationContext());
            prefInfo.remove(RequestCode);
            //관심팀 해쉬맵 저장
            StaticPref.savePref_prefInfo(getApplicationContext(), prefInfo);

            //몽고db에서 자료를 받아 온다.
            ArrayList<Integer> xxx=new ArrayList<Integer>();
            xxx.add(RequestCode);
            QueryBuilder_total.codeArray=xxx;
            QueryBuilder_total qb = new QueryBuilder_total();
            String urlString=qb.buildContactsGetURL();
            //보낸 url api 주소 확인
            Log.e(TAG, urlString);
            String result ="";
            try {
                result = loadFromNetwork(urlString);
                // If the app finds the string "doodle" in the Google home page content, it
                // indicates the presence of a doodle. Post a "Doodle Alert" notification.

                try{
                    StaticMethod.acquireCpuWakeLock(this);
                    JSONArray ja=new JSONArray(result);
                    String msg="";

                    //팀한글화하기 파일읽는 부분, 그리고 어레이 만들때 넣어줌 (최초한번~)
                    JSONObject eTokJson=null;
                    String eTok= StaticMethod.loadJSONFromAsset("teamEtoK.json", getBaseContext());
                    try{
                        eTokJson=new JSONObject(eTok);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //사실 1번만 실행된다. (code를 array리스트로 주면서 다중실행)여러개 불러오기도 가능하다.
                    for(int i=0; i<ja.length(); i++){
                        JSONObject jo=ja.getJSONObject(i);
                        String home=jo.get("home").toString();
                        //홈 어웨이 한글화하기
                        home=eTokJson.get(home).toString();
                        String away=jo.get("away").toString();
                        away=eTokJson.get(away).toString();
                        msg+=home+" "+jo.get("score")+" "+away;
                    }
                    sendNotification(msg);
                    Log.e(TAG, result);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e(TAG, "connection_error");
            }
        }
        /*else if(when==1){
            // BEGIN_INCLUDE(service_onhandle)
            // The URL from which to fetch content.
            //date를 쪼개서 query에 넣어줘야합니다.
            try{
                ArrayList<Integer> al=new ArrayList<Integer>();
                al=StaticMethod.hashMap2.get(msecond).getCode();
                for(int i=0;i<al.size();i++)
                    Log.e(TAG, al.get(i)+"");
                ArrayList<Integer> xxx=StaticMethod.hashMap2.get(msecond).getCode();
                QueryBuilder_total.codeArray=xxx;
            }catch (Exception e){
                e.printStackTrace();
            }
            QueryBuilder_total qb = new QueryBuilder_total();
            String urlString=qb.buildContactsGetURL();
            //보낸 url api 주소 확인
            Log.e(TAG, urlString);
            String result ="";
            try {
                result = loadFromNetwork(urlString);
            } catch (IOException e) {
                Log.e(TAG, "connection_error");
            }
            // If the app finds the string "doodle" in the Google home page content, it
            // indicates the presence of a doodle. Post a "Doodle Alert" notification.
            StaticMethod.acquireCpuWakeLock(this);
            try{
                JSONArray ja=new JSONArray(result);
                String msg="";
                for(int i=0; i<ja.length(); i++){
                    JSONObject jo=ja.getJSONObject(i);
                    msg+=jo.get("home")+" "+jo.get("score")+" "+jo.get("away")+"\n";
                }
                sendNotification(msg);
                Log.e(TAG, result);
            }catch (Exception e){
                e.printStackTrace();
            }
        } //메세지 뽑기 끝 (msg에 다 담김) when 0 일때 쓰는겁니다. 나중에 하자. notifi로 주기만 하면 됨.*/
        // Try to connect to the Google homepage and download content.
        // Release the wake lock provided by the BroadcastReceiver.
        AlarmReceiver.completeWakefulIntent(intent);
        // WakeLock 해제.
        StaticMethod.releaseCpuLock();
        // END_INCLUDE(service_onhandle)
    }

    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
        this.getSystemService(Context.NOTIFICATION_SERVICE);

        String title="";
        if(when==0)
            title="경기시작 20분전";
        else if(when==1)
            title="경기종료";

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.mipmap.icon_redball)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg)
                .setSummaryText("누르면 모아보기로 접속"))
        .setContentText(msg)
        // 최상위로 올리는 옵션
        //.setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_MAX);

        //누르면 관심팀액티비티 연결하기
        Intent resultIntent = new Intent(this, LoadingActivity.class);
        resultIntent.setAction("ACTION_NOTIFICATION");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        //누르면 관심팀액티비티 연결하기

        /*//누르면 사라지게함.
        PendingIntent notifyPIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
        mBuilder.setContentIntent(notifyPIntent);*/

        //온고잉이랑 같이 사라지게 할려면 써야됨.  bit 머시기 때문에 검색하면나옴...
        Notification notification=mBuilder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
 
//
// The methods below this line fetch content from the specified URL and return the
// content as a string.
//
    /** Given a URL string, initiate a fetch operation. */
    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String str ="";
      
        try {
            stream = downloadUrl(urlString);
            str = readIt(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }      
        }
        return str;
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {
    
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    /** 
     * Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from www.google.com.
     * @return String version of InputStream.
     * @throws IOException
     */
    private String readIt(InputStream stream) throws IOException {

        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        for(String line = reader.readLine(); line != null; line = reader.readLine())
            builder.append(line);
        reader.close();
        return builder.toString();
    }

}
