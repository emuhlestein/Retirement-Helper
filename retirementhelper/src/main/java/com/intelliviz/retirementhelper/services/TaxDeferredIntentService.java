package com.intelliviz.retirementhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.TaxDeferredHelper;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ACTION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_EXTRA_DATA;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ID;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_DB_ROWS_UPDATED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_RETIRE_OPTIONS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.LOCAL_TAX_DEFERRED;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_QUERY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.SERVICE_DB_UPDATE;

public class TaxDeferredIntentService extends IntentService {

    public TaxDeferredIntentService() {
        super("TaxDeferredIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int action = intent.getIntExtra(EXTRA_DB_ACTION, SERVICE_DB_QUERY);
            long id = intent.getLongExtra(EXTRA_DB_ID, -1);
            if(action == SERVICE_DB_QUERY) {
                if(id == -1) {
                    return; // error
                }
                TaxDeferredIncomeData tdid = TaxDeferredHelper.getTaxDeferredIncomeData(this, id);
                RetirementOptionsData rod = DataBaseUtils.getRetirementOptionsData(this);
                if (tdid != null && rod != null) {
                    Intent localIntent = new Intent(LOCAL_TAX_DEFERRED);
                    localIntent.putExtra(EXTRA_DB_DATA, tdid);
                    localIntent.putExtra(EXTRA_DB_EXTRA_DATA, rod);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            } else if(action == SERVICE_DB_UPDATE) {
                TaxDeferredIncomeData tdid = intent.getParcelableExtra(EXTRA_DB_DATA);
                if(tdid != null) {
                    if(id == -1) {
                        String sid = TaxDeferredHelper.addTaxDeferredIncome(this, tdid);
                    } else {
                        int rowsUpdated = TaxDeferredHelper.saveTaxDeferredData(this, tdid);
                        Intent localIntent = new Intent(LOCAL_RETIRE_OPTIONS);
                        localIntent.putExtra(EXTRA_DB_ROWS_UPDATED, rowsUpdated);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                    }
                }
            }
        }
    }
}