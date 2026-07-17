package org.example.service;

import org.example.model.Location;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeocodingService {

    private static final String USER_AGENT = "CidadeMelhor/1.0";

    public static Location reverse(double latitude, double longitude) {

        try {

            String url =
                    "https://nominatim.openstreetmap.org/reverse"
                            + "?format=jsonv2"
                            + "&lat=" + latitude
                            + "&lon=" + longitude;

            HttpURLConnection connection =
                    (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestProperty("User-Agent", USER_AGENT);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            );

            StringBuilder response = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject json = new JSONObject(response.toString());

            Location location = new Location();

            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setAddress(json.optString("display_name", "Endereço não encontrado"));

            return location;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }
}
