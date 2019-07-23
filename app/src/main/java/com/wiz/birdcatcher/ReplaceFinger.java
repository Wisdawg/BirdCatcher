package com.wiz.birdcatcher;

import android.widget.ImageView;

public class ReplaceFinger {

    public static float[] coordSet(float leftBound, float rightBound, float upBound, float downBound, ImageView picImage){
        float coordinates [] = new float[3];
        float viewTop = picImage.getTop();
        float viewBottom = picImage.getBottom();
        float viewLeft = picImage.getLeft();
        float viewRight = picImage.getRight();

        float viewHeight = picImage.getHeight();
        float viewWidth = picImage.getWidth();

        return coordinates;
    }
}
