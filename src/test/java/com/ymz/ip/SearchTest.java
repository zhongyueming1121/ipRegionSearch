package com.ymz.ip;

import com.ymz.ip.model.DataBlock;
import com.ymz.ip.model.IpSearchConstant;
import com.ymz.ip.search.Searcher;
import com.ymz.ip.utils.ByteUtil;

import java.io.*;

/**
 * SearchTest
 * @author ymz
 * @date 2019/12/12 17:01
 */
public class SearchTest implements IpSearchConstant {
    public static void main(String[] args) throws IOException {
        System.out.println(Searcher.memorySearch("001.000.016.000"));
        search3();
    }

    /**
     * accuracy check
     */
    public static void search3() {
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(ByteUtil.getPath(IP_MERGE)));
            BufferedWriter bwr = new BufferedWriter(new FileWriter(ByteUtil.getPath(ERROR_LOG)));
            int errCount = 0;
            int lineCount = 0;
            String str = null;
            long sTime = System.currentTimeMillis();
            while ((str = bfr.readLine()) != null) {
                StringBuffer line = new StringBuffer(str);
                int firstIdx = line.indexOf("|");
                String firstIp = line.substring(0, firstIdx);
                line = new StringBuffer(line.substring(firstIdx + 1));
                int secondIdx = line.indexOf("|");
                String sourceRegion = line.substring(secondIdx + 1);

                //System.out.println("+---[Info]: Step1, search for first IP: "+first_ip);
                DataBlock fdata = Searcher.memorySearch(firstIp);
                if (fdata != null) {
                    if (fdata.getRegion().equals(sourceRegion)) {
                        lineCount++;
                        //System.out.println("success:" + lineCount);
                    } else {
                        System.out.println("[Error]: Search first IP failed, DB region = " + fdata.getRegion());
                        bwr.write("[Source]: Region: " + sourceRegion);
                        bwr.newLine();
                        bwr.write("[Source]: First Ip: " + firstIp);
                        bwr.newLine();
                        bwr.write("[DB]: Region: " + fdata.getRegion());
                        bwr.newLine();
                        bwr.flush();
                        errCount++;
                    }
                } else {
                    System.out.println("[Error]: First Ip: " + firstIp);
                    System.out.println("lineCount:" + lineCount);
                    break;
                }
            }
            long eTime = System.currentTimeMillis();

            bwr.close();
            bfr.close();
            System.out.println("+---Done, search complished");
            System.out.println("+---Statistics, Error count = " + errCount
                    + ", Total line = " + lineCount
                    + ", Fail ratio = " + ((float) (errCount / lineCount)) * 100 + "%");
            System.out.println("+---Cost time: " + (eTime - sTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
