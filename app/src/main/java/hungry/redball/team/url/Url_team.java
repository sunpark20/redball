package hungry.redball.team.url;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * stateID
 pre 12496
 serie 12770
 laliga 12647
 bundes 12559
 league1 12501
 */

public class Url_team {
    public static String[] NAVER_LEAGUE=new String[5];
    int num;

    final int LENGTH=10;
    JSONArray cell;

    public Url_team(int num){
        this.num=num;
        NAVER_LEAGUE[0]="premier";
        NAVER_LEAGUE[1]="primera";
        NAVER_LEAGUE[2]="bundesliga";
        NAVER_LEAGUE[3]="seria";
        NAVER_LEAGUE[4]="ligue1";
    }

    public JSONArray getUrlContent() throws Exception {
        int count=0;
        String strUrl="Http://sports.news.naver.com/sports/index.nhn?category=worldfootball&ctg=record&tab="
                +NAVER_LEAGUE[num];
        URL url = new URL(strUrl);
        HttpURLConnection conn = getConnection(url);
        String headerType = conn.getContentType();
        BufferedReader in;
//        if (headerType.toUpperCase().indexOf("UTF-8") != -1){
//            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
//        } else {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"EUC-KR"));
        //}

        StringBuffer sb = new StringBuffer();
        String thisLine = null;
        while( (thisLine = in.readLine())!=null ){
            sb.append(thisLine);
            sb.append("\n");
        }
        in.close();

        Document doc = Jsoup.parse(sb.toString());
        Elements rows = doc.select("table.tboxj tbody tr");
        String[] names = new String[LENGTH];

        cell = new JSONArray();
        for (Element row : rows) {
            JSONObject jsonObject = new JSONObject();

            Iterator<Element> iterElem = row.getElementsByTag("td").iterator();
            if(count==0){
                for (String name : names) {
                    names[count]=iterElem.next().text();
                    count++;
                }
            }else{
                for (String name : names) {
                    try{
                        jsonObject.put(name, iterElem.next().text());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                cell.put(jsonObject);
            }
        }

        //print list
//        for(int i=0;i<cell.length();i++){
//            try{
//                System.out.println(cell.get(i).toString());
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }

        Log.e("팀이 몇개일까", cell.length() + "");
        return cell;
    }
    private HttpURLConnection getConnection(URL entries) throws Exception{
        final int RETRY_DELAY_MS=3000;
        final int RETRIES=2;
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                Thread.sleep(RETRY_DELAY_MS);
            }
            HttpURLConnection connection = (HttpURLConnection)entries.openConnection();
            connection.setReadTimeout(20000 /* milliseconds */);
            connection.setConnectTimeout(30000 /* milliseconds */);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    Log.e("Url_team", entries + " **OK**");
                    return connection; // **EXIT POINT** fine, go on
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    Log.e("Url_team", entries + " **gateway timeout**");
                    break;// retry
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    Log.e("Url_team", entries + "**unavailable**");
                    break;// retry, server is unstable
                default:
                    Log.e("Url_team", entries + " **unknown response code**.");
                    break; // abort
            }
            // we did not succeed with connection (or we would have returned the connection).
            connection.disconnect();
            // retry
            retry++;
            Log.e("url_getHeader", "Failed retry " + retry + "/" + RETRIES);
            delay = true;

        } while (retry < RETRIES);
        Log.e("url_getHeader", "Aborting download of dataset.");
        return null;
    }


}
