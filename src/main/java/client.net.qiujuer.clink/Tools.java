package client.net.qiujuer.clink;

public class Tools {

        public static byte[] intToByteArray(int a) {

            return new byte[]{

                    (byte) ((a>>24)&0xFF),
                    (byte) ((a>>16)&0xFF),
                    (byte) ((a>>8)&0xFF),
                    (byte) ((a)&0xFF)
            };
        }
        public static int byteArrayToInt(byte[] b) {

            //&0xFF取低8位    a>>24&0xFF  就是 00000001 00000010 00001101 00001111 a>>24得
            //00000000 00000000 00000000 00000001 再&0xFF 及与11111111做位运算 结果再取低8位就获取到了 高8位得值
            return b[3] & 0xFF|
                    (b[2]&0xFF)<<8|
                    (b[1]&0xFF)<<16|
                    (b[0]&0xFF<<24);
        }
}
