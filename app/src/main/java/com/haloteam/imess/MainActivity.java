package com.haloteam.imess;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.haloteam.imess.activity.ChatActivity;
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

import static com.haloteam.imess.activity.AddFriendActivity.NOTI_ACCEPT;
import static com.haloteam.imess.activity.ChatActivity.GROUP_NAME;
import static com.haloteam.imess.activity.ChatActivity.PHOTO_URL;
import static com.haloteam.imess.fragment.GroupsFragment.GROUP_ID;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        FriendsFragment.OnRecyclerViewScrollListener,
        GroupsFragment.OnRecyclerViewScrollListener{

//    public static class ChatGroupViewHolder extends RecyclerView.ViewHolder{
//
//        TextView chatName;
//
//
//        public ChatGroupViewHolder(View itemView) {
//            super(itemView);
//            chatName = (TextView) itemView.findViewById(R.id.name);
//        }
//    }

    private static final String TAG = "mainactivity";
//    private static int REQUEST_CODE_CREATE_GROUP = 1;

//    public static final String ANONYMOUS = "anonymous";
    public static final String GROUPS_CHILD = "groups";
    public static final String CHATS_CHILD = "chats";
    public static final String FRIENDS_CHILD = "friends";
    public static final String USERS_CHILD = "users";
    public static final String TITLE_CHILD = "title";
    public static final String LAST_MESSAGE_CHILD = "lastMessage";
//    public static final String TIMESTAMP_CHILD = "timestamp";
    public static final String MEMBERS_CHILD = "members";
    public static final String MESSAGES_CHILD = "messages";
    public static final String ID_CHILD = "id";
    public static final String IMAGES_CHILD = "images";
    public static final String PHOTO_URL_CHILD = "photoUrl";
    public static final String PRIVATE_CHAT_CHILD = "privateChatID";

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private GoogleApiClient mGoogleApiClient;

//    private FirebaseRecyclerAdapter<Chat, ChatGroupViewHolder> mFirebaseAdapter;

//    private String mUsername;
//    private String mPhotoUrl;
    private static User mCurrentUser;

//    private FrameLayout mFrameContainer;
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
//        mUsername = ANONYMOUS;
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
//            mUsername = mFirebaseUser.getDisplayName();

            mCurrentUser.setName(mFirebaseUser.getDisplayName());
            mCurrentUser.setEmail(mFirebaseUser.getEmail());
            mCurrentUser.setId(mFirebaseUser.getUid());
            if(mFirebaseUser.getPhotoUrl() != null)
                mCurrentUser.setPhotoUrl(mFirebaseUser.getPhotoUrl().toString());
            else
                mCurrentUser.setPhotoUrl("");
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

//        mFrameContainer = (FrameLayout) findViewById(R.id.container);
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
//        mUsername = ANONYMOUS;
        startActivity(new Intent(this, SignInActivity.class));
    }

    @Override
    public void onConnectionFailed(ConnectionResult mConnectionResult) {

    }

    private void initOneSignal(){
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
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
                    String groupId = "";
                    String photoUrl = "";
                    String groupName = "";
                    String senderName = "";
                    try {
                        messageType = data.getString("type");
                        groupId = data.getString("groupId");
                        groupName = data.getString("groupName");
                        photoUrl = data.getString("photoUrl");
                        senderName = data.getString("senderName");
                        if(groupName.equals(mCurrentUser.getName()))
                            groupName = senderName;
//                        Toast.makeText(MainActivity.this, groupId + "/" + groupName + "/" + photoUrl, Toast.LENGTH_SHORT).show();
                    } catch (JSONException mE) {
                        mE.printStackTrace();
                    }

                    if(messageType.equals("friend_request")){
                        if (actionType == OSNotificationAction.ActionType.ActionTaken){
                            if(result.action.actionID.equals(NOTI_ACCEPT)) {
                                addFriend(data);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, R.string.declined_request, Toast.LENGTH_SHORT).show();
                        }
                    } else if(messageType.equals("message")){
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        intent.putExtra(GROUP_ID, groupId);
                        intent.putExtra(GROUP_NAME, groupName);
                        intent.putExtra(PHOTO_URL, photoUrl);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }

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
        String currentUserId = "";
        try{
            currentUserId = mFirebaseAuth.getCurrentUser().getUid();
        } catch (NullPointerException e){

        }
        try {
            user.setName(object.getString("sender_name"));
            user.setEmail(object.getString("sender_email"));
            user.setOneSignalId(object.getString("sender_oneSignalId"));
            user.setId(object.getString("sender_id"));
            user.setPhotoUrl(object.getString("sender_photoUrl"));

            //Add sender to receiver's friend list
            mFirebaseDatabaseReference.child(USERS_CHILD)
                    .child(currentUserId)
                    .child(FRIENDS_CHILD)
                    .child(user.getId())
                    .setValue(user);

            //Add receiver to sender's friend list
            mFirebaseDatabaseReference.child(USERS_CHILD)
                    .child(user.getId())
                    .child(FRIENDS_CHILD)
                    .child(currentUserId)
                    .setValue(mCurrentUser);

            //Add sender and receiver to a private chat
            String chatId = mFirebaseDatabaseReference.child(CHATS_CHILD).push().getKey();
//            user.setPrivateChatId(chatId);
            mFirebaseDatabaseReference.child(CHATS_CHILD).child(chatId).child(ID_CHILD).setValue(chatId);
            mFirebaseDatabaseReference.child(CHATS_CHILD).child(chatId).child(MEMBERS_CHILD).child(currentUserId).setValue(mCurrentUser);
            mFirebaseDatabaseReference.child(CHATS_CHILD).child(chatId).child(MEMBERS_CHILD).child(user.getId()).setValue(user);

            //Add private chat ID to current user friend list
//            mFirebaseDatabaseReference.child(USERS_CHILD).child(currentUserId).child(FRIENDS_CHILD).child(user.getId()).child(PRIVATE_CHAT_CHILD).setValue(chatId);
            mFirebaseDatabaseReference.child(USERS_CHILD).child(currentUserId).child(PRIVATE_CHAT_CHILD).child(user.getId()).setValue(chatId);
            mFirebaseDatabaseReference.child(USERS_CHILD).child(user.getId()).child(PRIVATE_CHAT_CHILD).child(currentUserId).setValue(chatId);

            Toast.makeText(this, "Added " + user.getName() + " to your friend list", Toast.LENGTH_SHORT).show();

        } catch (JSONException mE) {
            mE.printStackTrace();
            Toast.makeText(this, "Failed to add friend", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onScrolled(int dy) {
        if(dy > 0)
            mFab.hide();
        else if(dy < 0)
            mFab.show();
    }

    public static User getCurrentUser(){
        return mCurrentUser;
    }
}
