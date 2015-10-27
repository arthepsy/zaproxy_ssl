package org.computerist.sniexperiments;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.computerist.snitools.SNITerminator;
import org.computerist.snitools.SingleTCPForwarder;
import org.computerist.ssltools.zap.ZAPSslToolsWrapper;

public class TCPServerHarness {
  public static void main(String args[]) {
    try {
      String ZAP_CA = "";
      InetAddress listenAddress = Inet4Address.getByName("0.0.0.0");
      int listenPort = 8443;
      InetAddress serverAddress = Inet4Address.getLocalHost();
      int serverPort = 80;

      KeyStore caks = ZAPSslToolsWrapper.getService().string2Keystore(ZAP_CA);

      SNITerminator terminator = new SNITerminator(caks,
          ZAPSslToolsWrapper.getService().getPassphrase(), listenAddress, listenPort,
          new SingleTCPForwarder(serverAddress, serverPort));
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
    }
  }
}
