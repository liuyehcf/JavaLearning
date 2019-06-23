package org.liuyehcf.ssl;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
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

    private static final String ALIAS_CREATED_PRIVATE = "alias_created_private";
    private static final String ALIAS_CREATED_CERT = "alias_created_cert";
    private static final String ALIAS_LOAD_CERT = "alias_loaded_cert";

    public static void main(String[] args) throws Exception {
        createKeyStore();

        createPrivateEntryAndCertChain(ALIAS_CREATED_PRIVATE);
        getPrivateEntryAndCertChain(ALIAS_CREATED_PRIVATE);

        createCert(ALIAS_CREATED_CERT);
        getCert(ALIAS_CREATED_CERT);

        loadCert("", ALIAS_LOAD_CERT);
        getCert(ALIAS_LOAD_CERT);
    }

    private static void createKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);

        // init the key store
        keyStore.load(null, null);

        keyStore.store(new FileOutputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
    }

    private static void createPrivateEntryAndCertChain(String alias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

        CertAndKeyGen gen = new CertAndKeyGen("RSA", "SHA1WithRSA");
        gen.generate(1024);

        Key key = gen.getPrivateKey();
        X509Certificate cert = gen.getSelfCertificate(new X500Name("CN=ROOT"), (long) 365 * 24 * 3600);

        X509Certificate[] chain = new X509Certificate[1];
        chain[0] = cert;

        keyStore.setKeyEntry(alias, key, KEY_PASSWORD.toCharArray(), chain);

        keyStore.store(new FileOutputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
    }

    private static void getPrivateEntryAndCertChain(String alias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

        Key pvtKey = keyStore.getKey(alias, KEY_PASSWORD.toCharArray());
        assertNotNull(pvtKey);
        System.out.println(pvtKey.toString());

        Certificate[] chain = keyStore.getCertificateChain(alias);
        assertNotNull(chain);
        for (Certificate cert : chain) {
            System.out.println(cert.toString());
        }

        //or you can get cert by same alias
        Certificate cert = keyStore.getCertificate(alias);
        assertNotNull(cert);
        System.out.println(cert);
    }

    private static void createCert(String alias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

        CertAndKeyGen gen = new CertAndKeyGen("RSA", "SHA1WithRSA");
        gen.generate(1024);

        X509Certificate cert = gen.getSelfCertificate(new X500Name("CN=ROOT"), (long) 365 * 24 * 3600);

        keyStore.setCertificateEntry(alias, cert);

        keyStore.store(new FileOutputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
    }

    private static void getCert(String alias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

        Certificate cert = keyStore.getCertificate(alias);
        assertNotNull(cert);
        System.out.println(cert);
    }

    private static void loadCert(String certPath, String alias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(new FileInputStream(certPath));

        keyStore.setCertificateEntry(alias, certificate);

        keyStore.store(new FileOutputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
    }

    private static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        }
    }
}
