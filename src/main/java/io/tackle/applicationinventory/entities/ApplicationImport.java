package io.tackle.applicationinventory.entities;

import com.fasterxml.jackson.annotation.JsonSetter;
import io.tackle.applicationinventory.dto.ImportSummaryDto;
import io.tackle.commons.annotations.CheckType;
import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.NamedNativeQuery;

import javax.persistence.*;
import java.util.Date;

@Entity
@SqlResultSetMapping(
        name="ImportSummaryDtoMapping",
        classes=@ConstructorResult(
                targetClass=ImportSummaryDto.class,
                columns={@ColumnResult(name="filename", type = String.class),
                        @ColumnResult(name="parentid", type = Long.class),
                        @ColumnResult(name="status", type = String.class),
                        @ColumnResult(name="createUser", type = String.class),
                        @ColumnResult(name="createTime", type = Date.class),
                        @ColumnResult(name="validCount", type = Integer.class),
                        @ColumnResult(name="invalidCount", type = Integer.class)}))
@NamedNativeQuery(name = "ApplicationImport.getSummary",
        query = "SELECT i.filename, min(i.createUser) as createuser, min(i.createTime) as createtime, sum(case when i.isValid = true then 1 else 0 end) AS validCount, " +
                "sum(case when i.isValid = false then 1 else 0 end) AS invalidCount  FROM Application_Import i GROUP BY i.filename",
        resultSetMapping = "ImportSummaryDtoMapping")
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
    private String tagType5;
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
    private String tag20; 
    private String errorMessage;
    @Filterable(check = CheckType.EQUAL)
    public Boolean isValid = true;
    @Filterable
    public String filename;

    private String status;
    private Long parentId;

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
    public String getTagType5() {
        return tagType5;
    }

    @JsonSetter("Tag Type 5")
    public void setTagType5(String tagType5) {
        this.tagType5 = tagType5;
    }

    public String getTag5() {
        return tag5;
    }

    @JsonSetter("Tag 5")
    public void setTag5(String tag5) {
        this.tag5 = tag5;
    }

    public String getTagType6() {
        return tagType6;
    }

    @JsonSetter("Tag Type 6")
    public void setTagType6(String tagType6) {
        this.tagType6 = tagType6;
    }

    public String getTag6() {
        return tag6;
    }

    @JsonSetter("Tag 6")
    public void setTag6(String tag6) {
        this.tag6 = tag6;
    }

    public String getTagType7() {
        return tagType7;
    }

    @JsonSetter("Tag Type 7")
    public void setTagType7(String tagType7) {
        this.tagType7 = tagType7;
    }

    public String getTag7() {
        return tag7;
    }

    @JsonSetter("Tag 7")
    public void setTag7(String tag7) {
        this.tag7 = tag7;
    }

    public String getTagType8() {
        return tagType8;
    }

    @JsonSetter("Tag Type 8")
    public void setTagType8(String tagType8) {
        this.tagType8 = tagType8;
    }

    public String getTag8() {
        return tag8;
    }

    @JsonSetter("Tag 8")
    public void setTag8(String tag8) {
        this.tag8 = tag8;
    }

    public String getTagType9() {
        return tagType9;
    }

    @JsonSetter("Tag Type 9")
    public void setTagType9(String tagType9) {
        this.tagType9 = tagType9;
    }

    public String getTag9() {
        return tag9;
    }

    @JsonSetter("Tag 9")
    public void setTag9(String tag9) {
        this.tag9 = tag9;
    }

    public String getTagType10() {
        return tagType10;
    }

    @JsonSetter("Tag Type 10")
    public void setTagType10(String tagType10) {
        this.tagType10 = tagType10;
    }

    public String getTag10() {
        return tag10;
    }

    @JsonSetter("Tag 10")
    public void setTag10(String tag10) {
        this.tag10 = tag10;
    }

    public String getTagType11() {
        return tagType11;
    }

    @JsonSetter("Tag Type 11")
    public void setTagType11(String tagType11) {
        this.tagType11 = tagType11;
    }

    public String getTag11() {
        return tag11;
    }

    @JsonSetter("Tag 11")
    public void setTag11(String tag11) {
        this.tag11 = tag11;
    }

    public String getTagType12() {
        return tagType12;
    }

    @JsonSetter("Tag Type 12")
    public void setTagType12(String tagType12) {
        this.tagType12 = tagType12;
    }

    public String getTag12() {
        return tag12;
    }

    @JsonSetter("Tag 12")
    public void setTag12(String tag12) {
        this.tag12 = tag12;
    }
    
    public String getTagType13() {
        return tagType13;
    }

    @JsonSetter("Tag Type 13")
    public void setTagType13(String tagType13) {
        this.tagType13 = tagType13;
    }

    public String getTag13() {
        return tag13;
    }

    @JsonSetter("Tag 13")
    public void setTag13(String tag13) {
        this.tag13 = tag13;
    }

    public String getTagType14() {
        return tagType14;
    }

    @JsonSetter("Tag Type 14")
    public void setTagType14(String tagType14) {
        this.tagType14 = tagType14;
    }

    public String getTag14() {
        return tag14;
    }

    @JsonSetter("Tag 14")
    public void setTag14(String tag14) {
        this.tag14 = tag14;
    }

    public String getTagType15() {
        return tagType15;
    }

    @JsonSetter("Tag Type 15")
    public void setTagType15(String tagType15) {
        this.tagType15 = tagType15;
    }

    public String getTag15() {
        return tag15;
    }

    @JsonSetter("Tag 15")
    public void setTag15(String tag15) {
        this.tag15 = tag15;
    }

    public String getTagType16() {
        return tagType16;
    }

    @JsonSetter("Tag Type 16")
    public void setTagType16(String tagType16) {
        this.tagType16 = tagType16;
    }

    public String getTag16() {
        return tag16;
    }

    @JsonSetter("Tag 16")
    public void setTag16(String tag16) {
        this.tag16 = tag16;
    }

    public String getTagType17() {
        return tagType17;
    }

    @JsonSetter("Tag Type 17")
    public void setTagType17(String tagType17) {
        this.tagType17 = tagType17;
    }

    public String getTag17() {
        return tag17;
    }

    @JsonSetter("Tag 17")
    public void setTag17(String tag17) {
        this.tag17 = tag17;
    }

    public String getTagType18() {
        return tagType18;
    }

    @JsonSetter("Tag Type 18")
    public void setTagType18(String tagType18) {
        this.tagType18 = tagType18;
    }

    public String getTag18() {
        return tag18;
    }

    @JsonSetter("Tag 18")
    public void setTag18(String tag18) {
        this.tag18 = tag18;
    }

    public String getTagType19() {
        return tagType19;
    }

    @JsonSetter("Tag Type 19")
    public void setTagType19(String tagType19) {
        this.tagType19 = tagType19;
    }

    public String getTag19() {
        return tag19;
    }

    @JsonSetter("Tag 19")
    public void setTag19(String tag19) {
        this.tag19 = tag19;
    }

    public String getTagType20() {
        return tagType20;
    }

    @JsonSetter("Tag Type 20")
    public void setTagType20(String tagType20) {
        this.tagType20 = tagType20;
    }

    public String getTag20() {
        return tag20;
    }

    @JsonSetter("Tag 20")
    public void setTag20(String tag20) {
        this.tag20 = tag20;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}



