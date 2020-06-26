package com.adminturnos.ObjectViews;

import android.view.View;
import android.widget.TextView;

import com.adminturnos.ObjectInterfaces.Service;
import com.adminturnos.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class ViewService extends AbstractFlexibleItem<ViewService.ViewHolderService> implements IFilterable<String> {
    private Service service;

    public ViewService(Service service) {
        this.service = service;
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
    }

    public static class ViewHolderService extends FlexibleViewHolder {
        TextView labelName;

        public ViewHolderService(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            labelName = view.findViewById(R.id.labelName);
        }
    }
}
