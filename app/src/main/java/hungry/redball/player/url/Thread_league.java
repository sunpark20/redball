package hungry.redball.player.url;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import hungry.redball.LoadingActivity;
import hungry.redball.aStatic.StaticMethod;
import hungry.redball.aStatic.StaticPref;

//AsyncTask<Param, Progress, Result>
public  class Thread_league extends AsyncTask<String, Void,  String[]> {
    private final String TAG="Thread_league";
    Url_player_sub ud;
    String[] sub={"defensive",
                    "offensive",
                    "passing",
                    "all"    };
    String[] bResult=new String[4];
    private JSONObject[] PerCategori=new JSONObject[4];
    private JSONArray[] tjArr=new JSONArray[4];
    private int num;

    Context c;
    public Thread_league(Context c, int num){
        this.c=c;
        this.num = num;
    }


    @Override
    protected String[] doInBackground(String... params){
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

        for(int i=0;i<4;i++){
            ud=new Url_player_sub(num, sub[i], params[0]);
            try{
                bResult[i] = nomalized(ud.readStat());
            }catch (Exception e){
                e.printStackTrace();
                Log.e("Thread_league", "오류" + "league: " + num + "//" + i);
            }
        }
        return bResult;

    }

    @Override
    protected void onPostExecute( String[] result) {
        if(isCancelled()) {
            Log.e("Thread_league", "종료합니다3. (cancle이 요까진 안오네)");
            return;
        }
        try{
           for(int i=0;i<4;i++){
               PerCategori[i]=new JSONObject(result[i]);
               tjArr[i] = PerCategori[i].getJSONArray("playerTableStats");
           }
            //json 결합과정 최적화필요합니다.
            JSONArray tempArray1 = new JSONArray();
            for (int j = 0; j < tjArr[0].length(); j++) {
                JSONObject mergedJsonObject = concatJson(
                        tjArr[0].getJSONObject(j), tjArr[1].getJSONObject(j));
                tempArray1.put(mergedJsonObject);
            }
            JSONArray tempArray2 = new JSONArray();
            for (int j = 0; j < tjArr[0].length(); j++) {
                JSONObject mergedJsonObject = concatJson(
                        tempArray1.getJSONObject(j), tjArr[2].getJSONObject(j));
                tempArray2.put(mergedJsonObject);
            }
            JSONArray tempArray3 = new JSONArray();
            for (int j = 0; j < tjArr[0].length(); j++) {
                JSONObject mergedJsonObject = concatJson(
                        tempArray2.getJSONObject(j), tjArr[3].getJSONObject(j));
                tempArray3.put(mergedJsonObject);
            }
            StaticMethod.jArr[num]=tempArray3;
            Log.e("Thread_league", "StaticMethod.urlFinish" +num);
            String key="p"+num;
            StaticPref.savePref_String(c, TAG, tempArray3.toString(), key);
            LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(num));


            //최초접속 체크하기
            if(num==4){
                Calendar temp = Calendar.getInstance();
                String now = fomatedDate(temp.getTime());
                //최초접속에서 에러나도 날짜는 기록이 됨
                StaticPref.savePref_String(c, TAG, now, StaticPref.PLAYER_DATE);
            }

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
    //json 결합하기 2개만.
    private static JSONObject concatJson(JSONObject... jObject)
            throws JSONException {
        //I assume that your two JSONObjects are o1 and o2
        JSONObject mergedObj = new JSONObject();

        Iterator i1 = jObject[0].keys();
        Iterator i2 = jObject[1].keys();
        String tmp_key;
        while(i1.hasNext()) {
            tmp_key = (String) i1.next();
            mergedObj.put(tmp_key, jObject[0].get(tmp_key));
        }
        while(i2.hasNext()) {
            tmp_key = (String) i2.next();
            mergedObj.put(tmp_key, jObject[1].get(tmp_key));
        }
        return mergedObj;
    }
}
