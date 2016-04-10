/*
 * TeamWork
 * Copyright (C) 2015  FattycatR
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program;  if not, see <http://www.gnu.org/licenses/>.
 */
package me.fattycat.kun.teamwork.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class MainTabPagerAdapter extends FragmentPagerAdapter {
    private FragmentManager mFragmentManager;

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mTitles = new ArrayList<>();

    public MainTabPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mTitles.add(title);
    }

    public void clear() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragments) {
            transaction.remove(fragment);
        }
        transaction.commit();
        mFragments.clear();
        mTitles.clear();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
