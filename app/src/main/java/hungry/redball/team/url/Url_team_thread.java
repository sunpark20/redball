package hungry.redball.team.url;

import android.os.AsyncTask;

import org.json.JSONArray;

import hungry.redball.LoadingActivity;
import hungry.redball.aStatic.StaticMethod;

/**
 * Created by soy on 2015-11-16.
 */
public class Url_team_thread extends AsyncTask<Integer, Void, JSONArray> {
    int num;
    @Override
    protected JSONArray doInBackground(Integer... params) {
        // TODO Auto-generated method stub
        num=params[0];
        Url_team ud = new Url_team(params[0]);
        try {
            return ud.getUrlContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    @Override
    protected void onPostExecute(JSONArray result) {
        StaticMethod.jArr_team[num]=result;
        if(num==4)
            LoadingActivity.mHandler.sendMessage(LoadingActivity.mHandler.obtainMessage(11));
    }
}