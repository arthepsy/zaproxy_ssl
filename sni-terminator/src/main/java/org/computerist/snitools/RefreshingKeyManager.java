package org.computerist.snitools;

import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509KeyManager;

/*
 * A KeyManager which can refresh itself and an associated SSLContext when its keystore is modified
 */
public class RefreshingKeyManager implements X509KeyManager {

  private X509KeyManager mgr;
  private String alias;
  private KeyStore ks;
  private char[] passphrase;
  private SSLContext sslContext;

  public RefreshingKeyManager(KeyStore ks, char[] passphrase,
      SSLContext sslContext) throws UnrecoverableKeyException,
      KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
    this.ks = ks;
    this.passphrase = passphrase;
    this.sslContext = sslContext;
    this.refresh();
  }

  void refresh() throws UnrecoverableKeyException, KeyStoreException,
      NoSuchAlgorithmException, KeyManagementException {
    KeyManagerFactory keyFactory = KeyManagerFactory.getInstance("SunX509");
    keyFactory.init(this.ks, this.passphrase);

    KeyManager[] managers = { this };
    sslContext.init(managers, null, null);
    this.mgr = (X509KeyManager) keyFactory.getKeyManagers()[0];
  }

  @Override
  public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
    return mgr.chooseClientAlias(arg0, arg1, arg2);
  }

  @Override
  public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
    String serverAlias = mgr.chooseServerAlias(arg0, arg1, arg2);
    // all of our certificates are created with the same way in the same
    // keystore.
    // This allows us to trick the consumer into accepting whichever alias
    // suits us:
    if (null != serverAlias) {
      serverAlias = this.alias;
    }
    return serverAlias;
  }

  @Override
  public X509Certificate[] getCertificateChain(String arg0) {
    X509Certificate[] certs = mgr.getCertificateChain(arg0);
    return certs;
  }

  @Override
  public String[] getClientAliases(String keyType, Principal[] issuers) {
    return mgr.getClientAliases(keyType, issuers);
  }

  @Override
  public PrivateKey getPrivateKey(String alias) {
    return mgr.getPrivateKey(alias);
  }

  @Override
  public String[] getServerAliases(String keyType, Principal[] issuers) {
    return mgr.getServerAliases(keyType, issuers);
  }

  public void switchAlias(String hostName) {
    this.alias = hostName;
  }

  public String getAlias() {
    return this.alias;
  }
}
