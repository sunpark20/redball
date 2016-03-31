

package hungry.redball.fixtures;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import hungry.redball.R;
import hungry.redball.aStatic.StaticMethod;
import hungry.redball.aStatic.StaticPref;
import hungry.redball.alram.AlarmReceiver;
import hungry.redball.alram.RepeatReceiver;
import hungry.redball.alram.model.PrefInfo;
import hungry.redball.matchRepo.ReportActivity;
import hungry.redball.mongo.GetContactsAsyncTask;
import hungry.redball.mongo.QueryBuilder;


public class Frag_fictures extends Fragment{

	private static final String ARG_POSITION = "position";
	private int position;
	private View view;
	private ListView listView;
	static public final CustomAdapter ca[]=new CustomAdapter[5];
	static public final boolean isLimit[]=new boolean[5];
	static public final int scrollIndex[]=new int[5];
	Bitmap onMSrc,unMSrc;
	//uk
	Map<Integer, PrefInfo> prefInfo;

	//팀한글로 바꾸기
	JSONObject eTokJson;

	public static Frag_fictures newInstance(int position) {
		Frag_fictures f = new Frag_fictures();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	public String[] reportLoad(String code) {
		HashMap<String,String> map = new HashMap();
		map.put("code", code);
		GetContactsAsyncTask task = new GetContactsAsyncTask(getActivity(), QueryBuilder.QueryKinde.matchResultQ,map);
		String matchResult;
		String[] resultArr=null;
		try {
			matchResult = task.execute().get();
			JSONArray contacts=new JSONArray(matchResult);
			JSONObject jObj=(JSONObject)contacts.get(0);
			resultArr =new String[]{jObj.getString("goalEve"),jObj.getString("hTeamRecord"),jObj.getString("aTeamRecord"),jObj.getString("hPlayerRecord"),jObj.getString("aPlayerRecord")};
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Resources res=getActivity().getResources();

		//팀한글로 바꾸기
		String eTok= StaticMethod.loadJSONFromAsset("teamEtoK.json", getActivity());
		try{
			eTokJson=new JSONObject(eTok);
		}catch (Exception e){
			e.printStackTrace();
		}


		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		onMSrc=BitmapFactory.decodeResource(res, R.drawable.f_jong, options);
		unMSrc=BitmapFactory.decodeResource(res, R.drawable.f_unjong, options);
		if (FicturesActivity.isOnce) {
			FicturesActivity.isOnce=false;
//			FicturesActivity.testCodeArr.put(959584,true);
//			FicturesActivity.testCodeArr.put(959662,true);
//			FicturesActivity.testCodeArr.put(959672,true);
		}
//		flag=new FlagHashMap(); //깃발 정보 불러오기
//		flag.makeHashMap();
		position = getArguments().getInt(ARG_POSITION);
		ca[position]=new CustomAdapter(getActivity());
//		FicturesActivity.setList(position, FicturesActivity.m);

//		teamName=new String[contacts.size()];//온클릭 리스너에서 팀이름 보여주기
//		readJsonFile();

		//외부 폰트
//		typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/customfont.ttf");
//		viewwww.setTypeface(typeface);

//		w=String.valueOf(Cal.get(Cal.WEEK_OF_YEAR));
//		Log.e("today",today.getTime().toString());
		//DateFormat 변경
//		SimpleDateFormat monthParse = new SimpleDateFormat("EEEE, MMM dd yyyy", Locale.ENGLISH);
//		today=monthParse.format(Cal.getTime());



//		try{
//			jarr=new Url_team_thread().execute(position).get();
//		}catch(Exception e){
//			e.printStackTrace();
//		}

		//관심팀 해쉬맵 로드
		prefInfo= StaticPref.loadPref_prefInfo(getActivity());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.e("onCreateView","onCreateView");
		setHasOptionsMenu(true);
		view = inflater.inflate(R.layout.fragment_fixtures, container, false);
		listView=(ListView) view.findViewById(R.id.listView);
		listView.setAdapter(ca[position]);
		return view;
	}

	/*private synchronized void readJsonFile(){
//		JSONParser parser  = new JSONParser();
		try {
//			String externalPath=null;
//			externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//			String dirName=getActivity().getPackageName();
//			File file=new File(externalPath+"/"+dirName);
//			String path=file+"/fi.json";
//			Object object = parser.parse(new FileReader(path));

			InputStream is = getActivity().getAssets().open("fi.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String json = new String(buffer, "UTF-8");
			JSONArray contacts=new JSONArray(json);
			JSONObject dateObj;
			for(int i=0;i<contacts.length();i++){
				JSONObject userObj=contacts.getJSONObject(i);
				dateObj=userObj.getJSONObject("date");
				String newKey = userObj.get("league").toString()+"_"+dateObj.get("month").toString();
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

//			getList("premier", "12");

			*//*Object o = com.mongodb.util.JSON.parse(json);
			BasicDBList contacts = (BasicDBList) o;
			DBObject dateObj;
			for (Object obj : contacts) {
				DBObject userObj = (DBObject) obj;
				dateObj=(DBObject)userObj.get("date");
				String newKey = userObj.get("league").toString()+"_"+dateObj.get("month").toString();
				if(map.containsKey(newKey)){
					BasicDBList classifiData = map.get(newKey);
					classifiData.add(obj);
					map.put(newKey, classifiData);
				}else{
					BasicDBList newClassifiData=new BasicDBList();
					newClassifiData.add(obj);
					map.put(newKey,newClassifiData);
				}
//				Log.e(newKey, newKey);
//				Log.e(m, dateObj.get("month").toString());
//				Log.e(d, dateObj.get("day").toString());
			}
			newContacts.putAll(map);*//*


		} catch(Exception e) {
			e.printStackTrace();
		}
	}*/

	/*private void loadFixtures(JSONArray jArr){
		int flagId;

		try{
			for (int i=0;i<jArr.length();i++) {
				JSONObject userObj = (JSONObject) jArr.get(i);
				Fixtures Fixtures=new Fixtures();
//				String flagName=userObj.get("league").toString();
//				try { // 깃발정보 다르면 이미지x
//					flagId = flag.list.get(0).get(flagName);
//				}catch (NullPointerException e){
//					flagId = 0;
//					Log.e("Frag_fictures", "깃발 정보가 없어요!");
//				}
//				Fixtures.setFlag(flagId);
				Fixtures.setDate(userObj.get("date").toString());
				Fixtures.setHome(userObj.get("home").toString());
				Fixtures.setAway(userObj.get("away").toString());
				Fixtures.setHscore(userObj.get("score").toString());
				Fixtures.setAscore(userObj.get("score").toString());

				rows.add(Fixtures);

//				MyContact temp = new MyContact();
//				temp.setDoc_id(userObj.get("_id").toString());
//				temp.setFirst_name(userObj.get("time").toString());
//				temp.setLast_name(userObj.get("aPlayerRecord").toString());
//				temp.setEmail(userObj.get("hTeamRecord").toString());
//				temp.setPhone(userObj.get("code").toString());

			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}*/

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
			return FicturesActivity.jsonSortListArr[position].size();
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
			LinearLayout rowLayout;
			LinearLayout dateTemp;
			ImageView hFlag;
			ImageView aFlag;
			ImageButton markFlag;
			TextView time;
			TextView date;
			TextView dateState;
			TextView space;
			TextView home;
			TextView away;
			TextView hscore;
			TextView ascore;
		}
		@Override
		public View getView(final int index, View convertView, ViewGroup parent) {
			Holder holder=null;
			final boolean isBtnOn;
			final boolean isResult;
			if (convertView == null) {
				holder=new Holder();
				convertView = inflater.inflate(R.layout.fragment_fixtures_row, parent, false);
				holder.rowLayout=(LinearLayout) convertView.findViewById(R.id.rowlayout);
				holder.dateTemp=(LinearLayout) convertView.findViewById(R.id.dateTemp);
				holder.hFlag=(ImageView) convertView.findViewById(R.id.hflag);
				holder.aFlag=(ImageView) convertView.findViewById(R.id.aflag);
				holder.markFlag=(ImageButton) convertView.findViewById(R.id.markFlag);
				holder.time=(TextView) convertView.findViewById(R.id.time);
				holder.space=(TextView) convertView.findViewById(R.id.space);
				holder.date=(TextView) convertView.findViewById(R.id.date);
				holder.dateState=(TextView) view.findViewById(R.id.dateState);
				holder.home=(TextView) convertView.findViewById(R.id.home);
				holder.away=(TextView) convertView.findViewById(R.id.away);
				holder.hscore=(TextView) convertView.findViewById(R.id.hscore);
				holder.ascore=(TextView) convertView.findViewById(R.id.ascore);

				convertView.setTag(holder);
			}else {
				holder = (Holder) convertView.getTag();
			}
			//resize (flag)
			Resources res=getActivity().getResources();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			if(isLimit[position]){
				isLimit[position]=false;
				listView.postDelayed(new Runnable() {
					@Override
					public void run() {
						listView.setSelection(scrollIndex[position]);
					}
				}, 100);
			}
			try {
				JSONObject fJObj = FicturesActivity.jsonSortListArr[position].get(0);
				try {
					if(FicturesActivity.scrollState==0){
						fJObj = FicturesActivity.jsonSortListArr[position].get(listView.getFirstVisiblePosition());
					}else if(FicturesActivity.scrollState==1){
						fJObj = FicturesActivity.jsonSortListArr[position].get(listView.getFirstVisiblePosition()+1);
					}else if(FicturesActivity.scrollState==2){
						fJObj = FicturesActivity.jsonSortListArr[position].get(listView.getFirstVisiblePosition()-1);
					}
				}catch (IndexOutOfBoundsException e){
					Log.e("Exception!!!","IndexOutOfBoundsException");
				}
				JSONObject fDateObj = (JSONObject)fJObj.get("date");
				Calendar fGregStartDate = new GregorianCalendar(fDateObj.getInt("year"),fDateObj.getInt("month")-1, fDateObj.getInt("day"));
				SimpleDateFormat fWeekParse = new SimpleDateFormat("EEE");
				String fWeek=fWeekParse.format(fGregStartDate.getTime());

				JSONObject jObj = FicturesActivity.jsonSortListArr[position].get(index);
				JSONObject beforeJObj = new JSONObject();
				if(index!=0)
					beforeJObj = FicturesActivity.jsonSortListArr[position].get(index-1);
				JSONObject dateObj = (JSONObject)jObj.get("date");
				Calendar gregStartDate = new GregorianCalendar(dateObj.getInt("year"),dateObj.getInt("month")-1, dateObj.getInt("day"));
				SimpleDateFormat weekParse = new SimpleDateFormat("EEE");
				String week=weekParse.format(gregStartDate.getTime());
				int hId=Integer.valueOf(jObj.get("hFlag").toString());
				Bitmap hSrc=BitmapFactory.decodeResource(res, hId, options);
				holder.hFlag.setImageBitmap(hSrc);
				int aId=Integer.valueOf(jObj.get("aFlag").toString());
				Bitmap aSrc=BitmapFactory.decodeResource(res, aId, options);
				holder.aFlag.setImageBitmap(aSrc);
				//날짜 중복 제거
				if(index!=0&&jObj.getString("date").equals(beforeJObj.getString("date"))) {
					holder.space.setVisibility(View.GONE);
					holder.date.setVisibility(View.GONE);
					holder.dateTemp.setBackgroundColor(getResources().getColor(R.color.transparent));
				}else{
					holder.space.setVisibility(View.VISIBLE);
					holder.space.setBackgroundColor(getResources().getColor(R.color.dateTemp));
					holder.dateTemp.setBackgroundColor(getResources().getColor(R.color.dateTemp));
					holder.date.setVisibility(View.VISIBLE);
					holder.date.setBackgroundColor(getResources().getColor(R.color.dateTemp));
					holder.date.setText(dateObj.getInt("month") + "/" + dateObj.getInt("day") + "(" + week + ")");
				}
				if(index==0){
					holder.space.setVisibility(View.GONE);
					holder.date.setVisibility(View.GONE);
					holder.dateTemp.setBackgroundColor(getResources().getColor(R.color.transparent));
				}
				holder.dateState.setText(fDateObj.getInt("month") + "/" + fDateObj.getInt("day") + "(" + fWeek + ")");
				//팀한글로 바꾸기
				holder.home.setText(eTokJson.get(jObj.get("home").toString()).toString());
				holder.away.setText(eTokJson.get(jObj.get("away").toString()).toString());
				String score = jObj.get("score").toString();
				if(score.indexOf(":")!=-1){
					holder.hscore.setText(score.substring(0,score.indexOf(":")).trim());
					holder.ascore.setText(score.substring(score.indexOf(":")+1, score.length()).trim());
					isResult=true;
				}
				else if(score.indexOf("-")!=-1){
					holder.hscore.setText(score.substring(0,score.indexOf("-")).trim());
					holder.ascore.setText(score.substring(score.indexOf("-")+1, score.length()).trim());
					isResult=true;
				}else{
					holder.hscore.setText("");
					holder.ascore.setText("");
					isResult=false;
				}

				final int code = FicturesActivity.jsonSortListArr[position].get(index).getInt("code");
				final String tempHome=jObj.get("home").toString();
				final String tempAway=jObj.get("away").toString();
				final String tempScore=jObj.get("score").toString();
				if(!isResult){
					holder.time.setText(jObj.getString("time"));
					holder.rowLayout.setEnabled(false);
					holder.markFlag.setVisibility(View.VISIBLE);

					final boolean isPressed;
					if(prefInfo.size()==0) {
						holder.markFlag.setImageBitmap(unMSrc);
						isPressed=false;
					}
					else if(prefInfo.containsKey(code)) {
						holder.markFlag.setImageBitmap(onMSrc);
						isPressed=true;
					}
					else {
						holder.markFlag.setImageBitmap(unMSrc);
						isPressed=false;
					}

					final Holder finalHolder = holder;


					holder.markFlag.setOnClickListener(new View.OnClickListener() {
						boolean tempP=isPressed;
						@Override
						public void onClick(View v) {
							Toast.makeText(getActivity(), code+"", Toast.LENGTH_SHORT).show();
							if(!tempP) {
								finalHolder.markFlag.setImageBitmap(onMSrc);
								//uk
								//관심팀 해쉬맵 로드
								prefInfo=StaticPref.loadPref_prefInfo(getActivity());
								PrefInfo p=new PrefInfo();
								p.sethTeam(tempHome);
								p.setaTeam(tempAway);
								p.setLove(true);
								prefInfo.put(code, p);
								//관심팀 해쉬맵 저장
								StaticPref.savePref_prefInfo(getActivity(), prefInfo);

								//알람 장전
								RepeatReceiver rr=new RepeatReceiver();
								rr.setTime();
								rr.setAlarmFromFixtures(getActivity(), code);
								tempP=!tempP;
							}else {
								finalHolder.markFlag.setImageBitmap(unMSrc);
								tempP=!tempP;
								//uk
								//관심팀 해쉬맵 로드
								prefInfo=StaticPref.loadPref_prefInfo(getActivity());
								prefInfo.remove(code);
								//관심팀 해쉬맵 저장
								StaticPref.savePref_prefInfo(getActivity(), prefInfo);

								//알람 캔슬
								//0알람과 1알람(code에 더해준 temppuls를 뺴줘야함) 캔슬
								AlarmReceiver AR=new AlarmReceiver();
								AR.cancelAlarm(getActivity(), code);
								int rCode=code+RepeatReceiver.TEMPPLUS;
								AR.cancelAlarm(getActivity(), rCode);
								Log.e("alarm", code + ": " + tempHome + tempAway + "는 캔슬됬습니다.");
							}
						}
					});
				}else{
					holder.time.setText("end");
					holder.rowLayout.setEnabled(true);
					holder.markFlag.setVisibility(View.GONE);
					holder.rowLayout.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(), ReportActivity.class);
							intent.putExtra("report",reportLoad(String.valueOf(code)));
							intent.putExtra("score",tempScore);
							intent.putExtra("hTeam",tempHome);
							intent.putExtra("aTeam",tempAway);
							startActivity(intent);
							Toast.makeText(getActivity(), code+"", Toast.LENGTH_SHORT).show();
						}
					});
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			//속성부여
//			if(position%2==0)
//				holder.row.setBackgroundColor(getResources().getColor(R.color.wweak));
//			else
//				holder.row.setBackgroundColor(getResources().getColor(R.color.white));
//			holder.row.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Toast.makeText(getActivity(),teamName[position],Toast.LENGTH_SHORT).show();
//				}
//			});
			return convertView;
		}

	}
}
