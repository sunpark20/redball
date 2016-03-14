package hungry.redball.alram.model;

/**
 * Created by soy on 2015-07-01.
 */
public class Pref {
    //row1
    String league, date;

    //row2 home
    int hFlag;
    String hTeam, hScore;

    //row3 home
    int aFlag;
    String aTeam, aScore;

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int gethFlag() {
        return hFlag;
    }

    public void sethFlag(int hFlag) {
        this.hFlag = hFlag;
    }

    public String gethTeam() {
        return hTeam;
    }

    public void sethTeam(String hTeam) {
        this.hTeam = hTeam;
    }

    public String gethScore() {
        return hScore;
    }

    public void sethScore(String hScore) {
        this.hScore = hScore;
    }

    public int getaFlag() {
        return aFlag;
    }

    public void setaFlag(int aFlag) {
        this.aFlag = aFlag;
    }

    public String getaTeam() {
        return aTeam;
    }

    public void setaTeam(String aTeam) {
        this.aTeam = aTeam;
    }

    public String getaScore() {
        return aScore;
    }

    public void setaScore(String aScore) {
        this.aScore = aScore;
    }
}
