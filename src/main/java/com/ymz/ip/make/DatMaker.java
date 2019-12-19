package com.ymz.ip.make;

import com.alibaba.fastjson.JSONArray;
import com.ymz.ip.model.DataBlock;
import com.ymz.ip.model.IndexBlock;
import com.ymz.ip.model.IpSearchConstant;
import com.ymz.ip.utils.ByteUtil;
import com.ymz.ip.utils.GZipUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * dbFile maker
 *
 * @author ymz
 * @date 2019-12-16 00:51
 **/
@Slf4j
public class DatMaker implements IpSearchConstant {
    private LinkedList<IndexBlock> indexPool = new LinkedList<>();
    /**
     * region and data ptr mapping data
     */
    private HashMap<String, DataBlock> regionPtrPool = new HashMap<>();

    /**
     * make the Db file
     *
     * @param tagDbFilePath
     * @param srcFilePath
     */
    public void make(String tagDbFilePath, String srcFilePath) {
        log.info("|--tagDbFilePath: " + tagDbFilePath);
        log.info("|--srcFilePath: " + srcFilePath);
        System.out.println();
        BufferedReader ipReader = null;
        RandomAccessFile raf = null;
        try {
            // bdFile
            File tagDbFile = ByteUtil.checkFileAndNew(tagDbFilePath);
            File srcIpFile = new File(srcFilePath);
            log.info("+-Try to load the file ...");
            log.info("|--[Ok]");
            ipReader = new BufferedReader(new FileReader(srcIpFile));
            raf = new RandomAccessFile(tagDbFile, "rw");
            //init the db file
            raf.seek(0L);
            //store the serialized object file pointer
            raf.write(new byte[HEAD_BLOCK_LENGTH]);
            log.info("+-Db file initialized.");
            log.info("+-Try to write the data blocks ... ");
            String line = null;
            int count = 0;
            while ((line = ipReader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                if (line.charAt(0) == '#') {
                    continue;
                }
                //1. get the start ip
                int sIdx = 0, eIdx = 0;
                eIdx = line.indexOf('|', sIdx + 1);
                if (eIdx == -1) {
                    continue;
                }
                String startIp = line.substring(sIdx, eIdx);
                //2. get the end ip
                sIdx = eIdx + 1;
                eIdx = line.indexOf('|', sIdx + 1);
                if (eIdx == -1) {
                    continue;
                }
                String endIp = line.substring(sIdx, eIdx);
                //3. get the region
                sIdx = eIdx + 1;
                String region = line.substring(sIdx);
                addDataBlock(raf, startIp, endIp, region);
                count++;
            }
            log.info("|--Data block flushed!");
            log.info("|--Reader lines: " + count);
            log.info("|--Data file pointer: " + raf.getFilePointer() + "\n");

            int[] ipSegments = new int[indexPool.size() * 2];
            int[] ipRegionPtr = new int[indexPool.size()];
            short[] ipRegionLen = new short[indexPool.size()];

            int index = 0;
            int ipRegionPtrIndex = 0;
            int ipRegionLenIndex = 0;
            for (IndexBlock block : indexPool) {
                ipSegments[index++] = block.getStartIp();
                ipSegments[index++] = block.getEndIp();
                ipRegionPtr[ipRegionPtrIndex++] = (int) block.getDataPtr();
                ipRegionLen[ipRegionLenIndex++] = block.getDataLen();
            }
            // serialize ipsegments
            List<Object> list = new ArrayList<>(3);
            list.add(ipSegments);
            list.add(ipRegionPtr);
            list.add(ipRegionLen);
            byte[] searchInfoBytes = JSONArray.toJSONBytes(list);
            // data end prt
            long dataEndPrt = raf.getFilePointer();
            log.info("+--Try to write searchInfo block ... ");
            raf.write(searchInfoBytes);
            long ipSegmentsEndPrt = raf.getFilePointer();
            log.info("|--[Ok]");
            // head block
            log.info("+-Try to write head block ... ");
            byte[] headBlockBytes = new byte[HEAD_BLOCK_LENGTH];
            ByteUtil.write32Long(headBlockBytes, 0, dataEndPrt);
            ByteUtil.write32Long(headBlockBytes, 4, ipSegmentsEndPrt);
            raf.seek(0L);
            raf.write(headBlockBytes);
            raf.close();
            raf = null;
            log.info("|--[Ok]");
            if (GZIP) {
                log.info("+--Try to gzip ... ");
                GZipUtils.compress(tagDbFilePath,true);
                log.info("|--[Ok]");
            }
            //print the copyright and the release timestamp info
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String copyright = "Created by ymz at " + dateFormat.format(cal.getTime());
            log.info("|--[copyright] " + copyright);
            log.info("|--[Ok]");
        } catch (Exception e) {
            log.error("make error.", e);
        } finally {
            ByteUtil.ezIOClose(ipReader);
            ByteUtil.ezIOClose(raf);
        }
    }

    /**
     * internal method to add a new data block record
     *
     * @param raf
     * @param startIp
     * @param endIp
     * @param region  data
     */
    private void addDataBlock(RandomAccessFile raf, String startIp, String endIp, String region) {
        try {
            byte[] data = region.getBytes(StandardCharsets.UTF_8);
            long dataPtr = 0;
            //check region ptr pool first
            if (regionPtrPool.containsKey(region)) {
                DataBlock dataBlock = regionPtrPool.get(region);
                dataPtr = dataBlock.getDataPtr();
            } else {
                //This method returns the offset from the beginning of the file, in bytes
                dataPtr = raf.getFilePointer();
                raf.write(data);
                regionPtrPool.put(region, new DataBlock(region, dataPtr));
            }
            //add the data index blocks
            IndexBlock ib = new IndexBlock(ByteUtil.ipToInteger(startIp),
                    ByteUtil.ipToInteger(endIp), dataPtr, (short) data.length);
            indexPool.add(ib);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
