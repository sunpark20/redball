package hungry.redball;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.mongodb.BasicDBObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import hungry.redball.aStatic.StaticMethod;
import hungry.redball.aStatic.StaticPref;
import hungry.redball.alram.AlarmActivity;
import hungry.redball.alram.AlarmReceiver;
import hungry.redball.alram.PrefActivity;
import hungry.redball.alram.RepeatReceiver;
import hungry.redball.fixtures.FicturesActivity;
import hungry.redball.player.PlayerActivity;
import hungry.redball.team.TeamActivity;
import hungry.redball.util.RedballProgressDialog;

public class MainActivity extends AppCompatActivity {
    private HashMap<String,JSONArray> map = new HashMap<String,JSONArray>();
    static public BasicDBObject newContacts = new BasicDBObject();
    private final String TAG="MainActivity";
    //networkCheck dialog
    private AlertDialog networkCheckDialog;
    //redball dialog
    private RedballProgressDialog redballDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readJsonFile();
    }

    private void readJsonFile(){
        Log.e("static", "readJsonFile");
        try {
            String loadMatchInfo= StaticPref.loadPref_String(this, TAG, LoadingActivity.JSON_MATCH);
            JSONArray contacts=new JSONArray(loadMatchInfo);
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
            newContacts.putAll(map);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void AlarmTestOnClicked(View view) {
        AlarmReceiver a=new AlarmReceiver();
        a.setAlarm(this, 9999, 1000 * 10, 0, "없음");
    }
    public void AlarmcTestOnClicked(View view) {
        AlarmReceiver a=new AlarmReceiver();
        a.cancelAlarm(this, 9999);
    }
    public void downTestOnClicked(View view) {
        LoadingActivity l=new LoadingActivity();
        l.download();
    }
    public void ButtonTestOnClicked(View view) {

        Intent intent = new Intent(this, RepeatReceiver.class);

        boolean alarmUp = (PendingIntent.getBroadcast(this, 0,
                intent,
                PendingIntent.FLAG_NO_CREATE) != null);

//        if(alarmUp){
//            Log.e(TAG, "알람이가 벌써 동작하고있잔아...");
//        }else{
            Log.e(TAG, "메인에서 테스트중..");
            Log.e(TAG, "반복 스케줄 동작합니다.");
            RepeatReceiver repeatAlarm = new RepeatReceiver();
            //Context context, int RequestCode //무조건 0 주면 된다.
            repeatAlarm.setAlarm(this, 0);
//        }
    }

    public void ButtonTeamOnClicked(View view) {
        if(!StaticMethod.isNetworkConnected(getBaseContext()))
            showNetworkDialog();
        Intent intent = new Intent(this, TeamActivity.class);
        startActivity(intent);
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

