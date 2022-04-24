package com.yanzhenjie.andserver.delegate.ssl;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

public class SSLContextBuilder {

    static final String TLS = "TLS";

    private final org.apache.hc.core5.ssl.SSLContextBuilder builder;

    private String keyStoreType = KeyStore.getDefaultType();

    SSLContextBuilder() {
        this(org.apache.hc.core5.ssl.SSLContextBuilder.create());
    }

    public SSLContextBuilder(org.apache.hc.core5.ssl.SSLContextBuilder builder) {
        this.builder = builder;
    }

    public static SSLContextBuilder create() {
        return new SSLContextBuilder();
    }

    public SSLContextBuilder setProtocol(String protocol) {
        builder.setProtocol(protocol);
        return this;
    }

    public SSLContextBuilder setProvider(Provider provider) {
        builder.setProvider(provider);
        return this;
    }

    public SSLContextBuilder setProvider(String name) {
        builder.setProvider(name);
        return this;
    }

    public SSLContextBuilder setKeyStoreType(String keyStoreType) {
        builder.setKeyStoreType(keyStoreType);
        this.keyStoreType = keyStoreType;
        return this;
    }

    public SSLContextBuilder setKeyManagerFactoryAlgorithm(String keyManagerFactoryAlgorithm) {
        builder.setKeyManagerFactoryAlgorithm(keyManagerFactoryAlgorithm);
        return this;
    }

    public SSLContextBuilder setTrustManagerFactoryAlgorithm(String trustManagerFactoryAlgorithm) {
        builder.setTrustManagerFactoryAlgorithm(trustManagerFactoryAlgorithm);
        return this;
    }

    public SSLContextBuilder setSecureRandom(SecureRandom secureRandom) {
        builder.setSecureRandom(secureRandom);
        return this;
    }

    public SSLContextBuilder loadTrustMaterial(KeyStore truststore, TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException {
        builder.loadTrustMaterial(truststore, trustStrategy.wrapped());
        return this;
    }

    public SSLContextBuilder loadTrustMaterial(TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException {
        builder.loadTrustMaterial(trustStrategy.wrapped());
        return this;
    }

    public SSLContextBuilder loadTrustMaterial(File file, char[] storePassword, TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        builder.loadTrustMaterial(file, storePassword, trustStrategy.wrapped());
        return this;
    }

    public SSLContextBuilder loadTrustMaterial(File file, char[] storePassword) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        builder.loadTrustMaterial(file, storePassword);
        return this;
    }

    public SSLContextBuilder loadTrustMaterial(File file) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        builder.loadTrustMaterial(file);
        return this;
    }

    public SSLContextBuilder loadTrustMaterial(URL url, char[] storePassword, TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        builder.loadTrustMaterial(url, storePassword, trustStrategy.wrapped());
        return this;
    }

    public SSLContextBuilder loadTrustMaterial(URL url, char[] storePassword) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        builder.loadTrustMaterial(url, storePassword);
        return this;
    }

    public SSLContextBuilder loadKeyMaterial(KeyStore keystore, char[] keyPassword, PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        builder.loadKeyMaterial(keystore, keyPassword, aliasStrategy.wrapped());
        return this;
    }

    public SSLContextBuilder loadKeyMaterial(KeyStore keystore, char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        builder.loadKeyMaterial(keystore, keyPassword);
        return this;
    }

    public SSLContextBuilder loadKeyMaterial(File file, char[] storePassword, char[] keyPassword, PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        builder.loadKeyMaterial(file, storePassword, keyPassword, aliasStrategy.wrapped());
        return this;
    }

    public SSLContextBuilder loadKeyMaterial(File file, char[] storePassword, char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        builder.loadKeyMaterial(file, storePassword, keyPassword);
        return this;
    }

    public SSLContextBuilder loadKeyMaterial(URL url, char[] storePassword, char[] keyPassword, PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        builder.loadKeyMaterial(url, storePassword, keyPassword, aliasStrategy.wrapped());
        return this;
    }

    public SSLContextBuilder loadKeyMaterial(URL url, char[] storePassword, char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        builder.loadKeyMaterial(url, storePassword, keyPassword);
        return this;
    }

    public SSLContextBuilder loadKeyMaterial(
            @NonNull final InputStream inStream,
            final char[] storePassword,
            final char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        final KeyStore identityStore = KeyStore.getInstance(keyStoreType);
        identityStore.load(inStream, storePassword);
        return loadKeyMaterial(identityStore, keyPassword);
    }

    public SSLContextBuilder loadKeyMaterial(
            @NonNull final InputStream inStream,
            final char[] storePassword,
            final char[] keyPassword,
            final PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        final KeyStore identityStore = KeyStore.getInstance(keyStoreType);
        identityStore.load(inStream, storePassword);
        return loadKeyMaterial(identityStore, keyPassword, aliasStrategy);
    }

    public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
        return builder.build();
    }
}
