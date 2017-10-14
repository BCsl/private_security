package com.tools.security.widget.popupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tools.security.R;

/**
 * Created by lzx on 2017/2/4.
 */

public class FeedbackPopWindow extends PopupWindow implements View.OnClickListener {

    private View mContentView;
    private Context mContext;
    private TextView mItemSug, mItemPro, mItemForce;
    private OnItemClick mOnItemClick;

    public FeedbackPopWindow(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.pop_feedback, null);
        mItemSug = (TextView) mContentView.findViewById(R.id.item_sug);
        mItemPro = (TextView) mContentView.findViewById(R.id.item_pro);
        mItemForce = (TextView) mContentView.findViewById(R.id.item_force);
        mItemSug.setOnClickListener(this);
        mItemPro.setOnClickListener(this);
        mItemForce.setOnClickListener(this);
        setFocusable(true);
        this.setContentView(mContentView);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);

        this.setBackgroundDrawable(new BitmapDrawable());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_sug:
                if (mOnItemClick != null) {
                    mOnItemClick.onClick(mItemSug.getText().toString(), "Suggestion");
                }
                break;
            case R.id.item_pro:
                if (mOnItemClick != null) {
                    mOnItemClick.onClick(mItemPro.getText().toString(), "Problem");
                }
                break;
            case R.id.item_force:
                if (mOnItemClick != null) {
                    mOnItemClick.onClick(mItemForce.getText().toString(), "Forced/Tricked Installation");
                }
                break;
        }
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onClick(String text, String feedbackType);
    }
}
