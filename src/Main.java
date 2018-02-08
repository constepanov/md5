import java.io.File;

public class Main {
    public static void main(String[] args) {
        testMD5();
        System.out.println(MD5.hash(new File("src/Main.java")));
    }

    private static void testMD5() {
        System.out.println("--------------TEST--------------");
        System.out.println(MD5.hash(""));
        System.out.println(MD5.hash("a"));
        System.out.println(MD5.hash("abc"));
        System.out.println(MD5.hash("message digest"));
        System.out.println(MD5.hash("abcdefghijklmnopqrstuvwxyz"));
        System.out.println("--------------------------------");
    }
}
