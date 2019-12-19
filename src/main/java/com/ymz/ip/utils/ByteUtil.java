package com.ymz.ip.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;

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
     * get a long from a byte array start from the specifiled offset
     *
     * @param src
     * @param offset
     * @return
     */
    public static long get64Long(byte[] src, int offset) {
        byte[] bytes = new byte[8];
        bytesArrPutOffSrc(bytes, offset, src);
        return byteArrayToLong(bytes);
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

    public static short getShort(byte[] src, int offset) {
        byte[] bytes = new byte[2];
        bytesArrPutOffSrc(bytes, offset, src);
        return byteArrayToShort(bytes);
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
     * write a 64-bit long to a byte array
     *
     * @param tag
     * @param offset
     * @param v
     */
    public static void write64Long(byte[] tag, int offset, long v) {
        byte[] bytes = longToByteArray(v);
        bytesArrPutOffTag(tag, offset, bytes);
    }

    /**
     * write a int to a byte array
     *
     * @param tag
     * @param offset
     * @param v
     */
    public static void writeInt(byte[] tag, int offset, int v) {
        byte[] bytes = intToByteArray(v);
        bytesArrPutOffTag(tag, offset, bytes);
    }

    /**
     * write a short to a byte array
     *
     * @param tag
     * @param offset
     * @param v
     */
    public static void writeShort(byte[] tag, int offset, short v) {
        byte[] bytes = shortToByteArray(v);
        bytesArrPutOffTag(tag, offset, bytes);
    }

    /**
     * int to byte
     *
     * @param x
     * @return
     */
    public static byte intToByte(int x) {
        return (byte) x;
    }

    /**
     * byte to int
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte b) {
        //Java always treats byte as signed; we can get its unsigned value by binary-summing it with 0xFF
        return b & 0xFF;
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
     * put a 64 bit long value into an 8 byte byte array
     *
     * @param num
     */
    public static byte[] longToByteArray(long num) {
        return new byte[]{
                (byte) ((num >> 56) & 0xFF),
                (byte) ((num >> 48) & 0xFF),
                (byte) ((num >> 40) & 0xFF),
                (byte) ((num >> 32) & 0xFF),
                (byte) ((num >> 24) & 0xFF),
                (byte) ((num >> 16) & 0xFF),
                (byte) ((num >> 8) & 0xFF),
                (byte) ((num) & 0xFF)
        };
    }

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
     * convert an 8-byte byte array into a 64-bit long value
     *
     * @param byteArray
     * @return the converted long value
     */
    public static long byteArrayToLong(byte[] byteArray) {
        return ((((long) byteArray[0] & 0xff) << 56)
                | (((long) byteArray[1] & 0xff) << 48)
                | (((long) byteArray[2] & 0xff) << 40)
                | (((long) byteArray[3] & 0xff) << 32)
                | (((long) byteArray[4] & 0xff) << 24)
                | (((long) byteArray[5] & 0xff) << 16)
                | (((long) byteArray[6] & 0xff) << 8)
                | (((long) byteArray[7] & 0xff)));
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
     * convert an 2-byte byte array into short
     *
     * @param bytes
     * @return
     */
    public static short byteArrayToShort(byte[] bytes) {
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
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

    public static String getPath(String fileName) {
        return Objects.requireNonNull(ByteUtil.class.getClassLoader().getResource("")).getPath() + fileName;
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
     * int to ip string
     *
     * @param ip
     * @return string
     */
    public static String longToIp(long ip) {
        return String.valueOf((ip >> 24) & 0xFF) + '.' +
                ((ip >> 16) & 0xFF) + '.' +
                ((ip >> 8) & 0xFF) + '.' +
                ((ip) & 0xFF);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * @param filename
     * @return
     */
    public static byte[] fileToByteArray(String filename) {

        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(filename, "r").getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size()).load();
            byte[] result = new byte[(int) fc.size()];
            if (byteBuffer.remaining() > 0) {
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ezIOClose(fc);
        }
        return null;
    }
}
