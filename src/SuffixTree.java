
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

class SuffixTree {

 final int oo = Integer.MAX_VALUE / 2;

 private Node[] nodes;
 private char[] text;

 private int root;
 private int position = -1;
 private int currentNode;
 private int needSuffixLink;
 private int remainder;

 private int active_node;
 private int active_length;
 private int active_edge;

 private TreeMap<String, Integer> repeatedSubstrings = new TreeMap<String, Integer> ();

 class Node {

  private int start;
  private int end = oo;
  private int link;

  private int nodeNb = 0;
  private int parentNode = root;
  private int support = 0;

  public TreeMap<Character, Integer> next = new TreeMap<Character, Integer>();

  public Node(int start, int end, int the_nodeNb) {
   this.start = start;
   this.end = end;
   nodeNb = the_nodeNb;
  }

  public void setFinalParentNode(int leParent) {
   parentNode = leParent;
  }

  public int edgeLength() {
   return Math.min(end, position + 1) - start;
  }

  public int getNodeSupport() {
   return support;
  }

  public void set_node_support(int supportValue) {
   support = supportValue;
  }
 }

 public SuffixTree(int length) {
  nodes = new Node[2 * length + 2];
  text = new char[length];
  root = active_node = newNode(-1, -1);
 }

 private void addSuffixLink(int node) {
  if (needSuffixLink > 0)
   nodes[needSuffixLink].link = node;
  needSuffixLink = node;
 }

 public char getActiveEdge() {
  return text[active_edge];
 }

 public boolean walkDown(int next) {
  if (active_length >= nodes[next].edgeLength()) {
   active_edge += nodes[next].edgeLength();
   active_length -= nodes[next].edgeLength();
   active_node = next;
   return true;
  }
  return false;
 }

 public int newNode(int start, int end) {
  ++currentNode;
  nodes[currentNode] = new Node(start, end, currentNode);
  return currentNode;
 }

 public String edgeString(int node) {
  return new String(Arrays.copyOfRange(text, nodes[node].start, Math.min(position + 1, nodes[node].end)));
 }

 public void addChar(char c) throws Exception {
  text[++position] = c;
  needSuffixLink = -1;
  remainder++;
  while (remainder > 0) {
   if (active_length == 0) active_edge = position;
   if (!nodes[active_node].next.containsKey(getActiveEdge())) {
    int leaf = newNode(position, oo);
    nodes[active_node].next.put(getActiveEdge(), leaf);
    // nodes[leaf].setParentNode(active_node);
    addSuffixLink(active_node);
   } else {
    int next = nodes[active_node].next.get(getActiveEdge());
    if (walkDown(next)) continue;
    if (text[nodes[next].start + active_length] == c) { //observation 1
     active_length++;
     addSuffixLink(active_node);
     break;
    }
    int split = newNode(nodes[next].start, nodes[next].start + active_length);
    nodes[active_node].next.put(getActiveEdge(), split);
    //nodes[split].setParentNode(active_node);
    int leaf = newNode(position, oo);
    nodes[split].next.put(c, leaf);
    // nodes[leaf].setParentNode(split);
    nodes[next].start += active_length;
    nodes[split].next.put(text[nodes[next].start], next);
    addSuffixLink(split);
   }
   remainder--;
   if (active_node == root && active_length > 0) { //rule 1
    active_length--;
    active_edge = position - remainder + 1;
   } else
    active_node = nodes[active_node].link > 0 ? nodes[active_node].link : root;
  }
 }

 public void calculateSupportsAndSubstrings(int x, String curr_string, int minLength, int minSupport) {
  if (x == 0) x = root;
  if (nodes[x].next.size() == 0) {
   // leaf
   nodes[x].support = 1;
  } else {
   for (int child: nodes[x].next.values()) {
    String actualString = curr_string + edgeString(child);
    calculateSupportsAndSubstrings(child, actualString, minLength, minSupport);
    Node childN = nodes[child];
    nodes[x].support += childN.support;
    if (childN.getNodeSupport() >= minSupport && actualString.length() >= minLength)
     repeatedSubstrings.put(actualString, nodes[child].getNodeSupport());
    //updateRepeatedSubstrings(child,curr_string+edgeString(child));
   }
  }
 }

 public Map<String, Integer> getPatterns(int minLength, int minSupport) {
  repeatedSubstrings.clear();
  calculateSupportsAndSubstrings(0, "", minLength, minSupport);
  return repeatedSubstrings;
 }
}
