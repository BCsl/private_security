package com.tools.security.common.viewhoder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.tools.security.R;
import com.tools.security.widget.shimmer.ShimmerTextView;

/**
 * description:原生广告
 * author: xiaodifu
 * date: 2017/1/18.
 */

public class NormalNativeAdViewHolder extends RecyclerView.ViewHolder {

    public TextView nameText;
    public TextView markText;
    public NetworkImageView iconImg;
    public NetworkImageView contentImg;
    public ShimmerTextView installText;

    public NormalNativeAdViewHolder(View itemView) {
        super(itemView);
        nameText = (TextView) itemView.findViewById(R.id.text_name);
        markText = (TextView) itemView.findViewById(R.id.text_mark);
        iconImg = (NetworkImageView) itemView.findViewById(R.id.img_icon);
        contentImg = (NetworkImageView) itemView.findViewById(R.id.img_bg);
        installText= (ShimmerTextView) itemView.findViewById(R.id.text_install);
    }

}
