package io.tackle.applicationinventory.dto;


import java.util.Date;

public class ImportSummaryDto {
    public String filename;
    public String createUser;
    public Date createTime;
    public int validCount;
    public int invalidCount;

    public ImportSummaryDto(String filename, String createUser, Date createTime, int validCount, int invalidCount)
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

    public Date getCreateTime() {
        return createTime;
    }

    public int getValidCount() {
        return validCount;
    }

    public int getInvalidCount() {
        return invalidCount;
    }
}
