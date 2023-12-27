package com.example.aram;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

//searchfragment 뷰 선택 어댑터
public class SearchAdapter extends FragmentStatePagerAdapter {
    private int numberOfFragment;
    public SearchAdapter(FragmentManager fragmentManager, int numberOfFragment) {
        super(fragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numberOfFragment = numberOfFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new SearchPostFragment();
            default:
                return new SearchUserFragment();
        }
    }

    @Override
    public int getCount() {
        return numberOfFragment;
    }
}