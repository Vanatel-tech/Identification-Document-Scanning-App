package com.android.example.cameraxapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class DisplayActivity extends AppCompatActivity {
    private TopBar topBar;
    private BottomBar bottomBar;

    private TextView textNameView, textDocNoView, textPhoneNoView, selectedDocumentView, selectedCountryView;
    private EditText additionalDetailsView;
    private Spinner floorSpinner;
    private static final int REQUEST_PHONE_VERIFICATION = 1;

    private ProgressBar progressBar;
    private SQLiteDatabase database;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

//        Initialize the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

//        Get the intent
        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());

        //          Set the top bar Items
        topBar = findViewById(R.id.top_bar);
        topBar.setTitle("Visitor Details");
        topBar.setBackIconClickListener(view -> {
            intent.set(new Intent(DisplayActivity.this, SelectDocumentActivity.class));
            startActivity(intent.get());
            finish();
        });
        topBar.setMenuIconClickListener(view -> {
            Toast.makeText(this, "Menu Icon clicked", Toast.LENGTH_SHORT).show();
        });

//        Set the bottom bar items
        bottomBar = findViewById(R.id.bottom_bar);

        // Initialize views
        progressBar = findViewById(R.id.Progressbar);
        progressBar.setVisibility(View.GONE);

        textNameView = findViewById(R.id.name_text);
        textDocNoView = findViewById(R.id.doc_no_text);
        textPhoneNoView = findViewById(R.id.phone_number_text);
        selectedDocumentView = findViewById(R.id.selected_document_text);
        selectedCountryView = findViewById(R.id.selected_country_text);
        additionalDetailsView = findViewById(R.id.additional_details_text);
        floorSpinner = findViewById(R.id.floor_spinner);

//        Buttons
        Button buttonBack = findViewById(R.id.button_retake);
        ImageView iconBack = findViewById(R.id.back_icon);
        Button buttonSave = findViewById(R.id.save_data);

//  Check firing intent activity
        String source = intent.get().getStringExtra("source");
        if (source != null) {
            switch (source) {
                case "CameraActivity":
                    String extractedText = intent.get().getStringExtra("extractedText");
                    String documentType = intent.get().getStringExtra("selectedDocument");
                    String country = intent.get().getStringExtra("selectedCountry");
                    String phoneNumber = intent.get().getStringExtra("phoneNo");

                    selectedDocumentView.setText(documentType);
                    selectedCountryView.setText(country);
                    textPhoneNoView.setText(phoneNumber);

                    if (extractedText != null) {
                        String[] lines = extractedText.split("\n");

                        for (int i = 0; i < lines.length; i++) {
                            lines[i] = lines[i].replaceAll("\\s", "");
                        }

                        if ("ID Card".equals(documentType)) {
                            getIDCardDetails(lines);
                        } else if ("Passport".equals(documentType)) {
                            getPassportDetails(lines);
                        } else if ("Driving License".equals(documentType)) {
                            Toast.makeText(DisplayActivity.this, "Selected Doc is DL ", Toast.LENGTH_SHORT).show();
                        } else {
                            intent.set(new Intent(DisplayActivity.this, SelectDocumentActivity.class));
                            startActivity(intent.get());
                            finish();
                        }

                    } else {
                        Toast.makeText(this, "No text found. Please Retake image", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    break;

                case "VerifyPhoneActivity":
                    String countryname = intent.get().getStringExtra("selectedCountry");
                    String phoneNo = intent.get().getStringExtra("phoneNo");

                    selectedDocumentView.setText("No Document");
                    selectedCountryView.setText(countryname);
                    textPhoneNoView.setText(phoneNo);

                    break;
            }
        }

//        Spinners
        Spinner organizationSpinner = findViewById(R.id.organization_spinner);
        List<String> organizations = Arrays.asList("Organization 1", "Organization 2", "Organization 3");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, organizations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        organizationSpinner.setAdapter(adapter);

        organizationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no item selected
            }
        });

//        Floor spinner setup
        List<String> floors = Arrays.asList("Ground Floor", "First Floor", "Second Floor", "Third Floor");
        ArrayAdapter<String> floorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, floors);
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorSpinner.setAdapter(floorAdapter);

//        Button listeners
        buttonBack.setOnClickListener(view -> {
            intent.set(new Intent(DisplayActivity.this, SelectDocumentActivity.class));
            startActivity(intent.get());
            finish();
        });

        iconBack.setOnClickListener(view -> {
            intent.set(new Intent(DisplayActivity.this, SelectDocumentActivity.class));
            startActivity(intent.get());
            finish();
        });

        buttonSave.setOnClickListener(view -> {
            saveData();
        });
    }

    private void getIDCardDetails(String[] lines) {
        // Extract details from lines and set to views
        // For example:
        textNameView.setText("Extracted Name"); // Use extracted value
        textDocNoView.setText("Extracted ID Number"); // Use extracted value
    }

    private void getPassportDetails(String[] lines) {
        // Extract details from lines and set to views
        // For example:
        textNameView.setText("Extracted Name"); // Use extracted value
        textDocNoView.setText("Extracted Passport Number"); // Use extracted value
    }

    private void saveData() {
        progressBar.setVisibility(View.VISIBLE);

        String name = textNameView.getText().toString();
        String idNumber = textDocNoView.getText().toString();
        String document = selectedDocumentView.getText().toString();
        String country = selectedCountryView.getText().toString();
        String phone = textPhoneNoView.getText().toString();
        String purposeOfVisit = ""; // Get purpose from your logic or UI
        String signupTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String signoutTime = ""; // Set appropriately
        String floor = floorSpinner.getSelectedItem().toString();
        String additionalDetails = additionalDetailsView.getText().toString();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_ID_NUMBER, idNumber);
        values.put(DatabaseHelper.COLUMN_DOCUMENT, document);
        values.put(DatabaseHelper.COLUMN_COUNTRY, country);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_PURPOSE_OF_VISIT, purposeOfVisit);
        values.put(DatabaseHelper.COLUMN_SIGNUP_TIME, signupTime);
        values.put(DatabaseHelper.COLUMN_SIGNOUT_TIME, signoutTime);
        values.put(DatabaseHelper.COLUMN_FLOOR, floor);
        values.put(DatabaseHelper.COLUMN_ADDITIONAL_DETAILS, additionalDetails);

        long result = database.insert(DatabaseHelper.TABLE_USER_SIGNUP, null, values);

        if (result != -1) {
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        }

        progressBar.setVisibility(View.GONE);
    }
}
