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
 * Configuration for the over-scroll behaviour.
 */
public class BouncyConfig {
    private static final double DEF_SPEED_FACTOR = 5;
    private static final int DEF_GAP_LIMIT = 220; // dp
    private static final int DEF_VIEW_COUNT_ESTIMATE_SIZE = 5;
    private static final int DEF_MAX_ADAPTER_SIZE_TO_ESTIMATE = 20;
    private static final int DEF_TENSION = 1000;
    private static final int DEF_FRICTION = 200;

    /**
     * The maximum over-scroll gap size (in dp).
     */
    protected final int gapLimit;

    /**
     * The higher the speedFactor is, the less the view will utilize
     * the gap limit. Minimum value is 1.
     */
    protected final double speedFactor;

    /**
     * Tension of the spring. It should be set to a high value (ex. 1000)
     * for smooth animation.
     */
    protected final int tension;

    /**
     * Friction of the spring. High friction value will slow down the
     * scroll-back speed.
     */
    protected final int friction;

    /**
     * The number of children views to estimate the content size of RecyclerView (or ListView).
     * The estimation is computed by averaging the children views size then multiply by the
     * total items inside the adapter.
     */
    protected final int viewCountEstimateSize;

    /**
     * The maximum adapter size (number of items in the adapter) that the system will include
     * content size estimation of the RecyclerView (or ListView) in the calculation.
     */
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

        /**
         * @param gapLimit The maximum over-scroll gap size (in dp). The default
         *                 value is 220dp.
         */
        public Builder setGapLimit(int gapLimit) {
            nestedGapLimit = gapLimit;
            return this;
        }

        /**
         * @param speedFactor The higher the speedFactor is, the less the view will utilize
         * the gap limit. Minimum value is 1. The default value is 5.
         */
        public Builder setSpeedFactor(double speedFactor) {
            if (speedFactor < 1)
                speedFactor = 1;
            nestedSpeedFactor = speedFactor;
            return this;
        }

        /**
         * @param tension Tension of the spring. It should be set to a high value (ex. 1000)
         * for smooth animation. The default value is 1000.
         */
        public Builder setTension(int tension) {
            nestedTension = tension;
            return this;
        }

        /**
         * @param friction Friction of the spring. High friction value will slow down the
         * scroll-back speed. The default value is 200.
         */
        public Builder setFriction(int friction) {
            nestedFriction = friction;
            return this;
        }

        /**
         * @param count The number of children views to estimate the content size of RecyclerView (or ListView).
         * The estimation is computed by averaging the children views size then multiply by the
         * total items inside the adapter. The default value is 5.
         */
        public Builder setViewCountEstimateSize(int count) {
            nestedViewCountEstimateSize = count;
            return this;
        }

        /**
         * @param size The maximum adapter size (number of items in the adapter) that the system will include
         * content size estimation of the RecyclerView (or ListView) in the calculation. The default
         *             value is 20.
         */
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
