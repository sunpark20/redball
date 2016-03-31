package hungry.redball.alram;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hungry.redball.R;
import hungry.redball.aStatic.StaticPref;
import hungry.redball.alram.model.Pref;
import hungry.redball.alram.model.PrefInfo;
import hungry.redball.mongo.GetContactsAsyncTask;
import hungry.redball.mongo.QueryBuilder;
import hungry.redball.team.util.FlagHashMap;

/**
 * Created by soy on 2016-01-17.
 */
public class PrefActivity extends AppCompatActivity {
    public static final String TAG = "PrefActivity";
    private ArrayList<Pref> rows=new ArrayList<Pref>();
    private CustomAdapter ca;
    private ListView listView;
    Map<Integer, PrefInfo> prefInfo; //저장된 즐찾팀 정보 불러오기
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);
        prefInfo= StaticPref.loadPref_prefInfo(this);
        setStuff();

        HashMap<String,String> map = new HashMap();
        map.put("code", "959903");
        GetContactsAsyncTask task = new GetContactsAsyncTask(this, QueryBuilder.QueryKinde.matchResultQ,map);
        String matchResult;
        try {
            matchResult = task.execute().get();
            JSONArray contacts=new JSONArray(matchResult);
            JSONObject jObj=(JSONObject)contacts.get(0);
//            Log.e("hTeamRecord",matchResult);
//            Log.e("score",jObj.getString("score"));
            Log.e("goalEve",jObj.getString("goalEve"));
            Log.e("aTeamRecord",jObj.getString("aTeamRecord"));
            Log.e("hTeamRecord",jObj.getString("hTeamRecord"));
            Log.e("hPlayerRecord",jObj.getString("hPlayerRecord"));
            Log.e("aPlayerRecord",jObj.getString("aPlayerRecord"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStuff(){
        loadTeam(); //팀정보 불러오기
        ca=new CustomAdapter(this);
        listView=(ListView)findViewById(R.id.listView);
        listView.setAdapter(ca);
    }

    private void loadTeam(){
        //깃발 정보 불러오기
        FlagHashMap flag=new FlagHashMap();
        flag.makeHashMap();

        JSONArray ja=null;
//        private final String JSON_MATCH="JSON_MATCH";
        String infoTemp=StaticPref.loadPref_String(this, TAG, "JSON_MATCH");

        try{
            ja=new JSONArray(infoTemp);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(int key: prefInfo.keySet()){
            Pref pref=new Pref();
            pref.sethTeam(prefInfo.get(key).gethTeam());
            pref.setaTeam(prefInfo.get(key).getaTeam());

            int flagId=0, flagId2=0;
            try { // 깃발정보 다르면 이미지x
                flagId = flag.list.get(0).get(pref.gethTeam().toLowerCase());
                flagId2 = flag.list.get(0).get(pref.getaTeam().toLowerCase());
            }catch (NullPointerException e){
                Log.e(TAG, "깃발 정보가 없어요!");
            }
            pref.sethFlag(flagId);
            pref.setaFlag(flagId2);

            rows.add(pref);

//            //brute force
//            try{
//                for(int j=0;j<ja.length(); j++){
//                    if(al.get(i).getCode()==(int)ja.getJSONObject(j).get("code")){
//                        String s=ja.getJSONObject(j).get("score").toString();
//                        String league=ja.getJSONObject(j).get("league").toString();
//                        String date=ja.getJSONObject(j).get("date").toString();
//                        String time=ja.getJSONObject(j).get("time").toString();
//                        league+=" "+date+" "+time;
//                        pref.setLeague(league);
//
//                        if(s.contains("-")){
//                            pref.sethScore(s.split("-")[0]);
//                            pref.setaScore(s.split("-")[1].trim());
//                        }else if(s.contains(":")){
//                            pref.sethScore(s.split(":")[0]);
//                            pref.setaScore(s.split(":")[1].trim());
//                        }
//                        break;
//                    }
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }
    }

    class CustomAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater inflater=null;


        public CustomAdapter(Context c) {
            // TODO Auto-generated constructor stub
            context= c;
            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return rows.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {
            //row1
            TextView league;
            //row2 home
            ImageView hFlag;
            TextView hTeam, hScore;
            //row3 home
            ImageView aFlag;
            TextView aTeam, aScore;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder=null;

            if (convertView == null) {
                holder=new Holder();
                convertView = inflater.inflate(R.layout.activity_pref_row, parent, false);
                holder.league=(TextView) convertView.findViewById(R.id.league);

                holder.hFlag=(ImageView) convertView.findViewById(R.id.hFlag);
                holder.hTeam=(TextView) convertView.findViewById(R.id.hTeam);
                holder.hScore=(TextView) convertView.findViewById(R.id.hScore);

                holder.aFlag=(ImageView) convertView.findViewById(R.id.aFlag);
                holder.aTeam=(TextView) convertView.findViewById(R.id.aTeam);
                holder.aScore=(TextView) convertView.findViewById(R.id.aScore);
                convertView.setTag(holder);
            }else {
                holder = (Holder) convertView.getTag();
            }

            //resize (flag)
            Resources res=getBaseContext().getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            int id=rows.get(position).gethFlag();
            Bitmap src=BitmapFactory.decodeResource(res, id, options);
            holder.hFlag.setImageBitmap(src);

            int id2=rows.get(position).getaFlag();
            Bitmap src2=BitmapFactory.decodeResource(res, id2, options);
            holder.aFlag.setImageBitmap(src2);

            holder.league.setText(rows.get(position).getLeague());

            holder.hTeam.setText(rows.get(position).gethTeam());
            holder.hScore.setText(rows.get(position).gethScore());

            holder.aTeam.setText(rows.get(position).getaTeam());
            holder.aScore.setText(rows.get(position).getaScore());

            return convertView;
        }
    }
}
