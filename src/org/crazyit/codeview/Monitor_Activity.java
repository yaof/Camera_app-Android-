package org.crazyit.codeview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yaofei_Feng on 7/30/13.
 */
public class Monitor_Activity extends Activity {
    CameraApp c;
    private CameraSurfaceView cameraView;
    private ImageView imageResult;
    private FrameLayout frame;
    private String config_name = "config.txt";
    private String background_name = "Background.jpg";
    private boolean isDemo;
    Timer t;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring);
        cameraView = new CameraSurfaceView(getApplicationContext());
        imageResult = new ImageView(getApplicationContext());
        frame = (FrameLayout) findViewById(R.id.monitor_frame);
        frame.addView(imageResult);
        frame.addView(cameraView);
        frame.bringChildToFront(cameraView);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        c = (CameraApp) data.getSerializable("camera");
        isDemo = data.getBoolean("isDemo");
        File sdCardDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraApp");
        c.set_sensor(sdCardDir.getPath() + File.separator + config_name);
        c.set_background(sdCardDir.getPath() + File.separator + background_name);
        c.set_car_background();
    }
    public void monitor_cancelHandler(View view){
        if(t != null){
            t.cancel();
        }
        Intent intent = new Intent(Monitor_Activity.this, Login_Activity.class);
        startActivity(intent);
        finish();
    }
    public void monitor_infoHandler(View view){
        Bundle data = new Bundle();
        data.putSerializable("camera", c);
        data.putBoolean("isDemo", isDemo);
        Intent intent = new Intent(Monitor_Activity.this, Info_Dialog.class);
        intent.putExtras(data);
        startActivity(intent);
    }
    public void monitor_startHandler(View view){
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0x1122){

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
                m.what = 0x1122;

                handler.sendMessage(m);

            }
        }, 0 , 10000);
    }
    private Camera.PictureCallback jpegHandler = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap image_r = RotateBitmap(image, 90);
            imageResult.setImageBitmap(image_r);
            frame.bringChildToFront(imageResult);

            File pictureFile = getOutputMediaFile();
            try{
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(bitmapToByteArray(image_r));
                fos.close();
            }catch (FileNotFoundException e){

            }catch(IOException e){

            }
            if(c.cur_bitmap == null){
                c.update(image_r, image_r);
                c.cur_bitmap = image_r;
            }
            else{
                c.update(c.cur_bitmap, image_r);
                c.cur_bitmap = image_r;
            }
            for(int i = 0; i < c.num; i++ ){
                if(c.cur_status.get(i) != c.status.get(i)){
                    if(isDemo == false){
                        WebServiceU.Send_status(c.get_lot(),c.get_id(), i);
                    }
                    else{
                        WebServiceU_demo.Send_status_demo(c.get_lot(),c.get_id(),i);
                    }
                    c.status.set(i, c.cur_status.get(i));
                }
            }
            cameraView.camera.startPreview();
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
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    public static byte[] bitmapToByteArray(Bitmap bm) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}