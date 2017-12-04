package com.fallenangel.linmea._linmea.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.interfaces.OnStartDragListener;
import com.fallenangel.linmea._linmea.model.MessageModel;
import com.fallenangel.linmea.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea.interfaces.OnItemTouchHelperViewHolder;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea.linmea.user.authentication.User;

import java.util.List;

/**
 * Created by NineB on 11/23/2017.
 */

public class ChatRoomRVAdapter extends AbstractRecyclerViewAdapter<MessageModel, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER_MESSAGE = 0;
    private static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    private static final String TAG = "ChatRoomRVAdapter";

    private String mFriendName;

    public ChatRoomRVAdapter(Context context, List<MessageModel> items, String friendName, OnRecyclerViewClickListener clickListener, OnStartDragListener dragStartListener, OnItemTouchHelper onItemTouchHelper) {
        super(context, items, clickListener, dragStartListener, onItemTouchHelper);
        this.mFriendName = friendName;
        Log.i(TAG, "ChatRoomRVAdapter: " + mFriendName);
    }

    @Override
    protected void bindItemData(RecyclerView.ViewHolder viewHolder, MessageModel data, int position) {
        switch (viewHolder.getItemViewType()){
            case VIEW_TYPE_FRIEND_MESSAGE:
                FriendViewHolder friendViewHolder = (FriendViewHolder) viewHolder;

                Log.i(TAG, "bindItemData: " + mFriendName);
                friendViewHolder.mName.setText(mFriendName);

                friendViewHolder.mText.setText(data.getText());
                friendViewHolder.mData.setText(data.getData());
                break;
            case VIEW_TYPE_USER_MESSAGE:
                UserViewHolder userViewHolder = (UserViewHolder) viewHolder;

                userViewHolder.mText.setText(data.getText());
                userViewHolder.mData.setText(data.getData());

                //userViewHolder.mName.setText(mFriendName);
                break;
        }


//        if (viewHolder instanceof UserViewHolder) {
//            ((UserViewHolder) viewHolder).mText.setText(data.getText());
//            ((UserViewHolder) viewHolder).mData.setText(data.getData());
//
//            ((UserViewHolder) viewHolder).mName.setText(mFriendName);
//        }
//
//        if (viewHolder instanceof FriendViewHolder) {
//            Log.i(TAG, "bindItemData: " + mFriendName);
//            ((FriendViewHolder) viewHolder).mName.setText(mFriendName);
//
//            ((FriendViewHolder) viewHolder).mText.setText(data.getText());
//            ((FriendViewHolder) viewHolder).mData.setText(data.getData());
//
//        }
    }

//        viewHolder.mText.setText(data.getText());
//        Log.i(TAG, "bindItemData: " + data.getText());
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_TYPE_USER_MESSAGE:
                View userView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_user, parent, false);
                return new UserViewHolder(userView, mClickListener);
            case VIEW_TYPE_FRIEND_MESSAGE:
                View friendView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_friend, parent, false);
                return new FriendViewHolder(friendView, mClickListener);
            default:
                return null;
        }
    }





    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        if (mItems.get(position).getSender().equals(User.getCurrentUserUID())){
            return VIEW_TYPE_USER_MESSAGE;
        } else return VIEW_TYPE_FRIEND_MESSAGE;
            //return mItems.get(position).getSender().equals(User.getCurrentUserUID()) ? VIEW_TYPE_USER_MESSAGE : VIEW_TYPE_FRIEND_MESSAGE;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, OnItemTouchHelperViewHolder {

        public TextView mText, mData, mName;
        public CardView mCardView;
        private ImageView mAvatar, mStatus;
        private OnRecyclerViewClickListener mClickListener;

        public UserViewHolder(View itemView, OnRecyclerViewClickListener clickListener) {
            super(itemView);
            this.mClickListener = clickListener;

            mText = (TextView) itemView.findViewById(R.id.chat_text);
            mData = (TextView) itemView.findViewById(R.id.chat_data);
            mName = (TextView) itemView.findViewById(R.id.chat_name);

            mCardView = (CardView) itemView.findViewById(R.id.chat_cardview);
            //mStatus = (ImageView) itemView.findViewById(R.id.chat_status);
            //mAvatar = (ImageView) itemView.findViewById(R.id.chat_avatar);
        }


        @Override
        public void onClick(View v) {

            switch (v.getId()){
//                case R.id.extended_list_item_options_menu:
//                    if (mClickListener != null) {
//                        mClickListener.onOptionsClicked(v, getAdapterPosition ());
//                    }
//                    break;
                case R.id.chat_cardview:
                    if (mClickListener != null) {
                        mClickListener.onItemClicked(v, getAdapterPosition ());
                    }
                    break;
            }

//            if (mClickListener != null) {
//                mClickListener.onItemClicked(v, getAdapterPosition());
//            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mClickListener != null) {
                return mClickListener.onItemLongClicked(v, getAdapterPosition());
            }
            return true;
        }

        @Override
        public void onItemSelected(RecyclerView.ViewHolder viewHolder) {
            //mCardView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        }
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, OnItemTouchHelperViewHolder {

        public TextView mText, mData, mName;
        public CardView mCardView;
        private ImageView mAvatar, mStatus;
        private OnRecyclerViewClickListener mClickListener;

        public FriendViewHolder(View itemView, OnRecyclerViewClickListener clickListener) {
            super(itemView);
            this.mClickListener = clickListener;

            mText = (TextView) itemView.findViewById(R.id.chat_text);
            mData = (TextView) itemView.findViewById(R.id.chat_data);
            mName = (TextView) itemView.findViewById(R.id.chat_name);

            mCardView = (CardView) itemView.findViewById(R.id.chat_cardview);
            //mStatus = (ImageView) itemView.findViewById(R.id.chat_status);
            //mAvatar = (ImageView) itemView.findViewById(R.id.chat_avatar);
        }


        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClicked(v, getAdapterPosition ());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mClickListener != null) {
                return mClickListener.onItemLongClicked(v, getAdapterPosition ());
            }
            return true;
        }

        @Override
        public void onItemSelected(RecyclerView.ViewHolder viewHolder) {
            mCardView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        }
    }

}
