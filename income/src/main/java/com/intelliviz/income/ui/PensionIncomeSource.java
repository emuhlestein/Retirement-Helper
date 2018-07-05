package com.intelliviz.income.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.intelliviz.db.entity.AbstractIncomeSource;

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

/**
 * Created by edm on 3/13/2018.
 */

public class PensionIncomeSource implements IncomeSource {
    private AbstractIncomeSource mIncomeSourceEntity;

    public PensionIncomeSource(AbstractIncomeSource incomeSourceEntity) {
        mIncomeSourceEntity = incomeSourceEntity;
    }

    @Override
    public void startAddActivity(FragmentActivity activity) {
        Intent intent = new Intent(activity, PensionIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, 0);
        activity.startActivity(intent);
    }

    @Override
    public void startEditActivity(FragmentActivity activity) {
        Intent intent = new Intent(activity, PensionIncomeEditActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceEntity.getId());
        activity.startActivity(intent);
    }

    @Override
    public void startDetailsActivity(Context context) {
        Intent intent = new Intent(context, PensionIncomeDetailsActivity.class);
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceEntity.getId());
        context.startActivity(intent);
    }

    @Override
    public AbstractIncomeSource getIncomeSourceEntity() {
        return mIncomeSourceEntity;
    }

    @Override
    public long getId() {
        return mIncomeSourceEntity.getId();
    }
}
