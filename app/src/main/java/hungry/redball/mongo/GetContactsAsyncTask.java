package hungry.redball.mongo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mongodb.BasicDBObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import hungry.redball.mongo.QueryBuilder.QueryKinde;

/**
 * Async Task to retrieve your stored contacts from mongolab
 * @author KYAZZE MICHAEL
 *
 */
public class GetContactsAsyncTask extends AsyncTask<String , Void, String> {
	private BasicDBObject user = null;
	private String OriginalObject = "";
	private String server_output = null;
	private String temp_output = null;
	private HashMap<String,String> queryValue = null;
	private Context context;
	private QueryKinde queryKinde;
	public GetContactsAsyncTask(Context context,QueryKinde queryKinde,HashMap<String,String>... queryValue){
		this.context=context;
		this.queryKinde=queryKinde;
		if(queryValue.length!=0){
			this.queryValue=queryValue[0];
		}
	}

	@Override
	protected String doInBackground(String... arg0) {
		
		ArrayList<MyContact> mycontacts = new ArrayList<MyContact>();
		String mongoarray = null;
		try
		{
			QueryBuilder qb = new QueryBuilder();
//			queryValue=new HashMap<String,String>();
//			queryValue.put("date.month","12");
//			queryValue.put("league","premier");
	        URL url = new URL(qb.buildContactsGetURL(queryKinde,queryValue));
			Log.e("url",url.toString());
	        HttpURLConnection conn = (HttpURLConnection) url
					.openConnection();
	        conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			while ((temp_output = br.readLine()) != null) {
				server_output = temp_output;
			}

            // create a basic db list
			mongoarray = server_output;
//			Object o = com.mongodb.util.JSON.parse(mongoarray);
//			BasicDBList contacts = (BasicDBList) o;
//		  for (Object obj : contacts) {
//			  DBObject userObj = (DBObject) obj;
//
//			  MyContact temp = new MyContact();
//			  temp.setDoc_id(userObj.get("_id").toString());
//			  temp.setFirst_name(userObj.get("time").toString());
//			  temp.setLast_name(userObj.get("aPlayerRecord").toString());
//			  temp.setEmail(userObj.get("hTeamRecord").toString());
//			  temp.setPhone(userObj.get("code").toString());
//			  mycontacts.add(temp);
//
//		  }

		  /*//json file make
			Log.e("aaaa","aaaa");
			String state= Environment.getExternalStorageState();
			String externalPath=null;
			if(state.equals(Environment.MEDIA_MOUNTED)){
				externalPath = Environment.getExternalStorageDirectory()
						.getAbsolutePath();
			}else if(state.equals(Environment.MEDIA_UNMOUNTED)){
				Toast.makeText(context, "MEDIA_UNMOUNTED", Toast.LENGTH_SHORT).show();
			}else if(state.equals(Environment.MEDIA_UNMOUNTABLE)){
				Toast.makeText(context, "MEDIA_UNMOUNTABLE", Toast.LENGTH_SHORT).show();
			}
			String dirName=context.getPackageName();
			File file=new File(externalPath+"/"+dirName);
			if(file.exists()==true) {
				File jsonFile=new File(externalPath+"/"+dirName+ "/fi.json");
				if(jsonFile.exists()==true) {
					Log.e("33","33");
				}else{
					Log.e("22","22");
					String path=externalPath+"/"+dirName;
					PrintWriter writer = new PrintWriter(path+"/fi.json");
					writer.print((Object) server_output);
					writer.close();
				}
			} else {
				Log.e("11","11");
				file.mkdir();
				String path=externalPath+"/"+dirName;
				PrintWriter writer = new PrintWriter(path+"/fi.json");
				writer.print((Object) server_output);
				writer.close();
			}//json file make(end)*/


		}catch (Exception e) {
			e.getMessage();
		}
		
		return mongoarray;
	}
}
