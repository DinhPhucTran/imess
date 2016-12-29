package com.haloteam.imess.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haloteam.imess.MainActivity;
import com.haloteam.imess.R;
import com.haloteam.imess.activity.ChatActivity;
import com.haloteam.imess.model.Message;
import com.haloteam.imess.model.User;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;
import static com.haloteam.imess.MainActivity.CHATS_CHILD;
import static com.haloteam.imess.MainActivity.ID_CHILD;
import static com.haloteam.imess.MainActivity.LAST_MESSAGE_CHILD;
import static com.haloteam.imess.MainActivity.MEMBERS_CHILD;
import static com.haloteam.imess.MainActivity.PHOTO_URL_CHILD;
import static com.haloteam.imess.MainActivity.TITLE_CHILD;
import static com.haloteam.imess.MainActivity.USERS_CHILD;

public class ChatFragment extends Fragment {

    public static final String TAG = "ChatFragment";

    public static String MESSAGES_CHILD = "messages";
    public static String RECENTS_CHILD = "recents";

    private String mGroupId;
    private String mGroupName;
    private String mPhotoUrl;
    private DatabaseReference mFirebaseDbRef;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private RecyclerView mRvMessages;
    private Button mBtSend;
    private EditText mEtMessage;

//    static public User currentUser = new User("a", "abc@gmail.com", "abc", null);
//    List<Message> messages;
    private String mCurrentUserId;
    private String mFriendId;
    private String mCurrentUserName;

    private OnFragmentInteractionListener mListener;

    public ChatFragment() {
        // Required empty public constructor
    }

//    public static ChatFragment newInstance(String param1, String param2) {
//        ChatFragment fragment = new ChatFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    public void setGroupId(String id){
        mGroupId = id;
    }

    public void setGroupName(String name){
        mGroupName = name;
    }

    public void setPhotoUrl(String url){
        mPhotoUrl = url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFriendId = ChatActivity.mFriendId;
        mCurrentUserId = MainActivity.getCurrentUser().getId();
        mCurrentUserName = MainActivity.getCurrentUser().getName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setStackFromEnd(true);
        mRvMessages = (RecyclerView) view.findViewById(R.id.rvChat);
        mRvMessages.setLayoutManager(mLinearLayoutManager);

        mBtSend = (Button) view.findViewById(R.id.btSend);
        mEtMessage = (EditText) view.findViewById(R.id.etMessage);

        initChat();

        mEtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mBtSend.setEnabled(true);
                } else {
                    mBtSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mBtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mEtMessage.getText().toString().isEmpty()) {
                    Message message = new Message();
                    message.setSenderName(MainActivity.getCurrentUser().getName());
                    message.setSenderPhotoUrl(MainActivity.getCurrentUser().getPhotoUrl());
                    message.setMessageContent(mEtMessage.getText().toString());
                    message.setTimeStamp(System.currentTimeMillis());
                    message.setSenderId(mCurrentUserId);

                    mFirebaseDbRef.child(MESSAGES_CHILD).child(mGroupId).push().setValue(message);
                    mEtMessage.setText("");

                    //Send notification to other members
                    sendNotis(message.getMessageContent());

                    //Add this chat to recent chats
                    mFirebaseDbRef.child(USERS_CHILD).child(mCurrentUserId).child(RECENTS_CHILD).child(mGroupId).child(ID_CHILD).setValue(mGroupId);
                    mFirebaseDbRef.child(USERS_CHILD).child(mCurrentUserId).child(RECENTS_CHILD).child(mGroupId).child(TITLE_CHILD).setValue(mGroupName);
                    mFirebaseDbRef.child(USERS_CHILD).child(mCurrentUserId).child(RECENTS_CHILD).child(mGroupId).child(PHOTO_URL_CHILD).setValue(mPhotoUrl);
                    mFirebaseDbRef.child(USERS_CHILD).child(mCurrentUserId).child(RECENTS_CHILD).child(mGroupId).child(LAST_MESSAGE_CHILD).setValue(mCurrentUserName + ": " + message.getMessageContent());
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void initChat(){
        mFirebaseDbRef = FirebaseDatabase.getInstance().getReference();
        if(mFirebaseAdapter != null)
            mFirebaseAdapter.cleanup();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");

        final LinearLayout.LayoutParams leftLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        leftLayoutParams.setMargins(5, 5, 5, 40);

        final LinearLayout.LayoutParams rightLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rightLayoutParams.setMargins(40, 5, 5, 5);


        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.message_item,
                MessageViewHolder.class,
                mFirebaseDbRef.child(MESSAGES_CHILD).child(mGroupId)) {

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.senderName.setText(model.getSenderName());
                viewHolder.message.setText(model.getMessageContent());
                Date date = new Date(model.getTimeStamp());
                viewHolder.timeStamp.setText(dateFormat.format(date));

                if(model.getSenderPhotoUrl() != null)
                    Glide.with(getContext()).load(model.getSenderPhotoUrl()).into(viewHolder.senderImage);
                else
                    viewHolder.senderImage.setImageDrawable(ContextCompat.getDrawable(
                            getContext(),
                            R.drawable.account_circle));

                if(model.getSenderId() != null) {
                    if (model.getSenderId().equals(mCurrentUserId)) {
                        viewHolder.senderName.setVisibility(View.GONE);
                        viewHolder.senderImage.setVisibility(View.GONE);
                        viewHolder.message.setBackgroundResource(R.drawable.message_bg_1);
                        viewHolder.message.setPadding(10, 10, 10, 10);
                        viewHolder.layout.setGravity(RIGHT);

                        ViewGroup.LayoutParams lp = ((ViewGroup) viewHolder.layout).getLayoutParams();
                        if( lp instanceof ViewGroup.MarginLayoutParams)
                        {
                            ((ViewGroup.MarginLayoutParams) lp).leftMargin = 40;
                        }

                    } else {
                        viewHolder.senderName.setVisibility(View.VISIBLE);
                        viewHolder.senderImage.setVisibility(View.VISIBLE);
                        viewHolder.message.setBackgroundResource(R.drawable.message_bg_2);
                        viewHolder.message.setPadding(10, 10, 10, 10);
                        viewHolder.layout.setGravity(LEFT);

                        ViewGroup.LayoutParams lp = ((ViewGroup) viewHolder.layout).getLayoutParams();
                        if( lp instanceof ViewGroup.MarginLayoutParams)
                        {
                            ((ViewGroup.MarginLayoutParams) lp).rightMargin = 40;
                        }
                    }
                }

                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(viewHolder.timeStamp.getVisibility() == View.GONE)
                            viewHolder.timeStamp.setVisibility(View.VISIBLE);
                        else
                            viewHolder.timeStamp.setVisibility(View.GONE);
                    }
                });
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int msgCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (msgCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mRvMessages.scrollToPosition(positionStart);
                }
            }
        });

        mRvMessages.setLayoutManager(mLinearLayoutManager);
        mRvMessages.setAdapter(mFirebaseAdapter);
    }

    public void sendNotis(final String message){
        mFirebaseDbRef.child(CHATS_CHILD).child(mGroupId).child(MEMBERS_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Toast.makeText(getContext(), dataSnapshot.getKey().toString()
//                        + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);

                    if(!user.getId().equals(mCurrentUserId)){
                        try {
                            OneSignal.postNotification(new JSONObject("{" +
                                    "'contents':{'en':'" + mCurrentUserName + ": " + message + "'}," +
                                    "'data': {'type': 'message', 'groupId': '" + mGroupId + "', 'groupName': '" + mGroupName +"', 'photoUrl': '" + mPhotoUrl +"', 'senderId': '" + mCurrentUserId +"', 'senderName': '" + mCurrentUserName +"'}," +
                                    "'include_player_ids':['" + user.getOneSignalId() + "']}"),
                                    new OneSignal.PostNotificationResponseHandler() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    Log.d(TAG, response.toString());
                                }

                                @Override
                                public void onFailure(JSONObject response) {
                                    Log.d(TAG, response.toString());
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView senderName;
        public CircleImageView senderImage;
        public TextView timeStamp;
        public LinearLayout layout;

        public MessageViewHolder(View v) {
            super(v);
            message = (TextView) v.findViewById(R.id.message);
            senderImage = (CircleImageView) v.findViewById(R.id.senderImage);
            senderName = (TextView) v.findViewById(R.id.senderName);
            timeStamp = (TextView) v.findViewById(R.id.timeStamp);
            layout = (LinearLayout) v.findViewById(R.id.messageLayout);
        }
    }

}
