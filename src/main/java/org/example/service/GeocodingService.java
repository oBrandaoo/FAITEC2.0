package org.example.service;

import org.example.model.Location;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeocodingService {

    private static final String USER_AGENT = "CidadeMelhor/1.0";
    private static final int TIMEOUT_MILLIS = 8000;

    public static Location search(String address) {
        try {
            String query = address + ", Santa Rita do Sapucaí, MG, Brasil";
            String url = "https://nominatim.openstreetmap.org/search"
                    + "?format=jsonv2"
                    + "&limit=1"
                    + "&countrycodes=br"
                    + "&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

            HttpURLConnection connection =
                    (HttpURLConnection) new URL(url).openConnection();
            configure(connection);

            JSONArray results = new JSONArray(readResponse(connection));
            if (results.isEmpty()) {
                return null;
            }

            JSONObject result = results.getJSONObject(0);
            return new Location(
                    result.getDouble("lat"),
                    result.getDouble("lon"),
                    result.optString("display_name", address)
            );
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static Location reverse(double latitude, double longitude) {
        try {
            String url = "https://nominatim.openstreetmap.org/reverse"
                    + "?format=jsonv2"
                    + "&lat=" + latitude
                    + "&lon=" + longitude;

            HttpURLConnection connection =
                    (HttpURLConnection) new URL(url).openConnection();
            configure(connection);

            JSONObject json = new JSONObject(readResponse(connection));
            return new Location(
                    latitude,
                    longitude,
                    json.optString("display_name", "Endereço não encontrado")
            );
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static void configure(HttpURLConnection connection) {
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setConnectTimeout(TIMEOUT_MILLIS);
        connection.setReadTimeout(TIMEOUT_MILLIS);
    }

    private static String readResponse(HttpURLConnection connection) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
        )) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }
}
