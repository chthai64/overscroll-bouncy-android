/**
 The MIT License (MIT)

 Copyright (c) 2016 Chau Thai

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

package com.chauthai.overscroll;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * A smoother scroller which decelerates the scroll speed quadratically.
 */
public class DecelerateSmoothScroller extends LinearSmoothScroller {
    private static final float DECELERATE_FACTOR = 2.0f;
    private float mInitialSpeed = 1; // px per ms
    private int mDistanceToStop = 100;

    private boolean mShouldForceHorzSnap = false;
    private boolean mShouldForceVertSnap = false;
    private int mForcedHorzSnap = SNAP_TO_ANY;
    private int mForceVertSnap = SNAP_TO_ANY;

    private PointF mScrollVector = new PointF();

    public DecelerateSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        final int dx = calculateDxToMakeVisible(targetView, getHorizontalSnap());
        final int dy = (mScrollVector.y > 0)? -mDistanceToStop : mDistanceToStop;
        final int distance = (int) Math.sqrt(dx * dx + dy * dy);
        final int time = calculateTimeForDeceleration(distance);

        if (time > 0) {
            action.update(-dx, -dy, time, new DecelerateInterpolator(DECELERATE_FACTOR));
        }
    }

    @Override
    protected int calculateTimeForScrolling(int dx) {
        return (int) Math.ceil(Math.abs(dx) / mInitialSpeed);
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return mScrollVector;
    }

    /**
     * Set the initial speed to scroll.
     * @param speed pixels per ms
     */
    public void setInitialSpeed(float speed) {
        mInitialSpeed = speed;
    }

    /**
     * Set the distance where it should stop.
     * @param distance in pixels.
     */
    public void setDistanceToStop(int distance) {
        mDistanceToStop = distance;
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
     *  <li>x &#62; 0 : scroll left</li>
     *  <li>x &#60; 0 : scroll right</li>
     *  <li>y &#62; 0 : scroll up</li>
     *  <li>y &#60; 0 : scroll down</li>
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
