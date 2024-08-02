package com.android.example.cameraxapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignOutActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText searchBar;
    private Button backButton;
    private Button searchButton;
    private LinearLayout userContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signout);

        databaseHelper = new DatabaseHelper(this);

        searchBar = findViewById(R.id.search_bar);
        backButton = findViewById(R.id.button_back);
        searchButton = findViewById(R.id.search_button);
        userContainer = findViewById(R.id.user_container);

        backButton.setOnClickListener(view -> finish());

        searchButton.setOnClickListener(view -> {
            String query = searchBar.getText().toString();
            if (!query.isEmpty()) {
                searchAndDisplayVisitors(query);
            } else {
                Toast.makeText(SignOutActivity.this, "Please enter an ID Number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchAndDisplayVisitors(String idNumber) {
        Cursor cursor = databaseHelper.getVisitorsNotSignedOut();
        if (cursor != null) {
            userContainer.removeAllViews(); // Clear previous search results

            boolean visitorFound = false;
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_NUMBER));

                if (id.equals(idNumber)) {
                    visitorFound = true;
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                    String document = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOCUMENT));
                    String country = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COUNTRY));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));
                    String purposeOfVisit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PURPOSE_OF_VISIT));
                    String signUpTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SIGNUP_TIME));
                    String signOutTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SIGNOUT_TIME));

                    displayUserData(name, id, document, country, phone, purposeOfVisit, signUpTime, signOutTime);

                    if (databaseHelper.signOutVisitor(idNumber)) {
                        Toast.makeText(SignOutActivity.this, "Visitor signed out successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignOutActivity.this, "Failed to sign out visitor", Toast.LENGTH_SHORT).show();
                    }
                    break; // Exit loop once visitor is found and signed out
                }
            }

            if (!visitorFound) {
                Toast.makeText(SignOutActivity.this, "No visitor found with this ID", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
        }
    }

    private void displayUserData(String name, String idNumber, String document, String country, String phone, String purposeOfVisit, String signUpTime, String signOutTime) {
        LinearLayout userRow = new LinearLayout(this);
        userRow.setOrientation(LinearLayout.VERTICAL);
        userRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView nameTextView = createTextView("Name: " + name);
        TextView idNumberTextView = createTextView("ID Number: " + idNumber);
        TextView documentTextView = createTextView("Document: " + document);
        TextView countryTextView = createTextView("Country: " + country);
        TextView phoneTextView = createTextView("Phone: " + phone);
        TextView purposeOfVisitTextView = createTextView("Purpose of Visit: " + purposeOfVisit);
        TextView signUpTimeTextView = createTextView("Sign Up Time: " + signUpTime);
        TextView signOutTimeTextView = createTextView("Sign Out Time: " + signOutTime);

        userRow.addView(nameTextView);
        userRow.addView(idNumberTextView);
        userRow.addView(documentTextView);
        userRow.addView(countryTextView);
        userRow.addView(phoneTextView);
        userRow.addView(purposeOfVisitTextView);
        userRow.addView(signUpTimeTextView);
        userRow.addView(signOutTimeTextView);

        userContainer.addView(userRow);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(12);
        return textView;
    }
}
