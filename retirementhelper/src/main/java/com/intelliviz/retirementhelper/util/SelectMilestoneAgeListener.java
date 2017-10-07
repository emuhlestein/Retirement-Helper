package com.intelliviz.retirementhelper.util;

import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;

/**
 * Listener for milestone age selection.
 * Created by Ed Muhlestein on 6/24/2017.
 */

public interface SelectMilestoneAgeListener {
    void onSelectMilestoneAge(MilestoneAgeEntity age);
}
