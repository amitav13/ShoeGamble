package com.hhbrown.amitav.shoegamble;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class GeneratedShoe extends Activity {

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
            return rootView;
        }
        public class FetchShoesTask extends AsyncTask<Void , Void, Void>{

            private final String LOG_TAG = FetchShoesTask.class.getSimpleName();

            @Override
            protected Void doInBackground(Void... params) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                final String MEN_BASE_URL = "https://www.apitite.net/api/hhbrown/menminmax/json?";
                final String WOMEN_BASE_URL = "https://www.apitite.net/api/hhbrown/menminmax/json?";

                String shoesJsonStr = null;
                try {
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
                    URL url= new URL(builtUri.toString());


                    Log.v(LOG_TAG,"Built uri: "+builtUri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
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
                    shoesJsonStr = buffer.toString();
                    Log.v(LOG_TAG, "Shoes JSON string: "+shoesJsonStr);



                }catch (IOException e){
                    Log.e(LOG_TAG,"Error", e);
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
                }


            return null;
            }
        }

    }
}
