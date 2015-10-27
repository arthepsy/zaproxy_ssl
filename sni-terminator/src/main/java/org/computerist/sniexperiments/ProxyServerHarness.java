package org.computerist.sniexperiments;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.computerist.snitools.ProxyForwarder;
import org.computerist.snitools.SNITerminator;
import org.computerist.ssltools.zap.ZAPSslToolsWrapper;

public class ProxyServerHarness {
  public static void main(String[] args) {
    try {
      String ZAP_CA = "";
      InetAddress listenAddress = Inet4Address.getByName("0.0.0.0");
      int listenPort = 8443;
      InetAddress proxyAddress = Inet4Address.getLocalHost();
      int proxyPort = 8080;

      KeyStore caks = ZAPSslToolsWrapper.getService().string2Keystore(ZAP_CA);

      SSLContext clientContext = SSLContext.getInstance("TLS");
      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(caks);

      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(caks, ZAPSslToolsWrapper.getService().getPassphrase());
      clientContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
      SSLSocketFactory clientSSLSocketFactory = clientContext
          .getSocketFactory();

      SNITerminator terminator = new SNITerminator(caks,
          ZAPSslToolsWrapper.getService().getPassphrase(), listenAddress, listenPort,
          new ProxyForwarder(proxyAddress, proxyPort, clientSSLSocketFactory));
      terminator.start();

    } catch (UnknownHostException uhe) {
      uhe.printStackTrace();
    } catch (KeyStoreException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (CertificateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (UnrecoverableKeyException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    }
  }
}
