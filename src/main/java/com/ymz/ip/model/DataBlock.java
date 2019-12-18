package com.ymz.ip.model;

import lombok.Data;

import java.io.Serializable;

/**
 * data block class
 *
 * @author ymz
 */
@Data
public class DataBlock implements Serializable {
    /**
     * region address
     */
    private String region;

    /**
     * region filePointer in the db file
     */
    private long dataPtr;

    public DataBlock() {
    }

    public DataBlock(String region, long dataPtr) {
        this.region = region;
        this.dataPtr = dataPtr;
    }

    public DataBlock(String region) {
        this(region, 0);
    }

    @Override
    public String toString() {
        return region;
    }

}
