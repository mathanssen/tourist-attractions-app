package com.example.touristattractionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Register extends AppCompatActivity {

    // Properties
    private String TAG = "REGISTER";
    private EditText etUsername;
    private EditText etPassword;

    // On Create Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void backInitialScreen(View view) {
        Intent intent = new Intent(Register.this, MainActivity.class);
        startActivity(intent);
    }

    // Read JSON
    private String readFromFile() {
        String ret = "";
        InputStream inputStream = null;
        try {
            inputStream = openFileInput("user_accounts.json");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    // Main methods
    public void secondRegisterPressed(View view) {
        // User interface components
        etUsername = (EditText) findViewById(R.id.text_register_username);
        etPassword = (EditText) findViewById(R.id.text_register_password);

        // Get the values stored in the user interface components
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        // Create user
        UserAccount user = new UserAccount(username, password);

        // Read and convert JSON String => Object
        String jsonString = readFromFile();
        JSONArray jsonData = this.convertToJSON(jsonString);
        if (jsonData == null) {
            Log.d(TAG, "Error converting file string data to JSON");
            return;
        }

        // Register account
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put("username", user.getName());
            object.put("password", user.getPassword());
            jsonData.put(object);
        } catch (Exception e) {
            Log.d(TAG, "Failed to create JSON object");
            e.printStackTrace();
        }

        // Write to the storage
        String data = jsonData.toString();
        Log.d(TAG, "Data: " + data);
        this.writeToFile("user_accounts.json", data);

        // Go to attractions
        Intent intent = new Intent(this, AttractionList.class);
        startActivity(intent);
    }

    private void writeToFile(String filename, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.d(TAG, "File written");
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
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

}