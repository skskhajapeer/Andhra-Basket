package com.absket.in;

/**
 * Created by Sreejith on 08-10-2015.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

public class ViewPagerAdapterCategoryProducts extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    public ViewPagerAdapterCategoryProducts(FragmentManager fm, int mNumbOfTabsumb, CharSequence mTitles[]) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {



        if(position%2 == 0){
//            return CategoryProductsFragment.init(position);
            CategoryProductsFragment.iPosition(position);
            return new CategoryProductsFragment();
        }else{
//            return CategoryProductsFragmentNew.init(position);
            CategoryProductsFragmentNew.iPosition(position);
            return new CategoryProductsFragmentNew();
        }

       /* int count = position+1;
        if(count%2==0)
        {
            CategoryProductsFragment2.iPosition(position);
            return new CategoryProductsFragment2();
        }
        else if(count%3==0)
        {
            CategoryProductsFragment3.iPosition(position);
            return new CategoryProductsFragment3();
        }
        else
        {
            CategoryProductsFragment1.iPosition(position);
            return new CategoryProductsFragment1();
        }*/

//        Bundle bundle = new Bundle();
//        bundle.putInt("position", position);
//
//         CategoryProductsFragment f = new CategoryProductsFragment();
//        f.setArguments(bundle);
//        return CategoryProductsFragment.init(position);

//        CategoryProductsFragment.iPosition(position);
//        return new CategoryProductsFragment();

    }

    @Override
    public CharSequence getPageTitle(int position) {
            return Titles[position];
    }


    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
