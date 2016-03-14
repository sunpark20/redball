package hungry.redball.alram.model;

/**
 * Created by soy on 2015-12-30.
 */
public class PrefInfo {
    String hTeam;
    String aTeam;
    boolean love;

    //love의 역활.  경기인포에서 추가해서 들어왔는지, 즐찾팀에서 들어왔는지 결정.
    // 인포에서 왔다면 1 아니면 0
    // 즐찾팀에서 팀을 지웠을때, 0인것만 삭제한다.

    public String gethTeam() {
        return hTeam;
    }

    public void sethTeam(String hTeam) {
        this.hTeam = hTeam;
    }

    public String getaTeam() {
        return aTeam;
    }

    public void setaTeam(String aTeam) {
        this.aTeam = aTeam;
    }

    public boolean isLove() {
        return love;
    }

    public void setLove(boolean love) {
        this.love = love;
    }

}
