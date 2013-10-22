package org.crazyit.codeview;

/**
 * Created by Yaofei_Feng on 7/23/13.
 */
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class WebServiceU_demo {
    final static String SERVICE_NS ="http://br.yaofei.org/";
    final static String SERVICE_URL ="http://50.131.113.98//Test/executeService";
    public static String login_demo(String id){
        String methodName = "login_demo";
        HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        soapObject.addProperty("arg0", id);
        envelope.bodyOut = soapObject;
        try {
            ht.call(null,envelope);
            if(envelope.getResponse() != null){
                SoapObject result = (SoapObject) envelope.bodyIn;
                StringBuffer s_b = new StringBuffer();
                s_b.append(result.getProperty(0));
                return s_b.toString();
            }
        }   catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean register_demo(String id, String parking, int num){
        String methodName = "register_demo";
        HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        soapObject.addProperty("arg0", id);
        soapObject.addProperty("arg1", parking);
        soapObject.addProperty("arg2", num);
        envelope.bodyOut = soapObject;
        try {
            ht.call(null,envelope);
            if(envelope.getResponse() != null){
                SoapObject result = (SoapObject) envelope.bodyIn;
                StringBuffer s_b = new StringBuffer();
                s_b.append(result.getProperty(0));
                if(s_b.toString().equals("true")){
                    return true;
                }
                else{
                    return false;
                }
            }
        }   catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static  boolean Send_status_demo(String name, int id, int index){
        String methodName = "update_demo";
        HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        soapObject.addProperty("arg0", name);
        soapObject.addProperty("arg1", id);
        soapObject.addProperty("arg2", index);
        envelope.bodyOut = soapObject;
        try {
            ht.call(null,envelope);
            if(envelope.getResponse() != null){
                SoapObject result = (SoapObject) envelope.bodyIn;
                StringBuffer s_b = new StringBuffer();
                s_b.append(result.getProperty(0));
                if(s_b.toString().equals("true")){
                    return true;
                }
                else{
                    return false;
                }
            }
        }   catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  false;
    }
}