package com.wiz.birdcatcher;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends Activity {
    Button selectButton, uploadButton, screenshotButton, cameraButton;
    TextView view_status;
    Bitmap myBitmap;
    Bitmap screenShot;
    Bitmap backUpImage;
    ImageView myImage;
    ImageView birdImage= null;
    ConstraintLayout cl;
    ProgressDialog progress;
    String response;
    String responseCheck;
    String realPath;
    Intent intent ;
    JSONObject jObj;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    static final String  UPLOAD_SERVER = "https://api.sightengine.com/1.0/check.json";
    static final String api_user = "1667316172";
    static final String api_secret = "QBbnvK6q49UEQiYeQj7y";
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    public  static final int RequestPermissionCode  = 1 ;

    protected void onCreate(Bundle savedInstanceState) {
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            System.out.println("Let's go");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity);

       // cl = (ConstraintLayout) findViewById(R.id.screenshot_layout);
        view_status = (TextView) findViewById(R.id.view_status);
        view_status.setText("Select your Image");

        selectButton = (Button) findViewById(R.id.button);
        selectButton.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        selectButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open image selector
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
                selectButton.getBackground().setColorFilter(0xffd6d7d7, PorterDuff.Mode.MULTIPLY);
                if (birdImage!=null){
                    birdImage.setVisibility(View.INVISIBLE);
                }
                if (myImage!=null){
                    myImage.setVisibility(View.VISIBLE);
                }

                view_status.setText("Try to upload the Image");
            }
        });

        uploadButton = (Button) findViewById(R.id.button2);
        uploadButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open image selector
                SendImage start_task = new SendImage();
                start_task.execute();
                view_status.setTextColor(Color.GRAY);

            }
        });

        screenshotButton = (Button) findViewById(R.id.button3);
        screenshotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    MediaStore
                        The Media provider contains meta data for all available media on both internal and external storage devices.
                    MediaStore.Images Contains meta data for all available images.
                    insertImage(ContentResolver cr, Bitmap source, String title, String description)
                        this will insert an image and create a thumbnail for it.
                */
                // Save the screenshot on device gallery
                MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        screenShot,
                        "Flip",
                        "Screenshot of Your Flipped Photo"
                );

                // Notify the user that screenshot taken.
                Toast.makeText(getApplicationContext(), "Screen Captured.",Toast.LENGTH_SHORT).show();
                view_status.setText("You've downloaded your flipped-photo");
                view_status.setTextColor(Color.GRAY);
            }
        });

        cameraButton =(Button) findViewById(R.id.button4);
        cameraButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);
            }
        });

        uploadButton.setEnabled(false);
        screenshotButton.setEnabled(false);

    }
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (reqCode == 7 && resCode == RESULT_OK) {
            myBitmap = (Bitmap) data.getExtras().get("data");
            System.out.println("Camera Progress");
            /*String timeStamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date());
            String imageFileName = "IMG_" + timeStamp + "_";*/
            //realPath=Util.saveBitmap(myBitmap, Environment.DIRECTORY_PICTURES, imageFileName);
            realPath = ImageSave.saveTempBitmap(myBitmap);
            System.out.println(myBitmap);
            System.out.println(Environment.DIRECTORY_PICTURES);
            System.out.println(realPath);
            try {
                ExifInterface exif = new ExifInterface(realPath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                }
                else if (orientation == 3) {
                    matrix.postRotate(180);
                }
                else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true); // rotating bitmap
            }
            catch (Exception e) {

            }
            view_status.setText("Image path: " + realPath + "\n\nYou can start the upload now");
            uploadButton.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
            uploadButton.setEnabled(true);
            screenshotButton.getBackground().setColorFilter(0xffd6d7d7, PorterDuff.Mode.MULTIPLY);
            screenshotButton.setEnabled(false);
            myImage = (ImageView) findViewById(R.id.imageView);
            myImage.setImageBitmap(myBitmap);
            birdImage = (ImageView) findViewById(R.id.birdView);
        }
        else if(resCode == Activity.RESULT_OK && data != null){
            // Check the SDK Version before calling PathOfImage generator
            if (Build.VERSION.SDK_INT < 11) {
                System.out.println("Select v1");
                realPath = PathOfImage.PathAPI11(this, data.getData());
            }
            else if (Build.VERSION.SDK_INT < 19) {
                realPath = PathOfImage.Path_API18(this, data.getData());
                System.out.println("Select v2");
            }
            else {
                realPath = PathOfImage.Path_API19(this, data.getData());
                System.out.println("Select v3");
            }
            System.out.println(realPath);
            view_status.setText("Image path: " + realPath + "\n\nYou can start the upload now");
            uploadButton.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
            uploadButton.setEnabled(true);
            screenshotButton.getBackground().setColorFilter(0xffd6d7d7, PorterDuff.Mode.MULTIPLY);
            screenshotButton.setEnabled(false);
            backUpImage = BitmapFactory.decodeFile(realPath);
            myBitmap = BitmapFactory.decodeFile(realPath);
            try {
                ExifInterface exif = new ExifInterface(realPath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                }
                else if (orientation == 3) {
                    matrix.postRotate(180);
                }
                else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true); // rotating bitmap
            }
            catch (Exception e) {

            }
            myImage = (ImageView) findViewById(R.id.imageView);
            myImage.setImageBitmap(myBitmap);
            birdImage = (ImageView) findViewById(R.id.birdView);
        }



    }

    public class SendImage extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Uploading....");
            progress.setMessage("Please wait while we analyze the picture");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                response = POST_Data(realPath);

                progress.dismiss();

            } catch (Exception e) {
                response = "Image was not uploaded!";
                progress.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(responseCheck.contains("success"))
            {

                view_status.setTextColor(Color.parseColor("#21c627"));

            }
            try {
                System.out.println("Find the flip");
                view_status.setText(findTheFlip(response));
                screenshotButton.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                screenshotButton.setEnabled(true);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            uploadButton.getBackground().setColorFilter(0xffd6d7d7, PorterDuff.Mode.MULTIPLY);
        }
    }

    public String POST_Data(String filepath) throws Exception {

        // Create an HTTP client to execute the request
        OkHttpClient client = new OkHttpClient();

        // Create a multipart request body. Add metadata and files as 'data parts'.
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("models", "offensive")
                .addFormDataPart("api_user", api_user)
                .addFormDataPart("api_secret", api_secret)
                .addFormDataPart("media", "file.jpg",
                        RequestBody.create(MediaType.parse("image/jpg"), new File(filepath)))
                .build();

        // Create a POST request to send the data to UPLOAD_URL
        Request request = new Request.Builder()
                .url(UPLOAD_SERVER)
                .post(requestBody)
                .build();

        // Execute the request and get the response from the server
        Response response = null;

        try {
            response = client.newCall(request).execute();
            //response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.d("myTag", "Exception");
            e.printStackTrace();
        }

        // Check the response to see if the upload succeeded
        if (response == null || !response.isSuccessful()) {
            Log.d("myTag", "Unable to upload to server.");
        } else {
            Log.d("myTag", "Upload was successful.");
        }
        responseCheck = response.toString();
        return response.body().string();
    }

    public String findTheFlip(String response) throws JSONException {
        String guess;
        Log.d("myTag", response);

        jObj = new JSONObject(response);

        if (response.contains("middlefinger")){
            String offensive;
            String prob = "";
            offensive = jObj.getString("offensive");
            jObj = new JSONObject(offensive);
            prob = jObj.getString("prob");

            Log.d("myTag", prob);
            randomBird();

            if(Float.parseFloat(prob)>=.50){
                String boxes = jObj.getString("boxes");
                JSONArray jArray = new JSONArray(boxes);
                guess = "Yep, there's a middle finger there better cover it up.";
                for(int i=0; i<jArray.length(); i++) {
                    jObj = jArray.getJSONObject(i);
                    float x1 = Float.parseFloat(jObj.getString("x1"));
                    float x2 = Float.parseFloat(jObj.getString("x2"));
                    float y1 = Float.parseFloat(jObj.getString("y1"));
                    float y2 = Float.parseFloat(jObj.getString("y2"));
                    screenShot = ReplaceFinger.replace(x1, x2, y1, y2, backUpImage, birdImage);
                    try {
                        ExifInterface exif = new ExifInterface(realPath);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        Log.d("EXIF", "Exif: " + orientation);
                        Matrix matrix = new Matrix();
                        if (orientation == 6) {
                            matrix.postRotate(90);
                        }
                        else if (orientation == 3) {
                            matrix.postRotate(180);
                        }
                        else if (orientation == 8) {
                            matrix.postRotate(270);
                        }
                        screenShot = Bitmap.createBitmap(screenShot, 0, 0, screenShot.getWidth(), screenShot.getHeight(), matrix, true); // rotating bitmap
                    }
                    catch (Exception e) {

                    }
                    myImage.setImageBitmap(screenShot);
                }

            }
            else{guess ="There might be a middle finger in there but we're not sure";}
        }
        else{
            guess = "Nope, there isn't a middle finger in this pic. Try again.";
        }
        return guess;
    }


    public void randomBird () {
        //Randomly decide which bird pic to use for censoring

        //Get number between 0 and 5
        Random random = new Random();
        int n = random.nextInt(6);

        //Put possible bird pics in an array
        int[] images = new int[6];
        images[0] = R.drawable.uglybird_0;
        images[1] = R.drawable.uglybird_1;
        images[2] = R.drawable.uglybird_2;
        images[3] = R.drawable.uglybird_3;
        images[4] = R.drawable.uglybird_4;
        images[5] = R.drawable.uglybird_5;

        birdImage.setImageResource(images[n]);
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

}