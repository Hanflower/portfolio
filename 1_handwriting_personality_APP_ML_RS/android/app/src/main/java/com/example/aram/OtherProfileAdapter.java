package com.example.aram;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

//profilefragment 뷰 선택 어댑터
public class OtherProfileAdapter extends FragmentStatePagerAdapter {
    private int numberOfFragment;

    public OtherProfileAdapter(FragmentManager fragmentManager, int numberOfFragment) {
        super(fragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numberOfFragment = numberOfFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new OtherProfilePostFragment();
            default:
                return new OtherProfileScrapFragment();
        }
    }

    @Override
    public int getCount() {
        return numberOfFragment;
    }
}