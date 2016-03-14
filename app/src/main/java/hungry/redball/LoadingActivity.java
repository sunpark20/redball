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

import com.mongodb.BasicDBObject;

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
import java.util.HashMap;

import hungry.redball.aStatic.StaticMethod;
import hungry.redball.aStatic.StaticPref;
import hungry.redball.alram.PrefActivity;
import hungry.redball.alram.RepeatReceiver;
import hungry.redball.player.url.Thread_league;
import hungry.redball.player.url.Url_player_sub;
import hungry.redball.team.url.Url_team_thread;
import hungry.redball.util.QueryBuilder_loading;

public class LoadingActivity extends AppCompatActivity {
    //경기일정
    private HashMap<String,JSONArray> map = new HashMap<String,JSONArray>();
    static public BasicDBObject newContacts = new BasicDBObject();

    private final String TAG="LoadingActivity";

    //networkCheck dialog
    private AlertDialog networkCheckDialog;

    private TextView tv;
    public static MyHandler mHandler;
    int count=0;
    public static final String JSON_MATCH="JSON_MATCH";
    private boolean enterFromNotify;

    private final int PROGRESS_NUM=8;
    private final int PROGRESS_INT=(100/PROGRESS_NUM) + 1;
    //프로그래스바
    private int value = 0;
    private ProgressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        tv =(TextView)findViewById(R.id.textView);
        progBar= (ProgressBar)findViewById(R.id.progBar1);

        mHandler= new MyHandler(this);
        progressWork();

        // ssl exception 실험
        //new testThred2().execute();

        //PHASE1:SCORE  이거할때 date도 같이 해버리자
        try{
            scoreUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }

        //노티피에서 왔는지 확인.
        try{
            Intent nIntent = getIntent();
            if (nIntent.getAction().equals("ACTION_NOTIFICATION")){
                Log.e(TAG,"노티피에서 오셨습니다. 스코어 다운이 완료되면 pref로 접속합니다.");
                tv.append("관심팀 점수를 가져옵니다.");
                enterFromNotify=true;
                return;
            }
        }catch (Exception e){
            Log.e(TAG,"걍 넘겨");
        }

        if(!enterFromNotify) {
            download();
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
    }
    //end of onCreate

    private void progressWork(){
        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (value < 100) {
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        int limit=(count)*PROGRESS_INT;
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
        count++;
        Log.e("LoadingActivity", "count: " + count + " " + "msg: " + msg.what);
        switch (msg.what) {
            case 999:
                tv.append("오류 발생. 네트워크환경체크 후 재접속 해주세요.\n");
                count--;
                break;
            case 11:
                tv.append(msg.what+"team  complete"+count*PROGRESS_INT+"%\n");
                break;
            case 12:
                if(enterFromNotify) //스코어 받으면 진입하려고 여기 있는거네.
                    startPrefActivity();
                tv.append(msg.what+"score   complete"+count*PROGRESS_INT+"%\n");
                break;
            case 13:
                tv.append(msg.what+"date   complete"+count*PROGRESS_INT+"%\n");
                break;
            default:
                tv.append(msg.what+"player  complete"+count*PROGRESS_INT+"%\n");
                break;
        }
        //선수 로딩 막음(주석제거)
     /*   if(count>=PROGRESS_NUM)
            startActivity();*/
        //선수 로딩 막음(주석)
        if(count>=3)
            startActivity();

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

    public void download(){
        if(!StaticMethod.isNetworkConnected(getBaseContext()))
            showNetworkDialog();

        //팀 다운
        for(int i=0;i<5;i++)
                new Url_team_thread().execute(i);

        String date=StaticPref.loadPref_String(this, TAG, StaticPref.PLAYER_DATE);
        Log.e(TAG, date.toString());

        /*
        //선수 로딩 막음(주석제거)
        if(date.compareTo("defValue")==0){
            tv.append("최초 접속시 선수정보를 다운(약 1분 소요)\n");
            downPlayer();
        }else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage("선수 정보 날짜\n"+date+"\n\n업데이트 하시겠습니까\n   약1.5mb소모")
                    .setPositiveButton("네", dialogClickListener)
                    .setNegativeButton("아니요", dialogClickListener).show();
            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextSize(25);
        }*/
    }

    private void scoreUpdate()throws Exception{
        String loadMatchInfo=StaticPref.loadPref_String(this,TAG,JSON_MATCH);
        JSONArray ja=null;
        int count=0;

        if(loadMatchInfo.compareTo("defValue")==0){
            Log.e(TAG,"PHASE1:SCORE pref-JSON_MATCH 없음 asset에서 로드");
            String temp=StaticMethod.loadJSONFromAsset("matchinfo.json", this);
            ja=new JSONArray(temp);
        }else{
            Log.e(TAG,"PHASE1:SCORE pref-JSON_MATCH 업데이트");
            Log.e(TAG, loadMatchInfo.toString());
            ja=new JSONArray(loadMatchInfo);
        }

        Calendar nCal=Calendar.getInstance();

        ArrayList<Integer> codeArray=new ArrayList<Integer>();

        for(int i=0;i<ja.length();i++){
            JSONObject jo=ja.getJSONObject(i);
            String score=jo.get("score").toString();

            Calendar jCal=StaticMethod.setJsonCal(jo);

            if(score.compareTo("vs")==0 //조건1. score값이 비어 있을 때
                    && jCal.compareTo(nCal)==-1){ //조건2. 현재시간과 json시간을 비교
                int code=(int)jo.get("code");
                codeArray.add(code);
                count++;
            }
        }
        //먼저 date를 채워줌. 순서중요. 이거하고 나서 score해야 한번에 최신으로 됨.
        Log.e(TAG,"2016 date를 전부 업데이트 합니다(최적화필요)");
        new Thread_query_date(this, ja).execute();

        Log.e(TAG,"스코어가 "+ count + "만큼 비어있습니다.");
        if(count>0) {
            QueryBuilder_loading.codeArray = codeArray;
            new Thread_query_score(this, ja).execute();
        }else{
            LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(12));
        }

    }

    //1. start query_score class
    class Thread_query_score extends AsyncTask<String, Void, String> {
        Context context;
        String result ="";
        JSONArray ja;

        public Thread_query_score(Context context, JSONArray ja){
            this.ja=ja;
            this.context=context;
        }

        @Override
        protected String doInBackground(String... arg0) {

            QueryBuilder_loading qb = new QueryBuilder_loading();
            String urlString=qb.buildScoreUrl();
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
            //result는 mongo에서 받아온것
            //그냥 jo,ja는 저장할것.
            Log.e(TAG, result);
            try{
                JSONArray result_ja=new JSONArray(result);

                for(int i=0;i<result_ja.length();i++){
                    JSONObject result_jo=result_ja.getJSONObject(i);
                    String result_code=result_jo.get("code").toString();
                    for(int j=0;j<ja.length();j++) {
                        JSONObject jo=ja.getJSONObject(j);
                        String code=jo.get("code").toString();

                        if(code.compareTo(result_code)==0){
                            String score=result_jo.get("score").toString();

                            //추측. 여기서 에러가 나는듯 하다.. score가 한번씩 비어 있나우?
                            if(score.compareTo("")!=0)
                                ja.getJSONObject(j).put("score", score);
                            else
                                System.out.println("이건 비어 있으면 안되는디우??"+code+score);
                            break;
                        }
                    }
                }
                StaticPref.savePref_String(context, TAG, ja.toString(), JSON_MATCH);
            }catch (Exception e){
                e.printStackTrace();
            }
            LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(12));
        }
    }//END query_score class

    //2. start query_date class
    class Thread_query_date extends AsyncTask<String, Void, String> {
        Context context;
        String result ="";
        JSONArray ja;

        public Thread_query_date(Context context, JSONArray ja){
            this.ja=ja;
            this.context=context;
        }

        @Override
        protected String doInBackground(String... arg0) {

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
//            System.out.println("내가 보고 싶은 결과");
            try{
                JSONArray result_ja=new JSONArray(result);
                for(int i=0;i<result_ja.length();i++){
                    JSONObject result_jo=result_ja.getJSONObject(i);
                    String result_code=result_jo.get("code").toString();
//                    System.out.println(result_jo.toString());
                    for(int j=0;j<ja.length();j++) {
                        JSONObject jo=ja.getJSONObject(j);
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
                                ja.getJSONObject(j).put("time", time);
                            else
                                System.out.println("이건 비어 있으면 안되는디우??"+code+time);

                            //date넣기
                            if(date.toString().compareTo("")!=0)
                                ja.getJSONObject(j).put("date", date);
                            else
                                System.out.println("이건 비어 있으면 안되는디우??"+code+date);
                            break;
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            StaticPref.savePref_String(context, TAG, ja.toString(), JSON_MATCH);
            LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(13));
        }
    }//END query_date class

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

    private JSONArray RemoveJSONArray( JSONArray jarray,int pos) {
        JSONArray Njarray = new JSONArray();
        try {
            for (int i = 0; i < jarray.length(); i++) {
                if (i != pos)
                    Njarray.put(jarray.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Njarray;
    }

    private void downPlayer(){
        try{
            new Thread_league(this, 0).execute(Url_player_sub.PRE);
            new Thread_league(this, 1).execute(Url_player_sub.LALIGA);
            new Thread_league(this, 2).execute(Url_player_sub.BUNDES);
            new Thread_league(this, 3).execute(Url_player_sub.SERIE);
            new Thread_league(this, 4).execute(Url_player_sub.LIGUE1);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    downPlayer();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    new Thread_prefLoad(getApplicationContext()).execute();
                    break;
            }
        }
    };

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

//AsyncTask<Param, Progress, Result>
class Thread_prefLoad extends AsyncTask< Void, Void, Void> {
    private final String TAG="LoadingActivity";

    Context c;
    public Thread_prefLoad(Context c){
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
                LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

