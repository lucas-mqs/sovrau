package br.com.sovrau.adapters;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Lucas on 14/11/2016.
 */

public class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
    private ArrayList<String> resultList;
    private static final String TAG = GooglePlacesAutocompleteAdapter.class.getSimpleName();
    private static final String API_KEY = "AIzaSyBQcK9sX8AmJ2LvKAN_yiaW1ATe4KX6BbM";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private long idOrigem;
    private long idDestino;
    private String latitudeOrigem;
    private String longitudeOrigem;
    private String latitudeDestino;
    private String longitudeDestino;

    public long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public long getIdDestino() {
        return idDestino;
    }

    public void setIdDestino(long idDestino) {
        this.idDestino = idDestino;
    }

    public String getLatitudeOrigem() {
        return latitudeOrigem;
    }

    public void setLatitudeOrigem(String latitudeOrigem) {
        this.latitudeOrigem = latitudeOrigem;
    }

    public String getLongitudeOrigem() {
        return longitudeOrigem;
    }

    public void setLongitudeOrigem(String longitudeOrigem) {
        this.longitudeOrigem = longitudeOrigem;
    }

    public String getLatitudeDestino() {
        return latitudeDestino;
    }

    public void setLatitudeDestino(String latitudeDestino) {
        this.latitudeDestino = latitudeDestino;
    }

    public String getLongitudeDestino() {
        return longitudeDestino;
    }

    public void setLongitudeDestino(String longitudeDestino) {
        this.longitudeDestino = longitudeDestino;
    }

    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
    @Override
    public int getCount() {
        return resultList.size();
    }
    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }
    @Override
    public Filter getFilter() {
       Filter filter = new Filter() {
       @Override
       protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:BR");
            sb.append("&language=pt-BR");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            Log.e(TAG, "URL do Request: " + sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }
}
