package org.liuyehcf.ssl;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import javax.crypto.KeyGenerator;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * @author hechenfeng
 * @date 2018/12/7
 */
public class KeyTool {

    private static final String KEY_STORE_PATH = "/tmp/keyStore.ks";
    private static final String KEY_STORE_PASSWORD = "123456";
    private static final String KEY_STORE_TYPE = "PKCS12";

    private static final String KEY_PASSWORD = "654321";

    private static final String ALIAS_SECRET = "key_secret";
    private static final String ALIAS_PRIVATE = "key_private";
    private static final String ALIAS_CERT = "key_cert";

    public static void main(String[] args) {
        createKeyStore();
        createSecretEntry();
        createPrivateEntryAndCert();
        storeCert();
        loadPrivateEntry();
        getCert();
        loadCert();
    }

    private static void createKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(null, null);

            keyStore.store(new FileOutputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createSecretEntry() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            Key key = keyGen.generateKey();
            keyStore.setKeyEntry(ALIAS_SECRET, key, KEY_PASSWORD.toCharArray(), null);

            keyStore.store(new FileOutputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createPrivateEntryAndCert() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

            CertAndKeyGen gen = new CertAndKeyGen("RSA", "SHA1WithRSA");
            gen.generate(1024);

            Key key = gen.getPrivateKey();
            X509Certificate cert = gen.getSelfCertificate(new X500Name("CN=ROOT"), (long) 365 * 24 * 3600);

            X509Certificate[] chain = new X509Certificate[1];
            chain[0] = cert;

            keyStore.setKeyEntry(ALIAS_PRIVATE, key, KEY_PASSWORD.toCharArray(), chain);

            keyStore.store(new FileOutputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void storeCert() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

            CertAndKeyGen gen = new CertAndKeyGen("RSA", "SHA1WithRSA");
            gen.generate(1024);

            X509Certificate cert = gen.getSelfCertificate(new X500Name("CN=ROOT"), (long) 365 * 24 * 3600);

            keyStore.setCertificateEntry(ALIAS_CERT, cert);

            keyStore.store(new FileOutputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void loadPrivateEntry() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

            Key pvtKey = keyStore.getKey(ALIAS_PRIVATE, KEY_PASSWORD.toCharArray());
            System.out.println(pvtKey.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void loadCert() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

            Key pvtKey = keyStore.getKey(ALIAS_PRIVATE, KEY_PASSWORD.toCharArray());
            System.out.println(pvtKey.toString());

            java.security.cert.Certificate[] chain = keyStore.getCertificateChain(ALIAS_PRIVATE);
            for (java.security.cert.Certificate cert : chain) {
                System.out.println(cert.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void getCert() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

            java.security.cert.Certificate cert = keyStore.getCertificate(ALIAS_PRIVATE);

            System.out.println(cert);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
