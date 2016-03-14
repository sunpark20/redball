package hungry.redball.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import hungry.redball.R;


/**
 * Created by soy on 2015-11-12.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {
    Context context;
    String[] items = new String[] {};

    public SpinnerAdapter(final Context context,
                          final int textViewResourceId, final String[] objects) {
        super(context, textViewResourceId, objects);
        this.items = objects;
        this.context = context;
    }

    /**
     * 스피너 클릭시 보여지는 View의 정의
     */
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        TextView tv = (TextView) convertView
                .findViewById(android.R.id.text1);
        tv.setText(items[position]);
        tv.setTextSize(17);
        tv.setPadding(2, 5, 5, 2);
        tv.setBackgroundResource(R.drawable.xml_border);
        tv.setLayoutParams(new AbsListView.LayoutParams(getPx(300),
                AbsListView.LayoutParams.WRAP_CONTENT));
        if(position <=6)
            tv.setTextColor( context.getResources().getColor(R.color.white) );
        else if(7<=position && position <=12)
            tv.setTextColor( context.getResources().getColor(R.color.red) );
        else if(13<=position && position <=18)
            tv.setTextColor( context.getResources().getColor(R.color.green) );
        else if(19<=position)
            tv.setTextColor( context.getResources().getColor(R.color.blue) );
        return convertView;
    }

    public int getPx(int dimensionDp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dimensionDp * density + 0.5f);
    }
    /**
     * 기본 스피너 View 정의
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_item, parent, false);
        }
        TextView tv = (TextView) convertView
                .findViewById(android.R.id.text1);
        tv.setText(items[position]);
        tv.setTextColor(context.getResources().getColor(R.color.wweak));
        tv.setTextSize(17);
        return convertView;
    }
}
