package org.computerist.ssltools.zap;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * A wrapper for the various SSL utils used by ZAP. The intention is to allow
 * the local versions to be used... unless the ZAP version is available -
 * allowing ZAP to share keypairs, etc. to reduce the perf. hit of dynamic
 * creation where possible
 * 
 * @author mgoodwin
 *
 */
public class ZAPSslToolsWrapper {
  private FixedSslCertificateService fixedService;
  private static ZAPSslToolsWrapper singleton = new ZAPSslToolsWrapper();

  private ZAPSslToolsWrapper() {
    this.fixedService = FixedSslCertificateService.getService();
  }

  public KeyStore getHostKeyStore() {
    return this.fixedService.getHostKeyStore();
  }

  public void initializeRootCA(KeyStore caks) throws UnrecoverableKeyException,
      KeyStoreException, NoSuchAlgorithmException {
    this.fixedService.initializeRootCA(caks);
  }

  public void createCertForHost(String hostName) throws InvalidKeyException,
      UnrecoverableKeyException, NoSuchAlgorithmException,
      CertificateException, NoSuchProviderException, SignatureException,
      KeyStoreException, IOException {
    this.fixedService.createCertForHost(hostName);
  }

  public KeyStore createRootCA() throws NoSuchAlgorithmException {
    return ZapSslCertificateUtils.createRootCA();
  }

  public String keyStore2String(KeyStore keystore)
      throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
      IOException {
    return ZapSslCertificateUtils.keyStore2String(keystore);
  }

  public KeyStore string2Keystore(String str)
      throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
      IOException {
    return ZapSslCertificateUtils.string2Keystore(str);
  }

  public char[] getPassphrase() {
    return FixedSslCertificateService.PASSPHRASE;
  }

  public static final ZAPSslToolsWrapper getService() {
    return singleton;
  }
}
