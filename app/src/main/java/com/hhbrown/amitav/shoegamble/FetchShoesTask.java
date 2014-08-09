package com.hhbrown.amitav.shoegamble;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Amitav on 8/9/2014.
 */
public class FetchShoesTask extends AsyncTask<String , Void, String> {

    private final String LOG_TAG = FetchShoesTask.class.getSimpleName();

    @Override
    protected String doInBackground(String... params) {

        String gender = params[0];
        String min_price = params[1];
        String max_price = params[2];
        HttpsURLConnection urlConnection = null,dataUrlConnection = null;
        BufferedReader reader = null,reader2 = null;
        final String MEN_BASE_URL = "https://www.apitite.net/api/hhbrown/menminmax/json?";
        final String WOMEN_BASE_URL = "https://www.apitite.net/api/hhbrown/menminmax/json?";

        String shoesJsonStr = null;
        String authJsonStr = null;
        try {

            URL authUrl = new URL("https://www.apitite.net/api/hhbrown/oauth/access_token");

            //Log.v(LOG_TAG,"Built uri: "+builtUri.toString());

            urlConnection = (HttpsURLConnection) authUrl.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            String postParameters = "grant_type=client_credentials&client_id=53e61f45200934020000001c&client_secret=fdqtdCcqywbR_JfGdd6BYtO5";
            urlConnection.setFixedLengthStreamingMode(
                    postParameters.getBytes().length);
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(postParameters);
            out.close();
                    /*
                    urlConnection.addRequestProperty("grant_type", "client_credentials");
                    urlConnection.addRequestProperty("client_id", "53e61f45200934020000001c");
                    urlConnection.addRequestProperty("client_secret", "fdqtdCcqywbR_JfGdd6BYtO5");
                    */

            urlConnection.connect();
            //authJsonStr = urlConnection.getResponseMessage();

            // Read the input stream into a String
            InputStream inputStream;
            int status = urlConnection.getResponseCode();

            if(status >= HttpStatus.SC_BAD_REQUEST)
                inputStream = urlConnection.getErrorStream();
            else
                inputStream = urlConnection.getInputStream();

            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            authJsonStr = buffer.toString();
            JSONObject authJson = new JSONObject(authJsonStr);
            String access_token_base64 = authJson.getString("access_token_base64");


            //ACCESS TOKEN RECEIVED
            Log.v(LOG_TAG, "Access token base 64: " + access_token_base64);
            Uri builtUri= null;
            if (gender.equals("Male")){
                builtUri = Uri.parse(MEN_BASE_URL).buildUpon()
                        .appendQueryParameter("maxprice",max_price)
                        .appendQueryParameter("minprice",min_price)
                        .build();
            }else if (gender.equals("Female")){
                builtUri = Uri.parse(WOMEN_BASE_URL).buildUpon()
                        .appendQueryParameter("maxprice",max_price)
                        .appendQueryParameter("minprice",min_price)
                        .build();
            }
            URL dataUrl= new URL(builtUri.toString());
            Log.v(LOG_TAG,"Data url: "+dataUrl);
            final String bearerAuth = "Bearer " + Base64.encodeToString(access_token_base64.getBytes(), Base64.NO_WRAP);
            dataUrlConnection= (HttpsURLConnection) dataUrl.openConnection();
            dataUrlConnection.setRequestMethod("GET");
            dataUrlConnection.setRequestProperty("Authorization","Bearer "+access_token_base64);
            //dataUrlConnection.setRequestProperty("Authorization", bearerAuth);

            dataUrlConnection.connect();
            InputStream inputStream2 = dataUrlConnection.getInputStream();
            StringBuffer buffer2 = new StringBuffer();
            if (inputStream2 == null) {
                // Nothing to do.
                Log.d(LOG_TAG,"Input stream is null");
                return null;
            }
            reader2 = new BufferedReader(new InputStreamReader(inputStream2));

            String line2;
            while ((line2 = reader2.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer2.append(line2 + "\n");
            }

            if (buffer2.length() == 0) {
                // Stream was empty.  No point in parsing.
                Log.d(LOG_TAG,"Stream was empty, no point in parsing");
                return null;
            }
            shoesJsonStr = buffer2.toString();
            Log.v(LOG_TAG, "Shoes JSON string: "+shoesJsonStr);


        }catch (IOException e){
            Log.e(LOG_TAG,"Error", e);
        }
        catch (JSONException e){
            Log.e(LOG_TAG,"JSON error ",e);
        }
        finally
        {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG , "Error closing stream", e);
                }
            }
            if (dataUrlConnection!= null) {
                dataUrlConnection.disconnect();
            }
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG , "Error closing stream", e);
                }
            }
            return shoesJsonStr;
        }

    }
}
