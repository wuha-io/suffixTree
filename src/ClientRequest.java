
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

final class ClientRequest implements Runnable {

  private Socket clientSocket;

  public ClientRequest(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void run() {
    try {
      // reading request parameters
      InputStreamReader isr = new InputStreamReader(this.clientSocket.getInputStream());
      BufferedReader br = new BufferedReader(isr);
      String str = br.readLine();
      int minLength = Integer.parseInt(br.readLine());
      int minSupport = Integer.parseInt(br.readLine());
      // building the tree
      int strLength = str.length();
      SuffixTree tree = new SuffixTree(strLength);
      for (int i = 0; i < strLength; i++)
        tree.addChar(str.charAt(i));
      // extracting the patterns
      Map<String, Integer> treePatterns = tree.getPatterns(minLength, minSupport);
      // sending the patterns one by one
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
      Iterator <String> keys = treePatterns.keySet().iterator();
      while (keys.hasNext()) {
        String pattern = keys.next();
        int support = treePatterns.get(pattern);
        out.println(support + ":" + pattern);
        out.flush();
      }
      this.clientSocket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
