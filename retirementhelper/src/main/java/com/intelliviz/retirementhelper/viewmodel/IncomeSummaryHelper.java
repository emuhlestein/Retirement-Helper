package com.intelliviz.retirementhelper.viewmodel;


import com.intelliviz.income.data.AgeData;
import com.intelliviz.income.data.IncomeData;
import com.intelliviz.income.data.IncomeDataAccessor;
import com.intelliviz.income.data.PensionRules;
import com.intelliviz.income.data.Savings401kIncomeRules;
import com.intelliviz.income.data.SavingsIncomeRules;
import com.intelliviz.income.data.SocialSecurityRules;
import com.intelliviz.income.db.AppDatabase;
import com.intelliviz.income.db.entity.GovPensionEntity;
import com.intelliviz.income.db.entity.PensionIncomeEntity;
import com.intelliviz.income.db.entity.RetirementOptionsEntity;
import com.intelliviz.income.db.entity.SavingsIncomeEntity;
import com.intelliviz.income.util.AgeUtils;
import com.intelliviz.income.util.RetirementConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by edm on 2/17/2018.
 */

public class IncomeSummaryHelper {
    public static List<IncomeData> getIncomeSummary(AppDatabase mDB, RetirementOptionsEntity roe) {
        List<IncomeData> benefitDataList = new ArrayList<>();

        List<SavingsIncomeEntity> sieList = mDB.savingsIncomeDao().get();
        List<IncomeDataAccessor> incomeSourceEntityList = new ArrayList<>();
        for (SavingsIncomeEntity sie : sieList) {
            if (sie.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(), roe.getEndAge());
                sie.setRules(sir);
                incomeSourceEntityList.add(sie.getIncomeDataAccessor());
            } else if (sie.getType() == RetirementConstants.INCOME_TYPE_401K) {
                Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), roe.getEndAge());
                sie.setRules(tdir);
                incomeSourceEntityList.add(sie.getIncomeDataAccessor());
            }
        }

        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
        for (GovPensionEntity gpe : gpeList) {
            incomeSourceEntityList.add(gpe.getIncomeDataAccessor());
        }

        List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
        for (PensionIncomeEntity pie : pieList) {
            AgeData minAge = pie.getMinAge();
            PensionRules pr = new PensionRules(roe.getBirthdate(), minAge, roe.getEndAge(), Double.parseDouble(pie.getMonthlyBenefit()));
            pie.setRules(pr);
            incomeSourceEntityList.add(pie.getIncomeDataAccessor());
        }

        if(incomeSourceEntityList.isEmpty()) {
            return Collections.emptyList();
        }

        int minYear = 999;
        int maxYear = 0;
        List<Map<Integer, IncomeData>> mapListBenefitData = new ArrayList<>();
        for(int year = minYear; year <= maxYear; year++) {
            for (IncomeDataAccessor incomeDataAccessor : incomeSourceEntityList) {
            /*
            Map<Integer, IncomeData> benefitDataMap = new HashMap<>();
            mapListBenefitData.add(benefitDataMap);
            for(IncomeData benefitData : listBenefitData) {
                int year = benefitData.getAge().getYear();
                benefitDataMap.put(year, benefitData);
                if(year < minYear) {
                    minYear = year;
                }
                if(year > maxYear) {
                    maxYear = year;
                }
            }
            */
            }
        }

        AgeData age = AgeUtils.getAge(roe.getBirthdate());
        AgeData endAge = roe.getEndAge();

        for(int year = age.getYear(); year <= endAge.getYear(); year++) {
            double sumBalance = 0;
            double sumMonthlyWithdraw = 0;
            //AgeData age = incomeSourceEntityList.get(0).get(0).getAge();

            for(IncomeDataAccessor accessor : incomeSourceEntityList) {
                IncomeData benefitData = accessor.getIncomeData(new AgeData(year, 0));
                if(benefitData != null) {
                    sumBalance += benefitData.getBalance();
                    sumMonthlyWithdraw += benefitData.getMonthlyAmount();
                }
            }

            benefitDataList.add(new IncomeData(new AgeData(year, 0), sumMonthlyWithdraw, sumBalance, 0, false));
/*
            for (List<IncomeData> bdList : incomeSourceEntityList) {
                benefitData = bdList.get(year);
                age = new AgeData(benefitData.getAge().getNumberOfMonths());
                sumBalance += benefitData.getBalance();
                sumMonthlyWithdraw += benefitData.getMonthlyAmount();
            }
*/
        }

        return benefitDataList;
    }
}
