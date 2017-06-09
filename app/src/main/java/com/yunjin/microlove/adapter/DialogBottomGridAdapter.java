package com.yunjin.microlove.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunjin.microlove.R;
import com.yunjin.microlove.bean.DialogGrid;

import java.util.List;

/**
 * @Description 底部Dialog适配器
 * @Author 一花一世界
 */
public class DialogBottomGridAdapter extends BaseAdapter {

    private List<DialogGrid> data;
    private LayoutInflater inflater;

    public DialogBottomGridAdapter(Context context, List<DialogGrid> data) {
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_dialog_grid, null);
            holder = new ViewHolder();
            holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DialogGrid itemBean = data.get(position);
        holder.iv_image.setImageResource(itemBean.getImageId());
        holder.tv_name.setText(itemBean.getName());
        return convertView;
    }

    private class ViewHolder {
        private ImageView iv_image;
        private TextView tv_name;
    }
}
