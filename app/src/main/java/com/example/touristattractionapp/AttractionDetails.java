package com.example.touristattractionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

public class AttractionDetails extends AppCompatActivity {

    // Properties
    String TAG = "ATTRACTION-DETAILS";
    private TextView title;
    private TextView description;
    private TextView address;
    private TextView phone;
    private TextView pricing;
    private String website;
    private RatingBar rating;
    private int ratingLevel;
    private Attraction attraction;
    private boolean fileExists;
    private boolean isInWishList = false;
    private ArrayList<Attraction> attractions = new ArrayList<Attraction>();
    private JSONArray accountsArray = new JSONArray();
    private int currentPosition = 0;


    // On create properties and methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_details);

        // Get the values from attraction list
        Intent myIntent = getIntent();
        Attraction attraction = (Attraction) myIntent.getSerializableExtra("AttractionObj");
        this.attraction = attraction;

        // Rating Bar
        rating = (RatingBar) findViewById(R.id.ratingBar);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingLevel = (int) ratingBar.getRating();
            }
        });

        // Create a wish list if does not exist. Load if exists.
        this.fileExists = fileExist("wish_list.json");
        if (this.fileExists == false) {
            createWishList();
        } else {
            String fileData = loadDataFromFile();
            JSONArray jsonData = this.convertToJSON(fileData);
            boolean isInList = false;
            try {
                for (int i = 0; i < jsonData.length(); i++) {
                    JSONObject currentObject = jsonData.getJSONObject(i);
                    this.accountsArray.put(i, currentObject);
                    if (currentObject.getString("name").contentEquals(this.attraction.getName())) {
                        isInList = true;
                        if (currentObject.getBoolean("favorite") == true) {
                            this.isInWishList = true;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (isInList == false) {
                JSONObject accountObject = null;
                try {
                    accountObject = new JSONObject();
                    accountObject.put("name", this.attraction.getName());
                    accountObject.put("favorite", this.attraction.isFavorite());
                    this.accountsArray.put(accountObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // Set the red heart image if the attraction is in wish list
            ImageView ivHeart = (ImageView) findViewById(R.id.image_heart);
            if (isInWishList == true) {
                String imageName = "heart_red";
                int resID = getResources().getIdentifier(imageName, "drawable", getPackageName());
                ivHeart.setImageResource(resID);
            }
        }

        // Set the image of the attraction
        ImageView ivPicture = (ImageView) findViewById(R.id.image_details);
        String imageName = attraction.getImage();
        int resID = getResources().getIdentifier(imageName, "drawable", this.getPackageName());
        ivPicture.setImageResource(resID);

        // UI components
        title = (TextView) findViewById(R.id.text_title_details);
        description = (TextView) findViewById(R.id.text_description_details);
        address = (TextView) findViewById(R.id.text_address_details);
        phone = (TextView) findViewById(R.id.text_phone_details);
        pricing = (TextView) findViewById(R.id.text_pricing_details);

        // Set the attraction information
        title.setText(attraction.getName());
        description.setText(attraction.getDescription());
        address.setText(attraction.getAddress());
        phone.setText(attraction.getPhone());
        pricing.setText(String.valueOf(attraction.getPricing()));
        website = attraction.getWebsite();

        // Set the website link
        TextView textView = (TextView) findViewById(R.id.text_website_details);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href="+ website +"> Go to the website! </a>";
        textView.setText(Html.fromHtml(text));
    }

    // Open phone dialer
    public void numberPressed(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:0123456789"));
        startActivity(intent);
    }

    // Load data from JSON
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

    // Convert JSON data
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

    // Add attraction to the wish list
    public void addToWishList(View view) {
        // Change the heart picture
        ImageView ivPicture = (ImageView) findViewById(R.id.image_heart);
        if (this.isInWishList == false) {
            String imageName = "heart_red";
            int resID = getResources().getIdentifier(imageName, "drawable", getPackageName());
            ivPicture.setImageResource(resID);
            Toast popup = Toast.makeText(getApplicationContext(), "Added to your wish list", Toast.LENGTH_SHORT);
            popup.show();
            this.attraction.setFavorite(true);
            this.isInWishList = true;
        } else {
            String imageName = "heart_black";
            int resID = getResources().getIdentifier(imageName, "drawable", getPackageName());
            ivPicture.setImageResource(resID);
            Toast popup = Toast.makeText(getApplicationContext(), "Removed from your wish list", Toast.LENGTH_SHORT);
            popup.show();
            this.attraction.setFavorite(false);
            this.isInWishList = false;
        }

        // Change state of the object
        for (int i = 0; i < this.accountsArray.length(); i++) {
            try {
                JSONObject currObject = accountsArray.getJSONObject(i);
                if (this.attraction.getName().contentEquals(currObject.getString("name"))) {
                    this.accountsArray.remove(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject accountObject = null;
        try {
            accountObject = new JSONObject();
            accountObject.put("name", this.attraction.getName());
            accountObject.put("favorite", this.attraction.isFavorite());
            this.accountsArray.put(accountObject);
            Log.d(TAG, "PRINT" + this.accountsArray);
        }
        catch (Exception e) {
            Log.d(TAG, "Failed to create JSON object");
            e.printStackTrace();
        }

        // Save wish list
        String data = this.accountsArray.toString();
        this.writeToFile("wish_list.json", data);
        Log.d(TAG, "Wish list saved!");
    }

    // Write and storage JSON data
    public void writeToFile(String filename, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE));
            Log.d(TAG, "File is saved: " + this.getFilesDir().getAbsolutePath());
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Nothing was written");
        }
    }

    // Change the image
    public void changeImage(View view) {
        ArrayList<String> imagesList = new ArrayList<String>();
        if (this.attraction.getName().contentEquals("Paulista Avenue")) {
            imagesList.add("avenue");
            imagesList.add("avenue_2");
            imagesList.add("avenue_3");
        } else if (this.attraction.getName().contentEquals("MASP")) {
            imagesList.add("masp");
            imagesList.add("masp_2");
            imagesList.add("masp_3");
        } else if (this.attraction.getName().contentEquals("Football Museum")) {
            imagesList.add("museum");
            imagesList.add("museum_2");
            imagesList.add("museum_3");
        } else if (this.attraction.getName().contentEquals("Ibirapuera Park")) {
            imagesList.add("park");
            imagesList.add("park_2");
            imagesList.add("park_3");
        } else if (this.attraction.getName().contentEquals("Neo Quimica Arena")) {
            imagesList.add("stadium");
            imagesList.add("stadium_2");
            imagesList.add("stadium_3");
        }
        ImageView ivPicture = (ImageView) findViewById(R.id.image_details);
        Random random = new Random();
        int position = this.currentPosition;
        while (position == this.currentPosition) {
            position = random.nextInt(imagesList.size());
        }
        this.currentPosition = position;
        String character = imagesList.get(this.currentPosition);
        int resID = getResources().getIdentifier(character, "drawable", this.getPackageName());
        ivPicture.setImageResource(resID);
    }

    // Create wish list if does not exist
    public void createWishList() {
        JSONArray accountsArray = new JSONArray();
        JSONObject accountObject = null;
        try {
            accountObject = new JSONObject();
            accountObject.put("name", this.attraction.getName());
            accountObject.put("favorite", this.attraction.isFavorite());
            accountsArray.put(accountObject);
        }
        catch (Exception e) {
            Log.d(TAG, "Failed to create JSON object");
            e.printStackTrace();
        }
        String data = accountsArray.toString();
        Log.d(TAG, "Data: " + data);
        this.writeToFile("wish_list.json", data);
        Log.d(TAG, "Wish list created!");
    }

    // Load wish list if it exists
    public String loadWishList() {
        String fileData = this.loadDataFromFile();
        JSONArray jsonData = this.convertToJSON(fileData);
        this.parseJSONData(jsonData);
        JSONArray accountsArray = new JSONArray();
        for (int i = 0; i < this.attractions.size(); i++) {
            Attraction attraction = this.attractions.get(i);
            JSONObject accountObject = null;
            try {
                accountObject = new JSONObject();
                accountObject.put("name", this.attraction.getName());
                accountObject.put("favorite", this.attraction.isFavorite());
                accountsArray.put(accountObject);
            }
            catch (Exception e) {
                Log.d(TAG, "Failed to create JSON object");
                e.printStackTrace();
            }
        }
        String data = accountsArray.toString();
//        this.writeToFile("user_accounts.json", data);
        return data;
    }

    // Check if a file exists in the internal storage
    public boolean fileExist(String filename){
        File file = getBaseContext().getFileStreamPath(filename);
        return file.exists();
    }


}