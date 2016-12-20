package com.haloteam.imess.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haloteam.imess.R;
import com.haloteam.imess.activity.ChatActivity;
import com.haloteam.imess.model.Chat;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.haloteam.imess.MainActivity.CHATS_CHILD;
import static com.haloteam.imess.MainActivity.USERS_CHILD;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupsFragment.OnRecyclerViewScrollListener} interface
 * to handle interaction events.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {

    public static final String GROUP_ID = "groupID";

    private OnRecyclerViewScrollListener mListener;

    private RecyclerView mRecyclerViewGroups;
    private DatabaseReference mFirebaseDbRef;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    public GroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupsFragment newInstance() {
        GroupsFragment fragment = new GroupsFragment();
//        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        mRecyclerViewGroups.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
                recyclerViewScroll(dy);
            }
        });
        return view;
    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecyclerViewScrollListener) {
            mListener = (OnRecyclerViewScrollListener) context;
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

    public interface OnRecyclerViewScrollListener {
        void onScrolled(int dy);
    }

    public void recyclerViewScroll(int dy){
        if(mListener != null){
            mListener.onScrolled(dy);
        }
    }

    private void initGroupList(){
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirebaseDbRef = FirebaseDatabase.getInstance().getReference();
        if(mFirebaseAdapter != null)
            mFirebaseAdapter.cleanup();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Chat, GroupViewHolder>(
                Chat.class,
                R.layout.group_item,
                GroupViewHolder.class,
                mFirebaseDbRef.child(USERS_CHILD).child(currentUserId).child(CHATS_CHILD)){
            @Override
            protected void populateViewHolder(GroupViewHolder viewHolder, final Chat model, final int position) {
                viewHolder.title.setText(model.getTitle());
                viewHolder.lastMessage.setText(model.getLastMessage());
                if(model.getPhotoUrl() != null)
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
                        startActivity(intent);

//                        Toast.makeText(getActivity(), "item " + position, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView lastMessage;
        CircleImageView image;

        public GroupViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            lastMessage = (TextView) itemView.findViewById(R.id.last_message);
            image = (CircleImageView) itemView.findViewById(R.id.image);
        }
    }


}
