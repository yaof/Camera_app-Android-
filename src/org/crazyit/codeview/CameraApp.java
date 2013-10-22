package org.crazyit.codeview;

/**
 * Created by Yaofei_Feng on 7/23/13.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class CameraApp implements Serializable {
    /*Class Attribute*/

    private int Camera_id;
    private String belongs_to_lot;
    private int sensor_size;//pixel size of the each virtual sensor

    public ArrayList<int[]> sensor_location;
    public ArrayList<int[][]> background;
    public ArrayList<int[][]> car_background;
    public Bitmap cur_bitmap;
    public int num;
    public ArrayList<Boolean> status;  //status in the system
    public ArrayList<Boolean> cur_status; // status after updating
    /*Global Variables*/

    private int min_change_thres = 30; //threshold to determine pixel level change
    private double partchange_thres = 0.25; //threshold to the part change
    private double entirechange_thres = 0.75; //threshold to the entire change
    private double dark_rate = 0.95;//unknown variable
    private double light_rate = 1.05;
    private double grey_threshold = 0.95;
    //private double var_sec_thres = 100; //unknown variable

    CameraApp(int id, String belongs){
        this.Camera_id = id;
        this.belongs_to_lot = belongs;
        this.num = 0;
        this.status = new ArrayList<Boolean>();
        this.cur_status = new ArrayList<Boolean>();
        this.sensor_location = new ArrayList<int[]>();
        this.background = new ArrayList<int[][]>();
        this.car_background = new ArrayList<int[][]>();
        this.cur_bitmap = null;
    }

    public int get_id(){
        return this.Camera_id;
    }

    public String get_lot(){
        return this.belongs_to_lot;
    }

    /*Initial the pixel location of each virtual sensor
     *Input: the file path of configuration file
     */
    public void set_sensor(String txt_file){
        File file = new File(txt_file);
        if(file.exists()){
            try{

                BufferedReader input = new BufferedReader (new FileReader(file));
                String text;
                text = input.readLine();
                this.sensor_size = Integer.parseInt(text);
                while((text = input.readLine()) != null){
                    int index=0;
                    int[] tmp = new int[2];
                    for(int i = 0; i<text.length(); i++){
                        if(text.charAt(i)==' '){
                            index = i;
                            break;
                        }
                    }
                    tmp[0]=Integer.parseInt(text.substring(0,index));
                    tmp[1]=Integer.parseInt(text.substring(index+1, text.length()));
                    sensor_location.add(tmp);
                    status.add(true);
                    cur_status.add(true);
                }
                input.close();
            }
            catch(IOException ioException){
                System.err.println("File Error!");
            }
        }
        this.num = this.sensor_location.size(); //update the number of parking  space monitored by the camera
    }
    public void set_background(String path){
        try{
            FileInputStream fis = new FileInputStream(path);
            Bitmap data  = BitmapFactory.decodeStream(fis);
            this.background = retrieve_info(data);
        }catch(FileNotFoundException e){

        }

    }
    public void set_car_background(){
        for(int i = 0; i < this.num; i++){
            int [][] current_sensor = new int[sensor_size][sensor_size];
            for(int j = 0; j < sensor_size; j++){
                for(int k = 0; k < sensor_size; k++){
                    current_sensor[j][k] = 0;
                }
            }
            car_background.add(current_sensor);
        }
    }
    public void update(Bitmap data_cur, Bitmap data_next){
        ArrayList<int[][]> fst = retrieve_info(data_cur);
        ArrayList<int[][]> sec = retrieve_info(data_next);
        ArrayList<int[][]> current_background = new ArrayList<int[][]>();


        for(int i = 0; i < this.num; i++){
            //current_background.add(background.get(i));

            if(status.get(i) == true){
                current_background.add(background.get(i));
            }
            else{
                current_background.add(car_background.get(i));
            }

        }

        ArrayList<Integer> change_type = change_detection(compare(current_background,fst), grey_rate(current_background,fst));
        ArrayList<Integer> change_type_2 = change_detection(compare(this.background,fst), grey_rate(this.background,fst));
        ArrayList<Integer> change_type_next = change_detection(compare(fst,sec), grey_rate(fst,sec));
        for(int i = 0; i < this.num; i++){
            status_update(change_type.get(i),change_type_2.get(i),change_type_next.get(i),i,fst.get(i));
        }

    }


    private int[][] get_pixel(Bitmap data){
        int[][] result = null;
        int height = data.getHeight();
        int width = data.getWidth();
        //int tmp = 0;
        result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                result[i][j] = data.getPixel(i, j) & 0xFFFFFF;
                //result[i][j] = (((tmp& 0xff0000 ) >> 16) + ((tmp & 0xff00 ) >> 8) +(tmp & 0xff ))/3;
                //result[i][j] = (tmp& 0xff0000 ) >> 16;
            }
        }
        return result;
    }
    private ArrayList<int[][]> retrieve_info(Bitmap data){
        ArrayList<int[][]> result = new ArrayList<int[][]>();
        int[][] pixel_value = get_pixel(data);
        for(int i = 0; i < this.num; i++){
            int[][] current_sensor = new int[sensor_size][sensor_size];
            for(int j = 0; j < sensor_size; j++){
                for( int k = 0; k < sensor_size; k++){
                    current_sensor[j][k] = pixel_value[j+sensor_location.get(i)[0]][k+sensor_location.get(i)[1]];
                }
            }
            result.add(current_sensor);
        }

        return result;
    }

    private ArrayList<int[][]> compare(ArrayList<int[][]> fst, ArrayList<int[][]> sec){
        ArrayList<int[][]> result= new ArrayList<int[][]>();

        for(int i = 0; i < this.num; i++){
            int[][] tmp1 = fst.get(i).clone();
            int[][] tmp2 = sec.get(i).clone();
            int[][] tmp3 = new int[tmp1.length][tmp1[0].length];
            for(int j = 0; j < tmp1.length; j++){
                for(int k = 0; k < tmp1[0].length; k++){
                    int cur = 0;
                    int a = (tmp1[j][k] & 0xff0000) >> 16;
                    int b = (tmp1[j][k] & 0xff00) >> 8;
                    int c = tmp1[j][k] & 0xff;
                    int d = (tmp2[j][k] & 0xff0000) >> 16;
                    int e = (tmp2[j][k] & 0xff00) >> 8;
                    int f = tmp2[j][k] & 0xff;
                    cur = Math.abs(a - d);
                    cur = cur + Math.abs(b - e);
                    cur = cur + Math.abs(c  - f);
                    tmp3[j][k] = cur;
                }
            }
            result.add(tmp3);
        }
        return result;
    }
    private ArrayList<double[][]> grey_rate(ArrayList<int[][]> fst, ArrayList<int[][]> sec){
        ArrayList<double[][]> result= new ArrayList<double[][]>();
        for(int i = 0; i < this.num; i++){
            int[][] tmp1 = fst.get(i).clone();
            int[][] tmp2 = sec.get(i).clone();
            double[][] tmp3 = new double[tmp1.length][tmp1[0].length];
            for(int j = 0; j < tmp1.length; j++){
                for(int k = 0; k < tmp1[0].length; k++){
                    int a = (tmp1[j][k] & 0xff0000) >> 16;
                    int b = (tmp1[j][k] & 0xff00) >> 8;
                    int c = tmp1[j][k] & 0xff;
                    int d = (tmp2[j][k] & 0xff0000) >> 16;
                    int e = (tmp2[j][k] & 0xff00) >> 8;
                    int f = tmp2[j][k] & 0xff;
                    tmp3[j][k] = (a*0.2989+b*0.5870+c*0.1440)/(d*0.2989+e*0.5870+f*0.1440);
                }
            }
            result.add(tmp3);
        }
        return result;
    }
    /*
    0 means no change
    1 means part change
    2 means huge change but all changes are from light to dark
    3 means huge change but all changes are from dark to light
    4 means huge change with random grey rate
     */


    private ArrayList<Integer> change_detection(ArrayList<int[][]> difference, ArrayList<double[][]> grey_rate){
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(int i = 0; i < difference.size(); i++){
            int[][] current = difference.get(i);
            double[][] current_grey = grey_rate.get(i);
            int total_size = sensor_size*sensor_size;
            int count = 0;
            int dark_change = 0;
            int light_change = 0;
            for(int j = 0; j < current.length; j++){
                for(int k = 0; k < current[0].length; k++){
                    if(Math.abs(current[j][k]) > min_change_thres){
                        count++;
                        if(current_grey[j][k] > dark_rate){
                            dark_change++;
                        }
                        if(current_grey[j][k] < light_rate){
                            light_change++;
                        }
                    }
                }
            }
            if(count < partchange_thres*total_size){
                result.add(0);
            }
            else if(count >= partchange_thres*total_size && count < entirechange_thres*total_size){
                result.add(1);
            }
            else{
                if(dark_change/count > grey_threshold){
                    result.add(2);
                }
                else if(light_change/count > grey_threshold){
                    result.add(3);
                }
                else{
                    result.add(4);
                }
            }

        }

        return result;
    }


    private void status_update(int c_b, int n_b, int c_n, int index, int[][] current_pixel){
        if(status.get(index) == true){
            if(c_b == 0){
                background.set(index, current_pixel);
                return;
            }
            if(c_b == 1||c_b == 2){
                if(c_n == 0){
                    background.set(index, current_pixel);
                }
                return;

            }
            if(c_b == 3||c_b == 4){
                if(c_n == 0){
                    cur_status.set(index, false);
                    car_background.set(index, current_pixel);
                }
                return;
            }

        }
        else{
            if(c_b == 0){
                car_background.set(index, current_pixel);
                return;
            }
            if(c_b == 1){
                if(c_n == 0){
                    car_background.set(index, current_pixel);
                }
                return;
            }
            if(c_b == 4|| c_b == 3||c_b == 2){
                if(c_n == 0){
                    if(n_b == 0 || n_b == 1 || n_b == 2 || n_b == 3){
                        cur_status.set(index, true);
                        background.set(index, current_pixel);
                    }
                    else{
                        car_background.set(index, current_pixel);
                    }
                }
                return;
            }
        }
    }
}

