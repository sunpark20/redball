package hungry.redball.matchRepo;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ViewPageAdapter extends PagerAdapter {

    private ArrayList<View> pageViews;
    private Context mContext;
    private Button btn;

    public ViewPageAdapter(ArrayList<View> pageViews, Context mContext) {
        super();
        this.pageViews = pageViews;
        this.mContext = mContext;
    }

    @Override
    public Object instantiateItem(View v, int position) {
        switch (position) {
            case 0:
                Log.e("1page","1page");
//                btn = (Button) pageViews.get(position).findViewById(R.id.button1);
//                btn.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(mContext, "Clicked!", Toast.LENGTH_SHORT).show();
//                    }
//                });
                break;
            case 1:
                Log.e("2page","2page");
                break;
            case 2:
                Log.e("3page","3page");
                break;

            default:
                break;
        }

        ((ViewPager) v).addView(pageViews.get(position));
        return pageViews.get(position);
    }

    @Override
    public void destroyItem(View v, int position, Object arg2) {
        ((ViewPager) v).removeView(pageViews.get(position));
    }

    @Override
    public void finishUpdate(View arg0) {
    }

    @Override
    public int getCount() {
        return pageViews.size();
    }

    @Override
    public boolean isViewFromObject(View v, Object arg1) {
        return v == arg1;
    }

    @Override
    public void startUpdate(View arg0) {
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
