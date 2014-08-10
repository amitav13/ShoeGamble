package com.hhbrown.amitav.shoegamble;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;


public class GeneratedShoe extends Activity {

    static int times_clicked=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_shoe);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ShoeDetailsFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.generated_shoe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ShoeDetailsFragment extends Fragment {

        Intent i;
        String min_price,max_price,gender;
        ArrayList<HashMap<String, String>> shoesList;
        String shoesJsonStr = null;
        TextView shoe_title,shoe_price,shoe_description;
        WebView shoe_pic;
        Button try_again, buy;
        int totalShoes,shoe1,shoe2;

        public ShoeDetailsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_generated_shoe, container, false);

            i = getActivity().getIntent();
            min_price = i.getStringExtra("min_price");
            max_price = i.getStringExtra("max_price");
            gender = i.getStringExtra("gender");
            shoe_title = (TextView)rootView.findViewById(R.id.shoe_title);
            shoe_price = (TextView)rootView.findViewById(R.id.shoe_price);
            shoe_description = (TextView)rootView.findViewById(R.id.shoe_description);
            shoe_pic = (WebView)rootView.findViewById(R.id.shoe_image);
            try_again = (Button)rootView.findViewById(R.id.try_again);
            buy = (Button)rootView.findViewById(R.id.buy);
            FetchShoesTask task = new FetchShoesTask();
            task.execute(gender,min_price,max_price);
            try_again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshUI();
                }
            });
            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String,String> shoe;
                    if((times_clicked-1)==0){
                        shoe = shoesList.get(shoe1);

                    }else{
                        shoe = shoesList.get(shoe2);
                    }
                    String link = shoe.get("link");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(link));
                    startActivity(intent);
                }
            });
            return rootView;
        }
        public class FetchShoesTask extends AsyncTask<String , Void, Void > {

            private final String LOG_TAG = FetchShoesTask.class.getSimpleName();

            @Override
            protected Void doInBackground(String... params) {

                String gender = params[0];
                String min_price = params[1];
                String max_price = params[2];
                HttpsURLConnection urlConnection = null,dataUrlConnection = null;
                BufferedReader reader = null,reader2 = null;
                final String MEN_BASE_URL = "https://www.apitite.net/api/hhbrown/menminmax/json?";
                final String WOMEN_BASE_URL = "https://www.apitite.net/api/hhbrown/womenminmax/json?";


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
                    //Log.v(LOG_TAG, "Shoes JSON string: "+shoesJsonStr);


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
                    return null;
                }

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.d(LOG_TAG,"onPostExecute");
                buildShoesArrayFromJSON();
                refreshUI();

            }


        }
        public void buildShoesArrayFromJSON() {
            shoesList = new ArrayList<HashMap<String, String>>();
            try {
                JSONArray shoes = new JSONArray(shoesJsonStr);
                for(int i = 0;i<shoes.length();i++){
                    JSONObject shoe = shoes.getJSONObject(i);

                    HashMap<String, String> shoeHashmap = new HashMap<String, String>();
                    shoeHashmap.put("title",shoe.getString("title"));
                    shoeHashmap.put("price",shoe.getString("price"));
                    shoeHashmap.put("image_link",shoe.getString("image_link"));
                    shoeHashmap.put("description",shoe.getString("description"));
                    shoeHashmap.put("link",shoe.getString("link"));
                    Log.d("a", "Shoe title:" + shoe.getString("title"));
                    shoesList.add(shoeHashmap);
                }
                totalShoes = shoesList.size();
                Random r = new Random();
                shoe1 = r.nextInt(totalShoes);
                shoe2 = r.nextInt(totalShoes);

            }catch (JSONException e){
                Log.e("JSON","JSON parsing exception");
            }

        }
        public void refreshUI(){

            Log.d("a","refreshUI called, times clicked="+times_clicked);
            //Log.d("a","Total shoes:"+totalShoes);
            if(times_clicked==0) {
                HashMap<String, String> shoe = shoesList.get(shoe1);
                String image_url=shoe.get("image_link").toString();
                //getImageTask getImage = new getImageTask();
                //getImage.execute(image_url);
                //Log.d("a","Retrieved shoe title:"+shoe.get("title"));
                shoe_pic.loadUrl(image_url);
                shoe_title.setText(shoe.get("title").toString());
                shoe_description.setText(shoe.get("description").toString());
                double originalPrice = Double.parseDouble(shoe.get("price"));
                double discountPrice = 0.8 * originalPrice;
                discountPrice = Math.round(discountPrice * 100.0) / 100.0;
                shoe_price.setText("Get these $" + originalPrice + " shoes at $" + discountPrice + " (20% off!)");
                times_clicked++;
            }
            else if(times_clicked==1){
                HashMap<String, String> shoe = shoesList.get(shoe2);
                //Log.d("a","Retrieved shoe title:"+shoe.get("title"));
                String image_url=shoe.get("image_link").toString();
                //getImageTask getImage = new getImageTask();
                //getImage.execute(image_url);
                shoe_pic.loadUrl(image_url);
                shoe_title.setText(shoe.get("title").toString());
                shoe_description.setText(shoe.get("description").toString());
                double originalPrice = Double.parseDouble(shoe.get("price"));
                double discountPrice = 0.9 * originalPrice;
                discountPrice = Math.round(discountPrice * 100.0) / 100.0;
                shoe_price.setText("Get these $" + originalPrice + " shoes at $" + discountPrice + " (10% off!)");
                /*
                Fragment frg = new ShoeDetailsFragment();
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
                */
                times_clicked++;
            }
            else {
                try_again.setText("Try again in a day!");
            }

        }
        public class getImageTask extends AsyncTask<String,Void,Void>{
            @Override
            protected Void doInBackground(String... params) {
                try{
                    URL url = new URL(params[0]);
                    //try this url = "http://0.tqn.com/d/webclipart/1/0/5/l/4/floral-icon-5.jpg"
                    HttpGet httpRequest = null;

                    httpRequest = new HttpGet(url.toURI());

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = (HttpResponse) httpclient
                            .execute(httpRequest);

                    HttpEntity entity = response.getEntity();
                    BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
                    InputStream input = b_entity.getContent();

                    Bitmap bitmap = BitmapFactory.decodeStream(input);

                    //shoe_pic.setImageBitmap(bitmap);
                }catch(Exception e){

                }
                return null;
            }
        }

    }
}
