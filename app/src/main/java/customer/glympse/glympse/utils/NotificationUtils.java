package customer.glympse.glympse.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by admin on 12/30/2016.
 */

public class NotificationUtils {

    public String getData(Map<String,String> remoteMessage) {
        String payLoad = "";
        try {
            payLoad = convertMapToJSON(remoteMessage).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return payLoad;
    }

    private JSONObject convertMapToJSON(Map<String,String> requestMap) throws JSONException {
        JSONObject finalJSON = new JSONObject();
        for (Map.Entry<String, String> entry : requestMap.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            if (entry.getValue().charAt(0) == '{') {
                //this is json object, get json object and replace
                String jsonObjectString = entry.getValue();
                JSONObject jsonObject = new JSONObject(jsonObjectString);
                finalJSON.put(entry.getKey(),jsonObject);
            } else if (entry.getValue().charAt(0) == '[') {
                //json array
                String jsonArrayString = entry.getValue();
                JSONArray jsonArray = new JSONArray(jsonArrayString);
                finalJSON.put(entry.getKey(),jsonArray);
            } else {
                //string
                finalJSON.put(entry.getKey(),entry.getValue());
            }
        }
        Log.i("NOTIFICATION_JSON",finalJSON.toString());
        return finalJSON;
    }
}
