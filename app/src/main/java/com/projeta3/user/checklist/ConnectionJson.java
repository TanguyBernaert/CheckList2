package com.projeta3.user.checklist;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import static org.json.JSONObject.NULL;

public class ConnectionJson extends AsyncTask<String, Void, String> {
    private PropertyChangeSupport ptChSupport = new PropertyChangeSupport(this);
    String server_response;

    @Override
    protected String doInBackground(String... strings) {
        try
        {
            JSONObject json = new JSONObject();
            Log.d("RETOUR ", get(strings[0],"UPD",json));

            ptChSupport.firePropertyChange("GET-ALL", NULL , parse(get(strings[0],"ALL",json)));
            List<Product> listProduct = parse(get(strings[0],"ALL",json));
            for (int i=0;i<listProduct.size();i++){
                Log.d("PRODUIT ", listProduct.get(i).name()+" "+listProduct.get(i).id()+"\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String get(String url, String commande, JSONObject pdt) throws IOException, JSONException {

        InputStream is = null;

            /*json.put("pdt", pdt);
            Log.v("RETOUR ",URLEncoder.encode(pdt.toString(), "utf-8"));
         /*   final HttpURLConnection conn = (HttpURLConnection)
                    new URL(url+"?cmd="+commande+"&pdt="+ URLEncoder.encode(pdt.toString(), "utf-8")).openConnection();*/
            final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            Log.e("TEST",conn.toString());

        // Starts the query
            conn.connect();
            is = conn.getInputStream();
            // Read the InputStream and save it in a string


            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            if (is != NULL) {
                is.close();
            }
        return readIt(is);
    }

    private String readIt(InputStream is) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        Log.e("TEST",Integer.toString(r.read()));
        while ((line = r.readLine()) != null) {
            response.append(line).append('\n');
            if (!r.ready()) {
                break;
            }
        }
        return response.toString();
    }

    private List<Product> parse(final String json) {
        try {
            final List<Product> products = new ArrayList<>();
            final JSONArray jProductArray = new JSONArray(json);
            for (int i = 0; i < jProductArray.length(); i++) {
                products.add(new Product(jProductArray.optJSONObject(i)));
            }
            return products;
        } catch (JSONException e) {
            Log.v("TAG","[JSONException] e : " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e("Response", "" + server_response);
    }
    public void addPropertyChangeListener(PropertyChangeListener l){
        ptChSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l){
        ptChSupport.removePropertyChangeListener(l);
    }
}
