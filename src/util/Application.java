package util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class Application{
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
	
	private Integer[] rankingFileOcurrence = new Integer[5];
	private Integer[] fileOcurrence = new Integer[5];
	
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
	      // Get an iterator
	      Iterator i = set.iterator();
	      // Display elements
	      int j=0;
	      while(i.hasNext() && j<5) {
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
		if(rankingBySize!=null){
			for(int i=0; i<rankingBySize.length; i++){
				totalSize+=rankingBySize[i].size;
			}
		}
		
	}
	
	public void computeFileFolderDistribution(){
		int distribution[][] = new int[this.numLevels+1][2]; //distribution of files (0) and folders (0) by levels
		Enumeration<DefaultMutableTreeNode> children=root.depthFirstEnumeration();
		
		int totalFiles=0, totalFolders=0;
		
		divisor=numLevels<=distributionSize?numLevels:(numLevels/distributionSize)+1;
		
		while(children.hasMoreElements()){
			DefaultMutableTreeNode child = children.nextElement();
			if(child.isLeaf()){
				distribution[child.getLevel()][0]++;
				totalFiles++;
			}else{
				distribution[child.getLevel()][1]++;
				totalFolders++;
			}
		}
		
		fileDistribution = new double[distributionSize];
		folderDistribution = new double[distributionSize];
		
		
		if(numLevels<distributionSize)
		for(int i=0; i<numLevels; i++){
			fileDistribution[(int) i/divisor]+=distribution[i][0];
			folderDistribution[(int) i/divisor]+=distribution[i][1];
		}
		else for(int i=0; i<numLevels; i++){
			fileDistribution[(int) i/divisor]+=distribution[i][0];
			folderDistribution[(int) i/divisor]+=distribution[i][1];
		}
		
		
		for(int i=0; i<distributionSize; i++){
			fileDistribution[i]/=totalFiles;
			folderDistribution[i]/=totalFolders;
		}
		
	}

	
	
	
	

	
}
