package com.haloteam.imess.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import com.onesignal.OneSignal;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.haloteam.imess.MainActivity.CHATS_CHILD;
import static com.haloteam.imess.MainActivity.PHOTO_URL_CHILD;
import static com.haloteam.imess.common.Constant.EMAIL_CHILD;
import static com.haloteam.imess.common.Constant.ID_CHILD;
import static com.haloteam.imess.common.Constant.NAME_CHILD;
import static com.haloteam.imess.common.Constant.ONESIGNAL_CHILD;
import static com.haloteam.imess.common.Constant.PHOTOURL_CHILD;
import static com.haloteam.imess.common.Constant.USERS_CHILD;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    public static final int REQUEST_CODE_IMAGE_PICKER = 1;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mFirebaseDatabaseReference;

    private static User mUser;
    private String mOneSignalId;

    private EditText mEtName;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private EditText mEtPasswordConfirm;
    private CircleImageView mProfileImage;
    private ProgressDialog mProgressDialog;

    private String mName;
    private String mEmail;
    private String mPassword;
    private String mPasswordConfirm;
    private String mProfileImagePath;
    private String mPhotoUrl;
    private StorageReference mStorageRef;
    private boolean mUploadedPhoto;
    private boolean mChosePhoto;
    private boolean mUpdatedProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEtName = (EditText) findViewById(R.id.signUpName);
        mEtEmail = (EditText) findViewById(R.id.signUpEmail);
        mEtPassword = (EditText) findViewById(R.id.signUpPassword);
        mEtPasswordConfirm = (EditText) findViewById(R.id.signUpPasswordConfirm);
        mProfileImage = (CircleImageView) findViewById(R.id.profileImage);

        //Because onAuthStateChanged might be called several times by FirebaseAuth, define these properties.
        mUploadedPhoto = false;
        mChosePhoto = false;
        mPhotoUrl = "https://firebasestorage.googleapis.com/v0/b/imess-eaa69.appspot.com/o/account-circle.png?alt=media&token=fb85c0f7-2642-4f1a-8847-f93ba55c83c6";
        mUpdatedProfile = false;

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.authenticating));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                if (registrationId != null)
                    mOneSignalId = userId;
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.storage_url));

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mProgressDialog.dismiss();

                    mUser = new User();
                    mUser.setId(user.getUid());
                    mUser.setEmail(user.getEmail());
                    mUser.setName(user.getDisplayName());

                    if(mOneSignalId != null) {
                        mUser.setOneSignalId(mOneSignalId);
                    }

                    //Save user info to (our) database
                    //Set individual value to avoid losing friend list
                    mFirebaseDatabaseReference.child(USERS_CHILD).child(user.getUid()).child(EMAIL_CHILD).setValue(mUser.getEmail());
                    mFirebaseDatabaseReference.child(USERS_CHILD).child(user.getUid()).child(ID_CHILD).setValue(mUser.getId());
                    mFirebaseDatabaseReference.child(USERS_CHILD).child(user.getUid()).child(ONESIGNAL_CHILD).setValue(mUser.getOneSignalId());

                    if(!mUploadedPhoto && mChosePhoto)
                        uploadProfileImage(user, user.getUid());
                    else
                        mFirebaseDatabaseReference.child(USERS_CHILD).child(user.getUid()).child(PHOTOURL_CHILD).setValue(mPhotoUrl);

                    if(!mUpdatedProfile)
                        updateProfile(user, mName, mPhotoUrl);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public boolean validate(){
        boolean valid = false;

        mName = mEtName.getText().toString();
        if(mName.isEmpty()){
            mEtName.setError(getString(R.string.enter_your_name));
            valid = false;
        } else {
            mEtEmail.setError(null);
            valid = true;
        }

        mEmail = mEtEmail.getText().toString();
        if(mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()){
            mEtEmail.setError(getString(R.string.enter_a_valid_email));
            valid = false;
        } else {
            mEtEmail.setError(null);
            valid = true;
        }

        mPassword = mEtPassword.getText().toString();
        if(mPassword.isEmpty() || mPassword.length() < 6){
            mEtPassword.setError(getString(R.string.password_must_6_char));
            valid = false;
        } else {
            mEtPassword.setError(null);
            valid = true;
        }

        mPasswordConfirm = mEtPasswordConfirm.getText().toString();
        if(mPasswordConfirm.isEmpty() || !mPasswordConfirm.equals(mPassword)){
            mEtPasswordConfirm.setError(getString(R.string.passwords_dont_match));
            valid = false;
        } else{
            mEtPasswordConfirm.setError(null);
            valid = true;
        }

        return valid;
    }

    public void signUp(View view) {
        if(validate()){
            mProgressDialog.show();
            mFirebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                mProgressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_failed),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void signIn(String email, String password){
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(SignUpActivity.this, R.string.authentication_failed,
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
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
            mProfileImagePath = images.get(0).getPath();
            mProfileImage.setImageBitmap(BitmapFactory.decodeFile(mProfileImagePath));
            mChosePhoto = true;
        }
    }

    private void uploadProfileImage(final FirebaseUser user, final String userId){
//        final String[] urlArr = new String[1];
//        urlArr[0] = "";

        if(!mUploadedPhoto) {
            StorageReference imageRef = mStorageRef.child("profileimage" + userId + ".jpg");


            mProfileImage.setDrawingCacheEnabled(true);
            mProfileImage.buildDrawingCache();
            Bitmap bitmap = mProfileImage.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.uploading_profile_image));
            dialog.show();

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    exception.printStackTrace();
                    dialog.dismiss();
                    Toast.makeText(SignUpActivity.this, getString(R.string.upload_profile_image_failed), Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    dialog.dismiss();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mPhotoUrl = downloadUrl.toString();
                    mFirebaseDatabaseReference.child(USERS_CHILD).child(userId).child(PHOTOURL_CHILD).setValue(mPhotoUrl);

                    //Re-update photo URL
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Updated photo URL.");
                                    }
                                }
                            });
                }
            });
            mUploadedPhoto = true;
        }
    }

    private void updateProfile(final FirebaseUser user, final String name, String photoUrl){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(Uri.parse(photoUrl))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseDatabaseReference.child(USERS_CHILD).child(user.getUid()).child(NAME_CHILD).setValue(name);
                            Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_succeed), Toast.LENGTH_SHORT).show();
                            mUpdatedProfile = true;
                            signIn(mEmail, mPassword);
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }
}
