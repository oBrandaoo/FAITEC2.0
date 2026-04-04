package org.example.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StreetLoader {

    public static List<String> loadStreets() {

        List<String> streets = new ArrayList<>();

        try {

            InputStream is = StreetLoader.class
                    .getResourceAsStream("/ruas.csv");

            if (is == null) {
                throw new RuntimeException("Arquivo ruas.csv não encontrado em resources!");
            }

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(is));

            String line;

            while ((line = reader.readLine()) != null) {
                streets.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return streets;
    }
}