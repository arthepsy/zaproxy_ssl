package org.computerist.snitools;

/*
 * Get a socket via HTTP CONNECT.
 * Derived from : https://code.google.com/p/java-socket-over-http-proxy-connect/
 *              : http://stackoverflow.com/a/15869788
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public final class ProxyConnectSocketFactory {
  public static Socket GetSocket(String host, int port) throws IOException {
    /*************************
     * Get the jvm arguments
     *************************/

    int proxyPort = Integer.parseInt(System.getProperty("http.proxyPort"));
    String proxyHost = System.getProperty("http.proxyHost");
    return GetSocket(host, port, Inet4Address.getByName(proxyHost), proxyPort);
  }

  public static Socket GetSocket(String host, int port, InetAddress hostAddress,
      int proxyPort) throws IOException {
    // Socket object connecting to proxy
    Socket sock = new Socket(hostAddress, proxyPort);

    /***********************************
     * HTTP CONNECT protocol RFC 2616
     ***********************************/
    String proxyConnect = "CONNECT " + host + ":" + port+" HTTP/1.1" + "\n\n";

    //System.out.println(proxyConnect);
    sock.getOutputStream().write(proxyConnect.getBytes());
    /***********************************/

    /***************************
     * validate HTTP response.
     ***************************/
    byte[] tmpBuffer = new byte[512];
    InputStream socketInput = sock.getInputStream();

    int len = socketInput.read(tmpBuffer, 0, tmpBuffer.length);

    if (len == 0) {
      throw new SocketException("Invalid response from proxy");
    }

    String proxyResponse = new String(tmpBuffer, 0, len, "UTF-8");

    // Expecting HTTP/1.x 200 OK
    if (proxyResponse.indexOf("200") != -1) {

      // Flush any outstanding message in buffer
      if (socketInput.available() > 0)
        socketInput.skip(socketInput.available());

      // Proxy Connect Successful, return the socket for IO
      return sock;
    } else {
      throw new ProxyConnectSocketException("Fail to create Socket",
          proxyResponse);
    }
  }
}

class ProxyConnectSocketException extends IOException {
  public ProxyConnectSocketException(String string, String proxyResponse) {
    super(string + ": " + proxyResponse);
  }

  /**
   * 
   */
  private static final long serialVersionUID = 191084721553349199L;

}