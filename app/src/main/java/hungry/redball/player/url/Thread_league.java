package hungry.redball.player.url;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hungry.redball.LoadingActivity;
import hungry.redball.aStatic.StaticPref;

//AsyncTask<Param, Progress, Result>
public  class Thread_league extends AsyncTask<String, Void,  String> {
    private final String TAG="Thread_league";
    Url_player_sub ud;
    String code;
    String bResult;
    private JSONObject PerCategori=new JSONObject();
    private int num;

    Context c;
    public Thread_league(Context c, int num){
        this.c=c;
        this.num = num;
    }


    @Override
    protected String doInBackground(String... params){
        // TODO Auto-generated method stub
        //key값을 못받아오면 쓰레드를 종료시켜버립니다.
        try{
            Url_getHeader u=new Url_getHeader(Url_player_sub.Referer[num]);
            Url_player_sub.last_model_key[num]=u.getUrlContent();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Thread_league", "종료합니다.");
            LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(999));
            cancel(true);
        }
        if(isCancelled()) {
            Log.e("Thread_league", "종료합니다2.");
            return null;
        }

        ud=new Url_player_sub(num, code, params[0]);
        try{
            bResult = nomalized(ud.readStat());
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Thread_league", "오류" + "league: " + num + "//");
        }
        return bResult;

    }

    @Override
    protected void onPostExecute( String result) {
        if(isCancelled()) {
            Log.e("Thread_league", "종료합니다3. (cancle이 요까진 안오네)");
            return;
        }
        try{

            PerCategori=new JSONObject(result);
            Log.e("result", PerCategori+"");
            Calendar temp = Calendar.getInstance();
            String now = fomatedDate(temp.getTime());
            //최초접속에서 에러나도 날짜는 기록이 됨
            StaticPref.savePref_String(c, TAG, now, StaticPref.PLAYER_DATE);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String fomatedDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        return format.format(date).toString();
    }

    private String nomalized(String a){
        String normalizedString = Normalizer.normalize(a, Normalizer.Form.NFKD);
        String ascii = normalizedString.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
        return ascii;
    }

}
