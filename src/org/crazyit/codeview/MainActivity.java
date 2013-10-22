package org.crazyit.codeview;

/**
 * Created by Yaofei_Feng on 7/23/13.
 */
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void startHandler(View view){
        EditText ID = (EditText) findViewById(R.id.editID);
        EditText Parking_Lot = (EditText) findViewById(R.id.editLot);
        EditText Group_Index = (EditText) findViewById(R.id.editIndex);
        CameraApp cur_camera = new CameraApp(Integer.parseInt(ID.getText().toString()), Parking_Lot.getText().toString());
        Bundle data = new Bundle();
        data.putSerializable("camera", cur_camera);
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        intent.putExtras(data);
        startActivity(intent);
        finish();
    }
    public void releaseHandler(View view){

    }



}
