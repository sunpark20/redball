package hungry.redball.mongo;

import java.util.HashMap;

public class QueryBuilder {

	static public String query;

	public enum QueryKinde{
		monthFinderQ,basicQ,scoreQ,dateTimeFinderQ,matchResultQ
	}

	public String getDatabaseName() {
		return "sunpark20";
	}

	public String getApiKey() {
		return "L0WOWao1LgpHWKB7hhTA4bcGy4jULrEW";
	}

	public String getBaseUrl()
	{
		return "https://api.mongolab.com/api/1/databases/"+getDatabaseName()+"/collections/";
	}
	
	public String docApiKeyUrl()
	{
		return "&l=2000&apiKey="+getApiKey();
	}
	
	public String documentRequest(QueryKinde Qkind,HashMap<String,String> Qvalue)
	{
		switch (Qkind){
			case monthFinderQ:
				query="fictures?q="+"{'date.month':'"+Qvalue.get("date.month")+"','league':'"+Qvalue.get("league")+"'}";
				break;
			case dateTimeFinderQ:
				query="fictures?q="+"{'date.year':'"+Qvalue.get("date.year")+"','date.month':'"+Qvalue.get("date.month")+"','date.day':'"+Qvalue.get("date.day")+"','time':'"+Qvalue.get("time")+"'}";
				break;
			case basicQ:
				query="fictures?f="+
						"{'league':1,'code':1,'date':1,time:1,'home':1,'away':1,'result':1}";
				break;
			case scoreQ:
				query="fictures?f="+"{'score':1,'code':1,'_id':0}";
				break;
			case matchResultQ:
				query="fictures?q="+"{'code':"+Integer.valueOf(Qvalue.get("code"))+"}&f="+"{'goalEve':1,,'hTeamRecord':1,'aTeamRecord':1,'hPlayerRecord':1,'aPlayerRecord':1,'_id':0}";
				break;
		}
		return query;
	}
	
	public String buildContactsGetURL(QueryKinde Qk,HashMap<String,String> Qvalue)
	{
		return getBaseUrl()+documentRequest(Qk,Qvalue)+docApiKeyUrl();
	}

}
