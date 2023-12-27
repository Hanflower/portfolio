package com.example.aram;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

//homefragment 뷰 선택 어댑터
public class HomeAdapter extends FragmentStatePagerAdapter {
    private int numberOfFragment;

    public HomeAdapter(FragmentManager fragmentManager, int numberOfFragment) {
        super(fragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numberOfFragment = numberOfFragment;

    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RecommendTimelineFragment();
            default:
                return new TimelineFragment();
        }
    }
    @Override
    public int getCount() {
        return numberOfFragment;
    }
    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }
}
