package com.adminturnos.ObjectViews;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adminturnos.ObjectInterfaces.JobRequest;
import com.adminturnos.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class ViewJobRequest extends AbstractFlexibleItem<ViewJobRequest.ViewHolderJobRequest> implements IFilterable<Boolean> {

    private JobRequest jobRequest;
    private boolean accepted;
    private boolean denied;

    public ViewJobRequest(JobRequest jobRequest) {
        this.jobRequest = jobRequest;
        this.accepted = false;
        this.denied = false;
    }

    public JobRequest getJobRequest() {
        return jobRequest;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isDenied() {
        return denied;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.job_request_layout;
    }

    @Override
    public ViewHolderJobRequest createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolderJobRequest(view, adapter);
    }

    @Override
    public void bindViewHolder(final FlexibleAdapter<IFlexible> adapter, ViewHolderJobRequest holder, final int position, List<Object> payloads) {
        holder.textViewToPlace.setText(jobRequest.getPlace().getBusinessName());
        holder.textViewFromWho.setText(jobRequest.getCustomUser().getName());

        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accepted = true;
                applyFilter(adapter);
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                denied = true;
                applyFilter(adapter);
            }
        });
    }

    private void applyFilter(FlexibleAdapter<IFlexible> adapter) {
        Boolean filter = adapter.getFilter(Boolean.class);
        boolean next = false;
        if (filter != null) {
            next = !filter;
        }
        adapter.setFilter(next);
        adapter.filterItems();
    }

    @Override
    public boolean filter(Boolean constraint) {
        return !isAccepted() && !isDenied();
    }

    public class ViewHolderJobRequest extends FlexibleViewHolder {
        public TextView textViewToPlace;
        public TextView textViewFromWho;
        public Button btnConfirm, btnCancel;

        public ViewHolderJobRequest(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            this.textViewToPlace = view.findViewById(R.id.textViewToPlace);
            this.textViewFromWho = view.findViewById(R.id.textViewFromWho);
            this.btnCancel = view.findViewById(R.id.btnCancel);
            this.btnConfirm = view.findViewById(R.id.btnConfirm);
        }
    }
}
