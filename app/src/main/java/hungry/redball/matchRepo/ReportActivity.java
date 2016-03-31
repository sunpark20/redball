package hungry.redball.matchRepo;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
    LayoutInflater inflater;
    LinearLayout buttonLayout;
    LinearLayout viewContainer;
    private TextView matchBtn,playerBtn,youTubeBtn;
    private TextView aPlayerBtn,hPlayerBtn,hScore,aScore;
    private TextView repo_hGoal,repo_aGoal,textView3;
    private ImageView hFlag,aFlag;
    private int[] childViewH;
    private int basicGoalH;
    private int childViewW;
    private String[] report;
    private Bitmap onBtn,offBtn,onBtn2,offBtn2;
    private Drawable onDraw,offDraw,onDraw2,offDraw2;
    private String hTeam,aTeam,score,hScoreStr,aScoreStr;
    private Bitmap hTeamFlag,aTeamFlag;
    private FlagHashMap flag;
    private final String ASSIST="도움";
    private final String OWNGOAL="자책골";


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
            pageViews.get(i).measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            childViewH[i] = pageViews.get(i).getMeasuredHeight();
        }
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

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
//        int width = textView1.getWidth();
//        int height = textView1.getHeight();
        Log.e("onCreateView", "onCreateView");
//        h1=textView1.getHeight();
//        w=textView1.getWidth();
//        h2=textView2.getHeight();
//        h3=textView3.getHeight();
        if(repo_hGoal.getHeight()>repo_aGoal.getHeight())
            childViewH[0]=childViewH[0]+repo_hGoal.getHeight()-basicGoalH;
        else
            childViewH[0]=childViewH[0]+repo_aGoal.getHeight()-basicGoalH;
        viewContainer.setLayoutParams(new LinearLayout.LayoutParams(childViewW, childViewH[0]));

        matchBtn.setBackground(onDraw);
        playerBtn.setBackground(offDraw);
        youTubeBtn.setBackground(offDraw);

        hPlayerBtn.setBackground(onDraw2);
        aPlayerBtn.setBackground(offDraw2);

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

        ArrayList<String[]> goalList = new ArrayList<String[]>();
        HashMap<String, String> hTeamMap = new HashMap<String, String>(),aTeamMap = new HashMap<String, String>();
        ArrayList<String[]> hPlayerList = new ArrayList<String[]>(),aPlayerList = new ArrayList<String[]>();




        for(int i=0;i<5;i++){
            report[i]=report[i].replaceAll(",,","");

//            String p=report[i].replace(report[i].charAt(0), '{');
//            p=p.replace(p.charAt(p.length()-1), '}');
//            Log.e("sad",report[i]);
            Object resultObj = com.mongodb.util.JSON.parse(report[i]);//바꿀꺼!!
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

        String goalData = report[0].substring(1,report[0].length()-1).replace("\"","");
        int index=0,hIndex=0,aIndex=0;
//        String[] goalArr=new String[]{};
        String[] goalArrTemp;
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
        int possession = Integer.valueOf(hTeamMap.get("possession_percentage"));
        Log.e("홈점유율",hTeamMap.get("possession_percentage"));
//		System.out.println(hRecod.get("won_corners"));
//		System.out.println(aRecod.get("won_corners"));
		/*
		for(String a[]:hPlayerList){
			System.out.println(a[0]+a[1]+a[2]+a[3]+a[4]+a[5]);//이름,포지션종류,출전포지션,등번호,선발/후보,출전여부,교체시간
//			System.out.println(a[6]);//세부기록
			String recordArr[] = a[6].replace("[","").replace("]","").trim().split(",");
			for(String aa:recordArr){
				aa=aa.trim();
				System.out.println(aa);
			}
//			System.out.println(a[6].replace("[","").replace("]","").trim());
		}

		for(String a[]:aPlayerList){
			System.out.println(a[0]+a[1]+a[2]+a[3]+a[4]+a[5]);//이름,포지션종류,출전포지션,등번호,선발/후보,출전여부,교체시간
//			System.out.println(a[6]);//세부기록
			String recordArr[] = a[6].replace("[","").replace("]","").trim().split(",");
			for(String aa:recordArr){
				aa=aa.trim();
				System.out.println(aa);
			}
//			System.out.println(a[6].replace("[","").replace("]","").trim());
		}
		*/


        repo_hGoal.setText(hGoalsetText);
        repo_aGoal.setText(aGoalsetText);
    }

    public void matchClick(View view){
        buttonLayout.setVisibility(View.GONE);
        matchBtn.setBackground(onDraw);
        playerBtn.setBackground(offDraw);
        youTubeBtn.setBackground(offDraw);
        viewContainer.setLayoutParams(new LinearLayout.LayoutParams(childViewW, childViewH[0]));
//        Log.e("1111" + "", textView1.getHeight() + "");
        viewPager.setCurrentItem(0, false);
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
    }

    public void youTubeClick(View view){
        buttonLayout.setVisibility(View.GONE);
        matchBtn.setBackground(offDraw);
        playerBtn.setBackground(offDraw);
        youTubeBtn.setBackground(onDraw);

//        Log.e("2222" + "", textView2.getHeight() + "");
    }

    public void hPlayerClick(View view){
        viewContainer.setLayoutParams(new LinearLayout.LayoutParams(childViewW, childViewH[2]));
//        Log.e("2222" + "", textView2.getHeight() + "");
        hPlayerBtn.setBackground(onDraw2);
        aPlayerBtn.setBackground(offDraw2);
        viewPager.setCurrentItem(1, false);
    }

    public void aPlayerClick(View view){
//        textView2.setText(report[4]);
        viewContainer.setLayoutParams(new LinearLayout.LayoutParams(childViewW, childViewH[3]));
//        Log.e("3333" + "", textView3.getHeight() + "");
        hPlayerBtn.setBackground(offDraw2);
        aPlayerBtn.setBackground(onDraw2);
        viewPager.setCurrentItem(2, false);
    }
}
