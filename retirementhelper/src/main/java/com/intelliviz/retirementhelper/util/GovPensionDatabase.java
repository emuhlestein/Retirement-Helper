package com.intelliviz.retirementhelper.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;

/**
 * Created by edm on 9/27/2017.
 */

public class GovPensionDatabase {
    private volatile static GovPensionDatabase mINSTANCE;
    private ContentResolver mContentResolver;

    public static GovPensionDatabase getInstance(Context context) {
        if(mINSTANCE == null) {
            synchronized (GovPensionDatabase.class) {
                if(mINSTANCE == null) {

                    mINSTANCE = new GovPensionDatabase(context);
                }
            }
        }
        return mINSTANCE;
    }

    private GovPensionDatabase(Context context) {
        mContentResolver = context.getContentResolver();
    }

    public GovPensionIncomeData getGovPensionIncomeData(long incomeId) {
        GovPensionDatabase.IncomeDataHelper idh = getIncomeTypeData(incomeId);
        if(idh == null) {
            return null;
        }

        Cursor cursor = getGovPensionIncome(incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int minAgeIndex = cursor.getColumnIndex(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE);
        int monthlyBenefitIndex = cursor.getColumnIndex(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT);
        String startAge = cursor.getString(minAgeIndex);
        String monthlyBenefit = cursor.getString(monthlyBenefitIndex);
        double amount = Double.parseDouble(monthlyBenefit);
        cursor.close();
        return new GovPensionIncomeData(incomeId, idh.name, idh.type, startAge, amount);
    }

    private Cursor getGovPensionIncome(long incomeId) {
        Uri uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
        String selection = RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return mContentResolver.query(uri, null, selection, selectionArgs, null);
    }

    private IncomeDataHelper getIncomeTypeData(long incomeId) {
        Cursor cursor = getIncomeType(incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int nameIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_NAME);
        int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE);
        String name = cursor.getString(nameIndex);
        int type = cursor.getInt(typeIndex);
        cursor.close();
        return new IncomeDataHelper(name, type);
    }

    private Cursor getIncomeType(long incomeId) {
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        String selection = RetirementContract.IncomeTypeEntry._ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return mContentResolver.query(uri, null, selection, selectionArgs, null);
    }

    private static class IncomeDataHelper {
        public String name;
        public int type;
        public IncomeDataHelper(String name, int type) {
            this.name = name;
            this.type = type;
        }
    }
}