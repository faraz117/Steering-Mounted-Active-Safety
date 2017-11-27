package pk.ejunkies.android.activeDriverSafety.connectivity;
import android.content.ClipData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/*
public class PostRequest{

    private Context context;

    private static final String TAG = "PostRequest";
    private static final String TAG_MESSAGE = "message";

    public static final String DOMAIN = "http://pocomo.local/sensor?";

    private static String getStringfromURL(String url){
        try {

            URL urli = new URL(url); // here is your URL path


            HttpURLConnection conn = (HttpURLConnection) urli.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {

                    sb.append(line+"\n");
                    Log.e(TAG,"sb: "+sb.toString());
                    break;
                }

                in.close();

                return sb.toString();

            } else {
                return new String("false : " + responseCode);
            }
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    public static String getLEDsandAppliances(String output, String status)
    {
        Log.e(TAG,"getLEDSandAppliances JSON: "+getStringfromURL(DOMAIN+"output="+output+"&status="+status));
        return  getStringfromURL(DOMAIN+"output="+output+"&status="+status);
    }

}
*/


public class PostRequest extends AsyncTask<String, Void, String> {

    private Context context;
    private String action="";
    private String output="";
   // public static final String DOMAIN = "http://pocomo.local/action?";
      public static final String DOMAIN = "http://pocomo.local/sensor?";
    private static final String TAG = "PostRequest";

    public PostRequest(Context context, String action, String output) {
        this.context = context;
        this.action = action;
        this.output = output;
    }

    protected void onPreExecute(){}

    protected String doInBackground(String... arg0) {

        try {

            URL url = new URL(DOMAIN+"output="+action+"&status="+output); // here is your URL path

//                JSONObject postDataParams = new JSONObject();
//                postDataParams.put("name", "abc");
//                postDataParams.put("email", "abc@gmail.com");
//                Log.e("params",postDataParams.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(getPostDataString(postDataParams));
//
//                writer.flush();
//                writer.close();
//                os.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in=new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line="";

                while((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            }
            else {
                return new String("false : "+responseCode);
            }
        }
        catch(Exception e){
            return "Exception: " + e.getMessage();
        }

    }

    @Override
    protected void onPostExecute(String result) {

        try
        {
            if (result!=null)
            {
                String s = result.replace(" ","");
                Log.e(TAG,"S: "+s);
                JSONObject jo = new JSONObject(s);

                //Log.e(TAG,"VALUE: " + data.getValue());


            }
        } catch (JSONException e)
        {
            Log.e(TAG, e.toString());
        }
    }


    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

}




