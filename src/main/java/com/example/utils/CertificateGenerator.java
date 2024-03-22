package com.example.utils;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateGenerator {
    public static void genCrtKey() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(4096, new SecureRandom());
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        X500Name issuerName = new X500Name("CN=JetProfile CA");
        X500Name subjectName = new X500Name("CN=Novice-from-2024-01-19");
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000); // Yesterday
        Date notAfter = new Date(System.currentTimeMillis() + 3650L * 24 * 60 * 60 * 1000); // 10 years later

        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                issuerName, serialNumber, notBefore, notAfter, subjectName, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(privateKey);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(signer));
        // 将私钥写入 PEM 文件
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter("./ca.key"))) {
            pemWriter.writeObject(privateKey);
        }

        // 将证书写入 PEM 文件
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter("./ca.crt"))) {
            pemWriter.writeObject(cert);
        }


    }

    public static void main(String[] args) {
        try {
            genCrtKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
