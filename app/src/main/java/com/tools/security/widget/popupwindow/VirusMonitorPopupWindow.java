package com.tools.security.widget.popupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tools.security.R;

/**
 * description:实时监听的dialog的menu
 * author: xiaodifu
 * date: 2016/12/22.
 */

public class VirusMonitorPopupWindow extends PopupWindow {
    private TextView ignoreMenuText;
    private View mContentView;
    private IOnMenuClickListener iOnMenuClickListener;

    public VirusMonitorPopupWindow(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.pop_virus_monitor, null);
        ignoreMenuText = (TextView) mContentView.findViewById(R.id.text_menu_ignore);

        ignoreMenuText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.text_menu_ignore:
                        if (iOnMenuClickListener != null) {
                            iOnMenuClickListener.onIgnore();
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        // 设置SelectPicPopupWindow的View
        this.setContentView(mContentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.PopupAnimation);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(new BitmapDrawable());
//        this.setOutsideTouchable(true);
    }

    public void setOnMenuClickListener(IOnMenuClickListener listener) {
        this.iOnMenuClickListener = listener;
    }

    public interface IOnMenuClickListener {
        void onIgnore();
    }
}
