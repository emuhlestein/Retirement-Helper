package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by edm on 5/23/2017.
 */

public class AgeData implements Parcelable, Comparable {
    private int mYear;
    private int mMonth;

    public AgeData() {
        mYear = 0;
        mMonth = 0;
    }

    public AgeData(int year, int month) {
        mYear = year;
        mMonth = month;
    }

    /**
     * Does ageDate come on or before this date.
     * @param age
     * @return
     */
    public boolean isBefore(AgeData age) {
        if(mYear < age.getYear()) {
            return true;
        } else if(mYear == age.getYear()) {
            if(mMonth < age.getMonth() ) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public AgeData subtract(AgeData ageData) {
        int year = mYear - ageData.getYear();
        int month = mMonth - ageData.getMonth();
        if(month < 0) {
            year--;
            month = 12 + month;
        }

        return new AgeData(year, month);
    }

    public float getAge() {
        return (float)(mYear + mMonth / 12.0);
    }

    public int getNumberOfMonths() {
        return mYear * 12 + mMonth;
    }

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        return mMonth;
    }

    public boolean isValid() {
        if(mYear > 0 && mMonth > 0) {
            return true;
        } else {
            return false;
        }
    }

    public AgeData(Parcel in) {
        readFromParcel(in);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(mYear));
        sb.append("y ");
        sb.append(Integer.toString(mMonth));
        sb.append("m");
        return sb.toString();
    }

    public String getUnformattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(mYear));
        sb.append(" ");
        sb.append(Integer.toString(mMonth));
        return sb.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mYear);
        dest.writeInt(mMonth);
    }

    public void readFromParcel(Parcel in) {
        mYear = in.readInt();
        mMonth = in.readInt();
    }

    public static final Parcelable.Creator<AgeData> CREATOR = new Parcelable.Creator<AgeData>()
    {
        @Override
        public AgeData createFromParcel(Parcel in) {
            return new AgeData(in);
        }

        @Override
        public AgeData[] newArray(int size) {
            return new AgeData[size];
        }
    };

    @Override
    public int compareTo(@NonNull Object o) {
        AgeData age = (AgeData)o;
        return getNumberOfMonths()-age.getNumberOfMonths();
    }
}
