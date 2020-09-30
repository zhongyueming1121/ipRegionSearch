package com.ymz.ip.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * byte utils
 *
 * @author ymz
 * @date 2019-12-16 00:20
 **/
public class ByteUtil {
    /**
     * put byte with offset
     *
     * @param tagBytes
     * @param offset
     * @param srcBytes
     */
    public static void bytesArrPutOffTag(byte[] tagBytes, int offset, byte[] srcBytes) {
        for (byte srcByte : srcBytes) {
            tagBytes[offset++] = srcByte;
        }
    }

    /**
     * put byte with offset
     *
     * @param tagBytes
     * @param offset
     * @param srcBytes
     */
    public static void bytesArrPutOffSrc(byte[] tagBytes, int offset, byte[] srcBytes) {
        for (int i = 0; i < tagBytes.length; i++) {
            tagBytes[i] = srcBytes[offset++];
        }
    }

    /**
     * get a int from a byte array start from the specifiled offset,
     * it is actually converting bytes to int and then int.
     *
     * @param src
     * @param offset
     * @return
     */
    public static long get32Long(byte[] src, int offset) {
        byte[] bytes = new byte[4];
        bytesArrPutOffSrc(bytes, offset, src);
        return byteArrayToInt(bytes);
    }

    /**
     * get int
     *
     * @param src
     * @param offset
     * @return
     */
    public static int getInt(byte[] src, int offset) {
        byte[] bytes = new byte[4];
        bytesArrPutOffSrc(bytes, offset, src);
        return byteArrayToInt(bytes);
    }

    /**
     * write a 32-bit long to a byte array,
     * it is actually converting long to int and then bytes.
     *
     * @param tag
     * @param offset
     * @param v
     */
    public static void write32Long(byte[] tag, int offset, long v) {
        byte[] bytes = intToByteArray((int) v);
        bytesArrPutOffTag(tag, offset, bytes);
    }

    /**
     * put a 32 bit int value into a 4 byte byte array
     *
     * @param num
     * @return
     */
    public static byte[] intToByteArray(int num) {
        return new byte[]{
                (byte) ((num >> 24) & 0xFF),
                (byte) ((num >> 16) & 0xFF),
                (byte) ((num >> 8) & 0xFF),
                (byte) (num & 0xFF)
        };
    }

    /**
     * convert a 4-byte byte array into an int value
     *
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    /**
     * put a 64 bit long value into a 32 byte byte array
     *
     * @param val
     * @return
     */
    public static byte[] longTo32ByteArray(long val) {
        int num = (int) val;
        return new byte[]{
                (byte) ((num >> 24) & 0xFF),
                (byte) ((num >> 16) & 0xFF),
                (byte) ((num >> 8) & 0xFF),
                (byte) (num & 0xFF)
        };
    }

    /**
     * short to a byte array
     *
     * @param data
     * @return
     */
    public static byte[] shortToByteArray(short data) {
        return new byte[]{
                (byte) (data & 0xff),
                (byte) ((data & 0xff00) >> 8)
        };
    }

    /**
     * ip to int
     *
     * @param ip
     * @return
     */
    public static int ipToInteger(String ip) {
        String[] ips = ip.split("\\.");
        int ipFour = 0;
        for (String ip4 : ips) {
            int ip4a = Integer.parseInt(ip4);
            ipFour = (ipFour << 8) | ip4a;
        }
        return ipFour;
    }

    /**
     * int to a ip string
     *
     * @param ip
     * @return
     */
    public static String integerToIp(Integer ip) {
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            int ipa = (ip >> (8 * i)) & (0xff);
            sb.append(ipa).append(".");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    /**
     * string ip to long ip
     *
     * @param ip
     * @return long
     */
    public static long ipToLong(String ip) {
        String[] p = ip.split("\\.");
        if (p.length != 4) {
            return 0;
        }
        int p1 = ((Integer.parseInt(p[0]) << 24) & 0xFF000000);
        int p2 = ((Integer.parseInt(p[1]) << 16) & 0x00FF0000);
        int p3 = ((Integer.parseInt(p[2]) << 8) & 0x0000FF00);
        int p4 = ((Integer.parseInt(p[3])) & 0x000000FF);
        return ((p1 | p2 | p3 | p4) & 0xFFFFFFFFL);
    }

    /**
     * easily close the stream
     *
     * @param io
     */
    public static void ezIOClose(Closeable io) {
        try {
            if (io != null) {
                io.close();
            }
        } catch (IOException o) {
            // ignore
        }
    }

    /**
     * check file exists, if not new one
     *
     * @param path
     * @return
     */
    public static File checkFileExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * check file exists and del it then new one
     *
     * @param path
     * @return
     */
    public static File checkFileAndNew(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                file.delete();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * fileDel
     *
     * @param path
     * @return
     */
    public static void fileDel(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * get path of file
     *
     * @param fileName
     * @return
     */
    public static String getPath(String fileName) {
        return Objects.requireNonNull(ByteUtil.class.getClassLoader().getResource("")).getPath() + fileName;
    }

    /**
     * compress str
     *
     * @param unzip
     * @return
     */
    public static byte[] zipString(String unzip) {
        // 0 ~ 9 compress level
        Deflater deflater = new Deflater(9);
        deflater.setInput(unzip.getBytes());
        deflater.finish();

        final byte[] bytes = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);

        while (!deflater.finished()) {
            int length = deflater.deflate(bytes);
            outputStream.write(bytes, 0, length);
        }

        deflater.end();
        return outputStream.toByteArray();
    }

    /**
     * unCompress str
     *
     * @param zip
     * @return
     */
    public static String unzipString(byte[] zip) {

        Inflater inflater = new Inflater();
        inflater.setInput(zip);

        final byte[] bytes = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);

        try {
            while (!inflater.finished()) {
                int length = inflater.inflate(bytes);
                outputStream.write(bytes, 0, length);
            }
        } catch (DataFormatException e) {
            e.printStackTrace();
            return "";
        } finally {
            inflater.end();
        }
        return outputStream.toString();
    }
}
