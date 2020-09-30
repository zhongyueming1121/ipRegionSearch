package com.ymz.ip.make;


import com.ymz.ip.model.IPSearchConstant;
import com.ymz.ip.utils.ByteUtil;

/**
 * 一键生成search.db
 * 可以通过配置IpSearchConstant的GZIP来控制是否压缩
 *
 * @author ymz
 * @date 2020/04/22 11:10
 */
public class MakeMain implements IPSearchConstant {

    public static void main(String[] args) {
        oneKeyMake();
    }

    /**
     * 一键生成search.db
     * 可以通过配置IpSearchConstant的GZIP来控制是否压缩
     */
    public static void oneKeyMake() {
        try {
            // 将ip_info_new.csv压缩转换成特定格式
            Adapter.myIpInfo2IpMerge();
            // 使用make()方法生成search.db
            DatMaker datMaker = new DatMaker();
            datMaker.make(ByteUtil.getPath(SEARCH_DB), ByteUtil.getPath(IP_MERGE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
