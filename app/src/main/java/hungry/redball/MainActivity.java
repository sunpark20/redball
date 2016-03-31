package hungry.redball;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeIntents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import hungry.redball.aStatic.Sfile;
import hungry.redball.aStatic.StaticMethod;
import hungry.redball.alram.AlarmActivity;
import hungry.redball.alram.PrefActivity;
import hungry.redball.alram.RepeatReceiver;
import hungry.redball.fixtures.FicturesActivity;
import hungry.redball.matchRepo.ReportActivity;
import hungry.redball.player.PlayerActivity;
import hungry.redball.team.TeamActivity;
import hungry.redball.util.RedballProgressDialog;

public class MainActivity extends AppCompatActivity {
   // static public BasicDBObject newContacts = new BasicDBObject();

    private final String TAG="MainActivity";
    //networkCheck dialog
    private AlertDialog networkCheckDialog;
    //redball dialog
    private RedballProgressDialog redballDialog;

    private boolean temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long e = System.currentTimeMillis();
        setContentView(R.layout.activity_main);
        readJsonFile();
        serviceUp();
        if(!temp){
            temp=true;
            StaticMethod.endTime();
        }


    }
    private void youtubeStart(){
        //유투브 앱넣는 부분
        final String appPackageName = "com.google.android.youtube"; // getPackageName() from Context or Activity  object
        String USER_ID = "Saturday, Mar 19 2016 Crystal Palace Leicester";

        String version = YouTubeIntents.getInstalledYouTubeVersionName(this);
        if (version != null) {
            Intent intent = YouTubeIntents.createSearchIntent(this, USER_ID);
            startActivity(intent);
        } else {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
      /*  2번째 효짜꺼에서 따온것.
                view15.setOnClickListener(new OnClickListener() {
            String search= teamList.getTeam(team1)+" vs "+teamList.getTeam(team2)+" "+score1+":"+score2+" "+y;
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEARCH);
                intent.setPackage("com.google.android.youtube");
                intent.putExtra("query", search);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });*/
    }

    private void readJsonFile(){
        Log.e("static", "readJsonFile");
        HashMap<String, JSONArray> map=new HashMap<String, JSONArray>();
        JSONObject parsedJo = new JSONObject();
        try {
            JSONArray contacts=new JSONArray(Sfile.readFile(this, Sfile.json_fixturesName));
            JSONObject dateObj;

            for(int i=0;i<contacts.length();i++){
                JSONObject userObj=contacts.getJSONObject(i);
                dateObj=userObj.getJSONObject("date");

                String newKey = userObj.get("league").toString()+"_"+dateObj.getString("year")+dateObj.getString("month");
                if(map.containsKey(newKey)){
                    JSONArray classifiData = map.get(newKey);
                    classifiData.put(userObj);
                    map.put(newKey, classifiData);
                }else{
                    JSONArray newClassifiData=new JSONArray();
                    newClassifiData.put(userObj);
                    map.put(newKey,newClassifiData);
                }
            }
            for(String key:map.keySet()){
                parsedJo.put(key, map.get(key));
            }

            Sfile.saveFile(this, Sfile.json_parsed_fixturesName, parsedJo.toString());
        } catch(Exception e) {
            e.printStackTrace();
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

    public void ButtonTeamOnClicked(View view) {
        if(!StaticMethod.isNetworkConnected(getBaseContext()))
            showNetworkDialog();
        Intent intent = new Intent(this, TeamActivity.class);
        startActivity(intent);
    }
    public void ButtonNewsOnClicked(View view) {
        Toast.makeText(this,"뉴스기사 준비중입니다.", Toast.LENGTH_SHORT).show();
    }

    public void ButtonPlayerOnClicked(View view) {
        if(!StaticMethod.isNetworkConnected(getBaseContext()))
            showNetworkDialog();
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }
    public void  ficturePlayerOnClicked(View view) {
        if(!StaticMethod.isNetworkConnected(getBaseContext()))
            showNetworkDialog();
        FicturesActivity.isJong=false;
        Intent intent = new Intent(this, FicturesActivity.class);
        startActivity(intent);
    }
    public void  ButtonPrefOnClicked(View view) {
        Intent intent = new Intent(this, PrefActivity.class);
        startActivity(intent);
    }
    public void ButtonAlarmOnClicked(View view) {
        Intent intent = new Intent(this, AlarmActivity.class);
        startActivity(intent);
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
    private void showRedballDialog(){
        //기다림바 설정
        redballDialog = new RedballProgressDialog(this);
        redballDialog .getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //기다림바 스타트
        redballDialog.show();
        //화면 터치시 꺼짐 방지
        redballDialog.setCancelable(false);
    }
    private void hideRedballDialog(){
        if (redballDialog != null) {
            redballDialog.dismiss();
            redballDialog = null;
        }
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
        if (redballDialog != null)
            hideRedballDialog();
    }
    int exitCount=0;
    @Override
    public void onBackPressed() {
        exitCount++;
        if(exitCount==1) {
            String message = "'뒤로'버튼을 한번 더 누르면 종료됩니다.";
            StaticMethod.fToast(this, message);
        }
        if(exitCount==2)
            MainActivity.this.finish();

        Runnable task = new Runnable() {
            public void run() {
                SystemClock.sleep(2000);
                exitCount=0;
                Log.e("백버튼 스택이 감소", exitCount+"");
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
}

