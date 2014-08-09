package com.hhbrown.amitav.shoegamble;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new UserdataFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public static class UserdataFragment extends Fragment {
        private Spinner min_price;
        private Spinner max_price;
        private Spinner gender;
        private Button lucky;
        Intent i;

        public UserdataFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            min_price=(Spinner)rootView.findViewById(R.id.min_price_spinner);
            max_price=(Spinner)rootView.findViewById(R.id.max_price_spinner);
            gender=(Spinner)rootView.findViewById(R.id.gender_spinner);
            lucky=(Button)rootView.findViewById(R.id.lucky_button);
            i = new Intent(getActivity(),GeneratedShoe.class);
            ArrayAdapter<CharSequence> minPriceAdapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.min_prices, android.R.layout.simple_spinner_item);
            ArrayAdapter<CharSequence> maxPriceAdapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.max_prices,android.R.layout.simple_spinner_item);
            ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.gender,android.R.layout.simple_spinner_item);
            minPriceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            maxPriceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            min_price.setAdapter(minPriceAdapter);
            max_price.setAdapter(maxPriceAdapter);
            gender.setAdapter(genderAdapter);
            min_price.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    i.putExtra("min_price",parent.getItemAtPosition(position).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            max_price.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    i.putExtra("max_price",parent.getItemAtPosition(position).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    i.putExtra("gender",parent.getItemAtPosition(position).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            lucky.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(i);
                }
            });
            return rootView;
        }

    }
}
