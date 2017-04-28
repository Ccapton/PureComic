package com.capton.purecomic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.capton.purecomic.R;

import java.util.List;
import java.util.Map;

/**
 * Created by capton on 2017/4/19.
 */

public class OptionAdapter extends SimpleAdapter {
    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */

    private int [] res;
  private Context context;
    public OptionAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,int [] res) {
        super(context, data, resource, from, to);
        this.context=context;
        this.res=res;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView icon;
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.option_item, parent, false);
            convertView.setBackgroundResource(R.drawable.user_item);
            icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(icon);
        }else {
            icon= (ImageView) convertView.getTag();
        }
        icon.setImageResource(res[position]);

        return super.getView(position, convertView, parent);
    }
}
