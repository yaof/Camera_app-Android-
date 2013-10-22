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
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yaofei_Feng on 7/30/13.
 */
public class TakeSample_Activity extends Activity {
    private CameraSurfaceView cameraView;
    private ImageView imageResult;
    private FrameLayout frame;
    private boolean takePicture = false;
    public byte[] bytes_image ;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takesample);
        cameraView = new CameraSurfaceView(getApplicationContext());
        imageResult = new ImageView(getApplicationContext());
        frame = (FrameLayout) findViewById(R.id.imagefilter_frame);
        frame.addView(imageResult);
        frame.addView(cameraView);
        frame.bringChildToFront(cameraView);
    }

    public void imagefilter_cancelHandler(View view){
        Intent intent = new Intent(TakeSample_Activity.this, Login_Activity.class);
        startActivity(intent);
        finish();
    }

    public void imagefilter_saveHandler(View view){
        if(bytes_image == null){
            Toast toast = Toast.makeText(TakeSample_Activity.this, "Please take a picture first", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else{
            File pictureFile = getOutputMediaFile();
            try{
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(bytes_image);
                fos.close();
                Toast toast = Toast.makeText(TakeSample_Activity.this, "The picture has been saved", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }catch (FileNotFoundException e){

            }catch(IOException e){

            }
        }
    }
    public void imagefilter_takeHandler(View view){
        cameraView.camera.takePicture(null, null, jpegHandler);
    }
    private Camera.PictureCallback jpegHandler = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap image_r = RotateBitmap(image, 90);
            bytes_image = bitmapToByteArray(image_r);
            imageResult.setImageBitmap(image_r);
            frame.bringChildToFront(imageResult);
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
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "Background" + ".jpg");
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