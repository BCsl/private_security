package com.tools.security.wifi.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.WifiProblem;

import java.util.List;

/**
 * description:wifi有问题结果页adapter
 * author: xiaodifu
 * date: 2017/1/15.
 */

public class WifiProblemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TRANSPARENT = 1;
    private static final int VIEW_TYPE_NORMAL = 2;

    private List<WifiProblem> list;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_TRANSPARENT) {
            return new TransViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_transparent, parent, false));
        }
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_problem_result, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_NORMAL) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            WifiProblem wifiProblem = list.get(position - 1);
            // TODO: 2017/1/17 根据问题状态设置不同的图片
            switch (wifiProblem.getType()) {
                case WifiProblem.TYPE_CONNECT:
                    break;
                case WifiProblem.TYPE_CAPTIVE:
                    break;
                case WifiProblem.TYPE_ARP:
                    break;
                case WifiProblem.TYPE_DEVICE:
                    break;
                case WifiProblem.TYPE_MITM:
                    break;
                case WifiProblem.TYPE_ENCRITION:
                    break;
                case WifiProblem.TYPE_SPEED:
                    break;

            }
            holder.textView.setText(wifiProblem.getName());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_TRANSPARENT;
        }
        return VIEW_TYPE_NORMAL;
    }


    @Override
    public int getItemCount() {
        return list == null ? 1 : list.size() + 1;
    }

    public void setData(List<WifiProblem> problemList) {
        this.list = problemList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img_problem);
            textView = (TextView) itemView.findViewById(R.id.text_problem);
        }
    }

    class TransViewHolder extends RecyclerView.ViewHolder {
        public TransViewHolder(View itemView) {
            super(itemView);
        }
    }
}
