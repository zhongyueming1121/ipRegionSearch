package com.ymz.ip.model;

import com.ymz.ip.utils.ByteUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * item index class
 *
 * @author ymz
 */
@Getter
@Setter
public class IndexBlock implements Serializable {
    /**
     * start ip address
     */
    private int startIp;

    /**
     * end ip address
     */
    private int endIp;

    /**
     * file pointer position
     */
    private long dataPtr;

    /**
     * data length
     */
    private short dataLen;

    public IndexBlock() {
    }

    public IndexBlock(int startIp, int endIp, long dataPtr, short dataLen) {
        this.startIp = startIp;
        this.endIp = endIp;
        this.dataPtr = dataPtr;
        this.dataLen = dataLen;
    }

    /**
     * get the bytes for storage
     *
     * @return byte[]
     */
    public byte[] getBytes() {
        /*
         * +------------+-----------+-----------+
         * | 4bytes        | 4bytes    | 6bytes    |
         * +------------+-----------+-----------+
         *  start ip      end ip      4bytes  data ptr + 2bytes  len
         */
        byte[] b = new byte[14];
        // 4bytes start ip
        byte[] startIpBytes = ByteUtil.intToByteArray(startIp);
        // 4bytes end ip
        byte[] endIpBytes = ByteUtil.intToByteArray(endIp);
        // 4bytes data ptr
        byte[] bytesDataPtr = ByteUtil.longTo32ByteArray(dataPtr);
        // 2bytes len
        byte[] bytesDataLen = ByteUtil.shortToByteArray(dataLen);
        // put into b
        ByteUtil.bytesArrPutOffTag(b, 0, startIpBytes);
        ByteUtil.bytesArrPutOffTag(b, 4, endIpBytes);
        ByteUtil.bytesArrPutOffTag(b, 8, bytesDataPtr);
        ByteUtil.bytesArrPutOffTag(b, 12, bytesDataLen);
        return b;
    }

    public String print() {
        return "IndexBlock{" +
                "startIp=" + ByteUtil.integerToIp(startIp) +
                ", endIp=" + ByteUtil.integerToIp(endIp) +
                ", dataPtr=" + dataPtr +
                ", dataLen=" + dataLen +
                '}';
    }
}
