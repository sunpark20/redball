package hungry.redball.player.util;

import java.util.Comparator;

import hungry.redball.player.model.Player;

/**
 * String 을 double 형으로 바꾼 뒤 솔팅하기 때문에 comparator가 필요합니다.
 */

//너무 길어서 여기 씀.
public class PlayerComparator implements Comparator<Player> {
    private String target;
    public PlayerComparator(String target) {
        this.target = target;
    }

    @Override
    public int compare(Player a, Player b) {
        double pA=0.0, pB=0.0;
        if(a.nameReturn(target).compareTo("-")==0)
            pA=0.0;
        else
            pA = Double.valueOf(a.nameReturn(target));
        if(b.nameReturn(target).compareTo("-")==0)
            pB=0.0;
        else
            pB = Double.valueOf(b.nameReturn(target));
        return  Double.compare(pB, pA);
    }
}
