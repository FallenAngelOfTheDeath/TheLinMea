package com.fallenangel.linmea._test;
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

import com.fallenangel.linmea._modulus.non.view.UnderlayButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class SwipeHelper extends ItemTouchHelper.Callback {


    private static final String TAG = "ItemTouchHelperCallback";
    public static final int BUTTON_WIDTH = 160;
    private RecyclerView recyclerView;
    private List<UnderlayButton> buttons;
    private GestureDetector gestureDetector;
    private int swipedPos = -1;
    private float swipeThreshold = 0.5f;
    //private Map<Integer, List<UnderlayButton>> buttonsBuffer;
    private Map<Integer, List<UnderlayButton>> mLeftButtonsBuffer;
    private Map<Integer, List<UnderlayButton>> mRightButtonsBuffer;
    private Queue<Integer> recoverQueue;
    private Context mContext;
    private int mSwipeFlags;
    //private final OnItemTouchHelper mOnItemTouchHelper;

    public static final int BOTH_SIDE = 0;
    public static final int LEFT_SIDE = 1;
    public static final int RIGHT_SIDE = 2;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            for (UnderlayButton button : buttons){
                if(button.onClickUnderlayButton(e.getX(), e.getY()))
                    break;
            }

            return true;
        }
    };

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

    public SwipeHelper(Context context, int swipeFlag, RecyclerView recyclerView) {
        //super(0, ItemTouchHelper.LEFT);
        //this.mOnItemTouchHelper = onItemTouchHelper;
        this.mSwipeFlags = swipeFlag;
        this.mContext = context;
        this.recyclerView = recyclerView;
        this.buttons = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        //buttonsBuffer = new HashMap<>();
        mLeftButtonsBuffer = new HashMap<>();
        mRightButtonsBuffer = new HashMap<>();
        recoverQueue = new LinkedList<Integer>(){
            @Override
            public boolean add(Integer o) {
                if (contains(o))
                    return false;
                else
                    return super.add(o);
            }
        };

        attachSwipe();
    }

   @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags;
        switch (mSwipeFlags){
            case BOTH_SIDE:
                swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                break;
            case LEFT_SIDE:
                swipeFlags = ItemTouchHelper.LEFT;
                break;
            case RIGHT_SIDE:
                swipeFlags = ItemTouchHelper.RIGHT;
                break;
            default:
                swipeFlags = -1;
                break;
        }
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
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();

        if (swipedPos != pos)
            recoverQueue.add(swipedPos);

        swipedPos = pos;

        switch (direction){
            case ItemTouchHelper.RIGHT:
                if (mLeftButtonsBuffer.containsKey(swipedPos))
                    buttons.addAll(mLeftButtonsBuffer.get(swipedPos));
                else
                    mLeftButtonsBuffer.clear();
                mLeftButtonsBuffer.clear();
                break;
            case ItemTouchHelper.LEFT:
                if (mRightButtonsBuffer.containsKey(swipedPos))
                    buttons.addAll(mRightButtonsBuffer.get(swipedPos));
                else
                    mRightButtonsBuffer.clear();
                mRightButtonsBuffer.clear();
                break;
        }

        swipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
        recoverSwipedItem();
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f * defaultValue;
    }

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
                List<UnderlayButton> buffer = new ArrayList<>();

                if (!mRightButtonsBuffer.containsKey(pos)){
                    instantiateUnderlayButton(viewHolder, buffer);
                    mRightButtonsBuffer.put(pos, buffer);
                }
                else {
                    buffer = mRightButtonsBuffer.get(pos);
                }

                translationX = dX * buffer.size() * BUTTON_WIDTH / itemView.getWidth();
                drawButtons(c, itemView, buffer, pos, translationX);
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

    private void drawButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX){
        float right = itemView.getRight();
        float dButtonWidth = (-1) * dX / buffer.size();

        for (UnderlayButton button : buffer) {
            float left = right - dButtonWidth;
            button.onDraw(
                    c,
                    new RectF(
                            left,
                            itemView.getTop(),
                            right,
                            itemView.getBottom()
                    ),
                    pos
            );

            right = left;
        }
    }

    public void attachSwipe(){
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public abstract void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);

}
// /
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.helper.ItemTouchHelper;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//
//import com.fallenangel.linmea._modulus.non.view.UnderlayButton;
//import com.fallenangel.linmea.interfaces.OnItemTouchHelperViewHolder;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Queue;
//
///**
// * Created by NineB on 11/5/2017.
// */
//
//public abstract class SwipeHelper extends ItemTouchHelper.Callback {
//
//    public static final int BUTTON_WIDTH = 160;
//    private RecyclerView recyclerView;
//    private List<UnderlayButton> buttons;
//    private GestureDetector gestureDetector;
//    private int swipedPos = -1;
//    private float swipeThreshold = 0.5f;
//    private Map<Integer, List<UnderlayButton>> buttonsBuffer;
//    private Map<Integer, List<UnderlayButton>> mLeftButtonsBuffer;
//    private Map<Integer, List<UnderlayButton>> mRightButtonsBuffer;
//    private Queue<Integer> recoverQueue;
//    private Context mContext;
//    private int mSwipeFlags;
//    //private final OnItemTouchHelper mOnItemTouchHelper;
//
//    public static final int BOTH_SIDE = 0;
//    public static final int LEFT_SIDE = 1;
//    public static final int RIGHT_SIDE = 2;
//
//    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent e) {
//            for (UnderlayButton button : buttons){
//                if(button.onClickUnderlayButton(e.getX(), e.getY()))
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
//            if (swipedPos < 0) return false;
//            Point point = new Point((int) e.getRawX(), (int) e.getRawY());
//
//            RecyclerView.ViewHolder swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPos);
//            View swipedItem = swipedViewHolder.itemView;
//            Rect rect = new Rect();
//            swipedItem.getGlobalVisibleRect(rect);
//
//            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP ||e.getAction() == MotionEvent.ACTION_MOVE) {
//                if (rect.top < point.y && rect.bottom > point.y)
//                    gestureDetector.onTouchEvent(e);
//                else {
//                    recoverQueue.add(swipedPos);
//                    swipedPos = -1;
//                    recoverSwipedItem();
//                }
//            }
//            return false;
//        }
//    };
//
//    public abstract void onCreateRightUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);
//    public abstract void onCreateLeftUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);
//
//    public SwipeHelper(Context context, int swipeFlag, RecyclerView recyclerView) {
//        //super(0, ItemTouchHelper.LEFT);
//       // this.mOnItemTouchHelper = onItemTouchHelper;
//        this.mSwipeFlags = swipeFlag;
//        this.mContext = context;
//        this.recyclerView = recyclerView;
//        this.buttons = new ArrayList<>();
//        this.gestureDetector = new GestureDetector(context, gestureListener);
//        this.recyclerView.setOnTouchListener(onTouchListener);
//        buttonsBuffer = new HashMap<>();
//        mLeftButtonsBuffer = new HashMap<>();
//        mRightButtonsBuffer = new HashMap<>();
//        recoverQueue = new LinkedList<Integer>(){
//            @Override
//            public boolean add(Integer o) {
//                if (contains(o))
//                    return false;
//                else
//                    return super.add(o);
//            }
//        };
//
//        attachSwipe();
//    }
//
//    @Override
//    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//        int swipeFlags;
//        switch (mSwipeFlags){
//            case BOTH_SIDE:
//                swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
//                break;
//            case LEFT_SIDE:
//                swipeFlags = ItemTouchHelper.LEFT;
//                break;
//            case RIGHT_SIDE:
//                swipeFlags = ItemTouchHelper.RIGHT;
//                break;
//            default:
//                swipeFlags = -1;
//                break;
//        }
//        return makeMovementFlags(dragFlags, swipeFlags);
//    }
//
//    @Override
//    public boolean isLongPressDragEnabled() {
//        return false;
//    }
//
//    @Override
//    public boolean isItemViewSwipeEnabled() {
//        return true;
//    }
//
//
//    @Override
//    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//       // mOnItemTouchHelper.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//        return false;
//    }
//
//    @Override
//    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        int pos = viewHolder.getAdapterPosition();
//
//        if (swipedPos != pos)
//            recoverQueue.add(swipedPos);
//
//        swipedPos = pos;
//
//
//        switch (direction){
//            case ItemTouchHelper.LEFT:
//                if (mLeftButtonsBuffer.containsKey(swipedPos))
//                    buttons.addAll(mLeftButtonsBuffer.get(swipedPos));
//                else
//                    mLeftButtonsBuffer.clear();
//                mLeftButtonsBuffer.clear();
//                break;
//            case ItemTouchHelper.RIGHT:
//                if (mRightButtonsBuffer.containsKey(swipedPos))
//                    buttons.addAll(mRightButtonsBuffer.get(swipedPos));
//                else
//                    mRightButtonsBuffer.clear();
//                mRightButtonsBuffer.clear();
//                break;
//        }
////        if (buttonsBuffer.containsKey(swipedPos))
////            buttons = buttonsBuffer.get(swipedPos);
////        else
////            buttons.clear();
//
//        buttonsBuffer.clear();
//        swipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
//        recoverSwipedItem();
//    }
//
//    @Override
//    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//        super.onSelectedChanged(viewHolder, actionState);
//        //Call when item dropped after move for view holder
//        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//            if (viewHolder instanceof OnItemTouchHelperViewHolder) {
//                OnItemTouchHelperViewHolder itemViewHolder = (OnItemTouchHelperViewHolder) viewHolder;
//                itemViewHolder.onItemSelected(viewHolder);
//            }
//        }
//        //Call when item dropped after move
//        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && actionState != ItemTouchHelper.ACTION_STATE_DRAG) {
//          //  mOnItemTouchHelper.onItemMoveComplete(viewHolder);
//        }
////
////
////        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
////            swipedPos = viewHolder.getAdapterPosition();
////        }
//    }
//
//    @Override
//    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        super.clearView(recyclerView, viewHolder);
//        if (viewHolder instanceof OnItemTouchHelperViewHolder) {
//            OnItemTouchHelperViewHolder itemViewHolder =
//                    (OnItemTouchHelperViewHolder) viewHolder;
//            itemViewHolder.onItemClear(recyclerView, viewHolder);
//        }
//    }
//
//    @Override
//    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
//        return swipeThreshold;
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
//    @Override
//    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        int pos = viewHolder.getAdapterPosition();
//        float translationX = dX;
//        View itemView = viewHolder.itemView;
//
//        if (pos < 0){
//            swipedPos = pos;
//            return;
//        }
//
//        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
//            if(dX < 0) {
////                List<UnderlayButton> buffer = new ArrayList<>();
////
////                if (!buttonsBuffer.containsKey(pos)){
////                    onCreateRightUnderlayButton(viewHolder, buffer);
////                    buttonsBuffer.put(pos, buffer);
////                }
////                else {
////                    buffer = buttonsBuffer.get(pos);
////                }
////
////                translationX = dX * buffer.size() * BUTTON_WIDTH / itemView.getWidth();
////                drawRightButtons(c, itemView, buffer, pos, translationX);
//                List<UnderlayButton> rightBuffer = new ArrayList<>();
//                if (!mRightButtonsBuffer.containsKey(pos)){
//                    onCreateRightUnderlayButton(viewHolder, rightBuffer);
//                    mRightButtonsBuffer.put(pos, rightBuffer);
//                }
//                else {
//                    rightBuffer = mRightButtonsBuffer.get(pos);
//                }
//                translationX = dX * rightBuffer.size() * BUTTON_WIDTH / itemView.getWidth();
//                drawRightButtons(c, itemView, rightBuffer, pos, translationX);
//            } else {
////                List<UnderlayButton> buffer = new ArrayList<>();
////
////                if (!buttonsBuffer.containsKey(pos)){
////                    onCreateLeftUnderlayButton(viewHolder, buffer);
////                    buttonsBuffer.put(pos, buffer);
////                }
////                else {
////                    buffer = buttonsBuffer.get(pos);
////                }
////
////                translationX = dX * buffer.size() * BUTTON_WIDTH / itemView.getWidth();
////                drawLeftButtons(c, itemView, buffer, pos, translationX);
//
//                List<UnderlayButton> leftBuffer = new ArrayList<>();
//                if (!mLeftButtonsBuffer.containsKey(pos)){
//                    onCreateLeftUnderlayButton(viewHolder, leftBuffer);
//                    mLeftButtonsBuffer.put(pos, leftBuffer);
//                }
//                else {
//                    leftBuffer = mLeftButtonsBuffer.get(pos);
//                }
//                translationX = dX * leftBuffer.size() * BUTTON_WIDTH / itemView.getWidth();
//                drawLeftButtons(c, itemView, leftBuffer, pos, translationX);
//            }
//        }
//
//        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
//    }
//
//    private synchronized void recoverSwipedItem(){
//        while (!recoverQueue.isEmpty()){
//            int pos = recoverQueue.poll();
//            if (pos > -1) {
//                recyclerView.getAdapter().notifyItemChanged(pos);
//            }
//        }
//    }
//
//    private void drawRightButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX){
//        float right = itemView.getRight();// - padding;
//        float dButtonWidth = (-1) * dX / buffer.size();
//
//        for (UnderlayButton button : buffer) {
//            float left = right - dButtonWidth;
//            button.onDraw(
//                    c,
//                    new RectF(
//                            left,
//                            itemView.getTop(),// + padding,// - 1,
//                            right,
//                            itemView.getBottom()// - padding// + 6
//                    ),
//                    pos
//            );
//
//            right = left;
//        }
//    }
//
//    private void drawLeftButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX) {
//        float left = itemView.getLeft();// + padding;
//        float dButtonWidth = (-1) * dX / buffer.size();
//
//
//        for (UnderlayButton button : buffer) {
//            float right = left - dButtonWidth;
//            button.onDraw(
//                    c,
//                    new RectF(
//                            left,
//                            itemView.getTop(),// + padding, // - 1,
//                            right,
//                            itemView.getBottom()// - padding // + 6
//                    ),
//                    pos
//            );
//
//            left = right;
//        }
//    }
//
//
//    public void attachSwipe(){
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//    }
//
//
////    public static class UnderlayButton {
////        private String text;
////        private int imageResId;
////        private int color;
////        private int pos;
////        private RectF clickRegion;
////        private UnderlayButtonClickListener clickListener;
////        private Context mContext;
////        public UnderlayButton(Context context, String text, int imageResId, int color, UnderlayButtonClickListener clickListener) {
////            this.text = text;
////            this.imageResId = imageResId;
////            this.color = color;
////            this.clickListener = clickListener;
////            this.mContext = context;
////        }
////
////        public boolean onClick(float x, float y){
////            if (clickRegion != null && clickRegion.contains(x, y)){
////                clickListener.onClick(pos);
////                return true;
////            }
////
////            return false;
////        }
////
////        public void onDraw(Canvas c, RectF rect, int pos){
////            Paint p = new Paint();
////
////            // Draw background
////            p.setColor(color);
////            c.drawRect(rect, p);
////
////            // Draw Text
////            p.setColor(Color.WHITE);
////            p.setTextSize(12);
////
////            Rect r = new Rect();
////            float cHeight = rect.height();
////            float cWidth = rect.width();
////            p.setTextAlign(Paint.Align.LEFT);
////            p.getTextBounds(text, 0, text.length(), r);
////            float x = cWidth / 2f - r.width() / 2f - r.left;
////            float y = cHeight / 2f + r.height() / 2f - r.bottom;
////            c.drawText(text, rect.left + x, rect.top + y, p);
////
////            clickRegion = rect;
////            this.pos = pos;
////        }
////    }
////
////    public interface UnderlayButtonClickListener {
////        void onClick(int pos);
////    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////    private RecyclerView recyclerView;
////    private List<UnderlayButton> buttons;
////    private GestureDetector gestureDetector;
////    private int swipedPos = -1;
////    private float swipeThreshold = 0.5f;
////    private Map<Integer, List<UnderlayButton>> buttonsBufferLeft;
////    private Map<Integer, List<UnderlayButton>> buttonsBufferRight;
////    private Queue<Integer> recoverQueue;
////    private final OnItemTouchHelper mOnItemTouchHelper;
////
////    private int padding = 12;
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
//////
////    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
////        @Override
////        public boolean onTouch(View view, MotionEvent e) {
////            if (swipedPos < 0) return false;
////            Point point = new Point((int) e.getRawX(), (int) e.getRawY());
////
////            RecyclerView.ViewHolder swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPos);
////            View swipedItem = swipedViewHolder.itemView;
////            Rect rect = new Rect();
////            swipedItem.getGlobalVisibleRect(rect);
////
////            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP ||e.getAction() == MotionEvent.ACTION_MOVE) {
////                if (rect.top < point.y && rect.bottom > point.y)
////                    gestureDetector.onTouchEvent(e);
////                else {
////                    recoverQueue.add(swipedPos);
////                    swipedPos = -1;
////                    recoverSwipedItem();
////                }
////            }
////            return false;
////        }
////    };
////
////
////
////
////    public SwipeHelper(Context context, RecyclerView recyclerView, OnItemTouchHelper onItemTouchHelper) {
//////        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
//////                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
////        this.recyclerView = recyclerView;
////        this.buttons = new ArrayList<>();
////        this.mOnItemTouchHelper = onItemTouchHelper;
////
////        //this.gestureDetector = new GestureDetector(context, gestureListener);
////    //    this.recyclerView.setOnTouchListener(onTouchListener);
////        buttonsBufferLeft = new HashMap<>();
////        buttonsBufferRight = new HashMap<>();
////        recoverQueue = new LinkedList<Integer>(){
////            @Override
////            public boolean add(Integer o) {
////                if (contains(o))
////                    return false;
////                else
////                    return super.add(o);
////            }
////        };
////        // attachSwipe();
////    }
////
////        @Override
////    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
////        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
////        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
////        return makeMovementFlags(dragFlags, swipeFlags);
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
////    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
////        mOnItemTouchHelper.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
////        return false;
////    }
////
////    @Override
////    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
////        int pos = viewHolder.getAdapterPosition();
////
////        if (swipedPos != pos)
////            recoverQueue.add(swipedPos);
////
////        swipedPos = pos;
////
////        if (buttonsBufferLeft.containsKey(swipedPos))
////            buttons = buttonsBufferLeft.get(swipedPos);
////        else
////            buttons.clear();
////
////        if (buttonsBufferRight.containsKey(swipedPos))
////            buttons = buttonsBufferRight.get(swipedPos);
////        else
////            buttons.clear();
////
////        buttonsBufferLeft.clear();
////        buttonsBufferRight.clear();
////        swipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
////        recoverSwipedItem();
////
////        //mOnItemTouchHelper.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
////    }
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
//////    @Override
//////    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
//////        return swipeThreshold;
//////    }
////
//////    @Override
//////    public float getSwipeEscapeVelocity(float defaultValue) {
//////        return 0.1f * defaultValue;
//////    }
////
//////    @Override
//////    public float getSwipeVelocityThreshold(float defaultValue) {
//////        return 5.0f * defaultValue;
//////    }
////
////    @Override
////    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
////        int pos = viewHolder.getAdapterPosition();
////        float translationX = dX;
////        View itemView = viewHolder.itemView;
////
////
////
////        if (pos < 0){
////            swipedPos = pos;
////            return;
////        }
////
////        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
////            List<UnderlayButton> leftButtonsBuffer = new ArrayList<>();
////            List<UnderlayButton> rightButtonsBuffer = new ArrayList<>();
////            if(dX < 0) {
////                if (!buttonsBufferRight.containsKey(pos)){
////                    onCreateRightUnderlayButton(viewHolder, rightButtonsBuffer);
////                    //instantiateUnderlayButton(viewHolder, buffer);
////                    buttonsBufferRight.put(pos, rightButtonsBuffer);
////                }
////                else {
////                    rightButtonsBuffer = buttonsBufferRight.get(pos);
////                }
////                translationX = dX * rightButtonsBuffer.size() * BUTTON_WIDTH / itemView.getWidth();
////                drawRightButtons(c, itemView, rightButtonsBuffer, pos, translationX);
////            } else {
////                if (!buttonsBufferLeft.containsKey(pos)){
////                    onCreateLeftUnderlayButton(viewHolder, leftButtonsBuffer);
////                    //instantiateUnderlayButton(viewHolder, buffer);
////                    buttonsBufferLeft.put(pos, leftButtonsBuffer);
////                }
////                else {
////                    leftButtonsBuffer = buttonsBufferLeft.get(pos);
////                }
////
////                translationX = dX * leftButtonsBuffer.size() * BUTTON_WIDTH / itemView.getWidth();
////                drawLeftButtons(c, itemView, leftButtonsBuffer, pos, translationX);
////            }
////        }
////
////
////        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
////    }
////
////    private synchronized void recoverSwipedItem(){
////        while (!recoverQueue.isEmpty()){
////            int pos = recoverQueue.poll();
////            if (pos > -1) {
////                recyclerView.getAdapter().notifyItemChanged(pos);
////            }
////        }
////    }
////
////    private void drawRightButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX){
////        float right = itemView.getRight() + padding;
////        float dButtonWidth = (-1) * dX / buffer.size();
////
////        for (UnderlayButton button : buffer) {
////            float left = right - dButtonWidth - padding;
////            button.onDraw(
////                    c,
////                    new RectF(
////                            left,
////                            itemView.getTop() + padding,
////                            right,
////                            itemView.getBottom() - padding
////                    ),
////                    pos
////            );
////
////            right = left;
////        }
////    }
////
////    private void drawLeftButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX){
////        float left = itemView.getLeft() + padding;
////        float dButtonWidth = (-1) * dX / buffer.size();
////
////
////        for (UnderlayButton button : buffer) {
////            float right = left - dButtonWidth + padding;
////            button.onDraw(
////                    c,
////                    new RectF(
////                            left,
////                            itemView.getTop() + padding,
////                            right ,
////                            itemView.getBottom() - padding
////                    ),
////                    pos
////            );
////
////            left = right;
////        }
////    }
////
////    public void attachSwipe(){
////        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
////        itemTouchHelper.attachToRecyclerView(recyclerView);
////    }
////
////    //public abstract void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);
////    public abstract void onCreateRightUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);
////    public abstract void onCreateLeftUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);
////
////    public static class UnderlayButton {
////        private String text;
////        private int imageResId;
////        private int color;
////        private int pos;
////        private RectF clickRegion;
////        private UnderlayButtonClickListener clickListener;
////
////        public UnderlayButton(String text, int imageResId, int color, UnderlayButtonClickListener clickListener) {
////            this.text = text;
////            this.imageResId = imageResId;
////            this.color = color;
////            this.clickListener = clickListener;
////        }
////
////        public boolean onClick(float x, float y){
////            if (clickRegion != null && clickRegion.contains(x, y)){
////                clickListener.onClick(pos);
////                return true;
////            }
////            return false;
////        }
////
////        public void onDraw(Canvas c, RectF rect, int pos){
////            Paint p = new Paint();
////
////            // Draw background
////            p.setColor(color);
////            c.drawRect(rect, p);
////
////            // Draw Text
////            p.setColor(Color.WHITE);
////            p.setTextSize(32);
////
////            Rect r = new Rect();
////            float cHeight = rect.height();
////            float cWidth = rect.width();
////            p.setTextAlign(Paint.Align.LEFT);
////            p.getTextBounds(text, 0, text.length(), r);
////            float x = cWidth / 2f - r.width() / 2f - r.left;
////            float y = cHeight / 2f + r.height() / 2f - r.bottom;
////            c.drawText(text, rect.left + x, rect.top + y, p);
////
////            clickRegion = rect;
////            this.pos = pos;
////        }
////    }
////
////    public interface UnderlayButtonClickListener {
////        void onClick(int pos);
////    }
//}