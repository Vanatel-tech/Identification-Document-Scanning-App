package com.android.example.cameraxapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private LinearLayout userContainer;
    private String signUpTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        userContainer = findViewById(R.id.user_container);

        Button addUserButton = findViewById(R.id.add_user_button);
        addUserButton.setOnClickListener(v -> startActivity(new Intent(this, SelectDocumentActivity.class))); // Redirect to SelectDocumentActivity

        Button viewDatabaseButton = findViewById(R.id.view_database_button);
        viewDatabaseButton.setOnClickListener(v -> viewDatabaseEntries()); // View database entries

        Button openCameraButton = findViewById(R.id.open_camera_button);
        openCameraButton.setOnClickListener(v -> openCameraActivity()); // Redirect to CameraActivity

        EditText searchField = findViewById(R.id.search_field);
        searchField.setOnEditorActionListener((v, actionId, event) -> {
            searchDatabaseEntries(v.getText().toString()); // Search functionality
            return true;
        });

        // Retrieve user details from Intent
        String name = getIntent().getStringExtra("name");
        String idNumber = getIntent().getStringExtra("idNumber");
        String selectedDocument = getIntent().getStringExtra("selectedDocument");
        String selectedCountry = getIntent().getStringExtra("selectedCountry");
        String phone = getIntent().getStringExtra("phone");
        signUpTime = getIntent().getStringExtra("signUpTime");

        // Auto-fill fields if data is passed from previous activity
        if (name != null && idNumber != null && selectedDocument != null && selectedCountry != null && phone != null) {
            autoFillUserData(name, idNumber, selectedDocument, selectedCountry, phone);
        }
    }

    private void addUserEntry() {
        LinearLayout userRow = new LinearLayout(this);
        userRow.setOrientation(LinearLayout.VERTICAL);
        userRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create editable fields
        EditText nameEditText = createEditText("Name");
        EditText idNumberEditText = createEditText("ID Number");
        EditText documentEditText = createEditText("Document");
        EditText countryEditText = createEditText("Country");
        EditText phoneEditText = createEditText("Phone");
        EditText purposeOfVisitEditText = createEditText("Purpose of Visit");

        // Create Sign Out button
        Button signOutButton = new Button(this);
        signOutButton.setText("Sign Out");

        // Create Sign Out Time field
        EditText signOutTimeEditText = createEditText("Sign Out Time");
        signOutTimeEditText.setVisibility(View.GONE); // Initially hidden

        // Button click listener
        signOutButton.setOnClickListener(v -> {
            String signOutTime = getCurrentTimestamp(); // Get current timestamp
            signOutTimeEditText.setText(signOutTime);
            signOutTimeEditText.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);

            saveUserData(nameEditText.getText().toString(),
                    idNumberEditText.getText().toString(),
                    documentEditText.getText().toString(),
                    countryEditText.getText().toString(),
                    phoneEditText.getText().toString(),
                    purposeOfVisitEditText.getText().toString(),
                    signUpTime,
                    signOutTime);
        });

        // Add views to the user row
        userRow.addView(nameEditText);
        userRow.addView(idNumberEditText);
        userRow.addView(documentEditText);
        userRow.addView(countryEditText);
        userRow.addView(phoneEditText);
        userRow.addView(purposeOfVisitEditText);
        userRow.addView(signOutButton);
        userRow.addView(signOutTimeEditText);

        // Add user row to the container
        userContainer.addView(userRow);
    }

    private EditText createEditText(String hint) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setTextSize(12);
        return editText;
    }

    private void saveUserData(String name, String idNumber, String document,
                              String country, String phone, String purposeOfVisit,
                              String signUpTime, String signOutTime) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("id_number", idNumber);
        values.put("document", document);
        values.put("country", country);
        values.put("phone", phone);
        values.put("purpose_of_visit", purposeOfVisit);
        values.put("signup_time", signUpTime);
        values.put("signout_time", signOutTime);

        long newRowId = database.insert("UserSignup", null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "User data saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error saving data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewDatabaseEntries() {
        Cursor cursor = database.query("UserSignup", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                // Process each row (example: print data)
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String idNumber = cursor.getString(cursor.getColumnIndex("id_number"));
                String document = cursor.getString(cursor.getColumnIndex("document"));
                String country = cursor.getString(cursor.getColumnIndex("country"));
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
                String purposeOfVisit = cursor.getString(cursor.getColumnIndex("purpose_of_visit"));
                String signupTime = cursor.getString(cursor.getColumnIndex("signup_time"));
                String signOutTime = cursor.getString(cursor.getColumnIndex("signout_time"));

                // Example: Log data
                System.out.println("Name: " + name + ", ID: " + idNumber + ", Sign In: " + signupTime + ", Sign Out: " + signOutTime);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void searchDatabaseEntries(String query) {
        Cursor cursor = database.query("UserSignup", null, "name LIKE ?", new String[]{"%" + query + "%"}, null, null, null);
        if (cursor.moveToFirst()) {
            // Process search results
            do {
                // Example: Print data
                String name = cursor.getString(cursor.getColumnIndex("name"));
                System.out.println("Found: " + name);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void openCameraActivity() {
        startActivity(new Intent(this, CameraActivity.class));
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void autoFillUserData(String name, String idNumber, String document, String country, String phone) {
        // Implementation to autofill user data (if needed)
    }
}
