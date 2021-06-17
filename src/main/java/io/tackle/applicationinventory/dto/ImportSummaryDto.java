package io.tackle.applicationinventory.dto;

public class ImportSummaryDto {
    public String filename;
    public String createUser;
    public String createTime;
    public String validCount;
    public String invalidCount;

    public ImportSummaryDto(String filename, String createUser, String createTime, String validCount, String invalidCount)
    {
        this.filename = filename;
        this.createUser = createUser;
        this.createTime = createTime;
        this.validCount = validCount;
        this.invalidCount = invalidCount;
    }

    public String getFilename() {
        return filename;
    }

    public String getCreateUser() {
        return createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getValidCount() {
        return validCount;
    }

    public String getInvalidCount() {
        return invalidCount;
    }
}
