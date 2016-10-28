package com.haloteam.imess;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haloteam.imess.activity.AddFriendActivity;
import com.haloteam.imess.activity.CreatingGroupActivity;
import com.haloteam.imess.activity.SignInActivity;
import com.haloteam.imess.fragment.FriendsFragment;
import com.haloteam.imess.fragment.GroupsFragment;
import com.haloteam.imess.fragment.RecentFragment;
import com.haloteam.imess.model.Chat;
import com.haloteam.imess.model.User;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        FriendsFragment.OnRecyclerViewScrollListener,
        RecentFragment.OnFragmentInteractionListener,
        GroupsFragment.OnFragmentInteractionListener{

    public static class ChatGroupViewHolder extends RecyclerView.ViewHolder{
        TextView chatName;
        public ChatGroupViewHolder(View itemView) {
            super(itemView);
            chatName = (TextView) itemView.findViewById(R.id.name);
        }
    }

    private static final String TAG = "mainactivity";

    public static final String ANONYMOUS = "anonymous";
    public static final String GROUP_CHILD = "groups";
    public static final String CHATS_CHILD = "chats";
    public static final String FRIENDS_CHILD = "friends";
    public static final String USERS_CHILD = "users";

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private GoogleApiClient mGoogleApiClient;

    private FirebaseRecyclerAdapter<Chat, ChatGroupViewHolder> mFirebaseAdapter;

    private String mUsername;
    private String mPhotoUrl;
    private User mCurrentUser;

    private FrameLayout mFrameContainer;
    private RecentFragment mRecentFragment;
    private GroupsFragment mGroupsFragment;
    private FriendsFragment mFriendsFragment;
    private BottomBar mBottomBar;
    private FloatingActionButton mFab;

    private int mSelectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();

        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        mCurrentUser = new User();

        //Initialize OneSignal
        initOneSignal();

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();

            mCurrentUser.setName(mFirebaseUser.getDisplayName());
            mCurrentUser.setEmail(mFirebaseUser.getEmail());
            mCurrentUser.setId(mFirebaseUser.getUid());
            mCurrentUser.setPhotoUrl(mFirebaseUser.getPhotoUrl().toString());
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(AppInvite.API)
                .build();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

//        mFirebaseAdapter = new FirebaseRecyclerAdapter<Chat, ChatGroupViewHolder>(
//                Chat.class,
//                R.layout.chat_group_item,
//                ChatGroupViewHolder.class,
//                mFirebaseDatabaseReference.child(CHATS_CHILD)) {
//            @Override
//            protected void populateViewHolder(ChatGroupViewHolder viewHolder, Chat chat, int position) {
//                viewHolder.chatName.setText(chat.getName());
//            }
//        };

    }

    private void initViews(){
        mFriendsFragment = new FriendsFragment();
        mRecentFragment = new RecentFragment();
        mGroupsFragment = new GroupsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, mRecentFragment).commit();

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mSelectedTab){
                    case R.id.tab_groups:
                        startActivity(new Intent(MainActivity.this, CreatingGroupActivity.class));
                        break;
                    case R.id.tab_friends:
                        startActivity(new Intent(MainActivity.this, AddFriendActivity.class));
                        break;
                }
            }
        });

        mFrameContainer = (FrameLayout) findViewById(R.id.container);
        mBottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId){
                    case R.id.tab_recent:
                        replaceFragment(mRecentFragment);
                        mFab.hide();
                        mSelectedTab = R.id.tab_recent;
                        break;
                    case R.id.tab_groups:
                        replaceFragment(mGroupsFragment);
                        mFab.show();
                        mSelectedTab = R.id.tab_groups;
                        break;
                    case R.id.tab_friends:
                        replaceFragment(mFriendsFragment);
                        mFab.show();
                        mSelectedTab = R.id.tab_friends;
                        break;
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_singout) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut(){
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        mUsername = ANONYMOUS;
        startActivity(new Intent(this, SignInActivity.class));
    }

    @Override
    public void onConnectionFailed(ConnectionResult mConnectionResult) {

    }

    private void initOneSignal(){
        OneSignal.startInit(this).setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
            @Override
            public void notificationReceived(OSNotification notification) {
//                JSONObject data = notification.payload.additionalData;
//                if(data != null){
//                    Toast.makeText(MainActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
//                }
            }
        }).setNotificationOpenedHandler(new OneSignal.NotificationOpenedHandler() {
            @Override
            public void notificationOpened(OSNotificationOpenResult result) {
                OSNotificationAction.ActionType actionType = result.action.type;
                JSONObject data = result.notification.payload.additionalData;
                String customKey;

                if (data != null) {
//                    Toast.makeText(MainActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
                    String messageType = "";
                    try {
                        messageType = data.getString("type");
                    } catch (JSONException mE) {
                        mE.printStackTrace();
                    }

                    if(messageType.equals("friend_request")){
                        addFriend(data);
                    }
                }

                if (actionType == OSNotificationAction.ActionType.ActionTaken)
                    Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
            }
        }).init();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                if (registrationId != null)
                    mCurrentUser.setOneSignalId(userId);
            }
        });
    }

    private void addFriend(JSONObject object){
        User user = new User();
        try {
            user.setName(object.getString("sender_name"));
            user.setEmail(object.getString("sender_email"));
            user.setOneSignalId(object.getString("sender_oneSignalId"));
            user.setId(object.getString("sender_id"));
            user.setPhotoUrl(object.getString("sender_photoUrl"));

            //Add sender to receiver's friend list
            mFirebaseDatabaseReference.child(USERS_CHILD)
                    .child(mFirebaseAuth.getCurrentUser().getUid())
                    .child(FRIENDS_CHILD)
                    .child(user.getId())
                    .setValue(user);

            //Add receiver to sender's friend list
            mFirebaseDatabaseReference.child(USERS_CHILD)
                    .child(user.getId())
                    .child(FRIENDS_CHILD)
                    .child(mFirebaseAuth.getCurrentUser().getUid())
                    .setValue(mCurrentUser);

            Toast.makeText(this, "Added " + user.getName() + " to your friend list", Toast.LENGTH_SHORT).show();
        } catch (JSONException mE) {
            mE.printStackTrace();
            Toast.makeText(this, "Failed to add friend", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onScrolled(int dy) {
        if(dy > 0)
            mFab.hide();
        else if(dy < 0)
            mFab.show();
    }
}
