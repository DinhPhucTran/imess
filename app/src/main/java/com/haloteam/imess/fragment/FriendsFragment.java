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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haloteam.imess.R;
import com.haloteam.imess.activity.AddFriendActivity;
import com.haloteam.imess.activity.ChatActivity;
import com.haloteam.imess.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.haloteam.imess.MainActivity.FRIENDS_CHILD;
import static com.haloteam.imess.MainActivity.USERS_CHILD;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_FRIEND_LIST = "friendList";

    // TODO: Rename and change types of parameters
    private RecyclerView mRecyclerViewFriends;
    private DatabaseReference mFirebaseDbRef;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    private OnRecyclerViewScrollListener mListener;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        mRecyclerViewFriends = (RecyclerView) view.findViewById(R.id.recycler_friends);
        mRecyclerViewFriends.setHasFixedSize(true);
        mRecyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        initFriendList();
        mRecyclerViewFriends.setAdapter(mFirebaseAdapter);
        mRecyclerViewFriends.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    + " must implement OnRecyclerViewScrollListener");
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

    public interface OnRecyclerViewScrollListener {
        void onScrolled(int dy);
    }

    public void recyclerViewScroll(int dy){
        if(mListener != null){
            mListener.onScrolled(dy);
        }
    }

    private void initFriendList(){
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirebaseDbRef = FirebaseDatabase.getInstance().getReference();
        if(mFirebaseAdapter != null)
            mFirebaseAdapter.cleanup();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class,
                R.layout.user_item,
                UserViewHolder.class,
                mFirebaseDbRef.child(USERS_CHILD).child(currentUserId).child(FRIENDS_CHILD)) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, final User model, int position) {
                viewHolder.name.setText(model.getName());
                viewHolder.email.setText(model.getEmail());
                if(model.getPhotoUrl() != null)
                    Glide.with(getContext()).load(model.getPhotoUrl()).into(viewHolder.image);
                else
                    viewHolder.image.setImageDrawable(ContextCompat.getDrawable(
                            getContext(),
                            R.drawable.account_circle));
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra(ChatActivity.FRIEND_ID, model.getId());
                        startActivity(intent);
                    }
                });
            }
        };
    }

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
}
