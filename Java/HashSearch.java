package de.ur.adp.search;
import de.ur.adp.linkedlist.Node;

//Melissa Zierer
// 2344931

public class HashSearch extends AbstractSearch<String> {
    private Node<String>[] hashTable;

    public HashSearch(String[] content) {
        super(content);
        hashTable = new Node[Character.MAX_VALUE];
        createTable(content);
    }

    private void createTable(String [] content){
        for (int i = 0; i < content.length; i++) {          //goes through the whole list of names
            // runtime O(n) with n = content because every value is checked
            int letter = hash(content[i]);                  // letter is the hash value of the current name
            int index = (letter - (int) 'A');               // is the numeric value of the letter
            // constant runtime
            if(hashTable[index] == null){                   // if the hashTable at the correct index is empty
                hashTable[index] = new Node<>(content[i]);  // name is saved there
            // runtime here is constant too
            }else{                                          // in case of collision:
                Node<String> current = hashTable[index];    // current is the already saved name at the index

                while (current.getNextNode() != null){      // as long as there are still names linked
                current = current.getNextNode();            // current iterates through the list
            }                                               // at the end of that list:
                current.setNextNode(new Node<>(content[i])); // the new name is added to the list linked to the correct hash index
            }
            // runtime grows in a linear way with every added node that has to be iterated through
        }
        toString(hashTable);
    }

    @Override
    public String[] find(String key) {
        char letter = hash(key);
        int index = (letter - (int) 'A');
        // constant runtime

        Node<String> current = hashTable[index];               // Node with the name at the current index
        while (current != null) {                              // as long as there is a name
            if (current.getContent().equals(key)) {            // checks if for the required name
                return new String[]{current.getContent()};     // returns that name
            }
            current = current.getNextNode();                   // iterates through the nodes until name is found, or
        }
        // runtime depends on the index where the key is in the list
        // so estimated runtime is 0(n) with n = length of the list at the node
        // so best case is O(1) and worst case is O(n) with n being the last node in the linked list
        return null;                                          // returns null if not found
    }

    private char hash(String key){
        char hash = key.charAt(0);                          // hash value is the first letter of the name
        hash = Character.toUpperCase(hash);                 // in upper case
        return hash;
    }

    public void toString(Node<String> [] hashTable){                   //not sure if necessary?

        for (int i = 0; i < hashTable.length; i++) {
            Node<String> current = hashTable[i];
            StringBuilder builder = new StringBuilder();
            builder.append("Index ").append(i).append(": ");

            while (current != null) {
                builder.append(current.getContent()).append(" -> ");
                current = current.getNextNode();
            }

            builder.append(" ");
            System.out.println(builder);
        }
    }
}
