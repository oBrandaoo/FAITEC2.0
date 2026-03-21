package org.example.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeocodingService {

    public static double[] getCoordinates(String address) {

        try {

            String urlString =
                    "https://nominatim.openstreetmap.org/search?q="
                            + address.replace(" ", "%20")
                            + "&format=json&limit=1";

            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONArray array = new JSONArray(response.toString());

            if (array.length() > 0) {

                JSONObject obj = array.getJSONObject(0);

                double lat = obj.getDouble("lat");
                double lon = obj.getDouble("lon");

                return new double[]{lat, lon};
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}