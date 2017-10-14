package com.tools.security.common.viewhoder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;

/**
 * description:应用内功能viewholder
 * author: xiaodifu
 * date: 2017/1/18.
 */

public class FunctionAdViewHolder extends RecyclerView.ViewHolder {
    public ImageView iconImg;
    public TextView titleText;
    public TextView markText;
    public TextView fixText;

    public FunctionAdViewHolder(View itemView) {
        super(itemView);
        iconImg = (ImageView) itemView.findViewById(R.id.img_icon);
        titleText = (TextView) itemView.findViewById(R.id.text_title);
        markText = (TextView) itemView.findViewById(R.id.text_mark);
        fixText = (TextView) itemView.findViewById(R.id.text_fix);
    }
}
