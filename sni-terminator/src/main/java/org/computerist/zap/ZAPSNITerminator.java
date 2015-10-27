package org.computerist.zap;

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

public class ZAPSNITerminator {
  private SNITerminator terminator;
  
  public ZAPSNITerminator(String ZAPRootCA, String serverAddressString, int serverPort, String proxyAddressString, int proxyPort) {
    try {
      InetAddress listenAddress = Inet4Address.getByName(serverAddressString);
      InetAddress proxyAddress = Inet4Address.getByName(proxyAddressString);

      KeyStore caks = ZAPSslToolsWrapper.getService().string2Keystore(ZAPRootCA);

      SSLContext clientContext = SSLContext.getInstance("TLS");
      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(caks);

      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(caks, ZAPSslToolsWrapper.getService().getPassphrase());
      clientContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
      SSLSocketFactory clientSSLSocketFactory = clientContext
          .getSocketFactory();

      terminator = new SNITerminator(caks,
          ZAPSslToolsWrapper.getService().getPassphrase(), listenAddress, serverPort,
          new ProxyForwarder(proxyAddress, proxyPort, clientSSLSocketFactory));

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
  
  public void start() {
    terminator.start();
  }
  
  public void stop() {
    terminator.stop();
  }
}
