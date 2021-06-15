package io.tackle.applicationinventory.entities;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.tackle.commons.annotations.CheckType;
import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "application_import")
public class ApplicationImport extends AbstractEntity {
    private String recordType1;
    private String applicationName;
    private String description;
    private String comments;
    private String businessService;
    private String tagType1;
    private String tag1;
    private String tagType2;
    private String tag2;
    private String tagType3;
    private String tag3;
    private String tagType4;
    private String tag4;
  /**  private String tagType5;
    private String tag5;
    private String tagType6;
    private String tag6;
    private String tagType7;
    private String tag7;
    private String tagType8;
    private String tag8;
    private String tagType9;
    private String tag9;
    private String tagType10;
    private String tag10;
    private String tagType11;
    private String tag11;
    private String tagType12;
    private String tag12;
    private String tagType13;
    private String tag13;
    private String tagType14;
    private String tag14;
    private String tagType15;
    private String tag15;
    private String tagType16;
    private String tag16;
    private String tagType17;
    private String tag17;
    private String tagType18;
    private String tag18;
    private String tagType19;
    private String tag19;
    private String tagType20;
    private String tag20; */
    private String errorMessage;
    @Filterable(check = CheckType.EQUAL)
    public Boolean isValid = true;
    @Filterable
    public String filename;


/**
    private Map<String, String> tag = new HashMap<>();

    @JsonAnySetter()
    public void setTag(String key, Object value) {
        this.tag = (Map<String, String>) value;
    }


    public Map<String,String> getTag()
    {
        return tag;
    }*/

    public ApplicationImport()
    {

    }

    public String getRecordType1() {
        return recordType1;
    }
    @JsonSetter("Record Type 1")
    public void setRecordType1(String recordType1) {
        this.recordType1 = recordType1;
    }

    public String getApplicationName() {
        return applicationName;
    }

    @JsonSetter("Application Name")
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getDescription() {
        return description;
    }

    @JsonSetter("Description")
    public void setDescription(String description) {
        this.description = description;
    }

    public String getComments() {
        return comments;
    }

    @JsonSetter("Comments")
    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getBusinessService() {
        return businessService;
    }

    @JsonSetter("Business Service")
    public void setBusinessService(String businessService) {
        this.businessService = businessService;
    }

    public String getTagType1() {
        return tagType1;
    }

    @JsonSetter("Tag Type 1")
    public void setTagType1(String tagType1) {
        this.tagType1 = tagType1;
    }

    public String getTag1() {
        return tag1;
    }

    @JsonSetter("Tag 1")
    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTagType2() {
        return tagType2;
    }

    @JsonSetter("Tag Type 2")
    public void setTagType2(String tagType2) {
        this.tagType2 = tagType2;
    }

    public String getTag2() {
        return tag2;
    }

    @JsonSetter("Tag 2")
    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getTagType3() {
        return tagType3;
    }

    @JsonSetter("Tag Type 3")
    public void setTagType3(String tagType3) {
        this.tagType3 = tagType3;
    }

    public String getTag3() {
        return tag3;
    }

    @JsonSetter("Tag 3")
    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    public String getTagType4() {
        return tagType4;
    }

    @JsonSetter("Tag Type 4")
    public void setTagType4(String tagType4) {
        this.tagType4 = tagType4;
    }

    public String getTag4() {
        return tag4;
    }

    @JsonSetter("Tag 4")
    public void setTag4(String tag4) {
        this.tag4 = tag4;
    }

    @Override
    public String toString() {
        return "ApplicationImport [Record Type 1=" + recordType1 +
                ", Application Name=" + applicationName +
                ", Description="+ description +
                ", Comments="+ comments +
                ", Business Service="+ businessService +
                ", Tag Type 1="+ tagType1 +
                ", Tag 1="+ tag1 +
                ", Tag Type 2="+ tagType2 +
                ", Tag 2="+ tag2 +
                ", Tag Type 3="+ tagType3 +
                ", Tag 3="+ tag3 +
                ", Tag Type 4="+ tagType4 +
                ", Tag 4="+ tag4 +
                ", Filename="+ filename +"]";
    }



    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}



