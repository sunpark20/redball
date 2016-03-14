package hungry.redball.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import hungry.redball.R;


/**
 * Created by soy on 2015-06-24.
 */
public class RedballProgressDialog extends Dialog {
    public RedballProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 지저분한(?) 다이얼 로그 제목을 날림
        setContentView(R.layout.dialog_redball); // 다이얼로그에 박을 레이아웃
    }
    public void hidePDialog(RedballProgressDialog cp) {
        if (cp != null) {
            cp.dismiss();
            cp = null;
        }
    }
}