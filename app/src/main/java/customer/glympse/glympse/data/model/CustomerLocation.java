package customer.glympse.glympse.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 1/16/2017.
 */
public class CustomerLocation {
    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("latitude")
    private Double latitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
