package com.intelliviz.retirementhelper.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.services.PersonalDataService;
import com.intelliviz.retirementhelper.services.RetirementOptionsService;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_TYPE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_PERSONAL_INFO;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_RETIRE_OPTIONS;

/**
 * Created by edm on 4/26/2017.
 */

public class SystemUtils {

    public static boolean onActivityResultForOptionMenu (Context context, int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_RETIRE_OPTIONS:
                if (resultCode == RESULT_OK) {
                    RetirementOptionsData rod = intent.getParcelableExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA);
                    updateROD(context, rod);
                }
                return false;
            case REQUEST_PERSONAL_INFO:
                if (resultCode == RESULT_OK) {
                    PersonalInfoData pid = intent.getParcelableExtra(RetirementConstants.EXTRA_PERSONALINFODATA);
                    updatePID(context, pid);
                }
                return false;
            default:
                return true;
        }
    }

    public static boolean validateBirthday(String birthdate) {
        String[] tokens = birthdate.split("-");
        if(tokens.length != 3) {
            return false;
        }

        // Validate year
        if(tokens[0].length() != 4) {
            return false;
        }
        if(!tokens[0].matches("[0-9][0-9][0-9][0-9]")) {
            return false;
        }

        // Validate month
        if(tokens[1].length() != 2) {
            return false;
        }
        if(!tokens[1].matches("[0-9][0-9]")) {
            return false;
        }

        // Validate day
        if(tokens[2].length() != 2) {
            return false;
        }
        if(!tokens[2].matches("[0-9][0-9]")) {
            return false;
        }

        String date = tokens[0]+"-"+tokens[1]+"-"+tokens[2];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(date);
        } catch(ParseException e) {
            return false;
        }

        return true;
    }

    public static void setToolbarSubtitle(AppCompatActivity activity, String subtitle) {
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }
    public static boolean isValidFloatValue(String value) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        Number number;
        try {
            number = nf.parse(value);
            if(number instanceof Float || number instanceof Double || number instanceof Long || number instanceof Integer) {
                return true;
            }
        } catch (ParseException e) {
            return false;
        }
        return false;
    }

    /*
    public static String getFloatValue(String value) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        Number number;
        try {
            number = nf.parse(value);
            if(number instanceof Float || number instanceof Double || number instanceof Long || number instanceof Integer) {
                return number.toString();
            }
        } catch (ParseException e) {
            return null;
        }
        return null;
    }
    */

    public static String getTodaysDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(RetirementConstants.DATE_FORMAT);
        return sdf.format(date);
    }

    public static float getFloatCurrency(String value) {
        Number number1 = 0;
        Number number2 = 0;
        float retval = 0;
        NumberFormat nf = NumberFormat.getCurrencyInstance( java.util.Locale.US );
        NumberFormat numberf = NumberFormat.getNumberInstance(Locale.US);
        Number num1;
        try {
            num1 = numberf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance(java.util.Locale.US);
        try {

            number1 = nf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            number2 = df.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //try {

            //BigDecimal bd = new BigDecimal(value);
            //value = "$1,000.00";
            String fmt = nf.format(value);
            Log.d("SystemUtils", fmt);
            /*
            if(number instanceof Integer) {
                retval = (float)number.intValue();
            } else if(number instanceof Float) {
                retval = number.floatValue();
            } else if(number instanceof Long) {
                retval = (float)number.longValue();
            } else {
                retval = 0;
            }
            */
        //}catch(ParseException e) {
         //   Log.d("SystemUtils", e.toString());
       // }
        return retval;
    }

    public static String getFormattedCurrency(Double value) {
        if (value == null) {
            return null;
        }
        Number number = value;
        double dvalue = number.doubleValue();
        NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        String s = nf.format(dvalue);
        return s;
    }

    public static String getFormattedCurrency(String value) {
        if(value == null || value.isEmpty()) {
            return null;
        }
        NumberFormat nf = NumberFormat.getInstance( java.util.Locale.US );
        try {
            Number number = nf.parse(value);
            double dvalue = number.doubleValue();
            nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
            String s = nf.format(dvalue);
            return s;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getIncomeSourceTypeString(Context context, int incomeSourceType) {
        final String[] incomeTypes = context.getResources().getStringArray(R.array.income_types);
        return incomeTypes[incomeSourceType];
    }

    public static int getBirthYear(String birthdate) {
        String[] birthTokens = birthdate.split("-");

        int birthYear = Integer.parseInt(birthTokens[0]);
        return birthYear;
    }

    public static AgeData getAge(String birthdate) {
        String[] birthTokens = birthdate.split("-");

        int birthYear = Integer.parseInt(birthTokens[0]);
        int birthMonth = Integer.parseInt(birthTokens[1]);
        int birthDay = Integer.parseInt(birthTokens[2]);

        String today = SystemUtils.getTodaysDate();

        String[] nowTokens = today.split("-");

        int nowYear = Integer.parseInt(nowTokens[0]);
        int nowMonth = Integer.parseInt(nowTokens[1]);
        int nowDay = Integer.parseInt(nowTokens[2]);

        int years = nowYear - birthYear;

        int monthDiff = nowMonth - birthMonth;
        if(monthDiff < 0) {
            years--;
        } else if(monthDiff == 0) {
            int dayDiff = nowDay - birthDay;
            if(dayDiff < 0) {
                years--;
            }
        }

        int months = nowMonth - birthMonth;
        if(months < 0) {
            months += 12;
        }
        int dayDiff = nowDay - birthDay;
        if(dayDiff < 0) {
            months--;
        }

        return new AgeData(years, months);
    }

    /**
     * Convert a currency value to a number. Try parsing the value as a number first.
     * If this fails, try parsing as currency. If this fails, return null.
     * @param value The currency value to convert.
     * @return A string with the number value.
     */
    public static String getFloatValue(String value) {
        if(value == null || value.isEmpty()) {
            return null;
        }
        NumberFormat nf = NumberFormat.getInstance( java.util.Locale.US );
        Number number;
        try {
            // try to parse a number
            number = nf.parse(value);
            return number.toString();
        } catch (ParseException e) {
            // ignore exception
        }

        // could not parse number; parse a currency. If this fails, input is invalid.
        nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        try {
            number = nf.parse(value);
            return number.toString();
        } catch (ParseException e) {

        }
        return null; // failed to convert
    }

    public static String getFormattedAge(AgeData ageData) {
        String year = Integer.toString(ageData.getYear());
        String month = Integer.toString(ageData.getMonth());
        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append("y ");
        sb.append(month);
        sb.append("m");
        return sb.toString();
    }

    public static Intent initTaxDeferredIntent(Intent intent, long incomeSourceId, int incomeSourceType, int action, TaxDeferredIncomeData tdid) {
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
        if(tdid != null) {
            intent.putExtra(EXTRA_INCOME_DATA, tdid);
        }
        intent.putExtra(EXTRA_INCOME_SOURCE_TYPE, incomeSourceType);
        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, action);
        return intent;
    }

    public static void initTaxDeferredIntent(Intent srcIntent, Intent dstIntent) {
        long incomeSourceId = srcIntent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
        int incomeSourceType = srcIntent.getIntExtra(EXTRA_INCOME_SOURCE_TYPE, -1);
        TaxDeferredIncomeData tdid = srcIntent.getParcelableExtra(EXTRA_INCOME_DATA);
        int action = srcIntent.getIntExtra(EXTRA_INCOME_SOURCE_ACTION, -1);
        dstIntent.putExtra(EXTRA_INCOME_SOURCE_ID, incomeSourceId);
        dstIntent.putExtra(EXTRA_INCOME_DATA, tdid);
        dstIntent.putExtra(EXTRA_INCOME_SOURCE_TYPE, incomeSourceType);
        dstIntent.putExtra(EXTRA_INCOME_SOURCE_ACTION, action);
    }

    private static void updateROD(Context context, RetirementOptionsData rod) {
        Intent intent = new Intent(context, RetirementOptionsService.class);
        intent.putExtra(RetirementConstants.EXTRA_DB_DATA, rod);
        intent.putExtra(RetirementConstants.EXTRA_DB_ACTION, RetirementConstants.SERVICE_DB_UPDATE);
        context.startService(intent);
    }

    private static void updatePID(Context context, PersonalInfoData pid) {
        Intent intent = new Intent(context, PersonalDataService.class);
        intent.putExtra(RetirementConstants.EXTRA_DB_DATA, pid);
        intent.putExtra(RetirementConstants.EXTRA_DB_ACTION, RetirementConstants.SERVICE_DB_UPDATE);
        context.startService(intent);
    }
}
