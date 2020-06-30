package com.adminturnos.ObjectViews;

import android.view.View;
import android.widget.TextView;

import com.adminturnos.ObjectInterfaces.Service;
import com.adminturnos.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class ViewService extends AbstractFlexibleItem<ViewService.ViewHolderService> implements IFilterable<String> {
    private boolean isProvided;
    private Service service;

    public ViewService(Service service, boolean isProvided) {
        this.isProvided = isProvided;
        this.service = service;
    }

    public void setProvided(boolean provided) {
        isProvided = provided;
    }

    public Service getService() {
        return service;
    }

    @Override
    public boolean filter(String constraint) {
        return service.getName().toLowerCase().contains(constraint);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.view_service_layout;
    }

    @Override
    public ViewHolderService createViewHolder(final View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolderService(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolderService holder, int position, List<Object> payloads) {
        holder.labelName.setText(service.getName());

        if (isProvided) {
            holder.labelName.setTextColor(holder.labelName.getResources().getColor(R.color.white));
            holder.cardView.setCardBackgroundColor(holder.cardView.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.labelName.setTextColor(holder.labelName.getContext().getResources().getColor(R.color.black));
            holder.cardView.setCardBackgroundColor(holder.cardView.getResources().getColor(R.color.white));
        }
    }

    public static class ViewHolderService extends FlexibleViewHolder {
        TextView labelName;
        MaterialCardView cardView;

        public ViewHolderService(View view, FlexibleAdapter adapter) {
            super(view, adapter);

            cardView = view.findViewById(R.id.cardViewContainer);
            labelName = view.findViewById(R.id.labelName);
        }
    }
}
