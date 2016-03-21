package hungry.redball;


import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.CountDownLatch;

import hungry.redball.aStatic.StaticMethod;
import hungry.redball.aStatic.StaticPref;
import hungry.redball.alram.PrefActivity;
import hungry.redball.alram.RepeatReceiver;
import hungry.redball.team.url.Url_team_thread;
import hungry.redball.util.QueryBuilder_loading;

public class LoadingActivity extends AppCompatActivity {

    private final String TAG="LoadingActivity";

    //networkCheck dialog
    private AlertDialog networkCheckDialog;

    public static final String JSON_MATCH="JSON_MATCH";
    private boolean enterFromNotify;

    //프로그래스바
    private TextView tv;
    public static MyHandler mHandler;
    private final int PROGRESS_NUM=4;
    private final int PROGRESS_INT=(100/PROGRESS_NUM) + 1;
    private int value = 0;
    private ProgressBar progBar;
    int myHandlerCount=0;

    //몽고db와 동기화 제어하는 countDownLatch
    CountDownLatch latch1 = new CountDownLatch(1);
    JSONArray FixJsonArray=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        tv =(TextView)findViewById(R.id.textView);
        progBar= (ProgressBar)findViewById(R.id.progBar1);

        if(!StaticMethod.isNetworkConnected(getBaseContext()))
            showNetworkDialog();

        mHandler= new MyHandler(this);
        progressWork();

        //FixturesUpdate에서 capsulation: total->date->>result3뺴기-> serviceUp
        // (date의 onpost에서 result3과 serviceup실행해줌)
        // 개판소스인데 고치고싶다.
        try{
            FixturesUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }

        //노티피에서 왔냐 안왔냐? capsul-1
        try{
            Intent nIntent = getIntent();
            if (nIntent.getAction().equals("ACTION_NOTIFICATION")){
                Log.e(TAG,"노티피에서 오셨습니다. 스코어 다운이 완료되면 pref로 접속합니다.");
                tv.append("관심팀 점수를 가져옵니다.");
                enterFromNotify=true;
                return;
            }
        }catch (Exception e){
            Log.e(TAG, "걍 넘겨");
        }

        //노티피에서 왔냐 안왔냐? capsul-2
        //$문제소스 플레이어 다운 하루간격 11로 체크해서 다운받기
        if(!enterFromNotify) {
            tv.append("최초 접속시 선수정보를 다운(최대 1분 소요)\n");
            new Thread_player(this).execute();
            //팀 다운
            for(int i=0;i<1;i++)
                new Url_team_thread().execute(i);
        }

    }

    private void serviceUp(){
        Intent intent = new Intent(this, RepeatReceiver.class);
        boolean alarmUp = (PendingIntent.getBroadcast(this, 0,
                intent,
                PendingIntent.FLAG_NO_CREATE) != null);
        if(alarmUp){
            Log.e(TAG, "알람이가 동작중");
        }else{
            Log.e(TAG, "알람이 부팅에서 안켜졌네. 여기서 킵니다.");
            RepeatReceiver repeatAlarm = new RepeatReceiver();
            //Context context, int RequestCode //무조건 0 주면 된다.
            repeatAlarm.setAlarm(this, 0);
        }
    }

    private void progressWork(){
        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (value < 100) {
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        int limit=(myHandlerCount)*PROGRESS_INT;
                        public void run() {
                            if (value < limit) {
                                value += 1;
                            }
                            progBar.setProgress(value);
                        }
                    });
                    try {
                        //Display progress slowly
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void myHandleMessage(Message msg) {
        myHandlerCount++;
        Log.e("LoadingActivity", "count: " + myHandlerCount + " " + "msg: " + msg.what);
        switch (msg.what) {
            case 999:
                tv.append("오류 발생. 네트워크환경체크 후 재접속 해주세요.\n");
                myHandlerCount--;
                break;
            case 1:
                tv.append(msg.what+"fixtures total  complete"+myHandlerCount*PROGRESS_INT+"%\n");
                break;
            case 2:
                if(enterFromNotify) //스코어 받으면 진입하려고 여기 있는거네.
                    startPrefActivity();
                tv.append(msg.what+"fixtures date  complete"+myHandlerCount*PROGRESS_INT+"%\n");
                break;
            case 3:
                tv.append(msg.what+"player  complete"+myHandlerCount*PROGRESS_INT+"%\n");
                break;
            case 4:
                tv.append(msg.what+"team  complete"+myHandlerCount*PROGRESS_INT+"%\n");
                break;
        }
        //선수 로딩 막음(주석제거)
        if(myHandlerCount>=PROGRESS_NUM)
            startActivity();
        //선수 로딩 막음(주석)
    }

    private void startActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void startPrefActivity(){
        Intent intent = new Intent(this, PrefActivity.class);
        startActivity(intent);
        this.finish();
    }

    public static class MyHandler extends Handler {
        private final WeakReference<LoadingActivity> mActivity;

        public MyHandler(LoadingActivity activity) {
            mActivity = new WeakReference<LoadingActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoadingActivity activity = mActivity.get();
            if (activity != null) {
                activity.myHandleMessage(msg);
            }
        }
    }

    private void FixturesUpdate() throws Exception{
        String loadMatchInfo=StaticPref.loadPref_String(this,TAG,JSON_MATCH);
        //최초 로딩시, 그 다음 접속 구분해서 fixtures틀 만들기
        if(loadMatchInfo.compareTo("defValue")==0) {
            Log.e(TAG, "Fixtures Thread1-Total start download from mongoDB(2016년만받음)");
            new Thread_query_total(this).execute();
        }else{
            Log.e(TAG, "Fixtures Thread1-Total start load from sharedPreference");
            FixJsonArray=new JSONArray(loadMatchInfo);
            LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(1));
            latch1.countDown();
        }
        Log.e(TAG, "Fixtures Thread2-Date start (2016년을 전부 업데이트하기 떄문에 최적화필요)");
        //$문제소스 json끼리 검색할때 엄청오래걸림.
        new Thread_query_date(this).execute();
    }

    //0. start query_total thread class
    class Thread_query_total extends AsyncTask<String, Void, String> {
        Context context;
        String result ="";

        public Thread_query_total(Context context){
            this.context=context;
        }

        @Override
        protected String doInBackground(String... arg0) {

            QueryBuilder_loading qb = new QueryBuilder_loading();
            String urlString=qb.buildTotalUrl();
            //보낸 url api 주소 확인
            Log.e(TAG, "Thread_query_total의 URL: "+urlString);

            try {
                result = loadFromNetwork(urlString);
            }catch (IOException e) {
                Log.e(TAG, "connection_error");
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{
                FixJsonArray=new JSONArray(result);
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.e(TAG, "Fixtures Thread-Total complete");
            LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(1));
            latch1.countDown();
        }

    }//END query_score class

    //1. start query_date thread class
    class Thread_query_date extends AsyncTask<String, Void, String> {
        Context context;
        String result ="";

        public Thread_query_date(Context context){
            this.context=context;
        }

        @Override
        protected String doInBackground(String... arg0) {

            try{
                latch1.await();
            }catch (Exception e){
                e.printStackTrace();
            }

            //임시-2016년것 다 받아오기 (최적화 필요)
            QueryBuilder_loading qb = new QueryBuilder_loading();
            String urlString=qb.buildDateUrl();
            //보낸 url api 주소 확인
            Log.e(TAG, urlString);
            try {
                result = loadFromNetwork(urlString);
            }catch (IOException e) {
                Log.e(TAG, "connection_error");
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{
                JSONArray result_ja=new JSONArray(result);
                for(int i=0;i<result_ja.length();i++){
                    JSONObject result_jo=result_ja.getJSONObject(i);
                    String result_code=result_jo.get("code").toString();

//                    System.out.println(result_jo.toString());
                    for(int j=0;j<FixJsonArray.length();j++) {
                        JSONObject jo=FixJsonArray.getJSONObject(j);
                        String code=jo.get("code").toString();
                        if(code.compareTo(result_code)==0){
                            //이전 날짜랑 오늘 날짜랑 비교하자.
                            //1.result들을 json으로 바꿈
                            Calendar cal = StaticMethod.setJsonCal(result_jo);
                            Calendar localTime = new GregorianCalendar();
                            localTime.setTimeInMillis(cal.getTimeInMillis());

                            //time change
                            String h=StaticMethod.iTos(localTime.get(Calendar.HOUR_OF_DAY));
                            String m=StaticMethod.iTos(localTime.get(Calendar.MINUTE));
                            if(h.length()==1)
                                h="0"+h;
                            if(m.length()==1)
                                m="0"+m;
                            String time=h + ":" + m;
//                            System.out.println(h + ":" + m +" 이전데이터:"+jo.get("time")+" "+result_jo.get("date"));

                            //date change
                            //"date": {"year": "2015", "day": "9", "month": "08"}
                            String year=StaticMethod.iTos(localTime.get(Calendar.YEAR));
//                            System.out.println(year);
                            JSONObject date=jo.getJSONObject("date");
                            date.put("year", year);
                            String month=StaticMethod.iTos(localTime.get(Calendar.MONTH)+1);
                            if(month.length()==1)
                                month="0"+month;
//                            System.out.println(month);
                            date.put("month", month);
                            String day=StaticMethod.iTos(localTime.get(Calendar.DAY_OF_MONTH));
//                            System.out.println(day);
                            date.put("day", day);
//                            System.out.println(jo.toString());

                            //이제 time 이랑 date 다 넣을꺼다.
                            //time 넣기
                            if(time.compareTo("")!=0)
                                FixJsonArray.getJSONObject(j).put("time", time);
                            else
                                System.out.println("이건 비어 있으면 안되는디우??"+code+time);

                            //date넣기
                            if(date.toString().compareTo("")!=0)
                                FixJsonArray.getJSONObject(j).put("date", date);
                            else
                                System.out.println("이건 비어 있으면 안되는디우??"+code+date);

                            //score넣기
                            FixJsonArray.getJSONObject(j).put("score", result_jo.get("score"));
                            break;
                        }
                    }
                } //end for

            }catch (Exception e){
                e.printStackTrace();
            }
            Log.e(TAG, "Fixtures Thread-Date complete");

            StaticPref.savePref_String(context, TAG, FixJsonArray.toString(), JSON_MATCH);
            serviceUp();
            LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(2));
        }
    }//END query_date class

    //0-1. start query_total thread class
    class Thread_player extends AsyncTask<String, Void, String> {
        Context context;
        String result ="";

        public Thread_player(Context context){
            this.context=context;
        }

        @Override
        protected String doInBackground(String... arg0) {

            QueryBuilder_loading qb = new QueryBuilder_loading();
            String urlString=qb.buildPlayerUrl();
            //보낸 url api 주소 확인
            Log.e(TAG, "Thread_player의 URL: "+urlString);

            try {
                result = loadFromNetwork(urlString);
            }catch (IOException e) {
                Log.e(TAG, "connection_error");
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String[] leagueName={
                    "Premier League",
                    "La Liga",
                    "Bundesliga",
                    "Serie A",
                    "Ligue 1",
            };

            ArrayList<String> temp=new ArrayList<String>();
            JSONArray[] resultJa=new JSONArray[5];
            try{
                //init resultJa
                for(int i=0;i<resultJa.length;i++)
                    resultJa[i]=new JSONArray();

                JSONArray ja=new JSONArray(result);

                for(int i = 0;i<ja.length();i++){
                    JSONObject jo=ja.getJSONObject(i);
                    for(int j=0;j<5;j++){
                        if(jo.get("tournamentName").toString().compareTo(leagueName[j])==0)
                            resultJa[j].put(jo);
                    } //end for-j
                } //end for-i

                for(int j=0;j<temp.size();j++)
                    System.out.println(temp.get(j));

                for(int j=0;j<5;j++) {
                    System.out.println(resultJa[j].length());
                    String key="p"+j;
                    StaticPref.savePref_String(getApplicationContext(),
                            TAG, resultJa[j].toString(), key);
                    StaticMethod.jArr[j]=resultJa[j];
                }


            }catch (Exception e){
                e.printStackTrace();
            }
            Log.e(TAG, "Player Thread complete");
            LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(3));
        }
    }//END query_score class

    //0-2. (다운안받아도 될 경우에, pref에서 선수데이터 로드)
    class Thread_player_prefLoad extends AsyncTask< Void, Void, Void> {
        Context c;
        public Thread_player_prefLoad(Context c){
            this.c=c;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                Log.e("LoadingActivity", "shared를 불러옵니다.");
                for(int i=0;i<5;i++){
                    String key="p"+i;
                    String temp=StaticPref.loadPref_String(c, TAG, key);
                    JSONArray ja=new JSONArray(temp.toString());
                    StaticMethod.jArr[i]=ja;
                    Log.e("데이터확인", ja.length() + ja.toString());
                }
                LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(3));
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

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

    private String readIt(InputStream stream) throws IOException {

        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        for(String line = reader.readLine(); line != null; line = reader.readLine())
            builder.append(line);
        reader.close();
        return builder.toString();
    }

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

    private void showNetworkDialog(){
        networkCheckDialog=new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("네트워크 연결 오류").setMessage("네트워크 연결 상태 확인 후 다시 시도해 주십시요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        finish();
                    }
                }).show();
    }
    private void hideNdialog(){
        if (networkCheckDialog != null) {
            networkCheckDialog.dismiss();
            networkCheckDialog = null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(networkCheckDialog!=null)
            hideNdialog();
    }

}



