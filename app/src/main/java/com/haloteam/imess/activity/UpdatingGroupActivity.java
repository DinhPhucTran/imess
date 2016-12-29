package com.haloteam.imess.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dpizarro.autolabel.library.AutoLabelUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.haloteam.imess.MainActivity;
import com.haloteam.imess.R;
import com.haloteam.imess.model.User;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.haloteam.imess.MainActivity.CHATS_CHILD;
import static com.haloteam.imess.MainActivity.FRIENDS_CHILD;
import static com.haloteam.imess.MainActivity.MEMBERS_CHILD;
import static com.haloteam.imess.MainActivity.PHOTO_URL_CHILD;
import static com.haloteam.imess.MainActivity.TITLE_CHILD;
import static com.haloteam.imess.MainActivity.USERS_CHILD;
import static com.haloteam.imess.activity.ChatActivity.GROUP_NAME;
import static com.haloteam.imess.activity.ChatActivity.PHOTO_URL;
import static com.haloteam.imess.fragment.GroupsFragment.GROUP_ID;

public class UpdatingGroupActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_IMAGE_PICKER = 1;

    private CircleImageView mGroupImage;
    private AutoLabelUI mLabelUI;
    private EditText mGroupName;
    private RecyclerView mFriendList;

    private List<User> mSelectedFriends;
    private List<Integer> mSelectedPos;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private DatabaseReference mDbRef;
    private String mCurrentUserId;
    private StorageReference mStorageRef;
    private String mGroupImagePath;
    private String mGroupId;
    private String mName;
    private String mPhotoUrl;
    private boolean mIsAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updating_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mGroupId = intent.getStringExtra(GROUP_ID);
        mName = intent.getStringExtra(GROUP_NAME);
        mPhotoUrl = intent.getStringExtra(PHOTO_URL);

        mSelectedPos = new ArrayList<>();
        mSelectedFriends = new ArrayList<>();
        mIsAdded = false;

        //Init Firebase
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.storage_url));
        mDbRef = FirebaseDatabase.getInstance().getReference();
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        initViews();
        getFriends();

        mLabelUI.setOnRemoveLabelListener(new AutoLabelUI.OnRemoveLabelListener() {
            @Override
            public void onRemoveLabel(View view, int position) {
                mSelectedPos.remove(Integer.valueOf(position));
                mFirebaseAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        getFriends();
    }


    private void initViews(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGroup(view);
            }
        });

        mGroupImage = (CircleImageView) findViewById(R.id.group_image);
        mGroupName = (EditText) findViewById(R.id.et_group_name);
        mFriendList = (RecyclerView) findViewById(R.id.recycler_view_friends);
        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(this));

        mLabelUI = (AutoLabelUI) findViewById(R.id.tag_view);
        mGroupImage = (CircleImageView) findViewById(R.id.group_image);
        mGroupName = (EditText) findViewById(R.id.et_group_name);
        mGroupName.setText(mName);

        if(mPhotoUrl != null)
            Glide.with(UpdatingGroupActivity.this).load(mPhotoUrl).into(mGroupImage);

        mDbRef.child(CHATS_CHILD).child(mGroupId).child(MEMBERS_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    User friend = data.getValue(User.class);
                    mSelectedFriends.add(friend);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdatingGroupActivity.this, getResources().getString(R.string.failed_to_get_members), Toast.LENGTH_SHORT).show();
            }
        });

        getWindow().getDecorView().clearFocus();
    }

    private void getFriends(){
        if(mFirebaseAdapter != null)
            mFirebaseAdapter.cleanup();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<User, CreatingGroupActivity.FriendViewHolder>(User.class,
                R.layout.friend_item_with_check,
                CreatingGroupActivity.FriendViewHolder.class,
                mDbRef.child(USERS_CHILD).child(mCurrentUserId).child(FRIENDS_CHILD)) {
            @Override
            protected void populateViewHolder(final CreatingGroupActivity.FriendViewHolder viewHolder, final User model, final int position) {
                viewHolder.name.setText(model.getName());
                viewHolder.email.setText(model.getEmail());
                if(model.getPhotoUrl() != null)
                    Glide.with(UpdatingGroupActivity.this).load(model.getPhotoUrl()).into(viewHolder.image);
                else
                    viewHolder.image.setImageDrawable(ContextCompat.getDrawable(
                            UpdatingGroupActivity.this,
                            R.drawable.account_circle));

                if(mSelectedFriends.contains(model)){
//                    if(!mIsAdded) {
                        mLabelUI.addLabel(model.getName(), position);
//                    }
                    viewHolder.checkBox.setChecked(true);
//                    Toast.makeText(UpdatingGroupActivity.this, model.getName(), Toast.LENGTH_SHORT).show();
                    mSelectedPos.add(position);
                } else {
                    viewHolder.checkBox.setChecked(false);
                }

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean successAdd = false;
                        boolean successRemove = false;
                        if(mSelectedPos.contains(position)){
                            successRemove = mLabelUI.removeLabel(position);
                        } else {
                            successAdd = mLabelUI.addLabel(model.getName(), position);
                        }
                        if(successAdd) {
                            mSelectedPos.add(position);
                            mSelectedFriends.add(model);
                            viewHolder.checkBox.setChecked(true);
                        } else if(successRemove){
                            mSelectedPos.remove(Integer.valueOf(position));
                            mSelectedFriends.remove(model);
                            viewHolder.checkBox.setChecked(false);
                        }
                    }
                });
            }
        };
        mFriendList.setAdapter(mFirebaseAdapter);
        mIsAdded = true;
    }

    public void openImagePicker(View view) {
        ImagePicker.create(this)
                .imageTitle(getString(R.string.tap_to_select))
                .single()
                .showCamera(true)
                .start(REQUEST_CODE_IMAGE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            mGroupImagePath = images.get(0).getPath();
            mGroupImage.setImageBitmap(BitmapFactory.decodeFile(mGroupImagePath));
        }
    }

    public void updateGroup(View view){
        if(mDbRef == null)
            mDbRef = FirebaseDatabase.getInstance().getReference();

        if(mGroupName.getText().toString().isEmpty()){
            Snackbar.make(view, getString(R.string.please_enter_group_name), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            mDbRef.child(CHATS_CHILD).child(mGroupId).child(TITLE_CHILD).setValue(mGroupName.getText().toString());

            //Add friends to group
            for(User friend : mSelectedFriends) {
                mDbRef.child(CHATS_CHILD)
                        .child(mGroupId)
                        .child(MEMBERS_CHILD)
                        .child(friend.getId())
                        .setValue(friend);
            }

            //Save group to current user's group list
            mDbRef.child(USERS_CHILD)
                    .child(mCurrentUserId)
                    .child(CHATS_CHILD)
                    .child(mGroupId)
                    .child(TITLE_CHILD)
                    .setValue(mGroupName.getText().toString());

            StorageReference imageRef = mStorageRef.child("groupimage" + mGroupId + ".jpg");

            mGroupImage.setDrawingCacheEnabled(true);
            mGroupImage.buildDrawingCache();
            Bitmap bitmap = mGroupImage.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show(this, getString(R.string.updating_group), getString(R.string.uploading_image));

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    exception.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(UpdatingGroupActivity.this, getString(R.string.created_group_successfully_but_failed_to_upload_image), Toast.LENGTH_LONG).show();
                    finish();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    //Save photo URL to group
                    mDbRef.child(CHATS_CHILD).child(mGroupId).child(PHOTO_URL_CHILD).setValue(downloadUrl.toString());

                    //Save photo URL to current user's group list
                    mDbRef.child(USERS_CHILD)
                            .child(mCurrentUserId)
                            .child(CHATS_CHILD)
                            .child(mGroupId)
                            .child(PHOTO_URL_CHILD)
                            .setValue(downloadUrl.toString());

                    Toast.makeText(UpdatingGroupActivity.this, getString(R.string.update_group_succeed), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }
            });

        }
    }

}
