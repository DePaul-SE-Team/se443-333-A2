package org.example.task1.Resource;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BuggyFile {

    public void method0() {

    }

    public void method1_1() {
        int x = 3;
        try {
            x = x + 1;
        } catch (Exception e) {

        }
    }

    public void method2(){
        String str = null;
        System.out.println(str.length());
    }

    public void method3() {
        if (true) {

        }
        int x = 4;
        if (x < 3) {
            x = x + 1;
        }
    }

    private String getStringFromDatabase(boolean valid) {
        if (valid) {
            return "Valid String";
        } else {
            return null;
        }
    }

    public void method4() {
        String text;
        String data = getStringFromDatabase(false);
    }

    public void method5() {
        String s1 = "test";
        String s2 = "test";
        boolean equal = (s1 == s2);
    }

    public void method6_1() {
        try {
            FileReader fileReader = new FileReader("file.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void method6_2() {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("file.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Ensure fileReader is closed
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void method7_1() {
        int x = 10;
        x = x / 0;
    }

    public int method7_0() {
        int x = 10;
        x = x / 0;
        return x;
    }

    public void method7_2() {
        int x = 10;
        int y = 0;
        x = x / y;
    }

    public void testMethod() {
        int a = 5;
        int b = a + 1;
        int c = b * 2;
        int d = 10;
        System.out.println(b);
        int e = 20;
    }
}
