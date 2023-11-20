import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
public class PA3{
	/**
	 * findSmallestTree finds the smallest tree in a given forest, allowing for a single skip
	 * @param forest list containing all huffman trees
	 * @return the tree with the smallest weight
	 */
	public static int findSmallestTree(List<HuffmanTree<Character>> forest)
	{
		return findSmallestTree(forest, -1); //find the very smallest
	}

	/**
	 * findSmallestTree helper method finds the smallest tree in a given forest, allowing for a single skip
	 * @param forest list containing all huffman trees
	 * @param index_to_ignore index that will be skipped
	 * @return the tree with the smallest weight
	 */
	public static int findSmallestTree(List<HuffmanTree<Character>> forest, int index_to_ignore) 
	{
		int min = forest.get(0).getWeight(); // get base minumum weight and index
		int minIndex = 0;
		if(index_to_ignore == 0) { // change if 0 should be ignored since there are always at least 2 items
			min = forest.get(1).getWeight();
			minIndex = 1;
		}
		for(int i = 0; i < forest.size(); i++) { // cycle through all elements to find the smallest weight/index
			if (forest.get(i).getWeight() < min && i != index_to_ignore) {
				min = forest.get(i).getWeight();
				minIndex = i;
			}
		}
		return minIndex; //find the smallest except the index to ignore.
	}

	/**
	 * huffmanTreeFromText generates a Huffman character tree from the supplied text
	 * @param data list of strings with characters going into the tree
	 * @return huffman tree with all characters
	 */
	public static HuffmanTree<Character> huffmanTreeFromText(List<String> data) {
		Map<Character, Integer> characterMap = new HashMap<Character, Integer>();
		// add every single character from every entry in list
		// add characters to map based on if they are already in the map
		// if they are already in the map, increase the frequency
		for(String line: data) {
			for(int i = 0; i < line.length(); i ++) {
				if(characterMap.containsKey(line.charAt(i))) {
					characterMap.replace(line.charAt(i), characterMap.get(line.charAt(i)), characterMap.get(line.charAt(i)) +1);
				}
				else {
					characterMap.put(line.charAt(i), 1);
				}
			}
		}

		List<HuffmanTree<Character>> forest = new ArrayList<HuffmanTree<Character>>();
		// create a new tree for each character with their
		// frequency as their weight, then add them to a list
		for(Character c: characterMap.keySet()) {
			HuffmanTree<Character> tree = new HuffmanTree<>(c, characterMap.get(c));
			forest.add(tree);
		}
		// merge the two smallest trees until there is only
		// 1 big tree left. Merge the smallest tree to the left
		// (left child) of every new tree.
		while(forest.size()>1) {
			int index1 = findSmallestTree(forest);
			int index2 = findSmallestTree(forest, index1);
			HuffmanTree<Character> merged = new HuffmanTree<Character>(forest.get(index1), forest.get(index2));
			forest.add(merged);
			if(index1 < index2) { // remove indexes to make sure right index is deleted
				forest.remove(index2);
				forest.remove(index1);
			}
			else {
				forest.remove(index1);
				forest.remove(index2);
			}
		}
		return forest.get(0); // return the last tree in the forest
	}

	/**
	 * huffmanTreeFromMap generates a Huffman character tree from the supplied encoding ma
	 * @param huffmanMap supplied encoding map from characters to binary code
	 * @return huffman tree with all characters
	 */
	public static HuffmanTree<Character> huffmanTreeFromMap(Map<Character, String> huffmanMap) {
		HuffmanInternalNode<Character> root = new HuffmanInternalNode<Character>(null, null); // create root
		HuffmanTree<Character> tree = new HuffmanTree<>(root); // create tree with above root
		// For every character in the map, get the code. Then
		// for every character in the code either traverse left or right
		// until the end of the code. Add a leaf node with the value
		// for the last index, then repeat.
		for(char c: huffmanMap.keySet()) {
			String code = huffmanMap.get(c);
			root = (HuffmanInternalNode<Character>) tree.getRoot();
            for(int i = 0; i < code.length()-1; i++){ // for loop until last index
				if(code.charAt(i)=='0') {
					if (root.getLeftChild() != null) { // if node has already been made, traverse there
						root = (HuffmanInternalNode<Character>) root.getLeftChild();
					}
					else { // if no node has already been made, create a new node then traverse there
						root.setLeftChild(new HuffmanInternalNode<Character>(null, null));
						root = (HuffmanInternalNode<Character>) root.getLeftChild();
					}
				}
				if(code.charAt(i)=='1') {
					if(root.getRightChild() != null) { // if node has already been made, traverse there
						root = (HuffmanInternalNode<Character>) root.getRightChild();
					}
					else { // if node has already been made, traverse there
						root.setRightChild(new HuffmanInternalNode<>(null, null));
						root = (HuffmanInternalNode<Character>) root.getRightChild();
					}
				}
			}
			// for when the last index has been reached
			if(code.charAt(code.length()-1)=='0'){
				HuffmanLeafNode<Character> leaf = new HuffmanLeafNode<>(c, 0);
				root.setLeftChild(leaf); // if last index is 0, add leaf to the left
			}
			else {
				HuffmanLeafNode<Character> leaf = new HuffmanLeafNode<>(c, 0);
				root.setRightChild(leaf); // if last index is 1, add leaf to the right
			}
		}
        return tree;
	}

	/**
	 * huffmanEncodingMapFromTree generates a Huffman encoding map from the supplied Huffman tree
	 * @param tree supplied huffman tree
	 * @return map from characters to binary code
	 */
	public static Map<Character, String> huffmanEncodingMapFromTree(HuffmanTree<Character> tree) {
		String code = ""; // create blank string
		HuffmanNode<Character> root = tree.getRoot(); // get root of tree
		Map<Character, String> result = new HashMap<>(); // make map from characters to codes

		return huffmanEncodingMapFromTreeHelper(root, code, result); // call helper
	}

	/**
	 * huffmanEncodingMapFromTreeHelper helper method that creates huffman map from a tree
	 * @param root root of tree
	 * @param code binary code of character
	 * @param result map from character to binary code
	 * @return map from character to binary code
	 */
	private static Map<Character, String> huffmanEncodingMapFromTreeHelper(HuffmanNode<Character> root, String code, Map<Character,String> result) {
		HuffmanInternalNode<Character> parent = (HuffmanInternalNode<Character>) root;
		if (parent.getLeftChild().isLeaf()) {
			HuffmanLeafNode<Character> lChild = (HuffmanLeafNode<Character>) parent.getLeftChild(); // if left child is leaf, put the value and code into map
			result.put(lChild.getValue(), code + "0");
		}
		if (parent.getRightChild().isLeaf()) {
			HuffmanLeafNode<Character> rChild = (HuffmanLeafNode<Character>) parent.getRightChild(); // if right child is leaf, put the value and code into map
			result.put(rChild.getValue(), code + "1");
		}
		if (!parent.getLeftChild().isLeaf()) {
			result = huffmanEncodingMapFromTreeHelper(parent.getLeftChild(), code + "0", result); // recursive call if child isn't a leaf
		}
		if(!parent.getRightChild().isLeaf()){
			result = huffmanEncodingMapFromTreeHelper(parent.getRightChild(), code + "1", result); // recursive call if child isn't a leaf
		}
		return result;
	}

	/**
	 * writeEncodingMapToFile method writes an encoding map to file.  Needed for decompression
	 * @param huffmanMap map from characters to their binary code
	 * @param file_name name of file
	 */
	public static void writeEncodingMapToFile(Map<Character, String> huffmanMap, String file_name) {
		//Writes the supplied encoding map to a file.  My map file has one 
		//association per line (e.g. 'a' and 001).  Each association is separated by 
		//a sentinel value.  In my case, I went with a double pipe (||).
		try {
			BufferedWriter console = new BufferedWriter(new FileWriter(file_name));
			for (Character c : huffmanMap.keySet()) {
				console.write(c + "||" + huffmanMap.get(c)); // seperate key and code by double bar
				console.newLine(); // new line after every entry
			}
			console.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * readEncodingMapFromFile method reads an encoding map from a file, needed for decompression
	 * @param file_name name of file
	 * @return map from characters to binary code
	 */
	public static Map<Character, String> readEncodingMapFromFile(String file_name) {
		// I had to use a slightly different search for the
		// files since I am on macOS, usually I would just use
		// a "C:" directory filepath but since I am on mac
		// I used the username, and direct path to my project
		Map<Character, String> result = new HashMap<>();
		String username = System.getProperty("user.name");
		String path = "/Users/" + username + "/IdeaProjects/PA3";
		File dir = new File(path);
		File file = fileFinder(dir, file_name);
		// after finding the file, scan the file line by line
		// split each line by the double bar, and put the values
		// into the map
		try {
			Scanner s = new Scanner(file);
			while(s.hasNextLine()){
				String line = s.nextLine();
				String[] values = line.split("\\|\\|");
				result.put(values[0].charAt(0), values[1]);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * fileFinder helper method to find files in computer
	 * @param dir filepath
	 * @param fileName name of file
	 * @return null if not found, file if found
	 */
	private static File fileFinder(File dir, String fileName){
		// goes to directory and gets list of all files
		// if files isn't null, then search all files in directory
		// if filename matches, return it
		if (dir.isDirectory()){
			File[] files = dir.listFiles();
			if(files != null) {
				for(File file: files) {
					if(file.isDirectory()) {
						fileFinder(file, fileName);
					}
					else if (file.getName().equals(fileName)) {
						File found = new File(file.getAbsolutePath());
						return found;
					}
				}
			}
		}
		return null;
	}

	/**
	 * decodeBits converts a list of bits (bool) back into readable text using the supplied Huffman map
	 * @param bits list of booleans representing text
	 * @param huffmanMap map from characters to their binary code
	 * @return the string formed by the list of boolean values
	 */
	public static String decodeBits(List<Boolean> bits, Map<Character, String> huffmanMap) {
		// created new tree using the map given
		// get the root of the tree
		HuffmanTree<Character> tree = huffmanTreeFromMap(huffmanMap);
		HuffmanNode<Character> root = tree.getRoot();
		StringBuilder result = new StringBuilder();
		// create x outside the loops so the last character isn't cut off of result
		int x;
		// for every element in the list, if it's a leaf append the value, if it isn't then traverse the tree
		for(x = 0; x < bits.size();) {
			if(!root.isLeaf()) {
				if (bits.get(x)) { // if bits.get(x) == true
					root = ((HuffmanInternalNode<Character>)root).getRightChild();
					x++;
				}
				else { // if bits.get(x) == false
					root = ((HuffmanInternalNode<Character>) root).getLeftChild();
					x++;
				}
			}
			else { // if root.isLeaf()
				result.append(((HuffmanLeafNode<Character>)root).getValue());
				root = tree.getRoot();
			}
		}
		// so the last character isn't cut off, if the root happens to be a leaf and x is
		// the size of the last element in bits, append it. I think this has to do with even
		// or odd numbers in bits
		if(root.isLeaf() && x == bits.size()) {
			result.append(((HuffmanLeafNode<Character>)root).getValue());
		}
		// use a StringBuilder to append results.
		return result.toString();
	}

	/**
	 * toBinary method uses the supplied Huffman map compression and converts the supplied text into a series of bits (boolean values)
	 * @param text list of strings in text being compressed
	 * @param huffmanMap map from characters to binary code
	 * @return list of boolean values representing text
	 */
	public static List<Boolean> toBinary(List<String> text, Map<Character, String> huffmanMap) {
		// create new list of booleans, and for every element in the list
		// get the key, get the code, then add true or false to the list
		// based on the code
		List<Boolean> result = new ArrayList<>();
        for (String word : text) {
			for (int i = 0; i < word.length(); i++) {
				Character key = word.charAt(i);
				String code = huffmanMap.get(key);
				for (int x = 0; x < code.length(); x++) {
					if (code.charAt(x) == '0') {
						result.add(false); // add false if 0, left on tree
					} else {
						result.add(true); // add true if 1, right on tree
					}
				}
			}
		}
		return result;
	}

}
