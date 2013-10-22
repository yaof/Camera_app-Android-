package org.crazyit.codeview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Yaofei_Feng on 7/30/13.
 */
public class Info_Dialog extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_dialog);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        CameraApp c = (CameraApp) data.getSerializable("camera");
        boolean isDemo = data.getBoolean("isDemo");
        TextView tv_id = (TextView)findViewById(R.id.info_id);
        TextView tv_parking = (TextView)findViewById(R.id.info_parking);
        TextView tv_num = (TextView)findViewById(R.id.info_num);
        tv_id.setText("The camera ID is " + String.valueOf(c.get_id()));
        tv_parking.setText("This camera is belongs to " + c.get_lot());
        tv_num.setText("This camera is monitoring "+ String.valueOf(c.num) + " parking space");
    }
    public void confirmHandler(View view){
        finish();
    }
}