package util;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveDouble;

public class OctaveFunctions {
	public static final String TRAINING_SET_FILENAME = "octave-files/dataTrainingSet.mat";
	public static final String FINAL_TRAINING_SET_FILENAME = "octave-files/finalDataTrainingSet.mat";
	
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
		
		/*
		
		%  Before running PCA, it is important to first normalize X
		[X_norm, mu, sigma] = featureNormalize(X);
		
		function [X_norm, mu, sigma] = featureNormalize(X)
				%FEATURENORMALIZE Normalizes the features in X 
				%   FEATURENORMALIZE(X) returns a normalized version of X where
				%   the mean value of each feature is 0 and the standard deviation
				%   is 1. This is often a good preprocessing step to do when
				%   working with learning algorithms.

				mu = mean(X);
				X_norm = bsxfun(@minus, X, mu);

				sigma = std(X_norm);
				X_norm = bsxfun(@rdivide, X_norm, sigma);


				% ============================================================

				end
				
		
				%  Run PCA
				[U, S] = pca(X_norm);
				
				
				function [U, S] = pca(X)
						%PCA Run principal component analysis on the dataset X
						%   [U, S, X] = pca(X) computes eigenvectors of the covariance matrix of X
						%   Returns the eigenvectors U, the eigenvalues (on diagonal) in S
						%

						% Useful values
						[m, n] = size(X);

						% You need to return the following variables correctly.
						U = zeros(n);
						S = zeros(n);

						% ====================== YOUR CODE HERE ======================
						% Instructions: You should first compute the covariance matrix. Then, you
						%               should use the "svd" function to compute the eigenvectors
						%               and eigenvalues of the covariance matrix.
						%
						% Note: When computing the covariance matrix, remember to divide by m (the
						%       number of examples).
						%

						sigma = (X'*X)./m;

						[U, S, V] = svd(sigma);





						% =========================================================================

						end
						
						
						
						%  Project the data onto K = 1 dimension
						K = 1;
						Z = projectData(X_norm, U, K);
						
						
						function Z = projectData(X, U, K)
								%PROJECTDATA Computes the reduced data representation when projecting only
								%on to the top k eigenvectors
								%   Z = projectData(X, U, K) computes the projection of
								%   the normalized inputs X into the reduced dimensional space spanned by
								%   the first K columns of U. It returns the projected examples in Z.
								%

								% You need to return the following variables correctly.
								Z = zeros(size(X, 1), K);

								% ====================== YOUR CODE HERE ======================
								% Instructions: Compute the projection of the data using only the top K
								%               eigenvectors in U (first K columns).
								%               For the i-th example X(i,:), the projection on to the k-th
								%               eigenvector is given as follows:
								%                    x = X(i, :)';
								%                    projection_k = x' * U(:, k);
								%

								Z = X * U(:, 1:K);


								% =============================================================

								end
								*/
	}

	

}
