package org.crazyit.codeview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;
/**
 * Created by Yaofei_Feng on 7/17/13.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder mholder;
    public Camera camera = null;
    public CameraSurfaceView(Context context){
        super(context);
        mholder = getHolder();
        mholder.addCallback(this);
        mholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    public void surfaceCreated(SurfaceHolder holder){
        camera = Camera.open();
        try{
            camera.setPreviewDisplay(mholder);
            camera.setDisplayOrientation(90);
        }catch (Exception e){
            camera.release();
            camera = null;
        }

    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        Camera.Parameters params = camera.getParameters();

        params.setPictureSize(640,480);
        params.setPreviewSize( params.getSupportedPreviewSizes().get(0).width, params.getSupportedPreviewSizes().get(0).height);
        camera.setParameters(params);
        camera.startPreview();

    }
    public void surfaceDestroyed(SurfaceHolder holder){
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void capture(Camera.PictureCallback jpegHandler){
        camera.takePicture(null,null, jpegHandler);
    }


}
