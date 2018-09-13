package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeDetails;
import com.intelliviz.data.IncomeSummaryHelper;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SavingsDataEx;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.db.entity.SavingsDataEntityMapper;
import com.intelliviz.income.data.SavingsViewData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;
import com.intelliviz.repo.SavingsIncomeEntityRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by edm on 10/23/2017.
 */

public class SavingsIncomeViewModel extends AndroidViewModel {
    private LiveData<SavingsViewData> mViewData = new MutableLiveData<>();
    private LiveData<SavingsDataEx> mSource;
    private SavingsIncomeEntityRepo mRepo;
    private LiveData<List<IncomeDetails>> mIncomeDetailsList = new MutableLiveData<>();
    private long mId;

    public SavingsIncomeViewModel(Application application, long incomeId, int incomeType) {
        super(application);
        mRepo = SavingsIncomeEntityRepo.getInstance(application);
        mSource = mRepo.getSavingsDataEx(incomeId);
        subscribe(incomeId, incomeType);
        mId = incomeId;
    }

    private void subscribe(final long id, final int incomeType) {
        mViewData = Transformations.switchMap(mSource,
                new Function<SavingsDataEx, LiveData<SavingsViewData>>() {
                    @Override
                    public LiveData<SavingsViewData> apply(SavingsDataEx input) {
                        RetirementOptions ro = RetirementOptionsMapper.map(input.getROE());
                        SavingsData sd = null;
                        if(input.getSie() != null) {
                            sd = SavingsDataEntityMapper.map(input.getSie());
                        }

                        if(sd == null) {
                            Log.d("SavingsIncomeViewModel", "HERE");
                        }
                        SavingsIncomeHelper helper = new SavingsIncomeHelper(getApplication(), sd, ro, input.getNumRecords());
                        MutableLiveData<SavingsViewData> ldata = new MutableLiveData();
                        ldata.setValue(helper.get(id, incomeType));
                        return ldata;
                    }
                });
    }

    public LiveData<SavingsViewData> get() {
        return mViewData;
    }

    public LiveData<List<IncomeDetails>> getList() {
        return mIncomeDetailsList;
    }

    public void update() {
      mRepo.load(mId);
    }

    public void setData(SavingsData sie) {
        mRepo.setData(SavingsDataEntityMapper.map(sie));
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;
        private long mIncomeId;
        private int mIncomeType;

        public Factory(@NonNull Application application, long incomeId, int incomeType) {
            mApplication = application;
            mIncomeId = incomeId;
            mIncomeType = incomeType;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new SavingsIncomeViewModel(mApplication, mIncomeId, mIncomeType);
        }
    }

    // TODO make utils method
    private List<IncomeDetails> getIncomeDetailsList(List<IncomeSourceEntityBase> incomeSourceList, RetirementOptions ro) {
        List<IncomeData> incomeDataList = IncomeSummaryHelper.getIncomeSummary(incomeSourceList, ro);
        if(incomeDataList == null) {
            return Collections.emptyList();
        }

        List<IncomeDetails> incomeDetails = new ArrayList<>();

        for (IncomeData benefitData : incomeDataList) {
            AgeData age = benefitData.getAge();
            String amount = SystemUtils.getFormattedCurrency(benefitData.getMonthlyAmount());
            String balance = SystemUtils.getFormattedCurrency(benefitData.getBalance());
            String line1 = age.toString() + "   " + amount + "  " + balance;
            IncomeDetails incomeDetail = new IncomeDetails(line1, RetirementConstants.BALANCE_STATE_GOOD, "");
            incomeDetails.add(incomeDetail);
        }

        return incomeDetails;
    }
}
