package org.computerist.snitools;

import java.io.InputStream;
import java.io.OutputStream;

public interface Forwarder {
  public void forward(InputStream inputStream, OutputStream outputStream, String host, Tidier tidier);
}
