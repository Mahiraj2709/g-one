
package customer.glympse.glympse.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Page {

    @SerializedName("pages_id")
    @Expose
    private String pagesId;
    @SerializedName("pages_name")
    @Expose
    private String pagesName;
    @SerializedName("pages_identifier")
    @Expose
    private String pagesIdentifier;
    @SerializedName("pages_desc")
    @Expose
    private String pagesDesc;
    @SerializedName("meta_title")
    @Expose
    private String metaTitle;
    @SerializedName("meta_keyword")
    @Expose
    private String metaKeyword;
    @SerializedName("meta_description")
    @Expose
    private String metaDescription;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("subject")
    @Expose
    private String subject;

    public String getPagesId() {
        return pagesId;
    }

    public void setPagesId(String pagesId) {
        this.pagesId = pagesId;
    }

    public String getPagesName() {
        return pagesName;
    }

    public void setPagesName(String pagesName) {
        this.pagesName = pagesName;
    }

    public String getPagesIdentifier() {
        return pagesIdentifier;
    }

    public void setPagesIdentifier(String pagesIdentifier) {
        this.pagesIdentifier = pagesIdentifier;
    }

    public String getPagesDesc() {
        return pagesDesc;
    }

    public void setPagesDesc(String pagesDesc) {
        this.pagesDesc = pagesDesc;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaKeyword() {
        return metaKeyword;
    }

    public void setMetaKeyword(String metaKeyword) {
        this.metaKeyword = metaKeyword;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

}
