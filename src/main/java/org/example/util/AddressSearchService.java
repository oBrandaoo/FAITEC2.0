package org.example.util;

import org.example.model.AddressSuggestion;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddressSearchService {

    public static List<AddressSuggestion> search(String query) {

        List<AddressSuggestion> results = new ArrayList<>();

        try {

            String urlString =
                    "https://nominatim.openstreetmap.org/search?q="
                            + query.replace(" ", "%20")
                            + "&format=json&limit=5";

            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "JavaFX-App");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
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
                double lat = obj.getDouble("lat");
                double lon = obj.getDouble("lon");

                results.add(new AddressSuggestion(name, lat, lon));
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return results;
    }
}