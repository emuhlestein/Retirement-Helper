package com.intelliviz.retirementhelper.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DIALOG_MESSAGE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_ACTION_DELETE;

/**
 * A simple {@link Fragment} subclass.
 */
public class YesNoDialog extends AppCompatActivity {
    private long mIncomeSourceId; // TODO get rid of this
    @Bind(R.id.yes_no_message_text)
    TextView mYesNoMessageText;

    @OnClick(R.id.yes_button) void onYesClick() {
        sendResult();
        finish();
    }

    @OnClick(R.id.no_button) void onNoClick() {
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes_no_layout);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mIncomeSourceId = intent.getLongExtra(EXTRA_DB_ID, -1);
        String message = intent.getStringExtra(EXTRA_DIALOG_MESSAGE);
        mYesNoMessageText.setText(message);
    }

    private void sendResult() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_INCOME_SOURCE_ID, mIncomeSourceId);
        intent.putExtra(EXTRA_INCOME_SOURCE_ACTION, INCOME_ACTION_DELETE);
        setResult(Activity.RESULT_OK, intent);
    }
}
