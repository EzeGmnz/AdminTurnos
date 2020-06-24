package com.adminturnos.ObjectViews;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.adminturnos.ObjectInterfaces.Service;
import com.adminturnos.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.antonious.materialdaypicker.MaterialDayPicker;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class ViewService extends AbstractFlexibleItem<ViewService.ViewHolderService> implements IFilterable<String> {
    private Service service;
    private List<MaterialDayPicker.Weekday> selectedDays;
    private int parallelism;
    private float price;

    public ViewService(Service service, List<MaterialDayPicker.Weekday> selectedDays, int parallelism, float price) {
        this.service = service;
        this.selectedDays = selectedDays;
        this.parallelism = parallelism;
        this.price = price;
        if (selectedDays == null) {
            this.selectedDays = new ArrayList<>();
        }
    }

    public ViewService(Service service) {
        this(service, null, -1, -1);
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public List<MaterialDayPicker.Weekday> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(List<MaterialDayPicker.Weekday> selectedDays) {
        this.selectedDays = selectedDays;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
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
    public ViewHolderService createViewHolder(final View view, FlexibleAdapter<IFlexible> adapter) {
        view.findViewById(R.id.expandable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View expandedContainer = view.findViewById(R.id.expandedContainer);
                ImageView expandImage = view.findViewById(R.id.expandImage);
                CardView cardView = view.findViewById(R.id.containerCardView);

                if (expandedContainer.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandedContainer.setVisibility(View.VISIBLE);
                    expandImage.setImageResource(R.drawable.ic_baseline_expand_less_24);
                } else {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandedContainer.setVisibility(View.GONE);
                    expandImage.setImageResource(R.drawable.ic_baseline_expand_more_24);
                }
            }
        });

        return new ViewHolderService(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolderService holder, int position, List<Object> payloads) {
        holder.labelName.setText(service.getName());

        if (price != -1) {
            holder.inputPrice.setText(String.valueOf(price));
        }
        if (parallelism != -1) {
            holder.inputParallelism.setText(parallelism);
        }
        for (MaterialDayPicker.Weekday w : selectedDays) {
            holder.dayPicker.setDayEnabled(w, true);
        }
    }

    public static class ViewHolderService extends FlexibleViewHolder {
        TextView labelName;
        TextInputEditText inputPrice, inputParallelism;
        MaterialDayPicker dayPicker;

        public ViewHolderService(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            labelName = view.findViewById(R.id.labelName);
            dayPicker = view.findViewById(R.id.dayPicker);
            inputPrice = view.findViewById(R.id.inputPrice);
            inputParallelism = view.findViewById(R.id.inputParallelism);

            dayPicker.setLocale(new Locale("es", "AR"));
        }
    }
}
