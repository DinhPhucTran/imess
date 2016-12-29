package com.haloteam.imess.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haloteam.imess.R;
import com.haloteam.imess.model.User;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.haloteam.imess.MainActivity.FRIENDS_CHILD;

public class AddFriendActivity extends AppCompatActivity {

    private static final String TAG = "addfriendactivity";
    public static final String NOTI_ACCEPT = "btAccept";
    public static final String NOTI_DECLINE = "btDecline";

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView email;
        CircleImageView image;

        public UserViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tvName);
            email = (TextView) itemView.findViewById(R.id.tvMail);
            image = (CircleImageView) itemView.findViewById(R.id.image);
        }
    }

    public static final String USERS_CHILD = "users";
    public static final String EMAIL_CHILD = "email";

    private DatabaseReference mDatabaseReference;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<User, UserViewHolder> mFirebaseAdapter;

    private EditText mSearchText;
    private ImageButton mBtSearch;
    private RecyclerView mUserList;

    private FirebaseUser mUser;
    private String mCurrentUserOneSignalId;
    private String mPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mSearchText = (EditText) findViewById(R.id.etSearch);
        mBtSearch = (ImageButton) findViewById(R.id.btSearch);
        mUserList = (RecyclerView) findViewById(R.id.userList);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mUserList.setLayoutManager(mLinearLayoutManager);
        mUserList.setHasFixedSize(true);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser.getPhotoUrl() != null){
            mPhotoUrl = mUser.getPhotoUrl().toString();
        } else {
            mPhotoUrl = "ic_group";
        }
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                if (registrationId != null)
                    mCurrentUserOneSignalId = userId;
            }
        });

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)){
//                    Toast.makeText(AddFriendActivity.this, "Finding...", Toast.LENGTH_SHORT).show();
                    findFriend(mSearchText.getText().toString());
                }
                return true;
            }
        });

        mBtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mSearchText.getText().toString().isEmpty()){
//                    Toast.makeText(AddFriendActivity.this, "Finding...", Toast.LENGTH_SHORT).show();
                    findFriend(mSearchText.getText().toString());
                }
            }
        });

    }

    private void findFriend(String email){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        if(mFirebaseAdapter != null)
            mFirebaseAdapter.cleanup();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class,
                R.layout.user_item,
                UserViewHolder.class,
                mDatabaseReference.child(USERS_CHILD).orderByChild(EMAIL_CHILD).startAt(email)) {
            @Override
            protected void populateViewHolder(final UserViewHolder viewHolder, final User model, int position) {
                //If this user existed in current user's friend list, exclude him/her
                if(model.getEmail().equals(mUser.getEmail())) {
                    viewHolder.itemView.getLayoutParams().height = 0;
                    viewHolder.itemView.requestLayout();
                } else {
                    viewHolder.name.setText(model.getName());
                    viewHolder.email.setText(model.getEmail());
                    if (model.getPhotoUrl() != null)
                        Glide.with(AddFriendActivity.this).load(model.getPhotoUrl()).into(viewHolder.image);
                    else
                        viewHolder.image.setImageDrawable(ContextCompat.getDrawable(
                                AddFriendActivity.this,
                                R.drawable.account_circle));

                    mDatabaseReference.child(USERS_CHILD)
                            .child(mUser.getUid())
                            .child(FRIENDS_CHILD)
                            .child(model.getId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot mDataSnapshot) {
                                    if(mDataSnapshot.exists()){
                                        viewHolder.itemView.getLayoutParams().height = 0;
                                        viewHolder.itemView.requestLayout();
                                    } else {
                                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    OneSignal.postNotification(new JSONObject("{" +
                                                                    "'contents': {'en':' " + mUser.getDisplayName() + " wants to be your friend.', 'vi': '" + mUser.getDisplayName() + " đã gửi cho bạn yêu cầu kết bạn.'}, " +
                                                                    "'data': {'type': 'friend_request', " +
                                                                    "'sender_id': '" + mUser.getUid() + "', " +
                                                                    "'sender_email': '" + mUser.getEmail() + "', " +
                                                                    "'sender_name': '" + mUser.getDisplayName() + "', " +
                                                                    "'sender_oneSignalId': '" + mCurrentUserOneSignalId + "', " +
                                                                    "'sender_photoUrl': '" + mUser.getPhotoUrl() + "'}, " +
                                                                    "'buttons': [{'id': '" + NOTI_ACCEPT +"', 'text': '" + getString(R.string.accept) +"', 'icon': 'ic_check_white'}, " +
                                                                                "{'id': '" + NOTI_DECLINE + "', 'text': '" + getString(R.string.decline) + "', 'icon': 'ic_close'}], " +
                                                                    "'big_picture': '" + mPhotoUrl + "', " +
                                                                    "'include_player_ids': ['" + model.getOneSignalId() + "']}"),
                                                            new OneSignal.PostNotificationResponseHandler() {
                                                                @Override
                                                                public void onSuccess(JSONObject response) {
                                                                    Log.i(TAG, "postNotification Success: " + response.toString());
                                                                    Toast.makeText(AddFriendActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void onFailure(JSONObject response) {
                                                                    Log.e(TAG, "postNotification Failure: " + response.toString());
                                                                }
                                                            });
                                                    Toast.makeText(AddFriendActivity.this, "Sending request to " + model.getName(), Toast.LENGTH_SHORT).show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError mDatabaseError) {

                                }
                            });

                }
            }
        };
        mUserList.getRecycledViewPool().clear();
        mFirebaseAdapter.notifyDataSetChanged();
        mUserList.setAdapter(mFirebaseAdapter);
    }
}
