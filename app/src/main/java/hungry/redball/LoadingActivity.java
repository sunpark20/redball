package hungry.redball;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import hungry.redball.aStatic.StaticFile;
import hungry.redball.aStatic.StaticMethod;
import hungry.redball.aStatic.StaticPref;
import hungry.redball.alram.PrefActivity;
import hungry.redball.team.url.Url_team_thread;
import hungry.redball.util.QueryBuilder_loading;

public class LoadingActivity extends AppCompatActivity {

    //date 합칠때 쓰는거요.
    HashMap<Integer,JSONObject> jMap=new HashMap<Integer,JSONObject>();

    private final String TAG="LoadingActivity";

    //networkCheck dialog
    private AlertDialog networkCheckDialog;

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
    //경기일정부터 다운받자.
    CountDownLatch latch2 = new CountDownLatch(1);

    //player 다운결정하는 변수
    boolean alreadyLoad=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        getSupportActionBar().hide();

        final ImageView animImageView = (ImageView) findViewById(R.id.ivAnimation);
        animImageView.setBackgroundResource(R.drawable.anim);
        animImageView.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable frameAnimation =
                        (AnimationDrawable) animImageView.getBackground();
                frameAnimation.start();
            }
        });

        tv =(TextView)findViewById(R.id.textView);
        progBar= (ProgressBar)findViewById(R.id.progBar1);

        StaticMethod.startTime();
        if(!StaticMethod.isNetworkConnected(getBaseContext())){
            showNetworkDialog();
            return; //return을 안하니까 밑에 계쏙 도네요.
        }

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

            //1.팀다운  (need capsulation 1.team- 2.player)
            for(int i=0;i<5;i++)
                new Url_team_thread().execute(i);

            //2.선수다운
            long tempM=StaticPref.loadPref_long(getApplicationContext());

            //2-1.먼저 선수정보를 로딩하고
            //2-2.다운받는 아이는 백그라운드에서 돌아간다음 완료되면 리스트뷰를 notify 해준다.

            if(tempM==0) { //처음 시작한다는 뜻(Thread_player 완료에서 millisecond를 저장함)
                new Thread_player(this).execute();
            }else{
                boolean isDown=false;
                new Thread_player_prefLoad(this).execute();
                //중요함. handler에 한번만 쏴주는 변수
                alreadyLoad=true;
                //savedCal을 11시로 설정해놓고  now랑 비교하는 과정.
                //( 11시 전후로  오늘과 내일설정하는 부분)
                Calendar savedCal=new GregorianCalendar();
                savedCal.setTimeInMillis(tempM);
                Calendar nowCal=new GregorianCalendar();

                int hour=savedCal.get(Calendar.HOUR_OF_DAY);
                if(hour==0)
                    hour=24;

                if(hour>23)
                    savedCal.add(Calendar.DAY_OF_MONTH, 1);

                savedCal.set(Calendar.HOUR_OF_DAY, 23);
                savedCal.set(Calendar.MINUTE, 0);
                savedCal.set(Calendar.SECOND, 0);

                if(savedCal.compareTo(nowCal)==-1)
                    isDown=true;
                System.out.println(savedCal.getTime() +" < "+ nowCal.getTime()+
                        "\n이면 다운로드 합니다.");
                if(isDown)
                    new Thread_player(this).execute();
            }
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
    private HashMap<Integer,JSONObject> jsonToMap(String result){
        HashMap<Integer,JSONObject> map = new HashMap<Integer,JSONObject>();
        try {
            JSONArray ja=new JSONArray(result);
            for(int i=0;i<ja.length();i++){
                JSONObject jo=ja.getJSONObject(i);
                map.put((int)jo.get("code"), jo);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return map;
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

    private void FixturesUpdate(){

        String loadMatchInfo= StaticFile.readFile(this, StaticFile.json_fixturesName);
        //최초 로딩시, 그 다음 접속 구분해서 fixtures틀 만들기
        if(loadMatchInfo=="") {
            Log.e(TAG, "Fixtures Thread1-Total start download from mongoDB(2016년만받음)");
            new Thread_query_total(this).execute();
        }else {
            Log.e(TAG, "Fixtures Thread1-Total start load from sharedPreference");
            try{
                makeJmap(loadMatchInfo);
                LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(1));
                latch1.countDown();
            }catch (Exception e){
                Log.e(TAG, "FixturesUpdate 오류발생!!! ");
                e.printStackTrace();
            }
        }
        Log.e(TAG, "Fixtures Thread2-Date start (2016년을 전부 업데이트하기 떄문에 최적화필요)");
        new Thread_query_date(this).execute();
    }

    private void makeJmap(String loadMatchInfo) throws  Exception{
        JSONArray ja=new JSONArray(loadMatchInfo);
        for(int i=0;i<ja.length();i++){
            JSONObject jo=ja.getJSONObject(i);
            jMap.put((int)jo.get("code"), jo);
        }
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
            Log.e(TAG, "Thread_query_total URL: "+urlString);

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
            Log.e(TAG, "Fixtures Thread-Total complete");
            LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(1));
            jMap=jsonToMap(result);

            System.out.println("map사이즈는?" + jMap.size());
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
                int iLength=result_ja.length();

                for(int i=0;i<iLength;i++){
                    JSONObject result_jo=result_ja.getJSONObject(i);
                    //date 한국시간으로 바꾼 후 넣습니다.
                    //한국시간 바꾸기
                    //1.result들을 cal로 바꿈
                    Calendar cal = StaticMethod.setJsonCal(result_jo);
                    Calendar localTime = new GregorianCalendar();
                    localTime.setTimeInMillis(cal.getTimeInMillis());

                    String h=StaticMethod.iTos(localTime.get(Calendar.HOUR_OF_DAY));
                    String m=StaticMethod.iTos(localTime.get(Calendar.MINUTE));
                    if(h.length()==1)
                        h="0"+h;
                    if(m.length()==1)
                        m="0"+m;
                    String time=h + ":" + m;

                    //date change
                    //"date": {"year": "2015", "day": "9", "month": "08"}
                    String year=StaticMethod.iTos(localTime.get(Calendar.YEAR));
//                            System.out.println(year);
                    JSONObject tDate=new JSONObject();
                    tDate.put("year", year);
                    String month=StaticMethod.iTos(localTime.get(Calendar.MONTH)+1);
                    if(month.length()==1)
                        month="0"+month;
//                            System.out.println(month);
                    tDate.put("month", month);
                    String day=StaticMethod.iTos(localTime.get(Calendar.DAY_OF_MONTH));
//                            System.out.println(day);
                    tDate.put("day", day);
//                            System.out.println(jo.toString());

                    //이제 다 잡아넣습니다.
                    int result_code=StaticMethod.sToi(result_jo.get("code").toString());
                    String result_score=result_jo.get("score").toString();

                    if(jMap.get(result_code)!=null){
                        jMap.get(result_code).put("score", result_score );
                        jMap.get(result_code).put("time", time);
                        jMap.get(result_code).put("date", tDate);
                    }else{
                        System.out.println("이게 와 널이냐?");
                        System.out.println(result_code+ result_jo.toString());
                    }

                } //end for i

            }catch (Exception e){
                e.printStackTrace();
            }

            Log.e(TAG, "Fixtures Thread-Date complete");

            JSONArray ja=new JSONArray();
            for(Integer i: jMap.keySet())
                ja.put(jMap.get(i));

            StaticFile.saveFile(context, StaticFile.json_fixturesName, ja.toString());
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

            JSONArray[] resultJa=new JSONArray[5];
            try{
                //init resultJa
                for(int i=0;i<resultJa.length;i++)
                    resultJa[i]=new JSONArray();

                JSONArray ja=new JSONArray(result);

                //서버가 고장났는지 체크 고장났다면 몽고디비에 0개 들어있어서 0개짜리를 가져 올것이다.
                if(ja.length()==0) {
                    Log.e(TAG, "서버고장");
                    if(!alreadyLoad)
                        LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(3));
                    return;
                }

                for(int i = 0;i<ja.length();i++){
                    JSONObject jo=ja.getJSONObject(i);
                    for(int j=0;j<5;j++){
                        if(jo.get("tournamentName").toString().compareTo(leagueName[j])==0)
                            resultJa[j].put(jo);
                    } //end for-j
                } //end for-i

                for(int j=0;j<5;j++) {
                    System.out.println(resultJa[j].length());
                    String key="p"+j;
                    StaticFile.saveFile(context, key, resultJa[j].toString());
                    StaticMethod.setJ(resultJa[j], j);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            Log.e(TAG, "Player Thread complete");
            Calendar c=new GregorianCalendar();

            StaticPref.savePref_long(getApplicationContext(), c.getTimeInMillis());
            if(!alreadyLoad)
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
                    String temp= StaticFile.readFile(c, key);
                    JSONArray ja=new JSONArray(temp.toString());
                    StaticMethod.setJ(ja, i);
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
        }catch (Exception e){
            e.printStackTrace();
        }
        finally
        {
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
