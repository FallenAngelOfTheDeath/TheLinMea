package com.fallenangel.linmea.linmea.utils.callback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
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

    private static final String TAG = "ItemTouchHelperCallback";
    private static final int BUTTON_WIDTH = 160;
    private RecyclerView recyclerView;
    private List<UnderlayButton> leftButtons;
    private List<UnderlayButton> rightButtons;
    private List<UnderlayButton> buttons;
    private GestureDetector gestureDetector;
    private int swipedPos = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer, List<UnderlayButton>> buttonsBufferLeft;
    private Map<Integer, List<UnderlayButton>> buttonsBufferRight;
    private Queue<Integer> recoverQueue;
    private final OnItemTouchHelper mOnItemTouchHelper;

    private static final int padding = 12;

    public abstract void onCreateRightUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);
    public abstract void onCreateLeftUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);

    private GestureDetector.SimpleOnGestureListener gestureListener

            = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            for (UnderlayButton button : buttons){
                if(button.onClick(e.getX(), e.getY())){
                    Log.i(TAG, "onSingleTapConfirmed, e.getX:" + e.getX() + "e.getY:" + e.getY());
                    break;
                }
            }
            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        if (swipedPos < 0) {
            return false;
        }
        Point point = new Point((int) e.getRawX(), (int) e.getRawY());
        RecyclerView.ViewHolder swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPos);
        View swipedItem = swipedViewHolder.itemView;
        Rect rect = new Rect();
        swipedItem.getGlobalVisibleRect(rect);

        if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP ||e.getAction() == MotionEvent.ACTION_MOVE) {
            if (rect.top < point.y && rect.bottom > point.y){
                gestureDetector.onTouchEvent(e);
            } else {
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
        this.rightButtons = new ArrayList<>();
        this.leftButtons = new ArrayList<>();
        this.buttons = new ArrayList<>();
        this.mOnItemTouchHelper = onItemTouchHelper;
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        buttonsBufferLeft = new HashMap<>();
        buttonsBufferRight = new HashMap<>();
        recoverQueue = new LinkedList<Integer>(){
            @Override
            public boolean add(Integer o) {
                if (contains(o))
                    return false;
                else
                    return super.add(o);
            }
        };
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recoverSwipedItem();
            }
        });
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
        int pos = viewHolder.getAdapterPosition();
        if (swipedPos != pos)
            recoverQueue.add(swipedPos);

        swipedPos = pos;
        if(buttonsBufferRight.containsKey(swipedPos) || buttonsBufferLeft.containsKey(swipedPos)){
                if (!buttonsBufferRight.isEmpty())
                   buttons.addAll(buttonsBufferRight.get(pos));
                if (!buttonsBufferLeft.isEmpty())
                    buttons.addAll(buttonsBufferLeft.get(pos));

        } else {
            buttons.clear();
        }
        buttonsBufferLeft.clear();
        buttonsBufferRight.clear();
        swipeThreshold = 0.5f * buttonsBufferRight.size() * BUTTON_WIDTH;
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


        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            swipedPos = viewHolder.getAdapterPosition();
            Log.i(TAG, "onSelectedChanged swipedPos: " + swipedPos);
        }
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
            if(dX < 0) {
                List<UnderlayButton> rightButtonsBuffer = new ArrayList<>();
                if (!buttonsBufferRight.containsKey(pos)){
                    onCreateRightUnderlayButton(viewHolder, rightButtonsBuffer);
                    buttonsBufferRight.put(pos, rightButtonsBuffer);
                } else {
                    rightButtonsBuffer = buttonsBufferRight.get(pos);
                }
                translationX = dX * rightButtonsBuffer.size() * BUTTON_WIDTH / itemView.getWidth();
                drawRightButtons(c, itemView, rightButtonsBuffer, pos, translationX);
            } else {
                List<UnderlayButton> leftButtonsBuffer = new ArrayList<>();
                if (!buttonsBufferLeft.containsKey(pos)){
                    onCreateLeftUnderlayButton(viewHolder, leftButtonsBuffer);
                    buttonsBufferLeft.put(pos, leftButtonsBuffer);
                } else {
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
        float right = itemView.getRight() - padding;
        float dButtonWidth = (-1) * dX / buffer.size();

        for (UnderlayButton button : buffer) {
            float left = right - dButtonWidth;
            button.onDraw(
                    c,
                    new RectF(
                            left,
                            itemView.getTop() + padding - 1,
                            right,
                            itemView.getBottom() - padding + 6
                    ),
                    pos
            );

            right = left;
        }
    }

    private void drawLeftButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX) {
        float left = itemView.getLeft() + padding;
        float dButtonWidth = (-1) * dX / buffer.size();


        for (UnderlayButton button : buffer) {
            float right = left - dButtonWidth;
            button.onDraw(
                    c,
                    new RectF(
                            left,
                            itemView.getTop() + padding - 1,
                            right,
                            itemView.getBottom() - padding + 6
                    ),
                    pos
            );

            left = right;
        }
    }
}