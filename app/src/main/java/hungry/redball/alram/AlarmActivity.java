package hungry.redball.alram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import hungry.redball.R;
import hungry.redball.aStatic.Sfile;
import hungry.redball.aStatic.StaticMethod;
import hungry.redball.aStatic.StaticPref;
import hungry.redball.alram.adapter.GridViewAdapter;
import hungry.redball.alram.adapter.GridViewAdapter2;
import hungry.redball.alram.model.Grid;
import hungry.redball.alram.model.PrefInfo;
import hungry.redball.team.util.FlagHashMap;

/**
 * Created by soy on 2015-08-06.
 */
public class AlarmActivity extends AppCompatActivity {
    public static final String TAG = "AlarmActivity";
    private GridView gridView, gridView2, gridView3;

    //스트링 하나.
    private GridViewAdapter gridAdapter;
    private ArrayList<String> ligue;

    //깃발과 스트링
    private GridViewAdapter2 gridAdapter2, gridAdapter3;
    private ArrayList<Grid> gTeam, gSavedArray;

    //추가된 팀, 삭제된 팀 보기.
    private ArrayList<String> preData;

    //깃발 정보 불러오기
    FlagHashMap flag;

    //팀영어로 바꾸기
    JSONObject kToeJson, eTokJson;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        //팀영어로 바꾸기
        String kToe= StaticMethod.loadJSONFromAsset("teamKtoE.json", this);
        String eTok= StaticMethod.loadJSONFromAsset("teamEtoK.json", this);
        try{
            kToeJson=new JSONObject(kToe);
            eTokJson=new JSONObject(eTok);
        }catch (Exception e){
            e.printStackTrace();
        }

        //깃발 정보 불러오기
        flag=new FlagHashMap();
        flag.makeHashMap();

        //저장된 데이터 불러왔음
        try{
            preData= StaticPref.loadPref_prefTeam(this);
            getSavedArray(preData);
        }catch (Exception e){
            gSavedArray = new ArrayList<Grid>();
        }

        //리그 추가
        ligue = new ArrayList<String>();
        ligue.add("프리미어");
        ligue.add("프리메라");
        ligue.add("분데스");
        ligue.add("세리에 A");
        ligue.add("리그1");

        //grid of ligue
        gridView = (GridView) findViewById(R.id.grid1);
        gridAdapter = new GridViewAdapter(this, R.layout.activity_alarm_row, ligue);
        gridView.setAdapter(gridAdapter);

        //grid of team
        gTeam = new ArrayList<Grid>();
        gridView2 = (GridView) findViewById(R.id.grid2);
        gridAdapter2 = new GridViewAdapter2(this, R.layout.activity_alarm_row2, gTeam);
        gridView2.setAdapter(gridAdapter2);

        //grid of savedArray
        gridView3 = (GridView) findViewById(R.id.grid3);
        gridAdapter3 = new GridViewAdapter2(this, R.layout.activity_alarm_row2, gSavedArray);
        gridView3.setAdapter(gridAdapter3);

        //맨 끝 보게하기.
        gridView3.smoothScrollToPosition(gSavedArray.size());

        //START ligue listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getBaseContext(), ligue.get(position) + "선택", Toast.LENGTH_SHORT).show();
                try {
                    getTeam(position, gTeam);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                gridAdapter2.notifyDataSetChanged(); //team adapter refresh
            }
        });
        //END ligue listener

        //START team listener
        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                boolean isOk=true;

                for(int i=0;i<gSavedArray.size();i++){ //중복 체크.
                    String ts=gTeam.get(position).getTeamName();
                    if(gSavedArray.get(i).getTeamName().compareTo(ts)==0){
                        StaticMethod.fToast(getBaseContext(), ts + "는 중복된 팀입니다.");
                        isOk=false;
                        break;
                    }
                }
                if(isOk){
                    gSavedArray.add(gTeam.get(position));
                    //savePref
                    ArrayList<String> newStr=new ArrayList<String>();
                    //팀영어로 바꾸기
                    for(int i=0;i<gSavedArray.size();i++) {
                        String teamName=gSavedArray.get(i).getTeamName();
                        try{
                            newStr.add(kToeJson.get(teamName).toString());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    StaticPref.savePref_prefTeam(getApplicationContext(), newStr);
                    gridAdapter3.notifyDataSetChanged();
                    //맨 끝 보게하기.
                    gridView3.smoothScrollToPosition(gSavedArray.size());
                }
            }
        });
        //END team listener

        //START saved listener
        gridView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                gSavedArray.remove(position);
                //savePref
                ArrayList<String> newStr=new ArrayList<String>();
                for(int i=0;i<gSavedArray.size();i++) {
                    String teamName=gSavedArray.get(i).getTeamName();
                    //팀영어로 바꾸기
                    try{
                        newStr.add(kToeJson.get(teamName).toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                StaticPref.savePref_prefTeam(getApplicationContext(), newStr);
                gridAdapter3.notifyDataSetChanged();
            }
        });
        //END saved listener
    }

    private void getTeam(int position, ArrayList<Grid> aList) throws Exception{
        String teamName=null;
        aList.clear();
        String temp= StaticMethod.loadJSONFromAsset("league_team_korean.json", this);
        JSONObject jo=new JSONObject(temp);
        String leagueName=returnLeagueName(position);
        JSONArray jr=jo.getJSONArray(leagueName);
        for(int i=0;i<jr.length();i++){
            JSONObject jo2=jr.getJSONObject(i);
            teamName=jo2.get("teamName").toString();
            int flagId = flag.list.get(0).get(teamName);
            Grid g=new Grid();
            g.setFlag(flagId);
            g.setTeamName(teamName);
            aList.add(g);
        }
    }
    private void getSavedArray(ArrayList<String> savedTeam) throws Exception{
        gSavedArray=new ArrayList<Grid>();
        gSavedArray.clear();

        //영어로 된 아이를 한글로 바꿔서 넣어줘야함.
        for(int i=0;i<savedTeam.size();i++){
            String teamName=eTokJson.get(savedTeam.get(i)).toString();
            int flagId = flag.list.get(0).get(teamName);
            Grid g=new Grid();
            g.setFlag(flagId);
            g.setTeamName(teamName);
            gSavedArray.add(g);
        }
    }

    private String returnLeagueName(int position){
        if(position==0)
            return "pre";
        else if(position==1)
            return "la";
        else if(position==2)
            return "bun";
        else if(position==3)
            return "se";
        else if(position==4)
            return "li";
        return null;
    }

    public void exitButtonOnClicked(View v){
        //3.팀추가할때마다서비스 재시작
        //현재 날짜를 받아오고, 24시간 후 까지 추가한다.
        //액티비티에 접속했을때와 비교해서, 삭제된 팀, 추가된 팀을 찾는다.
        ArrayList<String> lastData= StaticPref.loadPref_prefTeam(this);

        Set<String> preSet=new HashSet<String>();
        preSet.addAll(preData);
        Set<String> lastSet=new HashSet<String>();
        lastSet.addAll(lastData);

        //추가된 팀 뽑기
        Set<String> addedSet=new HashSet<String>();
        addedSet.addAll(lastData);
        for(String p: preSet){
            if(addedSet.contains(p))
                addedSet.remove(p);
        }
        Log.e(TAG,"추가된 데이터"+addedSet.toString());

        //삭제된 팀
        Set<String> removedSet=new HashSet<String>();
        removedSet.addAll(preData);
        for(String l: lastSet){
            if(removedSet.contains(l))
                removedSet.remove(l);
        }

        Log.e(TAG, "삭제된 데이터" + removedSet.toString());

        //추가된 팀이 있다면 등록한다.
        RepeatReceiver rr=new RepeatReceiver();
        if(!addedSet.isEmpty()) {
            Log.e(TAG, "추가된거 있다.");

            Calendar c_now=Calendar.getInstance();
            //1모아보기 목록에 추가
            String loadMatchInfo= StaticPref.loadPref_String(this, TAG, Sfile.json_fixturesName);
            try{
                JSONArray ja=new JSONArray(loadMatchInfo);
                //관심팀 해쉬맵 로드
                Map<Integer, PrefInfo> prefInfo= StaticPref.loadPref_prefInfo(this);
                Set keySet=prefInfo.keySet();

                for(int i=0;i<ja.length(); i++) {
                    JSONObject row = ja.getJSONObject(i);
                    String home=row.get("home").toString();
                    String away=row.get("away").toString();
                    int code=(int)row.get("code");
                    PrefInfo p=new PrefInfo();

                    //제끼기
                    //제끼기1.관심팀에 이미 존재하는 경우
                    if(keySet.contains(code))
                        continue;
                    //제끼기2.오늘보다 이전 날짜
                    Calendar jCal = StaticMethod.setJsonCal(row);
                    if(jCal.compareTo(c_now)==-1)
                        continue;
                    if(addedSet.contains(home) || addedSet.contains(away)){
                        p.sethTeam(home);
                        p.setaTeam(away);
                        p.setLove(false);
                        prefInfo.put(code, p);
                    }
                    //관심팀 해쉬맵 저장
                    StaticPref.savePref_prefInfo(this, prefInfo);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            //2알람 스케쥴
            rr.setTime();
            rr.setAddedTeamAlarm(this, addedSet);
        }
        if(!removedSet.isEmpty()) {
            Log.e(TAG, "삭제된거 있다.");

            Iterator<String> itr = lastSet.iterator();
            // 이터레이터 객체 itr에 HashSet객체인 movie안에 있는 값들을 가져온다
            // 모든 컬렉션 안에는 iterator()메서드가 있기 때문에 점찍고 사용하면 movie안에 있는 값들을 가져올 수 있다
            while (itr.hasNext()) { // 값이 나올때까지 while문을 돈다
                String s = itr.next(); // 문자열 변수 s에 다음값을 넣는다
                System.out.println(s);
            }

            //관심팀 해쉬맵 로드
            Map<Integer, PrefInfo> prefInfo= StaticPref.loadPref_prefInfo(this);

            ArrayList<Integer> tempArray=new ArrayList<>();

            for(int code:prefInfo.keySet()){
                String pref_H_Team=prefInfo.get(code).gethTeam();
                String pref_A_Team=prefInfo.get(code).getaTeam();
                boolean isRemove=false;
                if(removedSet.contains(pref_H_Team)){
                    if(!lastSet.contains(pref_A_Team))
                        isRemove=true;
                }else if(removedSet.contains(pref_A_Team)){
                    if(!lastSet.contains(pref_H_Team))
                        isRemove=true;
                }
                if(isRemove){
                    //0알람과 1알람(1알람은code는 code+RepeatReceiver.TEMPPLUS) 캔슬
                    AlarmReceiver AR=new AlarmReceiver();
                    AR.cancelAlarm(this, code);
                    //순서: 코드에 1알람값 더하기 전에 삭제리스트에 추가해줘야함. 순서지켜야한다.
                    tempArray.add(code);
                    code+= RepeatReceiver.TEMPPLUS;
                    AR.cancelAlarm(this, code);
                    Log.e(TAG, code + ": " + pref_H_Team + " " + pref_A_Team + "는 캔슬됬습니다.");
                    //어레이 리스트에 저장해준 후, 마지막에 이것을 pref에서 remove하고 sharedpref에저장한다.
                    //(원본에 하면 i가 꼬임)
                }
            } //end for(int code:prefInfo.keySet())
            //저장한 리스트를 이용해서 삭제.
            for(int i: tempArray) {
                if(!prefInfo.get(i).isLove()) //경기일정에서 추가한건 안지운다.
                    prefInfo.remove(i);
            }
            //관심팀 해쉬맵 저장
            StaticPref.savePref_prefInfo(this, prefInfo);
            //print();
        }
        finish();
    }

    private void print(){
        Map<Integer, PrefInfo> prefInfo= StaticPref.loadPref_prefInfo(this);
        Log.e(TAG,prefInfo.size()+"");
        for(int key: prefInfo.keySet()){
            Log.e(TAG, prefInfo.get(key).gethTeam()+" " +prefInfo.get(key).getaTeam());
        }
    }
}








