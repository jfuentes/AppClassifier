
/*
 * This class represents the information about each node of the tree
 */


package util;

import javax.swing.tree.DefaultMutableTreeNode;

public class NodeInfo extends DefaultMutableTreeNode implements Comparable{
	
	private static final long serialVersionUID = 1L;
	public String name;
	public long size; // file size is on Kb
	long mtime;
	long ctime;
	long uid;
	long gid;
	boolean isFile;
	public String extension;
	public int codeFile;
	
	
	
	public NodeInfo(String name, long size, long mtime, long ctime, long uid, long gid){
		this.name=name;
		this.size=size;
		this.mtime=mtime;
		this.ctime=ctime;
		this.uid=uid;
		this.gid=gid;
	}
	
	public NodeInfo(String name, long size, long mtime, long ctime, boolean isFile){
		this.name=name;
		this.size=size;
		this.mtime=mtime;
		this.ctime=ctime;
		this.isFile=isFile;
	}
	
	public NodeInfo(String name, long size, boolean isFile){
		this.name=name;
		this.size=size;
		this.isFile=isFile;
	}
	
	public NodeInfo(String name){
		this.name=name;
	}
	
	public NodeInfo(String name, long size, long mtime, long ctime, boolean isFile, String extension, int codeFile) {
		// TODO Auto-generated constructor stub
		this.name=name;
		this.size=size;
		this.mtime=mtime;
		this.ctime=ctime;
		this.isFile=isFile;
		this.extension=extension;
		this.codeFile=codeFile;
	}
	
	public NodeInfo(String name, long size, boolean isFile, String extension, int codeFile) {
		// TODO Auto-generated constructor stub
		this.name=name;
		this.size=size;
		this.isFile=isFile;
		this.extension=extension;
		this.codeFile=codeFile;
	}

	public String toString(){
		return isFile?name+"  "+size:name;
	}
	

	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return (int) (((NodeInfo)arg0).size-this.size);
	}

}
