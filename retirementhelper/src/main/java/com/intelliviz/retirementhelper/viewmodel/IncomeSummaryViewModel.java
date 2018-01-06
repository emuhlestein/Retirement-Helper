package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.PensionRules;
import com.intelliviz.retirementhelper.data.Savings401kIncomeRules;
import com.intelliviz.retirementhelper.data.SavingsIncomeRules;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 9/30/2017.
 */

public class IncomeSummaryViewModel extends AndroidViewModel {
    private MutableLiveData<List<BenefitData>> mAmountData = new MutableLiveData<>();
    private AppDatabase mDB;

    public IncomeSummaryViewModel(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAmountDataAsyncTask().execute();
    }

    public LiveData<List<BenefitData>> getList() {
        return mAmountData;
    }

    public void update() {
        new GetAmountDataAsyncTask().execute();
    }

    private class GetAmountDataAsyncTask extends AsyncTask<Void, Void, List<BenefitData>> {

        @Override
        protected List<BenefitData> doInBackground(Void... voids) {
            return  getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<BenefitData> benefitData) {
            mAmountData.setValue(benefitData);
        }
    }

    private List<BenefitData> getAllIncomeSources() {
        List<List<BenefitData>> allIncomeSources = new ArrayList<>();
        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        AgeData endAge = roe.getEndAge();
        switch(roe.getCurrentOption()) {
            case RetirementConstants.INCOME_SUMMARY_MODE:
                return getIncomeSummary(roe);
            case RetirementConstants.REACH_AMOUNT_MODE:
                return getReachAmount(roe);
            case RetirementConstants.REACH_IMCOME_PERCENT_MODE:
                return getIncomeSummary(roe);
            default:

        }
        return getIncomeSummary(roe);
    }

    private List<BenefitData> sumAmounts(AgeData endAge, List<List<BenefitData>> allIncomeSources) {
        List<BenefitData> allAmounts = new ArrayList<>();
        int numIncomeSources = allIncomeSources.size();
        List<IndexAmount> indeces = new ArrayList<>();

        for(int incomeSource = 0; incomeSource < numIncomeSources; incomeSource++) {
            IndexAmount indexAmount = new IndexAmount();
            indexAmount.mBenefitData = allIncomeSources.get(incomeSource);
            indexAmount.currentIndex = 0;
            indeces.add(indexAmount);
        }

        int lastMonth = endAge.getNumberOfMonths();
        for(int currentMonth = 0; currentMonth < lastMonth; currentMonth++) {
            double sumMonthlyAmount = 0;
            double sumBalance = 0;
            for (int incomeSource = 0; incomeSource < numIncomeSources; incomeSource++) {
                double monthlyAmount;
                double balance;
                List<BenefitData> benefitData = indeces.get(incomeSource).mBenefitData;
                int index = indeces.get(incomeSource).currentIndex;
                if(benefitData.get(index).getAge().getNumberOfMonths() == currentMonth) {
                    monthlyAmount = benefitData.get(index).getMonthlyAmount();
                    sumMonthlyAmount += monthlyAmount;

                    balance = benefitData.get(index).getBalance();
                    sumBalance += balance;
                    indeces.get(incomeSource).currentIndex++;
                }
            }

            if(sumMonthlyAmount > 0) {
                //AgeData age, double monthlyAmount, double balance, int balanceState, boolean penalty)
                AgeData age = new AgeData(currentMonth);
                BenefitData sumAmount = new BenefitData(age, sumMonthlyAmount, sumBalance, RetirementConstants.BALANCE_STATE_GOOD, false);
                allAmounts.add(sumAmount);
            }
        }

        return allAmounts;
    }

    private List<BenefitData> getIncomeSummary(RetirementOptionsEntity roe) {
        float desiredBalance = Float.parseFloat(roe.getReachAmount());
        List<List<BenefitData>> allIncomeSources = new ArrayList<>();
        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        AgeData endAge = roe.getEndAge();
        for(SavingsIncomeEntity sie : tdieList) {
            AgeData startAge = sie.getStartAge();
            if(sie.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(), endAge, startAge,
                        Double.parseDouble(sie.getBalance()),
                        Double.parseDouble(sie.getInterest()),
                        Double.parseDouble(sie.getMonthlyAddition()),
                        sie.getWithdrawMode(), Double.parseDouble(sie.getWithdrawAmount()));
                sie.setRules(sir);
                sie.getBalance();
                allIncomeSources.add(sie.getBenefitData());

            } else if(sie.getType() == RetirementConstants.INCOME_TYPE_401K) {

                Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), endAge, startAge, Double.parseDouble(sie.getBalance()),
                        Double.parseDouble(sie.getInterest()), Double.parseDouble(sie.getMonthlyAddition()), sie.getWithdrawMode(),
                        Double.parseDouble(sie.getWithdrawAmount()));
                sie.setRules(tdir);
                allIncomeSources.add(sie.getBenefitData());
            }

        }
        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        for(GovPensionEntity gpie : gpeList) {

            String birthdate = roe.getBirthdate();
            SocialSecurityRules ssr = new SocialSecurityRules(birthdate, endAge);
            gpie.setRules(ssr);
            allIncomeSources.add(gpie.getBenefitData());

        }
        List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
        for(PensionIncomeEntity pie : pieList) {
            AgeData minAge = pie.getMinAge();
            PensionRules pr = new PensionRules(minAge, endAge,  Double.parseDouble(pie.getMonthlyBenefit()));
            pie.setRules(pr);
            allIncomeSources.add(pie.getBenefitData());
        }

        return sumAmounts(endAge, allIncomeSources);
    }

    private List<BenefitData> getReachAmount(RetirementOptionsEntity roe) {
        List<List<BenefitData>> allIncomeSources = new ArrayList<>();
        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        AgeData endAge = roe.getEndAge();

        int currentMonth = 0;
        while(true) {
        for(SavingsIncomeEntity sie : tdieList) {
            AgeData startAge = sie.getStartAge();
            if (sie.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(), endAge, startAge,
                        Double.parseDouble(sie.getBalance()),
                        Double.parseDouble(sie.getInterest()),
                        Double.parseDouble(sie.getMonthlyAddition()),
                        sie.getWithdrawMode(), Double.parseDouble(sie.getWithdrawAmount()));
                sie.setRules(sir);
                allIncomeSources.add(sie.getBenefitData());

            } else if (sie.getType() == RetirementConstants.INCOME_TYPE_401K) {

                Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), endAge, startAge, Double.parseDouble(sie.getBalance()),
                        Double.parseDouble(sie.getInterest()), Double.parseDouble(sie.getMonthlyAddition()), sie.getWithdrawMode(),
                        Double.parseDouble(sie.getWithdrawAmount()));
                sie.setRules(tdir);
                allIncomeSources.add(sie.getBenefitData());
            }
        }
        }
    }

    private static class IndexAmount {
        public List<BenefitData> mBenefitData;
        public int currentIndex;
    }
}
