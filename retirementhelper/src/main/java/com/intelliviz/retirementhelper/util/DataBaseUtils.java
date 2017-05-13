package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.db.RetirementContract;

/**
 * Created by edm on 4/25/2017.
 */

public class DataBaseUtils {
    //
    // Methods for IncomeType table
    //
    public static String addIncomeType(Context context, String name, int type) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.IncomeTypeEntry.COLUMN_NAME, name);
        values.put(RetirementContract.IncomeTypeEntry.COLUMN_TYPE, type);
        Uri uri = context.getContentResolver().insert(RetirementContract.IncomeTypeEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static int saveIncomeType(Context context, long incomeId, String name, int type) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.IncomeTypeEntry.COLUMN_NAME, name);
        values.put(RetirementContract.IncomeTypeEntry.COLUMN_TYPE, type);

        String sid = String.valueOf(incomeId);
        String selectionClause = RetirementContract.IncomeTypeEntry._ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    public static Cursor getIncomeType(Context context, String name) {
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.IncomeTypeEntry.COLUMN_NAME+ " = ?";
        String[] selectionArgs = {name};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public static Cursor getIncomeType(Context context, long incomeId) {
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.IncomeTypeEntry._ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public static IncomeTypeData getIncomeTypeData(Context context, long incomeId) {
        Cursor cursor = getIncomeType(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int nameIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_NAME);
        int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE);
        String name = cursor.getString(nameIndex);
        int type = cursor.getInt(typeIndex);
        return new IncomeTypeData(name, type);
    }

    //
    // Methods for SavingsIncome table
    //

    public static String addSavingsIncome(Context context, long incomeId, String monthlyAdd, String annualInterest) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD, monthlyAdd);
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST, annualInterest);
        Uri uri = context.getContentResolver().insert(RetirementContract.SavingsIncomeEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static int saveSavingsIncomeData(Context context, long incomeId, String monthlyIncrease, String annualInterest) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD, monthlyIncrease);
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST, annualInterest);

        String sid = String.valueOf(incomeId);
        //String selectionClause = RetirementContract.SavingsIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        //String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, null, null);
    }

    public static Cursor getSavingsIncome(Context context, long incomeId) {
        Uri uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.SavingsIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public static SavingsIncomeData getSavingsIncomeData(Context context, long incomeId) {
        IncomeTypeData itd = getIncomeTypeData(context, incomeId);
        if(itd == null) {
            return null;
        }
        Cursor cursor = getSavingsIncome(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int interestIndex = cursor.getColumnIndex(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST);
        int monthAddIndex = cursor.getColumnIndex(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD);
        String interest = cursor.getString(interestIndex);
        String monthAdd = cursor.getString(monthAddIndex);
        return new SavingsIncomeData(itd.getName(), itd.getType(), interest, monthAdd);
    }

    //
    // Methods for TaxDeferredIncome table
    //

    public static String addTaxDeferredIncome(Context context, long incomeId, String minimumAge,
                                              String interest, String monthlyAdd, String penalty, int is401k) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE, minimumAge);
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST, interest);
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD, monthlyAdd);
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY, penalty);
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K, is401k);
        Uri uri = context.getContentResolver().insert(RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static int saveTaxDeferredData(Context context, long incomeId, String minimumAge,
                                          String interest, String monthlyAdd, String penalty, int is401k) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE, minimumAge);
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST, interest);
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD, monthlyAdd);
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY, penalty);
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K, is401k);

        String sid = String.valueOf(incomeId);
        String selectionClause = RetirementContract.TaxDeferredIncomeEntry._ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    public static Cursor getTaxDeferredIncome(Context context, long incomeId) {
        Uri uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String sid = String.valueOf(incomeId);
        String[] selectionArgs = {sid};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public static TaxDeferredIncomeData getTaxDeferredIncomeData(Context context, long incomeId) {
        IncomeTypeData itd = getIncomeTypeData(context, incomeId);
        if(itd == null) {
            return null;
        }

        Cursor cursor = getTaxDeferredIncome(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int minAgeIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE);
        int interestIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST);
        int monthAddIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD);
        int penaltyIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY);
        int is401kIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K);
        String minAge = cursor.getString(minAgeIndex);
        String interest = cursor.getString(interestIndex);
        String monthAdd = cursor.getString(monthAddIndex);
        String penalty = cursor.getString(penaltyIndex);
        int is401k = cursor.getInt(is401kIndex);
        return new TaxDeferredIncomeData(itd.getName(), itd.getType(), minAge, interest, monthAdd, penalty, is401k);
    }

    //
    // Methods for PensionIncome table
    //

    public static String addPensionData(Context context, long incomeId, String monthlyAmount, String startAge) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_MONTH_BENEFIT, monthlyAmount);
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_START_AGE, startAge);
        Uri uri = context.getContentResolver().insert(RetirementContract.PensionIncomeEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static int savePensionData(Context context, long incomeId, String monthlyAmount, String startAge) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_MONTH_BENEFIT, monthlyAmount);
        values.put(RetirementContract.PensionIncomeEntry.COLUMN_START_AGE, startAge);

        String sid = String.valueOf(incomeId);
        String selectionClause = RetirementContract.PensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.PensionIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    public static Cursor getPensionIncome(Context context, long incomeId) {
        Uri uri = RetirementContract.PensionIncomeEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.PensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public static PensionIncomeData getPensionIncomeData(Context context, long incomeId) {
        IncomeTypeData itd = getIncomeTypeData(context, incomeId);
        if(itd == null) {
            return null;
        }
        Cursor cursor = getPensionIncome(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int startAgeIndex = cursor.getColumnIndex(RetirementContract.PensionIncomeEntry.COLUMN_START_AGE);
        int monthlyBenefitIndex = cursor.getColumnIndex(RetirementContract.PensionIncomeEntry.COLUMN_MONTH_BENEFIT);
        String startAge = cursor.getString(startAgeIndex);
        String monthlyBenefit = cursor.getString(monthlyBenefitIndex);
        return new PensionIncomeData(itd.getName(), itd.getType(), startAge, monthlyBenefit);
    }

    //
    // Methods for GovPensionIncome table
    //

    public static String addGovPensionData(Context context, long incomeId, String monthlyAmount, String minimumAge) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT, monthlyAmount);
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE, minimumAge);
        Uri uri = context.getContentResolver().insert(RetirementContract.GovPensionIncomeEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static int saveGovPensionData(Context context, long incomeId, String monthlyAmount, String minimumAge) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT, monthlyAmount);
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE, minimumAge);

        String selectionClause = RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String sid = String.valueOf(incomeId);
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    public static Cursor getGovPensionIncome(Context context, long incomeId) {
        Uri uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public static GovPensionIncomeData getGovPensionIncomeData(Context context, long incomeId) {
        IncomeTypeData itd = getIncomeTypeData(context, incomeId);
        if(itd == null) {
            return null;
        }

        Cursor cursor = getGovPensionIncome(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int minAgeIndex = cursor.getColumnIndex(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE);
        int monthlyBenefitIndex = cursor.getColumnIndex(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT);
        String startAge = cursor.getString(minAgeIndex);
        String monthlyBenefit = cursor.getString(monthlyBenefitIndex);
        return new GovPensionIncomeData(itd.getName(), itd.getType(), startAge, monthlyBenefit);
    }

    //
    // Methods for GovPensionIncome table
    //

    public static String addBalanceData(Context context, long incomeId, String amount, String date) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.BalanceEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.BalanceEntry.COLUMN_AMOUNT, amount);
        values.put(RetirementContract.BalanceEntry.COLUMN_DATE, date);
        Uri uri = context.getContentResolver().insert(RetirementContract.BalanceEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static int saveBalanceData(Context context, long incomeId, String amount, String date) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.BalanceEntry.COLUMN_AMOUNT, amount);
        values.put(RetirementContract.BalanceEntry.COLUMN_DATE, date);

        String sid = String.valueOf(incomeId);
        String selectionClause = RetirementContract.BalanceEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.BalanceEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    public static Cursor getBalances(Context context, long incomeId) {
        Uri uri = RetirementContract.BalanceEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        String selection = RetirementContract.BalanceEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        String sortOrder = RetirementContract.BalanceEntry.COLUMN_DATE + " DESC";
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
    }

    public static BalanceData[] getBalanceData(Context context, long incomeId) {
        Cursor cursor = getBalances(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        BalanceData bd[] = new BalanceData[cursor.getCount()];
        int index = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int balanceIndex = cursor.getColumnIndex(RetirementContract.BalanceEntry.COLUMN_AMOUNT);
            int dateIndex = cursor.getColumnIndex(RetirementContract.BalanceEntry.COLUMN_DATE);
            String balance = cursor.getString(balanceIndex);
            String date = cursor.getString(dateIndex);
            bd[index++] = new BalanceData(balance, date);
        }

        return bd;
    }

    public static int deleteBalance(Context context, long incomeId) {
        String sid = String.valueOf(incomeId);
        Uri uri = RetirementContract.BalanceEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().delete(uri, null, null);
    }
}
