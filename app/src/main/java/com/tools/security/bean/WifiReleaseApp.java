package com.tools.security.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * description:带宽
 * author: xiaodifu
 * date: 2017/1/15.
 */

public class WifiReleaseApp implements Comparable<WifiReleaseApp>, Parcelable {
    private String packageName;
    private long flow;
    private boolean checked;

    public WifiReleaseApp(String packageName, long flow, boolean checked) {
        this.packageName = packageName;
        this.flow = flow;
        this.checked = checked;
    }

    public WifiReleaseApp() {
    }

    protected WifiReleaseApp(Parcel in) {
        packageName = in.readString();
        flow = in.readLong();
        checked = in.readByte() != 0;
    }

    public static final Creator<WifiReleaseApp> CREATOR = new Creator<WifiReleaseApp>() {
        @Override
        public WifiReleaseApp createFromParcel(Parcel in) {
            return new WifiReleaseApp(in);
        }

        @Override
        public WifiReleaseApp[] newArray(int size) {
            return new WifiReleaseApp[size];
        }
    };

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getFlow() {
        return flow;
    }

    public void setFlow(long flow) {
        this.flow = flow;
    }


    @Override
    public int compareTo(WifiReleaseApp o) {
        return (int) (o.getFlow() - flow);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeLong(flow);
        dest.writeByte((byte) (checked ? 1 : 0));
    }

    @Override
    public String toString() {
        return "WifiReleaseApp{" +
                "packageName='" + packageName + '\'' +
                ", flow=" + flow +
                ", checked=" + checked +
                '}';
    }
}
