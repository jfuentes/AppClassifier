/*
 * Small program to parse origin files from backup metadata
 */

package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Parser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.print("Enter the file name to parse: ");
		Scanner sc = new Scanner(System.in);
		String fileName = sc.next();
		try (BufferedReader br = new BufferedReader(new FileReader("origin_files/"+fileName)))
		{
			
			PrintWriter writer = new PrintWriter("origin_files/parsed_"+fileName, "UTF-8");
			
	
			
			String sCurrentLine;
			String path;
			long size;
			
			
			
			while ((sCurrentLine = br.readLine()) != null) {
				path="";
				StringTokenizer stk = new StringTokenizer(sCurrentLine," ");
				
				stk.nextToken(); // permission
				stk.nextToken(); //user
				size = Long.parseLong(stk.nextToken()); //size
				stk.nextToken();//percentage 
				stk.nextToken(); //date
				stk.nextToken(); //time
				while(stk.hasMoreTokens()){
					path+=stk.nextToken();
				}
				
				writer.println(path+","+size);
				
			}
			
			
			br.close();
			writer.close();
			System.out.println("File generated: "+"origin_files/parsed_"+fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} 

	}

}
