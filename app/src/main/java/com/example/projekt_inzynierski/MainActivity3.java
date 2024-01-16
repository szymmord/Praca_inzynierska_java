package com.example.projekt_inzynierski;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity3 extends AppCompatActivity {

    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        textViewResult = findViewById(R.id.tTest);
        if (textViewResult == null) {
            // Możesz również dodać logikę obsługi sytuacji, gdy textViewResult jest null
            Log.e("MainActivity3", "Nie znaleziono TextView o ID tTest");
        }

        // Tworzenie instancji zadania asynchronicznego (AsyncTask)
        ApiRequestTask apiRequestTask = new ApiRequestTask();
        apiRequestTask.execute();
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
            if (textViewResult != null) {
                try {
                    // Parsowanie JSON
                    JSONObject jsonObject = new JSONObject(result);

                    // Ustawienie tekstu w TextView za pomocą StringBuilder
                    StringBuilder resultBuilder = new StringBuilder();

                    if (jsonObject.has("indexes")) {
                        JSONArray indexesArray = jsonObject.getJSONArray("indexes");
                        for (int i = 0; i < indexesArray.length(); i++) {
                            JSONObject index = indexesArray.getJSONObject(i);
                            String displayName = index.optString("displayName", "No DisplayName");
                            int aqi = index.optInt("aqi", -1);

                            // Dodawanie do StringBuilder
                            resultBuilder.append("DisplayName: ").append(displayName).append("\n");
                            resultBuilder.append("AQI: ").append(String.valueOf(aqi)).append("\n\n");
                        }
                    }

                    // Ustawienie tekstu w TextView
                    textViewResult.setText(resultBuilder.toString());
                } catch (JSONException e) {
                    Log.e("ApiRequestTask", "Błąd parsowania JSON: " + e.getMessage());
                }
            } else {
                Log.e("MainActivity3", "textViewResult is null in onPostExecute");
            }
        }
    }
}
