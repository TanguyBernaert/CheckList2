package com.projeta3.user.checklist;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Product {
    private final long id;
    private final String name;
    private final String rayon;
    private final int localisation;

    public Product(JSONObject jObject) {
        this.id = jObject.optLong("id");
        this.name = jObject.optString("name");
        this.rayon = jObject.optString("rayon");
        this.localisation = jObject.optInt("localisation");
    }

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String rayon() {
        return rayon;
    }

    public int localisation() {
        return localisation;
    }

    private List parse(final String json) {
        try {
            final List products = new ArrayList<>();
            final JSONArray jProductArray = new JSONArray(json);
            for (int i = 0; i < jProductArray.length(); i++) {
                products.add(new Product(jProductArray.optJSONObject(i)));
            }
            return products;
        } catch (JSONException e) {
            Log.v(TAG, "[JSONException] e : " + e.getMessage());
        }
        return null;
    }
}

