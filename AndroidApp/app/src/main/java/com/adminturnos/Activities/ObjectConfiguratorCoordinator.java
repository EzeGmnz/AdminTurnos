package com.adminturnos.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.adminturnos.R;

import java.util.List;

/**
 * ViewPager with id view_pager
 * Next Button with id btn_confirm
 */
public class ObjectConfiguratorCoordinator {

    private PagerAdapter adapter;
    private ViewPager viewPager;
    private Button btnConfirm;
    private ListenerCoordinator listener;
    private Bundle bundle;

    private List<ObjectConfigurator> fragments;

    public ObjectConfiguratorCoordinator(FragmentManager fragmentManager,
                                         View rootView,
                                         List<ObjectConfigurator> fragments,
                                         ListenerCoordinator listener) {

        this.viewPager = rootView.findViewById(R.id.view_pager);
        this.btnConfirm = rootView.findViewById(R.id.btn_confirm);
        this.fragments = fragments;
        this.listener = listener;
        this.adapter = new ObjectConfigPagerAdapter(fragmentManager, fragments);

        btnConfirm.setOnClickListener(new BtnConfirmClickListener());
        viewPager.setAdapter(adapter);
        updateButton();
    }

    private void updateButton() {
        if (!hasNext()) {
            btnConfirm.setText("Confirmar");
        } else {
            btnConfirm.setText("Siguiente");
        }
    }

    private boolean hasNext() {
        return viewPager.getCurrentItem() < adapter.getCount() - 1;
    }

    private boolean hasPrev() {
        return viewPager.getCurrentItem() > 0;
    }

    private void updateBundle() {
        Bundle currentFragBundle = fragments.get(viewPager.getCurrentItem()).getData();

        for (String key : currentFragBundle.keySet()) {
            bundle.putString(key, currentFragBundle.getString(key));
        }

    }

    public void next() {
        if (fragments.get(viewPager.getCurrentItem()).validateData()) {
            updateBundle();
            if (hasNext()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                listener.onFinish(bundle);
            }
            updateButton();
        }
    }

    public void prev() {
        if (hasPrev()) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        } else {
            listener.onCanceled();
        }
        updateButton();
    }

    public interface ListenerCoordinator {
        void onFinish(Bundle bundle);

        void onCanceled();
    }

    private static class ObjectConfigPagerAdapter extends FragmentPagerAdapter {

        private List<ObjectConfigurator> fragmentList;

        public ObjectConfigPagerAdapter(FragmentManager fm, List<ObjectConfigurator> frags) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            fragmentList = frags;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    private class BtnConfirmClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            next();
        }
    }
}
