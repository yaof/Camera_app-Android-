package org.crazyit.codeview;

/**
 * Created by Yaofei_Feng on 7/23/13.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yaofei_Feng on 7/20/13.
 */
public class CaptureActivity extends Activity {
    CameraApp c;
    private CameraSurfaceView cameraView;
    private ImageView imageResult;
    private FrameLayout framenew;
    String config_name = "config.txt";
    Timer t;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        TextView ID = (TextView) findViewById(R.id.show_id);
        TextView lot_name = (TextView) findViewById(R.id.show_lot);
        TextView index = (TextView) findViewById(R.id.show_index);
        //TextView info = (TextView) findViewById(R.id.show_info);
        cameraView = new CameraSurfaceView(getApplicationContext());
        imageResult = new ImageView(getApplicationContext());
        imageResult.setBackgroundColor(Color.GRAY);
        framenew = (FrameLayout) findViewById(R.id.frameLayout);
        framenew.addView(imageResult);
        framenew.addView(cameraView);
        imageResult.setImageBitmap(null);
        framenew.bringChildToFront(cameraView);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        c = (CameraApp) data.getSerializable("camera");
        File sdCardDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraApp");

        c.set_sensor(sdCardDir.getPath() + File.separator + config_name);
        c.set_car_background();
        //info.setText(String.valueOf(c.sensor_location.get(0)[1]));
        ID.setText("ID is: " + c.get_id());
        lot_name.setText("Belongs to parking lot: " + c.get_lot());






    }

    public void goHandler(View view){

        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0x112222){
                    //TextView info = (TextView) findViewById(R.id.show_info);
                    //info.setText("Updated at: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
                    cameraView.camera.takePicture(null, null, jpegHandler);
                }
                super.handleMessage(msg);
            }

        };
        t = new Timer();

        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Message m = new Message();
                m.what = 0x112222;

                handler.sendMessage(m);

            }
        }, 0 , 10000);

        //cameraView.camera.takePicture(null,null,jpegHandler);
    }

    private Camera.PictureCallback jpegHandler = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            TextView info = (TextView) findViewById(R.id.show_info);

            File pictureFile = getOutputMediaFile();
            try{
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(bytes);
                fos.close();
            }catch (FileNotFoundException e){

            }catch(IOException e){

            }


            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);


            if(c.cur_bitmap == null){

                c.update(image, image);
                c.cur_bitmap = image;
            }
            else{
                c.update(c.cur_bitmap, image);
                c.cur_bitmap = image;
            }

            imageResult.setImageBitmap(image);
            framenew.bringChildToFront(imageResult);
            //int tmp = image.getPixel(100,100) & 0xFFFFFF;
            //info.setText("Pixel value is " + String.valueOf((tmp& 0xff0000 ) >> 16));
            info.setText("Updated at: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
            cameraView.camera.startPreview();
/*
            boolean sent = WebServiceU.Send_status(c.get_id(), c.status);

            if(sent == true){
                info.setText("Updated at: " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
            }
            else{
                info.setText("Failed to update");
            }

            */

        }
    };

    public File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraApp");
        if(!mediaStorageDir.exists()){
            if(! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    public void finishHandler(View view){
        t.cancel();
        Intent intent = new Intent(CaptureActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
    public void continueHandler(View view){
        imageResult.setImageBitmap(null);
        cameraView.camera.startPreview();
        framenew.bringChildToFront(cameraView);
    }
}

