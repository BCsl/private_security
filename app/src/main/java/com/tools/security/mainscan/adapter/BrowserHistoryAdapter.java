package com.tools.security.mainscan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.BrowserHistory;
import com.tools.security.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2016/12/20.
 * email：386707112@qq.com
 * 功能：
 */

public class BrowserHistoryAdapter extends RecyclerView.Adapter {

    private List<BrowserHistory> mHistoryList = new ArrayList<>();
    private Context mContext;

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_HAS_DATE = 1;

    public BrowserHistoryAdapter(Context context) {
        mContext = context;
    }

    public void cleanHistory() {
        mHistoryList.clear();
        notifyDataSetChanged();
    }

    public void setData(List<BrowserHistory> historyList) {
        this.mHistoryList = historyList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        BrowserHistory history = mHistoryList.get(position);
        if (position == 0) {
            return TYPE_HAS_DATE;
        } else {
            int index = position - 1;
            String time1 = StringUtil.getFormatBrowserHistoryTime(mHistoryList.get(index).getLongdate());
            String time2 = StringUtil.getFormatBrowserHistoryTime(history.getLongdate());
            if (!time1.equals(time2)) {
                return TYPE_HAS_DATE;
            } else {
                return TYPE_NORMAL;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_NORMAL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browser_history, null);
                return new BrowserHistoryViewHolder(view);
            case TYPE_HAS_DATE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browser_history_has_date, null);
                return new BrowserHistoryDateViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BrowserHistory history = mHistoryList.get(position);
        if (holder instanceof BrowserHistoryViewHolder) {
            BrowserHistoryViewHolder historyViewHolder = (BrowserHistoryViewHolder) holder;
            historyViewHolder.mHistoryTitle.setText(history.getTitle());
            historyViewHolder.mHistoryUrl.setText(history.getUrl());
            historyViewHolder.mLine.setVisibility((position == mHistoryList.size() - 1) ? View.INVISIBLE : View.VISIBLE);
        } else if (holder instanceof BrowserHistoryDateViewHolder) {
            BrowserHistoryDateViewHolder historyDateViewHolder = (BrowserHistoryDateViewHolder) holder;
            historyDateViewHolder.mHistoryDate.setText(StringUtil.getFormatBrowserHistoryTime(history.getLongdate()));
            historyDateViewHolder.mHistoryTitle.setText(history.getTitle());
            historyDateViewHolder.mHistoryUrl.setText(history.getUrl());
            historyDateViewHolder.mLine.setVisibility((position == mHistoryList.size() - 1) ? View.INVISIBLE : View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }

    private class BrowserHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView mHistoryTitle, mHistoryUrl;
        private View mLine;

        public BrowserHistoryViewHolder(View itemView) {
            super(itemView);
            mHistoryTitle = (TextView) itemView.findViewById(R.id.history_title);
            mHistoryUrl = (TextView) itemView.findViewById(R.id.history_url);
            mLine = itemView.findViewById(R.id.line);
        }
    }

    private class BrowserHistoryDateViewHolder extends RecyclerView.ViewHolder {
        private TextView mHistoryTitle, mHistoryUrl, mHistoryDate;
        private View mLine;

        public BrowserHistoryDateViewHolder(View itemView) {
            super(itemView);
            mHistoryTitle = (TextView) itemView.findViewById(R.id.history_title);
            mHistoryUrl = (TextView) itemView.findViewById(R.id.history_url);
            mHistoryDate = (TextView) itemView.findViewById(R.id.history_date);
            mLine = itemView.findViewById(R.id.line);
        }
    }


}
