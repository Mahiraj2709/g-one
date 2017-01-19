
package customer.glympse.glympse.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @SerializedName("response_msg")
    @Expose
    private String responseMsg;
    @SerializedName("response_key")
    @Expose
    private String responseKey;
    @SerializedName("response_status")
    @Expose
    private Integer responseStatus;
    @SerializedName("response_data")
    @Expose
    private ResponseData responseData;

    @SerializedName("location")
    @Expose
    private CustomerLocation customerLocation;
    /**
     * 
     * @return
     *     The responseMsg
     */
    public String getResponseMsg() {
        return responseMsg;
    }

    /**
     * 
     * @param responseMsg
     *     The response_msg
     */
    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    /**
     * 
     * @return
     *     The responseStatus
     */
    public Integer getResponseStatus() {
        return responseStatus;
    }

    /**
     * 
     * @param responseStatus
     *     The response_status
     */
    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    /**
     * 
     * @return
     *     The responseData
     */
    public ResponseData getResponseData() {
        return responseData;
    }

    /**
     * 
     * @param responseData
     *     The response_data
     */
    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    public String getResponseKey() {
        return responseKey;
    }

    public void setResponseKey(String responseKey) {
        this.responseKey = responseKey;
    }

    public CustomerLocation getCustomerLocation() {
        return customerLocation;
    }

    public void setCustomerLocation(CustomerLocation customerLocation) {
        this.customerLocation = customerLocation;
    }
}
