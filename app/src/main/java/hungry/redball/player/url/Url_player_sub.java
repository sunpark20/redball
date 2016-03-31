package hungry.redball.player.url;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * stateID
 pre 12496
 serie 12770
 laliga 12647
 bundes 12559
 league1 12501
 */

public class Url_player_sub {

    public final static String PRE="12496";
    public final static String LALIGA="12647";
    public final static String BUNDES="12559";
    public final static String SERIE="12770";
    public final static String LIGUE1="12501";

    public final static String[] Referer={
        "England-Premier-League-2015-2016",
        "Https://www.whoscored.com/Regions/206/Tournaments/4/Seasons/5933/Stages/12647/PlayerStatistics/Spain-La-Liga-2015-2016",
        "Https://www.whoscored.com/Regions/81/Tournaments/3/Seasons/5870/Stages/12559/PlayerStatistics/Germany-Bundesliga-2015-2016",
        "Https://www.whoscored.com/Regions/108/Tournaments/5/Seasons/5970/Stages/12770/PlayerStatistics/Italy-Serie-A-2015-2016",
        "Https://www.whoscored.com/Regions/206/Tournaments/4/Seasons/5933/Stages/12647/PlayerStatistics/Spain-La-Liga-2015-2016"
    };
    static String[] last_model_key=new String[5];

    String code;
    String stageId;
    int num;
    String re;

    public Url_player_sub(int num, String code, String stageId){
        this.num=num;
        this.code=code;
        this.stageId=stageId;
    }

    public String readStat()throws Exception{
        String data=null;
        InputStream is = null;

        String category="summary";
        String numberOfPlayersToPick="all";

        String strUrl="https://www.whoscored.com/StatisticsFeed/1/GetPlayerStatistics?category="+
                category+
                "&subcategory="+"&statsAccumulationType=0&isCurrent=true&playerId=&teamIds=&matchId=&stageId="+
                stageId+"&tournamentOptions=2&sortBy=Rating&sortAscending=&age=&ageComparisonType=&appearances=&appearancesComparisonType=&field=Overall&nationality=&positionOptions=&timeOfTheGameEnd=&timeOfTheGameStart=&isMinApp=true&page=3&includeZeroValues=&numberOfPlayersToPick=" +
                numberOfPlayersToPick;
        URL url = new URL(strUrl);
        HttpsURLConnection conn = getConnection(url);
        try {
            is = new BufferedInputStream(conn.getInputStream());
            data = readIt(is);
            //데이터받은것 확인
            Log.e("Url_player_subcate", "///" + data);
        }finally {
            if(is!=null)
                is.close();
        }
        return data;
    }
    private String readIt(InputStream stream) throws IOException {

        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        for(String line = reader.readLine(); line != null; line = reader.readLine())
            builder.append(line);
        reader.close();
        return builder.toString();
    }
    private HttpsURLConnection getConnection(URL entries) throws Exception{
        final int RETRY_DELAY_MS=3000;
        final int RETRIES=2;
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                Thread.sleep(RETRY_DELAY_MS);
            }
            HttpsURLConnection connection = (HttpsURLConnection)entries.openConnection();
            SSLSocketFactoryEx factory = new SSLSocketFactoryEx();
            connection.setSSLSocketFactory(factory);
            connection.setRequestProperty("charset", "utf-8");
            connection.setReadTimeout(60000 /* milliseconds */);
            connection.setConnectTimeout(60000 /* milliseconds */);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            connection.setRequestProperty("Host", "www.whoscored.com");
            connection.setRequestProperty("Referer", Referer[num]);
            connection.setRequestProperty("Model-Last-Mode", Url_player_sub.last_model_key[num]);
            //Log.e("Url_player_sub", "이 키를 사용" + url_player_sub.last_model_key[num]);
            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    Log.e("url_player_sub", entries + " **OK**");
                    return connection; // **EXIT POINT** fine, go on
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    Log.e("url_player_sub", entries + " **gateway timeout**");
                    break;// retry
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    Log.e("url_player_sub", entries + "**unavailable**");
                    break;// retry, server is unstable
                default:
                    Log.e("url_player_sub", entries + " **unknown response code**.");
                    break; // abort
            }
            // we did not succeed with connection (or we would have returned the connection).
            connection.disconnect();
            // retry
            retry++;
            Log.e("url_player_sub", "Failed retry " + retry + "/" + RETRIES);
            delay = true;

        } while (retry < RETRIES);
        Log.e("url_getHeader", "Aborting download of dataset.");
        return null;
    }
}
