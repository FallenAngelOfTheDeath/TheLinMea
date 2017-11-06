package com.fallenangel.linmea.linmea.utils.callback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.fallenangel.linmea.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea.interfaces.OnItemTouchHelperViewHolder;
import com.fallenangel.linmea.linmea.view.UnderlayButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by NineB on 10/23/2017.
 */

public abstract class ItemTouchHelperCallback extends ItemTouchHelper.Callback {


    private static final int BUTTON_WIDTH = 160;
    private RecyclerView recyclerView;
    private List<UnderlayButton> buttons;
    private GestureDetector gestureDetector;
    private int swipedPos = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer, List<UnderlayButton>> buttonsBufferLeft;
    private Map<Integer, List<UnderlayButton>> buttonsBufferRight;
    private Map<Integer, List<UnderlayButton>> buttonsBuffer;
    private Queue<Integer> recoverQueue;
    private final OnItemTouchHelper mOnItemTouchHelper;

    private static final int padding = 12;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            for (UnderlayButton button : buttons){
                if(button.onClick(e.getX(), e.getY()))
                    break;
            }

            return true;
        }
    };
//
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent e) {
            if (swipedPos < 0) return false;
            Point point = new Point((int) e.getRawX(), (int) e.getRawY());

            RecyclerView.ViewHolder swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPos);
            View swipedItem = swipedViewHolder.itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);

            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP ||e.getAction() == MotionEvent.ACTION_MOVE) {
                if (rect.top < point.y && rect.bottom > point.y)
                    gestureDetector.onTouchEvent(e);
                else {
                    recoverQueue.add(swipedPos);
                    swipedPos = -1;
                    recoverSwipedItem();
                }
            }
            return false;
        }
    };




    public ItemTouchHelperCallback(Context context, RecyclerView recyclerView, OnItemTouchHelper onItemTouchHelper) {
        this.recyclerView = recyclerView;
        this.buttons = new ArrayList<>();
        this.mOnItemTouchHelper = onItemTouchHelper;

        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        buttonsBufferLeft = new HashMap<>();
        buttonsBufferRight = new HashMap<>();
        buttonsBuffer = new HashMap<>();
        recoverQueue = new LinkedList<Integer>(){
            @Override
            public boolean add(Integer o) {
                if (contains(o))
                    return false;
                else
                    return super.add(o);
            }
        };
        // attachSwipe();
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mOnItemTouchHelper.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        int pos = viewHolder.getAdapterPosition();
//
//        if (swipedPos != pos)
//            recoverQueue.add(swipedPos);
//
//        swipedPos = pos;
//
//        if(buttonsBufferLeft.containsKey(swipedPos) || buttonsBufferRight.containsKey(swipedPos)){
//           // buttonsBuffer.putAll(buttonsBufferLeft);
//          //  buttonsBuffer.putAll(buttonsBufferRight);
//            //buttons = buttonsBufferRight.get(pos);
//            buttons.addAll(buttonsBufferRight.get(pos));
//        } else {
//            buttons.clear();
//        }
//
//        buttonsBufferLeft.clear();
//        buttonsBufferRight.clear();
//        swipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
//        recoverSwipedItem();



        int pos = viewHolder.getAdapterPosition();

        if (swipedPos != pos)
            recoverQueue.add(swipedPos);

        swipedPos = pos;

        if (buttonsBufferRight.containsKey(swipedPos))
            buttons = buttonsBufferRight.get(swipedPos);
        else
            buttons.clear();

        buttonsBufferRight.clear();
        swipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
        recoverSwipedItem();

        //mOnItemTouchHelper.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        //Call when item dropped after move for view holder
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof OnItemTouchHelperViewHolder) {
                OnItemTouchHelperViewHolder itemViewHolder = (OnItemTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected(viewHolder);
            }
        }
        //Call when item dropped after move
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && actionState != ItemTouchHelper.ACTION_STATE_DRAG) {
            mOnItemTouchHelper.onItemMoveComplete(viewHolder);
        }






//        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
//            int pos = viewHolder.getAdapterPosition();
//
//            if (swipedPos != pos)
//                recoverQueue.add(swipedPos);
//
//            swipedPos = pos;
//
//            if (actionState == ItemTouchHelper.LEFT){
//                if(buttonsBufferLeft.containsKey(swipedPos)){
//                    buttons = buttonsBufferLeft.get(pos);
//                } else {
//                    buttons.clear();
//                }
//            } else if (actionState == ItemTouchHelper.RIGHT) {
//                if( buttonsBufferRight.containsKey(swipedPos)){
//                    buttons = buttonsBufferRight.get(pos);
//                } else {
//                    buttons.clear();
//                }
//            }
//            buttonsBufferLeft.clear();
//            buttonsBufferRight.clear();
//            swipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
//            recoverSwipedItem();
//        }

    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof OnItemTouchHelperViewHolder) {
            OnItemTouchHelperViewHolder itemViewHolder =
                    (OnItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemClear(recyclerView, viewHolder);
        }
    }

//    @Override
//    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
//        return swipeThreshold;
//    }

//    @Override
//    public float getSwipeEscapeVelocity(float defaultValue) {
//        return 0.1f * defaultValue;
//    }

//    @Override
//    public float getSwipeVelocityThreshold(float defaultValue) {
//        return 5.0f * defaultValue;
//    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int pos = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;



        if (pos < 0){
            swipedPos = pos;
            return;
        }

        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            List<UnderlayButton> leftButtonsBuffer = new ArrayList<>();
            List<UnderlayButton> rightButtonsBuffer = new ArrayList<>();
            if(dX < 0) {
                if (!buttonsBufferRight.containsKey(pos)){
                    //if(ItemTouchHelper.RIGHT == UnderlayButton.RIGHT)

                    onCreateRightUnderlayButton(viewHolder, rightButtonsBuffer);
                    //instantiateUnderlayButton(viewHolder, buffer);
                    buttonsBufferRight.put(pos, rightButtonsBuffer);
                }
                else {
                    rightButtonsBuffer = buttonsBufferRight.get(pos);
                }
                translationX = dX * rightButtonsBuffer.size() * BUTTON_WIDTH / itemView.getWidth();
                drawRightButtons(c, itemView, rightButtonsBuffer, pos, translationX);
            } else {
                if (!buttonsBufferLeft.containsKey(pos)){
                    onCreateLeftUnderlayButton(viewHolder, leftButtonsBuffer);
                    //instantiateUnderlayButton(viewHolder, buffer);
                    buttonsBufferLeft.put(pos, leftButtonsBuffer);
                }
                else {
                    leftButtonsBuffer = buttonsBufferLeft.get(pos);
                }

                translationX = dX * leftButtonsBuffer.size() * BUTTON_WIDTH / itemView.getWidth();
                drawLeftButtons(c, itemView, leftButtonsBuffer, pos, translationX);
            }
        }


        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private synchronized void recoverSwipedItem(){
        while (!recoverQueue.isEmpty()){
            int pos = recoverQueue.poll();
            if (pos > -1) {
                recyclerView.getAdapter().notifyItemChanged(pos);
            }
        }
    }

    private void drawRightButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX){
        float right = itemView.getRight() + padding;
        float dButtonWidth = (-1) * dX / buffer.size();

        for (UnderlayButton button : buffer) {
            float left = right - dButtonWidth - padding;
            button.onDraw(
                    c,
                    new RectF(
                            left,
                            itemView.getTop() + padding,
                            right,
                            itemView.getBottom() - padding
                    ),
                    pos
            );

            right = left;
        }
    }

    private void drawLeftButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX){
        float left = itemView.getLeft() + padding;
        float dButtonWidth = (-1) * dX / buffer.size();


        for (UnderlayButton button : buffer) {
            float right = left - dButtonWidth + padding;
            button.onDraw(
                    c,
                    new RectF(
                            left,
                            itemView.getTop() + padding,
                            right ,
                            itemView.getBottom() - padding
                    ),
                    pos
            );

            left = right;
        }
    }

    public void attachSwipe(){
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    //public abstract void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);
    public abstract void onCreateRightUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);
    public abstract void onCreateLeftUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);







//
//    private Context mContext;
//    private RecyclerView mRecyclerView;
//    private final OnItemTouchHelper mOnItemTouchHelper;
//    private Map<Integer, List<UnderlayButton>> mButtonsBuffer;
//    private List<UnderlayButton> mButtons;
//    private Queue<Integer> mRecoverQueue;
//    private GestureDetector mGestureDetector;
//
//    private float mSwipeThreshold = 0.5f;
//
//    private int mSwipedPosition = -1;
//
//    private static final int BUTTON_WIDTH = 200;
//
//    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent e) {
//            for (UnderlayButton button : mButtons){
//                if(button.onClick(e.getX(), e.getY()))
//                    break;
//            }
//
//            return true;
//        }
//    };
//
//    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent e) {
//            if (mSwipedPosition < 0) return false;
//            Point point = new Point((int) e.getRawX(), (int) e.getRawY());
//
//            RecyclerView.ViewHolder swipedViewHolder = mRecyclerView.findViewHolderForAdapterPosition(mSwipedPosition);
//            View swipedItem = swipedViewHolder.itemView;
//            Rect rect = new Rect();
//            swipedItem.getGlobalVisibleRect(rect);
//
//            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP ||e.getAction() == MotionEvent.ACTION_MOVE) {
//                if (rect.top < point.y && rect.bottom > point.y)
//                    mGestureDetector.onTouchEvent(e);
//                else {
//                    mRecoverQueue.add(mSwipedPosition);
//                    mSwipedPosition = -1;
//                    recoverSwipedItem();
//                }
//            }
//            return false;
//        }
//    };
//
//    public ItemTouchHelperCallback(Context context, RecyclerView recyclerView, OnItemTouchHelper onItemTouchHelper) {
//        super(0, ItemTouchHelper.LEFT);
//        this.mContext = context;
//        this.mRecyclerView = recyclerView;
//        this.mOnItemTouchHelper = onItemTouchHelper;
//        this.mRecyclerView.setOnTouchListener(onTouchListener);
//        this.mButtons = new ArrayList<>();
//        mButtonsBuffer = new HashMap<>();
//        mRecoverQueue = new LinkedList<Integer>(){
//            @Override
//            public boolean add(Integer o) {
//                if (contains(o))
//                    return false;
//                else
//                    return super.add(o);
//            }
//        };
//        //ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
//        //itemTouchHelper.attachToRecyclerView(mRecyclerView);
//    }
//
////
////    @Override
////    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
////        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
////        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
////        return makeMovementFlags(dragFlags, swipeFlags);
////    }
//
////    @Override
////    public boolean isLongPressDragEnabled() {
////        return false;
////    }
//
////    @Override
////    public boolean isItemViewSwipeEnabled() {
////        return true;
////    }
//
//    @Override
//    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//        // mOnItemTouchHelper.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//        return false;
//    }
//
//    @Override
//    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        //mOnItemTouchHelper.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
//
//        int pos = viewHolder.getAdapterPosition();
//
//        if (mSwipedPosition != pos)
//            mRecoverQueue.add(mSwipedPosition);
//
//        mSwipedPosition = pos;
//
//        if (mButtonsBuffer.containsKey(mSwipedPosition))
//            mButtons = mButtonsBuffer.get(mSwipedPosition);
//        else
//            mButtons.clear();
//
//        mButtonsBuffer.clear();
//        mSwipeThreshold = 0.5f * mButtons.size() * BUTTON_WIDTH;
//        recoverSwipedItem();
//    }
//
//    @Override
//    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
//        return mSwipeThreshold;
//    }
//
//    @Override
//    public float getSwipeEscapeVelocity(float defaultValue) {
//        return 0.1f * defaultValue;
//    }
//
//    @Override
//    public float getSwipeVelocityThreshold(float defaultValue) {
//        return 5.0f * defaultValue;
//    }
//
////    @Override
////    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
////        super.onSelectedChanged(viewHolder, actionState);
////        //Call when item dropped after move for view holder
////        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
////            if (viewHolder instanceof OnItemTouchHelperViewHolder) {
////                OnItemTouchHelperViewHolder itemViewHolder = (OnItemTouchHelperViewHolder) viewHolder;
////                itemViewHolder.onItemSelected(viewHolder);
////            }
////        }
////        //Call when item dropped after move
////        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && actionState != ItemTouchHelper.ACTION_STATE_DRAG) {
////            mOnItemTouchHelper.onItemMoveComplete(viewHolder);
////        }
////    }
//
////    @Override
////    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
////        super.clearView(recyclerView, viewHolder);
////        if (viewHolder instanceof OnItemTouchHelperViewHolder) {
////            OnItemTouchHelperViewHolder itemViewHolder =
////                    (OnItemTouchHelperViewHolder) viewHolder;
////            itemViewHolder.onItemClear(recyclerView, viewHolder);
////        }
////    }
//
//    @Override
//    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        View itemView = viewHolder.itemView;
//        int position = viewHolder.getAdapterPosition();
//        float translationX = dX;
//
//        if (position < 0){
//            mSwipedPosition = position;
//            return;
//        }
//
//        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
//            if(dX < 0) {
//                List<UnderlayButton> buffer = new ArrayList<>();
//
//                if (!mButtonsBuffer.containsKey(position)){
//                    instantiateUnderlayButton(viewHolder, buffer);
//                    mButtonsBuffer.put(position, buffer);
//                }
//                else {
//                    buffer = mButtonsBuffer.get(position);
//                }
//
//                translationX = dX * buffer.size() * BUTTON_WIDTH / itemView.getWidth();
//                drawButtons(c, itemView, buffer, position, translationX);
//            }
//        }
//
//
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//    }
//
//
//    private void instantiateUnderlayButton (RecyclerView.ViewHolder viewHolder, List<UnderlayButton> buffer){
//        buffer.add(new UnderlayButton("Delete", 0, mContext.getResources().getColor(R.color.delete), new UnderlayButtonClickListener() {
//            @Override
//            public void onClick(int pos) {
//                //TODO
//            }
//        }));
//
//        buffer.add(new UnderlayButton("Edit", 0, mContext.getResources().getColor(R.color.edit), new UnderlayButtonClickListener() {
//            @Override
//            public void onClick(int pos) {
//                //TODO
//            }
//        }));
//    }
//
//    private synchronized void recoverSwipedItem(){
//        while (!mRecoverQueue.isEmpty()){
//            int pos = mRecoverQueue.poll();
//            if (pos > -1) {
//                mRecyclerView.getAdapter().notifyItemChanged(pos);
//            }
//        }
//    }
//
//    private void drawButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX){
//        float right = itemView.getRight();
//        float dButtonWidth = (-1) * dX / buffer.size();
//
//        for (UnderlayButton button : buffer) {
//            float left = right - dButtonWidth;
//            button.onDraw(c, new RectF(left, itemView.getTop(), right, itemView.getBottom()), pos);
//            right = left;
//        }
//    }
//
//
//
//    //    private RecyclerView.ViewHolder currentItemViewHolder = null;
////
////    private Context mContext;
////    private RecyclerView mRecyclerView;
////    private final OnItemTouchHelper mOnItemTouchHelper;
////    private Paint p = new Paint();
////    private boolean mSwipeBack = false;
////    //private static final float buttonWidth = 300;
////    //   private SwipeControllerActions buttonsActions = null;
////    private RectF buttonInstance = null;
////    private int padding = 12;
////    private static final float buttonWidth = 200;
////    private Map<Integer, Boolean> mSwiped = new HashMap<Integer, Boolean>();
////
//////    private Drawable deleteIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_delete);
////
////    // Draw the delete icon
//////     deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
//////     deleteIcon.draw(c);
////
////    private String TAG = "LOGTAGITEMTOUCH";
////
////    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
////        @Override
////        public boolean onTouch(View view, MotionEvent e) {
////            if (mSwipedPosition < 0) return false;
////            Point point = new Point((int) e.getRawX(), (int) e.getRawY());
////
////            RecyclerView.ViewHolder swipedViewHolder = mRecyclerView.findViewHolderForAdapterPosition(mSwipedPosition);
////            View swipedItem = swipedViewHolder.itemView;
////            Rect rect = new Rect();
////            swipedItem.getGlobalVisibleRect(rect);
////
////            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP ||e.getAction() == MotionEvent.ACTION_MOVE) {
////                if (rect.top < point.y && rect.bottom > point.y)
////                    gestureDetector.onTouchEvent(e);
////                else {
////                    mRecoverQueue.add(mSwipedPosition);
////                    mSwipedPosition = -1;
////                    recoverSwipedItem();
////                }
////            }
////            return false;
////        }
////    };
////
////
////    public ItemTouchHelperCallback(Context context, OnItemTouchHelper onItemTouchHelper, RecyclerView recyclerView) {
////        this.mContext = context;
////        this.mRecyclerView = recyclerView;
////        this.mOnItemTouchHelper = onItemTouchHelper;
////        //  this.buttonsActions = buttonsActions;
////        this.mRecyclerView.setOnTouchListener(onTouchListener);
////
////        mRecoverQueue = new LinkedList<Integer>(){
////            @Override
////            public boolean add(Integer o) {
////                if (contains(o))
////                    return false;
////                else
////                    return super.add(o);
////            }
////        };
////    }
////
////    @Override
////    public boolean isLongPressDragEnabled() {
////        return false;
////    }
////
////    @Override
////    public boolean isItemViewSwipeEnabled() {
////        return true;
////    }
////
////    @Override
////    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
////        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
////        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
////        //int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
////        return makeMovementFlags(dragFlags, swipeFlags);
////    }
////
////    @Override
////    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
////        return super.convertToAbsoluteDirection(flags, layoutDirection);
////    }
////
////    @Override
////    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
////        mOnItemTouchHelper.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
////        return false;
////    }
////
////    @Override
////    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
////
////        int position = viewHolder.getAdapterPosition();
////
////        if (mSwipedPosition != position)
////            mRecoverQueue.add(mSwipedPosition);
////
////        mSwipedPosition = position;
////
////        if (mButtonsBuffer.containsKey(mSwipedPosition))
////            buttons = mButtonsBuffer.get(mSwipedPosition);
////        else
////            buttons.clear();
////
////        mButtonsBuffer.clear();
////        mSwipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
////        recoverSwipedItem();
////
////        mOnItemTouchHelper.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
////    }
////
////
////    @Override
////    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
////        super.clearView(recyclerView, viewHolder);
////        if (viewHolder instanceof OnItemTouchHelperViewHolder) {
////            OnItemTouchHelperViewHolder itemViewHolder =
////                    (OnItemTouchHelperViewHolder) viewHolder;
////            itemViewHolder.onItemClear(recyclerView, viewHolder);
////        }
////    }
////
////
////    @Override
////    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
////        super.onSelectedChanged(viewHolder, actionState);
////        //Call when item dropped after move for view holder
////        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
////            if (viewHolder instanceof OnItemTouchHelperViewHolder) {
////                OnItemTouchHelperViewHolder itemViewHolder = (OnItemTouchHelperViewHolder) viewHolder;
////                itemViewHolder.onItemSelected(viewHolder);
////            }
////        }
////        //Call when item dropped after move
////        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && actionState != ItemTouchHelper.ACTION_STATE_DRAG) {
////            mOnItemTouchHelper.onItemMoveComplete(viewHolder);
////        }
////    }
//////
//////    @Override
//////    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//////        View itemView = viewHolder.itemView;
//////        if (actionState == ACTION_STATE_SWIPE) {
////////            p.setColor(Color.RED);
////////            RectF backgroundRight = new RectF((float) itemView.getRight() + dX - padding, (float) itemView.getTop() + padding, (float) itemView.getRight() - padding, (float) itemView.getBottom() - padding+5);
////////            c.drawRect(backgroundRight, p);
////////            Drawable deleteIcon = mContext.getResources().getDrawable(R.drawable.ic_delete);
////////            deleteIcon.setBounds(0, 0, 0, 0);
////////            deleteIcon.draw(c);
////////
////////            p.setColor(Color.GREEN);
////////            RectF backgroundLeft = new RectF((float) itemView.getLeft() + padding, (float) itemView.getTop() + padding, (float) itemView.getLeft() + dX + padding, (float) itemView.getBottom() - padding+5);
////////            c.drawRect(backgroundLeft, p);
//////
//////
//////            if(dX > 0){
//////                p.setColor(Color.parseColor("#388E3C"));
//////                RectF background = new RectF((float) itemView.getLeft() + padding, (float) itemView.getTop() + padding, dX + padding,(float) itemView.getBottom() - padding);
//////                c.drawRect(background,p);
//////            } else {
//////                p.setColor(Color.parseColor("#D32F2F"));
//////                RectF background = new RectF((float) itemView.getRight() + dX - padding, (float) itemView.getTop() + padding,(float) itemView.getRight() - padding, (float) itemView.getBottom() - padding + 5);
//////                c.drawRect(background,p);
//////            }
//////
//////
//////        }
//////        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//////    }
////
////
////    @Override
////    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
////        return mSwipeThreshold;
////    }
////
////    @Override
////    public float getSwipeEscapeVelocity(float defaultValue) {
////        return 0.1f * defaultValue;
////    }
////
////    @Override
////    public float getSwipeVelocityThreshold(float defaultValue) {
////        return 5.0f * defaultValue;
////    }
////
////    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
////        @Override
////        public boolean onSingleTapConfirmed(MotionEvent e) {
////            for (UnderlayButton button : buttons){
////                if(button.onClick(e.getX(), e.getY()))
////                    break;
////            }
////
////            return true;
////        }
////    };
////
////    private int mSwipedPosition = -1;
////    private Map<Integer, List<UnderlayButton>> mButtonsBuffer = new HashMap<>();
////    public static final int BUTTON_WIDTH = 150;
////    private List<UnderlayButton> buttons = new ArrayList<>();
////    private float mSwipeThreshold = 0.5f;
////    private Queue<Integer> mRecoverQueue;
////    private GestureDetector gestureDetector = new GestureDetector(mContext, gestureListener);
////   // public abstract void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<SwipeHelper.UnderlayButton> underlayButtons);
////
////
////    @Override
////    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
////
////        int position = viewHolder.getAdapterPosition();
////        float translationX = dX;
////        View itemView = viewHolder.itemView;
////
////        if (position < 0){
////            mSwipedPosition = position;
////            return;
////        }
////
////        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
////            if(dX < 0) {
////                List<UnderlayButton> buffer = new ArrayList<>();
////
////                if (!mButtonsBuffer.containsKey(position)){
////                   // instantiateUnderlayButton(viewHolder, buffer);
////                    buffer.add(new UnderlayButton("Delete", 0, Color.parseColor("#FF3C30"), new UnderlayButtonClickListener() {
////                        @Override
////                        public void onClick(int pos) {
////                            //to do
////                        }
////                    }));
////                    buffer.add(new UnderlayButton("Edit", 0, Color.parseColor("#FF9502"), new UnderlayButtonClickListener() {
////                        @Override
////                        public void onClick(int pos) {
////                            //to do
////                        }
////                    }));
////                    buffer.add(new UnderlayButton("Favorite", 0, Color.parseColor("#C7C7CB"), new UnderlayButtonClickListener() {
////                        @Override
////                        public void onClick(int pos) {
////                            //to do
////                        }
////                    }));
////
////                    mButtonsBuffer.put(position, buffer);
////                }
////                else {
////                    buffer = mButtonsBuffer.get(position);
////                }
////
////                translationX = dX * buffer.size() * BUTTON_WIDTH / itemView.getWidth();
////                drawButtons(c, itemView, buffer, position, translationX);
////            }
////        }
////
////        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
////    }
////
////
////
////    private void drawButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX){
////        float right = itemView.getRight();
////        float dButtonWidth = (-1) * dX / buffer.size();
////
////        for (UnderlayButton button : buffer) {
////            float left = right - dButtonWidth;
////            button.onDraw(
////                    c,
////                    new RectF(
////                            left,
////                            itemView.getTop(),
////                            right,
////                            itemView.getBottom()
////                    ),
////                    pos
////            );
////
////            right = left;
////        }
////    }
////
////    private synchronized void recoverSwipedItem(){
////        while (!mRecoverQueue.isEmpty()){
////            int pos = mRecoverQueue.poll();
////            if (pos > -1) {
////                mRecyclerView.getAdapter().notifyItemChanged(pos);
////            }
////        }
////    }
//


}