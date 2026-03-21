package org.example.util;

import org.example.model.AddressSuggestion;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AddressSearchService {

    public static List<AddressSuggestion> search(String query) {

        List<AddressSuggestion> results = new ArrayList<>();

        try {

            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);

            String urlString =
                    "https://nominatim.openstreetmap.org/search?q="
                            + encoded
                            + "&format=json"
                            + "&limit=5"
                            + "&countrycodes=br";

            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "JavaFX-App");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );

            StringBuilder response = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null){
                response.append(line);
            }

            JSONArray array = new JSONArray(response.toString());

            for(int i=0;i<array.length();i++){

                JSONObject obj = array.getJSONObject(i);

                String name = obj.getString("display_name");
                double lat = Double.parseDouble(obj.getString("lat"));
                double lon = Double.parseDouble(obj.getString("lon"));

                results.add(new AddressSuggestion(name, lat, lon));
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return results;
    }
}