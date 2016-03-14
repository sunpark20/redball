package hungry.redball.player.url;


import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by soy on 2015-12-20.
 */
public class Url_getHeader {
    String strUrl;
    public Url_getHeader(String strUrl){
        this.strUrl=strUrl;
    }
    public String getUrlContent() throws Exception{

        int count=0;
        URL url = new URL(strUrl);

        // HttpURLConnection 객체 생성.
        HttpsURLConnection conn = getConnection(url);

        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));

        StringBuffer sb = new StringBuffer();
        String thisLine = null;
        while( (thisLine = in.readLine())!=null ){
            sb.append(thisLine);
            sb.append("\n");
        }
        in.close();

        Document doc = Jsoup.parse(sb.toString());
        Element doc2=doc.head();
        //받은 head 전체를 볼때.. $모르겠음: lastkey는 안담겨 있는데 밑에서찾긴찾네. tostring 으로 바꾸면 안보이나??
//        Log.e("url_getHeader", doc2.text());

        String doc22=doc2.toString();
//        Log.e("dd", doc22);

        String word = "Model-Last-Mode";
        int index=doc22.lastIndexOf(word); // model-last-mode
        count=0;
        String key="";
        while(true){
            key+=doc22.charAt(index+count);
            if(count==100)
                break;
            count++;
        }
        key=key.split("'")[2];
        Log.e("url_getHeader", key);
        return key;
    }
    private HttpsURLConnection getConnection(URL entries) throws Exception{
        final int RETRY_DELAY_MS=5500;
        final int RETRIES=4;
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                Thread.sleep(RETRY_DELAY_MS);
            }

            HttpsURLConnection connection = (HttpsURLConnection) entries.openConnection();

            SSLSocketFactoryEx factory = new SSLSocketFactoryEx();
            connection.setSSLSocketFactory(factory);
            connection.setRequestProperty("charset", "utf-8");

            //HttpURLConnection connection = (HttpURLConnection)entries.openConnection();
            connection.setReadTimeout(24000 /* milliseconds */);
            connection.setConnectTimeout(30000 /* milliseconds */);

            connection.setRequestProperty("Host", "www.whoscored.com");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");
            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    Log.e("url_getHeader", entries + " **OK**");
                    return connection; // **EXIT POINT** fine, go on
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    Log.e("url_getHeader", entries + " **gateway timeout**");
                    break;// retry
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    Log.e("url_getHeader", entries + "**unavailable**");
                    break;// retry, server is unstable
                default:
                    Log.e("url_getHeader", entries + " **unknown response code**.");
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
