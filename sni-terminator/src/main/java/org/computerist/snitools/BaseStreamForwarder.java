package org.computerist.snitools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BaseStreamForwarder implements Forwarder {
  static int buf_size=4096;
  
  protected void connectStreams(InputStream serverIn, OutputStream serverOut,
      InputStream clientIn, OutputStream clientOut, Tidier tidier) {
    Thread upThread = new Thread(new Runnable() {
      @Override
      public void run() {
        byte[] buf = new byte[buf_size];
        try {
          for (int read = 0; read != -1; read = serverIn.read(buf, 0,
              buf_size)) {
            clientOut.write(buf, 0, read);
          }
        } catch (IOException e) {
        } finally {
          tidier.tidyUp();
        }
      }
    });
    upThread.start();

    Thread downThread = new Thread(new Runnable() {
      @Override
      public void run() {
        byte[] buf = new byte[buf_size];
        try {
          for (int read = 0; read != -1; read = clientIn.read(buf, 0,
              buf_size)) {
            serverOut.write(buf, 0, read);
          }
        } catch (IOException e) {
        } finally {
          tidier.tidyUp();
        }
      }
    });
    downThread.start();
  }
}
