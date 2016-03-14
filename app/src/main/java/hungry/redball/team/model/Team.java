package hungry.redball.team.model;

/**
 * Created by soy on 2015-07-01.
 */
public class Team {
    int flag;
    String teamName, rank, app, win, draw, lose, goal, minusGoal, compareGG, winScore;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public String getDraw() {
        return draw;
    }

    public void setDraw(String draw) {
        this.draw = draw;
    }

    public String getLose() {
        return lose;
    }

    public void setLose(String lose) {
        this.lose = lose;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getMinusGoal() {
        return minusGoal;
    }

    public void setMinusGoal(String minusGoal) {
        this.minusGoal = minusGoal;
    }

    public String getCompareGG() {
        return compareGG;
    }

    public void setCompareGG(String compareGG) {
        this.compareGG = compareGG;
    }

    public String getWinScore() {
        return winScore;
    }

    public void setWinScore(String winScore) {
        this.winScore = winScore;
    }
}
