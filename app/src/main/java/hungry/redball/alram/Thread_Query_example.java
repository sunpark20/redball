package hungry.redball.alram;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Async Task to retrieve your stored contacts from mongolab
 * @author KYAZZE MICHAEL
 *
 */
public class Thread_Query_example extends AsyncTask<String, Void, String> {
	private final String TAG="Thread_mdb";
	static String server_output = null;
	static String temp_output = null;
	Context c;
	public Thread_Query_example(Context c){
		this.c=c;
	}

	@Override
	protected String doInBackground(String... arg0) {

		try 
		{
			QueryBuilder_total qb = new QueryBuilder_total();
	        URL url = new URL(qb.buildContactsGetURL());
			Log.e(TAG, url.toString());
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
			Log.e(TAG, server_output);

		}catch (Exception e) {
			e.printStackTrace();
		}
//		파일저장하는건데요 지금 안써요.
//		Static.savePref_String(c, server_output, "matchInfo_total");
//		try{
//			JSONArray ja=new JSONArray(server_output.toString());
//			Log.e("d1", ja.length() + ja.toString());
//			Static.saveFile(c, ja);
//
//
//		}catch (Exception e){
//			e.printStackTrace();
//		}

		return server_output;
	}
}
