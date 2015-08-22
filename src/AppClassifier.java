
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.Octave;
import dk.ange.octave.type.OctaveDouble;
import dk.ange.octave.type.OctaveString;
import util.*;

public class AppClassifier {
	
	public static final String CLASSES_DIRECTORY = "metadata-classes";
	public static final String TEST_CASES_DIRECTORY = "test-cases";
	public static final String STATISTICS_DIRECTORY = "statistics";
	public static final int TRAINING_SET_SIZE = 65;
	
	public static int NUMBER_OF_CLASSES = 0;
	
	
	public static HashMap<String, Integer> fileCodes = new HashMap<String, Integer>();
	private static HashMap<Integer, String> codeFiles = new HashMap<Integer, String>();
	
	public static double [][] maxMinFeatureValues;
	
	public static double [] meanFeatures;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ArrayList<AppClass> classes = new ArrayList<AppClass>();
		
		double [] Y = new double[TRAINING_SET_SIZE];
		double [] X = new double[TRAINING_SET_SIZE*Application.NUM_FEATURES];
		double [] Xnorm = null; //normalized dataset
		
		int option;
		Scanner sc = new Scanner(System.in);
		
		MultiLayerPerceptron neuralNet = null;
		
		NumberFormat formatter = new DecimalFormat("#0.000"); 
		
		do{
			System.out.println();
			System.out.println("1.- Construct training set from metadata files");
			System.out.println("2.- Normalize and Export features to CSV file");
			System.out.println("3.- Apply PCA for dimensionality reduction");
			System.out.println("4.- Train Neural Network");
			System.out.println("5.- Classify Application with NN");
			System.out.println("6.- Generate statistics files");
			System.out.println("7.- Exit");
			System.out.print("\nOption: ");
			option = sc.nextInt();
			System.out.println();
			switch(option){
			case 1:
				System.out.println("The program will extract all the features from metadata-classes folder");
				System.out.println("Each metadata file represents a class with different version of one application");
				System.out.println();
				String[] fileNames = getMetadataFiles();
				//Arrays.sort(fileNames);
				
				
				NUMBER_OF_CLASSES=fileNames.length;

				int posY=0;
				
				int posX=0;
				
				for(int i=0; i<fileNames.length; i++){
					AppClass appClass = new AppClass(fileCodes, codeFiles, CLASSES_DIRECTORY+"/"+fileNames[i], fileNames[i]);
					appClass.createApplicationsFromFile();
					System.out.println("Features from "+fileNames[i]+" (class "+(i+1) +") extracted:");
					//appClass.printApplicationStatistics();
					//appClass.setVisible(true);
					double [][] trainingSet = appClass.getFeaturesPerApplication();
					
					
					
					for(int k=0; k<trainingSet.length; k++){
						for (int j = 0; j < trainingSet[0].length; j++) {
							System.out.print(formatter.format(trainingSet[k][j])+" ");
							X[posX++]=trainingSet[k][j];
						}
						Y[posY]=(i+1);
						System.out.print(" >>"+Y[posY]);
						posY++;
						System.out.println();
						
					}
					
					classes.add(appClass);
					
					System.out.println();
					
					//OctaveFunctions.saveTrainingSet(X, Y, TRAINING_SET_SIZE, Application.NUM_FEATURES);
				}
				
				
				
				break;
				
			case 2:
				try {
					Xnorm = normalizeDataSet(X);
					//Xnorm = featureStandardization(X);
					PrintWriter writer = new PrintWriter("features/features"+AppClassifier.TRAINING_SET_SIZE+"x"+Application.NUM_FEATURES+".csv", "UTF-8");
					PrintWriter writer2 = new PrintWriter("features/normalized_features"+AppClassifier.TRAINING_SET_SIZE+"x"+Application.NUM_FEATURES+".csv", "UTF-8");
					int cont=0,i=0;
					while(i<X.length){
						String s="", s2="";
						for(int j=0; j<Application.NUM_FEATURES; j++){
							s+=X[i]+",";
							s2+=Xnorm[i]+",";
							i++;
						}
						
						//int [] arrayY = new int[Y.length];
						//arrayY[(int) Y[cont]-1]=1;
						
						for(int j=0; j<NUMBER_OF_CLASSES; j++){
							s+=j==((int)Y[cont]-1)?"1.00"+",":"0.00"+",";
							s2+=j==((int)Y[cont]-1)?"1.00"+",":"0.00"+",";
						}
						
						cont++;
						
						writer.println(s);
						writer2.println(s2);
					}
					writer.close();
					writer2.close();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//for (int i = 0; i < maxMinFeatureValues[0].length; i++) {
				//	System.out.print("["+maxMinFeatureValues[0][i]+", "+maxMinFeatureValues[1][i]+"] ");
				//}
				//System.out.println();
				break;
				
			case 3:
				OctaveFunctions.saveTrainingSet(X, Y, TRAINING_SET_SIZE, Application.NUM_FEATURES);
				System.out.println("The current number of features is "+Application.NUM_FEATURES);
				System.out.print("Enter the new number of features: ");
				int K = sc.nextInt();
				
				OctaveFunctions.performDimensionalityReductionPCA(K);
				
				System.out.println();
				double [][] reducedFeatures = OctaveFunctions.readReductedTrainingSet();
				for (int i = 0; i < reducedFeatures.length; i++) {
					for (int j = 0; j < reducedFeatures[0].length; j++) {
						System.out.print(reducedFeatures[i][j]+ " ");
					}
					System.out.println();
				}
				break;
				
			case 4:
				// get the path to file with data
		        String inputFileName = "features/normalized_features"+AppClassifier.TRAINING_SET_SIZE+"x"+Application.NUM_FEATURES+".csv";
		        
		        // create MultiLayerPerceptron neural network
		        neuralNet = new MultiLayerPerceptron(TransferFunctionType.SIGMOID,36, 24, 12);
		        
		        // set learning parameters
		        MomentumBackpropagation learningRule = (MomentumBackpropagation) neuralNet.getLearningRule();
		        //learningRule.setLearningRate(0.5);
		        //learningRule.setMomentum(0.8);
		        //learningRule.setMaxIterations(200);
		        
		        // create training set from file
		        DataSet dataSet = DataSet.createFromFile(inputFileName, 36, 12, ",", false);
		       
		        // train the network with training set
		        System.out.println("Training neural network...");
		        neuralNet.learn(dataSet);     
		        System.out.println("Training Done.");
		        
		        
		        
		        /* 
		        System.out.println("Testing network...");
		        for(DataSetRow testSetRow : dataSet.getRows()) {
		            neuralNet.setInput(testSetRow.getInput());
		            neuralNet.calculate();
		            double[] networkOutput = neuralNet.getOutput();
		            int index=0;
		            double max=0;;
		            for(int i=0; i<networkOutput.length;i++){
		            	if(networkOutput[i]>max){
		            		max=networkOutput[i];
		            		index=i;
		            	}
		            }
		            System.out.print("(Class "+(index+1)+")  ");
		            System.out.print("Input: " + Arrays.toString( testSetRow.getInput() ) );
		            System.out.println(" Output: " + Arrays.toString( networkOutput) );
		        }
		        */
				break;
				
			case 5:
				int op=0;
				double [] testVector = new double[Application.NUM_FEATURES];
				double [] classVector = new double[NUMBER_OF_CLASSES];
				classVector[1]=1;
				do{
					System.out.println("    1.- Extract features from a file");
					System.out.println("    2.- Enter the features manually");
					op = sc.nextInt();
					switch(op){
					case 1:
						System.out.print("Enter the file name: ");
						String metaFile= sc.next();
						
						AppClass appClass = new AppClass(fileCodes, codeFiles, TEST_CASES_DIRECTORY+"/"+metaFile, metaFile);
						appClass.createApplicationsFromFile();
						System.out.println("Extracting features from "+metaFile+" ...\n");
						//appClass.printApplicationStatistics();
						//appClass.setVisible(true);
						testVector = appClass.getFeaturesFirstApplication();
						
						System.out.print("Features: [");
						for (int j = 0; j < testVector.length; j++) {
							System.out.print(formatter.format(testVector[j])+" ");
						}

						System.out.println("]");
											
						break;
					case 2:
						System.out.print("Enter the set of features: ");
						String featuresTestCase = sc.next();
						//System.out.print("\nEnter the class number: ");
						int classNum = 1;
						
						String [] featuresStrings = featuresTestCase.split(",");
						for(int i=0; i< featuresStrings.length; i++){
							testVector[i]= Double.parseDouble(featuresStrings[i]);
						}
						break;
					default: System.out.println("Invalid option");
					}
				}while(op!=1 & op!=2);
				
				
				
				
				//testVector = normalizeTestExample(testVector);
				//testVector = featureStandardizationExample(testVector);
				testVector = normalizeExample(testVector);
				
				DataSet testSet = new DataSet(Application.NUM_FEATURES, NUMBER_OF_CLASSES);
				testSet.addRow(new DataSetRow(testVector, classVector));
		        for(DataSetRow testSetRow : testSet.getRows()) {
		            neuralNet.setInput(testSetRow.getInput());
		            neuralNet.calculate();
		            double[] networkOutput = neuralNet.getOutput();
		            int index=-1;
		            double max=0.7;
		            for(int i=0; i<networkOutput.length;i++){
		            	if(networkOutput[i]>max){
		            		max=networkOutput[i];
		            		index=i;
		            	}
		            }
		            System.out.println();
		            System.out.println(index==-1?"Answer: No class ":"Answer: Class "+(index+1));
		            double [] input=testSetRow.getInput();
		            System.out.print("  Input: [" );
		            for (int i = 0; i < input.length; i++) { //show the input features
						System.out.print(formatter.format(input[i])+" ");
					}
		            System.out.println("]");
		            
		            double [] output = networkOutput;
		            System.out.print("  Output: [" );
		            for (int i = 0; i < output.length; i++) { //show the input features
						System.out.print(formatter.format(output[i])+"  ");
					}
		            System.out.println("]");
		        }
				break;
				
			case 6: 
				for(AppClass appClass : classes){
					//statistics for most common files
					try {
						PrintWriter writer = new PrintWriter(STATISTICS_DIRECTORY+"/rankingCommonFiles_"+appClass.getClassName()+".csv", "UTF-8");
						String [] ranking = appClass.getRankingCommonFiles();
						for (int i = 0; i < ranking.length; i++) {
							writer.println(ranking[i]);
						}
						writer.close();
						
						writer = new PrintWriter(STATISTICS_DIRECTORY+"/rankingBySize_"+appClass.getClassName()+".csv", "UTF-8");
						ranking = appClass.getRankingBySize();
						for (int i = 0; i < ranking.length; i++) {
							writer.println(ranking[i]);
						}
						writer.close();
						
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 7:
				break;
				
			default: System.out.println("Invalid option");
			}
		}while(option!=7);
		sc.close();
	}
	
	private static double[] normalizeExample(double[] example){
		for (int i = 0; i < Application.NUM_FEATURES; i++) {
			if(example[i]<maxMinFeatureValues[0][i])
				example[i]=0;
			else if(example[i]>maxMinFeatureValues[1][i])
				example[i]=1;
			else
				example[i] =(maxMinFeatureValues[1][i]-maxMinFeatureValues[0][i]==0)?0: (example[i]-maxMinFeatureValues[0][i])/(maxMinFeatureValues[1][i]-maxMinFeatureValues[0][i]);
		}
		return example;
	}
	
	private static double[] normalizeDataSet(double[] trainingSet) {
		// TODO Auto-generated method stub
		/*
		 * Function for normalization:
		 *   Xn = (X - Xmin)/(Xmax - Xmin)
		 *   
		 *   where:
		 *     X = value that should be normalized
		 *     Xn = normalized value
		 *     Xmin = minimum value of X
		 *     Xmax = maximum value of X
		 */
		
		maxMinFeatureValues = new double[2][Application.NUM_FEATURES];
		
		double [] normalizedDataSet = new double[TRAINING_SET_SIZE*Application.NUM_FEATURES];
		
		for(int col=0; col< Application.NUM_FEATURES; col++){
			double max=0, min=10000000;
			for(int i=col; i<trainingSet.length; i+=Application.NUM_FEATURES){
				if(trainingSet[i]>max)
					max=trainingSet[i];
				if(trainingSet[i]<min)
					min=trainingSet[i];
			}
			//System.out.println(col+": max "+max+"  min "+min);
			maxMinFeatureValues[0][col]=min;
			maxMinFeatureValues[1][col]=max;
			for(int i=col; i<trainingSet.length; i+=Application.NUM_FEATURES){
				normalizedDataSet[i] = (max-min)==0?0:((trainingSet[i]-min)/(max-min));
			}
		}
		
		return normalizedDataSet;
	}
	
	private static double[] featureStandardization(double[] trainingSet) {
		// TODO Auto-generated method stub
		/*
		 * Function for normalization:
		 *   Xn = (X - Xmin)/(Xmax - Xmin)
		 *   
		 *   where:
		 *     X = value that should be normalized
		 *     Xn = normalized value
		 *     Xmin = minimum value of X
		 *     Xmax = maximum value of X
		 */
		
		
		double [] normalizedDataSet = new double[TRAINING_SET_SIZE*Application.NUM_FEATURES];
		
		meanFeatures = new double[Application.NUM_FEATURES];
		
		//compute the mean
		for(int col=0; col< Application.NUM_FEATURES; col++){
			
			for(int i=col; i<trainingSet.length; i+=Application.NUM_FEATURES){
				meanFeatures[col]+=trainingSet[i];
			}
			
			meanFeatures[col]/=TRAINING_SET_SIZE;
			
		}
		
		//compute the standardization with standard deviation
		for(int col=0; col< Application.NUM_FEATURES; col++){
			
			for(int i=col; i<trainingSet.length; i+=Application.NUM_FEATURES){
				normalizedDataSet[i]= (trainingSet[i] - meanFeatures[col])/(Math.pow((trainingSet[i] - meanFeatures[col]), 2));
			}
			
			
			
		}
		
		return normalizedDataSet;
	}
	
	private static double[] featureStandardizationExample(double[] test) {
		// TODO Auto-generated method stub
		/*
		 * Function for normalization:
		 *   Xn = (X - Xmin)/(Xmax - Xmin)
		 *   
		 *   where:
		 *     X = value that should be normalized
		 *     Xn = normalized value
		 *     Xmin = minimum value of X
		 *     Xmax = maximum value of X
		 */
		
		//double [] normalizedDataSet = new double[TRAINING_SET_SIZE*Application.NUM_FEATURES];
		
     			
		//compute the standardization with standard deviation
		for(int col=0; col< Application.NUM_FEATURES; col++){
			
			
				test[col]= (test[col] - meanFeatures[col])/(Math.pow((test[col] - meanFeatures[col]), 2));
			
			
			
			
		}
		
		return test;
	}
	
	private static double[] normalizeTestExample(double[] test){
		double [] normalized = new double[test.length];
		for(int i=0; i<test.length; i++){
			normalized[i] = (maxMinFeatureValues[1][i]-maxMinFeatureValues[0][i])==0?0:(test[i]-maxMinFeatureValues[0][i])/(maxMinFeatureValues[1][i]-maxMinFeatureValues[0][i]);
			if(normalized[i]<0)
				normalized[i]=1;
		}
		return normalized;
	}

	private static String[] getMetadataFiles(){
		File folder = new File("metadata-classes");
		File[] listOfFiles = folder.listFiles();
		String [] fileNames = new String[listOfFiles.length];
		for (int i = 0; i < listOfFiles.length; i++) {
		   if (listOfFiles[i].isFile()) {
			   String fName = listOfFiles[i].getName();
			   String [] pos = fName.split("\\."); 
		       fileNames[Integer.parseInt(pos[0])-1]=fName;
		   } 
		}
		
		return fileNames;
	}

}
