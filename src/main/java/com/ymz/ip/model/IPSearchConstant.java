package com.ymz.ip.model;

/**
 * @author ymz
 * @date 2019/12/12 15:28
 */
public interface IPSearchConstant {
    /**
     * don`t edit
     */
    int HEAD_BLOCK_LENGTH = 8;

    String IP_INFO_NEW = "ip_info_new.csv";
    String IP_MERGE = "ip_merge.txt";
    String SEARCH_DB = "search_v1.2.0.db";
    String ERROR_LOG = "error_log.txt";
    /**
     * show dev log
     */
    boolean devDebug = true;
    /**
     * enable gzip
     */
    boolean GZIP = true;
}
