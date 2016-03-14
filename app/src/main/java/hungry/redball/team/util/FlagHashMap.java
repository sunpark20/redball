package hungry.redball.team.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hungry.redball.R;

/**
 * Created by sun on 2015-09-03.
 */
public class FlagHashMap {
    HashMap<String, Integer> map = new HashMap<String, Integer>();

    public List<Map<String, Integer>> list= new ArrayList<Map<String, Integer>>();

    public void makeHashMap(){
        map=new HashMap<String, Integer>();
        //pre(20)
        map.put("chelsea", R.drawable.pre_chelsea);
        map.put("첼시 FC", R.drawable.pre_chelsea);
        map.put("arsenal", R.drawable.pre_arsenal);
        map.put("아스널 FC", R.drawable.pre_arsenal);
        map.put("crystal palace", R.drawable.pre_crystal_palace);
        map.put("크리스탈 팰리스 FC", R.drawable.pre_crystal_palace);
        map.put("manchester city", R.drawable.pre_manchester_city);
        map.put("맨체스터 시티 FC", R.drawable.pre_manchester_city);
        map.put("stoke", R.drawable.pre_stoke);
        map.put("스토크 시티 FC", R.drawable.pre_stoke);

        map.put("liverpool", R.drawable.pre_liverpool);
        map.put("리버풀 FC", R.drawable.pre_liverpool);
        map.put("manchester united", R.drawable.pre_manchester_united);
        map.put("맨체스터 유나이티드 FC", R.drawable.pre_manchester_united);
        map.put("tottenham", R.drawable.pre_tottenham);
        map.put("토트넘 핫스퍼 FC", R.drawable.pre_tottenham);
        map.put("sunderland", R.drawable.pre_sunderland);
        map.put("선덜랜드 AFC", R.drawable.pre_sunderland);
        map.put("leicester", R.drawable.pre_leicester);
        map.put("레스터 시티 FC", R.drawable.pre_leicester);

        map.put("newcastle united", R.drawable.pre_newcastle_united);
        map.put("뉴캐슬 유나이티드 FC", R.drawable.pre_newcastle_united);
        map.put("southampton", R.drawable.pre_southampton);
        map.put("사우샘프턴 FC", R.drawable.pre_southampton);
        map.put("everton", R.drawable.pre_everton);
        map.put("에버턴 FC", R.drawable.pre_everton);
        map.put("aston villa", R.drawable.pre_aston_villa);
        map.put("애스턴 빌라 FC", R.drawable.pre_aston_villa);
        map.put("bournemouth", R.drawable.pre_bournemouth);
        map.put("AFC 본머스", R.drawable.pre_bournemouth);

        map.put("watford", R.drawable.pre_watford);
        map.put("왓포드 FC", R.drawable.pre_watford);
        map.put("norwich", R.drawable.pre_norwich);
        map.put("노리치 시티 FC", R.drawable.pre_norwich);
        map.put("swansea", R.drawable.pre_swansea);
        map.put("스완지 시티 AFC", R.drawable.pre_swansea);
        map.put("west bromwich albion", R.drawable.pre_west_bromwich_albion);
        map.put("웨스트 브로미치 알비온 FC", R.drawable.pre_west_bromwich_albion);
        map.put("west ham", R.drawable.pre_west_ham);
        map.put("웨스트햄 유나이티드 FC", R.drawable.pre_west_ham);

        //la(20)
        map.put("real madrid", R.drawable.la_real_madrid);
        map.put("레알 마드리드", R.drawable.la_real_madrid);
        map.put("atletico madrid", R.drawable.la_atletico_madrid);
        map.put("아틀레티코 마드리드", R.drawable.la_atletico_madrid);
        map.put("celta vigo", R.drawable.la_celta_vigo);
        map.put("셀타 비고", R.drawable.la_celta_vigo);

        map.put("villarreal", R.drawable.la_villarreal);
        map.put("비야레알 CF", R.drawable.la_villarreal);
        map.put("eibar", R.drawable.la_eibar);
        map.put("SD 에이바르", R.drawable.la_eibar);
        map.put("espanyol", R.drawable.la_espanyol);
        map.put("RCD 에스파뇰", R.drawable.la_espanyol);

        map.put("las palmas", R.drawable.la_las_palmas);
        map.put("UD 라스 팔마스", R.drawable.la_las_palmas);
        map.put("levante", R.drawable.la_levante);
        map.put("레반테 UD", R.drawable.la_levante);
        map.put("valencia", R.drawable.la_valencia);
        map.put("발렌시아 CF", R.drawable.la_valencia);

        map.put("athletic club", R.drawable.la_athletic_club);
        map.put("아틀레틱 빌바오", R.drawable.la_athletic_club);
        map.put("deportivo la coruna", R.drawable.la_deportivo_la_coruna);
        map.put("데포르티보 라 코루냐", R.drawable.la_deportivo_la_coruna);
        map.put("sevilla", R.drawable.la_sevilla);
        map.put("세비야 FC", R.drawable.la_sevilla);

        map.put("real betis", R.drawable.la_real_betis);
        map.put("레알 베티스", R.drawable.la_real_betis);
        map.put("rayo vallecano", R.drawable.la_rayo_vallecano);
        map.put("라요 바예카노", R.drawable.la_rayo_vallecano);
        map.put("sporting gijon", R.drawable.la_sporting_gijon);
        map.put("스포르팅 히혼", R.drawable.la_sporting_gijon);

        map.put("getafe", R.drawable.la_getafe);
        map.put("헤타페", R.drawable.la_getafe);
        map.put("real sociedad", R.drawable.la_real_sociedad);
        map.put("레알 소시에다드", R.drawable.la_real_sociedad);
        map.put("malaga", R.drawable.la_malaga);
        map.put("말라가 CF", R.drawable.la_malaga);

        map.put("barcelona", R.drawable.la_barcelona);
        map.put("FC 바르셀로나", R.drawable.la_barcelona);
        map.put("granada", R.drawable.la_granada);
        map.put("그라나다 CF", R.drawable.la_granada);

        //bun (18)
        map.put("bayern munich", R.drawable.bun_bayern_munich);
        map.put("FC 바이에른 뮌헨", R.drawable.bun_bayern_munich);
        map.put("borussia dortmund", R.drawable.bun_borussia_dortmund);
        map.put("보루시아 도르트문트", R.drawable.bun_borussia_dortmund);
        map.put("wolfsburg", R.drawable.bun_wolfsburg);
        map.put("VfL 볼프스부르크", R.drawable.bun_wolfsburg);

        map.put("hertha berlin", R.drawable.bun_hertha_berlin);
        map.put("헤르타 BSC 베를린", R.drawable.bun_hertha_berlin);
        map.put("schalke 04", R.drawable.bun_schalke_04);
        map.put("FC 샬케 04", R.drawable.bun_schalke_04);
        map.put("borussia m.gladbach", R.drawable.bun_borussia_m_gladbach); //쩜이있음 예외
        map.put("보루시아 묀헨글라드바흐", R.drawable.bun_borussia_m_gladbach);

        map.put("fc cologne", R.drawable.bun_fc_cologne);
        map.put("FC 쾰른", R.drawable.bun_fc_cologne);
        map.put("bayer leverkusen", R.drawable.bun_bayer_leverkusen);
        map.put("바이어 04 레버쿠젠", R.drawable.bun_bayer_leverkusen);
        map.put("mainz 05", R.drawable.bun_mainz_05);
        map.put("FSV 마인츠 05", R.drawable.bun_mainz_05);

        map.put("ingolstadt", R.drawable.bun_ingolstadt);
        map.put("FC 잉골슈타트", R.drawable.bun_ingolstadt);
        map.put("hamburger sv", R.drawable.bun_hamburger_sv);
        map.put("함부르크 SV", R.drawable.bun_hamburger_sv);
        map.put("eintracht frankfurt", R.drawable.bun_eintracht_frankfurt);
        map.put("아인트라흐트 프랑크푸르트", R.drawable.bun_eintracht_frankfurt);

        map.put("darmstadt", R.drawable.bun_darmstadt);
        map.put("다름슈타트", R.drawable.bun_darmstadt);
        map.put("werder bremen", R.drawable.bun_werder_bremen);
        map.put("SV 베르더 브레멘", R.drawable.bun_werder_bremen);
        map.put("hannover 96", R.drawable.bun_hannover_96);
        map.put("하노버 96", R.drawable.bun_hannover_96);

        map.put("vfb stuttgart", R.drawable.bun_vfb_stuttgart);
        map.put("VfB 슈투트가르트", R.drawable.bun_vfb_stuttgart);
        map.put("hoffenheim", R.drawable.bun_hoffenheim);
        map.put("TSG 1899 호펜하임", R.drawable.bun_hoffenheim);
        map.put("augsburg", R.drawable.bun_augsburg);
        map.put("FC 아우크스부르크", R.drawable.bun_augsburg);

        //se (20)
        map.put("fiorentina", R.drawable.se_fiorentina);
        map.put("ACF 피오렌티나", R.drawable.se_fiorentina);
        map.put("inter", R.drawable.se_inter);
        map.put("FC 인터 밀란", R.drawable.se_inter);
        map.put("roma", R.drawable.se_roma);
        map.put("AS 로마", R.drawable.se_roma);

        map.put("napoli", R.drawable.se_napoli);
        map.put("SSC 나폴리", R.drawable.se_napoli);
        map.put("sassuolo", R.drawable.se_sassuolo);
        map.put("US 사수올로 칼초", R.drawable.se_sassuolo);
        map.put("ac milan", R.drawable.se_ac_milan);
        map.put("AC 밀란", R.drawable.se_ac_milan);

        map.put("juventus", R.drawable.se_juventus);
        map.put("유벤투스 FC", R.drawable.se_juventus);
        map.put("atalanta", R.drawable.se_atalanta);
        map.put("아탈란타 BC", R.drawable.se_atalanta);
        map.put("lazio", R.drawable.se_lazio);
        map.put("SS 라치오", R.drawable.se_lazio);

        map.put("sampdoria", R.drawable.se_sampdoria);
        map.put("UC 삼프도리아", R.drawable.se_sampdoria);
        map.put("torino", R.drawable.se_torino);
        map.put("토리노 FC", R.drawable.se_torino);
        map.put("palermo", R.drawable.se_palermo);
        map.put("US 팔레르모", R.drawable.se_palermo);

        map.put("empoli", R.drawable.se_empoli);
        map.put("엠폴리 FC", R.drawable.se_empoli);
        map.put("chievo", R.drawable.se_chievo);
        map.put("AC 키에보베로나", R.drawable.se_chievo);
        map.put("genoa", R.drawable.se_genoa);
        map.put("제노아 CFC", R.drawable.se_genoa);

        map.put("bologna", R.drawable.se_bologna);
        map.put("볼로냐 FC 1909", R.drawable.se_bologna);
        map.put("udinese", R.drawable.se_udinese);
        map.put("우디네세 칼초", R.drawable.se_udinese);
        map.put("frosinone", R.drawable.se_frosinone);
        map.put("프로시노네 칼초", R.drawable.se_frosinone);

        map.put("verona", R.drawable.se_verona);
        map.put("베로나 FC", R.drawable.se_verona);
        map.put("carpi", R.drawable.se_carpi);
        map.put("카르피 FC", R.drawable.se_carpi);

        //li (20)
        map.put("paris saint germain", R.drawable.li_paris_saint_germain);
        map.put("파리 생제르맹 FC", R.drawable.li_paris_saint_germain);
        map.put("lyon", R.drawable.li_lyon);
        map.put("올림피크 리옹", R.drawable.li_lyon);
        map.put("caen", R.drawable.li_caen);
        map.put("SM 캉", R.drawable.li_caen);

        map.put("angers", R.drawable.li_angers);
        map.put("앙제 SCO", R.drawable.li_angers);
        map.put("saint-etienne", R.drawable.li_saint_etienne); //예외 짝대기가 있네.
        map.put("AS 생테티엔", R.drawable.li_saint_etienne);
        map.put("nice", R.drawable.li_nice);
        map.put("OGC 니스", R.drawable.li_nice);

        map.put("lorient", R.drawable.li_lorient);
        map.put("FC 로리앙", R.drawable.li_lorient);
        map.put("rennes", R.drawable.li_rennes);
        map.put("스타드 렌 FC", R.drawable.li_rennes);
        map.put("monaco", R.drawable.li_monaco);
        map.put("AS 모나코 FC", R.drawable.li_monaco);

        map.put("nantes", R.drawable.li_nantes);
        map.put("FC 낭트", R.drawable.li_nantes);
        map.put("bordeaux", R.drawable.li_bordeaux);
        map.put("FC 지롱댕 드 보르도", R.drawable.li_bordeaux);
        map.put("guingamp", R.drawable.li_guingamp);
        map.put("앙나방 갱강", R.drawable.li_guingamp);

        map.put("marseille", R.drawable.li_marseille);
        map.put("올림피크 드 마르세유", R.drawable.li_marseille);
        map.put("reims", R.drawable.li_reims);
        map.put("스타드 랭스", R.drawable.li_reims);
        map.put("sc bastia", R.drawable.li_sc_bastia);
        map.put("SC 바스티아", R.drawable.li_sc_bastia);

        map.put("lille", R.drawable.li_lille);
        map.put("릴 OSC", R.drawable.li_lille);
        map.put("montpellier", R.drawable.li_montpellier);
        map.put("몽펠리에 HSC", R.drawable.li_montpellier);
        map.put("gfc ajaccio", R.drawable.li_gfc_ajaccio);
        map.put("GFCO 아작시오", R.drawable.li_gfc_ajaccio);

        map.put("toulouse", R.drawable.li_toulouse);
        map.put("툴루즈 FC", R.drawable.li_toulouse);
        map.put("troyes", R.drawable.li_troyes);
        map.put("ES 트루아 AC", R.drawable.li_troyes);


        list.add(map);
    }
}
