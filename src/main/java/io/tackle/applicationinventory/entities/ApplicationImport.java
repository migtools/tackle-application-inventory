package io.tackle.applicationinventory.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.tackle.commons.annotations.CheckType;
import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "application_import")
public class ApplicationImport extends AbstractEntity {
    public static final int APP_NAME_MAX_LENGTH = 120;

    private String recordType1;

    @Size(max = APP_NAME_MAX_LENGTH)
    private String applicationName;

    @Size(max = 250)
    private String description;

    @Size(max = 250)
    private String comments;

    @Size(max = 120)
    private String businessService;

    @Size(max = 40)
    private String tagType1;

    @Size(max = 40)
    private String tag1;

    @Size(max = 40)
    private String tagType2;

    @Size(max = 40)
    private String tag2;

    @Size(max = 40)
    private String tagType3;

    @Size(max = 40)
    private String tag3;

    @Size(max = 40)
    private String tagType4;

    @Size(max = 40)
    private String tag4;

    @Size(max = 40)
    private String tagType5;

    @Size(max = 40)
    private String tag5;

    @Size(max = 40)
    private String tagType6;

    @Size(max = 40)
    private String tag6;

    @Size(max = 40)
    private String tagType7;

    @Size(max = 40)
    private String tag7;

    @Size(max = 40)
    private String tagType8;

    @Size(max = 40)
    private String tag8;

    @Size(max = 40)
    private String tagType9;

    @Size(max = 40)
    private String tag9;

    @Size(max = 40)
    private String tagType10;

    @Size(max = 40)
    private String tag10;

    @Size(max = 40)
    private String tagType11;

    @Size(max = 40)
    private String tag11;

    @Size(max = 40)
    private String tagType12;

    @Size(max = 40)
    private String tag12;

    @Size(max = 40)
    private String tagType13;

    @Size(max = 40)
    private String tag13;

    @Size(max = 40)
    private String tagType14;

    @Size(max = 40)
    private String tag14;

    @Size(max = 40)
    private String tagType15;

    @Size(max = 40)
    private String tag15;

    @Size(max = 40)
    private String tagType16;

    @Size(max = 40)
    private String tag16;

    @Size(max = 40)
    private String tagType17;

    @Size(max = 40)
    private String tag17;

    @Size(max = 40)
    private String tagType18;

    @Size(max = 40)
    private String tag18;

    @Size(max = 40)
    private String tagType19;

    @Size(max = 40)
    private String tag19;

    @Size(max = 40)
    private String tagType20;

    @Size(max = 40)
    private String tag20;

    private String errorMessage;

    @Filterable(check = CheckType.EQUAL)
    public Boolean isValid = true;

    @Filterable
    public String filename;

    @ManyToOne(optional = false)
    @JsonIgnore
    @Filterable(filterName = "importSummary.id")
    public ImportSummary importSummary;

    public ApplicationImport() {

    }

    public String getRecordType1() {
        return recordType1;
    }

    @JsonSetter("Record Type 1")
    public void setRecordType1(String recordType1) {
        this.recordType1 = recordType1 != null ? recordType1.trim() : null;
    }

    public String getApplicationName() {
        return applicationName;
    }

    @JsonSetter("Application Name")
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName != null ? applicationName.trim() : null;
    }

    public String getDescription() {
        return description;
    }

    @JsonSetter("Description")
    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    public String getComments() {
        return comments;
    }

    @JsonSetter("Comments")
    public void setComments(String comments) {
        this.comments = comments != null ? comments.trim() : null;
    }

    public String getBusinessService() {
        return businessService;
    }

    @JsonSetter("Business Service")
    public void setBusinessService(String businessService) {
        this.businessService = businessService != null ? businessService.trim() : null;
    }

    public String getTagType1() {
        return tagType1;
    }

    @JsonSetter("Tag Type 1")
    public void setTagType1(String tagType1) {
        this.tagType1 = tagType1 != null ? tagType1.trim() : null;
    }

    public String getTag1() {
        return tag1;
    }

    @JsonSetter("Tag 1")
    public void setTag1(String tag1) {
        this.tag1 = tag1 != null ? tag1.trim() : null;
    }

    public String getTagType2() {
        return tagType2;
    }

    @JsonSetter("Tag Type 2")
    public void setTagType2(String tagType2) {
        this.tagType2 = tagType2 != null ? tagType2.trim() : null;
    }

    public String getTag2() {
        return tag2;
    }

    @JsonSetter("Tag 2")
    public void setTag2(String tag2) {
        this.tag2 = tag2 != null ? tag2.trim() : null;
    }

    public String getTagType3() {
        return tagType3;
    }

    @JsonSetter("Tag Type 3")
    public void setTagType3(String tagType3) {
        this.tagType3 = tagType3 != null ? tagType3.trim() : null;
    }

    public String getTag3() {
        return tag3;
    }

    @JsonSetter("Tag 3")
    public void setTag3(String tag3) {
        this.tag3 = tag3 != null ? tag3.trim() : null;
    }

    public String getTagType4() {
        return tagType4;
    }

    @JsonSetter("Tag Type 4")
    public void setTagType4(String tagType4) {
        this.tagType4 = tagType4 != null ? tagType4.trim() : null;
    }

    public String getTag4() {
        return tag4;
    }

    @JsonSetter("Tag 4")
    public void setTag4(String tag4) {
        this.tag4 = tag4 != null ? tag4.trim() : null;
    }

    public String getTagType5() {
        return tagType5;
    }

    @JsonSetter("Tag Type 5")
    public void setTagType5(String tagType5) {
        this.tagType5 = tagType5 != null ? tagType5.trim() : null;
    }

    public String getTag5() {
        return tag5;
    }

    @JsonSetter("Tag 5")
    public void setTag5(String tag5) {
        this.tag5 = tag5 != null ? tag5.trim() : null;
    }

    public String getTagType6() {
        return tagType6;
    }

    @JsonSetter("Tag Type 6")
    public void setTagType6(String tagType6) {
        this.tagType6 = tagType6 != null ? tagType6.trim() : null;
    }

    public String getTag6() {
        return tag6;
    }

    @JsonSetter("Tag 6")
    public void setTag6(String tag6) {
        this.tag6 = tag6 != null ? tag6.trim() : null;
    }

    public String getTagType7() {
        return tagType7;
    }

    @JsonSetter("Tag Type 7")
    public void setTagType7(String tagType7) {
        this.tagType7 = tagType7 != null ? tagType7.trim() : null;
    }

    public String getTag7() {
        return tag7;
    }

    @JsonSetter("Tag 7")
    public void setTag7(String tag7) {
        this.tag7 = tag7 != null ? tag7.trim() : null;
    }

    public String getTagType8() {
        return tagType8;
    }

    @JsonSetter("Tag Type 8")
    public void setTagType8(String tagType8) {
        this.tagType8 = tagType8 != null ? tagType8.trim() : null;
    }

    public String getTag8() {
        return tag8;
    }

    @JsonSetter("Tag 8")
    public void setTag8(String tag8) {
        this.tag8 = tag8 != null ? tag8.trim() : null;
    }

    public String getTagType9() {
        return tagType9;
    }

    @JsonSetter("Tag Type 9")
    public void setTagType9(String tagType9) {
        this.tagType9 = tagType9 != null ? tagType9.trim() : null;
    }

    public String getTag9() {
        return tag9;
    }

    @JsonSetter("Tag 9")
    public void setTag9(String tag9) {
        this.tag9 = tag9 != null ? tag9.trim() : null;
    }

    public String getTagType10() {
        return tagType10;
    }

    @JsonSetter("Tag Type 10")
    public void setTagType10(String tagType10) {
        this.tagType10 = tagType10 != null ? tagType10.trim() : null;
    }

    public String getTag10() {
        return tag10;
    }

    @JsonSetter("Tag 10")
    public void setTag10(String tag10) {
        this.tag10 = tag10 != null ? tag10.trim() : null;
    }

    public String getTagType11() {
        return tagType11;
    }

    @JsonSetter("Tag Type 11")
    public void setTagType11(String tagType11) {
        this.tagType11 = tagType11 != null ? tagType11.trim() : null;
    }

    public String getTag11() {
        return tag11;
    }

    @JsonSetter("Tag 11")
    public void setTag11(String tag11) {
        this.tag11 = tag11 != null ? tag11.trim() : null;
    }

    public String getTagType12() {
        return tagType12;
    }

    @JsonSetter("Tag Type 12")
    public void setTagType12(String tagType12) {
        this.tagType12 = tagType12 != null ? tagType12.trim() : null;
    }

    public String getTag12() {
        return tag12;
    }

    @JsonSetter("Tag 12")
    public void setTag12(String tag12) {
        this.tag12 = tag12 != null ? tag12.trim() : null;
    }

    public String getTagType13() {
        return tagType13;
    }

    @JsonSetter("Tag Type 13")
    public void setTagType13(String tagType13) {
        this.tagType13 = tagType13 != null ? tagType13.trim() : null;
    }

    public String getTag13() {
        return tag13;
    }

    @JsonSetter("Tag 13")
    public void setTag13(String tag13) {
        this.tag13 = tag13 != null ? tag13.trim() : null;
    }

    public String getTagType14() {
        return tagType14;
    }

    @JsonSetter("Tag Type 14")
    public void setTagType14(String tagType14) {
        this.tagType14 = tagType14 != null ? tagType14.trim() : null;
    }

    public String getTag14() {
        return tag14;
    }

    @JsonSetter("Tag 14")
    public void setTag14(String tag14) {
        this.tag14 = tag14 != null ? tag14.trim() : null;
    }

    public String getTagType15() {
        return tagType15;
    }

    @JsonSetter("Tag Type 15")
    public void setTagType15(String tagType15) {
        this.tagType15 = tagType15 != null ? tagType15.trim() : null;
    }

    public String getTag15() {
        return tag15;
    }

    @JsonSetter("Tag 15")
    public void setTag15(String tag15) {
        this.tag15 = tag15 != null ? tag15.trim() : null;
    }

    public String getTagType16() {
        return tagType16;
    }

    @JsonSetter("Tag Type 16")
    public void setTagType16(String tagType16) {
        this.tagType16 = tagType16 != null ? tagType16.trim() : null;
    }

    public String getTag16() {
        return tag16;
    }

    @JsonSetter("Tag 16")
    public void setTag16(String tag16) {
        this.tag16 = tag16 != null ? tag16.trim() : null;
    }

    public String getTagType17() {
        return tagType17;
    }

    @JsonSetter("Tag Type 17")
    public void setTagType17(String tagType17) {
        this.tagType17 = tagType17 != null ? tagType17.trim() : null;
    }

    public String getTag17() {
        return tag17;
    }

    @JsonSetter("Tag 17")
    public void setTag17(String tag17) {
        this.tag17 = tag17 != null ? tag17.trim() : null;
    }

    public String getTagType18() {
        return tagType18;
    }

    @JsonSetter("Tag Type 18")
    public void setTagType18(String tagType18) {
        this.tagType18 = tagType18 != null ? tagType18.trim() : null;
    }

    public String getTag18() {
        return tag18;
    }

    @JsonSetter("Tag 18")
    public void setTag18(String tag18) {
        this.tag18 = tag18 != null ? tag18.trim() : null;
    }

    public String getTagType19() {
        return tagType19;
    }

    @JsonSetter("Tag Type 19")
    public void setTagType19(String tagType19) {
        this.tagType19 = tagType19 != null ? tagType19.trim() : null;
    }

    public String getTag19() {
        return tag19;
    }

    @JsonSetter("Tag 19")
    public void setTag19(String tag19) {
        this.tag19 = tag19 != null ? tag19.trim() : null;
    }

    public String getTagType20() {
        return tagType20;
    }

    @JsonSetter("Tag Type 20")
    public void setTagType20(String tagType20) {
        this.tagType20 = tagType20 != null ? tagType20.trim() : null;
    }

    public String getTag20() {
        return tag20;
    }

    @JsonSetter("Tag 20")
    public void setTag20(String tag20) {
        this.tag20 = tag20 != null ? tag20.trim() : null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage != null ? errorMessage.trim() : null;
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
        this.filename = filename != null ? filename.trim() : null;
    }


}



