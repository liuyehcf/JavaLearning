package org.liuyehcf.ssl;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * @author hechenfeng
 * @date 2018/12/7
 */
public class KeyStoreExample {

    private static final String KEY_STORE_PATH = "/tmp/keyStore.ks";
    private static final String KEY_STORE_PASSWORD = "123456";
    private static final String KEY_STORE_TYPE = "PKCS12";

    private static final String KEY_PASSWORD = "654321";

    private static final String ALIAS_PRIVATE = "alias_private";
    private static final String ALIAS_CERT = "alias_cert";

    public static void main(String[] args) throws Exception {
        createKeyStore();

        storePrivateEntryAndCertChain();
        loadPrivateEntryAndCertChain();

        storeCert();
        loadCert();
    }

    private static void createKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);

        // init the key store
        keyStore.load(null, null);

        keyStore.store(new FileOutputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
    }

    private static void storePrivateEntryAndCertChain() throws Exception {
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
    }

    private static void loadPrivateEntryAndCertChain() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

        Key pvtKey = keyStore.getKey(ALIAS_PRIVATE, KEY_PASSWORD.toCharArray());
        assertNotNull(pvtKey);
        System.out.println(pvtKey.toString());

        Certificate[] chain = keyStore.getCertificateChain(ALIAS_PRIVATE);
        assertNotNull(chain);
        for (Certificate cert : chain) {
            System.out.println(cert.toString());
        }

        //or you can get cert by same alias
        Certificate cert = keyStore.getCertificate(ALIAS_PRIVATE);
        assertNotNull(cert);
        System.out.println(cert);
    }

    private static void storeCert() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

        CertAndKeyGen gen = new CertAndKeyGen("RSA", "SHA1WithRSA");
        gen.generate(1024);

        X509Certificate cert = gen.getSelfCertificate(new X500Name("CN=ROOT"), (long) 365 * 24 * 3600);

        keyStore.setCertificateEntry(ALIAS_CERT, cert);

        keyStore.store(new FileOutputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
    }

    private static void loadCert() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

        Certificate cert = keyStore.getCertificate(ALIAS_CERT);
        assertNotNull(cert);
        System.out.println(cert);
    }

    private static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        }
    }
}
