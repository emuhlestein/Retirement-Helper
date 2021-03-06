package com.intelliviz.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

import static com.intelliviz.db.entity.PensionIncomeEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 *
 */
@Entity(tableName = TABLE_NAME)
public class PensionIncomeEntity extends IncomeSourceEntityBase {
    public static final String TABLE_NAME = "pension_income";
    public static final String MIN_AGE_FIELD = "min_age";
    public static final String MONTHLY_BENEFIT_FIELD = "monthly_benefit";

    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = MIN_AGE_FIELD)
    private AgeData minAge;

    @ColumnInfo(name = MONTHLY_BENEFIT_FIELD)
    private String monthlyBenefit;

    @Ignore
    public PensionIncomeEntity(long id, int type) {
        super(id, type, "", RetirementConstants.OWNER_PRIMARY, 1);
        this.minAge = new AgeData(0);
        this.monthlyBenefit = "0";
    }

    @Ignore
    public PensionIncomeEntity() {
        super(0, RetirementConstants.INCOME_TYPE_PENSION, "", RetirementConstants.OWNER_PRIMARY, 1);
        this.minAge = new AgeData(0);
        this.monthlyBenefit = "0";
    }
    //long id, int type, String name, int owner, int included
    public PensionIncomeEntity(long id, int type, String name, int owner, int included, AgeData minAge, String monthlyBenefit) {
        super(id, type, name, owner, included);
        this.minAge = minAge;
        this.monthlyBenefit = monthlyBenefit;
    }

    public AgeData getMinAge() {
        return minAge;
    }

    public void setMinAge(AgeData minAge) {
        this.minAge = minAge;
    }

    public String getMonthlyBenefit() {
        return monthlyBenefit;
    }

    public void setMonthlyBenefit(String monthlyBenefit) {
        this.monthlyBenefit = monthlyBenefit;
    }
}
