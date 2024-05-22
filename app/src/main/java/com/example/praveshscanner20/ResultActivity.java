package com.example.praveshscanner20;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@SuppressWarnings("deprecation")
public class ResultActivity extends AppCompatActivity {

    private String qrCodeContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView emailTextView = findViewById(R.id.addressTextView);
        TextView adhaarTextView = findViewById(R.id.adhaarTextView);
        TextView phoneTextView = findViewById(R.id.phoneTextView);
        Button allowButton = findViewById(R.id.button);

        // Get the data from the intent
        qrCodeContents = getIntent().getStringExtra("qrCodeContents");
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String adhaar = getIntent().getStringExtra("adhaar");
        String phone = getIntent().getStringExtra("phone");

        // Set the data to the TextViews
        nameTextView.setText("Name: " + name);
        emailTextView.setText("Email: " + email);
        adhaarTextView.setText("Adhaar: " + adhaar);
        phoneTextView.setText("Phone: " + phone);

        allowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send the QR code content to the server
                new SendPostRequest().execute(qrCodeContents);
            }
        });
    }

    private class SendPostRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String qrCodeContent = params[0];
            String urlString = "https://proj-backend.vercel.app/updateticketstatus"; // URL to call

            try {
                // Parse the QR code content to a JSON object
                JSONObject scannedTicket = new JSONObject(qrCodeContent);

                // Check if the entryAt property exists
                if (scannedTicket.has("entryAt")) {
                    // If entryAt exists, set the exitAt property to the current time
                    scannedTicket.put("exitAt", new Date().toString());
                } else {
                    // If entryAt does not exist, set the entryAt property to the current time
                    scannedTicket.put("entryAt", new Date().toString());
                }

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setDoOutput(true);

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = scannedTicket.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Request successful";
                } else {
                    return "Request failed. Response code: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception occurred: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Show the result of the request as a toast
            Toast.makeText(ResultActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
}