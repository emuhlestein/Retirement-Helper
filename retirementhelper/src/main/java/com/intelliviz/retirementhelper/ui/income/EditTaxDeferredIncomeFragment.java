package com.intelliviz.retirementhelper.ui.income;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.BalanceData;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;
import com.intelliviz.retirementhelper.util.TaxDeferredIncomeData;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Intent.EXTRA_INTENT;
import static com.intelliviz.retirementhelper.util.SystemUtils.getFloatValue;

/**
 * Fragment used for adding tax deferred income sources.
 *
 * @author Ed Muhlestein
 */
public class EditTaxDeferredIncomeFragment extends Fragment {
    public static final String EDIT_TAXDEF_INCOME_FRAG_TAG = "edit taxdef income frag tag";
    private TaxDeferredIncomeData mTDI;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.name_edit_text) EditText mIncomeSourceName;
    @Bind(R.id.balance_text) EditText mBalance;
    @Bind(R.id.annual_interest_text) EditText mAnnualInterest;
    @Bind(R.id.monthly_increase_text) EditText mMonthlyIncrease;
    @Bind(R.id.penalty_age_text) EditText mPenaltyAge;
    @Bind(R.id.penalty_amount_text) EditText mPenaltyAmount;
    @Bind(R.id.add_income_source_button) Button mAddIncomeSource;

    public EditTaxDeferredIncomeFragment() {
        // Required empty public constructor
    }

    public static EditTaxDeferredIncomeFragment newInstance(Intent intent) {
        EditTaxDeferredIncomeFragment fragment = new EditTaxDeferredIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Intent intent = getArguments().getParcelable(EXTRA_INTENT);
            mTDI = intent.getParcelableExtra(RetirementConstants.EXTRA_INCOME_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_tax_deferred_income, container, false);
        ButterKnife.bind(this, view);

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();

        if(mTDI.getId() == -1) {
            ab.setSubtitle(SystemUtils.getIncomeSourceTypeString(getContext(), RetirementConstants.INCOME_TYPE_TAX_DEFERRED));
        } else {
            updateUI();
        }
        mAddIncomeSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIncomeSourceData();
            }
        });

        mBalance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mBalance.setText(formattedString);
                    }
                }
            }
        });

        mMonthlyIncrease.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String formattedString;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mMonthlyIncrease.setText(formattedString);
                    }
                }
            }
        });

        mAnnualInterest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String interest = textView.getText().toString();
                    interest = getFloatValue(interest);
                    if(interest != null) {
                        mAnnualInterest.setText(interest + "%");
                    } else {
                        mAnnualInterest.setText("");
                    }
                }
            }
        });

        mPenaltyAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String interest = textView.getText().toString();
                    interest = getFloatValue(interest);
                    if(interest != null) {
                        mPenaltyAmount.setText(interest + "%");
                    } else {
                        mPenaltyAmount.setText("");
                    }
                }
            }
        });

        return view;
    }

    private void updateUI() {
        if(mTDI.getId() == -1) {
            return;
        }

        String incomeSourceName = mTDI.getName();
        int type = mTDI.getType();
        String incomeSourceTypeString = SystemUtils.getIncomeSourceTypeString(getContext(), type);

        String balanceString;
        List<BalanceData> bd = mTDI.getBalanceDataList();
        if(bd == null) {
            balanceString = "0.00";
        } else {
            balanceString = SystemUtils.getFormattedCurrency(bd.get(0).getBalance());
        }

        String monthlyIncreaseString = SystemUtils.getFormattedCurrency(mTDI.getMonthAddition());
        String minimumAge = mTDI.getMinimumAge();
        String penaltyAmount = mTDI.getPenalty()+"%";

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        ab.setSubtitle(incomeSourceTypeString);
        mIncomeSourceName.setText(incomeSourceName);
        mBalance.setText(balanceString);
        mAnnualInterest.setText(mTDI.getInterest()+"%");
        mMonthlyIncrease.setText(monthlyIncreaseString);
        mPenaltyAge.setText(minimumAge);
        mPenaltyAmount.setText(penaltyAmount);
    }

    public void sendIncomeSourceData() {
        String value = mBalance.getText().toString();
        String balance = getFloatValue(value);
        if(balance == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Balance value is not valid " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mAnnualInterest.getText().toString();
        String interest = getFloatValue(value);
        if(interest == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Interest value is not valid " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mMonthlyIncrease.getText().toString();
        String monthlyIncrease = getFloatValue(value);
        if(monthlyIncrease == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Monthly increase value is not valid " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        value = mPenaltyAmount.getText().toString();
        String penaltyAmount = getFloatValue(value);
        if(penaltyAmount == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Penalty amount is not valid " + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        Intent returnIntent = new Intent();

        String name = mIncomeSourceName.getText().toString();
        String date = SystemUtils.getTodaysDate();

        String minimumAge = mPenaltyAge.getText().toString();
        TaxDeferredIncomeData tdid = new TaxDeferredIncomeData(mTDI.getId(), name, mTDI.getType(), minimumAge, interest, monthlyIncrease, penaltyAmount, 1);
        tdid.addBalance(new BalanceData(balance, date));
        returnIntent.putExtra(RetirementConstants.EXTRA_INCOME_DATA, tdid);

        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }
}
