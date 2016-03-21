package hungry.redball.util;


import java.util.ArrayList;

public class QueryBuilder_loading {

	public static ArrayList<Integer> codeArray;

	public String getDatabaseName() {
		return "sunpark20";
	}

	public String getApiKey() {
		return "L0WOWao1LgpHWKB7hhTA4bcGy4jULrEW";
	}

	public String getBaseUrl() {
		return "https://api.mongolab.com/api/1/databases/"+getDatabaseName()+"/collections/";
	}
//	전체받을때 쓰는거요
//	public String docApiKeyUrl()
//	{
//		return "l=2000&apiKey="+getApiKey();
//	}
	public String docApiKeyUrl()
	{
		return "apiKey="+getApiKey();
	}

	public String documentRequest()
	{
		return "fictures?";
	}

	public String buildPlayerUrl ()
	{
		return getBaseUrl()+"players?"+"l=2000&"
				+"f={\"_id\":0,\"positionText\":0,\"rating\":0,\"playerId\":0,\"teamId\":0,\"playedPositions\":0,\"isOpta\":0,\"subOn\":0,\"tournamentShortName\":0,\"tournamentRegionId\":0,\"regionCode\":0,\"tournamentRegionCode\":0,\"accurateThroughBallPerGame\":0,\"seasonId\":0,\"ranking\":0,\"minsPlayed\":0,\"offsideWonPerGame\":0,\"wasDribbledPerGame\":0,\"isActive\":0,\"firstName\":0,\"lastName\":0,\"seasonName\":0,\"tournamentId\":0,\"isManOfTheMatch\":0}&"
				+docApiKeyUrl();
	}

	public String buildTotalUrl ()
	{
		return getBaseUrl()+documentRequest()+"l=2000&"
				+"q={'date.year':'2016'}&"
				+"f={\"aPlayerRecord\":0,\"hPlayerRecord\":0,\"aTeamRecord\":0,\"hTeamRecord\":0}&"
				+docApiKeyUrl();
	}
	public String buildScoreUrl ()
	{
		return getBaseUrl()+documentRequest()+q()+f()+docApiKeyUrl();
	}
	public String buildDateUrl ()
	{
		return getBaseUrl()+documentRequest()
				+"q={'date.year':'2016'}&"
				+"f={\"code\":1,\"date\":1,\"time\":1,\"score\":1}&"
				+docApiKeyUrl();
	}


	//	public String q2(int i){
//		return "{'code':"+ QueryBuilder_total.codeArray.get(i)+"}";
//	}
	public String q(){
		String query="q={'code':{$in:[";
		for(int i=0;i< QueryBuilder_loading.codeArray.size();i++){
			query+=codeArray.get(i);
			if(i!= QueryBuilder_loading.codeArray.size()-1)
				query+=",";
		}
		query+="]}}&";
		return query.trim();
	}

	//매치번호:[1006259, 1006261]
	public String f1(){
		return "f=";
	}
	public String f2(){
		return "{\"code\":1,\"score\":1}";
	}
	public String f3(){
		return "&";
	}
	public String f(){
		return f1()+f2()+f3();
	}



}
