package customer.glympse.glympse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 11/29/2016.
 */

public class Service implements Serializable{
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String serviceName;
    @SerializedName("image")
    @Expose
    private String serviceImage;

    @SerializedName("hover_image")
    @Expose
    private String serviceSelectedImage;
    @SerializedName("is_default")
    @Expose
    private int isDefault;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getServiceSelectedImage() {
        return serviceSelectedImage;
    }

    public void setServiceSelectedImage(String serviceSelectedImage) {
        this.serviceSelectedImage = serviceSelectedImage;
    }
}
