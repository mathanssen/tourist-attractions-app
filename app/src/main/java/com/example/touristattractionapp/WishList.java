package com.example.touristattractionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WishList extends AppCompatActivity {

    // Properties
    private String TAG = "WISH-LIST";
    private ListView listView;
    private ArrayList<Attraction> attractions = new ArrayList<Attraction>();
    private ArrayList<String> mTitle = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        // Load and convert JSON with the attractions information
        String fileData = this.loadDataFromFile();
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

        // Show title
        for (int i = 0; i < attractions.size(); i++) {
            Attraction currentAttraction = this.attractions.get(i);
            String currentTitle = currentAttraction.getName();
            boolean currentFavorite = currentAttraction.isFavorite();
            if (currentFavorite == true) {
                this.mTitle.add(currentTitle);
            }
        }

        listView = findViewById(R.id.wishList);
        MyAdapter adapter = new MyAdapter(this, mTitle);
        listView.setAdapter(adapter);
    }

    // Go to the main screen
    public void GoToAttractions(View view) {
        Intent intent = new Intent(WishList.this, AttractionList.class);
        startActivity(intent);
    }

    // Go back to the login screen
    public void logOut(View view) {
        Intent intent = new Intent(WishList.this, MainActivity.class);
        startActivity(intent);
    }

    // Go to the map in browser
    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    public void mapPressed (View view) {
        goToUrl ( "https://www.bing.com/maps?osid=e8987db2-5d54-4457-91e2-89284e33d5d3&cp=-23.621225~-46.812311&lvl=11&imgid=3d63f236-d0c5-4a14-975c-bd8a0d865cbe&v=2&sV=2&form=S00027");
    }

    public String loadDataFromFile() {
        String ret = "";
        InputStream inputStream = null;
        try {
            inputStream = openFileInput("wish_list.json");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

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

    public void parseJSONData(JSONArray jsonArray) {
        Log.d(TAG, "Parsing json: " + jsonArray.toString());
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currentObject = jsonArray.getJSONObject(i);
                String name = currentObject.getString("name");
                boolean favorite = currentObject.getBoolean("favorite");
                Attraction attraction = new Attraction(name, favorite);
                this.attractions.add(attraction);
            }
        } catch(JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> rTitle;

        MyAdapter (Context c, ArrayList<String> title) {
            super(c, R.layout.wish_list_item, R.id.textView1, title);
            this.context = c;
            this.rTitle = title;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.wish_list_item, parent, false);
            TextView myTitle = row.findViewById(R.id.textView1);
            myTitle.setText(rTitle.get(position));
            return row;
        }
    }

}





