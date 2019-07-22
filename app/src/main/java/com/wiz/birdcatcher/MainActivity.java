package com.wiz.birdcatcher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends Activity {
    Button selectButton, uploadButton;
    TextView view_status;
    ProgressDialog progress;
    String response;
    String realPath;
    static final String  UPLOAD_SERVER = "https://api.sightengine.com/1.0/check.json";
    static final String api_user = "1667316172";
    static final String api_secret = "QBbnvK6q49UEQiYeQj7y";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            }
        });

        uploadButton = (Button) findViewById(R.id.button2);
        uploadButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open image selector
                SendImage start_task = new SendImage();
                start_task.execute();
                view_status.setText("Try to upload Image");
                view_status.setTextColor(Color.GRAY);
            }
        });

    }
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if(resCode == Activity.RESULT_OK && data != null){
            // Check the SDK Version
            if (Build.VERSION.SDK_INT < 11)
                realPath = PathOfImage.PathAPI11(this, data.getData());
            else if (Build.VERSION.SDK_INT < 19)
                realPath = PathOfImage.Path_API18(this, data.getData());
            else
                realPath = PathOfImage.Path_API19(this, data.getData());
            view_status.setText("Image path: " + realPath + "\n\nYou can start the upload now");
            uploadButton.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }
    }

    public class SendImage extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Uploading....");
            progress.setMessage("Please wait until the process is finished");
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

//        @Override
//        protected void onPostExecute(Void result) {
//            if(response.contains("success"))
//            {
//                view_status.setTextColor(Color.parseColor("#21c627"));
//            }
//            view_status.setText(response);
//            uploadButton.getBackground().setColorFilter(0xffd6d7d7, PorterDuff.Mode.MULTIPLY);
//        }
    }

    public String POST_Data(String filepath) throws Exception {

      /*  Runtime runtime = Runtime.getRuntime();

        try {
            Process process = runtime.exec("curl -X POST 'https://api.sightengine.com/1.0/check.json' \\\n" +
                    "-F 'models=offensive' \\\n" +
                    "-F 'api_user="+api_user+"' \\\n" +
                    "-F 'api_secret="+api_secret+"' \\\n" +
                    "-F media="+filepath);

            int resultCode = process.waitFor();

            if (resultCode == 0) {
                // all is good
                String response = IOUtils.toString(process.getInputStream());
                Log.d("myTag", "is: " + response);

            } else {
                Log.d("myTag", "es: " + IOUtils.toString(process.getErrorStream()));
            }
        } catch (Throwable cause) {
            // process cause
            Log.d("myTag", "it didn't work");
            System.out.println(cause.getMessage());
        }*/


        HttpURLConnection connection;
        DataOutputStream outputStream;
        InputStream inputStream;
        String boundary =  "*****"+Long.toString(System.currentTimeMillis())+"*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        String[] q = filepath.split("/");
        int idx = q.length - 1;
        File file = new File(filepath);
        FileInputStream fileInputStream = new FileInputStream(file);
        URL url = new URL(UPLOAD_SERVER);


        connection = (HttpURLConnection) url.openConnection();
        Log.d("myTag", "0: connection made" );
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
        //connection.setRequestProperty("models","offensive");
        //connection.setRequestProperty("api_user", api_user);
        //connection.setRequestProperty("api_secret", api_secret);
        Log.d("myTag", "1: " + filepath );
       // connection.setRequestProperty("media", filepath);
        connection.setDoOutput(true);
        outputStream = new DataOutputStream(connection.getOutputStream());

/*        outputStream.writeBytes("--" + boundary + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "img_upload" + "\"; filename=\"" + q[idx] +"\"" + "\r\n");
        outputStream.writeBytes("Content-Type: image/jpeg" + "\r\n");
        outputStream.writeBytes("Content-Transfer-Encoding: binary" + "\r\n");
        outputStream.writeBytes("\r\n");*/

        outputStream.writeBytes("models=offensive"+"\r\n");
        outputStream.writeBytes("api_user="+api_user+"\r\n");
        outputStream.writeBytes("api_secret="+api_secret+"\r\n");
        outputStream.writeBytes("media=");

        //send file here
        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, 1048576);
        buffer = new byte[bufferSize];
        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        Log.d("myTag", "2: bytes and buffers made up");
        while(bytesRead > 0) {
            outputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, 1048576);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        //
        outputStream.writeBytes("\r\n");




        //outputStream.writeBytes("\r\n");

        //outputStream.writeBytes("--" + boundary + "--" + "\r\n");

        int status = connection.getResponseCode();
        Log.d("myTag", "3.3: status number: "+status);
        inputStream = connection.getInputStream();
        Log.d("myTag", "3.4");

        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            inputStream.close();
            connection.disconnect();
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
            Log.d("myTag", "is: " + response.toString());
            return response.toString();
        } else {
            throw new Exception("Non ok response returned");
        }

    }
}