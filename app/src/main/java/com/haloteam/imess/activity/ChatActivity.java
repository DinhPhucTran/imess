package com.haloteam.imess.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.haloteam.imess.R;
import com.haloteam.imess.fragment.ChatFragment;
import com.haloteam.imess.fragment.MapFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhonnguyen on 10/20/16.
 */

public class ChatActivity extends AppCompatActivity implements ChatFragment.OnFragmentInteractionListener, MapFragment.OnFragmentInteractionListener {

    public static final String FRIEND_ID = "FriendID";
//    static public User currentUser = new User("a", "abc@gmail.com", "abc", null);
//    List<Message> messages;
    private ChatFragment mChatFragment = null;
    private MapFragment mMapFragment = null;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    public static String mFriendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mFriendId = intent.getStringExtra(FRIEND_ID);

        setContentView(R.layout.activity_chat);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mChatFragment = new ChatFragment();
        mMapFragment = new MapFragment();
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(mViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        //FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //transaction.replace()

//        RecyclerView rvMessage = (RecyclerView) findViewById(R.id.rvChat);
//        messages = Message.createListMessage(30);
//        MessageAdapter adapter = new MessageAdapter(messages, this);
//        rvMessage.setAdapter(adapter);
//        rvMessage.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragment(), "CHAT");
        adapter.addFragment(new MapFragment(), "MAP");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
