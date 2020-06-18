package com.adminturnos.ObjectViews;

import android.view.View;
import android.widget.TextView;

import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class ViewPlace extends AbstractFlexibleItem<ViewPlace.ViewHolderPlace> {

    private Place place;

    public ViewPlace(Place place) {
        this.place = place;
    }

    public Place getPlace() {
        return place;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_place_search;
    }

    @Override
    public ViewHolderPlace createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolderPlace(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolderPlace holder, int position, List<Object> payloads) {
        holder.textViewBusinessName.setText(place.getBusinessName());
        holder.textViewAddress.setText(place.getAddress());
    }

    public static class ViewHolderPlace extends FlexibleViewHolder {
        public TextView textViewBusinessName;
        public TextView textViewAddress;

        public ViewHolderPlace(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            this.textViewBusinessName = view.findViewById(R.id.textViewBusinessNamePlaceSearch);
            this.textViewAddress = view.findViewById(R.id.textViewAddressPlaceSearch);

        }
    }
}
