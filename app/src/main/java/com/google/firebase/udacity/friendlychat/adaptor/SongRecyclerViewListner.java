package com.google.firebase.udacity.friendlychat.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.udacity.friendlychat.tabs.ChatFragment;

/**
 * Created by DMI on 24-07-2017.
 */

public class SongRecyclerViewListner implements RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;
    public ChatFragment.ClickListener clickListener;

    public SongRecyclerViewListner(Context context, final RecyclerView recyclerView, final  ChatFragment.ClickListener clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, rv.getChildPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public ChatFragment.ClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ChatFragment.ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
