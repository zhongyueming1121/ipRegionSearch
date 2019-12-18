package com.ymz.ip.test;

import com.ymz.ip.make.DatMaker;
import com.ymz.ip.model.IpSearchConstant;
import com.ymz.ip.utils.ByteUtil;

/**
 * make test
 * @author ymz
 * @date 2019/12/12 17:00
 */
public class MyMakeTest implements IpSearchConstant {
    public static void main(String[] args) {
        DatMaker datMaker = new DatMaker();
        datMaker.make(ByteUtil.getPath(SEARCH_DB), ByteUtil.getPath(IP_MERGE));
    }
}
