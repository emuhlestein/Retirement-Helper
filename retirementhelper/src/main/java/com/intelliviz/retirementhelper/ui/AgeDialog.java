package com.intelliviz.retirementhelper.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;


public class AgeDialog extends DialogFragment {

    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";
    private EditText mYearEditText;
    private EditText mMonthEditText;
    private OnAgeEditListener mListener;

    public interface OnAgeEditListener {
        void onEditAge(String year, String month);
    }

    public static AgeDialog newInstance(String year, String month) {
        Bundle args = new Bundle();
        args.putString(ARG_YEAR, year);
        args.putString(ARG_MONTH, month);
        AgeDialog fragment = new AgeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String year = getArguments().getString(ARG_YEAR);
        String month = getArguments().getString(ARG_MONTH);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.age_year_month_layout, null);

        mYearEditText = view.findViewById(R.id.year_edit_text);
        mMonthEditText = view.findViewById(R.id.month_edit_text);

        mYearEditText.setText(year);
        mMonthEditText.setText(month);

        setCancelable(false);

        return new AlertDialog.Builder(getActivity())
                .setMessage("Add Age")
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                sendResult();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnAgeEditListener) {
            mListener = (OnAgeEditListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void sendResult() {
        String year = mYearEditText.getText().toString();
        String month = mMonthEditText.getText().toString();
        if(mListener != null) {
            mListener.onEditAge(year, month);
        } else {
            if (getTargetFragment() == null) {
                return;
            }

            mListener = (OnAgeEditListener) getTargetFragment();
            mListener.onEditAge(year, month);
        }
    }
}
