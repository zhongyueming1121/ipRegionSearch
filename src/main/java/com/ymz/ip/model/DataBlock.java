package com.ymz.ip.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * data block class
 *
 * @author ymz
 */
@Getter
@Setter
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

    public IPInfoDataVO getIPInfoDataVO() {
        String region = this.getRegion();
        String[] split = region.split("\\|");
        IPInfoDataVO ipInfoDataVO = new IPInfoDataVO();
        ipInfoDataVO.setCountry("0".equals(split[0]) ? "" : split[0]);
        ipInfoDataVO.setProvince("0".equals(split[1]) ? "" : split[1]);
        ipInfoDataVO.setCity("0".equals(split[2]) ? "" : split[2]);
        ipInfoDataVO.setOperators("0".equals(split[3]) ? "" : split[3]);
        return ipInfoDataVO;
    }

}
