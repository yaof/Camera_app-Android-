package org.crazyit.codeview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Yaofei_Feng on 7/30/13.
 */
public class Login_Activity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
    }
    public void loginHandler(View view){

        EditText ID = (EditText) findViewById(R.id.login_id);
        if(ID.getText().toString() .equals("")){
            Toast toast = Toast.makeText(Login_Activity.this, "Please input the camera ID", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else{
            String parking = WebServiceU.login(ID.getText().toString());
            if(parking != null){
                CameraApp cur_camera = new CameraApp(Integer.parseInt(ID.getText().toString()), parking);
                Bundle data = new Bundle();
                data.putSerializable("camera", cur_camera);
                data.putBoolean("isDemo",false);
                Intent intent = new Intent(Login_Activity.this, Monitor_Activity.class);
                intent.putExtras(data);
                startActivity(intent);
                finish();
            }
            else{
                Toast toast = Toast.makeText(Login_Activity.this, "The camera ID is not register in our system", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }

    }

    public void logindemoHandler(View view){
        EditText ID = (EditText) findViewById(R.id.login_id);
        if(ID.getText().toString() .equals("")){
            Toast toast = Toast.makeText(Login_Activity.this, "Please input the camera ID", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else{
            String parking = WebServiceU_demo.login_demo(ID.getText().toString());
            if(parking != null){
                CameraApp cur_camera = new CameraApp(Integer.parseInt(ID.getText().toString()), parking);
                Bundle data = new Bundle();
                data.putSerializable("camera", cur_camera);
                data.putBoolean("isDemo",true);
                Intent intent = new Intent(Login_Activity.this, Monitor_Activity.class);
                intent.putExtras(data);
                startActivity(intent);
                finish();
            }
            else{
                Toast toast = Toast.makeText(Login_Activity.this, "The camera ID is not register in our system", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }
    }


    public void registerHandler(View view){

        EditText ID = (EditText) findViewById(R.id.register_id);
        EditText parking = (EditText) findViewById(R.id.register_parking);
        EditText num = (EditText) findViewById(R.id.register_number);
        if(ID.getText().toString().equals("") || parking.getText().toString().equals("") || num.getText().toString().equals("")){
            Toast toast = Toast.makeText(Login_Activity.this, "Please input all the information to register this camera", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else{
            boolean register_try = WebServiceU_demo.register_demo(ID.getText().toString(), parking.getText().toString(), Integer.valueOf(num.getText().toString()));
            if(register_try == true){
                Toast toast = Toast.makeText(Login_Activity.this, "Register success, please login with the camera ID"
                      , Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            else{
                Toast toast = Toast.makeText(Login_Activity.this, "Register failed, please contact our system" +
                        "administrator", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }
    }

    public void SetBackgroundHandler(View view){

        Intent intent = new Intent(Login_Activity.this, TakeSample_Activity.class);
        startActivity(intent);
        finish();

/*
        boolean res = false;
        res = WebServiceU.Send_status("Kelley", 100, 0);
        res = WebServiceU.Send_status("Kelley", 100, 1);
        res = WebServiceU.Send_status("Kelley", 100, 2);
        Toast toast = Toast.makeText(Login_Activity.this, "update" + res, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    */
    }
    public void onBackPressed() {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}