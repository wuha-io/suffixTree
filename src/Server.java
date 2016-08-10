
import java.net.ServerSocket;

class Server {

  private Thread serverThread;

  public Server(int port) throws Exception {
    ServerSocket serverSocket = new ServerSocket(port);
    RequestListener requestListener = new RequestListener(serverSocket);
    this.serverThread = new Thread(requestListener);
	}

  public void start() {
    this.serverThread.start();
  }

}
