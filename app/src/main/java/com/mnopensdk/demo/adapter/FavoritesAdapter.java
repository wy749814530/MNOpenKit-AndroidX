package com.mnopensdk.demo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.mn.bean.restfull.FavoritePointBean;

import java.util.List;

import com.mnopensdk.demo.GlideApp;
import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2020/1/14 0014.
 */

public class FavoritesAdapter extends BaseRecyclerAdapter<FavoritePointBean> {
    private boolean editState = false;
    private FavoritesItemListener mListener;
    private FavoritePointBean favoritePoint;

    public FavoritesAdapter(Context context, List<FavoritePointBean> data) {
        super(context, data, R.layout.adapter_favorites_item);
    }

    public void setFavoritesItemListener(FavoritesItemListener listener) {
        mListener = listener;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, FavoritePointBean bean) {
        baseViewHolder.setText(R.id.tv_name, bean.getName());
        ImageView ivView = (ImageView) baseViewHolder.getView(R.id.iv_image);
        GlideApp.with(mContext).load(bean.getImage_url()).error(R.mipmap.dynamic_play_placeholder_default).into(ivView);
        if (editState) {
            baseViewHolder.setVisible(R.id.iv_select, true);
            if (favoritePoint != null && bean.getId().equals(favoritePoint.getId())) {
                baseViewHolder.setImageResource(R.id.iv_select, R.mipmap.news_edit_choice_pre);
            } else {
                baseViewHolder.setImageResource(R.id.iv_select, R.mipmap.news_edit_choice);
            }
        } else {
            baseViewHolder.setVisible(R.id.iv_select, false);
        }

        baseViewHolder.setOnLongClickListener(R.id.rl_main_itme_lay, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    editState = true;
                    mListener.onLongClickFavorite(bean);
                    notifyDataSetChanged();
                }
                return false;
            }
        });

        baseViewHolder.setOnClickListener(R.id.rl_main_itme_lay, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    if (editState) {
                        favoritePoint = bean;
                        notifyDataSetChanged();
                        return;
                    }
                    mListener.onnClickFavorite(bean);
                }
            }
        });
    }

    public void setEditState(boolean state) {
        editState = state;
        notifyDataSetChanged();
    }

    public FavoritePointBean getSelectFavorite() {
        return favoritePoint;
    }

    public interface FavoritesItemListener {
        void onLongClickFavorite(FavoritePointBean item);

        void onnClickFavorite(FavoritePointBean item);
    }
}
