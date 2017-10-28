package com.intelliviz.retirementhelper.ui.income;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.IncomeViewDetailsAdapter;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.viewmodel.TaxDeferredDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

public class TaxDeferredDetailsActivity extends AppCompatActivity {

    private IncomeViewDetailsAdapter mAdapter;
    private List<MilestoneData> mMilestones;
    private TaxDeferredDetailsViewModel mViewModel;
    private TaxDeferredIncomeEntity mTDIE;
    private long mId;

    @Bind(R.id.name_text_view)
    TextView mIncomeSourceName;

    @Bind(R.id.current_balance_text_view)
    TextView mCurrentBalance;

    @Bind(R.id.annual_interest_text_view)
    TextView mAnnualInterest;

    @Bind(R.id.monthly_increase_text_view)
    TextView mMonthlyIncrease;

    @Bind(R.id.penalty_age_text_view)
    TextView mPenaltyAge;

    @Bind(R.id.penalty_amount_text_view)
    TextView mPenaltyAmount;

    @Bind(R.id.info_text_view)
    TextView mInfoText;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_deferred_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mId = 0;
        if(intent != null) {
            mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
        }

        mMilestones = new ArrayList<>();
        mAdapter = new IncomeViewDetailsAdapter(this, mMilestones);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));

        TaxDeferredDetailsViewModel.Factory factory = new
                TaxDeferredDetailsViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(TaxDeferredDetailsViewModel.class);

        mViewModel.getMilestones().observe(this, new Observer<List<MilestoneData>>() {
            @Override
            public void onChanged(@Nullable List<MilestoneData> milestones) {
                mAdapter.update(milestones);
            }
        });

        mViewModel.get().observe(this, new Observer<TaxDeferredIncomeEntity>() {
            @Override
            public void onChanged(@Nullable TaxDeferredIncomeEntity tdie) {
                mTDIE = tdie;
                updateUI();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.update();
    }

    private void updateUI() {
        if(mTDIE == null) {
            return;
        }

        mIncomeSourceName.setText(mTDIE.getName());
        String formattedCurrency = SystemUtils.getFormattedCurrency(mTDIE.getBalance());
        mCurrentBalance.setText(formattedCurrency);
        String formattedInterest = mTDIE.getInterest() + "%";
        mAnnualInterest.setText(formattedInterest);
        formattedCurrency = SystemUtils.getFormattedCurrency(mTDIE.getMonthlyIncrease());
        mMonthlyIncrease.setText(formattedCurrency);
        AgeData penaltyAge = SystemUtils.parseAgeString(mTDIE.getMinAge());
        mPenaltyAge.setText(penaltyAge.toString());
        mPenaltyAmount.setText(mTDIE.getPenalty() + "%");

    }
}
