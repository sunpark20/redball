package hungry.redball.alram.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hungry.redball.R;

public class GridViewAdapter extends ArrayAdapter<String> {
        Context context;
        int layoutResourceId;
        ArrayList<String> data = new ArrayList<String>();

        public GridViewAdapter(Context context, int layoutResourceId,
                               ArrayList<String> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            RecordHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new RecordHolder();
                holder.txtTitle = (TextView) row.findViewById(R.id.grid_text);
                row.setTag(holder);
            } else {
                holder = (RecordHolder) row.getTag();
            }
            String item = data.get(position).toString();

            holder.txtTitle.setText(item);

            return row;
        }

        class RecordHolder {
            TextView txtTitle;
        }

    }