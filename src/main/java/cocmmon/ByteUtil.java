package cocmmon;

/**
 * Byte 工具类
 *
 * @author pixiaozhi
 */
public class ByteUtil {
    private final static byte[] EMPTY_BYTES = new byte[0];

    /**
     * CCITT标准CRC16(1021)余数表 CRC16-CCITT ISO HDLC, ITU X.25, x16+x12+x5+1 多项式
     * 高位在先时生成多项式 Gm=0x11021 低位在先时生成多项式，Gm=0x8408
     * 本例采用高位在先
     */
    private static int[] CRC16_CCITT_TABLE = {
            0x0000, 0x1189, 0x2312, 0x329b, 0x4624, 0x57ad, 0x6536, 0x74bf,
            0x8c48, 0x9dc1, 0xaf5a, 0xbed3, 0xca6c, 0xdbe5, 0xe97e, 0xf8f7,
            0x1081, 0x0108, 0x3393, 0x221a, 0x56a5, 0x472c, 0x75b7, 0x643e,
            0x9cc9, 0x8d40, 0xbfdb, 0xae52, 0xdaed, 0xcb64, 0xf9ff, 0xe876,
            0x2102, 0x308b, 0x0210, 0x1399, 0x6726, 0x76af, 0x4434, 0x55bd,
            0xad4a, 0xbcc3, 0x8e58, 0x9fd1, 0xeb6e, 0xfae7, 0xc87c, 0xd9f5,
            0x3183, 0x200a, 0x1291, 0x0318, 0x77a7, 0x662e, 0x54b5, 0x453c,
            0xbdcb, 0xac42, 0x9ed9, 0x8f50, 0xfbef, 0xea66, 0xd8fd, 0xc974,
            0x4204, 0x538d, 0x6116, 0x709f, 0x0420, 0x15a9, 0x2732, 0x36bb,
            0xce4c, 0xdfc5, 0xed5e, 0xfcd7, 0x8868, 0x99e1, 0xab7a, 0xbaf3,
            0x5285, 0x430c, 0x7197, 0x601e, 0x14a1, 0x0528, 0x37b3, 0x263a,
            0xdecd, 0xcf44, 0xfddf, 0xec56, 0x98e9, 0x8960, 0xbbfb, 0xaa72,
            0x6306, 0x728f, 0x4014, 0x519d, 0x2522, 0x34ab, 0x0630, 0x17b9,
            0xef4e, 0xfec7, 0xcc5c, 0xddd5, 0xa96a, 0xb8e3, 0x8a78, 0x9bf1,
            0x7387, 0x620e, 0x5095, 0x411c, 0x35a3, 0x242a, 0x16b1, 0x0738,
            0xffcf, 0xee46, 0xdcdd, 0xcd54, 0xb9eb, 0xa862, 0x9af9, 0x8b70,
            0x8408, 0x9581, 0xa71a, 0xb693, 0xc22c, 0xd3a5, 0xe13e, 0xf0b7,
            0x0840, 0x19c9, 0x2b52, 0x3adb, 0x4e64, 0x5fed, 0x6d76, 0x7cff,
            0x9489, 0x8500, 0xb79b, 0xa612, 0xd2ad, 0xc324, 0xf1bf, 0xe036,
            0x18c1, 0x0948, 0x3bd3, 0x2a5a, 0x5ee5, 0x4f6c, 0x7df7, 0x6c7e,
            0xa50a, 0xb483, 0x8618, 0x9791, 0xe32e, 0xf2a7, 0xc03c, 0xd1b5,
            0x2942, 0x38cb, 0x0a50, 0x1bd9, 0x6f66, 0x7eef, 0x4c74, 0x5dfd,
            0xb58b, 0xa402, 0x9699, 0x8710, 0xf3af, 0xe226, 0xd0bd, 0xc134,
            0x39c3, 0x284a, 0x1ad1, 0x0b58, 0x7fe7, 0x6e6e, 0x5cf5, 0x4d7c,
            0xc60c, 0xd785, 0xe51e, 0xf497, 0x8028, 0x91a1, 0xa33a, 0xb2b3,
            0x4a44, 0x5bcd, 0x6956, 0x78df, 0x0c60, 0x1de9, 0x2f72, 0x3efb,
            0xd68d, 0xc704, 0xf59f, 0xe416, 0x90a9, 0x8120, 0xb3bb, 0xa232,
            0x5ac5, 0x4b4c, 0x79d7, 0x685e, 0x1ce1, 0x0d68, 0x3ff3, 0x2e7a,
            0xe70e, 0xf687, 0xc41c, 0xd595, 0xa12a, 0xb0a3, 0x8238, 0x93b1,
            0x6b46, 0x7acf, 0x4854, 0x59dd, 0x2d62, 0x3ceb, 0x0e70, 0x1ff9,
            0xf78f, 0xe606, 0xd49d, 0xc514, 0xb1ab, 0xa022, 0x92b9, 0x8330,
            0x7bc7, 0x6a4e, 0x58d5, 0x495c, 0x3de3, 0x2c6a, 0x1ef1, 0x0f78
    };

    /**
     * byte -&gt; hex
     *
     * @param b byte src
     * @return Hex's String
     */
    public static String toHex(byte b) {

        return String.valueOf(toHexDigit(b >> 4 & 0x0f)) +
                toHexDigit(b & 0x0f);
    }

    /**
     * byte[] -&gt; hex
     *
     * @param bytes byte array src
     * @return Hex's String
     */
    public static String toHex(byte... bytes) {
        if (bytes == null) {
            return "";
        }

        int len = bytes.length;
        StringBuilder sw = new StringBuilder(len * 2);
        for (byte aByte : bytes) {
            sw.append(toHexDigit(aByte >> 4 & 0x0f));
            sw.append(toHexDigit(aByte & 0x0f));
        }
        return sw.toString();
    }

    /**
     * int -&gt; hex
     *
     * @param i int src
     * @return Hex's String
     */
    public static String toHex(int i) {
        return Integer.toHexString(i);
    }

    /**
     * int -&gt; hex (指定长度)
     *
     * @param i  int src
     * @param bs return bytes length
     * @return Hex's String
     */
    public static String toHex(int i, int bs) {
        final String hex = toHex(i);

        final int allLen = bs * 2;
        final int strLen = hex.length();

        if (strLen < allLen) {
            int padLen = allLen - strLen;

            StringBuilder sb = new StringBuilder();

            while ((padLen--) > 0) {
                sb.append('0');
            }

            sb.append(hex);

            return sb.toString();
        } else {
            return hex;
        }
    }

    /**
     * HEX 优化，默认长度为2的倍数
     *
     * @param hex String's Src
     * @return Hex's String
     */
    public static String toHex(String hex) {
        if (hex == null || hex.isEmpty()) {
            return "";
        } else if (hex.length() % 2 == 1) {
            return "0" + hex;
        } else {
            return hex;
        }
    }

    /**
     * hex -&gt; byte[]
     *
     * @param hex hex's string
     * @return bytes
     */
    public static byte[] toByte(String hex) {
        if (hex == null || hex.length() == 0) {
            return EMPTY_BYTES;
        } else {
            char[] cs = hex.toCharArray();
            byte[] bs = new byte[cs.length / 2 + cs.length % 2];

            for (int i = 0; i < bs.length; i++) {
                // 高字节
                int i1 = 0x00;
                // 低字节
                int i2 = 0x00;

                if (cs.length % 2 == 1) {
                    // 如果传入的hex长度为单数，默认前面添0
                    if (i == 0) {
                        i1 = toByteDigit('0') & 0x0f;
                        i2 = toByteDigit(cs[i]) & 0x0f;
                    } else {
                        i1 = toByteDigit(cs[i * 2 - 1]) & 0x0f;
                        i2 = toByteDigit(cs[i * 2]) & 0x0f;
                    }
                } else {
                    i1 = toByteDigit(cs[i * 2]) & 0x0f;
                    i2 = toByteDigit(cs[i * 2 + 1]) & 0x0f;
                }

                bs[i] = (byte) (i1 << 4 | i2);
            }

            return bs;
        }
    }

    /**
     * hex -&gt; byte[] (指定byte数组长度)
     *
     * @param hex    hex's string
     * @param length return byte array length
     * @return bytes
     */
    public static byte[] toByte(String hex, int length) {
        if (hex == null || hex.length() == 0) {
            return EMPTY_BYTES;
        } else {
            final int allLen = length * 2;
            final int strLen = hex.length();

            if (strLen < allLen) {
                int padLen = allLen - strLen;

                StringBuilder sb = new StringBuilder();

                while ((padLen--) > 0) {
                    sb.append('0');
                }

                sb.append(hex);

                return toByte(sb.toString());
            } else if (strLen > allLen) {
                return toByte(hex.substring((strLen - allLen), strLen));
            } else {
                return toByte(hex);
            }
        }
    }

    /**
     * int -&gt; byte[]
     *
     * @param i int src
     * @return bytes
     */
    public static byte[] toByte(int i) {
        return new byte[]{(byte) i};
    }

    /**
     * int -&gt; byte[] (指定byte数组长度)
     *
     * @param i  int src
     * @param bs byte array length
     * @return bytes
     */
    public static byte[] toByte(int i, int bs) {
        byte[] result = new byte[bs];

        for (int j = 0; j < bs; j++) {
            result[bs - j - 1] = (byte) ((i >> (j * 8)) & 0xff);
        }

        return result;
    }

    /**
     * byte -&gt; int
     *
     * @param b byte src
     * @return int
     */
    public static int toInt(byte b) {
        return b & 0xff;
    }

    /**
     * byte[] -&gt; int
     *
     * @param bs bytes
     * @return int
     */
    public static int toInt(byte... bs) {
        int result = 0x00000000;

        for (byte b : bs) {
            result = (result << 8) | (b & 0xff);
        }

        return result;
    }

    /**
     * byte[] -&gt; float
     *
     * @param bs bytes
     * @return float
     */
    public static float toFloat(byte... bs) {
        int result = 0x00000000;

        for (byte b : bs) {
            result = (result << 8) | (b & 0xff);
        }

        return Float.intBitsToFloat(result);
    }

    /**
     * byte 数组转 Ascii
     *
     * @param bs bytes
     * @return ascii string
     */
    public static String toAscii(byte... bs) {
        if (bs == null) {
            return null;
        } else {
            char[] cs = new char[bs.length];

            for (int i = 0; i < bs.length; i++) {
                cs[i] = (char) bs[i];
            }

            return new String(cs);
        }
    }

    /**
     * hex -&gt; int
     *
     * @param hex hex's string
     * @return int
     */
    public static int toInt(String hex) {
        if (hex == null || hex.isEmpty()) {
            return 0;
        }

        char[] cs = hex.toCharArray();

        int res = 0;
        int len = cs.length;
        for (int i = 0; i < len; i++) {
            res = res | ((toByteDigit(cs[i]) & 0x0f) << ((len - i - 1) * 4));
        }

        return res;
    }

    /**
     * CRC生成
     *
     * @param regInit reg init value
     * @param bytes   bytes
     * @return crc's int
     */
    public static int toCRC(int regInit, byte[] bytes) {
        int crcReg = regInit;
        for (byte aByte : bytes) {
            crcReg = (crcReg >> 8) ^ CRC16_CCITT_TABLE[(crcReg ^ aByte) & 0xff];
        }
        return crcReg;
    }

    /**
     * CRC生成 （return byte[]）
     *
     * @param bytes bytes
     * @return crc's bytes
     */
    public static byte[] toCRC(byte[] bytes) {
        int crc = toCRC(0x0000, bytes);

        byte b1 = (byte) ((crc >> 8) & 0xFF);
        byte b2 = (byte) (crc & 0xFF);

        return new byte[]{b1, b2};
    }

    /**
     * CRC（Xmodem） 生成
     *
     * @param bytes bytes src
     * @return crc's int
     */
    public static int toCRCXmodem(byte[] bytes) {
        int crc = 0x0000;
        int polynomial = 0x1021;
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);

                crc <<= 1;
                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
            }
        }
        return (crc & 0xffff);
    }

    /**
     * 转换二进制字符串
     *
     * @param bs bytes src
     * @return binary's string
     */
    public static String toBinary(byte... bs) {
        StringBuilder builder = new StringBuilder();

        for (byte b : bs) {
            builder.append(b >> 7 & 0x01);
            builder.append(b >> 6 & 0x01);
            builder.append(b >> 5 & 0x01);
            builder.append(b >> 4 & 0x01);
            builder.append(b >> 3 & 0x01);
            builder.append(b >> 2 & 0x01);
            builder.append(b >> 1 & 0x01);
            builder.append(b & 0x01);
        }

        return builder.toString();
    }

    /**
     * 获取HEX 的字节长度
     *
     * @param hex hex's string
     * @return hes's length
     */
    public static int length(String hex) {
        if (hex == null) {
            return 0;
        }
        int len = hex.length();

        if (len % 2 == 0) {
            return len / 2;
        } else {
            return len / 2 + 1;
        }
    }

    private static char toHexDigit(int i) {
        if (i <= 9) {
            return (char) (i + 0x30);
        } else {
            return (char) (i + 0x57);
        }
    }

    private static int toByteDigit(char c1) {
        if (c1 >= '0' && c1 <= '9') {
            return (c1 - 0x30);
        } else if (c1 >= 'A' && c1 <= 'Z') {
            return (c1 - 0x27);
        } else if (c1 >= 'a' && c1 <= 'z') {
            return (c1 - 0x57);
        } else {
            return 0;
        }
    }
}