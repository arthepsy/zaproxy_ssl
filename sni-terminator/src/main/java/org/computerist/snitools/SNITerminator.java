package org.computerist.snitools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;

import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.StandardConstants;

import org.computerist.ssltools.zap.ZAPSslToolsWrapper;

public class SNITerminator {
  private InetAddress listenAddress;
  private int listenPort;
  private Forwarder forwarder;
  private KeyStore caks;
  private char[] keyStorePass;
  private boolean serverRunning;

  public SNITerminator(KeyStore keyStore, char[] keyStorePass,
      InetAddress listenAddress, int listenPort, Forwarder forwarder) {
    this.keyStorePass = keyStorePass;
    this.listenAddress = listenAddress;
    this.listenPort = listenPort;
    this.forwarder = forwarder;
    this.caks = keyStore;
  }

  public void start() {
    try {
      if (!serverRunning) {
        ZAPSslToolsWrapper scs = ZAPSslToolsWrapper.getService();
        scs.initializeRootCA(caks);

        KeyStore ks = scs.getHostKeyStore();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        RefreshingKeyManager mgr = new RefreshingKeyManager(ks, keyStorePass,
            sslContext);

        SSLServerSocketFactory sslServerSocketFactory = sslContext
            .getServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory
            .createServerSocket(this.listenPort, 10, this.listenAddress);

        Thread serverThread = new Thread(new Runnable() {
          @Override
          public void run() {
            serverRunning = true;
            while (serverRunning) {
              try {
                final SSLSocket sslSocket = (SSLSocket) sslServerSocket
                    .accept();

                SSLParameters params = sslSocket.getSSLParameters();

                SNIMatcher matcher = new SNIMatcher(
                    StandardConstants.SNI_HOST_NAME) {
                  @Override
                  public boolean matches(SNIServerName serverName) {
                    synchronized (sslServerSocket) {
                      String hostName = new String(serverName.getEncoded());
                      try {
                        if (!ks.containsAlias(hostName)) {
                          System.out.println(hostName
                              + " is a new alias; adding");
                          scs.createCertForHost(hostName);
                          mgr.refresh();
                        }
                        mgr.switchAlias(hostName);
                      } catch (Exception e) {
                        e.printStackTrace();
                      }
                    }
                    return true;
                  }
                };
                Collection<SNIMatcher> matchers = new ArrayList<>(1);
                matchers.add(matcher);
                params.setSNIMatchers(matchers);
                sslSocket.setSSLParameters(params);

                final InputStream serverIn = sslSocket.getInputStream();
                final OutputStream serverOut = sslSocket.getOutputStream();

                String host = mgr.getAlias();

                Tidier tidier = new Tidier() {
                  @Override
                  public void tidyUp() {
                    if (null != sslSocket && !sslSocket.isClosed()) {
                      try {
                        sslSocket.close();
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    }
                  }
                };
                forwarder.forward(serverIn, serverOut, host, tidier);
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
            try {
              sslServerSocket.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
        serverThread.setDaemon(true);
        serverThread.setName("SNITerminator");
        serverThread.start();
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void stop() {
    this.serverRunning = false;
  }

  public boolean isRunning() {
    return this.serverRunning;
  }
}
