package com.haloteam.imess.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haloteam.imess.MainActivity;
import com.haloteam.imess.R;
import com.haloteam.imess.activity.ChatActivity;
import com.haloteam.imess.model.Chat;

import static com.haloteam.imess.MainActivity.CHATS_CHILD;
import static com.haloteam.imess.MainActivity.USERS_CHILD;
import static com.haloteam.imess.activity.ChatActivity.GROUP_NAME;
import static com.haloteam.imess.activity.ChatActivity.PHOTO_URL;
import static com.haloteam.imess.fragment.ChatFragment.RECENTS_CHILD;

public class RecentFragment extends Fragment {
    public static final String GROUP_ID = "groupID";

    private RecyclerView mRecyclerViewGroups;
    private DatabaseReference mFirebaseDbRef;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    public RecentFragment() {
        // Required empty public constructor
    }

    public static RecentFragment newInstance(String param1, String param2) {
        RecentFragment fragment = new RecentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        mRecyclerViewGroups = (RecyclerView) view.findViewById(R.id.recycler_groups);
        mRecyclerViewGroups.setHasFixedSize(true);
        mRecyclerViewGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        initGroupList();
        mRecyclerViewGroups.setAdapter(mFirebaseAdapter);

        return view;
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initGroupList(){
        String currentUserId = "";
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mFirebaseDbRef = FirebaseDatabase.getInstance().getReference();
//            if (mFirebaseAdapter != null)
//                mFirebaseAdapter.cleanup();
            mFirebaseAdapter = new FirebaseRecyclerAdapter<Chat, GroupsFragment.GroupViewHolder>(
                    Chat.class,
                    R.layout.group_item,
                    GroupsFragment.GroupViewHolder.class,
                    mFirebaseDbRef.child(USERS_CHILD).child(currentUserId).child(RECENTS_CHILD)) {
                @Override
                protected void populateViewHolder(GroupsFragment.GroupViewHolder viewHolder, final Chat model, final int position) {
                    if(model != null) {
                        viewHolder.title.setText(model.getTitle());
                        viewHolder.lastMessage.setText(model.getLastMessage());
                        if (model.getPhotoUrl() != null)
                            Glide.with(getContext()).load(model.getPhotoUrl()).into(viewHolder.image);
                        else
                            viewHolder.image.setImageDrawable(ContextCompat.getDrawable(
                                    getContext(),
                                    R.drawable.ic_group));
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra(GROUP_ID, model.getId());
                                intent.putExtra(GROUP_NAME, model.getTitle());
                                intent.putExtra(PHOTO_URL, model.getPhotoUrl());
                                startActivity(intent);

//                        Toast.makeText(getActivity(), "item " + position, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            };
        }
    }


}
