package hungry.redball.player.model;

/**
 * Created by soy on 2015-07-01.
 */
public class Player {
    //row1
    private String r;
    private int flag;
    private String name;
    //row2
    private String team;
    private String position;
    private String height;
    private String weight;
    private String age;
    //row3 (main)
    private String goals;
    private String assists;
    private String motM;
    private String apps;
    private String yel;
    private String red;
    //offensive
    private String spG;
    private String drb;
    private String unstch;
    private String off;
    private String fouled;
    private String disp;
    //passing
    private String crosses;
    private String aerialsWon;
    private String keyP;
    private String avgP;
    private String psR;
    private String longB;
    //defensive
    private String tackles;
    private String inter;
    private String block;
    private String fouls;
    private String clear;
    private String ownG;

    public String getR() {
        return r;
    }

    public String getFouled() {
        return fouled;
    }

    public void setFouled(String fouled) {
        this.fouled = fouled;
    }

    public void setR(String r) {
        this.r = r;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getAssists() {
        return assists;
    }

    public void setAssists(String assists) {
        this.assists = assists;
    }

    public String getMotM() {
        return motM;
    }

    public void setMotM(String motM) {
        this.motM = motM;
    }

    public String getApps() {
        return apps;
    }

    public void setApps(String apps) {
        this.apps = apps;
    }

    public String getYel() {
        return yel;
    }

    public void setYel(String yel) {
        this.yel = yel;
    }

    public String getRed() {
        return red;
    }

    public void setRed(String red) {
        this.red = red;
    }

    public String getSpG() {
        return spG;
    }

    public void setSpG(String spG) {
        this.spG = spG;
    }

    public String getDrb() {
        return drb;
    }

    public void setDrb(String drb) {
        this.drb = drb;
    }

    public String getUnstch() {
        return unstch;
    }

    public void setUnstch(String unstch) {
        this.unstch = unstch;
    }

    public String getOff() {
        return off;
    }

    public void setOff(String off) {
        this.off = off;
    }

    public String getDisp() {
        return disp;
    }

    public void setDisp(String disp) {
        this.disp = disp;
    }

    public String getCrosses() {
        return crosses;
    }

    public void setCrosses(String crosses) {
        this.crosses = crosses;
    }

    public String getAerialsWon() {
        return aerialsWon;
    }

    public void setAerialsWon(String aerialsWon) {
        this.aerialsWon = aerialsWon;
    }

    public String getKeyP() {
        return keyP;
    }

    public void setKeyP(String keyP) {
        this.keyP = keyP;
    }

    public String getAvgP() {
        return avgP;
    }

    public void setAvgP(String avgP) {
        this.avgP = avgP;
    }

    public String getPsR() {
        return psR;
    }

    public void setPsR(String psR) {
        this.psR = psR;
    }

    public String getLongB() {
        return longB;
    }

    public void setLongB(String longB) {
        this.longB = longB;
    }

    public String getTackles() {
        return tackles;
    }

    public void setTackles(String tackles) {
        this.tackles = tackles;
    }

    public String getInter() {
        return inter;
    }

    public void setInter(String inter) {
        this.inter = inter;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getFouls() {
        return fouls;
    }

    public void setFouls(String fouls) {
        this.fouls = fouls;
    }

    public String getClear() {
        return clear;
    }

    public void setClear(String clear) {
        this.clear = clear;
    }

    public String getOwnG() {
        return ownG;
    }

    public void setOwnG(String ownG) {
        this.ownG = ownG;
    }

    public String nameReturn(String input){
        if(input.compareTo("goals")==0)  //row3 (main)
            return getGoals();
        else if(input.compareTo("assists")==0)
            return getAssists();
        else if(input.compareTo("motM")==0)
            return getMotM();
        else if(input.compareTo("apps")==0)
            return getApps();
        else if(input.compareTo("yel")==0)
            return getYel();
        else if(input.compareTo("red")==0)
            return getRed();
        else if(input.compareTo("spG")==0) //offensive
            return getSpG();
        else if(input.compareTo("drb")==0)
            return getDrb();
        else if(input.compareTo("unstch")==0)
            return getUnstch();
        else if(input.compareTo("off")==0)
            return getOff();
        else if(input.compareTo("fouled")==0)
            return getFouled();
        else if(input.compareTo("disp")==0)
            return getDisp();
        else if(input.compareTo("crosses")==0) //passing
            return getCrosses();
        else if(input.compareTo("aerialsWon")==0)
            return getAerialsWon();
        else if(input.compareTo("keyP")==0)
            return getKeyP();
        else if(input.compareTo("avgP")==0)
            return getAvgP();
        else if(input.compareTo("psR")==0)
            return getPsR();
        else if(input.compareTo("longB")==0)
            return getLongB();
        else if(input.compareTo("tackles")==0) //defensive
            return getTackles();
        else if(input.compareTo("inter")==0)
            return getInter();
        else if(input.compareTo("block")==0)
            return getBlock();
        else if(input.compareTo("fouls")==0)
            return getFouls();
        else if(input.compareTo("clear")==0)
            return getClear();
        else if(input.compareTo("ownG")==0)
            return getOwnG();

        return getGoals(); //default
    }

}

