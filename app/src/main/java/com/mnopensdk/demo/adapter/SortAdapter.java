package com.mnopensdk.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.mnopensdk.demo.utils.LanguageManager;
import com.mnopensdk.demo.bean.CountryCodeBean;

import java.util.ArrayList;
import java.util.List;

import com.mnopensdk.demo.R;

/**
 * Created by hjz on 2017/9/7.
 */

public class SortAdapter extends BaseAdapter implements SectionIndexer{
    private List<CountryCodeBean.AreasBean> list =new ArrayList<>();
    private Context mContext;

    public  SortAdapter(Context context, List<CountryCodeBean.AreasBean> list){
        mContext = context;
        this.list = list;
    }
    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<CountryCodeBean.AreasBean> list){
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CountryCodeBean.AreasBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.country_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.country_name);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            viewHolder.tvAc = (TextView) convertView.findViewById(R.id.country_ac);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (list!=null){
            final  CountryCodeBean.AreasBean mContent = list.get(position);

            //根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)){
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText(mContent.getSortLetters());
            }else {
                viewHolder.tvLetter.setVisibility(View.GONE);
            }

            if(LanguageManager.getLanguage().startsWith("zh")){
                viewHolder.tvTitle.setText(this.list.get(position).getCn_name());
            }else{
                viewHolder.tvTitle.setText(this.list.get(position).getEn_name());
            }
            viewHolder.tvAc.setText("+"+this.list.get(position).getAc());
        }
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }


    static  class  ViewHolder{
        TextView tvTitle;
        TextView tvLetter;
        TextView tvAc;
    }
}
