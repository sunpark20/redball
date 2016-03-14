package hungry.redball.alram.url;


import android.net.Uri;

public class QueryBuilder_total_example {

	public String getDatabaseName() {
		return "heroku_mlmp0b8g";
	}

	public String getApiKey() {
		return "VF3gZziKIsaA2A4kBfKI9V2IYUlZVXq1";
	}

	public String getBaseUrl() {
		return "https://api.mongolab.com/api/1/databases/"+getDatabaseName()+"/collections/";
	}

	public String docApiKeyUrl()
	{
		return "l=2000&apiKey="+getApiKey();
	}

	public String documentRequest()
	{
		return "conn?";
	}

	public String buildContactsGetURL()
	{
		return getBaseUrl()+documentRequest()+f()+docApiKeyUrl();
	}
	public String q1(){
		return "q=";
	}
	public String q2(){
		return Uri.encode("{\"date.month\":\"12\"}");
	}
	public String q3(){
		return "&";
	}
	public String q(){
		return q1()+q2()+q3();
	}
	public String f1(){
		return "f=";
	}
	public String f2(){
		return Uri.encode("{\"aPlayerRecord\":0, \"hPlayerRecord\":0, \"aTeamRecord\":0, \"hTeamRecord\":0}");
	}
	public String f3(){
		return "&";
	}
	public String f(){
		return f1()+f2()+f3();
	}

}
