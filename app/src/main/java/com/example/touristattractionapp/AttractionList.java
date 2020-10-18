package com.example.touristattractionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AttractionList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Properties
    private String TAG = "ATTRACTIONS-LIST";
    private ListView listView;
    private ArrayList<Attraction> attractions = new ArrayList<Attraction>();
    private ArrayList<String> mTitle = new ArrayList<String>();
    private ArrayList<String> mDescription = new ArrayList<String>();
    private ArrayList<Integer> images = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);

        // Load and convert JSON with the attractions information
        String fileData = this.loadDataFromFile("attraction.json");
        if (fileData == null) {
            Log.d(TAG, "Error loading file");
            return;
        }

        JSONArray jsonData = this.convertToJSON(fileData);
        if (jsonData == null) {
            Log.d(TAG, "Error converting file string data to JSON");
            return;
        }

        this.parseJSONData(jsonData);
        if (this.attractions.size() == 0) {
            Log.d(TAG, "Failed to load all the words from the JSON file");
            return;
        }

        // Show title, description and image in list view
        for (int i = 0; i < attractions.size(); i++) {
            Attraction currentAttraction = this.attractions.get(i);
            String currentTitle = currentAttraction.getName();
            String currentDescription = currentAttraction.getDescription();
            String currentImage = currentAttraction.getImage();
            this.mTitle.add(currentTitle);
            this.mDescription.add(currentDescription);
            int resID = getResources().getIdentifier(currentImage, "drawable", getPackageName());
            this.images.add(resID);
        }

        // Navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();
        NavigationView navigationView = (NavigationView)
                findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        }

        // Load the list view
        listView = findViewById(R.id.attractionList);
        MyAdapter adapter = new MyAdapter(this, mTitle, mDescription, images);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                for (int i = 0; i < attractions.size(); i++) {
                    Attraction currentAttraction = attractions.get(i);
                    String currentTitle = currentAttraction.getName();
                    if (currentTitle.equals(o)) {
                        String description = currentAttraction.getDescription();
                        String address = currentAttraction.getAddress();
                        String phone = currentAttraction.getPhone();
                        String website = currentAttraction.getWebsite();
                        double pricing = currentAttraction.getPricing();
                        boolean favorite = currentAttraction.isFavorite();
                        String image = currentAttraction.getImage();
                        Attraction transferAttraction = new Attraction(currentTitle, address, phone, website, description, pricing, favorite, image);
                        Intent intent = new Intent(AttractionList.this, AttractionDetails.class);
                        intent.putExtra("AttractionObj", transferAttraction);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    // Go to the wish list screen
    public void GoToWishList(View view) {
        Intent intent = new Intent(AttractionList.this, WishList.class);
        startActivity(intent);
    }

    // Navigation item selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        return false;
    }

    // Load JSON data
    public String loadDataFromFile(String filename) {
        String jsonString;
        try {
            InputStream fileData = this.getAssets().open(filename);
            int fileSize = fileData.available();
            byte[] buffer = new byte[fileSize];
            fileData.read(buffer);
            fileData.close();
            jsonString = new String(buffer, "UTF-8");
            return jsonString;
        } catch (IOException e) {
            Log.d(TAG, "Error opening file!");
            e.printStackTrace();
            return null;
        }
    }

    // Convert JSON data (String => Array)
    public JSONArray convertToJSON(String fileData) {
        JSONArray jsonData;
        try {
            jsonData = new JSONArray(fileData);
            return jsonData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Parse JSON data
    public void parseJSONData(JSONArray jsonArray) {
        Log.d(TAG, "Parsing json: " + jsonArray.toString());
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currentObject = jsonArray.getJSONObject(i);
                String name = currentObject.getString("name");
                String address = currentObject.getString("address");
                String phone = currentObject.getString("phone");
                String website = currentObject.getString("website");
                String description = currentObject.getString("description");
                double pricing = currentObject.getDouble("pricing");
                boolean favorite = currentObject.getBoolean("favorite");
                String image = currentObject.getString("image");
                Attraction attraction = new Attraction(name, address, phone, website, description, pricing, favorite, image);
                this.attractions.add(attraction);
            }
        } catch(JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    // Go back to the login screen
    public void logOut(View view) {
        Intent intent = new Intent(AttractionList.this, MainActivity.class);
        startActivity(intent);
    }

    // Launch browser in the map page
    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    public void mapPressed (View view) {
        goToUrl ( "https://www.bing.com/maps?osid=e8987db2-5d54-4457-91e2-89284e33d5d3&cp=-23.621225~-46.812311&lvl=11&imgid=3d63f236-d0c5-4a14-975c-bd8a0d865cbe&v=2&sV=2&form=S00027");
    }

    // Create new adapter for the list view
    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> rTitle;
        ArrayList<String> rDescription;
        ArrayList<Integer> rImgs;

        MyAdapter (Context c, ArrayList<String> title, ArrayList<String> description, ArrayList<Integer> imgs) {
            super(c, R.layout.attraction_item, R.id.textView1, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs = imgs;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.attraction_item, parent, false);
            ImageView images = row.findViewById(R.id.image);
            TextView myTitle = row.findViewById(R.id.textView1);
            TextView myDescription = row.findViewById(R.id.textView2);
            images.setImageResource(rImgs.get(position));
            myTitle.setText(rTitle.get(position));
            myDescription.setText(rDescription.get(position));
            return row;
        }
    }


}