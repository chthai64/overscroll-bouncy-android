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


/**
 * Created by Chau Thai on 6/27/16.
 */
public class BouncyConfig {
    private static final double DEF_SPEED_FACTOR = 5;
    private static final int DEF_GAP_LIMIT = 220; // dp
    private static final int DEF_VIEW_COUNT_ESTIMATE_SIZE = 5;
    private static final int DEF_MAX_ADAPTER_SIZE_TO_ESTIMATE = 20;
    private static final int DEF_TENSION = 1000;
    private static final int DEF_FRICTION = 200;

    protected final int gapLimit;
    protected final double speedFactor;
    protected final int tension;
    protected final int friction;
    protected final int viewCountEstimateSize;
    protected final int maxAdapterSizeToEstimate;

    public static final BouncyConfig DEFAULT = new Builder().build();

    private BouncyConfig(
            int gapLimit,
            double speedFactor,
            int viewCountToEstimateSize,
            int maxAdapterSizeToEstimate,
            int friction,
            int tension)
    {
        this.gapLimit = gapLimit;
        this.speedFactor = speedFactor;
        this.viewCountEstimateSize = viewCountToEstimateSize;
        this.maxAdapterSizeToEstimate = maxAdapterSizeToEstimate;
        this.friction = friction;
        this.tension = tension;
    }

    public int getGapLimit() {
        return gapLimit;
    }

    public double getSpeedFactor() {
        return speedFactor;
    }

    public int getTension() {
        return tension;
    }

    public int getFriction() {
        return friction;
    }

    public int getViewCountEstimateSize() {
        return viewCountEstimateSize;
    }

    public int getMaxAdapterSizeToEstimate() {
        return maxAdapterSizeToEstimate;
    }

    @Override
    public String toString() {
        return "BouncyConfig{" +
                "gapLimit=" + gapLimit +
                ", speedFactor=" + speedFactor +
                ", tension=" + tension +
                ", friction=" + friction +
                ", viewCountEstimateSize=" + viewCountEstimateSize +
                ", maxAdapterSizeToEstimate=" + maxAdapterSizeToEstimate +
                '}';
    }

    public static class Builder {
        private int nestedGapLimit = DEF_GAP_LIMIT;
        private double nestedSpeedFactor = DEF_SPEED_FACTOR;
        private int nestedTension = DEF_TENSION;
        private int nestedFriction = DEF_FRICTION;
        private int nestedViewCountEstimateSize = DEF_VIEW_COUNT_ESTIMATE_SIZE;
        private int nestedMaxAdapterSizeToEstimate = DEF_MAX_ADAPTER_SIZE_TO_ESTIMATE;

        public Builder setGapLimit(int gapLimit) {
            nestedGapLimit = gapLimit;
            return this;
        }

        public Builder setSpeedFactor(double speedFactor) {
            if (speedFactor < 1)
                speedFactor = 1;
            nestedSpeedFactor = speedFactor;
            return this;
        }

        public Builder setTension(int tension) {
            nestedTension = tension;
            return this;
        }

        public Builder setFriction(int friction) {
            nestedFriction = friction;
            return this;
        }

        public Builder setViewCountEstimateSize(int count) {
            nestedViewCountEstimateSize = count;
            return this;
        }

        public Builder setMaxAdapterSizeToEstimate(int size) {
            nestedMaxAdapterSizeToEstimate = size;
            return this;
        }

        public BouncyConfig build() {
            return new BouncyConfig(
                    nestedGapLimit,
                    nestedSpeedFactor,
                    nestedViewCountEstimateSize,
                    nestedMaxAdapterSizeToEstimate,
                    nestedFriction,
                    nestedTension
            );
        }
    }
}
