package com.example.controller;

import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.entity.LicensePart;
import com.example.entity.Product;
import com.example.entity.LicenseRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
/**
 * Created with IntelliJ IDEA.
 *
 * @Author: PastKing
 * @Date: 2024/03/22/8:59
 * @Description:
 */


@RestController
public class LicenseController {

    private final String certificatePath = "D:\\Desktop\\jar\\ca.crt";
    private final String privateKeyPath = "D:\\Desktop\\jar\\ca.key";

    @PostMapping("/generateLicense")
    public Result generateLicense(@RequestBody LicenseRequest request) throws Exception {
        X509Certificate cert = getCertificate(certificatePath);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, 50);
        String date = DateUtil.formatDate(calendar.getTime());
        String[] codes = request.getProducts().stream().map(Product::getCode).toArray(String[]::new);
        String licenseId = request.getLicenseId();
        LicensePart licensePart = new LicensePart(licenseId, codes, date);
        byte[] licensePartBytes = licensePart.toString().getBytes(StandardCharsets.UTF_8);

        PrivateKey privateKey = getPrivateKey(privateKeyPath);

        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey);
        signature.update(licensePartBytes);
        byte[] signatureBytes = signature.sign();

        String sigResultsBase64 = Base64.getEncoder().encodeToString(signatureBytes);

        String result = licenseId + "-" + Base64.getEncoder().encodeToString(licensePartBytes) + "-" + sigResultsBase64 + "-" + Base64.getEncoder().encodeToString(cert.getEncoded());

        return Result.success(result);
    }

    private X509Certificate getCertificate(String path) throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        FileInputStream fileInputStream = new FileInputStream(path);
        return (X509Certificate) certificateFactory.generateCertificate(fileInputStream);
    }


    private PrivateKey getPrivateKey(String path) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        PEMParser pemParser = new PEMParser(new FileReader(path));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        Object object = pemParser.readObject();
        KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
        return kp.getPrivate();
    }
}
