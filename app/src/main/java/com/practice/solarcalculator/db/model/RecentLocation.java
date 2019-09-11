package com.practice.solarcalculator.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Locale;

public class RecentLocation implements Parcelable {

    public static final Creator<RecentLocation> CREATOR = new Creator<RecentLocation>() {
        @Override
        public RecentLocation createFromParcel(Parcel in) {
            return new RecentLocation(in);
        }

        @Override
        public RecentLocation[] newArray(int size) {
            return new RecentLocation[size];
        }
    };

    private String mLocationName;
    private double mLatitude;
    private double mLongitude;

    public RecentLocation() {
        this(null, 0, 0);
    }

    public RecentLocation(String mLocationName, double mLatitude, double mLongitude) {
        this.mLocationName = mLocationName;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    private RecentLocation(Parcel in) {
        mLocationName = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    public String getExtra() {
        return String.format(Locale.getDefault(), "%s, %s", mLatitude, mLongitude);
    }

    public String getLocationName() {
        return mLocationName;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLocationName(String name) {
        this.mLocationName = name;
    }

    public void setLocationCoordinates(double latitude, double longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLocationName);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }

    @NonNull
    @Override
    public String toString() {
        return "RecentLocation{" +
                "mLocationName='" + mLocationName + '\'' +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                '}';
    }
}
