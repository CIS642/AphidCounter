package cis642.aphidcounter.entity;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by JacobLPruitt on 10/26/2014.
 */
public class Field implements Serializable {
    private String name;
    private String cropType;

    public Field() {
        return;
    }

    public Field(String n, String cT) {
        name = n;
        cropType = cT;

    }

    public void addFieldToList(Field f) {
        return;
    }

    public String name() {

        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Name", name);
            obj.put("CropType", cropType);
        }

        catch(JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
