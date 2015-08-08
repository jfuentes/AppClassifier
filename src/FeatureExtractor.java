import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


public class FeatureExtractor {
	/*
	 * Metadata is on text files with the following structure:
	 * 		- An application starts with its name after the '#' symbol
	 * 		- the following lines contain the application installation where each line contains separate by ',': 
	 * 		  > filename with its path
	 * 		  > size
	 * 		  > modification time
	 * 		  > ctime
	 * 		  > uid
	 * 		  > gid
	 */
	
	public static Double[] extractFeaturesOnTree(DefaultMutableTreeNode root){
		return null;
	}
	
	
	/*
	 * Structural features
	 */
	private int getNumberOfLevels(DefaultMutableTreeNode root){
		return root.getLastLeaf().getLevel();
	}
	
	
	
	
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
