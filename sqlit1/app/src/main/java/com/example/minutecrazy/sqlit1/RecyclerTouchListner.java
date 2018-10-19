package com.example.minutecrazy.sqlit1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerTouchListner implements RecyclerView.OnItemTouchListener{

    private ClickListener clickListener;
    private GestureDetector gestureDetector;

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
        if (child != null && clickListener != null &&
                gestureDetector.onTouchEvent(motionEvent)){
            clickListener.onClick(child,recyclerView.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    public interface ClickListener {
        void onClick(View view, int p);
        void onLongClick(View view, int p);
    }

    public RecyclerTouchListner(Context context,
                                final RecyclerView recyclerView,
                                final ClickListener clickListener){
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener(){
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                    public void onLongPress(MotionEvent e) {
                        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (child != null && clickListener != null) {
                            clickListener.onLongClick(child,
                                    recyclerView.getChildAdapterPosition(child));
                        }
                    }
                });
    }
}
