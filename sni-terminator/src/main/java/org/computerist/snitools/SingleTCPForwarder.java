package org.computerist.snitools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SingleTCPForwarder extends BaseStreamForwarder {

  private InetAddress serverAddress;
  private int serverPort;

  public SingleTCPForwarder(InetAddress serverAddress, int serverPort) {
    this.serverAddress = serverAddress;
    this.serverPort = serverPort;
  }

  @Override
  public void forward(InputStream inputStream, OutputStream outputStream,
      String host, Tidier serverTidier) {
    try {
      Socket socket = new Socket(this.serverAddress, this.serverPort);
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
      this.connectStreams(inputStream, outputStream, socket.getInputStream(), socket.getOutputStream(), tidier);
    } catch (IOException e) {
      e.printStackTrace();
    }
    

  }

}
