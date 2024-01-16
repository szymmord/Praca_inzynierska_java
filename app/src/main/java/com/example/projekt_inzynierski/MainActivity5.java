package com.example.projekt_inzynierski;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
public class MainActivity5 extends AppCompatActivity {
    private EditText addressEditText;
    private TextView resultTextView, resultTextView2; // Zmiana dokonana tutaj

    private Button searchButton,back;
    private LatLng currentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        addressEditText = findViewById(R.id.addressEditText);
        resultTextView =(TextView) findViewById(R.id.resultTextView);
        searchButton = findViewById(R.id.searchButton);
        resultTextView2= findViewById(R.id.resultTextView2);
        back = findViewById(R.id.refresh);




        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressEditText.getText().toString();
                if (!address.isEmpty()) {
                    new GeocodeTask().execute(address);

                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=  new Intent(getApplicationContext(), MainActivity.class  );
                startActivity(intent);
                finish();
            }
        });
    }

    private class GeocodeTask extends AsyncTask<String, Void, LatLng> {

        @Override
        protected LatLng doInBackground(String... params) {
            android.location.Geocoder geocoder = new android.location.Geocoder(MainActivity5.this, Locale.getDefault());
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
            if (result != null) {
                // Przekazuj dane geokodowania do ApiRequestTask
                ApiRequestTask apiRequestTask = new ApiRequestTask();
                apiRequestTask.execute(result);
                ApiRequestTask2 apiRequestTask2 = new ApiRequestTask2();
                apiRequestTask2.execute(result);
            } else {
                Log.e("GeocodeTask", "Błąd Geocoding: Brak danych");
            }

        }
    }
    private class ApiRequestTask extends AsyncTask<LatLng, Void, String> {

        @Override
        protected String doInBackground(LatLng... params) {
            try {
                if (params.length == 0 || params[0] == null) {
                    return "Błąd: Brak danych geokodowania";
                }

                // Zastąp YOUR_API_KEY rzeczywistym kluczem API
                String apiKey = "AIzaSyCmUpVSnRFwnPIq16VNfKkyudamUVPALqU";
                String apiUrl = "https://airquality.googleapis.com/v1/currentConditions:lookup?key=" + apiKey;

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Konfiguracja żądania HTTP
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                LatLng geocodeResult = params[0];

                // Zastąp szerokość geograficzną (latitude) i długość geograficzną (longitude) rzeczywistymi wartościami
                String postData = String.format(
                        "{\"universalAqi\": true, \"location\": {\"latitude\": %f, \"longitude\": %f}, " +
                                "\"extraComputations\": [\"HEALTH_RECOMMENDATIONS\",\"DOMINANT_POLLUTANT_CONCENTRATION\"," +
                                "\"POLLUTANT_CONCENTRATION\",\"LOCAL_AQI\",\"POLLUTANT_ADDITIONAL_INFO\"], " +
                                "\"languageCode\": \"en\"}",
                        geocodeResult.latitude, geocodeResult.longitude
                );

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
                try {
                    // Parsowanie JSON
                    JSONObject jsonObject = new JSONObject(result);

                    // Wyświetlanie informacji o jakości powietrza (AQI)
                    if (jsonObject.has("indexes")) {
                        JSONArray indexesArray = jsonObject.getJSONArray("indexes");

                        // Utwórz StringBuilder do przechowywania tekstu
                        StringBuilder stringBuilder = new StringBuilder();

                        for (int i = 0; i < indexesArray.length(); i++) {
                            JSONObject index = indexesArray.getJSONObject(i);
                            String displayName = index.optString("displayName", "No DisplayName");
                            int aqi = index.optInt("aqi", -1);

                            // Dodawanie do StringBuilder z odpowiednim kolorem
                            String colorTag = determineColorTagForAQI(aqi);
                            stringBuilder.append(displayName).append("<br>");
                            stringBuilder.append("AQI: ").append(colorTag).append(String.valueOf(aqi)).append("</font><br><br>");
                        }

                        // Ustawienie tekstu w resultTextView przy użyciu StringBuilder
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            resultTextView.setText(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            resultTextView.setText(HtmlCompat.fromHtml(stringBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                        }
                    }

                    // Dodaj automatyczne odświeżanie co 30 sekund
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Tutaj umieść kod do ponownego wykonania zapytania
                            // Sprawdź, czy currentLatLng nie jest null
                            if (currentLatLng != null) {
                                new ApiRequestTask().execute(currentLatLng);
                            }
                        }
                    }, 3000); // 30 sekund (czas w milisekundach)

                    // Wyświetlanie informacji o zanieczyszczeniach (pollutants)
                    if (jsonObject.has("pollutants")) {
                        JSONArray pollutantsArray = jsonObject.getJSONArray("pollutants");

                        // Utwórz StringBuilder do przechowywania tekstu
                        StringBuilder stringBuilder = new StringBuilder();

                        for (int i = 0; i < pollutantsArray.length(); i++) {
                            JSONObject pollutant = pollutantsArray.getJSONObject(i);
                            String pollutantName = pollutant.optString("displayName", "No Name");
                            String concentrationValue = "No Data";
                            if (pollutant.has("concentration")) {
                                JSONObject concentration = pollutant.getJSONObject("concentration");
                                if (concentration.has("value")) {
                                    double value = concentration.getDouble("value");
                                    concentrationValue = String.valueOf(value);
                                }
                            }

                            // Dodawanie do StringBuilder z odpowiednim kolorem
                            String colorTag = determineColorTagForPollutant(pollutantName, concentrationValue);
                            stringBuilder.append("Pollutant: ").append(pollutantName).append(", Value: ").append(colorTag).append(concentrationValue).append("</font><br>");

                            // Dodawanie zakładki "effects" tylko jeśli dostępna
                            if (pollutant.has("additionalInfo")) {
                                JSONObject additionalInfo = pollutant.getJSONObject("additionalInfo");
                                if (additionalInfo.has("effects")) {
                                    String effects = additionalInfo.optString("effects", "No Effects");
                                    stringBuilder.append("Effects: ").append(effects).append("<br>");
                                }
                            }

                            stringBuilder.append("<br>");
                        }

                        // Ustawienie tekstu w resultTextView przy użyciu StringBuilder
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            resultTextView.append(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            resultTextView.append(HtmlCompat.fromHtml(stringBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                        }
                    }

                    // Wyświetlanie informacji o zaleceniach zdrowotnych (healthRecommendations)
                    if (jsonObject.has("healthRecommendations")) {
                        JSONObject healthRecommendations = jsonObject.getJSONObject("healthRecommendations");
                        if (healthRecommendations.has("generalPopulation")) {
                            String generalPopulationRecommendation = healthRecommendations.optString("generalPopulation", "No Recommendation");

                            // Utwórz StringBuilder do przechowywania tekstu
                            StringBuilder stringBuilder = new StringBuilder();

                            // Dodaj informacje o zaleceniach zdrowotnych do StringBuilder
                            stringBuilder.append("General Population Recommendation: ").append(generalPopulationRecommendation).append("<br>");

                            // Ustawienie tekstu w resultTextView przy użyciu StringBuilder
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                resultTextView.append(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_LEGACY));
                            } else {
                                resultTextView.append(HtmlCompat.fromHtml(stringBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                            }
                        }
                        // Dodaj analogiczne warunki dla pozostałych zaleceń zdrowotnych
                    }

                } catch (JSONException e) {
                    Log.e("ApiRequestTask", "Błąd parsowania JSON: " + e.getMessage());
                }
            } else {
                Log.e("MainActivity5", "resultTextView is null in onPostExecute");
            }
        }

        // Funkcja pomocnicza do określenia koloru na podstawie normy dla substancji zanieczyszczającej
        private String determineColorTagForPollutant(String pollutantName, String concentrationValue) {
            // Tutaj można dodać logikę określającą, jaki kolor ma być przypisany na podstawie norm
            // Przykładowa logika: jeśli wartość przekracza normę, to czerwony, w przeciwnym razie zielony

            double value = Double.parseDouble(concentrationValue);

            switch (pollutantName) {
                case "CO":
                    return value <= 3500 ? "<font color='#008000'>" : "<font color='#FF0000'>";
                case "NO2":
                    return value <= 19 ? "<font color='#008000'>" : "<font color='#FF0000'>";
                case "O3":
                    return value <= 500 ? "<font color='#008000'>" : "<font color='#FF0000'>";
                case "PM10":
                    return value <= 50 ? "<font color='#008000'>" : "<font color='#FF0000'>";
                case "PM2.5":
                    return value <= 25 ? "<font color='#008000'>" : "<font color='#FF0000'>";
                case "SO2":
                    return value <= 13 ? "<font color='#008000'>" : "<font color='#FF0000'>";
                case "NOx":
                    return value <= 21 ? "<font color='#008000'>" : "<font color='#FF0000'>";
                default:
                    return "<font color='#000000'>";
            }
        }

        // Funkcja pomocnicza do określenia koloru dla AQI
        private String determineColorTagForAQI(int aqi) {
            if (aqi <= 50) {
                return "<font color='#008000'>"; // Zielony
            } else if (aqi <= 100) {
                return "<font color='#FFFF00'>"; // Żółty
            } else if (aqi <= 150) {
                return "<font color='#FFA500'>"; // Pomarańczowy
            } else if (aqi <= 200) {
                return "<font color='#FF0000'>"; // Czerwony
            } else if (aqi <= 300) {
                return "<font color='#800080'>"; // Fioletowy
            } else {
                return "<font color='#8B0000'>"; // Brązowy
            }
        }


        private String getFormattedUnits(String units) {
            // Dodaj odpowiednie zamiany dla jednostek
            switch (units) {
                case "MICROGRAMS_PER_CUBIC_METER":
                    return "µg/m³";
                case "PARTS_PER_BILLION":
                    return "ppb";
                // Dodaj więcej przypadków w razie potrzeby
                default:
                    return units;
            }
        }









    }
    private class ApiRequestTask2 extends AsyncTask<LatLng, Void, String> {

        @Override
        protected String doInBackground(LatLng... params) {
            try {
                if (params.length == 0 || params[0] == null) {
                    // Jeśli nie masz danych geokodowania, zwróć odpowiednią wartość lub obsłuż to w inny sposób
                    return "Błąd: Brak danych geokodowania";
                }

                // Zastąp YOUR_API_KEY rzeczywistym kluczem API
                String apiKey = "AIzaSyCmUpVSnRFwnPIq16VNfKkyudamUVPALqU";
                double longitude = params[0].longitude;
                double latitude = params[0].latitude;

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
            if (resultTextView2 != null) {
                try {
                    // Parsowanie JSON
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray dailyInfoArray = jsonObject.getJSONArray("dailyInfo");

                    // Przechodzenie przez listę danych dziennych
                    StringBuilder displayInfo = new StringBuilder();

                    for (int i = 0; i < dailyInfoArray.length(); i++) {
                        JSONObject dailyInfo = dailyInfoArray.getJSONObject(i);

                        // Przechodzenie przez listę informacji o pyłkach (pollenTypeInfo)
                        JSONArray pollenTypeInfoArray = dailyInfo.getJSONArray("pollenTypeInfo");
                        for (int j = 0; j < pollenTypeInfoArray.length(); j++) {
                            JSONObject pollenTypeInfo = pollenTypeInfoArray.getJSONObject(j);
                            String displayName = pollenTypeInfo.optString("displayName", "No DisplayName");
                            boolean inSeason = pollenTypeInfo.optBoolean("inSeason", false);

                            // Konwersja wartości inSeason na "yes" lub "no"
                            String inSeasonString = inSeason ? "yes" : "no";

                            // Dodawanie do StringBuilder
                            displayInfo.append("Pollen Type: ").append(displayName).append("\n");
                            displayInfo.append("In Season: ").append(inSeasonString).append("\n");

                            // Sprawdzenie dostępności healthRecommendations
                            if (pollenTypeInfo.has("healthRecommendations")) {
                                String healthRecommendation = pollenTypeInfo.getJSONArray("healthRecommendations").optString(0, "No Health Recommendation");
                                displayInfo.append("Health Recommendation: ").append(healthRecommendation).append("\n");
                            } else {
                                displayInfo.append("Health Recommendation: -\n");
                            }

                            displayInfo.append("\n");
                        }

                        // Przechodzenie przez listę informacji o roślinach (plantInfo)
                        JSONArray plantInfoArray = dailyInfo.getJSONArray("plantInfo");
                        for (int k = 0; k < plantInfoArray.length(); k++) {
                            JSONObject plantInfo = plantInfoArray.getJSONObject(k);
                            String plantDisplayName = plantInfo.optString("displayName", "No Plant DisplayName");
                            boolean plantInSeason = plantInfo.optBoolean("inSeason", false);

                            // Konwersja wartości inSeason na "yes" lub "no"
                            String plantInSeasonString = plantInSeason ? "yes" : "no";

                            // Dodawanie nazwy rośliny do StringBuilder
                            displayInfo.append("Plant Info: ").append(plantDisplayName).append("\n");
                            displayInfo.append("In Season: ").append(plantInSeasonString).append("\n");

                            // Dodawanie opisu rośliny do StringBuilder
                            JSONObject plantDescription = plantInfo.optJSONObject("plantDescription");
                            if (plantDescription != null) {
                                if (plantDescription.has("type")) {
                                    displayInfo.append("Type: ").append(plantDescription.optString("type", "No Type")).append("\n");
                                }
                                if (plantDescription.has("family")) {
                                    displayInfo.append("Family: ").append(plantDescription.optString("family", "No Family")).append("\n");
                                }


                                // Sprawdzenie dostępności atrybutu "plantDescription" i "value"
                                if (plantDescription.has("value")) {
                                    double value = plantDescription.optDouble("value", Double.NaN);

                                    // Konwersja wartości "value" na String z zabezpieczeniem przed NaN
                                    String valueString = Double.isNaN(value) ? "-" : String.valueOf(value);

                                    displayInfo.append("Plant Value: ").append(valueString).append("\n");
                                } else {
                                    displayInfo.append("Plant Value: -\n");
                                }

                                // Dodawanie pozostałych informacji o roślinie do StringBuilder
                            } else {
                                displayInfo.append("Plant Description: -\n");
                            }

                            displayInfo.append("\n");
                        }
                    }

                    // Ustawienie tekstu w resultTextView2
                    resultTextView2.setText(displayInfo.toString());
                } catch (JSONException e) {
                    Log.e("ApiRequestTask2", "Błąd parsowania JSON: " + e.getMessage());
                }
            } else {
                Log.e("MainActivity3", "resultTextView2 is null in onPostExecute");
            }
        }






    }

}