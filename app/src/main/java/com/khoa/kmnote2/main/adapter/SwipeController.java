package com.khoa.kmnote2.main.adapter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.kmnote2.MyApplication;
import com.khoa.kmnote2.R;

public class SwipeController extends ItemTouchHelper.Callback {

    private Drawable icon;
    private RectF rectF;
    private Paint paint;
    private OnSwipeListener listener;

    public SwipeController(OnSwipeListener listener) {
        this.listener = listener;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        rectF = new RectF();
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if(listener==null) return;
        NoteRcvAdapter.NoteViewHolder noteViewHolder = (NoteRcvAdapter.NoteViewHolder) viewHolder;
        if(direction == ItemTouchHelper.LEFT) listener.onSwipedLeftListener(noteViewHolder.getNote());
        else if(direction == ItemTouchHelper.RIGHT) listener.onSwipedRightListener(noteViewHolder.getNote());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int itemMargin = 24;

        if (dX > 0) { // Swiping to the right'
            icon = ContextCompat.getDrawable(MyApplication.getContext(), R.drawable.ic_like);
            paint.setColor(ContextCompat.getColor(MyApplication.getContext(), R.color.colorPink));

            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            int d = (int) Math.abs(dX);
            int iconLeft = itemView.getLeft() + (d - itemMargin - icon.getIntrinsicWidth()) / 2;
            int iconRight = iconLeft + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            rectF.set(itemView.getLeft()
                    , itemView.getTop()
                    , itemView.getLeft() + d - itemMargin,
                    itemView.getBottom());
            c.drawRoundRect(rectF, 20, 20, paint);
            icon.draw(c);

        } else if (dX < 0) { // Swiping to the left
            icon = ContextCompat.getDrawable(MyApplication.getContext(), R.drawable.ic_delete);
            paint.setColor(ContextCompat.getColor(MyApplication.getContext(), R.color.colorPrimary));

            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            int d = (int) Math.abs(dX);
            int iconLeft = itemView.getRight() - d + itemMargin + (d - itemMargin - icon.getIntrinsicWidth()) / 2;
            int iconRight = iconLeft + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            rectF.set(itemView.getRight() - d + itemMargin,
                    itemView.getTop()
                    , itemView.getRight()
                    , itemView.getBottom());
            c.drawRoundRect(rectF, 20, 20, paint);
            icon.draw(c);
        }
    }

}