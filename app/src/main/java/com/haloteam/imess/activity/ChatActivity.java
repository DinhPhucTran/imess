package com.haloteam.imess.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.haloteam.imess.R;
import com.haloteam.imess.fragment.ChatFragment;

import java.util.ArrayList;
import java.util.List;

import static com.haloteam.imess.fragment.GroupsFragment.GROUP_ID;

public class ChatActivity extends AppCompatActivity implements ChatFragment.OnFragmentInteractionListener {

    public static final String FRIEND_ID = "FriendID";
    public static final String GROUP_NAME = "GroupName";
    public static final String PHOTO_URL = "photoURL";

    private ChatFragment mChatFragment = null;
//    private MapFragment mMapFragment = null;
    private Toolbar mToolbar;
//    private TabLayout mTabLayout;
//    private ViewPager mViewPager;
    public static String mFriendId;
    private String mGroupId;
    private String mGroupName;
    private String mPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mFriendId = intent.getStringExtra(FRIEND_ID);
        mGroupId = intent.getStringExtra(GROUP_ID);
        mGroupName = intent.getStringExtra(GROUP_NAME);
        mPhotoUrl = intent.getStringExtra(PHOTO_URL);

        setContentView(R.layout.activity_chat);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        mChatFragment = new ChatFragment();
//        mMapFragment = new MapFragment();
//        mViewPager = (ViewPager) findViewById(R.id.viewPager);
//        setupViewPager(mViewPager);
//        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
//        mTabLayout.setupWithViewPager(mViewPager);

        //FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //transaction.replace()

        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setGroupId(mGroupId);
        chatFragment.setGroupName(mGroupName);
        chatFragment.setPhotoUrl(mPhotoUrl);

        getSupportFragmentManager().beginTransaction().add(R.id.frame, chatFragment).commit();

    }

//    private void setupViewPager(ViewPager viewPager) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        ChatFragment chatFragment = new ChatFragment();
//        chatFragment.setGroupId(mGroupId);
//        chatFragment.setGroupName(mGroupName);
//        chatFragment.setPhotoUrl(mPhotoUrl);
//        adapter.addFragment(chatFragment, "CHAT");
//        adapter.addFragment(new MapFragment(), "MAP");
//        viewPager.setAdapter(adapter);
//    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_update_group:
                Intent intent = new Intent(this, UpdatingGroupActivity.class);
                intent.putExtra(GROUP_ID, mGroupId);
                intent.putExtra(GROUP_NAME, mGroupName);
                intent.putExtra(PHOTO_URL, mPhotoUrl);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }
}
