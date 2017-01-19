
package customer.glympse.glympse.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;



public class ResponseData {

    @SerializedName("profile")
    @Expose
    private UserInfo userInfo;
    @SerializedName("session_token")
    @Expose
    private String sessionToken;
    @SerializedName("fileuploaderror")
    @Expose
    private Object fileuploaderror;

    @SerializedName("content")
    private String staticContent;


    @SerializedName("providers")
    private List<Provider> allProviders;


    @SerializedName("app_request_id")
    private String app_request_id;

    @SerializedName("service_provider")
    private MechanicDetail serviceProvider;

    @SerializedName("history")
    private List<ServiceHistory> serviceHistory;

    @SerializedName("page")
    private Page page;
    /**
     * 
     * @return
     *     The userInfo
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * 
     * @param userInfo
     *     The user_info
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 
     * @return
     *     The sessionToken
     */
    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * 
     * @param sessionToken
     *     The session_token
     */
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    /**
     * 
     * @return
     *     The fileuploaderror
     */
    public Object getFileuploaderror() {
        return fileuploaderror;
    }

    /**
     * 
     * @param fileuploaderror
     *     The fileuploaderror
     */
    public void setFileuploaderror(Object fileuploaderror) {
        this.fileuploaderror = fileuploaderror;
    }

    public String getStaticContent() {
        return staticContent;
    }

    public void setStaticContent(String staticContent) {
        this.staticContent = staticContent;
    }


    public List<Provider> getAllProviders() {
        return allProviders;
    }

    public void setAllProviders(List<Provider> allProviders) {
        this.allProviders = allProviders;
    }


    public String getApp_request_id() {
        return app_request_id;
    }

    public void setApp_request_id(String app_request_id) {
        this.app_request_id = app_request_id;
    }

    public MechanicDetail getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(MechanicDetail serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public List<ServiceHistory> getServiceHistory() {
        return serviceHistory;
    }

    public void setServiceHistory(List<ServiceHistory> serviceHistory) {
        this.serviceHistory = serviceHistory;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
