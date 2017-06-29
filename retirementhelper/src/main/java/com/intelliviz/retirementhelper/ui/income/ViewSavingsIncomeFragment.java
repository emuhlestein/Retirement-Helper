package com.intelliviz.retirementhelper.ui.income;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.MilestoneAdapter;
import com.intelliviz.retirementhelper.data.BalanceData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.ui.MilestoneDetailsDialog;
import com.intelliviz.retirementhelper.util.BenefitHelper;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SelectionMilestoneListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_SAVINGS;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewSavingsIncomeFragment extends Fragment implements SelectionMilestoneListener {
    public static final String VIEW_SAVINGS_INCOME_FRAG_TAG = "view savings income frag tag";
    private static final String EXTRA_INTENT = "extra intent";
    private SavingsIncomeData mSID;
    private RetirementOptionsData mROD;
    private MilestoneAdapter mMilestoneAdapter;

    @Bind(R.id.name_text_view) TextView mIncomeSourceName;
    @Bind(R.id.annual_interest_text_view) TextView mAnnualInterest;
    @Bind(R.id.monthly_increase_text_view) TextView mMonthlyIncrease;
    @Bind(R.id.current_balance_text_view) TextView mCurrentBalance;
    @Bind(R.id.monthly_amount_text_view) TextView mMonthlyAmount;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;

    private BroadcastReceiver mRetirementOptionsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intent.getIntExtra(EXTRA_DB_ROWS_UPDATED, -1);
            List<MilestoneData> milestones = BenefitHelper.getMilestones(getContext(), mSID, mROD);
            mMilestoneAdapter.update(milestones);
        }
    };

    public static ViewSavingsIncomeFragment newInstance(Intent intent) {
        ViewSavingsIncomeFragment fragment = new ViewSavingsIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewSavingsIncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Intent intent = getArguments().getParcelable(EXTRA_INTENT);
            if(intent != null) {
                mSID = intent.getParcelableExtra(RetirementConstants.EXTRA_INCOME_DATA);
                mROD = intent.getParcelableExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA);
            }

        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_savings_income, container, false);
        ButterKnife.bind(this, view);
        List<MilestoneData> milestones = BenefitHelper.getMilestones(getContext(), mSID, mROD);
        mMilestoneAdapter = new MilestoneAdapter(getContext(), milestones);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mMilestoneAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        mMilestoneAdapter.setOnSelectionMilestoneListener(this);

        setHasOptionsMenu(true);

        //((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        updateUI();
        return view;
    }

    private void updateUI() {
        if(mSID == null) {
            return;
        }

        mIncomeSourceName.setText(mSID.getName());
        String subTitle = SystemUtils.getIncomeSourceTypeString(getContext(), mSID.getType());
        SystemUtils.setToolbarSubtitle((AppCompatActivity)getActivity(), subTitle);
        mAnnualInterest.setText(mSID.getInterest()+"%");
        mMonthlyIncrease.setText(SystemUtils.getFormattedCurrency(mSID.getMonthlyIncrease()));

        List<BalanceData> bd = mSID.getBalanceDataList(); //getBalanceData(getContext(), mIncomeId);
        String formattedAmount = "$0.00";
        if(bd != null && !bd.isEmpty()) {
            formattedAmount = SystemUtils.getFormattedCurrency(bd.get(0).getBalance());
        }

        mCurrentBalance.setText(String.valueOf(formattedAmount));

        List<MilestoneData> milestones = BenefitHelper.getMilestones(getContext(), mSID, mROD);
        double monthlyAmount = milestones.get(0).getMonthlyBenefit();
        formattedAmount = SystemUtils.getFormattedCurrency(monthlyAmount);

        mMonthlyAmount.setText(formattedAmount);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(LOCAL_SAVINGS);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRetirementOptionsReceiver, filter);
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRetirementOptionsReceiver);
    }

    @Override
    public void onSelectMilestone(MilestoneData msd) {
        Intent intent = new Intent(getContext(), MilestoneDetailsDialog.class);
        intent.putExtra(RetirementConstants.EXTRA_MILESTONEDATA, msd);
        startActivity(intent);
    }
}
