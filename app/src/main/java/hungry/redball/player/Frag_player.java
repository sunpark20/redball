package hungry.redball.player;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;

import hungry.redball.R;
import hungry.redball.aStatic.StaticMethod;
import hungry.redball.player.adapter.SpinnerAdapter;
import hungry.redball.player.model.Player;
import hungry.redball.player.util.SpinnerPositionSetting;
import hungry.redball.team.util.FlagHashMap;

public class Frag_player extends Fragment{
	private AppCompatSpinner spinner;
	//for the setStuff, findViewById
	private View view;
	//for the hide spinner in searchView
	private Menu mainMenu = null;
    //argument
	private static final String ARG_POSITION = "position";
	private int position;
    //listview
	private ListView listView;
	private ArrayList<Player> rows=new ArrayList<Player>();
	private ArrayList<Player> copiedRows = new ArrayList<Player>();
	private CustomAdapter ca;
	private FlagHashMap flag;
    //팀한글화하기
    JSONObject eTokJson;

	public static Frag_player newInstance(int position) {
		Frag_player f = new Frag_player();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);

        //팀한글화하기 파일읽는 부분, 그리고 어레이 만들때 넣어줌 (최초한번~)
        String eTok=StaticMethod.loadJSONFromAsset("teamEtoK.json", getActivity());
        try{
            eTokJson=new JSONObject(eTok);
        }catch (Exception e){
            e.printStackTrace();
        }
        //onlyOnce
        loadTeam();
        //for the searchView
        copiedRows.addAll(rows);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fragment_player, container, false);

		// if this is set true,
		// Activity.onCreateOptionsMenu will call Fragment.onCreateOptionsMenu
		// Activity.onOptionsItemSelected will call Fragment.onOptionsItemSelected
		setHasOptionsMenu(true);
		setStuff();
		return view;
	}




	private void setStuff(){
		ca = new CustomAdapter(getActivity());
		listView = (ListView) view.findViewById(R.id.playerListView);
		listView.setAdapter(ca);
	}

	private void loadTeam(){
        //깃발 정보 불러오기
        flag=new FlagHashMap();
        flag.makeHashMap();

        JSONObject childJSONObject=null;

		try{
			for (int i=0; i< StaticMethod.getJ(position).length(); i++){
				childJSONObject = StaticMethod.getJ(position).getJSONObject(i);
				Player p=new Player();
				//row1
				p.setR(String.valueOf(i + 1));
				String teamName=childJSONObject.getString("teamName");
				try { // 깃발정보 다르면 이미지x
					p.setFlag(flag.list.get(0).get(teamName.toLowerCase()));
				}catch (NullPointerException e){
					p.setFlag(0);
					Log.e("Frag_player","깃발 정보가 없어요!");
				}
				p.setName(childJSONObject.getString("name"));

				//row2
                //팀한글화하기
				p.setTeam(eTokJson.get(teamName).toString());

				p.setPosition(childJSONObject.getString("playedPositionsShort"));
				p.setHeight(childJSONObject.getString("height"));
				p.setWeight(childJSONObject.getString("weight"));
				p.setAge(childJSONObject.getString("age"));
				//row3(main)
				p.setGoals(childJSONObject.getString("goal"));
				p.setAssists(childJSONObject.getString("assistTotal"));
				p.setMotM(childJSONObject.getString("manOfTheMatch"));
				p.setApps(childJSONObject.getString("apps"));
				p.setYel(childJSONObject.getString("yellowCard").split("\\.")[0]);
				p.setRed(childJSONObject.getString("redCard").split("\\.")[0]);
				//offensive
				p.setSpG(getNumber(childJSONObject.getString("shotsPerGame")));
				p.setDrb(getNumber(childJSONObject.getString("dribbleWonPerGame")));
				p.setUnstch(getNumber(childJSONObject.getString("turnoverPerGame")));
				p.setOff(getNumber(childJSONObject.getString("offsideGivenPerGame")));
				p.setFouled(getNumber(childJSONObject.getString("foulGivenPerGame")));
				p.setDisp(getNumber(childJSONObject.getString("dispossessedPerGame")));
				//passing
				p.setCrosses(getNumber(childJSONObject.getString("accurateCrossesPerGame")));
				p.setAerialsWon(getNumber(childJSONObject.getString("aerialWonPerGame")));
 				p.setKeyP(getNumber(childJSONObject.getString("keyPassPerGame")));
				p.setAvgP(getNumber(childJSONObject.getString("totalPassesPerGame")));
				p.setPsR(getNumber(childJSONObject.getString("passSuccess")));
				p.setLongB(getNumber(childJSONObject.getString("accurateLongPassPerGame")));
				//defensive
				p.setTackles(getNumber(childJSONObject.getString("tacklePerGame")));
				p.setInter(getNumber(childJSONObject.getString("interceptionPerGame")));
				p.setBlock(getNumber(childJSONObject.getString("outfielderBlockPerGame")));
				p.setFouls(getNumber(childJSONObject.getString("foulsPerGame")));
				p.setClear(getNumber(childJSONObject.getString("clearancePerGame")));
				p.setOwnG(getNumber(childJSONObject.getString("goalOwn")));

				rows.add(p);
			}
            //최초로 만든 후에  골로 정렬해놓기.
            SpinnerPositionSetting sps = new SpinnerPositionSetting();
            sps.set(rows, 0);

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	//get x.x or xx.x  from x.xxxxxxxx
	//sometimes format not match -> ArrayIndexOutOfBoundsException  $solved:if(a.length()<4)
	private String getNumber(String a){
		if(a.length()<4)
			return a;
		else
			return a.split("\\.")[0]+"."+a.split("\\.")[1].substring(0,1);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	private class CustomAdapter extends BaseAdapter {
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

        public class Holder{
            //row1
             TextView r;
             ImageView flag;
             TextView name;
            //row2
             TextView team;
             TextView position;
             TextView weight;
             TextView height;
             TextView age;
            //row3 (main)
             TextView goals;
             TextView assists;
             TextView motM;
             TextView apps;
             TextView yel;
             TextView red;
            //offensive
             TextView spG;
             TextView drb;
             TextView unstch;
             TextView off;
             TextView fouled;
             TextView disp;
            //passing
             TextView crosses;
             TextView aerialsWon;
             TextView keyP;
             TextView avgP;
             TextView psR;
             TextView longB;
            //defensive
             TextView tackles;
             TextView inter;
             TextView block;
             TextView fouls;
             TextView clear;
             TextView ownG;

             LinearLayout container;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder=null;

            if (convertView == null) {
                holder=new Holder();
                convertView = inflater.inflate(R.layout.fragment_player_row, parent, false);
                //row1
                holder.r = (TextView) convertView.findViewById(R.id.R);
                holder.flag = (ImageView) convertView.findViewById(R.id.flag);
                holder.name = (TextView) convertView.findViewById(R.id.Name);
                //row2
                holder.team = (TextView) convertView.findViewById(R.id.Team);
                holder.position = (TextView) convertView.findViewById(R.id.Position);
                holder.height = (TextView) convertView.findViewById(R.id.Height);
                holder.weight = (TextView) convertView.findViewById(R.id.Weight);
                holder.age = (TextView) convertView.findViewById(R.id.Age);
                //row3(main)
                holder.goals = (TextView) convertView.findViewById(R.id.Goals);
                holder.assists = (TextView) convertView.findViewById(R.id.Assists);
                holder.motM = (TextView) convertView.findViewById(R.id.MotM);
                holder.apps = (TextView) convertView.findViewById(R.id.Apps);
                holder.yel = (TextView) convertView.findViewById(R.id.Yel);
                holder.red = (TextView) convertView.findViewById(R.id.Red);
                //offensive
                holder.spG = (TextView) convertView.findViewById(R.id.spG);
                holder.drb = (TextView) convertView.findViewById(R.id.drb);
                holder.unstch = (TextView) convertView.findViewById(R.id.unstch);
                holder.off = (TextView) convertView.findViewById(R.id.off);
                holder.fouled = (TextView) convertView.findViewById(R.id.fouled);
                holder.disp = (TextView) convertView.findViewById(R.id.disp);
                //passing
                holder.crosses = (TextView) convertView.findViewById(R.id.crosses);
                holder.aerialsWon = (TextView) convertView.findViewById(R.id.aerialsWon);
                holder.keyP = (TextView) convertView.findViewById(R.id.keyP);
                holder.avgP = (TextView) convertView.findViewById(R.id.avgP);
                holder.psR = (TextView) convertView.findViewById(R.id.psR);
                holder.longB = (TextView) convertView.findViewById(R.id.longB);
                //defensive
                holder.tackles = (TextView) convertView.findViewById(R.id.tackles);
                holder.inter = (TextView) convertView.findViewById(R.id.inter);
                holder.block = (TextView) convertView.findViewById(R.id.block);
                holder.fouls = (TextView) convertView.findViewById(R.id.fouls);
                holder.clear = (TextView) convertView.findViewById(R.id.clear);
                holder.ownG = (TextView) convertView.findViewById(R.id.ownG);

                holder.container = (LinearLayout) convertView.findViewById(R.id.container);
                convertView.setTag(holder);
            }else {
                holder = (Holder) convertView.getTag();
            }

            //row1
            //스피너의 정렬 따라 1,2,3,4 붙도록  (순위 개념)
            holder.r.setText(position + 1 + "");

            //resize (flag)
            Resources res=context.getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            int id=rows.get(position).getFlag();
            Bitmap src=BitmapFactory.decodeResource(res, id, options);
            //바꾸는 중
            holder.flag.setImageBitmap(src);

            holder.name.setText(rows.get(position).getName());
            //row2
            holder.team.setText(rows.get(position).getTeam());
            holder.position.setText(rows.get(position).getPosition());
            holder.height.setText(rows.get(position).getHeight());
            holder.weight.setText(rows.get(position).getWeight());
            holder.age.setText(rows.get(position).getAge());
            //row3(main)
            holder.goals.setText(rows.get(position).getGoals());
            holder.assists.setText(rows.get(position).getAssists());
            holder.motM.setText(rows.get(position).getMotM());
            holder.apps.setText(rows.get(position).getApps());
            holder.yel.setText(rows.get(position).getYel());
            holder.red.setText(rows.get(position).getRed());
            //offensive
            holder.spG.setText(rows.get(position).getSpG());
            holder.drb.setText(rows.get(position).getDrb());
            holder.unstch.setText(rows.get(position).getUnstch());
            holder.off.setText(rows.get(position).getOff());
            holder.fouled.setText(rows.get(position).getFouled());
            holder.disp.setText(rows.get(position).getDisp());
            //passing
            holder.crosses.setText(rows.get(position).getCrosses());
            holder.aerialsWon.setText(rows.get(position).getAerialsWon());
            holder.keyP.setText(rows.get(position).getKeyP());
            holder.avgP.setText(rows.get(position).getAvgP());
            holder.psR.setText(rows.get(position).getPsR());
            holder.longB.setText(rows.get(position).getLongB());
            //defensive
            holder.tackles.setText(rows.get(position).getTackles());
            holder.inter.setText(rows.get(position).getInter());
            holder.block.setText(rows.get(position).getBlock());
            holder.fouls.setText(rows.get(position).getFouls());
            holder.clear.setText(rows.get(position).getClear());
            holder.ownG.setText(rows.get(position).getOwnG());

            return convertView;
        }

        // Filter Class
        public void filter(CharSequence charText) {
            rows.clear();
            if (charText.length() == 0) {
                rows.addAll(copiedRows);
            } else {
                for (Player p : copiedRows) {
                    if (StringUtils.containsIgnoreCase(p.getName(), charText) ||
                            StringUtils.containsIgnoreCase(p.getTeam(), charText) ||
                            StringUtils.containsIgnoreCase(p.getPosition(), charText)){
                        rows.add(p);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }



    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_player, menu);

		//for the hide
		mainMenu = menu;

		//searchView
		MenuItem searchViewItem2 = menu.findItem(R.id.action_search);
		SearchView sv = (SearchView)menu.findItem(R.id.action_search).getActionView();
        sv.setQueryHint("이름, 팀, 포지션 검색가능");
        sv.onActionViewCollapsed();

		//listener
		sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				ca.filter(newText);
				return false;
			}
		});

		//when expanded searchView, hide otherView(spinner)
		MenuItemCompat.setOnActionExpandListener(searchViewItem2, new MenuItemCompat.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				if (mainMenu != null)
					mainMenu.findItem(R.id.action_spinner).setVisible(true);
				return true;  // Return true to collapse action view
			}
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				if (mainMenu != null)
					mainMenu.findItem(R.id.action_spinner).setVisible(false);
				return true;  // Return true to expand action view
			}
		});

		//spinner
		MenuItem spinnerItem = menu.findItem(R.id.action_spinner);
		spinner = (AppCompatSpinner) spinnerItem.getActionView();
		//set the adapter
		String[] itemList=this.getResources().getStringArray(R.array.spinnerList);
		SpinnerAdapter adapter = new SpinnerAdapter(getActivity(),
				android.R.layout.simple_spinner_item, itemList);
		spinner.setAdapter(adapter);

        //프레그먼트 바뀔때마다 스피너 초기화시키기
        //참고로 searchview 는 지혼자 초기화됩니다. (그렇게 설정이 되있는듯)
		final AppCompatSpinner fSpinner=spinner;
		spinner.post(new Runnable() { //프레그먼트 바뀔때 왜 스피너가 안바뀌는지요
			@Override
			public void run() {
				fSpinner.setSelection(0);
			}
		});

		//listener
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        SpinnerPositionSetting sps = new SpinnerPositionSetting();
                        sps.set(rows, position);
                        ca.notifyDataSetChanged();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        StaticMethod.fToast(getActivity(), "로딩중입니다.");
                    }
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}



}
//골랐을 떄 동작하는 것 일단 나둡시당 안쓰지만 ㅇ리단 주석
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch(item.getItemId()) {
//
//			case R.id.spinner :
//				AppCompatSpinner spinner= new AppCompatSpinner(((PlayerActivity) getActivity()).getSupportActionBar().getThemedContext());
//				MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
//				MenuItemCompat.setActionView(item, spinner);
//
//				String[] itemList=this.getResources().getStringArray(R.array.spinnerList);
//				SpinnerAdapter adapter = new SpinnerAdapter(getActivity(),
//						android.R.layout.simple_spinner_item, itemList);
//				spinner.setAdapter(adapter);
//
////				spinner.setAdapter( ArrayAdapter.createFromResource(getActivity(),
////								R.array.spinnerList,
////								android.R.layout.simple_spinner_dropdown_item)
////				);
//				spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//					@Override
//					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//						SpinnerPositionSetting sps = new SpinnerPositionSetting();
//						sps.set(rows, position);
//						ca.notifyDataSetChanged();
//					}
//
//					@Override
//					public void onNothingSelected(AdapterView<?> parent) {
//					}
//				});
//				break;
//		}
//		return super.onOptionsItemSelected(item);
//	}
