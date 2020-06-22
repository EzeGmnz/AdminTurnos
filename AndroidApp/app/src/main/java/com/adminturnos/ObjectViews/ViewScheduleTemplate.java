package com.adminturnos.ObjectViews;

import android.view.View;
import android.widget.TextView;

import com.adminturnos.R;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class ViewScheduleTemplate extends AbstractFlexibleItem<ViewScheduleTemplate.ViewHolderTemplate> implements Serializable {

    private static final Map<Integer, String> mapNumberDay = new HashMap<Integer, String>() {{
        put(1, "Dom");
        put(2, "Lun");
        put(3, "Mar");
        put(4, "Mie");
        put(5, "Jue");
        put(6, "Vie");
        put(7, "Sab");
    }};

    private String name;
    private Calendar open, close;
    private List<Integer> days;

    public ViewScheduleTemplate(String name, Calendar open, Calendar close, List<Integer> days) {
        this.name = name;
        this.open = open;
        this.close = close;
        this.days = days;
    }

    public String getName() {
        return name;
    }

    public Calendar getOpen() {
        return open;
    }

    public Calendar getClose() {
        return close;
    }

    public List<Integer> getDays() {
        return days;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.schedule_template_layout;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolderTemplate holder, int position, List<Object> payloads) {
        StringBuilder strInfo = new StringBuilder();
        for (Integer i : days) {
            strInfo.append(mapNumberDay.get(i)).append(" ");
        }
        String strOpen = String.format("%d:%02d", open.get(Calendar.HOUR_OF_DAY), open.get(Calendar.MINUTE));
        String strClose = String.format("%d:%02d", close.get(Calendar.HOUR_OF_DAY), close.get(Calendar.MINUTE));

        holder.labelTemplateName.setText(name);
        holder.labelTemplateInfo.setText(strInfo.toString());
        holder.labelTemplateApertura.setText("Apertura " + strOpen);
        holder.labelTemplateCierre.setText("Cierre " + strClose);
    }

    @Override
    public ViewHolderTemplate createViewHolder(View view, FlexibleAdapter adapter) {
        return new ViewHolderTemplate(view, adapter);
    }

    public static class ViewHolderTemplate extends FlexibleViewHolder {
        public TextView labelTemplateName, labelTemplateInfo, labelTemplateApertura, labelTemplateCierre;

        public ViewHolderTemplate(View view, FlexibleAdapter adapter) {
            super(view, adapter);

            this.labelTemplateName = view.findViewById(R.id.labelTemplateName);
            this.labelTemplateInfo = view.findViewById(R.id.labelTemplateInfo);
            this.labelTemplateApertura = view.findViewById(R.id.labelTemplateApertura);
            this.labelTemplateCierre = view.findViewById(R.id.labelTemplateCierre);
        }
    }

}
