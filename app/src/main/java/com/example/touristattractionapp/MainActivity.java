package com.example.touristattractionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Properties
    private String TAG = "MAIN";
    private EditText etUsername;
    private EditText etPassword;
    private CheckBox cboRememberMe;
    private boolean fileExists;
    private ArrayList<UserAccount> userAccounts = new ArrayList<UserAccount>();
    private SharedPreferences preferences;
    private static final String PREFERENCES_NAME = "SavedAccount";

    // On Create Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load given JSON file to storage
        this.fileExists = fileExist("user_accounts.json");
        Log.d(TAG, "File exists: " + fileExists);
        if (this.fileExists == false) {
            copyFileToStorage();
        }

        // Load shared preferences and set saved credentials
        this.preferences = this.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean sharedPreferencesExists = checkSharedPreferencesExists();
        if (sharedPreferencesExists == true) {
            String usernameSaved = this.preferences.getString("username","");
            String passwordSaved = this.preferences.getString("password", "");
            etUsername = (EditText) findViewById(R.id.text_login_username);
            etPassword = (EditText) findViewById(R.id.text_login_password);
            etUsername.setText(usernameSaved);
            etPassword.setText(passwordSaved);
        }
    }

    // Main methods
    public void loginPressed(View view) {
        // User interface components
        etUsername = (EditText) findViewById(R.id.text_login_username);
        etPassword = (EditText) findViewById(R.id.text_login_password);
        cboRememberMe = (CheckBox) findViewById(R.id.checkbox_remember_me);

        // Get the values stored in the user interface components
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        boolean rememberMe = cboRememberMe.isChecked();

        // Load the file
        String fileData = this.loadDataFromStorage();
        if (fileData == null) {
            Log.d(TAG, "Error loading file");
            return;
        }

        // Convert the file (String => JSON)
        JSONArray jsonData = this.convertToJSON(fileData);
        if (jsonData == null) {
            Log.d(TAG, "Error converting file string data to JSON");
            return;
        }

        // Parse the data and check credentials
        boolean isUser = this.checkCredential(jsonData, username, password);
        Log.d(TAG, "Username: " + username + " password: " + password);
        if (isUser == false) {
            Log.d(TAG, "User not registered");
            Toast popup = Toast.makeText(getApplicationContext(), "User not registered", Toast.LENGTH_SHORT);
            popup.show();
        } else {
            if (rememberMe == true) {
                saveCredentials();
            }
            Intent intent = new Intent(this, AttractionList.class);
            startActivity(intent);
        }
    }

    // Load JSON data from internal storage
    public String loadDataFromStorage() {
        String ret = "";
        InputStream inputStream = null;
        try {
            inputStream = openFileInput("user_accounts.json");

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

    public boolean checkSharedPreferencesExists() {
        boolean sharedPreferencesExists = false;
        File f = new File("/data/data/com.example.touristattractionapp/shared_prefs/SavedAccount.xml");
        if (f.exists()) {
            sharedPreferencesExists = true;
        }
        return sharedPreferencesExists;
    }

    public void saveCredentials() {
        // Save the name to Shared Preferences
        SharedPreferences.Editor prefEditor = preferences.edit();

        // User interface components
        etUsername = (EditText) findViewById(R.id.text_login_username);
        etPassword = (EditText) findViewById(R.id.text_login_password);

        // Get the values
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        // Create a key-value pair
        prefEditor.putString("username", username);
        prefEditor.putString("password", password);

        // Commit the changes
        prefEditor.apply();

        Log.d(TAG, "Username and password saved!");
    }

    public void firstRegisterPressed(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    // Other methods
    public boolean fileExist(String filename){
        File file = getBaseContext().getFileStreamPath(filename);
        return file.exists();
    }

    // Functions for reading JSON of the users and check username and password
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
                String username = currentObject.getString("username");
                String password = currentObject.getString("password");
                UserAccount account = new UserAccount(username, password);
                this.userAccounts.add(account);
                Log.d(TAG, "Successfully added: " + account.toString());
                Log.d(TAG, "========");
            }
        } catch(JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    public boolean checkCredential(JSONArray jsonArray, String usernameToCheck, String passwordToCheck) {
        Log.d(TAG, "Parsing JSON: " + jsonArray.toString());
        boolean isRegistered = false;
        try {
            for (int i= 0; i < jsonArray.length(); i++) {
                JSONObject currentObject = jsonArray.getJSONObject(i);
                String username = currentObject.getString("username");
                String password = currentObject.getString("password");
                if (username.contentEquals(usernameToCheck) && password.contentEquals(passwordToCheck)) {
                    isRegistered = true;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isRegistered;
    }

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

    public void copyFileToStorage() {
        String fileData = this.loadDataFromFile("users.json");
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
        if (this.userAccounts.size() == 0) {
            Log.d(TAG, "Failed to load all the words from the JSON file");
            return;
        }

        JSONArray accountsArray = new JSONArray();
        for (int i = 0; i < this.userAccounts.size(); i++) {
            UserAccount account = this.userAccounts.get(i);
            JSONObject accountObject = null;
            try {
                accountObject = new JSONObject();
                accountObject.put("username", account.getName());
                accountObject.put("password", account.getPassword());
                accountsArray.put(i, accountObject);
            }
            catch (Exception e) {
                Log.d(TAG, "Failed to create JSON object");
                e.printStackTrace();
            }
        }
        String data = accountsArray.toString();
        Log.d(TAG, "Data: " + data);
        this.writeToFile("user_accounts.json", data);
    }

}