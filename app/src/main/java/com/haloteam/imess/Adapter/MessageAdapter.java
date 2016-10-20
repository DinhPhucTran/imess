package com.haloteam.imess.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haloteam.imess.R;
import com.haloteam.imess.activity.ChatActivity;
import com.haloteam.imess.model.Message;

import java.util.List;

/**
 * Created by nhonnguyen on 10/20/16.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static abstract class ViewHolder extends RecyclerView.ViewHolder{

        public TextView messageTextView;
        public ImageView avatarImageView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolderLeft extends MessageAdapter.ViewHolder{

        public ViewHolderLeft(View itemView) {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.textViewLeftChat);
            avatarImageView = (ImageView) itemView.findViewById(R.id.imageViewUserLeft);
        }
    }

    public static class ViewHolderRight extends MessageAdapter.ViewHolder{

        public ViewHolderRight(View itemView) {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.textViewRightChat);
            avatarImageView = (ImageView) itemView.findViewById(R.id.imageViewUserRight);
        }
    }

    private List<Message> messageList;
    private Context mContext;
    private int isMainUser;

    public MessageAdapter(List<Message> messageList, Context mContext) {
        this.messageList = messageList;
        this.mContext = mContext;
        this.isMainUser = 0;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getWriter().getName() == "a"){
            isMainUser = 1;
        } else {
            isMainUser = 0;
        }
        return isMainUser;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View messageView = inflater.inflate(R.layout.right_chat_item, parent, false);
        ViewHolder viewHolder = new ViewHolderRight(messageView);
        if (isMainUser == 1) {
            messageView = inflater.inflate(R.layout.right_chat_item, parent, false);
            viewHolder = new ViewHolderRight(messageView);
        } else if (isMainUser == 0){
            messageView = inflater.inflate(R.layout.left_chat_item, parent, false);
            viewHolder = new ViewHolderLeft(messageView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
        Message mess = messageList.get(position);
        TextView textView = holder.messageTextView;
        if (textView != null) {
            textView.setText(mess.getMessageContent());
        }
        ImageView imageView = holder.avatarImageView;
        //set avatar;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
