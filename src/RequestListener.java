
import java.net.ServerSocket;
import java.net.Socket;

class RequestListener implements Runnable {

  private ServerSocket serverSocket;

  public RequestListener(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }

  public void run() {
    Socket clientSocket = null;
    ClientRequest clientRequest = null;
    Thread requestThread = null;
    while (true) {
      try {
        clientSocket = serverSocket.accept();
        clientRequest = new ClientRequest(clientSocket);
        requestThread = new Thread(clientRequest);
        requestThread.start();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

}
