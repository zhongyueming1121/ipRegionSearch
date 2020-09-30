package com.ymz.ip.utils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP tools
 */
public abstract class GZipUtils {

    private static final int BUFFER = 1024;
    public static final String EXT = ".gz";

    /**
     * data compression
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] compress(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 压缩  
        try {
            compress(bais, baos);
            byte[] output = baos.toByteArray();
            baos.flush();
            baos.close();
            bais.close();
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ByteUtil.ezIOClose(baos);
            ByteUtil.ezIOClose(bais);
        }
        return null;

    }

    /**
     * file compression
     *
     * @param file
     * @throws Exception
     */
    public static void compress(File file) throws Exception {
        compress(file, true);
    }

    /**
     * file compression
     *
     * @param file
     * @param delete whether to delete the original file
     * @throws Exception
     */
    public static void compress(File file, boolean delete) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(file.getPath() + EXT);
            compress(fis, fos);
            fis.close();
            fos.flush();
            fos.close();

            if (delete) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ByteUtil.ezIOClose(fis);
            ByteUtil.ezIOClose(fos);
        }

    }

    /**
     * data compression
     *
     * @param is
     * @param os
     * @throws Exception
     */
    public static void compress(InputStream is, OutputStream os) {

        GZIPOutputStream gos = null;
        try {
            gos = new GZIPOutputStream(os);
            int count;
            byte[] data = new byte[BUFFER];
            while ((count = is.read(data, 0, BUFFER)) != -1) {
                gos.write(data, 0, count);
            }
            gos.finish();
            gos.flush();
            gos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ByteUtil.ezIOClose(gos);
        }
    }

    /**
     * file compression
     *
     * @param path
     * @throws Exception
     */
    public static void compress(String path) {
        compress(path, true);
    }

    /**
     * file compression
     *
     * @param path
     * @param delete whether to delete the original file
     * @throws Exception
     */
    public static void compress(String path, boolean delete) {
        File file = new File(path);
        compress(file, delete);
    }

    /**
     * data decompression
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] decompress(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            decompress(bais, baos);
            data = baos.toByteArray();
            baos.flush();
            baos.close();
            bais.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ByteUtil.ezIOClose(baos);
            ByteUtil.ezIOClose(bais);
        }
        return null;

    }

    /**
     * file extraction
     *
     * @param file
     * @throws Exception
     */
    public static void decompress(File file) {
        decompress(file, true);
    }

    /**
     * file extraction
     *
     * @param file
     * @param delete whether to delete the original file
     * @throws Exception
     */
    public static void decompress(File file, boolean delete) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(file.getPath().replace(EXT, ""));
            decompress(fis, fos);
            fis.close();
            fos.flush();
            fos.close();
            if (delete) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ByteUtil.ezIOClose(fis);
            ByteUtil.ezIOClose(fos);
        }


    }

    /**
     * data decompression
     *
     * @param is
     * @param os
     * @throws Exception
     */
    public static void decompress(InputStream is, OutputStream os) {
        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(is);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = gis.read(data, 0, BUFFER)) != -1) {
                os.write(data, 0, count);
            }
            gis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ByteUtil.ezIOClose(gis);
        }

    }

    /**
     * file extraction
     *
     * @param path
     * @throws Exception
     */
    public static void decompress(String path) {
        decompress(path, true);
    }

    /**
     * file extraction
     *
     * @param path
     * @param delete whether to delete the original file
     * @throws Exception
     */
    public static void decompress(String path, boolean delete) {
        File file = new File(path);
        decompress(file, delete);
    }

} 