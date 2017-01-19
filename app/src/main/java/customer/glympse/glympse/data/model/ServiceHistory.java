
package customer.glympse.glympse.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceHistory {

    @SerializedName("app_request_id")
    @Expose
    private String appRequestId;
    @SerializedName("appuser_id")
    @Expose
    private String appuserId;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("request_date")
    @Expose
    private String requestDate;
    @SerializedName("address_lat")
    @Expose
    private String addressLat;
    @SerializedName("address_long")
    @Expose
    private String addressLong;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("added_date")
    @Expose
    private String addedDate;
    @SerializedName("start_journey_date_time")
    @Expose
    private Object startJourneyDateTime;
    @SerializedName("arrived_date_time")
    @Expose
    private Object arrivedDateTime;
    @SerializedName("close_date_time")
    @Expose
    private Object closeDateTime;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("request_accept_date")
    @Expose
    private String requestAcceptDate;
    @SerializedName("service_provider_id")
    @Expose
    private String serviceProviderId;
    @SerializedName("accepted_dt")
    @Expose
    private String acceptedDt;
    @SerializedName("cancel_status")
    @Expose
    private String cancelStatus;
    @SerializedName("cancel_dt")
    @Expose
    private String cancelDt;
    @SerializedName("status_text")
    @Expose
    private String statusText;

    public String getAppRequestId() {
        return appRequestId;
    }

    public void setAppRequestId(String appRequestId) {
        this.appRequestId = appRequestId;
    }

    public String getAppuserId() {
        return appuserId;
    }

    public void setAppuserId(String appuserId) {
        this.appuserId = appuserId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getAddressLat() {
        return addressLat;
    }

    public void setAddressLat(String addressLat) {
        this.addressLat = addressLat;
    }

    public String getAddressLong() {
        return addressLong;
    }

    public void setAddressLong(String addressLong) {
        this.addressLong = addressLong;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public Object getStartJourneyDateTime() {
        return startJourneyDateTime;
    }

    public void setStartJourneyDateTime(Object startJourneyDateTime) {
        this.startJourneyDateTime = startJourneyDateTime;
    }

    public Object getArrivedDateTime() {
        return arrivedDateTime;
    }

    public void setArrivedDateTime(Object arrivedDateTime) {
        this.arrivedDateTime = arrivedDateTime;
    }

    public Object getCloseDateTime() {
        return closeDateTime;
    }

    public void setCloseDateTime(Object closeDateTime) {
        this.closeDateTime = closeDateTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRequestAcceptDate() {
        return requestAcceptDate;
    }

    public void setRequestAcceptDate(String requestAcceptDate) {
        this.requestAcceptDate = requestAcceptDate;
    }

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(String serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public String getAcceptedDt() {
        return acceptedDt;
    }

    public void setAcceptedDt(String acceptedDt) {
        this.acceptedDt = acceptedDt;
    }

    public String getCancelStatus() {
        return cancelStatus;
    }

    public void setCancelStatus(String cancelStatus) {
        this.cancelStatus = cancelStatus;
    }

    public String getCancelDt() {
        return cancelDt;
    }

    public void setCancelDt(String cancelDt) {
        this.cancelDt = cancelDt;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

}
