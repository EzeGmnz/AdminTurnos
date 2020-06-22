package com.adminturnos.ObjectViews;

import android.view.View;

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

    @Override
    public boolean filter(String constraint) {
        return false;
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
    public ViewHolderService createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolderService(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolderService holder, int position, List<Object> payloads) {

    }

    public static class ViewHolderService extends FlexibleViewHolder {
        public ViewHolderService(View view, FlexibleAdapter adapter) {
            super(view, adapter);
        }
    }
}
