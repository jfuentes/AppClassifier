package util;

//Imports
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.tree.*;

class Directory	extends JFrame{
	// Instance attributes used in this example
	private	JPanel		topPanel;
	private	JTree		tree;
	private	JScrollPane scrollPane;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel treeModel;
	private char separator='\\';
	
	private HashMap<String, Integer> fileCodes;
	private HashMap<Integer, Integer> fileStatistics; //statistics per app  <fileCode, Statistics array>
	private ArrayList<NodeInfo> filesInfo; //statistics per app  <fileCode, Statistics array>
	
	private ArrayList<Application> applications = new ArrayList<Application>();
	
	/*
	 * boolean array of folder presence
	 * positions = 0:bin, 1:lib, 2:classes, 3:doc, 4:include 
	 */
	private int[] folderPresence;
	

	// Constructor of main frame
	public Directory()
	{
		fileCodes = new HashMap<String, Integer>();
		// Set the frame characteristics
		setTitle( "More Advanced Tree Application" );
		setSize( 300, 200 );
		setBackground( Color.gray );

		// Create a panel to hold all other components
		topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add( topPanel );

		// Create data for the tree
		root= new DefaultMutableTreeNode( new NodeInfo("Applications") );

		
		// Create a new tree control
		treeModel = new DefaultTreeModel( root );
		tree = new JTree( treeModel );


		// Add the listbox to a scrolling pane
		scrollPane = new JScrollPane();
		scrollPane.getViewport().add( tree );
		topPanel.add( scrollPane, BorderLayout.CENTER );
	
	}

	// Helper method to write an enitre suit of cards to the
	// current tree node
	public void addAllCard( DefaultMutableTreeNode suit )
	{
		suit.add( new DefaultMutableTreeNode( "Ace" ) );
		suit.add( new DefaultMutableTreeNode( "Two" ) );
		suit.add( new DefaultMutableTreeNode( "Three" ) );
		suit.add( new DefaultMutableTreeNode( "Four" ) );
		suit.add( new DefaultMutableTreeNode( "Five" ) );
		suit.add( new DefaultMutableTreeNode( "Six" ) );
		suit.add( new DefaultMutableTreeNode( "Seven" ) );
		suit.add( new DefaultMutableTreeNode( "Eight" ) );
		suit.add( new DefaultMutableTreeNode( "Nine" ) );
		suit.add( new DefaultMutableTreeNode( "Ten" ) );
		suit.add( new DefaultMutableTreeNode( "Jack" ) );
		suit.add( new DefaultMutableTreeNode( "Queen" ) );
		suit.add( new DefaultMutableTreeNode( "King" ) );
	}
	

	
	private int[] buildNodeFromString(String path, long size, long mtime, long ctime) {
        StringTokenizer stk=new StringTokenizer(path, "/");
        DefaultMutableTreeNode node, lastNode = null;
        lastNode = root;
        boolean cont=false;
        String str;
        int countNodes=0;
        int numLevels=stk.countTokens();
        
        
        while(stk.hasMoreTokens())  {
        	str = stk.nextToken();
            Enumeration<DefaultMutableTreeNode> enume =lastNode.children();
            while(enume.hasMoreElements()){
            	DefaultMutableTreeNode n = (DefaultMutableTreeNode) enume.nextElement();
            	if(((NodeInfo)n.getUserObject()).name.equalsIgnoreCase(str)){
            		lastNode=n;
            		cont=true;
            		break;
            	}
            }
            if(cont){
            	cont=false;
            	continue;
            
            }
            
            //is this token a file
            if(!stk.hasMoreTokens()){
            	StringTokenizer stk2 = new StringTokenizer(str, ".");
            	//System.out.println(str);
            	String extension="";
            	if(stk2.countTokens()>1)
            		while(stk2.hasMoreTokens())
            			extension=stk2.nextToken();
            	extension = extension.toLowerCase();
            	
            	int fileCode= fileCodes.containsKey(extension)==true?(int)fileCodes.get(extension): fileCodes.size();
            	if(!fileCodes.containsKey(extension))
            		fileCodes.put(extension, fileCodes.size());
            	NodeInfo nInfo = new NodeInfo(str, size, mtime, ctime, true, extension, fileCode);
                node = new DefaultMutableTreeNode(nInfo);
                filesInfo.add(nInfo);
                if(fileStatistics.containsKey(fileCodes.get(extension))){
                	fileStatistics.put(fileCodes.get(extension), fileStatistics.get(fileCodes.get(extension))+1);
                }else
                	fileStatistics.put(fileCodes.get(extension), 1);
        	}else{
        		node = new DefaultMutableTreeNode(new NodeInfo(str, size, mtime, ctime, false));
        		if(str.equals("bin"))
        			this.folderPresence[0]=1;
        		else if(str.equals("lib"))
        			this.folderPresence[1]=1;
        		else if(str.equals("classes"))
        			this.folderPresence[2]=1;
        		else if(str.equals("doc"))
        			this.folderPresence[3]=1;
        		else if(str.equals("include") || str.equals("inc"))
        			this.folderPresence[4]=1;
        		else if(str.equals("src"))
        			this.folderPresence[5]=1;
        	}
        	countNodes++;
            if(lastNode != null)
                lastNode.add(node);
            lastNode = node;
        }
        int[] features = { countNodes, numLevels};
        return features;
    }
	
	public void createApplicationsFromFile(String fileName){
		try (BufferedReader br = new BufferedReader(new FileReader(fileName)))
		{

			String sCurrentLine;
			String path, s;
			long size,mtime,ctime,uid,gid;
			Application app=null;
			boolean firstApp=true;
			int features[] = null;
			int totalNodes =0;
			
			
			
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.charAt(0)=='#'){
					if(!firstApp){
						app.setRoot((DefaultMutableTreeNode)root.getLastChild());
						app.setTotalNodes(totalNodes);
						app.setNumDiffFileCodes(fileStatistics.size());
						LinkedHashMap fileStatistics2 = (LinkedHashMap<Integer, Integer>) this.sortByValue(fileStatistics);
						app.setFilesStatistics(fileStatistics2);
						
						Collections.sort(filesInfo);
						app.setSizeRanking(filesInfo.toArray(new NodeInfo[filesInfo.size()]));
						
						app.setFolderPresence(this.folderPresence);
						
						app.computeTotalSize();
						app.computeFileFolderDistribution();
						applications.add(app);
						
						
					}
					app = new Application(sCurrentLine.substring(1));
					fileStatistics = new HashMap<Integer, Integer>();
					filesInfo = new ArrayList<NodeInfo>();
					folderPresence = new int[6];
					firstApp=false;
					totalNodes =0;
					continue;
				}
				StringTokenizer stk = new StringTokenizer(sCurrentLine,",");
				path=stk.nextToken();
				s=stk.nextToken().trim();
				size = Long.parseLong(s.length()==0?"0":s);
				s=stk.nextToken().trim();
				mtime = Long.parseLong(s.length()==0?"0":s);
				s=stk.nextToken().trim();
				ctime = Long.parseLong(s.length()==0?"0":s);
				//s=stk.nextToken().trim();
				//uid = Long.parseLong(s.length()==0?"0":s);
				//s=stk.nextToken().trim();
				//gid = Long.parseLong(s.length()==0?"0":s);
				if(path.charAt(path.length()-1)!='\\'){
					features=this.buildNodeFromString(path.replace('\\', '/'), size/1000, mtime, ctime); //file size is on MB
					app.setMaxNumberLevels(features[1]);
					totalNodes+=features[0];
				}
			}
			if(app!=null){
				app.setRoot((DefaultMutableTreeNode)root.getLastChild());
				app.setTotalNodes(totalNodes);
				app.setNumDiffFileCodes(fileStatistics.size());
				LinkedHashMap fileStatistics2 = (LinkedHashMap<Integer, Integer>) this.sortByValue(fileStatistics);
				app.setFilesStatistics(fileStatistics2);
				
				Collections.sort(filesInfo);
				app.setSizeRanking(filesInfo.toArray(new NodeInfo[filesInfo.size()]));
				
				app.setFolderPresence(this.folderPresence);
				
				app.computeTotalSize();
				app.computeFileFolderDistribution();
				applications.add(app);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private void printApplicationStatistics() {
		for(Application a: applications){
			System.out.println(a.toString());
		}
		
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V>  sortByValue( Map<K, V> map ){
		LinkedList<Map.Entry<K, V>> list =   new LinkedList<>( map.entrySet() );
	    Collections.sort( list, new Comparator<Map.Entry<K, V>>()
	    {
	        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
	        {
	            return -(o1.getValue()).compareTo( o2.getValue() );
	        }
	    } );
	
	    Map<K, V> result = new LinkedHashMap<>();
	    for (Map.Entry<K, V> entry : list)
	    {
	        result.put( entry.getKey(), entry.getValue() );
	    }
	    return result;
	}

	// Main entry point for this example
	public static void main( String args[] )
	{
		// Create an instance of the test application
		Directory mainFrame	= new Directory();
		mainFrame.createApplicationsFromFile("datasets/nsrinfo_vm44-w2k8-lij5.lss.emc.com_1434389033.txt");
		mainFrame.setVisible( true );
		
		mainFrame.printApplicationStatistics();
	}

	
}