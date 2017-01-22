package com.khalilayache.starcode.utils;

import android.text.TextUtils;
import android.util.Log;

import com.khalilayache.starcode.models.StarWarsChar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author USUARIO
 * @since 21/01/2017.
 */
public final class ApiUtils {

    public static StarWarsChar fetchCharData (String requestURL){

        URL url =  createURL(requestURL);

        String jsonResponse = null;

        try{
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("StarCode ERROR","Problem making the HTTP request", e);
        }

        return extractInfosFromJson(jsonResponse);
    }

    private static URL createURL(String stringURL){
        URL url = null;
        try{
            url = new URL(stringURL);
        }catch (MalformedURLException e) {
            Log.e("StarCode","Problem building the URL", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e("StarCode ERROR","Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e("StarCode ERROR","Problem retrieving the earthquake JSON results", e);
        }
        finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static StarWarsChar extractInfosFromJson(String charJSON){

        if(TextUtils.isEmpty(charJSON)){
            return null;
        }

        StarWarsChar starWarsChar = new StarWarsChar();

        try {
            JSONObject root = new JSONObject(charJSON);
            starWarsChar.setName(root.getString("name"));
            Double height = (root.getDouble("height")/100);
            starWarsChar.setHeight(height.toString());
            starWarsChar.setMass(root.getString("mass"));
            starWarsChar.setHair_color(root.getString("hair_color"));
            starWarsChar.setSkin_color(root.getString("skin_color"));
            starWarsChar.setEye_color(root.getString("eye_color"));
            starWarsChar.setBirth_year(root.getString("birth_year"));
            starWarsChar.setGender(root.getString("gender"));
            starWarsChar.setHomeworld(root.getString("homeworld"));
            starWarsChar.setUrl(root.getString("url"));
            starWarsChar.setDate(formatDate(getTimeNow()));
            starWarsChar.setTime(formatTime(getTimeNow()));

            JSONArray filmsArray = root.getJSONArray("films");
            ArrayList<String> films = new ArrayList<>();
            for(int i = 0; i < filmsArray.length();i++) {
                String film = filmsArray.getString(i);
                films.add(film);
            }
            if (films.size()>0)
                starWarsChar.setFilms(films);

            JSONArray starshipsArray = root.getJSONArray("starships");
            ArrayList<String> starships = new ArrayList<>();
            for(int i = 0; i < starshipsArray.length();i++) {
                String starship = starshipsArray.getString(i);
                starships.add(starship);
            }
            if (starships.size()>0)
                starWarsChar.setStarships(starships);

            JSONArray vehiclesArray = root.getJSONArray("vehicles");
            ArrayList<String> vehicles = new ArrayList<>();
            for(int i = 0; i < vehiclesArray.length();i++) {
                String vehicle = vehiclesArray.getString(i);
                vehicles.add(vehicle);
            }
            if (vehicles.size()>0)
                starWarsChar.setVehicles(vehicles);

            JSONArray speciesArray = root.getJSONArray("species");
            ArrayList<String> species = new ArrayList<>();
            for(int i = 0; i < speciesArray.length();i++) {
                String specie = speciesArray.getString(i);
                species.add(specie);
            }
            if (species.size()>0)
                starWarsChar.setSpecies(species);

            Log.e("StarCode ERROR","Problem parsing the earthquake JSON results");
            /*


            private ArrayList<String> species;*/
            //JSONArray earthquakeArray = root.getJSONArray("features");


        } catch (JSONException e) {
            Log.e("StarCode ERROR","Problem parsing the earthquake JSON results", e);
        }

        return starWarsChar;
    }

    private static Date getTimeNow() {
        Calendar c = Calendar.getInstance();
        return c.getTime();
    }

    private static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    private static String formatTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }
}