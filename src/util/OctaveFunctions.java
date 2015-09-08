package util;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveDouble;

public class OctaveFunctions {
	public static final String TRAINING_SET_FILENAME = "octave-files/dataTrainingSet.mat";
	public static final String FINAL_TRAINING_SET_FILENAME = "octave-files/finalDataTrainingSet.mat";
	
	public static final String WEIGTHS_HIDDEN_LAYER = "octave-files/weightsHiddenLayer.mat";
	
	public static final String REDUCTED_FEATURES = "octave-files/reductedFeatures.mat";
	
	
	public static void saveTrainingSet(double[] X, double [] Y, int trainingExamples, int numFeatures){
		
		OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
		
		OctaveDouble oX = new OctaveDouble(X, trainingExamples, numFeatures);
		octave.put("X", oX);
		 
		OctaveDouble oY = new OctaveDouble(Y, trainingExamples, 1);
		octave.put("Y", oY);

		octave.eval("save -binary "+TRAINING_SET_FILENAME+" X Y;");
		octave.close();
		
	}
	
	public static void loadTrainingSet(){
		
		OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
		
		octave.eval("load "+TRAINING_SET_FILENAME+";");
		OctaveDouble result = octave.get(OctaveDouble.class, "Y");
		double [] integral = result.getData();
		for(int i=0; i<integral.length; i++)
		 System.out.print(integral[i]);
		octave.close();
	}
	
	public static void saveReducedTrainingSet(){
		
	}
	
	public static double [][] readReductedTrainingSet(){
		
		OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
		
		octave.eval("load "+REDUCTED_FEATURES+";");
		OctaveDouble result = octave.get(OctaveDouble.class, "X_red");
		double [] X = result.getData();
		//OctaveInt result2 = octave.get(OctaveInt.class, "K");
		//int numFeatures = result2.get(1);
		int numFeatures = 12;
		
		double [][] finalMatrix = new double[X.length/numFeatures][numFeatures];
		int k=0;
		for(int i=0; i<finalMatrix.length; i++){
			for(int j=0; j<numFeatures; j++)
				finalMatrix[i][j]=X[k++];
		}
		octave.close();
		
		return finalMatrix;
	}
	
	public static void performDimensionalityReductionPCA(int K){
		//Matrix X and K need to be save in mat file
		
		int [] KK ={K};
		
		OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
		octave.eval("load "+TRAINING_SET_FILENAME+";");
		
		//OctaveInt oK = new OctaveInt(KK, 1, 1);
		//octave.put("K", oK);
		octave.eval("K=12;");
		
		//featureNormalize
		octave.eval("mu = mean(X);");
		octave.eval("X_norm = bsxfun(@minus, X, mu);");
		octave.eval("sigma = std(X_norm);");
		octave.eval("X_norm = bsxfun(@rdivide, X_norm, sigma);");
		
		//octave.eval("X_norm = X;");
		
		//PCA
		octave.eval("[m, n] = size(X_norm);");
		octave.eval("U = zeros(n);");
		octave.eval("S = zeros(n);");
		octave.eval("sigma = (X_norm'*X_norm)./m;");
		octave.eval("[U, S, V] = svd(sigma);");
		
		//project Data
		octave.eval("Z = zeros(size(X_norm, 1), K);");
		octave.eval("Z = X_norm * U(:, 1:K);");
		
		octave.eval("X_red = reshape(Z',1,[]);");		
		
		//save the result into a file
		octave.eval("save -binary "+REDUCTED_FEATURES+" X_red K;");
		octave.close();
		
	}
	
	/*
	 * In order to use this method, the hidden layer weights need to be computed previously (learning process)
	 */
	public static double [] classifyWithNeuralNet(double[] X){
		
		OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
		
		OctaveDouble oX = new OctaveDouble(X, X.length, 1);
		octave.put("X", oX);
		
		octave.eval("load "+WEIGTHS_HIDDEN_LAYER+";");
		
		
		octave.eval("num_labels = size(Theta2, 1);");

		octave.eval("p = zeros(size(X, 1), 1);");

		octave.eval("a1 = [ones(size(X, 1), 1) X];");

		octave.eval("z2 = Theta1*a1';");
		octave.eval("a2 = 1.0 ./ (1.0 + exp(-z2));"); //sigmoid function

		octave.eval("a2 = [ones(1, size(a2, 2)); a2];");
		
		octave.eval("z3 = Theta2*a2;");
		octave.eval("a3 = 1.0 ./ (1.0 + exp(-z3));");

		octave.eval("[values, p] = max(a3', [], 2);");
		
		OctaveDouble result = octave.get(OctaveDouble.class, "p");
		double [] classResult = result.getData();
		
		octave.close();
		
		return classResult;
	}

	

}
