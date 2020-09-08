package com.neteru.mobileussdcodex.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.neteru.mobileussdcodex.R;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.mobileussdcodex.classes.Resources.EMPTY;

public class CountryFragment extends Fragment {

    private String section;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_country, container, false);

        TabLayout tabLayout = root.findViewById(R.id.tabs);
        ViewPager viewPager = root.findViewById(R.id.viewpager);

        Bundle bundle = getArguments();

        if (bundle == null){
            section = EMPTY;
        }else {
            section = bundle.getString("code", EMPTY);
        }

        setViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        return root;
    }

    private void setViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        switch (section){
            case "bn":
                adapter.addFragment(new NetworkFragment(), getString(R.string.MOOV), "bnMoov", "Moov - Bénin");
                adapter.addFragment(new NetworkFragment(), getString(R.string.MTN), "bnMtn", "Moov - Mtn");
                break;

            case "cm":
                adapter.addFragment(new NetworkFragment(), getString(R.string.Orange), "cmOrange", "Orange - Cameroun");
                break;

            case "ci":
                adapter.addFragment(new NetworkFragment(), getString(R.string.MOOV), "ciMoov", "Moov - CI");
                adapter.addFragment(new NetworkFragment(), getString(R.string.MTN), "ciMtn", "Mtn - CI");
                break;

            case "ma":
                adapter.addFragment(new NetworkFragment(), getString(R.string.Orange), "maOrange", "Orange - Mali");
                break;

            case "ni":
                adapter.addFragment(new NetworkFragment(), getString(R.string.MOOV), "niMoov", "Moov - Niger");
                adapter.addFragment(new NetworkFragment(), getString(R.string.Orange), "niOrange", "Orange - Niger");
                break;

            case "ng":
                adapter.addFragment(new NetworkFragment(), getString(R.string.Airtel), "ngAirtel", "Airtel - Nigéria");
                adapter.addFragment(new NetworkFragment(), getString(R.string.ETISALAT), "ngEtisalat", "Etisalat - Nigéria");
                adapter.addFragment(new NetworkFragment(), getString(R.string.GLO), "ngGlo", "Glo - Nigéria");
                adapter.addFragment(new NetworkFragment(), getString(R.string.MTN), "ngMtn", "Mtn - Nigéria");
                break;

            case "sn":
                adapter.addFragment(new NetworkFragment(), getString(R.string.Orange), "snOrange", "Orange - Sénégal");
                break;

            case "tg":
                adapter.addFragment(new NetworkFragment(), getString(R.string.MOOV), "tgMoov", "Moov - Togo");
                adapter.addFragment(new NetworkFragment(), getString(R.string.TOGOCEL), "tgTogocel", "Togocel");
                break;
        }

        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) { super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT); }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title, String fragmentID, String fragmentName) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

            NetworkFragment n = (NetworkFragment)fragment;
            n.setFragmentID(fragmentID);
            n.setFragmentName(fragmentName);
        }

        @Override
        public CharSequence getPageTitle(int position) { return mFragmentTitleList.get(position); }
    }

}
