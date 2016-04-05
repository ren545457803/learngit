package com.example;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class MyClass {

    private int id;
    private String name;

    public MyClass(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean equals(MyClass person) {
        return person.id == id;
    }

    public int hashCode() {
        return id;
    }

    public static void haha(String[] args) {

        Set<MyClass> set = new HashSet<MyClass>();
        for (int i = 0; i < 10; i++) {
            set.add(new MyClass(i, "Jim"));
        }
        System.out.println(set.size());
    }


    public static void main(String[] args) throws IOException {

        /*Scanner scanner = new Scanner(System.in);

        System.out.println("your name?");
        String name = scanner.nextLine();
        System.out.println("is " + name);

        System.out.println("age?");
        int age = scanner.nextInt();
        System.out.println("age:" + age);*/

        Console console = System.console();
        char[] pswUser = console.readPassword();
        System.out.println("--user:" + new String(pswUser));

//        haha(args);

//        write();
    }

    private static void read() throws IOException {
        InputStream inputStream = new FileInputStream(new File("E:/woca.txt"));

        StringBuilder builder = new StringBuilder();

        byte[] readBytes = new byte[1024];
        int readLength;

        while ((readLength = inputStream.read(readBytes)) > 0) {
            builder.append(new String(readBytes, 0, readLength));
        }

        System.out.println("--" + builder.toString());
        inputStream.close();
    }

    private static void write() throws IOException {
        /*OutputStream outputStream = new FileOutputStream(new File("E:/woca.txt"));
        outputStream.write(23);
        outputStream.close();*/
    }
}
