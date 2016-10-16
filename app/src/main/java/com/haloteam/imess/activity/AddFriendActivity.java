package com.haloteam.imess.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haloteam.imess.R;
import com.haloteam.imess.model.User;

public class AddFriendActivity extends AppCompatActivity {

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView name;

        public UserViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tvName);
        }
    }

    private DatabaseReference mDatabaseReference;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<User, UserViewHolder> mFirebaseAdapter;

    private EditText mSearchText;
    private Button mBtSearch;
    private RecyclerView mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mSearchText = (EditText) findViewById(R.id.etSearch);
        mBtSearch = (Button) findViewById(R.id.btSearch);
        mUserList = (RecyclerView) findViewById(R.id.userList);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
       
    }
}
