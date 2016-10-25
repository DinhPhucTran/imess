package com.haloteam.imess.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dpizarro.autolabel.library.AutoLabelUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haloteam.imess.R;
import com.haloteam.imess.fragment.FriendsFragment;
import com.haloteam.imess.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.haloteam.imess.MainActivity.FRIENDS_CHILD;
import static com.haloteam.imess.MainActivity.USERS_CHILD;

public class CreatingGroupActivity extends AppCompatActivity {

    public static class FriendViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView email;
        CircleImageView image;
        CheckBox checkBox;

        public FriendViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tvName);
            email = (TextView) itemView.findViewById(R.id.tvMail);
            image = (CircleImageView) itemView.findViewById(R.id.image);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

    private CircleImageView mGroupImage;
    private AutoLabelUI mLabelUI;
    private EditText mGroupName;
    private RecyclerView mFriendList;
    private List<User> mSelectedFriends;
    private List<Integer> mSelectedPos;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private DatabaseReference mDbRef;
    private String mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        getFriends();

    }

    private void initViews(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mGroupImage = (CircleImageView) findViewById(R.id.group_image);
        mGroupName = (EditText) findViewById(R.id.et_group_name);
        mFriendList = (RecyclerView) findViewById(R.id.recycler_view_friends);
        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(this));
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void getFriends(){
        mDbRef = FirebaseDatabase.getInstance().getReference();
        if(mFirebaseAdapter != null)
            mFirebaseAdapter.cleanup();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<User, FriendViewHolder>(User.class,
                R.layout.user_item,
                FriendViewHolder.class,
                mDbRef.child(USERS_CHILD).child(mCurrentUserId).child(FRIENDS_CHILD)) {
            @Override
            protected void populateViewHolder(FriendViewHolder viewHolder, final User model, int position) {
                viewHolder.name.setText(model.getName());
                viewHolder.email.setText(model.getEmail());
                if(model.getPhotoUrl() != null)
                    Glide.with(CreatingGroupActivity.this).load(model.getPhotoUrl()).into(viewHolder.image);
                else
                    viewHolder.image.setImageDrawable(ContextCompat.getDrawable(
                            CreatingGroupActivity.this,
                            R.drawable.account_circle));
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        mFriendList.setAdapter(mFirebaseAdapter);
    }

}
