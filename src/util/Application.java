package util;


import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;


public class Application{
	public static final int NUM_FEATURES = 36;
	
	private String name;
	private DefaultMutableTreeNode root;
	private int totalNodes;
	private int numLevels;
	private int numDiffFileCodes;
	
	//subdirectories with
	private int onlyFiles, onlyFolders, filesAndFolders;
	private boolean computedStatDirectories=false;
	
	//file and folder distribution by levels
	private double fileDistribution[];
	private double folderDistribution[];
	private int divisor;
	private int distributionSize=5;
	
	/*
	 * boolean array of folder presence
	 * positions = 0:bin, 1:lib, 2:classes, 3:doc, 4:include 
	 */
	private int[] folderPresence;
	
	private int maxNumFilesInDir;
	private int maxNumFoldersInDir;
	
	//statistics of files ocurrence
	private int fileHighestOccurrence;
	private int numHighiestOcurrence;
	
	private int[] rankingFileOcurrence;
	private int[] fileOcurrence;
	
	private NodeInfo[] rankingBySize;
	
	private long totalSize;
	
	public Application(String name, DefaultMutableTreeNode root){
		this.name=name;
		this.root=root;
	}
	
	public Application(String name){
		this.name=name;
		numLevels=0;
	}

	public void setRoot(DefaultMutableTreeNode root) {
		// TODO Auto-generated method stub
		this.root=root;
	}
	
	public void setTotalNodes(int countNodes) {
		// TODO Auto-generated method stub
		totalNodes=countNodes;
	}
	
	public String toString(){
		String s="";
		computeStatisticsSubdirectories();
		s+= name+" "+totalNodes+" "+getNumberOfFiles()+" "+getNumberOfSubdirectories()+" "+numLevels+" "+numDiffFileCodes+" "+onlyFiles+" "+onlyFolders+" "+filesAndFolders+
				" "+this.maxNumFilesInDir+" "+this.maxNumFoldersInDir+" "+this.rankingFileOcurrence[0]+"("+this.fileOcurrence[0]+") "+this.rankingFileOcurrence[1]+"("+this.fileOcurrence[1]+") "+this.rankingFileOcurrence[2]+"("+this.fileOcurrence[2]+") "
				+this.rankingBySize[0].extension+"("+this.rankingBySize[0].size+") "+this.rankingBySize[rankingBySize.length-1].extension+"("+this.rankingBySize[rankingBySize.length-1].size+") "+this.folderPresence[0]+" "+this.folderPresence[1]+" "+this.folderPresence[2]+" "
				+this.folderPresence[3]+" "+this.folderPresence[4]+" "+this.folderPresence[5]+" "+((double)totalSize/1000)+" ";
		for(int i=0; i<distributionSize;i++){
			s+=fileDistribution[i]+"|";
		}
		s+=" ";
		for(int i=0; i<distributionSize;i++){
			s+=folderDistribution[i]+"|";
		}
		s+=" ";
		
		return s;
	}
	
	public String getName(){
		return name;
	}
	
	public void setMaxNumberLevels(int levels) {
		// TODO Auto-generated method stub
		if(levels>numLevels)
			numLevels=levels;
	}

	public void setNumDiffFileCodes(int num) {
		// TODO Auto-generated method stub
		numDiffFileCodes = num;
	}
	
	//features
	
	//number of external nodes (files)
	public int getNumberOfFiles(){
		return root.getLeafCount();
	}
	
	//number of internal nodes (sub-directories)
	public int getNumberOfSubdirectories(){
		return totalNodes-root.getLeafCount();
	}
	
	//number of total nodes
	public int getNumberTotalNodes(){
		return totalNodes;
	}
	
	//number of levels
	public int getMaxNumberLevels(){
		return numLevels;
	}
	
	//number of sub-directories with only files
	public int getSubDirWithFiles(){
		if(!computedStatDirectories)
			computeStatisticsSubdirectories();
		return onlyFiles;
	}
	
	//number of sub-directories with only files
	public int getSubDirWithFolders(){
		if(!computedStatDirectories)
			computeStatisticsSubdirectories();
		return onlyFolders;
	}
	
	//number of sub-directories with only files
	public int getSubDirWithFilesAndFolders(){
		if(!computedStatDirectories)
			computeStatisticsSubdirectories();
		return filesAndFolders;
	}
	
	
		
	private void computeStatisticsSubdirectories(){
		if(!computedStatDirectories){
			Enumeration<DefaultMutableTreeNode> enume=root.breadthFirstEnumeration();
			while(enume.hasMoreElements()){
				DefaultMutableTreeNode node = enume.nextElement();
	
				int files=0, folders=0, cont=0;
				if(!node.isLeaf()){
					//System.out.println((node.toString()));
					Enumeration<DefaultMutableTreeNode> children=node.children();
					while(children.hasMoreElements()){
						cont++;
						DefaultMutableTreeNode child = children.nextElement();
						if(child.isLeaf())
							files++;
						else folders++;
					}
					if(files==cont)
						onlyFiles++;
					else if(folders==cont)
						onlyFolders++;
					else filesAndFolders++;
					//System.out.println(cont);
					
					//maximum number of files in a sub-directory
					if(files>this.maxNumFilesInDir)
						maxNumFilesInDir=files;
					
					//maximum number of sub-directories in a sub-directory
					if(folders>this.maxNumFoldersInDir)
						maxNumFoldersInDir=folders;
				}
				
			}
		}
		computedStatDirectories=true;
	}

	public void setFilesStatistics(LinkedHashMap<Integer, Integer> fileStatistics2) {
		// TODO Auto-generated method stub
		
		//set the file with the highest occurrence
		
		// Get a set of the entries
	      Set set = fileStatistics2.entrySet();
	      
	      rankingFileOcurrence = new int[set.size()>5?set.size():5];
	      fileOcurrence = new int[set.size()>5?set.size():5];
	      // Get an iterator
	      Iterator i = set.iterator();
	      // Display elements
	      int j=0;
	      while(i.hasNext()) {
	         Map.Entry me = (Map.Entry)i.next();
	         this.rankingFileOcurrence[j] = (Integer) me.getKey();
	 		 this.fileOcurrence[j] = (Integer) me.getValue();
	         j++;
	      }
		
		
	}

	public void setSizeRanking(LinkedHashMap fileSizes2) {
		// TODO Auto-generated method stub
		
	}

	public void setSizeRanking(NodeInfo[] array) {
		// TODO Auto-generated method stub
		this.rankingBySize=array;
	}

	public void setFolderPresence(int[] folderPresence) {
		// TODO Auto-generated method stub
		this.folderPresence=folderPresence;
	}

	public void computeTotalSize() {
		// TODO Auto-generated method stub
		
			for(int i=0; i<rankingBySize.length; i++){
				totalSize+=rankingBySize[i].size;
			}
		
		
	}
	
	public void computeFileFolderDistribution(){
		int levels = this.numLevels-1;
		int distribution[][] = new int[levels][2]; //distribution of files (0) and folders (0) by levels
		Enumeration<DefaultMutableTreeNode> children=root.breadthFirstEnumeration();
		
		int totalFiles=0, totalFolders=0;
		
		divisor=levels<=distributionSize?levels:(this.numLevels/distributionSize)+1;
		
		children.nextElement(); //get the root, not needed
		while(children.hasMoreElements()){
			DefaultMutableTreeNode child = children.nextElement();
			if(child.isLeaf()){
				distribution[child.getLevel()-2][0]++;
				totalFiles++;
			}else{
				distribution[child.getLevel()-2][1]++;
				totalFolders++;
			}
		}
		
		
		
		fileDistribution = new double[distributionSize];
		double [] fileDistribution2 = new double[distributionSize];
		folderDistribution = new double[distributionSize];
		double [] folderDistribution2 = new double[distributionSize];
		
		
		if(levels<distributionSize)
			for(int i=0; i<levels; i++){
				fileDistribution2[i]+=distribution[i][0];
				folderDistribution2[i]+=distribution[i][1];
			}
		else for(int i=0; i<levels; i++){
				fileDistribution2[(int) i/divisor]+=distribution[i][0];
				folderDistribution2[(int) i/divisor]+=distribution[i][1];
			}
		
		
		for(int i=0; i<distributionSize; i++){
			fileDistribution[i]=fileDistribution2[i]/totalFiles;
			folderDistribution[i]=folderDistribution2[i]/totalFolders;
		}
		
		/*
		NumberFormat formatter = new DecimalFormat("#0.00"); 
		System.out.println("Distribution... levels:"+levels);
		if(levels<distributionSize)
		for(int i=0; i<levels; i++){
			System.out.println("Level "+i+"    files: "+fileDistribution2[i]+" ("+formatter.format(fileDistribution[i])+")      folders: "+folderDistribution2[i]+" ("+formatter.format(folderDistribution[i])+")");
		}
		else for(int i=0; i<distributionSize; i++){
			System.out.println("Level "+i+"    files: "+fileDistribution2[i]+" ("+formatter.format(fileDistribution[i])+")      folders: "+folderDistribution2[i]+" ("+formatter.format(folderDistribution[i])+")");
		}
		System.out.println();
		*/
	}

	
	
	public double[] getFeatureVector(){
		computeStatisticsSubdirectories();
		
		//returns the vector of features
		double [] features = new double[NUM_FEATURES];
		
		//for feature meaning, see features.txt
		
		/*
		 * Structural features
		 */
		features[0] = numLevels; //number of levels
		features[1] = getNumberOfSubdirectories(); //number of internal nodes (sub-directories)
		features[2] = getNumberOfFiles(); //number of external nodes (files)
		features[3] = onlyFiles; //number of sub-directories with only files
		features[4] = onlyFolders; //number of sub-directories with only sub-directories
		features[5] = filesAndFolders; //number of sub-directories with files and sub-directories
		
		features[6] = fileDistribution[0]; //file distribution (top to low level)
		features[7] = fileDistribution[1]; //file distribution (top to low level)
		features[8] = fileDistribution[2]; //file distribution (top to low level)
		features[9] = fileDistribution[3]; //file distribution (top to low level)
		features[10] = fileDistribution[4]; //file distribution (top to low level)
		
		features[11] = folderDistribution[0]; //folder distribution (top to low level)
		features[12] = folderDistribution[1]; //folder distribution (top to low level)
		features[13] = folderDistribution[2]; //folder distribution (top to low level)
		features[14] = folderDistribution[3]; //folder distribution (top to low level)
		features[15] = folderDistribution[4]; //folder distribution (top to low level)

		
		/*
		 * Statistical features
		 */
		features[16] = maxNumFilesInDir; //maximum number of files in a sub-directory
		features[17] = maxNumFoldersInDir; //maximum number of sub-directories in a sub-directory (one level)
		
		features[18] = rankingFileOcurrence[0]; //file type with maximum occurrence (code of file type) #1
		features[19] = rankingFileOcurrence[1]; //file type with maximum occurrence (code of file type) #2
		features[20] = rankingFileOcurrence[2]; //file type with maximum occurrence (code of file type) #3
		features[21] = rankingFileOcurrence[3]; //file type with maximum occurrence (code of file type) #4
		features[22] = rankingFileOcurrence[4]; //file type with maximum occurrence (code of file type) #5

		features[23] = rankingBySize[0].codeFile; //ranking largest files (code of file type) #1
		features[24] = rankingBySize[1].codeFile; //ranking largest files (code of file type) #2
		features[25] = rankingBySize[2].codeFile; //ranking largest files (code of file type) #3
		features[26] = rankingBySize[3].codeFile; //ranking largest files (code of file type) #4
		features[27] = rankingBySize[4].codeFile; //ranking largest files (code of file type) #5
		
		features[28] = rankingBySize[0].size; //size biggest file (Kb)
		
		features[29] = numDiffFileCodes; //number of different file types
		features[30] = ((double)totalSize/1000); //total size installation
		
		
		/*
		 * Structural features
		 */
		
		/*
		 * boolean array of folder presence
		 * positions = 0:bin, 1:lib, 2:classes, 3:doc, 4:include 
		 */
		features[31] = folderPresence[0]; //bin
		features[32] = folderPresence[1]; //lib
		features[33] = folderPresence[2]; //classes
		features[34] = folderPresence[3]; //doc
		features[35] = folderPresence[4]; //include
		
		return features;
	}

	public int [] getRankingFileOcurrence(){
		return rankingFileOcurrence;
	}
	
	public int [] getFileOcurrence(){
		return fileOcurrence;
	}
	
	public NodeInfo[] getRankingBySize(){
		return rankingBySize;
		
	}
	

	
}
