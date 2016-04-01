package hungry.redball.fixtures;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import hungry.redball.R;
import hungry.redball.aStatic.StaticFile;
import hungry.redball.aStatic.StaticPref;
import hungry.redball.alram.model.PrefInfo;
import hungry.redball.team.util.FlagHashMap;

public class FicturesActivity extends AppCompatActivity {
    static public PagerSlidingTabStrip tabs;
    static public boolean isOnce=true;
    static public String y,m,d,w,today,pickM;
    private CalendarDatePickerDialogFragment calendarDatePickerDialogFragment;
    private FragmentActivity myContext;
    private static final String FRAG_TAG_DATE_PICKER = "date picker";
    private MenuItem menuItem_cal, menuItem_jong;
    private TextView tVYear,tVMonth;
    private ImageView iVJong;
    private Map<Integer, PrefInfo> prefInfo;

    private Typeface typeface;
    private ImageButton rightBtn;
    private ImageButton leftBtn;
    private Bitmap jongSrc,unJongSrc;

    private float firstX;
    private float firstY;
    private float moveY,tmpMoveY;
    private float lastX;
    private float lastY;

    static public String positionKey;
    static public JSONArray resultArray;
    static public boolean isResultArray[] = new boolean[5];
    static public List<JSONObject> jsonSortList;
    static public List<JSONObject> jsonSortListArr[] = new List[5];
    static public int scrollState;
    static public boolean isJong=false;

    private JSONObject newContacts=null;

    public FlagHashMap flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixtures);
        try{
            newContacts=new JSONObject(StaticFile.readFile(this, StaticFile.json_parsed_fixturesName));
        }catch (Exception e){
            e.printStackTrace();
        }

        //for the pre 4.0 device.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rightBtn = (ImageButton)findViewById(R.id.right_direction);
        leftBtn = (ImageButton)findViewById(R.id.left_direction);

//        layoutView = (LinearLayout)findViewById(R.id.ic_layout);
//        textView = (TextView)findViewById(R.id.chang_date);

        //오늘 날짜 설정
        Calendar Cal = new GregorianCalendar();
			y=String.valueOf(Cal.get(Cal.YEAR)); // 오늘 년도
        if(String.valueOf(Cal.get(Cal.MONTH)+1).length()==1){
            m="0"+String.valueOf(Cal.get(Cal.MONTH)+1);
        }else{
            m=String.valueOf(Cal.get(Cal.MONTH)+1);
        }
        d=String.valueOf(Cal.get(Cal.DAY_OF_MONTH)); // 오늘 날짜

        typeface = Typeface.createFromAsset(this.getAssets(), "fonts/numbers_font.ttf");

        // this is the important code :)
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        /*layoutView.setDrawingCacheEnabled(true);

        layoutView.measure(View.MeasureSpec.makeMeasureSpec(70, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(50, View.MeasureSpec.UNSPECIFIED));

        layoutView.layout(70, 50, layoutView.getMeasuredWidth(), layoutView.getMeasuredHeight());

        layoutView.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(layoutView.getDrawingCache());
        layoutView.setDrawingCacheEnabled(false); // clear drawing cache
        dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(b, 100, 70, true));*/

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.clearOnPageChangeListeners();
        // Bind the tabs to the ViewPager
        pager.setOffscreenPageLimit(5);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        isOnce=true;

        flag=new FlagHashMap(); //깃발 정보 불러오기
        flag.makeHashMap();

        Resources res=this.getResources();//종 이미지 set
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        jongSrc=BitmapFactory.decodeResource(res, R.drawable.f_jong, options);
        unJongSrc=BitmapFactory.decodeResource(res, R.drawable.f_unjong, options);

        for(int position=0;position<5;position++){
            jsonSortListArr[position] = setList(position, y+m, d);
        }
    }

    public void rightMove(View view) {
        int month = Integer.valueOf(m);
        d = "01";
        if(month!=12){
            month = month+1;
            if(String.valueOf(month).length()==1){
                m="0"+(month);
            }else{
                m=String.valueOf(month);
            }
        }else{
            m="01";
            y=String.valueOf(Integer.valueOf(y)+1);
        }
        tVYear.setTypeface(typeface);
        tVYear.setText(y);
        tVMonth.setTypeface(typeface);
        tVMonth.setText(m);

        for(int position=0;position<5;position++){
            jsonSortListArr[position] = setList(position, y+m, d);
            Frag_fictures.ca[position].notifyDataSetChanged();
        }
    }

    public void leftMove(View view) {
        int month = Integer.valueOf(m);
        d = "01";
        if(month!=1){
            month = month-1;
            if(String.valueOf(month).length()==1){
                m="0"+(month);
            }else{
                m=String.valueOf(month);
            }
        }else{
            m="12";
            y=String.valueOf(Integer.valueOf(y)-1);
        }
        tVYear.setTypeface(typeface);
        tVYear.setText(y);
        tVMonth.setTypeface(typeface);
        tVMonth.setText(m);

        for(int position=0;position<5;position++){
            jsonSortListArr[position] = setList(position, y+m, d);
            Frag_fictures.ca[position].notifyDataSetChanged();
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {


        private final String[] TITLES = { "프리미어","라리가","분데스","세리에","리그1" };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof Frag_fictures)
                return POSITION_NONE;
            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int position) {
            return Frag_fictures.newInstance(position);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fixtures, menu);
        menuItem_cal = menu.findItem(R.id.action_datePick);
        menuItem_jong = menu.findItem(R.id.action_jong);
        LinearLayout itemLayout = (LinearLayout) menu.findItem(R.id.action_datePick).getActionView();
        tVMonth = (TextView) itemLayout.findViewById(R.id.month_text);
        tVYear = (TextView) itemLayout.findViewById(R.id.year_text);
        tVYear.setTypeface(typeface);
        tVYear.setText(y);
        tVMonth.setTypeface(typeface);
        tVMonth.setText(m);

        LinearLayout itemLayout2 = (LinearLayout) menu.findItem(R.id.action_jong).getActionView();
        iVJong =  (ImageView) itemLayout2.findViewById(R.id.jong);
        if(isJong)
            iVJong.setImageBitmap(jongSrc);
        else
            iVJong.setImageBitmap(unJongSrc);

        menuItem_cal.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FicturesActivity.this.onOptionsItemSelected(menuItem_cal);
                FragmentManager fm = FicturesActivity.this.getSupportFragmentManager();
                DateTime now = DateTime.now();
                calendarDatePickerDialogFragment = CalendarDatePickerDialogFragment
                        .newInstance(onDateSetListener, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialogFragment.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        menuItem_jong.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isJong){
                    isJong=false;
                    iVJong.setImageBitmap(unJongSrc);
                }else{
                    isJong=true;
                    iVJong.setImageBitmap(jongSrc);
                }
                for(int position=0;position<5;position++){
                    jsonSortListArr[position] = setList(position, y+m, d);
                    Frag_fictures.ca[position].notifyDataSetChanged();
                }
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("Select","Select");
        if(item.getItemId() == R.id.action_datePick){
            Log.e("ItemSelect","ItemSelect");
            android.support.v4.app.FragmentManager fm = this.getSupportFragmentManager();
            DateTime now = DateTime.now();
            calendarDatePickerDialogFragment = CalendarDatePickerDialogFragment
                    .newInstance(onDateSetListener, now.getYear(), now.getMonthOfYear() - 1,
                            now.getDayOfMonth());
            calendarDatePickerDialogFragment.show(fm, FRAG_TAG_DATE_PICKER);
        }
        return super.onOptionsItemSelected(item);
    }*/

    CalendarDatePickerDialogFragment.OnDateSetListener onDateSetListener = new CalendarDatePickerDialogFragment.OnDateSetListener() {
        @Override
        public void onDateSet(CalendarDatePickerDialogFragment dialog, int i, int monthOfYear, int dayOfMonth) {
            int month = monthOfYear+1;
            if(String.valueOf(month).length() == 1) {
                m = "0"+String.valueOf(month);
            }else{
                m = String.valueOf(month);
            }
            y = String.valueOf(i);
            d = String.valueOf(dayOfMonth);

            tVYear.setTypeface(typeface);
            tVYear.setText(y);
            tVMonth.setTypeface(typeface);
            tVMonth.setText(m);
            //hyo
            for(int position=0;position<5;position++){
                jsonSortListArr[position] = setList(position, y+m, d);
                Frag_fictures.ca[position].notifyDataSetChanged();
            }

            //Frag_fictures.ca.notifyDataSetChanged();

//            for(int j=0;j <5;j++){
//                Log.e("wqdqwd", jsonSortList.get(j).length()+"");
//            }
        }
    };

    //hyo
    public List<JSONObject> setList(int position,String date,String day){
        scrollState=0;
        prefInfo= StaticPref.loadPref_prefInfo(this);
        Log.e("setList","position:"+position+"date:"+date);
        JSONArray array;
        TextView hiddenText=(TextView)findViewById(R.id.hiddenText);
        try {
            JSONObject leagueNum = new JSONObject("{'0':'premier','1':'laliga','2':'bundesliga','3':'serie','4':'ligue1'}");
            positionKey=leagueNum.get(String.valueOf(position))+"_"+date;
            hiddenText.setVisibility(View.GONE);
            if(newContacts.get(positionKey)!=null){
                array = (JSONArray)newContacts.get(positionKey);
                JSONObject userObj;
                resultArray = new JSONArray();
                int hFlagId,aFlagId;
                for(int i=0;i<array.length();i++)
                {
                    try {
                        userObj = array.getJSONObject(i);
                        int code=userObj.getInt("code");
                        String hFlagName=userObj.get("home").toString();
                        String aFlagName=userObj.get("away").toString();
                        try {
					        hFlagId = flag.list.get(0).get(hFlagName.toLowerCase());
                        } catch (NullPointerException e){
                            hFlagId = 0;
                            Log.e(hFlagName, "깃발 정보가 없어요!");
				        }
                        try {
                            aFlagId = flag.list.get(0).get(aFlagName.toLowerCase());
                        } catch (NullPointerException e){
                            aFlagId = 0;
                            Log.e(aFlagName, "깃발 정보가 없어요!");
                        }
                        userObj.put("hFlag", hFlagId);
                        userObj.put("aFlag", aFlagId);

                        if(prefInfo.containsKey(code)||!isJong){
                            resultArray.put(userObj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                jsonSortList = new ArrayList<JSONObject>();
                if(resultArray.length()==0){
                    isResultArray[position]=false;
                }else{
                    isResultArray[position]=true;
                }
                Log.e("jsonArraySize",resultArray.length()+"");
                for (int i = 0; i < resultArray.length(); i++) {
                    try {
                        jsonSortList.add(resultArray.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(jsonSortList, new Comparator<JSONObject>() {
                    //You can change "Name" with "ID" if you want to sort by ID

                    @Override
                    public int compare(JSONObject a, JSONObject b) {
                        JSONObject dateValA;
                        JSONObject dateValB;
                        int dayValA;
                        int dayValB;
                        Calendar calA=new GregorianCalendar();
                        Calendar calB=new GregorianCalendar();

                        try {
                            dateValA = (JSONObject) a.get("date");
                            //dayValA = dateValA.getInt("day");
                            String hourA = a.getString("time").substring(0, a.getString("time").indexOf(":"));
                            String minuteA = a.getString("time").substring(a.getString("time").indexOf(":") + 1, a.getString("time").length());
                            calA.set(dateValA.getInt("year"), dateValA.getInt("month") - 1, dateValA.getInt("day"), Integer.valueOf(hourA), Integer.valueOf(minuteA));
                            int compareDateA= Integer.valueOf(String.valueOf(calA.getTimeInMillis()).substring(0,9));
                            dateValB = (JSONObject) b.get("date");
                            //dayValB = dateValB.getInt("day");
                            String hourB = b.getString("time").substring(0, b.getString("time").indexOf(":"));
                            String minuteB = b.getString("time").substring(b.getString("time").indexOf(":") + 1, b.getString("time").length());
                            calB.set(dateValB.getInt("year"), dateValB.getInt("month") - 1, dateValB.getInt("day"), Integer.valueOf(hourB), Integer.valueOf(minuteB));
                            int compareDateB= Integer.valueOf(String.valueOf(calB.getTimeInMillis()).substring(0,9));
                            return Double.compare(compareDateA, compareDateB);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
            }else{
                Log.e("setlist","비었다. 비었다고 디스플레이해주자");
                jsonSortList = new ArrayList<JSONObject>();
                hiddenText.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        int dayObj=0;
        for(int y=0;y<jsonSortList.size();y++){
            JSONObject userObj = jsonSortList.get(y);
            try {
                JSONObject dateObj = (JSONObject) userObj.get("date");
                if(dayObj!=dateObj.getInt("day")){
                    if(dateObj.getInt("day")>=Integer.valueOf(d)){
                        Log.e("date:"+dayObj,"index:"+y);
                        Frag_fictures.scrollIndex[position]=y;
                        break;
                    }
                }
                if(y==jsonSortList.size()-1){
                    Frag_fictures.scrollIndex[position]=y;
                }
                dayObj = dateObj.getInt("day");
                /*if(dateObj.getString("day").equals(d)){
                    Log.e(dateObj.getString("day"), y + "");
                    break;
                }else{
                    while(true){
                        dayObj = dayObj+1;
                        if(dateObj.getInt("day")==dayObj){
                            Log.e(dateObj.getInt("day")+"", y + "");
                            break;
                        }else if(dayObj>=31){
                            Log.e("scrollStop", "scrollStop");
                            break;
                        }
                    }
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for(int i=0;i<5;i++){
            Frag_fictures.isLimit[i]=true;
        }

        return jsonSortList;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            firstX = ev.getX();
            firstY = ev.getY();
        }else if(ev.getAction()==MotionEvent.ACTION_MOVE){
            if(moveY>ev.getY()){
                scrollState=1;
            }else if(moveY<ev.getY()){
                scrollState=2;
            }
            moveY = ev.getY();
        }else if(ev.getAction()==MotionEvent.ACTION_UP){
            lastX = ev.getX();
            lastY = ev.getY();
//            Log.e("x::"+String.valueOf(firstX-lastX),"y::"+String.valueOf(firstY-lastY));
            if(firstY-lastY>45){
                rightBtn.setEnabled(false);
                leftBtn.setEnabled(false);
                rightBtn.setVisibility(View.INVISIBLE);
                leftBtn.setVisibility(View.INVISIBLE);
            }else if(firstY-lastY<-45){
                rightBtn.setEnabled(true);
                leftBtn.setEnabled(true);
                rightBtn.setVisibility(View.VISIBLE);
                leftBtn.setVisibility(View.VISIBLE);
            }else if(firstY-lastY>-45&&firstY-lastY<45&&(firstX-lastX<-65||firstX-lastX>65)){
                rightBtn.setEnabled(false);
                leftBtn.setEnabled(false);
                rightBtn.setVisibility(View.GONE);
                leftBtn.setVisibility(View.GONE);
            }
            else{
                Log.e("hold", "hold");
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}
