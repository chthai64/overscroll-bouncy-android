package com.chauthai.elasticrecyclerview;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by Chau Thai on 5/30/2016.
 */
public class ConstantSmoothScroller extends LinearSmoothScroller {
    private static final String TAG = "yolo";
    private float mScrollSpeed = 1; // px per ms

    private boolean mShouldForceHorzSnap = false;
    private boolean mShouldForceVertSnap = false;
    private int mForcedHorzSnap = SNAP_TO_ANY;
    private int mForceVertSnap = SNAP_TO_ANY;

    private PointF mScrollVector = new PointF();

    public ConstantSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        final int dx = calculateDxToMakeVisible(targetView, getHorizontalSnap());
        final int dy = calculateDyToMakeVisible(targetView, getVerticalSnap());
        final int distance = (int) Math.sqrt(dx * dx + dy * dy);
        final int time = calculateTimeForDeceleration(distance);

//        Log.d(TAG, "dy: " + dy + ", snapPref: " + getHorizontalSnapPreference());
//        Log.d(TAG, "onTargetFound, distance: " + distance + ", time: " + time);

        if (time > 0) {
            action.update(-dx, -dy, time, mLinearInterpolator);
        }
    }

    @Override
    protected int calculateTimeForScrolling(int dx) {
        int time = (int) Math.ceil(Math.abs(dx) / mScrollSpeed);
//        Log.d("yolo", "calculate time dx: " + dx + ", speed: " + mScrollSpeed + ", time: " + time);
        return time;
    }

    @Override
    protected int calculateTimeForDeceleration(int dx) {
        return calculateTimeForScrolling(dx);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d(TAG, "onStop");
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return mScrollVector;
    }

    /**
     * @param speed pixels per ms
     */
    public void setScrollSpeed(float speed) {
        mScrollSpeed = speed;
    }

    public void forceHorizontalSnap(int horizontalSnap) {
        mShouldForceHorzSnap = true;
        mForcedHorzSnap = horizontalSnap;
    }

    public void forceVerticalSnap(int verticalSnap) {
        mShouldForceVertSnap = true;
        mForceVertSnap = verticalSnap;
    }

    /**
     * Set the direction to scroll.
     * @param vector
     * <ul>
     *  <li>x > 0 : scroll left</li>
     *  <li>x < 0 : scroll right</li>
     *  <li>y > 0 : scroll up</li>
     *  <li>y < 0 : scroll down</li>
     * </ul>
     */
    public void setScrollVector(PointF vector) {
        mScrollVector = vector;
    }

    private int getHorizontalSnap() {
        if (mShouldForceHorzSnap) {
            return mForcedHorzSnap;
        }
        return getHorizontalSnapPreference();
    }

    private int getVerticalSnap() {
        if (mShouldForceVertSnap) {
            return mForceVertSnap;
        }
        return getVerticalSnapPreference();
    }
}
