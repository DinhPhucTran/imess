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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dpizarro.autolabel.library.AutoLabelUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.haloteam.imess.MainActivity.CHATS_CHILD;
import static com.haloteam.imess.MainActivity.FRIENDS_CHILD;
import static com.haloteam.imess.MainActivity.IMAGES_CHILD;
import static com.haloteam.imess.MainActivity.MEMBERS_CHILD;
import static com.haloteam.imess.MainActivity.PHOTO_URL_CHILD;
import static com.haloteam.imess.MainActivity.TITLE_CHILD;
import static com.haloteam.imess.MainActivity.USERS_CHILD;

public class CreatingGroupActivity extends AppCompatActivity {

    public static class FriendViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView email;
        CircleImageView image;
        CheckBox checkBox;

        public FriendViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.friend_name);
            email = (TextView) itemView.findViewById(R.id.email);
            image = (CircleImageView) itemView.findViewById(R.id.image);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSelectedPos = new ArrayList<>();
        mSelectedFriends = new ArrayList<>();

        //Init Firebase
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.storage_url));
        mDbRef = FirebaseDatabase.getInstance().getReference();
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        initViews();
        getFriends();

        mLabelUI.setOnRemoveLabelListener(new AutoLabelUI.OnRemoveLabelListener() {
            @Override
            public void onRemoveLabel(View view, int position) {
                mSelectedPos.remove(new Integer(position));
                notifyAll();
            }
        });

    }

    private void initViews(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup(view);
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
    }

    private void getFriends(){
        if(mFirebaseAdapter != null)
            mFirebaseAdapter.cleanup();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<User, FriendViewHolder>(User.class,
                R.layout.friend_item_with_check,
                FriendViewHolder.class,
                mDbRef.child(USERS_CHILD).child(mCurrentUserId).child(FRIENDS_CHILD)) {
            @Override
            protected void populateViewHolder(final FriendViewHolder viewHolder, final User model, final int position) {
                viewHolder.name.setText(model.getName());
                viewHolder.email.setText(model.getEmail());
                if(model.getPhotoUrl() != null)
                    Glide.with(CreatingGroupActivity.this).load(model.getPhotoUrl()).into(viewHolder.image);
                else
                    viewHolder.image.setImageDrawable(ContextCompat.getDrawable(
                            CreatingGroupActivity.this,
                            R.drawable.account_circle));
                if(mSelectedPos.contains(position)) {
                    viewHolder.checkBox.setChecked(true);
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
                            mSelectedPos.remove(new Integer(position));
                            mSelectedFriends.remove(model);
                            viewHolder.checkBox.setChecked(false);
                        }
                    }
                });
            }
        };
        mFriendList.setAdapter(mFirebaseAdapter);
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

    public void createGroup(View view) {
        if(mDbRef == null)
            mDbRef = FirebaseDatabase.getInstance().getReference();

        if(mGroupName.getText().toString().isEmpty()){
            Snackbar.make(view, getString(R.string.please_enter_group_name), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            final String groupId = mDbRef.child(CHATS_CHILD).push().getKey();
            mDbRef.child(CHATS_CHILD).child(groupId).child(TITLE_CHILD).setValue(mGroupName.getText().toString());

            //Add current user to group
            mDbRef.child(CHATS_CHILD)
                    .child(groupId)
                    .child(MEMBERS_CHILD)
                    .child(mCurrentUserId)
                    .setValue(MainActivity.getCurrentUser());

            //Add friends to group
            for(User friend : mSelectedFriends) {
                mDbRef.child(CHATS_CHILD)
                        .child(groupId)
                        .child(MEMBERS_CHILD)
                        .child(friend.getId())
                        .setValue(friend);
            }

            //Save group to current user's group list
            mDbRef.child(USERS_CHILD)
                    .child(mCurrentUserId)
                    .child(CHATS_CHILD)
                    .child(groupId)
                    .child(TITLE_CHILD)
                    .setValue(mGroupName.getText().toString());

            StorageReference imageRef = mStorageRef.child("groupimage" + groupId + ".jpg");

            mGroupImage.setDrawingCacheEnabled(true);
            mGroupImage.buildDrawingCache();
            Bitmap bitmap = mGroupImage.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show(this, getString(R.string.creating_group), getString(R.string.uploading_image));

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    exception.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(CreatingGroupActivity.this, getString(R.string.created_group_successfully_but_failed_to_upload_image), Toast.LENGTH_LONG).show();
                    finish();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    //Save photo URL to group
                    mDbRef.child(CHATS_CHILD).child(groupId).child(PHOTO_URL_CHILD).setValue(downloadUrl.toString());

                    //Save photo URL to current user's group list
                    mDbRef.child(USERS_CHILD)
                            .child(mCurrentUserId)
                            .child(CHATS_CHILD)
                            .child(groupId)
                            .child(PHOTO_URL_CHILD)
                            .setValue(downloadUrl.toString());

                    Toast.makeText(CreatingGroupActivity.this, getString(R.string.created_group_successfully), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }
            });

        }

    }

}
