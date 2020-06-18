package com.adminturnos.ObjectViews;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.adminturnos.ObjectInterfaces.JobType;
import com.adminturnos.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class ViewJobTypeSelection extends AbstractFlexibleItem<ViewJobTypeSelection.ViewHolderJobType> implements IFilterable<String> {

    private JobType jobType;
    private boolean isSelected;

    public ViewJobTypeSelection(JobType jobType) {
        this.jobType = jobType;
        this.isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    @Override
    public boolean equals(Object o) {
        return ((ViewJobTypeSelection) o).getJobType().getType().equals(jobType.getType());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.jobtype_item;
    }

    @Override
    public ViewHolderJobType createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolderJobType(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolderJobType holder, int position, List<Object> payloads) {
        holder.textView.setText(jobType.getType());
        holder.checkBox.setChecked(isSelected);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSelected = isChecked;
            }
        });
    }

    @Override
    public boolean filter(String constraint) {
        return this.jobType.getType().toLowerCase().contains(constraint);
    }

    public class ViewHolderJobType extends FlexibleViewHolder {
        public TextView textView;
        public MaterialCheckBox checkBox;

        public ViewHolderJobType(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            this.textView = view.findViewById(R.id.textViewJobTypeName);
            this.checkBox = view.findViewById(R.id.checkBoxJobType);
        }
    }
}
