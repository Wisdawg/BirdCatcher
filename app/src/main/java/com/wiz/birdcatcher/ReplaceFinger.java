package com.wiz.birdcatcher;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class ReplaceFinger {

    public static Bitmap replace(float leftProp, float rightProp, float upProp, float downProp, ImageView picImage, ImageView birdImage) {
        float coordinates[] = new float[4];

        Bitmap picBitmap = ((BitmapDrawable) picImage.getDrawable()).getBitmap();
        Bitmap birdBitmap = ((BitmapDrawable) birdImage.getDrawable()).getBitmap();
        float viewHeight = picBitmap.getHeight();
        float viewWidth = picBitmap.getWidth();

        //take the proportional bound multiply it by dimensions of the pic
        //left bound indexed at 0, right at 1, top at 2, bottom at 3
        coordinates[0] = viewWidth * leftProp;
        coordinates[1] = viewWidth * rightProp;
        coordinates[2] = viewHeight * upProp;
        coordinates[3] = viewHeight * downProp;

      /*  System.out.println("We got topview:"+viewTop+"   Also viewHeight:" +viewHeight+ "  Also leftProportion:"+upProp+"   Also: the final left bound:"+coordinates[2]);
        System.out.println("We got bottomview:"+viewBottom+"   Also viewHeight:" +viewHeight+ "  Also leftProportion:"+downProp+"   Also: the final left bound:"+coordinates[3]);*/


        //Resize Bird Image
        int width = birdBitmap.getWidth();
        int height = birdBitmap.getHeight();
        int newWidth = Math.round(coordinates[1]-coordinates[0]);
        int newHeight = Math.round(coordinates[3]-coordinates[2]);
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBirdBitmap = Bitmap.createBitmap(birdBitmap, 0, 0, newWidth, newHeight, matrix, true);

        //Bitmap resizedBirdBitmap = Bitmap.createScaledBitmap(birdBitmap, newWidth, newHeight, true);

        Bitmap result = Bitmap.createBitmap(picBitmap.getWidth(), picBitmap.getHeight(), picBitmap.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(picBitmap, 0f, 0f, null);
        //canvas.drawBitmap(resizedBirdBitmap, coordinates[0], coordinates[2], null);
        canvas.drawBitmap(birdBitmap, null, new RectF(coordinates[0],coordinates[2],coordinates[1],coordinates[3]),null);

        System.out.println(picImage.getLeft() + ": " + picImage.getTop() + ": " + coordinates[0] + ": " + coordinates[2] + ": ");
        System.out.println(picBitmap.getWidth() + " is: " + picBitmap.getHeight());
        System.out.println(resizedBirdBitmap.getWidth() + " is: " +resizedBirdBitmap.getHeight());
        System.out.println(newWidth + " is: " +newHeight);
        System.out.println(birdBitmap.getWidth() + " is: " +birdBitmap.getHeight());
        return result;

    }
}
