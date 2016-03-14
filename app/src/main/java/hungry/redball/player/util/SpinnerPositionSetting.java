package hungry.redball.player.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by soy on 2015-11-16.
 */
public class SpinnerPositionSetting {

    public void set(List<?> player, int position){
        //whoscored한테 안들키게 rank는 안쓴다.
//        if(position == 0) { //기본이면 그대로 붙여준다.
//            Comparator c=new PlayerComparator("ranks");
//            Collections.sort(player, c);
//        }
        if (position == 0) {
            Comparator c=new PlayerComparator("goals");
            Collections.sort(player, c);
        }else if (position == 1) {
            Comparator c=new PlayerComparator("assists");
            Collections.sort(player, c);
        }
        else if (position == 2) {
            Comparator c=new PlayerComparator("motM");
            Collections.sort(player, c);
        }
        else if (position == 3) {
            Comparator c=new PlayerComparator("apps");
            Collections.sort(player, c);
        }
        else if (position == 4) {
            Comparator c=new PlayerComparator("yel");
            Collections.sort(player, c);
        }
        else if (position == 5) {
            Comparator c=new PlayerComparator("red");
            Collections.sort(player, c);
        }
        //offensive
        else if (position == 6) {
            Comparator c=new PlayerComparator("spG");
            Collections.sort(player, c);
        }
        else if (position == 7) {
            Comparator c=new PlayerComparator("drb");
            Collections.sort(player, c);
        }
        else if (position == 8) {
            Comparator c=new PlayerComparator("unstch");
            Collections.sort(player, c);
        }
        else if (position == 9) {
            Comparator c=new PlayerComparator("off");
            Collections.sort(player, c);
        }
        else if (position == 10) {
            Comparator c=new PlayerComparator("fouled");
            Collections.sort(player, c);
        }
        else if (position == 11) {
            Comparator c=new PlayerComparator("disp");
            Collections.sort(player, c);
        }
        //passing
        else if (position == 12) {
            Comparator c=new PlayerComparator("crosses");
            Collections.sort(player, c);
        }
        else if (position == 13) {
            Comparator c=new PlayerComparator("aerialsWon");
            Collections.sort(player, c);
        }
        else if (position == 14) {
            Comparator c=new PlayerComparator("keyP");
            Collections.sort(player, c);
        }
        else if (position == 15) {
            Comparator c=new PlayerComparator("avgP");
            Collections.sort(player, c);
        }
        else if (position == 16) {
            Comparator c=new PlayerComparator("psR");
            Collections.sort(player, c);
        }
        else if (position == 17) {
            Comparator c=new PlayerComparator("longB");
            Collections.sort(player, c);
        }
        //defensive
        else if (position == 18) {
            Comparator c=new PlayerComparator("tackles");
            Collections.sort(player, c);
        }
        else if (position == 19) {
            Comparator c=new PlayerComparator("inter");
            Collections.sort(player, c);
        }
        else if (position == 20) {
            Comparator c=new PlayerComparator("block");
            Collections.sort(player, c);
        }
        else if (position == 21) {
            Comparator c=new PlayerComparator("fouls");
            Collections.sort(player, c);
        }
        else if (position == 22) {
            Comparator c=new PlayerComparator("clear");
            Collections.sort(player, c);
        }
        else if (position == 23) {
            Comparator c=new PlayerComparator("ownG");
            Collections.sort(player, c);
        }
    }
}
