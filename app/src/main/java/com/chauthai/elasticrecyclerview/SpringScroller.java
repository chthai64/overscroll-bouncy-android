package com.chauthai.elasticrecyclerview;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

/**
 * Created by Chau Thai on 6/8/16.
 */
public class SpringScroller {
    private static final SpringConfig DEFAULT_CONFIG = new SpringConfig(100, 20);
    private final SpringSystem mSpringSystem = SpringSystem.create();

    private Spring mSpringX;
    private Spring mSpringY;

    public SpringScroller() {
        mSpringX = mSpringSystem
                .createSpring()
                .setSpringConfig(DEFAULT_CONFIG);

        mSpringY = mSpringSystem
                .createSpring()
                .setSpringConfig(DEFAULT_CONFIG);
    }

    public void setConfig(double tension, double friction) {
        final SpringConfig config = new SpringConfig(tension, friction);
        mSpringX.setSpringConfig(config);
        mSpringY.setSpringConfig(config);
    }

    public boolean isFinished() {
        return mSpringX.isAtRest() && mSpringY.isAtRest();
    }

    public int getCurrX() {
        return (int) Math.round(mSpringX.getCurrentValue());
    }

    public int getCurrY() {
        return (int) Math.round(mSpringY.getCurrentValue());
    }
}
