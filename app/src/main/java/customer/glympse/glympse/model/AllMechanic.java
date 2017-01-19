package customer.glympse.glympse.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 12/29/2016.
 */

public class AllMechanic {

    @SerializedName("mechanic")
    public List<Mechanic>  mechanicList;
    public String total_offer;
    public String type;
    public String request_id;
    public String message;

    public class Mechanic implements Serializable{
        public String app_provider_id;
        public String offer_price;
        public String latitude;
        public String longitude;
        public String avg_rate;
        public String offer_id;
    }
}
