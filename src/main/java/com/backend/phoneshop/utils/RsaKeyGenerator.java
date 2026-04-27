package com.backend.phoneshop.utils;

import java.io.FileWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class RsaKeyGenerator {

    public static void main(String[] args) throws Exception {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);

        KeyPair keyPair = generator.generateKeyPair();

        String privateKey = Base64.getEncoder()
                .encodeToString(keyPair.getPrivate().getEncoded());

        String publicKey = Base64.getEncoder()
                .encodeToString(keyPair.getPublic().getEncoded());

        try (FileWriter privateWriter = new FileWriter("private.pem")) {
            privateWriter.write("-----BEGIN PRIVATE KEY-----\n");
            privateWriter.write(privateKey.replaceAll("(.{64})", "$1\n"));
            privateWriter.write("\n-----END PRIVATE KEY-----");
        }

        try (FileWriter publicWriter = new FileWriter("public.pem")) {
            publicWriter.write("-----BEGIN PUBLIC KEY-----\n");
            publicWriter.write(publicKey.replaceAll("(.{64})", "$1\n"));
            publicWriter.write("\n-----END PUBLIC KEY-----");
        }

        System.out.println("Keys generated successfully.");
    }
}
