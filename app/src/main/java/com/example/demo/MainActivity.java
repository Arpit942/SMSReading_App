package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView myTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTextView = findViewById(R.id.textView);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
    }

    public void Read_SMS(View view) {
        // Check if the app has the READ_SMS permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, so you can read SMS messages

            // Define the URI to access SMS content
            Uri smsUri = Uri.parse("content://sms/inbox");

            // Create a cursor to query the SMS data
            Cursor cursor = getContentResolver().query(smsUri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int bodyIndex = cursor.getColumnIndex("body"); // Index of the SMS message body
                int addressIndex = cursor.getColumnIndex("address"); // Index of the sender's phone number
                int dateIndex = cursor.getColumnIndex("date"); // Index of the date the SMS was sent

                // Get the SMS message body, sender's phone number, and date from the cursor
                String smsMessage = cursor.getString(bodyIndex);
                String senderNumber = cursor.getString(addressIndex);
                long dateMillis = cursor.getLong(dateIndex);

                // Format the date and time
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
                String formattedDate = sdf.format(new Date(dateMillis));


                // Extract and display location information if available in the message
                String locationInfo = extractLocationInfo(smsMessage); // Define a method to extract location information

                if (locationInfo != null && !locationInfo.isEmpty()) {
                    myTextView.setText("Sender: " + senderNumber + "\nDate: " + formattedDate + "\nLocation: " + locationInfo + "\nMessage: " + smsMessage);
                } else {
                    myTextView.setText("Sender: " + senderNumber + "\nDate: " + formattedDate + "\nMessage: " + smsMessage);
                }
            } else {
                // Handle the case where there are no SMS messages in the inbox
                myTextView.setText("No SMS messages found.");
            }

            // Close the cursor when you're done with it
            if (cursor != null) {
                cursor.close();
            }
        } else {
            // Permission is not granted, request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        }
    }
    private String extractLocationInfo(String smsMessage) {
        // Search for location information enclosed in square brackets in the SMS message body
        int startBracketIndex = smsMessage.indexOf("[");
        int endBracketIndex = smsMessage.indexOf("]");

        if (startBracketIndex != -1 && endBracketIndex != -1 && endBracketIndex > startBracketIndex) {
            // Extract the location information between the square brackets
            String locationInfo = smsMessage.substring(startBracketIndex + 1, endBracketIndex);
            return locationInfo;
        } else {
            // If no location information is found, return an empty string
            return "";
        }
    }


}