package com.example.projekt_inzynierski;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
public class MainActivity4 extends AppCompatActivity {
    private EditText addressEditText;
    private TextView resultTextView;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        addressEditText = findViewById(R.id.addressEditText);
        resultTextView = findViewById(R.id.resultTextView);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressEditText.getText().toString();
                if (!address.isEmpty()) {
                    new GeocodeTask().execute(address);
                }
            }
        });
    }

    private class GeocodeTask extends AsyncTask<String, Void, LatLng> {

        @Override
        protected LatLng doInBackground(String... params) {
            android.location.Geocoder geocoder = new android.location.Geocoder(MainActivity4.this, Locale.getDefault());
            String address = params[0];

            try {
                java.util.List<android.location.Address> addresses = geocoder.getFromLocationName(address, 1);
                if (!addresses.isEmpty()) {
                    android.location.Address location = addresses.get(0);
                    return new LatLng(location.getLatitude(), location.getLongitude());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(LatLng result) {
            super.onPostExecute(result);

            if (result != null) {
                resultTextView.setText("Latitude: " + result.latitude + ", Longitude: " + result.longitude);
            } else {
                resultTextView.setText("Location not found");
            }
        }
    }
    private class ApiRequestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Zastąp YOUR_API_KEY rzeczywistym kluczem API
                String apiKey = "AIzaSyCmUpVSnRFwnPIq16VNfKkyudamUVPALqU";
                String apiUrl = "https://airquality.googleapis.com/v1/currentConditions:lookup?key=" + apiKey;

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Konfiguracja żądania HTTP
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Zastąp szerokość geograficzną (latitude) i długość geograficzną (longitude) rzeczywistymi wartościami
                String postData = "{\"location\":{\"latitude\":37.419734,\"longitude\":-122.0827784}}";

                // Zapisz dane POST do połączenia
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(postData);
                outputStream.flush();
                outputStream.close();

                // Pobierz odpowiedź od serwera
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    return "Błąd: " + responseCode;
                }
            } catch (Exception e) {
                return "Wyjątek: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (resultTextView != null) {
                resultTextView.setText(result);
            } else {
                Log.e("MainActivity3", "textViewResult is null in onPostExecute");
            }
        }
    }
}