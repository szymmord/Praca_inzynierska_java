package com.example.projekt_inzynierski;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Api2 extends AsyncTask<Void, Void, String>{
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
        // Przetwórz wynik tutaj
        // Zmienna wynikowa zawiera odpowiedź od serwera
    }
}