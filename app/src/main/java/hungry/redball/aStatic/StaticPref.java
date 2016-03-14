package hungry.redball.aStatic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hungry.redball.alram.model.PrefInfo;

/**
 * Created by soy on 2016-01-07.
 */
public class StaticPref {
    public static final String PLAYER_DATE="PLATER_DATE";

    //START string sharedpreference
    public static void savePref_String(Context c,String TAG, String str, String name) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.e(TAG, "savePref_String: "+str+" saved");
        editor.putString(name, str);
        editor.commit();
    }
    public static String loadPref_String(Context c,String TAG, String name){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        String str=sharedPreferences.getString(name, "defValue");
        Log.e(TAG, "loadPref_String: " + str + " loaded");
        return str;
    }
    //END string sharedpreference

    //START  ArrayList<String> sharedpreference
    public static void savePref_prefTeam(Context c, ArrayList<String> al) {
        final String name = "PREF_TEAM";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String s= new Gson().toJson(al);
        editor.putString(name, s);
        editor.commit();
    }
    public static ArrayList<String> loadPref_prefTeam(Context c){
        final String name = "PREF_TEAM";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        String s = sharedPreferences.getString(name, "defValue");
        if(s.compareTo("defValue")==0)
            return new ArrayList<String>();
        else {
            Type type = new TypeToken<List<String>>() {}.getType();
            List<String> prefTeam = new Gson().fromJson(s, type);
            return (ArrayList<String>) prefTeam;
        }
    }
    //START  ArrayList<String> sharedpreference

    //START  Map<Integer, AlarmInfo> prefInfo
    public static void savePref_prefInfo(Context c, Map<Integer, PrefInfo> pi) {
        final String name = "PREF_INFO";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String gridJsonString= new Gson().toJson(pi);
        editor.putString(name, gridJsonString);
        editor.commit();
    }

    public static Map<Integer, PrefInfo> loadPref_prefInfo(Context c) {
        final String name = "PREF_INFO";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        String s = sharedPreferences.getString(name, "defValue");
        if(s.compareTo("defValue")==0)
            return new HashMap<Integer, PrefInfo>();
        else{
            Type type = new TypeToken<Map<Integer, PrefInfo>>() {}.getType();
            Map<Integer, PrefInfo> prefInfo = new Gson().fromJson(s, type);
            return (Map<Integer, PrefInfo>)prefInfo;
        }
    }
    //Map<Integer, AlarmInfo> prefInfo
}
