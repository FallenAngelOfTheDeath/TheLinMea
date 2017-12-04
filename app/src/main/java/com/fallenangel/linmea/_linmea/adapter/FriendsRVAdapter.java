package com.fallenangel.linmea._linmea.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.interfaces.OnFriendRequestClickListener;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._linmea.interfaces.OnStartDragListener;
import com.fallenangel.linmea._linmea.model.UserModel;
import com.fallenangel.linmea.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea.interfaces.OnItemTouchHelperViewHolder;

import java.util.List;

/**
 * Created by NineB on 11/25/2017.
 */

public class FriendsRVAdapter extends AbstractRecyclerViewAdapter<UserModel.MainUserModel, RecyclerView.ViewHolder> {
    private static final int FRIEND = 0;
    private static final int FRIEND_REQUEST = 1;
    private List<UserModel.FriendListModel> mFriendList;
    protected OnFriendRequestClickListener mFriendRequestClickListener;

    public FriendsRVAdapter(Context context, List<UserModel.MainUserModel> items, List<UserModel.FriendListModel> friendList,
                            OnRecyclerViewClickListener clickListener, OnStartDragListener dragStartListener,
                            OnItemTouchHelper onItemTouchHelper, OnFriendRequestClickListener friendRequestClickListener) {
        super(context, items, clickListener, dragStartListener, onItemTouchHelper);
        this.mFriendList = friendList;
        this.mFriendRequestClickListener = friendRequestClickListener;
    }

    @Override
    protected void bindItemData(RecyclerView.ViewHolder viewHolder, UserModel.MainUserModel data, int position) {
        switch (viewHolder.getItemViewType()) {
            case FRIEND:
                FriendsRVAdapter.ViewHolder friendViewHolder = (FriendsRVAdapter.ViewHolder) viewHolder;
                friendViewHolder.mNickName.setText(data.getNickName());
                friendViewHolder.mEmail.setText(data.getEmail());
                break;
            case FRIEND_REQUEST:
                FriendsRVAdapter.FriendRequestViewHolder friendRequestViewHolder = (FriendsRVAdapter.FriendRequestViewHolder) viewHolder;
                friendRequestViewHolder.mNickName.setText(data.getNickName());
                friendRequestViewHolder.mEmail.setText(data.getEmail());
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case FRIEND:
                View friendView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
                return new FriendsRVAdapter.ViewHolder(friendView, mClickListener);
            case FRIEND_REQUEST:
                View friendRequestView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_item, parent, false);
                return new FriendsRVAdapter.FriendRequestViewHolder(friendRequestView, mClickListener, mFriendRequestClickListener);
            default:
                return null;
        }

//        int layout = R.layout.friend_item;
//        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
//        FriendsRVAdapter.ViewHolder viewHolder = new FriendsRVAdapter.ViewHolder(view, mClickListener);
//        return viewHolder;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        if (mFriendList.get(position).getFriendStatus().equals(true)){
            return FRIEND;
        } else return FRIEND_REQUEST;
        //return mItems.get(position).getSender().equals(User.getCurrentUserUID()) ? VIEW_TYPE_USER_MESSAGE : VIEW_TYPE_FRIEND_MESSAGE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, OnItemTouchHelperViewHolder {

        public TextView mNickName, mEmail;
        public CardView mCardView;
        public ImageView mAvatar;
        public OnRecyclerViewClickListener mClickListener;

        public ViewHolder(View itemView, OnRecyclerViewClickListener clickListener) {
            super(itemView);
            this.mClickListener = clickListener;

            mNickName = (TextView) itemView.findViewById(R.id.nickname);
            mEmail = (TextView) itemView.findViewById(R.id.email);
            mCardView = (CardView) itemView.findViewById(R.id.cardview);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);

            mCardView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){
//                case R.id.extended_list_item_options_menu:
//                    if (mClickListener != null) {
//                        mClickListener.onOptionsClicked(v, getAdapterPosition ());
//                    }
//                    break;
                case R.id.cardview:
                    if (mClickListener != null) {
                        mClickListener.onItemClicked(v, getAdapterPosition ());
                    }
                    break;
            }


//            if (mClickListener != null) {
//                mClickListener.onItemClicked(v, getAdapterPosition ());
//            }
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

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, OnItemTouchHelperViewHolder {

        public TextView mNickName, mEmail, mAccept, mCancel;
        public CardView mCardView;
        public ImageView mAvatar;
        public OnRecyclerViewClickListener mClickListener;
        public OnFriendRequestClickListener mFriendRequestClickListener;

        public FriendRequestViewHolder(View itemView, OnRecyclerViewClickListener clickListener, OnFriendRequestClickListener friendRequestClickListener) {
            super(itemView);
            this.mClickListener = clickListener;
            this.mFriendRequestClickListener = friendRequestClickListener;

            mNickName = (TextView) itemView.findViewById(R.id.nickname);
            mEmail = (TextView) itemView.findViewById(R.id.email);
            mCardView = (CardView) itemView.findViewById(R.id.cardview);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mCancel = (TextView) itemView.findViewById(R.id.cancel_action);
            mAccept = (TextView) itemView.findViewById(R.id.accept_action);

            mCancel.setOnClickListener(this);
            mAccept.setOnClickListener(this);
            mCardView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){
//                case R.id.extended_list_item_options_menu:
//                    if (mClickListener != null) {
//                        mClickListener.onOptionsClicked(v, getAdapterPosition ());
//                    }
//                    break;
                case R.id.cardview:
                    if (mClickListener != null) {
                        mClickListener.onItemClicked(v, getAdapterPosition ());
                    }
                    break;
                case R.id.accept_action:
                    if (mFriendRequestClickListener != null) {
                        mFriendRequestClickListener.onAccept(v, getAdapterPosition ());
                    }
                    break;
                case R.id.cancel_action:
                    if (mFriendRequestClickListener != null) {
                        mFriendRequestClickListener.onCancel(v, getAdapterPosition ());
                    }
                    break;
            }


//            if (mClickListener != null) {
//                mClickListener.onItemClicked(v, getAdapterPosition ());
//            }
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
