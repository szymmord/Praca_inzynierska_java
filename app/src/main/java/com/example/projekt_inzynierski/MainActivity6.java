package com.example.projekt_inzynierski;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity6 extends AppCompatActivity {
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        textViewResult = findViewById(R.id.tTest);
        if (textViewResult == null) {
            Log.e("MainActivity3", "Nie znaleziono TextView o ID textViewResult");
        }

        // Tworzenie instancji zadania asynchronicznego (AsyncTask)
        ApiRequestTask apiRequestTask = new ApiRequestTask();
        apiRequestTask.execute();
    }

    private class ApiRequestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Zastąp YOUR_API_KEY, YOUR_LONGITUDE, YOUR_LATITUDE rzeczywistymi danymi
                String apiKey = "AIzaSyCmUpVSnRFwnPIq16VNfKkyudamUVPALqU";
                double longitude = 35.32;
                double latitude = 32.32;

                // Zbuduj URL zgodnie z odpowiednią strukturą
                String apiUrl = "https://pollen.googleapis.com/v1/forecast:lookup?key=" + apiKey +
                        "&location.longitude=" + longitude + "&location.latitude=" + latitude + "&days=1";

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Konfiguracja żądania HTTP
                connection.setRequestMethod("GET");

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
            if (textViewResult != null) {
                textViewResult.setText(result);
            } else {
                Log.e("MainActivity3", "textViewResult is null in onPostExecute");
            }
        }
    }
}