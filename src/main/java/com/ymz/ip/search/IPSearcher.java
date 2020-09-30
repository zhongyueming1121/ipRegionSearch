package com.ymz.ip.search;

import com.alibaba.fastjson.JSONArray;
import com.ymz.ip.model.DataBlock;
import com.ymz.ip.model.IPSearchConstant;
import com.ymz.ip.utils.ByteUtil;
import com.ymz.ip.utils.GZipUtils;
import lombok.extern.slf4j.Slf4j;

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
 * @date 2019-12-16 01:13
 **/
@Slf4j
public class IPSearcher implements IPSearchConstant {
    private static final Object LOCK = new Object();
    private static boolean memory_mode_load = false;
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

    /**
     * memorySearch ip
     *
     * @param targetIp ip
     * @return
     */
    public static DataBlock memorySearch(long targetIp) {
        int ip = (int) targetIp;
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
        return new DataBlock(region, dataPtr);
    }

    /**
     * memorySearch
     *
     * @param ip ip
     * @return
     */
    public static DataBlock memorySearch(String ip) {
        return memorySearch(ByteUtil.ipToLong(ip));
    }

    /**
     * binarySearch
     *
     * @param arr
     * @param low
     * @param high
     * @param searchNumber
     * @return
     */
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
        if (GZIP) {
            GZipUtils.decompress(ByteUtil.getPath(SEARCH_DB + GZipUtils.EXT), false);
        }
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
            deserialization(searchInfoBytes);
            long eTime = System.currentTimeMillis();
            log.info("load file cost time: " + (eTime - sTime) + "ms");
            memory_mode_load = true;
        } catch (IOException o) {
            throw new RuntimeException("load file error.", o);
        } finally {
            ByteUtil.ezIOClose(raf);
            if (GZIP) {
                ByteUtil.fileDel(ByteUtil.getPath(SEARCH_DB));
            }
        }

    }

    /**
     * deserialization
     *
     * @param searchInfoBytes json bytes
     */
    private static void deserialization(byte[] searchInfoBytes) {
        String unzipJsonStr = ByteUtil.unzipString(searchInfoBytes);
        List<ArrayList> jsonArray = JSONArray.parseArray(unzipJsonStr, ArrayList.class);
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
    }
}
