package org.computerist.snitools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

public class ProxyForwarder extends BaseStreamForwarder{
  private InetAddress proxyAddress;
  private int proxyPort;

  private SSLSocketFactory sslSocketFactory;

  public ProxyForwarder(InetAddress proxyAddress, int proxyPort, SSLSocketFactory sslSocketFactory) {
    this.proxyAddress = proxyAddress;
    this.proxyPort = proxyPort;
    this.sslSocketFactory = sslSocketFactory;
  }

  @Override
  public void forward(InputStream serverIn, OutputStream serverOut, String host, Tidier serverTidier) {
    
    try {
    Socket plainSocket = ProxyConnectSocketFactory.GetSocket(host,
        443, proxyAddress, proxyPort);

    Socket socket = this.sslSocketFactory.createSocket(plainSocket,
        null, plainSocket.getPort(), false);

    final OutputStream clientOut = socket.getOutputStream();
    final InputStream clientIn = socket.getInputStream();
    Tidier tidier = new Tidier(){
      @Override
      public void tidyUp() {
        if(null!=socket && !socket.isClosed()) {
          try {
            socket.close();
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            if(null != serverTidier) {
              serverTidier.tidyUp();
            }
          }
        }
      }
    };
    connectStreams(serverIn, serverOut, clientIn, clientOut, tidier);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
}
