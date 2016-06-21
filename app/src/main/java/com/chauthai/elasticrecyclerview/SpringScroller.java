package com.chauthai.elasticrecyclerview;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

/**
 * Created by Chau Thai on 6/8/16.
 */
public class SpringScroller extends SimpleSpringListener {
    private static final SpringConfig DEFAULT_CONFIG = new SpringConfig(1000, 200);

    private Spring mSpringX;
    private Spring mSpringY;

    private SpringScrollerListener mListener;

    public interface SpringScrollerListener {
        void onUpdate(int currX, int currY);
        void onAtRest();
    }

    public SpringScroller(SpringScrollerListener listener) {
        final SpringSystem mSpringSystem = SpringSystem.create();

        mSpringX = mSpringSystem
                .createSpring()
                .setSpringConfig(DEFAULT_CONFIG);

        mSpringY = mSpringSystem
                .createSpring()
                .setSpringConfig(DEFAULT_CONFIG);

        mSpringX.addListener(this);
        mSpringY.addListener(this);

        mListener = listener;
    }

    public void setConfig(double tension, double friction) {
        final SpringConfig config = new SpringConfig(tension, friction);
        mSpringX.setSpringConfig(config);
        mSpringY.setSpringConfig(config);
    }

    /**
     * Set vertical and horizontal distances. It will scroll back to 0.
     * @param distanceX vertical distance.
     * @param distanceY horizontal distance.
     */
    public void startScroll(int distanceX, int distanceY) {
        mSpringX.setCurrentValue(distanceX);
        mSpringY.setCurrentValue(distanceY);
        mSpringX.setEndValue(0);
        mSpringY.setEndValue(0);
    }

    public void stopScroll() {
        if (!mSpringX.isAtRest())
            mSpringX.setAtRest();
        if (!mSpringY.isAtRest())
            mSpringY.setAtRest();
    }

    public boolean isAtRest() {
        return mSpringX.isAtRest() && mSpringY.isAtRest();
    }

    public int getCurrX() {
        return (int) Math.round(mSpringX.getCurrentValue());
    }

    public int getCurrY() {
        return (int) Math.round(mSpringY.getCurrentValue());
    }

    @Override
    public void onSpringUpdate(Spring spring) {
        if (mListener != null) {
            mListener.onUpdate(getCurrX(), getCurrY());
        }
    }

    @Override
    public void onSpringAtRest(Spring spring) {
        if (mListener != null && isAtRest()) {
            mListener.onAtRest();
        }
    }
}
