package com.wiz.birdcatcher;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class ReplaceFinger {

    public static Bitmap replace(float leftProp, float rightProp, float upProp, float downProp, Bitmap pic, ImageView birdImage) {
        float coordinates[] = new float[4];

        Bitmap picBitmap = pic;
        Bitmap birdBitmap = ((BitmapDrawable) birdImage.getDrawable()).getBitmap();
        float viewHeight = picBitmap.getHeight();
        float viewWidth = picBitmap.getWidth();

        //take the proportional bound multiply it by dimensions of the pic
        //left bound indexed at 0, right at 1, top at 2, bottom at 3
        coordinates[0] = viewWidth * leftProp;
        coordinates[1] = viewWidth * rightProp;
        coordinates[2] = viewHeight * upProp;
        coordinates[3] = viewHeight * downProp;

        Bitmap result = Bitmap.createBitmap(picBitmap.getWidth(), picBitmap.getHeight(), picBitmap.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(picBitmap, 0f, 0f, null);
        canvas.drawBitmap(birdBitmap, null, new RectF(coordinates[0],coordinates[2],coordinates[1],coordinates[3]),null);

/*        System.out.println(picImage.getLeft() + ": " + picImage.getTop() + ": " + coordinates[0] + ": " + coordinates[2] + ": ");
        System.out.println(picBitmap.getWidth() + " is: " + picBitmap.getHeight());
        System.out.println(resizedBirdBitmap.getWidth() + " is: " +resizedBirdBitmap.getHeight());
        System.out.println(newWidth + " is: " +newHeight);
        System.out.println(birdBitmap.getWidth() + " is: " +birdBitmap.getHeight());*/
        return result;

    }
}
