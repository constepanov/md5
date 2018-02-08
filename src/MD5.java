import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Files;

class MD5 {

    private static int[] T = new int[64];

    static {
        for(int i = 0; i < T.length; i++) {
            T[i] = (int)(long)(Math.pow(2, 32) * Math.abs(Math.sin(i + 1)));
        }
    }

    private static int[][] SHIFTS = {
            {7, 12, 17, 22},
            {5,  9, 14, 20},
            {4, 11, 16, 23},
            {6, 10, 15, 21}
    };

    private static byte[] hash(byte[] data) {
        int length = data.length % 64;
        int paddingLength = (length < 56) ? (56 - length) : (120 - length);
        int capacity = data.length + paddingLength + 8;
        ByteBuffer extendedData = ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN);
        extendedData.put(data);
        extendedData.put((byte) -128);
        extendedData.putLong(extendedData.capacity() - 8, data.length * 8);
        extendedData.rewind();

        int a = 0x67452301;
        int b = 0xEFCDAB89;
        int c = 0x98BADCFE;
        int d = 0x10325476;

        while(extendedData.hasRemaining()) {
            IntBuffer buffer = extendedData.slice().order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
            int aa = a;
            int bb = b;
            int cc = c;
            int dd = d;
            int bufferIndex = 0;
            int shiftIndex = 0;
            for(int i = 0; i < 64; i++) {
                int func = 0;
                switch (i / 16) {
                    case 0:
                        bufferIndex = (i % 16 == 0) ? 0 : bufferIndex + 1;
                        func = (b & c) | (~b & d);
                        break;
                    case 1:
                        bufferIndex = (i % 16 == 0) ? 1 : (bufferIndex + 5) % 16;
                        func = (b & d) | (c & ~d);
                        break;
                    case 2:
                        bufferIndex = (i % 16 == 0) ? 5 : (bufferIndex + 3) % 16;
                        func = b ^ c ^ d;
                        break;
                    case 3:
                        bufferIndex = (i % 16 == 0) ? 0 : (bufferIndex + 7) % 16;
                        func = c ^ (b | ~d);
                        break;
                }
                shiftIndex = (i % 4 == 0) ? 0 : (shiftIndex + 1);
                int tmp = b + Integer.rotateLeft(a + func + buffer.get(bufferIndex) + T[i], SHIFTS[i / 16][shiftIndex]);
                a = d;
                d = c;
                c = b;
                b = tmp;
            }
            a += aa;
            b += bb;
            c += cc;
            d += dd;
            extendedData.position(extendedData.position() + 64);
        }

        ByteBuffer result = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        result.putInt(a);
        result.putInt(b);
        result.putInt(c);
        result.putInt(d);
        return result.array();
    }

    private static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    static String hash(String message) {
        return toHexString(hash(message.getBytes()));
    }

    static String hash(File file) {
        byte[] data = null;
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toHexString(hash(data));
    }
}
