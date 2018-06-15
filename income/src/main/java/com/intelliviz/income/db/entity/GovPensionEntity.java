package com.intelliviz.income.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;
import android.os.Bundle;

import com.intelliviz.income.data.AgeData;
import com.intelliviz.income.data.IncomeData;
import com.intelliviz.income.data.IncomeDataAccessor;
import com.intelliviz.income.data.IncomeTypeRules;
import com.intelliviz.income.data.SocialSecurityRules;
import com.intelliviz.income.util.RetirementConstants;

import java.util.List;

import static com.intelliviz.income.db.entity.GovPensionEntity.TABLE_NAME;

/**
 * Database table for government pension income source.
 *
 * Created by Ed Muhlestein on 10/2/2017.
 */
@Entity(tableName = TABLE_NAME)
public class GovPensionEntity extends IncomeSourceEntityBase {
    public static final String TABLE_NAME = "gov_pension_income";
    private static final String MONTHLY_BENEFIT_FIELD = "full_monthly_benefit";
    private static final String START_AGE_FIELD = "start_age";
    private static final String SPOUSE_FIELD = "spouse";

    @ColumnInfo(name = MONTHLY_BENEFIT_FIELD)
    private String mFullMonthlyBenefit;

    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = START_AGE_FIELD)
    private AgeData mStartAge;

    @ColumnInfo(name = SPOUSE_FIELD)
    private int mSpouse;

    @Ignore
    private SocialSecurityRules mRules;

    @Ignore
    private boolean mIsPrincipleSpouse;

    @Ignore
    public GovPensionEntity(long id, int type) {
        super(id, type, "");
        mFullMonthlyBenefit = "0";
        mStartAge = new AgeData(0);
        mSpouse = 0;
    }

    /**
     * Constructor.
     * @param id Database id.
     * @param type Type of income source.
     * @param name Name of income source.
     * @param fullMonthlyBenefit Monthly benefit when full retirement age is reached.
     * @param startAge The age at which to start receiving benefits.
     * @param spouse 1 if this is a spouse. 0 otherwise.
     */
    public GovPensionEntity(long id, int type, String name, String fullMonthlyBenefit, AgeData startAge, int spouse) {
        super(id, type, name);
        mFullMonthlyBenefit = fullMonthlyBenefit;
        mStartAge = startAge;
        mSpouse = spouse;
    }

    /**
     * Get the full monthly benefit.
     * @return The full monthly benefit.
     */
    public String getFullMonthlyBenefit() {
        return mFullMonthlyBenefit;
    }

    public AgeData getStartAge() {
        return mStartAge;
    }

    public void setStartAge(AgeData startAge) {
        mStartAge = startAge;
    }

    public int getSpouse() {
        return mSpouse;
    }

    public void setSpouse(int spouse) {
        mSpouse = spouse;
    }

    public AgeData getFullRetirementAge() {
        return mRules.getFullRetirementAge();
    }

    public boolean isPrincipleSpouse() {
        return mIsPrincipleSpouse;
    }

    public void setPrincipleSpouse(boolean principleSpouse) {
        mIsPrincipleSpouse = principleSpouse;
    }

    public double getMonthlyBenefit() {
        if(mRules != null) {
            return mRules.getMonthlyBenefit();
         }else {
            return 0;
        }
    }

    public AgeData getActualStartAge() {
        if(mRules != null) {
            return mRules.getActualStartAge();
        } else {
            return null;
        }
    }

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof SocialSecurityRules) {
            mRules = (SocialSecurityRules)rules;
            Bundle bundle = new Bundle();
            bundle.putString(RetirementConstants.EXTRA_INCOME_FULL_BENEFIT, mFullMonthlyBenefit);
            bundle.putParcelable(RetirementConstants.EXTRA_INCOME_START_AGE, mStartAge);
            mRules.setValues(bundle);
        } else {
            mRules = null;
        }
    }

    @Override
    public List<IncomeData> getIncomeData() {
        if(mRules != null) {
            return mRules.getIncomeData();
        } else {
            return null;
        }
    }

    @Override
    public IncomeDataAccessor getIncomeDataAccessor() {
        if(mRules != null) {
            return mRules.getIncomeDataAccessor();
        } else {
            return null;
        }
    }
}