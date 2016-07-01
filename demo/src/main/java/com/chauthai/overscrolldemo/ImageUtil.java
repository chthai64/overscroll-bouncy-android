package com.chauthai.overscrolldemo;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

/**
 * Created by Chau Thai on 6/30/16.
 */
public class ImageUtil {
    /**
     * Get circle transformation
     * @param borderWidth in dp
     * @param borderColor color of the border
     */
    public static Transformation getCircleTransformation(int borderWidth, int borderColor) {
        return new RoundedTransformationBuilder()
                .oval(true)
                .borderWidthDp(borderWidth)
                .borderColor(borderColor)
                .build();
    }

    /**
     * Get circle transformation without border
     */
    public static Transformation getCircleTransformationNoBorder() {
        return new RoundedTransformationBuilder()
                .oval(true)
                .build();
    }
}
