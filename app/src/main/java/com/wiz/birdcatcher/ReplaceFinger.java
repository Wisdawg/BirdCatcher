package com.wiz.birdcatcher;

import android.widget.ImageView;

public class ReplaceFinger {

    public static int[] coordSet(float leftProp, float rightProp, float upProp, float downProp, ImageView picImage){
        int coordinates [] = new int[4];
        float viewTop = picImage.getTop();
        float viewBottom = picImage.getBottom();
        float viewLeft = picImage.getLeft();

        float viewHeight = picImage.getHeight();
        float viewWidth = picImage.getWidth();

        //take the proportional bound multiply it by dimensions of the pic
        //left bound indexed at 0, right at 1, top at 2, bottom at 3
        coordinates[0] = Math.round(viewLeft + (viewWidth * leftProp));
        coordinates[1] = Math.round(viewLeft + (viewWidth * rightProp));
        coordinates[2] = Math.round(viewTop + (viewHeight * upProp));
        coordinates[3] = Math.round(viewTop + (viewHeight * downProp));

        System.out.println("We got topview:"+viewTop+"   Also viewHeight:" +viewHeight+ "  Also leftProportion:"+upProp+"   Also: the final left bound:"+coordinates[2]);
        System.out.println("We got bottomview:"+viewBottom+"   Also viewHeight:" +viewHeight+ "  Also leftProportion:"+downProp+"   Also: the final left bound:"+coordinates[3]);

        return coordinates;
    }
}
