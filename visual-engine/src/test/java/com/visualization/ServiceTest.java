package com.visualization;

import java.util.Base64;

//@SpringBootTest
public class ServiceTest {

    public static void main(String[] args) {
        byte[] decode = Base64.getDecoder().decode("5rWL6K+VOjpzYWQ6OjExMQ==");
        System.err.println(new String(decode));
    }
}
