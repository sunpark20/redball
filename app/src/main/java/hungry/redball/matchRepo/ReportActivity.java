package hungry.redball.matchRepo;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mongodb.util.JSONParseException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import hungry.redball.R;
import hungry.redball.team.util.FlagHashMap;

public class ReportActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ArrayList<View> pageViews; 	// pages view
    private ViewGroup viewPics;         // layout for view group
    private ViewPageAdapter pageAdapter;
    private int state;
    LayoutInflater inflater;
    LinearLayout buttonLayout;
    LinearLayout viewContainer;
    private TextView matchBtn,playerBtn,youTubeBtn;
    private TextView aPlayerBtn,hPlayerBtn,hScore,aScore;
    private TextView repo_hGoal,repo_aGoal,repo_hPossession,repo_aPossession
            ,repo_hTotalShot,repo_aTotalShot,repo_hAccurateShot,repo_aAccurateShot
            ,repo_hSuccessPass,repo_aSuccessPass,repo_hTackles,repo_aTackles,repo_hContest,repo_aContest
            ,repo_hAerial,repo_aAerial,repo_hFoul,repo_aFoul,repo_hOffside,repo_aOffside;
    private ImageView hFlag,aFlag;
    private int[] childViewH;
    private int basicGoalH;
    private int childViewW;
    private int basicRecordH,basicRecordA;
    private String[] report;
    private Bitmap onBtn,offBtn,onBtn2,offBtn2;
    private Drawable onDraw,offDraw,onDraw2,offDraw2;
    private String hTeam,aTeam,score,hScoreStr,aScoreStr;
    private Bitmap hTeamFlag,aTeamFlag;
    private FlagHashMap flag;
    private final String ASSIST="도움";
    private final String OWNGOAL="자책골";
    private LinearLayout recordHLayout,recordALayout;
    private View recordRow,recordRow2;
    private static final int HOME = 0;
    private static final int AWAY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        // save views into array
        inflater = getLayoutInflater();
        pageViews = new ArrayList<View>();
        pageViews.add(inflater.inflate(R.layout.report_match, null));
        pageViews.add(inflater.inflate(R.layout.report_hplayer, null));
        pageViews.add(inflater.inflate(R.layout.report_aplayer, null));
        pageViews.add(inflater.inflate(R.layout.report_youtube, null));

        childViewH=new int[4];
        for(int i=0;i<4;i++){
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                pageViews.get(i).measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                childViewH[i]= pageViews.get(i).getMeasuredHeight();
            } else {
                Log.e("JELLY_BEAN--","JELLY_BEAN--");
                childViewH[i]=1500;
            }
        }
        Log.e("onCreate" + "", childViewH[2] + "");
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        childViewW = dm.widthPixels;

        Intent intent = getIntent();
        report = intent.getExtras().getStringArray("report");
        hTeam = intent.getExtras().getString("hTeam");
        aTeam = intent.getExtras().getString("aTeam");
        score = intent.getExtras().getString("score");
        if(score.indexOf(":")!=-1){
            hScoreStr = score.substring(0,score.indexOf(":")).trim();
            aScoreStr = score.substring(score.indexOf(":")+1, score.length()).trim();
        }
        else if(score.indexOf("-")!=-1){
            hScoreStr =score.substring(0,score.indexOf("-")).trim();
            aScoreStr =score.substring(score.indexOf("-")+1, score.length()).trim();
        }

        Resources res=this.getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        onBtn=BitmapFactory.decodeResource(res, R.drawable.on_btn, options);
        offBtn=BitmapFactory.decodeResource(res, R.drawable.off_btn, options);
        onBtn2=BitmapFactory.decodeResource(res, R.drawable.on_btn2, options);
        offBtn2=BitmapFactory.decodeResource(res, R.drawable.off_btn2, options);

        onDraw = new BitmapDrawable(getResources(), onBtn);
        offDraw = new BitmapDrawable(getResources(), offBtn);
        onDraw2 = new BitmapDrawable(getResources(), onBtn2);
        offDraw2 = new BitmapDrawable(getResources(), offBtn2);

        flag=new FlagHashMap(); //깃발 정보 불러오기
        flag.makeHashMap();

        int hteamSrc = flag.list.get(0).get(hTeam.toLowerCase());
        int ateamSrc = flag.list.get(0).get(aTeam.toLowerCase());

        options.inSampleSize = 1;
        hTeamFlag=BitmapFactory.decodeResource(res, hteamSrc, options);
        aTeamFlag=BitmapFactory.decodeResource(res, ateamSrc, options);

        // save small points into array
        viewPics = (ViewGroup) inflater.inflate(R.layout.activity_report, null);

        // find view by id
        hFlag = (ImageView) viewPics.findViewById(R.id.repo_hflag);
        hFlag.setImageBitmap(hTeamFlag);
        aFlag = (ImageView) viewPics.findViewById(R.id.repo_aflag);
        aFlag.setImageBitmap(aTeamFlag);
        hScore = (TextView) viewPics.findViewById(R.id.repo_hScore);
        hScore.setText(hScoreStr);
        aScore = (TextView) viewPics.findViewById(R.id.repo_aScore);
        aScore.setText(aScoreStr);
        matchBtn = (TextView) viewPics.findViewById(R.id.match_btn);
        playerBtn = (TextView) viewPics.findViewById(R.id.player_btn);
        youTubeBtn = (TextView) viewPics.findViewById(R.id.youtube_btn);
        hPlayerBtn = (TextView) viewPics.findViewById(R.id.hplayer_btn);
        hPlayerBtn.setText(hTeam);
        aPlayerBtn = (TextView) viewPics.findViewById(R.id.aplayer_btn);
        aPlayerBtn.setText(aTeam);
        viewPager = (ViewPager) viewPics.findViewById(R.id.viewPager);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true; }
        });
        buttonLayout = (LinearLayout)viewPics.findViewById(R.id.buttonLayout);
        viewContainer = (LinearLayout)viewPics.findViewById(R.id.view_container);
        pageAdapter = new ViewPageAdapter(pageViews, this);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(0, false);
        setContentView(viewPics);

//        if(savedInstanceState!=null)
//            Log.e("notNull","notNull");
//        else
//            Log.e("null","null");
        state = 0;
        matchBtn.setBackground(onDraw);
        playerBtn.setBackground(offDraw);
        youTubeBtn.setBackground(offDraw);

        hPlayerBtn.setBackground(onDraw2);
        aPlayerBtn.setBackground(offDraw2);

    }

//    protected void onSaveInstanceState(Bundle outState) {
//
//        Bundle bundle = new Bundle();
//        bundle.putString("my_data", "save");
//        outState.putBundle("save_data", bundle);
//    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
//        int width = textView1.getWidth();
//        int height = textView1.getHeight();
        Log.e("onCreateView", "onCreateView");
        Log.e("nowState", state+"");
//        h1=textView1.getHeight();
//        w=textView1.getWidth();
//        h2=textView2.getHeight();
//        h3=textView3.getHeight();
        if(repo_hGoal.getHeight()>repo_aGoal.getHeight())
            childViewH[0] = childViewH[0] + repo_hGoal.getHeight()-basicGoalH;
        else
            childViewH[0]=childViewH[0]+repo_aGoal.getHeight()-basicGoalH;

        if(state==0)
            viewContainer.setLayoutParams(new LinearLayout.LayoutParams(childViewW, childViewH[0]));
        if(state==1)
            viewContainer.setLayoutParams(new LinearLayout.LayoutParams(childViewW, childViewH[1]));
        if(state==2)
            viewContainer.setLayoutParams(new LinearLayout.LayoutParams(childViewW, childViewH[2]));


//        Log.e("1111" + "",textView1.getHeight()+"");
//        Log.e("2222" + "",textView2.getHeight()+"");
//        Log.e("3333" + "",textView3.getHeight()+"");
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            setLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void setLayout() throws Exception{
        repo_hGoal=(TextView)pageViews.get(0).findViewById(R.id.repo_hGoal);
        repo_hGoal.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        basicGoalH = repo_hGoal.getMeasuredHeight();
        repo_aGoal=(TextView)pageViews.get(0).findViewById(R.id.repo_aGoal);
        repo_hPossession=(TextView)pageViews.get(0).findViewById(R.id.hPossession);
        repo_aPossession=(TextView)pageViews.get(0).findViewById(R.id.aPossession);
        repo_hTotalShot=(TextView)pageViews.get(0).findViewById(R.id.hTotalShot);
        repo_aTotalShot=(TextView)pageViews.get(0).findViewById(R.id.aTotalShot);
        repo_hAccurateShot=(TextView)pageViews.get(0).findViewById(R.id.hAccurateShot);
        repo_aAccurateShot=(TextView)pageViews.get(0).findViewById(R.id.aAccurateShot);
        repo_hSuccessPass=(TextView)pageViews.get(0).findViewById(R.id.hSuccessPass);
        repo_aSuccessPass=(TextView)pageViews.get(0).findViewById(R.id.aSuccessPass);
        repo_hTackles=(TextView)pageViews.get(0).findViewById(R.id.hTackles);
        repo_aTackles=(TextView)pageViews.get(0).findViewById(R.id.aTackles);
        repo_hContest=(TextView)pageViews.get(0).findViewById(R.id.hContest);
        repo_aContest=(TextView)pageViews.get(0).findViewById(R.id.aContest);
        repo_hAerial=(TextView)pageViews.get(0).findViewById(R.id.hAerial);
        repo_aAerial=(TextView)pageViews.get(0).findViewById(R.id.aAerial);
        repo_hFoul=(TextView)pageViews.get(0).findViewById(R.id.hFoul);
        repo_aFoul=(TextView)pageViews.get(0).findViewById(R.id.aFoul);
        repo_hOffside=(TextView)pageViews.get(0).findViewById(R.id.hOffside);
        repo_aOffside=(TextView)pageViews.get(0).findViewById(R.id.aOffside);

        repo_aOffside=(TextView)pageViews.get(0).findViewById(R.id.aOffside);


        ArrayList<String[]> goalList = new ArrayList<String[]>();
        HashMap<String, String> hTeamMap = new HashMap<String, String>(),aTeamMap = new HashMap<String, String>();
        ArrayList<String[]> hPlayerList = new ArrayList<String[]>(),aPlayerList = new ArrayList<String[]>();
        Object resultObj=new Object();



        for(int i=0;i<5;i++){
            report[i]=report[i].replaceAll(",,","");

//            String p=report[i].replace(report[i].charAt(0), '{');
//            p=p.replace(p.charAt(p.length()-1), '}');
//            Log.e("sad",report[i]);
            try {
                resultObj = com.mongodb.util.JSON.parse(report[i]);
            }catch (JSONParseException e){
                Log.e("JSONParseException","JSONParseException");
                continue;
            }

//            JSONArray resultObj=new JSONArray(report[i]);

            ArrayList<String> resultList = new ArrayList<String>();
            resultList.addAll((Collection<? extends String>) resultObj);
            Log.e("aaa", resultList + "");

            for (Object obj:resultList){
                ArrayList<String> arrMatchInfo = new ArrayList<String>();
                arrMatchInfo.addAll((Collection<? extends String>) obj);
                if(i==0){
                    String golaEveArr[]={String.valueOf(arrMatchInfo.get(0)),String.valueOf(arrMatchInfo.get(1)),String.valueOf(arrMatchInfo.get(3)),String.valueOf(arrMatchInfo.get(4)),String.valueOf(arrMatchInfo.get(5))};
                    goalList.add(golaEveArr);
//					System.out.println(String.valueOf(arrMatchInfo.get(0))+","+String.valueOf(arrMatchInfo.get(1))+","+String.valueOf(arrMatchInfo.get(3))+","+String.valueOf(arrMatchInfo.get(4))+","+String.valueOf(arrMatchInfo.get(5)));
                }
                else if(i==1||i==2){
//					System.out.println(String.valueOf(arrMatchInfo.get(0)).trim());
                    if(i==1)
                        hTeamMap.put(String.valueOf(arrMatchInfo.get(0)).trim(), String.valueOf(arrMatchInfo.get(1)).replace("[","").replace("]","").trim());
                    else
                        aTeamMap.put(String.valueOf(arrMatchInfo.get(0)).trim(), String.valueOf(arrMatchInfo.get(1)).replace("[","").replace("]","").trim());
                }else{
                    //1이름,3기록([]),4포지션종류,5출전포지션,6등번호,7선발/후보,8출전여부,9교체시간
                    ArrayList<String> arrPlayerInfo = new ArrayList<String>();
                    arrPlayerInfo.addAll((Collection<? extends String>) obj);
//					System.out.println(arrPlayerInfo);
                    if(i==3){
                        String golaEveArr[]={String.valueOf(arrPlayerInfo.get(1)),String.valueOf(arrPlayerInfo.get(4)),String.valueOf(arrPlayerInfo.get(5)),
                                String.valueOf(arrPlayerInfo.get(6)),String.valueOf(arrPlayerInfo.get(7)),String.valueOf(arrPlayerInfo.get(8)),String.valueOf(arrPlayerInfo.get(3))};
                        hPlayerList.add(golaEveArr);
//						ArrayList<String> arrPlayerRecord = new ArrayList<String>();
//						arrPlayerRecord.addAll((Collection<? extends String>) (Object)arrPlayerInfo.get(3));
//						System.out.println(arrPlayerInfo.get(1)+" "+arrPlayerInfo.get(1));
//						System.out.println(arrPlayerRecord);
                    }
                    else{
                        String golaEveArr[]={String.valueOf(arrPlayerInfo.get(1)),String.valueOf(arrPlayerInfo.get(4)),String.valueOf(arrPlayerInfo.get(5)),
                                String.valueOf(arrPlayerInfo.get(6)),String.valueOf(arrPlayerInfo.get(7)),String.valueOf(arrPlayerInfo.get(8)),String.valueOf(arrPlayerInfo.get(3))};
                        aPlayerList.add(golaEveArr);
                    }
                }
            }
        }

        int index=0,hIndex=0,aIndex=0;
        String state="(0-0)";
        String goalEve;
        String hGoalsetText=new String();
        String aGoalsetText=new String();

		for(String goalArr[]:goalList){
            if(index==0&&goalArr.length !=0){
                if(state.charAt(1)!=goalArr[2].charAt(1)){
                    if(!goalArr[1].equals("null")&&goalArr[3].equals("null")){
                        goalEve=goalArr[4]+"' "+goalArr[0]+"\n("+ASSIST+":"+goalArr[1]+")";
                    }else if(goalArr[1].equals("null")&&goalArr[3].equals("null")){
                        goalEve=goalArr[4]+"'  "+goalArr[0];
                    }else{
                        goalEve=goalArr[4]+"'  "+goalArr[0]+"\n("+OWNGOAL+")";
                    }
                    hGoalsetText +=goalEve;
                    hIndex++;
                }else{
                    if(!goalArr[1].equals("null")&&goalArr[3].equals("null")){
                        goalEve=goalArr[4]+"'  "+goalArr[0]+"\n("+ASSIST+":"+goalArr[1]+")";
                    }else if(goalArr[1].equals("null")&&goalArr[3].equals("null")){
                        goalEve=goalArr[4]+"'  "+goalArr[0];
                    }else{
                        goalEve=goalArr[4]+"'  "+goalArr[0]+"\n("+OWNGOAL+")";
                    }
                    aGoalsetText +=goalEve;
                    aIndex++;
                }
            }else if(goalArr.length!=0){
                if(state.charAt(1)!=goalArr[2].charAt(1)){
                    if(!goalArr[1].equals("null")&&goalArr[3].equals("null")){
                        goalEve=goalArr[4]+"'  "+goalArr[0]+"\n("+ASSIST+":"+goalArr[1]+")";
                    }else if(goalArr[1].equals("null")&&goalArr[3].equals("null")){
                        goalEve=goalArr[4]+"'  "+goalArr[0];
                    }else{
                        goalEve=goalArr[4]+"'  "+goalArr[0]+"\n("+OWNGOAL+")";
                    }
                    if(hIndex==0){
                        hGoalsetText +=goalEve;
                        hIndex++;
                    }
                    else
                        hGoalsetText += "\n"+goalEve;
                }else {
                    if (!goalArr[1].equals("null") && goalArr[3].equals("null")) {
                        goalEve = goalArr[4] + "'  " + goalArr[0] + "\n(" + ASSIST + ":" + goalArr[1] + ")";
                    } else if (goalArr[1].equals("null") && goalArr[3].equals("null")) {
                        goalEve = goalArr[4] + "'  " + goalArr[0];
                    } else {
                        goalEve = goalArr[4] + "'  " + goalArr[0] + "\n(" + OWNGOAL + ")";
                    }
                    if(aIndex==0){
                        aGoalsetText +=goalEve;
                        aIndex++;
                    }else
                        aGoalsetText += "\n"+goalEve;
                }
            }else{
                hGoalsetText="";
                aGoalsetText="";
            }
//			System.out.println(a[0]+a[1]+a[2]+a[3]+a[4]);//골 어시 스코어 자살골 시간
            index++;
            if(goalArr.length!=0)
                state=goalArr[2];
		}

        float possession = Float.valueOf(hTeamMap.get("possession_percentage"));
        int hPossession = Math.round(possession * 100 / 100);//점유율
        int aPossession = 100-hPossession;
        String hTotalShot=  hTeamMap.get("total_scoring_att");//총슈팅
        String aTotalShot=  aTeamMap.get("total_scoring_att");
        String hAccurateShot=  hTeamMap.get("ontarget_scoring_att");//유효슛
        String aAccurateShot=  aTeamMap.get("ontarget_scoring_att");
        int hTotalPass = Integer.valueOf(hTeamMap.get("total_pass"));
        int aTotalPass = Integer.valueOf(aTeamMap.get("total_pass"));
        int hAccuratePass = Integer.valueOf(hTeamMap.get("accurate_pass"));
        int aAccuratePass = Integer.valueOf(aTeamMap.get("accurate_pass"));
        double  hSuccessPass=Math.round(((double) hAccuratePass / hTotalPass * 100) * 100 / 100);//패스성공률
        double  aSuccessPass=Math.round(((double) aAccuratePass / aTotalPass * 100) * 100 / 100);
        String hTackles=  hTeamMap.get("total_tackle");//패스차단
        String aTackles=  aTeamMap.get("total_tackle");
        String hContest=  hTeamMap.get("won_contest");//경합승리
        String aContest=  aTeamMap.get("won_contest");
        String hAerial=  hTeamMap.get("aerial_won");//공중볼승리
        String aAerial=  aTeamMap.get("aerial_won");
        String hFoul=  hTeamMap.get("fk_foul_lost");//파울
        String aFoul=  aTeamMap.get("fk_foul_lost");
        String hOffside=  hTeamMap.get("total_offside");//오프사이드
        String aOffside=  aTeamMap.get("total_offside");

//		System.out.println(hRecod.get("won_corners"));
//		System.out.println(aRecod.get("won_corners"));
        recordHLayout = (LinearLayout)pageViews.get(1).findViewById(R.id.repo2);
        recordALayout = (LinearLayout)pageViews.get(2).findViewById(R.id.repo3);
        //초기화
        recordHLayout.removeAllViews();
        recordHLayout.refreshDrawableState();
        recordALayout.removeAllViews();
        recordALayout.refreshDrawableState();
        basicRecordH = 0;
        basicRecordA = 0;

        recordRow = inflater.inflate(R.layout.report_player_row, null);

        recordRow.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        basicRecordH = recordRow.getMeasuredHeight();
        basicRecordA = recordRow.getMeasuredHeight();
//        childViewH[2]=basicRecordH*2;
//        recordALayout.addView(recordRow);
        setRecord(AWAY,aPlayerList);
        setRecord(HOME,hPlayerList);

        /*
		for(String a[]:aPlayerList){
			Log.e("playerInpo","이름-"+a[0]+" 포지션종류-"+a[1]+" 출전포지션-"+a[2]+" 등번호-"+a[3]+" 선발/후보-"+a[4]+" 교체시간-"+a[5]);//이름,포지션종류,출전포지션,등번호,선발/후보,교체시간
//			System.out.println(a[6]);//세부기록
			String recordArr[] = a[6].replace("[","").replace("]","").trim().split(",");
			for(String record:recordArr){
				record=record.trim();
				System.out.println(record);
			}
//			System.out.println(a[6].replace("[","").replace("]","").trim());
		}
        */

        repo_hGoal.setText(hGoalsetText);
        repo_aGoal.setText(aGoalsetText);
        repo_hPossession.setText(String.valueOf(hPossession)+"%");
        repo_aPossession.setText(String.valueOf(aPossession)+"%");
        repo_hTotalShot.setText(hTotalShot);
        repo_aTotalShot.setText(aTotalShot);
        repo_hAccurateShot.setText(hAccurateShot);
        repo_aAccurateShot.setText(aAccurateShot);
        repo_hSuccessPass.setText(String.valueOf((int)hSuccessPass)+"%");
        repo_aSuccessPass.setText(String.valueOf((int)aSuccessPass)+"%");
        repo_hTackles.setText(hTackles);
        repo_aTackles.setText(aTackles);
        repo_hContest.setText(hContest);
        repo_aContest.setText(aContest);
        repo_hAerial.setText(hAerial);
        repo_aAerial.setText(aAerial);
        repo_hFoul.setText(hFoul);
        repo_aFoul.setText(aFoul);
        repo_hOffside.setText(hOffside);
        if(repo_hOffside.getText().length()==0)
            repo_hOffside.setText("0");
        repo_aOffside.setText(aOffside);
        if(repo_aOffside.getText().length()==0)
            repo_aOffside.setText("0");
    }

    public void setRecord(int kind,ArrayList<String[]> playerList){
        Log.e(kind+"","setRecord");
        ArrayList<View> fwPlayers = new ArrayList<>();
        ArrayList<View> mfPlayers = new ArrayList<>();
        ArrayList<View> dfPlayers = new ArrayList<>();
        ArrayList<View> gkPlayers = new ArrayList<>();
        ArrayList<View> changedPlayers = new ArrayList<>();
        ArrayList<String> changedTime = new ArrayList<>();
        HashMap<String,String[]> changedMap = new HashMap<>();
        ArrayList<View> subPlayers = new ArrayList<>();
        int sizeSum = 0;

        for(String a[]:playerList){
            if(!a[2].equals("Sub")){
                if(a[1].equals("1")){
                    gkPlayers.add(viewSet(a,4));
                    Log.e("stating",a[0]);
                    if(!a[5].equals("0")){
                        String[] arr = {"4",a[2]};
                        changedMap.put(a[5],arr);
                    }
                }
                if(a[1].equals("2")){
                    dfPlayers.add(viewSet(a, 3));
                    Log.e("stating",a[0]);
                    if(!a[5].equals("0")){
                        String[] arr = {"3",a[2]};
                        changedMap.put(a[5],arr);
                    }
                }
                if(a[1].equals("3")){
                    mfPlayers.add(viewSet(a,2));
                    Log.e("stating",a[0]);
                    if(!a[5].equals("0")){
                        String[] arr = {"2",a[2]};
                        changedMap.put(a[5],arr);
                    }
                }
                if(a[1].equals("4")){
                    fwPlayers.add(viewSet(a,1));
                    Log.e("stating",a[0]);
                    if(!a[5].equals("0")){
                        String[] arr = {"1",a[2]};
                        changedMap.put(a[5],arr);
                    }
                }
            }
            if(a[4].equals("2")){
                changedPlayers.add(changeViewSet(a, changedMap, a[5]));
                changedTime.add(a[5]);
                Log.e("changed",a[0]);
            }
            if(a[2].equals("Sub")&&a[4].equals("0")){
                subPlayers.add(viewSet(a,5));
                Log.e("Sub",a[0]);
            }
            Log.e("playerInpo","이름-"+a[0]+" 포지션종류-"+a[1]+" 출전포지션-"+a[2]+" 등번호-"+a[3]+" 선발/후보-"+a[4]+" 교체시간-"+a[5]);//이름,포지션종류,출전포지션,등번호,선발/후보,교체시간

            String recordArr[] = a[6].replace("[","").replace("]","").trim().split(",");//세부기록
            for(int i=0;i<recordArr.length;i++){
                String recordName=recordArr[i].trim();
                i++;
                String recordValue=recordArr[i].trim();
                Log.e("playerRecord", recordName + " : " + recordValue);
            }
//			System.out.println(a[6].replace("[","").replace("]","").trim());

            sizeSum = fwPlayers.size()+mfPlayers.size()+dfPlayers.size()+gkPlayers.size()+subPlayers.size()+changedPlayers.size();
            if(kind==HOME)
                childViewH[1]=basicRecordH*sizeSum;
            else
                childViewH[2]=basicRecordA*sizeSum;
        }

        for(int i=0;i<fwPlayers.size();i++){
            if(fwPlayers.get(i)!=null){
                if(kind==HOME)
                    recordHLayout.addView(fwPlayers.get(i));
                else
                    recordALayout.addView(fwPlayers.get(i));
                TextView v = (TextView)fwPlayers.get(i).findViewById(R.id.changed_time);
                if(!v.getText().equals("")){
                    if(kind==HOME)
                        recordHLayout.addView(changedPlayers.get(changedViewFinder(v.getText().toString(), changedTime)));
                    else
                        recordALayout.addView(changedPlayers.get(changedViewFinder(v.getText().toString(), changedTime)));
                }
            }
        }

        for(int i=0;i<mfPlayers.size();i++){
            if(mfPlayers.get(i)!=null){
                if(kind==HOME)
                    recordHLayout.addView(mfPlayers.get(i));
                else
                    recordALayout.addView(mfPlayers.get(i));
                TextView v = (TextView)mfPlayers.get(i).findViewById(R.id.changed_time);
                if(!v.getText().equals("")){
                    if(kind==HOME)
                        recordHLayout.addView(changedPlayers.get(changedViewFinder(v.getText().toString(),changedTime)));
                    else
                        recordALayout.addView(changedPlayers.get(changedViewFinder(v.getText().toString(),changedTime)));
                }
            }
        }

        for(int i=0;i<dfPlayers.size();i++){
            if(dfPlayers.get(i)!=null){
                if(kind==HOME)
                    recordHLayout.addView(dfPlayers.get(i));
                else
                    recordALayout.addView(dfPlayers.get(i));
                TextView v = (TextView)dfPlayers.get(i).findViewById(R.id.changed_time);
                if(!v.getText().equals("")){
                    if(kind==HOME)
                        recordHLayout.addView(changedPlayers.get(changedViewFinder(v.getText().toString(),changedTime)));
                    else
                        recordALayout.addView(changedPlayers.get(changedViewFinder(v.getText().toString(),changedTime)));
                }
            }
        }

        for(int i=0;i<gkPlayers.size();i++){
            if(gkPlayers.get(i)!=null){
                if(kind==HOME)
                    recordHLayout.addView(gkPlayers.get(i));
                else
                    recordALayout.addView(gkPlayers.get(i));
                TextView v = (TextView)gkPlayers.get(i).findViewById(R.id.changed_time);
                if(!v.getText().equals("")){
                    if(kind==HOME)
                        recordHLayout.addView(changedPlayers.get(changedViewFinder(v.getText().toString(),changedTime)));
                    else
                        recordALayout.addView(changedPlayers.get(changedViewFinder(v.getText().toString(),changedTime)));
                }
            }
        }

        for(int i=0;i<subPlayers.size();i++){
            if(subPlayers.get(i)!=null){
                if(kind==HOME)
                    recordHLayout.addView(subPlayers.get(i));
                else
                    recordALayout.addView(subPlayers.get(i));
            }
        }

    }


    public View viewSet(String[] recordArr,int classifi){
        View recordRow = inflater.inflate(R.layout.report_player_row, null);
        TextView position = (TextView)recordRow.findViewById(R.id.player_position);
        TextView name = (TextView)recordRow.findViewById(R.id.player_name);
        TextView mumber = (TextView)recordRow.findViewById(R.id.player_mumber);
        TextView changedTime = (TextView)recordRow.findViewById(R.id.changed_time);
        TextView changedKind = (TextView)recordRow.findViewById(R.id.changed_kind);

        if(classifi==1)
            position.setTextColor(getResources().getColor(R.color.positionred));
        if(classifi==2)
            position.setTextColor(getResources().getColor(R.color.positiongreen));
        if(classifi==3)
            position.setTextColor(getResources().getColor(R.color.positionblue));
        if(classifi==4)
            position.setTextColor(getResources().getColor(R.color.positionyellow));
        if(classifi==5)
            position.setTextColor(getResources().getColor(R.color.positiongray));

        TextView[] rowArr = new TextView[24];
        int id = R.id.row_value1;
        for(int i=0;i<24;i++){
            rowArr[i]=(TextView)recordRow.findViewById(id+i);
        }


        ArrayList<String> innerArr = recordRowAdd(recordArr[6]);
        Log.e("size?", innerArr.size() + "");


        for(int i=0;i<innerArr.size();i++){
//            Log.e("기록은",innerArr.get(i));
            if(i==24){
                break;
            }
            if(i!=0){
                if(i%2==1){
                    String key = innerArr.get(i).substring(1,innerArr.get(i).length()-1);
                    if(key.equals("formation_place")){
                        continue;
                    }
                    String getString;
                    if(getStringResourceByName(key)!=0)
                        getString = getResources().getString(getStringResourceByName(key));
                    else{
                        getString="";
                        Log.e("error",key);
                    }
                    rowArr[i].setText(getString);//name
                }else{
                    rowArr[i].setText(innerArr.get(i));//value
                }
            }else{
                rowArr[i].setText(innerArr.get(i));//value
            }
        }

        if(innerArr.size()<24){
            int limit = 24-innerArr.size();
            for(int y=24-limit;y<24;y++){
                rowArr[y].setText("");
            }
        }
        position.setText(recordArr[2]);
        name.setText(recordArr[0]);
        mumber.setText(recordArr[3]);
        if(!recordArr[5].equals("0")){
            changedTime.setText(recordArr[5]);
            changedKind.setText("OUT");
        }
        return recordRow;
    }

    public View changeViewSet(String[] recordArr,HashMap timeMap,String timeKey){
        View recordRow = inflater.inflate(R.layout.report_player_row, null);
        TextView position = (TextView)recordRow.findViewById(R.id.player_position);
        TextView name = (TextView)recordRow.findViewById(R.id.player_name);
        TextView mumber = (TextView)recordRow.findViewById(R.id.player_mumber);
        TextView changedTime = (TextView)recordRow.findViewById(R.id.changed_time);
        TextView changedKind = (TextView)recordRow.findViewById(R.id.changed_kind);

        String[] reValue=(String[])timeMap.get(timeKey);
        if(reValue[0]=="1")
            position.setTextColor(getResources().getColor(R.color.positionred));
        if(reValue[0]=="2")
            position.setTextColor(getResources().getColor(R.color.positiongreen));
        if(reValue[0]=="3")
            position.setTextColor(getResources().getColor(R.color.positionblue));
        if(reValue[0]=="4")
            position.setTextColor(getResources().getColor(R.color.positionyellow));


        TextView[] rowArr = new TextView[24];
        int id = R.id.row_value1;
        for(int y=0;y<24;y++){
            rowArr[y]=(TextView)recordRow.findViewById(id+y);
        }


        ArrayList<String> innerArr = recordRowAdd(recordArr[6]);
        Log.e("size?", innerArr.size() + "");
        for(int i=0;i<innerArr.size();i++){
//            Log.e("기록은",innerArr.get(i));
            if(i==24){
                break;
            }
            if(i!=0){
                if(i%2==1){
                    String key = innerArr.get(i).substring(1, innerArr.get(i).length() - 1);
                    String getString;
                    if(key.equals("formation_place")){
                        continue;
                    }
                    if(getStringResourceByName(key)!=0)
                        getString = getResources().getString(getStringResourceByName(key));
                    else{
                        getString="";
                        Log.e("error",key);
                    }
                    rowArr[i].setText(getString);//name
                }else{
                    rowArr[i].setText(innerArr.get(i));//value
                }
            }else{
                rowArr[i].setText(innerArr.get(i));//value
            }
        }

        if(innerArr.size()<24){
            int limit = 24-innerArr.size();
            for(int y=24-limit;y<24;y++){
                rowArr[y].setText("");
            }
        }
        position.setText(reValue[1]);
        name.setText(recordArr[0]);
        mumber.setText(recordArr[3]);
        if(!recordArr[5].equals("0")){
            changedTime.setText(recordArr[5]);
            changedKind.setText("IN");
            changedKind.setTextColor(getResources().getColor(R.color.positiongreen));
        }
        return recordRow;
    }

    public int changedViewFinder(String changedTime,ArrayList<String> changedTimeArr){
        int index = 0;
        for(int i=0;i<changedTimeArr.size();i++){
           if(changedTimeArr.get(i).equals(changedTime)){
               index = i;
               break;
           }
        }
        return index;
    }

    public ArrayList<String> recordRowAdd(String record){
        String recordArr[] = record.replace("[", "").replace("]","").trim().split(",");//세부기록
        ArrayList<String> arr = new ArrayList<>();
        for(int i=0;i<recordArr.length;i++){
            String recordName=recordArr[i].trim();
            i++;
            String recordValue=recordArr[i].trim();
            arr.add(recordValue);
            arr.add(recordName);
//            Log.e("playerRecord", recordName+" : "+recordValue);
        }
        return arr;
    }

    private int getStringResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return resId;
    }

    public void matchClick(View view){
        buttonLayout.setVisibility(View.GONE);
        matchBtn.setBackground(onDraw);
        playerBtn.setBackground(offDraw);
        youTubeBtn.setBackground(offDraw);
        viewContainer.setLayoutParams(new LinearLayout.LayoutParams(childViewW, childViewH[0]));
//        Log.e("1111" + "", textView1.getHeight() + "");
        viewPager.setCurrentItem(0, false);
        state = 0;
    }

    public void playerClick(View view){
        buttonLayout.setVisibility(View.VISIBLE);
        matchBtn.setBackground(offDraw);
        playerBtn.setBackground(onDraw);
        youTubeBtn.setBackground(offDraw);
        hPlayerBtn.setBackground(onDraw2);
        aPlayerBtn.setBackground(offDraw2);
//        Log.e("2222" + "", textView2.getHeight() + "");
        viewContainer.setLayoutParams(new LinearLayout.LayoutParams(childViewW, childViewH[1]));
        viewPager.setCurrentItem(1, false);
        state = 1;
    }

    public void youTubeClick(View view){
        buttonLayout.setVisibility(View.GONE);
        matchBtn.setBackground(offDraw);
        playerBtn.setBackground(offDraw);
        youTubeBtn.setBackground(onDraw);
        viewContainer.setLayoutParams(new LinearLayout.LayoutParams(childViewW, childViewH[3]));
        viewPager.setCurrentItem(3, false);

//        Log.e("2222" + "", textView2.getHeight() + "");
    }

    public void hPlayerClick(View view){
        viewContainer.setLayoutParams(new LinearLayout.LayoutParams(childViewW, childViewH[1]));
//        Log.e("2222" + "", textView2.getHeight() + "");
        hPlayerBtn.setBackground(onDraw2);
        aPlayerBtn.setBackground(offDraw2);
        viewPager.setCurrentItem(1, false);
        state = 1;
    }

    public void aPlayerClick(View view){
//        textView2.setText(report[4]);
        viewContainer.setLayoutParams(new LinearLayout.LayoutParams(childViewW, childViewH[2]));
//        Log.e("aPlayerClick" + "", childViewH[2] + "");
        hPlayerBtn.setBackground(offDraw2);
        aPlayerBtn.setBackground(onDraw2);
        viewPager.setCurrentItem(2, false);
        state = 2;
    }

}
