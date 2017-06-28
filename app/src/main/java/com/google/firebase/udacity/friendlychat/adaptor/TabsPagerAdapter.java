package com.google.firebase.udacity.friendlychat.adaptor;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import com.google.firebase.udacity.friendlychat.tabs.ChatFragment;
import com.google.firebase.udacity.friendlychat.tabs.GroupFragment;
import java.lang.ref.WeakReference;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

	private final SparseArray<WeakReference<Fragment>> instantiatedFragments = new SparseArray<>();


	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			ChatFragment downloadFragment=new ChatFragment();
			return downloadFragment;
		case 1:
			GroupFragment gamesFragment= new GroupFragment();

			return gamesFragment;
		default:
			return new ChatFragment();
		}
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}


	@Override public CharSequence getPageTitle(int position) {
		if(position==0){
			return new String("Chat");
		}else {
			return new String("Group");
		}
	}

	@Override
	public Object instantiateItem(final ViewGroup container, final int position) {
		final Fragment fragment = (Fragment) super.instantiateItem(container, position);
		instantiatedFragments.put(position, new WeakReference<>(fragment));
		return fragment;
	}

	@Override
	public void destroyItem(final ViewGroup container, final int position, final Object object) {
		instantiatedFragments.remove(position);
		super.destroyItem(container, position, object);
	}

	@Nullable
	public Fragment getFragment(final int position) {
		final WeakReference<Fragment> wr = instantiatedFragments.get(position);
		if (wr != null) {
			return wr.get();
		} else {
			return null;
		}
	}

}
