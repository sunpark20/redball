package hungry.redball.aStatic;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by soy on 2016-03-24.
 */
public class Sfile {
    //fixtures 파일이름 저장하는 변수
    public static final String json_fixturesName="json_fixtures";
    public static final String json_parsed_fixturesName="json_parsed";
    static public void saveFile(Context c, String fileName, String string){
        FileOutputStream fos=null;
        try{
            fos = c.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    static public String readFile(Context c, String fileName){
        try{
            FileInputStream stream = c.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            StringBuilder sb = new StringBuilder();
            int len = 0;
            while( (len = stream.read(buffer)) > 0 )
                sb.append( new String( buffer, 0, len ) );
            stream.close();
            return sb.toString();
        }
        catch (IOException e){
            System.out.println(fileName+"readFile null-----------------------------------------");
            return "";
        }
    }
}
