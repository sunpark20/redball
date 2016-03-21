/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hungry.redball.team;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import hungry.redball.R;
import hungry.redball.aStatic.StaticMethod;
import hungry.redball.team.model.Team;
import hungry.redball.team.util.FlagHashMap;

public class Frag_team extends Fragment {
	//for the setStuff, findViewById
	View view;
	private static final String ARG_POSITION = "position";
	private int position;

	private ListView listView;
	private ArrayList<Team> rows=new ArrayList<Team>();
	private CustomAdapter ca;

	String[] teamName;//온클릭 리스너에서 팀이름 보여주기

	FlagHashMap flag;

	public static Frag_team newInstance(int position) {
		Frag_team f = new Frag_team();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
		flag=new FlagHashMap(); //깃발 정보 불러오기
		flag.makeHashMap();

		// java.lang.NullPointerException이 떠서 일단 넣어놈
		try{
			teamName=new String[StaticMethod.jArr_team[position].length()];//온클릭 리스너에서 팀이름 보여주기
			loadTeam(); //팀정보 불러오기
		}catch (Exception e){
			e.printStackTrace();
		}
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		view = inflater.inflate(R.layout.fragment_team, container, false);

		ca=new CustomAdapter(getActivity());
		listView=(ListView) view.findViewById(R.id.listView);
		listView.setAdapter(ca);

		return view;
	}


	private void loadTeam(){
		int flagId;
		try{
			for(int i=0;i<StaticMethod.jArr_team[position].length();i++){
				JSONObject childJson=StaticMethod.jArr_team[position].getJSONObject(i);
				Team team=new Team();
				String flagName=childJson.get("팀명").toString();
				try { // 깃발정보 다르면 이미지x
					flagId = flag.list.get(0).get(flagName);
				}catch (NullPointerException e){
					flagId = 0;
					Log.e("Frag_team", "깃발 정보가 없어요!");
				}
				team.setFlag(flagId);
				team.setRank(childJson.get("순위").toString());
				team.setApp(childJson.get("경기수").toString());
				team.setWin(childJson.get("승").toString());
				team.setDraw(childJson.get("무").toString());
				team.setLose(childJson.get("패").toString());
				team.setGoal(childJson.get("득점").toString());
				team.setMinusGoal(childJson.get("실점").toString());
				team.setCompareGG(childJson.get("득실차").toString());
				team.setWinScore(childJson.get("승점").toString());

				rows.add(team);
				//온클릭 리스너에서 팀이름 보여주기
				teamName[i]=flagName;
			}
		}catch (Exception e){
			e.printStackTrace();
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
			LinearLayout row;
			ImageView flag;
			TextView rank;
			TextView app;
			TextView win;
			TextView draw;
			TextView lose;
			TextView goal;
			TextView minusGoal;
			TextView compareGG;
			TextView winScore;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder=null;

			if (convertView == null) {
				holder=new Holder();
				convertView = inflater.inflate(R.layout.fragment_team_row, parent, false);
				holder.row=(LinearLayout) convertView.findViewById(R.id.row);
				holder.flag=(ImageView) convertView.findViewById(R.id.flag);
				holder.rank=(TextView) convertView.findViewById(R.id.rank);
				holder.app=(TextView) convertView.findViewById(R.id.app);
				holder.win=(TextView) convertView.findViewById(R.id.win);
				holder.draw=(TextView) convertView.findViewById(R.id.draw);
				holder.lose=(TextView) convertView.findViewById(R.id.lose);
				holder.goal=(TextView) convertView.findViewById(R.id.goal);
				holder.minusGoal=(TextView) convertView.findViewById(R.id.minusGoal);
				holder.compareGG=(TextView) convertView.findViewById(R.id.compareGG);
				holder.winScore=(TextView) convertView.findViewById(R.id.winScore);
				convertView.setTag(holder);
			}else {
				holder = (Holder) convertView.getTag();
			}

			//resize (flag)
			Resources res=getActivity().getResources();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			int id=rows.get(position).getFlag();
			Bitmap src=BitmapFactory.decodeResource(res, id, options);
			holder.flag.setImageBitmap(src);

			holder.rank.setText(rows.get(position).getRank());
			holder.app.setText(rows.get(position).getApp());
			holder.win.setText(rows.get(position).getWin());
			holder.draw.setText(rows.get(position).getDraw());
			holder.lose.setText(rows.get(position).getLose());
			holder.goal.setText(rows.get(position).getGoal());
			holder.minusGoal.setText(rows.get(position).getMinusGoal());
			holder.compareGG.setText(rows.get(position).getCompareGG());
			holder.winScore.setText(rows.get(position).getWinScore());

			//속성부여
			if(position%2==0)
				holder.row.setBackgroundColor(getResources().getColor(R.color.wweak));
			else
				holder.row.setBackgroundColor(getResources().getColor(R.color.white));
			holder.row.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(),teamName[position],Toast.LENGTH_SHORT).show();
				}
			});

			return convertView;
		}
	}
}