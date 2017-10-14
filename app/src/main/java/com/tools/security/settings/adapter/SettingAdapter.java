package com.tools.security.settings.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.SettingInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhizhen on 2017/1/12.
 */

public class SettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {


    private static final int TYPE_CONTENT = 0;
    private static final int TYPE_HEAD = 1;
    private static Context mContext;
    private static List<SettingInfo> mList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecycleViewSwitchClickListener mOnSwitchClickListener = null;


    public SettingAdapter(Context context,ArrayList<SettingInfo> list){

        this.mContext = context;
        this.mList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        switch (viewType){
            case TYPE_HEAD:
                ViewGroup viewGroup = (ViewGroup)mInflater.inflate(R.layout.setting_adapter_header, parent,false);
                ViewHeadHolder viewHeadHolder = new ViewHeadHolder(viewGroup);
                return viewHeadHolder;

            case TYPE_CONTENT:
                ViewGroup viewContent = (ViewGroup)mInflater.inflate(R.layout.setting_adapter_content, parent,false);
                ViewContentHolder viewContentHolder = new ViewContentHolder(viewContent);
//             监听点击
                viewContent.setOnClickListener(this);
                return viewContentHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (position==0){
            ViewHeadHolder viewHeadHolder = (ViewHeadHolder)holder;
            viewHeadHolder.topSeperate.setVisibility(View.GONE);
            viewHeadHolder.textView_head.setText(mList.get(position).getTitle());

        }else if (position == 6|| position ==9){
            ViewHeadHolder viewHeadHolder = (ViewHeadHolder)holder;
            viewHeadHolder.textView_head.setText(mList.get(position).getTitle());

        }else if ((position == 5)||(position == mList.size() - 1)||(position == mList.size() - 2)
                ||(position == mList.size() - 3)||(position == mList.size() - 4)){

            ViewContentHolder viewContentHolder = (ViewContentHolder)holder;
            viewContentHolder.switchCompat.setVisibility(View.GONE);
            viewContentHolder.textView_content.setText(mList.get(position).getTitle());
            //将数据保存在itemView的Tag中，以便点击时进行获取
            holder.itemView.setTag(position);
            viewContentHolder.switchCompat.setTag(position);

        }else{
            ViewContentHolder viewContentHolder = (ViewContentHolder)holder;
            viewContentHolder.switchCompat.setVisibility(View.VISIBLE);
            viewContentHolder.switchCompat.setChecked(mList.get(position).isOpen());
            viewContentHolder.textView_content.setText(mList.get(position).getTitle());
            //将数据保存在itemView的Tag中，以便点击时进行获取
            holder.itemView.setTag(position);
            viewContentHolder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mOnSwitchClickListener!=null){
                        mOnSwitchClickListener.onSwitchCLick(buttonView,isChecked, position);
                    }
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0||position == 6|| position ==9){
            return TYPE_HEAD;
        }else {
            return TYPE_CONTENT;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setmOnSwitchClickListener(OnRecycleViewSwitchClickListener mOnSwitchClickListener) {
        this.mOnSwitchClickListener = mOnSwitchClickListener;
    }

    //为item的点击一个定义接口
    public  interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
    //为switchComp的点击一个定义接口
    public interface  OnRecycleViewSwitchClickListener{
        void  onSwitchCLick(CompoundButton buttonView, boolean isSelected, int position);

    }

    @Override
    public void onClick(View v) {

        if (mOnItemClickListener !=null){
//            注意这里使用getTag获取数据
            mOnItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }




    public class ViewHeadHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView textView_head;
        public SwitchCompat switchCompat;
        public View topSeperate;

        public ViewHeadHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.textView_head = (TextView) rootView.findViewById(R.id.setting_apdater_header_text);
            this.switchCompat = (SwitchCompat)rootView.findViewById(R.id.setting_apdater_content_switch);
            this.topSeperate = rootView.findViewById(R.id.topSeperate);

        }
    }

    public class ViewContentHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView textView_content;
        public SwitchCompat switchCompat;

        public ViewContentHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.textView_content = (TextView) rootView.findViewById(R.id.setting_apdater_content_text);
            this.switchCompat = (SwitchCompat)rootView.findViewById(R.id.setting_apdater_content_switch);
        }
    }


}
