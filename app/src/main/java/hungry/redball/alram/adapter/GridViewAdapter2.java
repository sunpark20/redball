package hungry.redball.alram.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import hungry.redball.R;
import hungry.redball.alram.model.Grid;

public class GridViewAdapter2 extends ArrayAdapter<Grid> {
        Context context;
        int layoutResourceId;
        ArrayList<Grid> data = new ArrayList<Grid>();

        public GridViewAdapter2(Context context, int layoutResourceId,
                                ArrayList<Grid> data) {
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
                holder.teamName = (TextView) row.findViewById(R.id.teamName);
                holder.flag = (ImageView) row.findViewById(R.id.flag);
                row.setTag(holder);
            } else {
                holder = (RecordHolder) row.getTag();
            }
            String teamName = data.get(position).getTeamName();
            holder.teamName.setText(teamName);

            //resize (flag)
            Resources res=context.getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            int id=data.get(position).getFlag();
            Bitmap src=BitmapFactory.decodeResource(res, id, options);
            holder.flag.setImageBitmap(src);

            return row;
        }

        class RecordHolder {
            TextView teamName;
            ImageView flag;
        }

}