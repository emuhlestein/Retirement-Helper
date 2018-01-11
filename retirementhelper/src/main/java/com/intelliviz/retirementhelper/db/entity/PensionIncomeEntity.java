package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.IncomeTypeRules;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.PensionData;
import com.intelliviz.retirementhelper.data.PensionRules;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
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
    private PensionRules mRules;

    public PensionIncomeEntity(long id, int type, String name, AgeData minAge, String monthlyBenefit) {
        super(id, type, name);
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

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof PensionRules) {
            mRules = (PensionRules)rules;
        } else {
            mRules = null;
        }
    }

    public BenefitData getBenefitForAge(AgeData age) {
        if(mRules != null) {
            return mRules.getBenefitForAge(age);
        } else {
            return null;
        }
    }

    @Override
    public List<BenefitData> getBenefitData() {
        if(mRules != null) {
            return mRules.getBenefitData();
        } else {
            return null;
        }
    }

    @Override
    public List<MilestoneData> getMilestones(List<MilestoneAgeEntity> ages, RetirementOptionsEntity rod) {
        List<MilestoneData> milestones = new ArrayList<>();
        if(ages.isEmpty()) {
            return milestones;
        }

        AgeData endAge = rod.getEndAge();
        double monthlyBenefit = Double.parseDouble(this.monthlyBenefit);

        MilestoneData milestone;
        for(MilestoneAgeEntity msad : ages) {
            AgeData age = msad.getAge();
            if(age.isBefore(minAge)) {
                milestone = new MilestoneData(age, endAge, minAge, 0, 0, 0, 0, 0, 0, 0, 0);
            } else {
                int numMonths = endAge.diff(age);

                milestone = new MilestoneData(age, endAge, minAge, monthlyBenefit, 0, 0, 0, numMonths, 0, 0, 0);
            }
            milestones.add(milestone);
        }
        return milestones;
    }

    @Override
    public List<AgeData> getAges() {
        List<AgeData> ages = new ArrayList<>();
        ages.add(minAge);
        return ages;
    }

    public PensionData getMonthlyBenefitForAge(AgeData startAge) {
        if(mRules != null) {
            return mRules.getMonthlyBenefitForAge(startAge);
        } else {
            return null;
        }
    }
}
