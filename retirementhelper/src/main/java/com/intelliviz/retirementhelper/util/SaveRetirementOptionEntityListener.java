package com.intelliviz.retirementhelper.util;

import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;

/**
 * Created by edm on 12/22/2017.
 */

public interface SaveRetirementOptionEntityListener {
    void onSaveRetirementOptionEntity(RetirementOptionsEntity roe);
}