package com.ymz.ip.search;

import com.alibaba.fastjson.JSONArray;
import com.ymz.ip.model.DataBlock;
import com.ymz.ip.model.IpSearchConstant;
import com.ymz.ip.utils.ByteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.RamUsageEstimator;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Searcher
 *
 * @author ymz
 * @date 2019-12-16 02:13
 **/
@Slf4j
public class Searcher implements IpSearchConstant {
    private static final Object LOCK = new Object();
    private static boolean memory_mode_load = false;
    private static boolean firstSearch = true;
    /**
     * for memory mode
     */
    private static int[] ipSegments;
    private static int[] ipRegionPtr;
    private static short[] ipRegionLen;

    /**
     * for memory mode
     * the original db binary string
     */
    private static byte[] dataRegion = null;

    public static DataBlock memorySearch(long tagip) {
        int ip = (int) tagip;
        // load data
        if (!memory_mode_load) {
            synchronized (LOCK) {
                if (!memory_mode_load) {
                    loadFileToMemoryMode();
                }
            }
        }

        if (ipSegments == null || ipSegments.length == 0) {
            throw new IllegalArgumentException("initialization failed...");
        }

        int index = binarySearch(ipSegments, 0, ipSegments.length - 1, ip);

        if (index == -1) {
            return null;
        }
        int dataPtr = ipRegionPtr[index >> 1];
        short len = ipRegionLen[index >> 1];
        String region = new String(dataRegion, dataPtr, len, StandardCharsets.UTF_8).trim();

        //---------------
        // used internal memory
        if (firstSearch && devDebug) {
            long sizeOfCollection1 = RamUsageEstimator.sizeOf(ipRegionPtr);
            long sizeOfCollection2 = RamUsageEstimator.sizeOf(dataRegion);
            long sizeOfCollection3 = RamUsageEstimator.sizeOf(ipRegionPtr);
            long sizeOfCollection4 = RamUsageEstimator.sizeOf(ipRegionLen);
            String humanReadableUnits1 = RamUsageEstimator.humanReadableUnits(sizeOfCollection1);
            String humanReadableUnits2 = RamUsageEstimator.humanReadableUnits(sizeOfCollection2);
            String humanReadableUnits3 = RamUsageEstimator.humanReadableUnits(sizeOfCollection3);
            String humanReadableUnits4 = RamUsageEstimator.humanReadableUnits(sizeOfCollection4);
            System.out.println("ipRegions humanSizeOf:" + humanReadableUnits1);
            System.out.println("dbBinStr humanSizeOf:" + humanReadableUnits2);
            System.out.println("ipRegions humanSizeOf:" + humanReadableUnits3);
            System.out.println("ipRegionLen humanSizeOf:" + humanReadableUnits4);
            firstSearch = false;
        }
        //--------------
        return new DataBlock(region, dataPtr);
    }

    public static DataBlock memorySearch(String ip) {
        return memorySearch(ByteUtil.ipToLong(ip));
    }

    private static int binarySearch(int[] arr, int low, int high, long searchNumber) {
        int mid;
        while (low <= high) {
            mid = (low + high) >> 1;
            if (arr[mid] > searchNumber) {
                high = mid - 1;
            } else if (arr[mid] < searchNumber) {
                low = mid + 1;
            } else {
                return mid;
            }
        }
        if (low > arr.length - 1 || high < 0) {
            return -1;
        }
        return high;
    }

    /**
     * load data into memory
     */
    private static void loadFileToMemoryMode() {
        RandomAccessFile raf = null;
        try {
            long sTime = System.currentTimeMillis();
            raf = new RandomAccessFile(new File(ByteUtil.getPath(SEARCH_DB)), "r");
            byte[] headBlock = new byte[HEAD_BLOCK_LENGTH];
            raf.seek(0L);
            raf.readFully(headBlock, 0, HEAD_BLOCK_LENGTH);
            // head block
            long dataEndPtr = ByteUtil.get32Long(headBlock, 0);
            long ipSegmentsEndPrt = ByteUtil.get32Long(headBlock, 4);
            // length
            int dataLen = (int) dataEndPtr;
            // container
            dataRegion = new byte[dataLen];
            byte[] searchInfoBytes = new byte[(int) (ipSegmentsEndPrt - dataEndPtr)];
            // read file
            raf.seek(0);
            raf.readFully(dataRegion, 0, dataLen);
            raf.seek(dataEndPtr);
            raf.readFully(searchInfoBytes, 0, searchInfoBytes.length);
            // deserialization
            List<ArrayList> jsonArray = JSONArray.parseArray(new String(searchInfoBytes, StandardCharsets.UTF_8), ArrayList.class);
            ArrayList arrayList0 = jsonArray.get(0);
            ArrayList arrayList1 = jsonArray.get(1);
            ArrayList arrayList2 = jsonArray.get(2);
            ipSegments = new int[arrayList0.size()];
            ipRegionPtr = new int[arrayList1.size()];
            ipRegionLen = new short[arrayList2.size()];
            for (int i = 0; i < arrayList0.size(); i++) {
                ipSegments[i] = (int) arrayList0.get(i);
            }
            for (int i = 0; i < arrayList1.size(); i++) {
                ipRegionPtr[i] = (int) arrayList1.get(i);
                ipRegionLen[i] = (short) (int) arrayList2.get(i);
            }
            long eTime = System.currentTimeMillis();
            log.info("load file cost time: " + (eTime - sTime) + "ms");
            memory_mode_load = true;
        } catch (IOException o) {
            throw new RuntimeException("load file error.", o);
        } finally {
            ByteUtil.ezIOClose(raf);
        }
    }
}
