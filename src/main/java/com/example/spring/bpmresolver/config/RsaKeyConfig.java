package com.example.spring.bpmresolver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaKeyConfig {

    private final ResourceLoader resourceLoader;

    public RsaKeyConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    RSAPublicKey jwtPublicKey(JwtProperties jwtProperties) throws IOException, GeneralSecurityException {
        Resource resource = resourceLoader.getResource(jwtProperties.publicKeyLocation());
        byte[] der = readPem(resource, "PUBLIC KEY");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(der));
    }

    @Bean
    RSAPrivateKey jwtPrivateKey(JwtProperties jwtProperties) throws IOException, GeneralSecurityException {
        Resource resource = resourceLoader.getResource(jwtProperties.privateKeyLocation());
        byte[] der = readPem(resource, "PRIVATE KEY");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(der));
    }

    private static byte[] readPem(Resource resource, String type) throws IOException {
        String pem = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String header = "-----BEGIN " + type + "-----";
        String footer = "-----END " + type + "-----";
        int start = pem.indexOf(header);
        int end = pem.indexOf(footer);
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Invalid PEM format, expected '" + type + "'");
        }
        String base64 = pem.substring(start + header.length(), end)
                .replace("\r", "")
                .replace("\n", "")
                .trim();
        return Base64.getDecoder().decode(base64);
    }
}
