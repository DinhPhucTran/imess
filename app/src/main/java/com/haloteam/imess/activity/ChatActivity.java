package com.haloteam.imess.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.haloteam.imess.Adapter.MessageAdapter;
import com.haloteam.imess.R;
import com.haloteam.imess.model.Message;
import com.haloteam.imess.model.User;

import java.util.List;

/**
 * Created by nhonnguyen on 10/20/16.
 */

public class ChatActivity extends AppCompatActivity {

    static public User currentUser = new User("a", "abc@gmail.com", "abc", null);
    List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        RecyclerView rvMessage = (RecyclerView) findViewById(R.id.rvChat);
        messages = Message.createListMessage(30);
        MessageAdapter adapter = new MessageAdapter(messages, this);
        rvMessage.setAdapter(adapter);
        rvMessage.setLayoutManager(new LinearLayoutManager(this));
    }
}
