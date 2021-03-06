package com.intelliviz.retirementhelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intelliviz.db.entity.MilestoneAgeEntity;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.SelectMilestoneAgeListener;

import java.util.List;


/**
 * Adapter for milestones.
 * Created by Ed Muhlestein on 6/22/2017.
 */
public class MilestoneAgeAdapter extends RecyclerView.Adapter<MilestoneAgeAdapter.MilestoneAgeHolder> {
    private SelectMilestoneAgeListener mListener;
    private List<MilestoneAgeEntity> mMilestoneAges;

    /**
     * Constructor.
     * @param milestoneAges The list of milestone ages.
     */
    public MilestoneAgeAdapter(List<MilestoneAgeEntity> milestoneAges) {
        mMilestoneAges = milestoneAges;
    }

    @Override
    public MilestoneAgeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.milestone_age_item, parent, false);
        return new MilestoneAgeHolder(view);
    }

    @Override
    public void onBindViewHolder(MilestoneAgeHolder holder, int position) {
        MilestoneAgeEntity ageData = mMilestoneAges.get(position);
        holder.bindMilestone(ageData);
    }

    @Override
    public int getItemCount() {
        if(mMilestoneAges != null) {
            return mMilestoneAges.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Update the milestone ages.
     * @param ages The new milestone ages.
     */
    public void update(List<MilestoneAgeEntity> ages) {
        mMilestoneAges.clear();
        if(ages != null) {
            mMilestoneAges.addAll(ages);
        }
        notifyDataSetChanged();
    }

    /**
     * Set the listerner for milestones selection.
     * @param listener THe listener.
     */
    public void setOnSelectMilestoneAgeListener(SelectMilestoneAgeListener listener) {
        mListener = listener;
    }

    class MilestoneAgeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {
        private TextView mMilestoneAgeTextView;
        private ImageView mImage;
        private MilestoneAgeEntity mAge;

        private MilestoneAgeHolder(View itemView) {
            super(itemView);
            mMilestoneAgeTextView = itemView.findViewById(R.id.milestone_age_text_view);
            mImage = itemView.findViewById(R.id.overflow_image_view);
            itemView.setOnClickListener(this);
        }

        private void bindMilestone(MilestoneAgeEntity ageData) {
            mMilestoneAgeTextView.setText(AgeUtils.getFormattedAge(ageData.getAge()));
            if(ageData.getId() == 0) {
                mImage.setVisibility(View.GONE);
            }
            mAge = ageData;
        }

        @Override
        public void onClick(View v) {
            if(mListener != null && mAge.getId() != 0) {
                mListener.onSelectMilestoneAge(mAge);
            }
        }
    }
}
