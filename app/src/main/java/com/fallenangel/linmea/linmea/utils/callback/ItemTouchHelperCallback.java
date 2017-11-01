package com.fallenangel.linmea.linmea.utils.callback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea.interfaces.OnItemTouchHelperViewHolder;

/**
 * Created by NineB on 10/23/2017.
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private RecyclerView.ViewHolder currentItemViewHolder = null;

    private Context mContext;
    private final OnItemTouchHelper mOnItemTouchHelper;
    private Paint p = new Paint();
    private boolean mSwipeBack = false;
    //private static final float buttonWidth = 300;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
 //   private SwipeControllerActions buttonsActions = null;
    private RectF buttonInstance = null;


    enum ButtonsState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    public ItemTouchHelperCallback(Context context, OnItemTouchHelper onItemTouchHelper) {
        mContext = context;
        mOnItemTouchHelper = onItemTouchHelper;
      //  this.buttonsActions = buttonsActions;
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
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mOnItemTouchHelper.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mOnItemTouchHelper.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }
//
//    @Override
//    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
//        if (mSwipeBack) {
//            mSwipeBack = false;
//            return 0;
//        }
//        return super.convertToAbsoluteDirection(flags, layoutDirection);
//    }

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

    //Block swiped item
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
       // if (flags != ItemTouchHelper.LEFT || flags != ItemTouchHelper.RIGHT) {
            if (mSwipeBack) {
                mSwipeBack = buttonShowedState != ButtonsState.GONE;
                //mSwipeBack = false;
                return 0;
            }
      //  }
//        return super.convertToAbsoluteDirection(flags, layoutDirection);
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        Bitmap icon;
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

            final View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            if (buttonShowedState != ButtonsState.GONE) {
                //block item after swipe
                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) dX = Math.max(dX, itemView.getHeight()+100);
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -itemView.getHeight()-100);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            } else {
                final float finalDX = dX;
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mSwipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                        if(mSwipeBack){
                            if (finalDX < -itemView.getHeight()) buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                            else if (finalDX > itemView.getHeight()) buttonShowedState = ButtonsState.LEFT_VISIBLE;
                            if (buttonShowedState != ButtonsState.GONE) {
                                setTouchDownListener(c, recyclerView, viewHolder, finalDX, dY, actionState, isCurrentlyActive);
                                setItemsClickable(recyclerView, false);
                            }
                        }

                        return false;
                    }
                });
            }






//            RectF leftButton = new RectF(itemView.getLeft()+12, itemView.getTop()+12, itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom()-12);
//            p.setColor(mContext.getResources().getColor(R.color.colorPrimary));
//            c.drawRoundRect(leftButton, corners, corners, p);
//            drawText("EDIT", c, leftButton, p);
//
//            RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop() + 12, itemView.getRight() -12, itemView.getBottom()-12);
//            p.setColor(mContext.getResources().getColor(R.color.colorAccent));
//            c.drawRoundRect(rightButton, corners, corners, p);
//            drawText("DELETE", c, rightButton, p);


            if(dX > 0){
                p.setColor(mContext.getResources().getColor(R.color.favorite));
                RectF background = new RectF((float) itemView.getLeft()+12, (float) itemView.getTop()+12, dX+20,(float) itemView.getBottom()-12);
                c.drawRect(background,p);

                p.setColor(mContext.getResources().getColor(R.color.learned));
                RectF background2 = new RectF((float) itemView.getLeft()+200, (float) itemView.getTop()+12, dX+20,(float) itemView.getBottom()-12);
                c.drawRect(background2,p);

              //  icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_edit);
//               RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
              //  c.drawBitmap(icon,null,icon_dest,p);
            } else {
                p.setColor(mContext.getResources().getColor(R.color.delete));
                RectF background = new RectF((float) itemView.getRight() + dX-20, (float) itemView.getTop()+12,(float) itemView.getRight()-12, (float) itemView.getBottom()-12);
                c.drawRect(background, p);




















//                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_delete);
//                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
//                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

//                icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_delete_white);
//                RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
//                c.drawBitmap();
//                c.drawBitmap(bitmap,
//                        null,
//                        icon_dest,
//                        p);

                p.setColor(mContext.getResources().getColor(R.color.edit));
                RectF background2 = new RectF((float) itemView.getRight() + dX-20, (float) itemView.getTop()+12,(float) itemView.getRight()-200, (float) itemView.getBottom()-12);
                c.drawRect(background2,p);
              //  icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_delete);
               // RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
              //  c.drawBitmap(icon,null,icon_dest,p);
            }
        }
        if (buttonShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        currentItemViewHolder = viewHolder;

//        View itemView = viewHolder.itemView;
//        if (actionState == ACTION_STATE_SWIPE) {
//            if (buttonShowedState != ButtonsState.GONE) {
//                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) dX = Math.max(dX, itemView.getHeight());
//                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -itemView.getHeight());
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            } else {
//                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            }
//        }
//
//        if (buttonShowedState == ButtonsState.GONE) {
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//       }
//        currentItemViewHolder = viewHolder;
//       drawButtons(c, viewHolder);
    }

    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            View itemView = viewHolder.itemView;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSwipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (mSwipeBack) {
                    if (dX < -itemView.getHeight()-12) buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                    else if (dX > itemView.getHeight()-12) buttonShowedState = ButtonsState.LEFT_VISIBLE;

                    if (buttonShowedState != ButtonsState.GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ItemTouchHelperCallback.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    setItemsClickable(recyclerView, true);
                    mSwipeBack = false;

//                    if (buttonsActions != null && buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())) {
//                        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
//                            buttonsActions.onLeftClicked(viewHolder.getAdapterPosition());
//                        }
//                        else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
//                            buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
//                        }
//                    }
                    buttonShowedState = ButtonsState.GONE;
                    currentItemViewHolder = null;
                }
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        View itemView = viewHolder.itemView;

        float buttonWidthWithoutPadding = itemView.getHeight() - 0;
        float corners = 3;

        Paint p = new Paint();

        RectF leftButton = new RectF(itemView.getLeft()+12, itemView.getTop()+12, itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom()-12);
        p.setColor(mContext.getResources().getColor(R.color.colorPrimary));
        c.drawRoundRect(leftButton, corners, corners, p);
        drawText("EDIT", c, leftButton, p);

        RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop() + 12, itemView.getRight() -12, itemView.getBottom()-12);
        p.setColor(mContext.getResources().getColor(R.color.colorAccent));
        c.drawRoundRect(rightButton, corners, corners, p);
        drawText("DELETE", c, rightButton, p);

        buttonInstance = null;
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
            buttonInstance = leftButton;
        }
        else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            buttonInstance = rightButton;
        }
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 60;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }
}