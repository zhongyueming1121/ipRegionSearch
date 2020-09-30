package com.ymz.ip;


import com.ymz.ip.model.DataBlock;
import com.ymz.ip.model.IPSearchConstant;
import com.ymz.ip.search.IPSearcher;
import com.ymz.ip.utils.ByteUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SearchTest
 *
 * @author ymz
 * @date 2019/12/12 17:01
 */
public class SearchTest implements IPSearchConstant {

    public static void main(String[] args) {
        oneIPTest();
        //searchAllTest(false);
    }

    /**
     * one ip test
     */
    public static void oneIPTest() {
        System.out.println(IPSearcher.memorySearch("001.000.016.000").getIPInfoDataVO().toString());
        System.out.println(IPSearcher.memorySearch("001.014.192.000"));
        System.out.println(IPSearcher.memorySearch("1.14.192.1").getIPInfoDataVO().toString());
    }

    /**
     * check all ip
     *
     * @param borderIp only test border ip
     */
    public static void searchAllTest(boolean borderIp) {
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
                // if not a border IP, add 1 to the first IP
                if (!borderIp && appearNumber(str, firstIp) < 2) {
                    String[] split = firstIp.split("\\.");
                    String s = split[3];
                    int ip4 = Integer.parseInt(s);
                    if (ip4 < 255 && ip4 > 0) {
                        ip4 = ip4 + 1;
                    }
                    firstIp = split[0] + "." + split[1] + "." + split[2] + "." + ip4;
                }

                //System.out.println("+---[Info]: Step1, search for first IP: "+first_ip);
                DataBlock fdata = IPSearcher.memorySearch(ByteUtil.ipToInteger(firstIp));
                if (fdata != null) {
                    if (fdata.getRegion().equals(sourceRegion)) {
                        lineCount++;
                        //System.out.println("success:" + lineCount);
                    } else {
                        System.out.println("[Error]: Search first IP failed, DB region = " + str + "----" + firstIp);
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
                    + ", Total line = " + lineCount);
            System.out.println("+---Cost time: " + (eTime - sTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }
}
