/**
 * Created by Fernandes on 07.11.2017.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.File;
import java.io.FileInputStream;
import java.lang.StringBuilder;
import java.io.FileReader;

import matlabcontrol.*;

public class SpectralReflectanceReconstruction {

	private static BufferedReader in;
	private static BufferedReader measuredIn;
	private static StringBuilder transpose;
	private static String transposeString;
	private static StringBuilder save;
	private static String saveString;
	private static StringBuilder reflectance;
	private static String reflectanceString;
	private static StringBuilder curSpec;
	private static String curSpecString;
	private static StringBuilder cntIllus;
	private static String cntIllusString;
	private static StringBuilder spectralWeights;
	private static String spectralWeightsString;
	private static StringBuilder spectralWeights2;
	private static String spectralWeights2String;
	private static StringBuilder rms_wiener;
	private static String rms_wienerString;
	private static StringBuilder rms_eigen;
	private static String rms_eigenString;
	private static StringBuilder rms_paulus;
	private static String rms_paulusString;
	private static StringBuilder D65_triplets_rec;
	private static String D65_triplets_recString;
	private static StringBuilder DE2000;
	private static String DE2000String;
	private static String line;
	private static String measureLine;
	private static String extension = ".txt";
	private static StringBuilder fileNameExt;
	private static StringBuilder finalString;
	private static String toMatlab;
	private static String fileName;
	private static String fileNameMatlab;
	private static String fileExt;
   	private static Scanner input = new Scanner(System.in);
	private static int selection = 0;
	private static int numColors = 0;
	private static int flagTriplets = 0;
	private static int alles = 0;
	
	
    public static void main(String[] args) throws IOException, MatlabConnectionException, MatlabInvocationException {

		
		
		if(args.length == 0){
			System.out.println("No file chosen. Example of usage: java -cp \".;Y:\\matlabcontrol.jar\" SpectralReflectanceReconstruction 20colors");
			System.exit(0);
		}
		fileName = args[0];
		//fileName contains "20colors"
		  
		fileNameExt = new StringBuilder();
		fileNameExt.append(fileName);
		fileNameExt.append(extension);
		fileExt = fileNameExt.toString();
		//fileExt contains 20colors.txt
		File f = new File(fileExt);
		
		if(!f.exists()){
			System.out.println("Couldn't find " + fileExt);
			System.exit(0);
		}
		
		MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder().setMatlabLocation("C:/Program Files/MATLAB/R2017b/bin/matlab.exe").build();
			
		MatlabProxyFactory factory = new MatlabProxyFactory(options);
		MatlabProxy proxy = factory.getProxy();

		proxy.eval("cd('Y:\\spectralReflectanceDB')");
		proxy.eval("load('munsell380_780_1_glossy.mat')");
		proxy.eval("load('avA2300.mat')");
		proxy.eval("addpath(genpath('optprop'))");
		
		Object[] resReflectance = null;
		Object[] resIlluminant = null;
		Object[] resSensitivity = null;
		
		
		
		//format what's gonna pass to Matlab
		finalString = new StringBuilder();
		if(Character.isDigit(fileName.charAt(0)))
			finalString.append("file");
		finalString.append(fileName);
		fileNameMatlab = finalString.toString();
		  
		finalString.append(" = [");
		  
		//read through the 20colors.txt
		try (BufferedReader in = new BufferedReader(new FileReader(fileExt))) {
			while (!((line = in.readLine()).equals("END"))) {
			   //line has line color name ex:#123456
			   
				numColors++;
				fileNameExt = new StringBuilder();
				fileNameExt.append(line);
				fileNameExt.append(extension);
				fileExt = fileNameExt.toString();
				//fileExt now contains #123456.txt
				if(fileExt.equals("END"))
					break;
				try (BufferedReader measuredIn = new BufferedReader(new FileReader(fileName + "/" + fileExt))) {
			   
				System.out.println(line);
			   
					while ((measureLine = measuredIn.readLine()) != null) {
						finalString.append(measureLine);
						finalString.append(" ");  
					}//closes while to read #color.txt
					finalString.append(";");
					
				}//closes try for #color.txt
			}//closes while to read 20colors.txt
		}//closes try for 20colors.txt
		//removes the extra ";"
		finalString.setLength(finalString.length() - 1);
		finalString.append("]");
		toMatlab = finalString.toString();
		//final format should be "20colors = [1 2 3 4 5 ....; 1 2 3 4 5 6 7...........]"
					
		do{
			
				System.out.println("-- Actions --");
				if(flagTriplets == 0){
					System.out.println(
						"Select an option: \n" +
						"  1) Add a new color measurement\n" +
						"  2) Calculate with a spectral reflectance\n" +
						"  3) Exit\n"
					);
				}else{
					System.out.println(
						"Select an option: \n" +
						"  1) Add a new color measurement\n" +
						"  2) Calculate with a spectral reflectance\n" +
						"  3) Reconstruct with last triplet\n" +
						"  4) Exit\n"
					);
				}
				
				selection = input.nextInt();	
				//input.nextLine();
			
			switch (selection) {
				case 1:
			
				System.out.println(toMatlab);
				
				proxy.eval(toMatlab);
				
				transpose = new StringBuilder();
				transpose.append(fileNameMatlab);
				transpose.append(" = ");
				transpose.append(fileNameMatlab);
				transpose.append("'");							  
				transposeString = transpose.toString();
				proxy.eval(transposeString);
				
				save = new StringBuilder();
				save.append("save('munsell380_780_1_glossy.mat','");
				save.append(fileNameMatlab);
				save.append("','-append')");							  
				saveString = save.toString();
				proxy.eval(saveString);
				
				
				  break;
				case 2:
					System.out.println("-- Actions --");
					System.out.println(
				        "Which would you like to choose?(1-1600 or 0 for all)\n"
					);
					selection = input.nextInt();
				  
					double innerValue = 0;
					double innerValue2 = 0;
					
					reflectance = new StringBuilder();
					curSpec = new StringBuilder();
					cntIllus = new StringBuilder();
					spectralWeights = new StringBuilder();
					spectralWeights2 = new StringBuilder();
					D65_triplets_rec = new StringBuilder();
					DE2000 = new StringBuilder();
					rms_wiener = new StringBuilder();
					rms_paulus = new StringBuilder();
					rms_eigen = new StringBuilder();
					
					cntIllus.append("cntIllus = size(");
					cntIllus.append(fileNameMatlab);
					cntIllus.append(", 2)");
					cntIllusString = cntIllus.toString();
					proxy.eval(cntIllusString);
					
					if(selection >= 1 && selection <= 1600){
						
						reflectance.setLength(0);
						reflectance.append("X(:,");
						reflectance.append(selection);
						reflectance.append(")");
						reflectanceString = reflectance.toString();
						
						curSpec.setLength(0);
						curSpec.append("curSpec = ");
						curSpec.append(reflectanceString);
						curSpecString = curSpec.toString();
						//curSpecString contains the curve spec
						proxy.eval(curSpecString);
						
						spectralWeights.append("spectralWeights = repelem(");
						spectralWeights.append(fileNameMatlab);
						spectralWeights.append("', 3, 1) .* repmat(sens', cntIllus, 1);");
						spectralWeightsString = spectralWeights.toString();
						proxy.eval(spectralWeightsString);
						
						proxy.eval("signals = spectralWeights * curSpec;");
						
						selection = 0;
						flagTriplets = 1;
						alles = 0;
					}else{
						if(selection == 0){
							spectralWeights.setLength(0);
							spectralWeights.append("spectralWeights = repelem(");
							spectralWeights.append(fileNameMatlab);
							spectralWeights.append("', 3, 1) .* repmat(sens', cntIllus, 1);");
							spectralWeightsString = spectralWeights.toString();
							proxy.eval(spectralWeightsString);
								
							spectralWeights2.setLength(0);
							spectralWeights2.append("spectralWeights2 = repelem(D65', 3, 1) .* repmat(cie_full', 1, 1);");
							spectralWeights2String = spectralWeights2.toString();
							proxy.eval(spectralWeights2String);
									
							flagTriplets = 1;
							alles = 1;
						}
					}
					break;
				case 3:	
				  if(flagTriplets == 0){
					proxy.disconnect();
					System.out.println("Finished");
					break;
				  }else{
						System.out.println("-- Actions --");
						System.out.println(
							"Select an option: \n" +
							"  1) Wiener reconstruction\n" +
							"  2) Eigen reconstruction\n" +
							"  3) Paulus reconstruction\n" +
							"  4) All of the above\n" +
							"  5) All with noise 0.1\n" +
							"  6) All with noise 0.01\n" +
							"  7) All with noise 0.001\n" +
							"  8) Back\n" 
						);
						
						Object[] mean = null;
						Object[] std = null;
						Object[] max = null;
						Object meanFirst = null;
						Object stdFirst = null;
						Object maxFirst = null;
						
						
						selection = input.nextInt();
						
						switch(selection){
							case 1:
								
								if(alles == 0){
									proxy.eval("lambdas = 380:780;");
									proxy.eval("rec_wiener = wiener_estimation(signals, spectralWeights, 0.1, 1);");
									proxy.eval("figure()");
									proxy.eval("hold on;");
									proxy.eval("plot(lambdas, curSpec);");
									proxy.eval("plot(lambdas, rec_wiener);");
									proxy.eval("xlim([lambdas(1), lambdas(end)]);");
									proxy.eval("ylim([0, 1]);");
									proxy.eval("legend('Original', 'Wiener Estimation')");
									proxy.eval("xlabel('Wavelength in nm')");
									proxy.eval("ylabel('spectral object reflectance')");
								}else{
									proxy.eval("lambdas = 380:780;");
									int rec_flag = 0;
									int d65_ori_flag = 0;
									int d65_rec_flag = 0;
									
									for(int s = 1; s <= 1600; s++){
										
										reflectance.setLength(0);
										reflectance.append("X(:,");
										reflectance.append(s);
										reflectance.append(")");
										reflectanceString = reflectance.toString();
										
										curSpec.setLength(0);
										curSpec.append("curSpec = ");
										curSpec.append(reflectanceString);
										curSpec.append(";");
										curSpecString = curSpec.toString();
										//curSpecString contains the curve spec
										proxy.eval(curSpecString);
										
										proxy.eval("signals = spectralWeights * curSpec;");
										
										if(d65_ori_flag == 0){
											proxy.eval("d65_triplets_original = spectralWeights2 * curSpec;");
											d65_ori_flag = 1;
										}else{
											proxy.eval("d65_triplets_original = [d65_triplets_original (spectralWeights2 * curSpec)];");
										}
										
										if(rec_flag == 0){
											proxy.eval("rec_wiener = wiener_estimation(signals, spectralWeights, 0.1, 1);");
											
											rec_flag = 1;
										}else{
											proxy.eval("rec_wiener = [rec_wiener wiener_estimation(signals, spectralWeights, 0.1, 1)];");
										}
										
										if(d65_rec_flag == 0){
											D65_triplets_rec.setLength(0);
											D65_triplets_rec.append("d65_triplets_rec_wiener = spectralWeights2 * rec_wiener(:,");
											D65_triplets_rec.append(s);
											D65_triplets_rec.append(");");
											D65_triplets_recString = D65_triplets_rec.toString();
											proxy.eval(D65_triplets_recString);
											
											d65_rec_flag = 1;
										}else{
											D65_triplets_rec.setLength(0);
											D65_triplets_rec.append("d65_triplets_rec_wiener = [d65_triplets_rec_wiener (spectralWeights2 * rec_wiener(:,");
											D65_triplets_rec.append(s);
											D65_triplets_rec.append("))];");
											D65_triplets_recString = D65_triplets_rec.toString();
											proxy.eval(D65_triplets_recString);
										}
										
										System.out.println(s + "/1600 reconstruction completed\r");
								
									}
									int rms_flag = 0;
									int de2000_flag = 0;
									
									
									for(int s = 1; s <= 1600; s++){
										reflectance.setLength(0);
										reflectance.append("X(:,");
										reflectance.append(s);
										reflectance.append(")");
										reflectanceString = reflectance.toString();
										
										curSpec.setLength(0);
										curSpec.append("curSpec = ");
										curSpec.append(reflectanceString);
										curSpec.append(";");
										curSpecString = curSpec.toString();
										//curSpecString contains the curve spec
										proxy.eval(curSpecString);
										
										if(rms_flag == 0){
											//Wiener rms
											rms_wiener.setLength(0);
											rms_wiener.append("rms_wiener = sqrt(sum(((curSpec - rec_wiener(:,");
											rms_wiener.append(s);
											rms_wiener.append(")) .* (curSpec - rec_wiener(:,");
											rms_wiener.append(s);
											rms_wiener.append("))))/401);");
											rms_wienerString = rms_wiener.toString();
											proxy.eval(rms_wienerString);
											
											rms_flag = 1;
										}else{
											//Wiener rms
											rms_wiener.setLength(0);
											rms_wiener.append("rms_wiener = [rms_wiener sqrt(sum(((curSpec - rec_wiener(:,");
											rms_wiener.append(s);
											rms_wiener.append(")) .* (curSpec - rec_wiener(:,");
											rms_wiener.append(s);
											rms_wiener.append("))))/401)];");
											rms_wienerString = rms_wiener.toString();
											proxy.eval(rms_wienerString);
										}
										
										if(de2000_flag == 0){
											DE2000.setLength(0);
											DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											DE2000.setLength(0);
											DE2000.append("XYZ_wiener = xyz2lab([d65_triplets_rec_wiener(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_wiener(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_wiener(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											proxy.eval("de_wiener = de2000(XYZ,XYZ_wiener)");
											
											de2000_flag = 1;
										}else{
											DE2000.setLength(0);
											DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											DE2000.setLength(0);
											DE2000.append("XYZ_wiener = xyz2lab([d65_triplets_rec_wiener(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_wiener(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_wiener(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											proxy.eval("de_wiener = [de_wiener de2000(XYZ,XYZ_wiener)];");
										}
										System.out.println(s + "/1600 rms/DE completed\r");
									}
									
									rms_flag = 0;
								
									mean = proxy.returningEval("mean(rms_wiener)",1);
									std = proxy.returningEval("std(rms_wiener)",1);
									max = proxy.returningEval("max(rms_wiener)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Wiener (rms/DE)-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_wiener)",1);
									std = proxy.returningEval("std(de_wiener)",1);
									max = proxy.returningEval("max(de_wiener)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
								}
							break;
							case 2:
								if(alles == 0){
									proxy.eval("lambdas = 380:780;");
									proxy.eval("q = cntIllus*3");
									proxy.eval("rec_eigen = reconstruction_eigen(signals, spectralWeights, [], q);");
									proxy.eval("figure()");
									proxy.eval("hold on;");
									proxy.eval("plot(lambdas, curSpec);");
									proxy.eval("plot(lambdas, rec_eigen);");
									proxy.eval("xlim([lambdas(1), lambdas(end)]);");
									proxy.eval("ylim([0, 1]);");
									proxy.eval("legend('Original', 'Principal Eigenvectors')");
									proxy.eval("xlabel('Wavelength in nm')");
									proxy.eval("ylabel('spectral object reflectance')");
								}else{
									proxy.eval("lambdas = 380:780;");
									proxy.eval("q = cntIllus*3;");
									int rec_flag = 0;
									int d65_ori_flag = 0;
									int d65_rec_flag = 0;
									
									for(int s = 1; s <= 1600; s++){
										
										reflectance.setLength(0);
										reflectance.append("X(:,");
										reflectance.append(s);
										reflectance.append(")");
										reflectanceString = reflectance.toString();
										
										curSpec.setLength(0);
										curSpec.append("curSpec = ");
										curSpec.append(reflectanceString);
										curSpec.append(";");
										curSpecString = curSpec.toString();
										//curSpecString contains the curve spec
										proxy.eval(curSpecString);
										
										proxy.eval("signals = spectralWeights * curSpec;");
										
										if(d65_ori_flag == 0){
											proxy.eval("d65_triplets_original = spectralWeights2 * curSpec;");
											d65_ori_flag = 1;
										}else{
											proxy.eval("d65_triplets_original = [d65_triplets_original (spectralWeights2 * curSpec)];");
										}
										
										if(rec_flag == 0){
											proxy.eval("rec_eigen = reconstruction_eigen(signals, spectralWeights, [], q);");
										
											rec_flag = 1;
										}else{
											proxy.eval("rec_eigen = [rec_eigen reconstruction_eigen(signals, spectralWeights, [], q)];");
										}
										
										if(d65_rec_flag == 0){
											D65_triplets_rec.setLength(0);
											D65_triplets_rec.append("d65_triplets_rec_eigen = spectralWeights2 * rec_eigen(:,");
											D65_triplets_rec.append(s);
											D65_triplets_rec.append(");");
											D65_triplets_recString = D65_triplets_rec.toString();
											proxy.eval(D65_triplets_recString);
											
											d65_rec_flag = 1;
										}else{
											D65_triplets_rec.setLength(0);
											D65_triplets_rec.append("d65_triplets_rec_eigen = [d65_triplets_rec_eigen (spectralWeights2 * rec_eigen(:,");
											D65_triplets_rec.append(s);
											D65_triplets_rec.append("))];");
											D65_triplets_recString = D65_triplets_rec.toString();
											proxy.eval(D65_triplets_recString);
										}
										System.out.println(s + "/1600 reconstruction completed\r");
									}
									int rms_flag = 0;
									int de2000_flag = 0;
									
									for(int s = 1; s <= 1600; s++){
										reflectance.setLength(0);
										reflectance.append("X(:,");
										reflectance.append(s);
										reflectance.append(")");
										reflectanceString = reflectance.toString();
										
										curSpec.setLength(0);
										curSpec.append("curSpec = ");
										curSpec.append(reflectanceString);
										curSpec.append(";");
										curSpecString = curSpec.toString();
										//curSpecString contains the curve spec
										proxy.eval(curSpecString);
										
										if(rms_flag == 0){
											//EigenV rms
											rms_eigen.setLength(0);
											rms_eigen.append("rms_eigen = sqrt(sum(((curSpec - rec_eigen(:,");
											rms_eigen.append(s);
											rms_eigen.append(")) .* (curSpec - rec_eigen(:,");
											rms_eigen.append(s);
											rms_eigen.append("))))/401);");
											rms_eigenString = rms_eigen.toString();
											proxy.eval(rms_eigenString);
											
											rms_flag = 1;
										}else{
											//EigenV rms
											rms_eigen.setLength(0);
											rms_eigen.append("rms_eigen = [rms_eigen sqrt(sum(((curSpec - rec_eigen(:,");
											rms_eigen.append(s);
											rms_eigen.append(")) .* (curSpec - rec_eigen(:,");
											rms_eigen.append(s);
											rms_eigen.append("))))/401)];");
											rms_eigenString = rms_eigen.toString();
											proxy.eval(rms_eigenString);
											
										}
										
										if(de2000_flag == 0){
											DE2000.setLength(0);
											DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											DE2000.setLength(0);
											DE2000.append("XYZ_eigen = xyz2lab([d65_triplets_rec_eigen(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_eigen(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_eigen(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											proxy.eval("de_eigen = de2000(XYZ,XYZ_eigen)");
											
											de2000_flag = 1;
										}else{
											DE2000.setLength(0);
											DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											DE2000.setLength(0);
											DE2000.append("XYZ_eigen = xyz2lab([d65_triplets_rec_eigen(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_eigen(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_eigen(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											proxy.eval("de_eigen = [de_eigen de2000(XYZ,XYZ_eigen)];");
										}
										
										System.out.println(s + "/1600 rms/DE completed\r");
									}
									
									rms_flag = 0;
									
									mean = proxy.returningEval("mean(rms_eigen)",1);
									std = proxy.returningEval("std(rms_eigen)",1);
									max = proxy.returningEval("max(rms_eigen)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Eigen (rms/DE)-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_eigen)",1);
									std = proxy.returningEval("std(de_eigen)",1);
									max = proxy.returningEval("max(de_eigen)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
								}
							break;
							case 3:
								if(alles == 0){
									proxy.eval("lambdas = 380:780;");
									proxy.eval("rec_paulus = reconstruction_paulus(signals, spectralWeights, [], 0.01);");
									proxy.eval("figure()");
									proxy.eval("hold on;");
									proxy.eval("plot(lambdas, curSpec);");
									proxy.eval("plot(lambdas, rec_paulus);");
									proxy.eval("xlim([lambdas(1), lambdas(end)]);");
									proxy.eval("ylim([0, 1]);");
									proxy.eval("legend('Original', 'Linear Estimation')");
									proxy.eval("xlabel('Wavelength in nm')");
									proxy.eval("ylabel('spectral object reflectance')");
								}else{
									proxy.eval("lambdas = 380:780;");
									int rec_flag = 0;
									int d65_ori_flag = 0;
									int d65_rec_flag = 0;
									
									for(int s = 1; s <= 1600; s++){
										
										reflectance.setLength(0);
										reflectance.append("X(:,");
										reflectance.append(s);
										reflectance.append(")");
										reflectanceString = reflectance.toString();
										
										curSpec.setLength(0);
										curSpec.append("curSpec = ");
										curSpec.append(reflectanceString);
										curSpec.append(";");
										curSpecString = curSpec.toString();
										//curSpecString contains the curve spec
										proxy.eval(curSpecString);
										
										proxy.eval("signals = spectralWeights * curSpec;");
										
										if(d65_ori_flag == 0){
											proxy.eval("d65_triplets_original = spectralWeights2 * curSpec;");
											d65_ori_flag = 1;
										}else{
											proxy.eval("d65_triplets_original = [d65_triplets_original (spectralWeights2 * curSpec)];");
										}
										
										if(rec_flag == 0){
											proxy.eval("rec_paulus = reconstruction_paulus(signals, spectralWeights, [], 0.01);");
										
											rec_flag = 1;
										}else{
											proxy.eval("rec_paulus = [rec_paulus reconstruction_paulus(signals, spectralWeights, [], 0.01)];");
											
										}
										
										if(d65_rec_flag == 0){
											D65_triplets_rec.setLength(0);
											D65_triplets_rec.append("d65_triplets_rec_paulus = spectralWeights2 * rec_paulus(:,");
											D65_triplets_rec.append(s);
											D65_triplets_rec.append(");");
											D65_triplets_recString = D65_triplets_rec.toString();
											proxy.eval(D65_triplets_recString);
											
											d65_rec_flag = 1;
										}else{
											D65_triplets_rec.setLength(0);
											D65_triplets_rec.append("d65_triplets_rec_paulus = [d65_triplets_rec_paulus (spectralWeights2 * rec_paulus(:,");
											D65_triplets_rec.append(s);
											D65_triplets_rec.append("))];");
											D65_triplets_recString = D65_triplets_rec.toString();
											proxy.eval(D65_triplets_recString);
										}
										
										System.out.println(s + "/1600 reconstruction completed\r");
								
									}
									int rms_flag = 0;
									int de2000_flag = 0;
									
									
									for(int s = 1; s <= 1600; s++){
										reflectance.setLength(0);
										reflectance.append("X(:,");
										reflectance.append(s);
										reflectance.append(")");
										reflectanceString = reflectance.toString();
										
										curSpec.setLength(0);
										curSpec.append("curSpec = ");
										curSpec.append(reflectanceString);
										curSpec.append(";");
										curSpecString = curSpec.toString();
										//curSpecString contains the curve spec
										proxy.eval(curSpecString);
										
										if(rms_flag == 0){
											//Paulus rms
											rms_paulus.setLength(0);
											rms_paulus.append("rms_paulus = sqrt(sum(((curSpec - rec_paulus(:,");
											rms_paulus.append(s);
											rms_paulus.append(")) .* (curSpec - rec_paulus(:,");
											rms_paulus.append(s);
											rms_paulus.append("))))/401);");
											rms_paulusString = rms_paulus.toString();
											proxy.eval(rms_paulusString);
											
											rms_flag = 1;
										}else{
											//Paulus rms
											rms_paulus.setLength(0);
											rms_paulus.append("rms_paulus = [rms_paulus sqrt(sum(((curSpec - rec_paulus(:,");
											rms_paulus.append(s);
											rms_paulus.append(")) .* (curSpec - rec_paulus(:,");
											rms_paulus.append(s);
											rms_paulus.append("))))/401)];");
											rms_paulusString = rms_paulus.toString();
											proxy.eval(rms_paulusString);
										}
										
										if(de2000_flag == 0){
											DE2000.setLength(0);
											DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											DE2000.setLength(0);
											DE2000.append("XYZ_paulus = xyz2lab([d65_triplets_rec_paulus(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_paulus(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_paulus(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											proxy.eval("de_paulus = de2000(XYZ,XYZ_paulus)");
											
											de2000_flag = 1;
										}else{
											DE2000.setLength(0);
											DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
									
											DE2000.setLength(0);
											DE2000.append("XYZ_paulus = xyz2lab([d65_triplets_rec_paulus(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_paulus(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_paulus(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											proxy.eval("de_paulus = [de_paulus de2000(XYZ,XYZ_paulus)];");
										}
										
										System.out.println(s + "/1600 rms/DE completed\r");
									}
									
									rms_flag = 0;
									
									mean = proxy.returningEval("mean(rms_paulus)",1);
									std = proxy.returningEval("std(rms_paulus)",1);
									max = proxy.returningEval("max(rms_paulus)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Paulus (rms/DE)-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_paulus)",1);
									std = proxy.returningEval("std(de_paulus)",1);
									max = proxy.returningEval("max(de_paulus)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
								}
								selection = 0;
							break;
							case 4:
								if(alles == 0){
									proxy.eval("lambdas = 380:780;");
									proxy.eval("rec_paulus = reconstruction_paulus(signals, spectralWeights, [], 0.01);");
									proxy.eval("q = cntIllus*3");
									proxy.eval("rec_eigen = reconstruction_eigen(signals, spectralWeights, [], q);");
									proxy.eval("rec_wiener = wiener_estimation(signals, spectralWeights, 0.1, 1);");
									proxy.eval("figure()");
									proxy.eval("hold on;");
									proxy.eval("plot(lambdas, curSpec);");
									proxy.eval("plot(lambdas, rec_eigen);");
									proxy.eval("plot(lambdas, rec_wiener);");
									proxy.eval("plot(lambdas, rec_paulus);");
									proxy.eval("xlim([lambdas(1), lambdas(end)]);");
									proxy.eval("ylim([0, 1]);");
									proxy.eval("legend('Original', 'Principal Eigenvectors', 'Wiener Estimation', 'Linear Estimation')");
									proxy.eval("xlabel('Wavelength in nm')");
									proxy.eval("ylabel('spectral object reflectance')");
									
									proxy.eval("rms_paulus = ((curSpec - rec_paulus)' .* (curSpec - rec_paulus))/lambdas");
									proxy.eval("rms_wiener = ((curSpec - rec_wiener)' .* (curSpec - rec_wiener))/lambdas");
									proxy.eval("rms_eigen = ((curSpec - rec_eigen)' .* (curSpec - rec_eigen))/lambdas");
									
									mean = proxy.returningEval("mean(rms_paulus)",1);
									std = proxy.returningEval("std(rms_paulus)",1);
									max = proxy.returningEval("max(rms_paulus)",1);
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Paulus-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_wiener)",1);
									std = proxy.returningEval("std(rms_wiener)",1);
									max = proxy.returningEval("max(rms_wiener)",1);
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Wiener-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_eigen)",1);
									std = proxy.returningEval("std(rms_eigen)",1);
									max = proxy.returningEval("max(rms_eigen)",1);
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Eigen-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
								}else{
									proxy.eval("lambdas = 380:780;");
									proxy.eval("q = cntIllus*3;");
									int rec_flag = 0;
									int d65_ori_flag = 0;
									int d65_rec_flag = 0;
										
									for(int s = 1; s <= 1600; s++){
										
										reflectance.setLength(0);
										reflectance.append("X(:,");
										reflectance.append(s);
										reflectance.append(")");
										reflectanceString = reflectance.toString();
										
										curSpec.setLength(0);
										curSpec.append("curSpec = ");
										curSpec.append(reflectanceString);
										curSpec.append(";");
										curSpecString = curSpec.toString();
										//curSpecString contains the curve spec
										proxy.eval(curSpecString);
										
										proxy.eval("signals = spectralWeights * curSpec;");
										
										
										if(d65_ori_flag == 0){
											proxy.eval("d65_triplets_original = spectralWeights2 * curSpec;");
											d65_ori_flag = 1;
										}else{
											proxy.eval("d65_triplets_original = [d65_triplets_original (spectralWeights2 * curSpec)];");
										}
										
										if(rec_flag == 0){
											proxy.eval("rec_wiener = wiener_estimation(signals, spectralWeights, 0.1, 1);");
											proxy.eval("rec_paulus = reconstruction_paulus(signals, spectralWeights, [], 0.01);");
											proxy.eval("rec_eigen = reconstruction_eigen(signals, spectralWeights, [], q);");
										
											rec_flag = 1;
										}else{
											proxy.eval("rec_wiener = [rec_wiener wiener_estimation(signals, spectralWeights, 0.1, 1)];");
											proxy.eval("rec_paulus = [rec_paulus reconstruction_paulus(signals, spectralWeights, [], 0.01)];");
											proxy.eval("rec_eigen = [rec_eigen reconstruction_eigen(signals, spectralWeights, [], q)];");
											
										}
										
										if(d65_rec_flag == 0){
											D65_triplets_rec.setLength(0);
											D65_triplets_rec.append("d65_triplets_rec_wiener = spectralWeights2 * rec_wiener(:,");
											D65_triplets_rec.append(s);
											D65_triplets_rec.append(");");
											D65_triplets_recString = D65_triplets_rec.toString();
											proxy.eval(D65_triplets_recString);
											
											D65_triplets_rec.setLength(0);
											D65_triplets_rec.append("d65_triplets_rec_paulus = spectralWeights2 * rec_paulus(:,");
											D65_triplets_rec.append(s);
											D65_triplets_rec.append(");");
											D65_triplets_recString = D65_triplets_rec.toString();
											proxy.eval(D65_triplets_recString);
											
											D65_triplets_rec.setLength(0);
											D65_triplets_rec.append("d65_triplets_rec_eigen = spectralWeights2 * rec_eigen(:,");
											D65_triplets_rec.append(s);
											D65_triplets_rec.append(");");
											D65_triplets_recString = D65_triplets_rec.toString();
											proxy.eval(D65_triplets_recString);
											
											d65_rec_flag = 1;
										}else{
											D65_triplets_rec.setLength(0);
											D65_triplets_rec.append("d65_triplets_rec_wiener = [d65_triplets_rec_wiener (spectralWeights2 * rec_wiener(:,");
											D65_triplets_rec.append(s);
											D65_triplets_rec.append("))];");
											D65_triplets_recString = D65_triplets_rec.toString();
											proxy.eval(D65_triplets_recString);
											
											D65_triplets_rec.setLength(0);
											D65_triplets_rec.append("d65_triplets_rec_paulus = [d65_triplets_rec_paulus (spectralWeights2 * rec_paulus(:,");
											D65_triplets_rec.append(s);
											D65_triplets_rec.append("))];");
											D65_triplets_recString = D65_triplets_rec.toString();
											proxy.eval(D65_triplets_recString);
											
											D65_triplets_rec.setLength(0);
											D65_triplets_rec.append("d65_triplets_rec_eigen = [d65_triplets_rec_eigen (spectralWeights2 * rec_eigen(:,");
											D65_triplets_rec.append(s);
											D65_triplets_rec.append("))];");
											D65_triplets_recString = D65_triplets_rec.toString();
											proxy.eval(D65_triplets_recString);
											
										}
										
										System.out.println(s + "/1600 reconstruction completed\r");
								
									}
									int rms_flag = 0;
									int de2000_flag = 0;
									
									
									for(int s = 1; s <= 1600; s++){
										reflectance.setLength(0);
										reflectance.append("X(:,");
										reflectance.append(s);
										reflectance.append(")");
										reflectanceString = reflectance.toString();
										
										curSpec.setLength(0);
										curSpec.append("curSpec = ");
										curSpec.append(reflectanceString);
										curSpec.append(";");
										curSpecString = curSpec.toString();
										//curSpecString contains the curve spec
										proxy.eval(curSpecString);
										
										if(rms_flag == 0){
											//Wiener rms
											rms_wiener.setLength(0);
											rms_wiener.append("rms_wiener = sqrt(sum(((curSpec - rec_wiener(:,");
											rms_wiener.append(s);
											rms_wiener.append(")) .* (curSpec - rec_wiener(:,");
											rms_wiener.append(s);
											rms_wiener.append("))))/401);");
											rms_wienerString = rms_wiener.toString();
											proxy.eval(rms_wienerString);
											
											//Paulus rms
											rms_paulus.setLength(0);
											rms_paulus.append("rms_paulus = sqrt(sum(((curSpec - rec_paulus(:,");
											rms_paulus.append(s);
											rms_paulus.append(")) .* (curSpec - rec_paulus(:,");
											rms_paulus.append(s);
											rms_paulus.append("))))/401);");
											rms_paulusString = rms_paulus.toString();
											proxy.eval(rms_paulusString);
											
											//EigenV rms
											rms_eigen.setLength(0);
											rms_eigen.append("rms_eigen = sqrt(sum(((curSpec - rec_eigen(:,");
											rms_eigen.append(s);
											rms_eigen.append(")) .* (curSpec - rec_eigen(:,");
											rms_eigen.append(s);
											rms_eigen.append("))))/401);");
											rms_eigenString = rms_eigen.toString();
											proxy.eval(rms_eigenString);
											
											rms_flag = 1;
										}else{
											//Wiener rms
											rms_wiener.setLength(0);
											rms_wiener.append("rms_wiener = [rms_wiener sqrt(sum(((curSpec - rec_wiener(:,");
											rms_wiener.append(s);
											rms_wiener.append(")) .* (curSpec - rec_wiener(:,");
											rms_wiener.append(s);
											rms_wiener.append("))))/401)];");
											rms_wienerString = rms_wiener.toString();
											proxy.eval(rms_wienerString);
											
											//Paulus rms
											rms_paulus.setLength(0);
											rms_paulus.append("rms_paulus = [rms_paulus sqrt(sum(((curSpec - rec_paulus(:,");
											rms_paulus.append(s);
											rms_paulus.append(")) .* (curSpec - rec_paulus(:,");
											rms_paulus.append(s);
											rms_paulus.append("))))/401)];");
											rms_paulusString = rms_paulus.toString();
											proxy.eval(rms_paulusString);
											
											//EigenV rms
											rms_eigen.setLength(0);
											rms_eigen.append("rms_eigen = [rms_eigen sqrt(sum(((curSpec - rec_eigen(:,");
											rms_eigen.append(s);
											rms_eigen.append(")) .* (curSpec - rec_eigen(:,");
											rms_eigen.append(s);
											rms_eigen.append("))))/401)];");
											rms_eigenString = rms_eigen.toString();
											proxy.eval(rms_eigenString);
											
										}
										
										if(de2000_flag == 0){
											DE2000.setLength(0);
											DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											DE2000.setLength(0);
											DE2000.append("XYZ_wiener = xyz2lab([d65_triplets_rec_wiener(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_wiener(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_wiener(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											DE2000.setLength(0);
											DE2000.append("XYZ_paulus = xyz2lab([d65_triplets_rec_paulus(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_paulus(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_paulus(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											DE2000.setLength(0);
											DE2000.append("XYZ_eigen = xyz2lab([d65_triplets_rec_eigen(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_eigen(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_eigen(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											proxy.eval("de_wiener = de2000(XYZ,XYZ_wiener)");
											proxy.eval("de_paulus = de2000(XYZ,XYZ_paulus)");
											proxy.eval("de_eigen = de2000(XYZ,XYZ_eigen)");
											
											de2000_flag = 1;
										}else{
											DE2000.setLength(0);
											DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_original(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											DE2000.setLength(0);
											DE2000.append("XYZ_wiener = xyz2lab([d65_triplets_rec_wiener(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_wiener(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_wiener(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											DE2000.setLength(0);
											DE2000.append("XYZ_paulus = xyz2lab([d65_triplets_rec_paulus(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_paulus(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_paulus(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											DE2000.setLength(0);
											DE2000.append("XYZ_eigen = xyz2lab([d65_triplets_rec_eigen(1,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_eigen(2,");
											DE2000.append(s);
											DE2000.append("),d65_triplets_rec_eigen(3,");
											DE2000.append(s);
											DE2000.append(")])");
											DE2000String = DE2000.toString();
											proxy.eval(DE2000String);
											
											proxy.eval("de_wiener = [de_wiener de2000(XYZ,XYZ_wiener)];");
											proxy.eval("de_paulus = [de_paulus de2000(XYZ,XYZ_paulus)];");
											proxy.eval("de_eigen = [de_eigen de2000(XYZ,XYZ_eigen)];");
										}
										
										System.out.println(s + "/1600 rms/DE completed\r");
									}
									
									rms_flag = 0;
								
									System.out.println("chegou aqui\n");
									mean = proxy.returningEval("mean(rms_wiener)",1);
									std = proxy.returningEval("std(rms_wiener)",1);
									max = proxy.returningEval("max(rms_wiener)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Wiener (rms/DE)-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_wiener)",1);
									std = proxy.returningEval("std(de_wiener)",1);
									max = proxy.returningEval("max(de_wiener)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_paulus)",1);
									std = proxy.returningEval("std(rms_paulus)",1);
									max = proxy.returningEval("max(rms_paulus)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Paulus (rms/DE)-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_paulus)",1);
									std = proxy.returningEval("std(de_paulus)",1);
									max = proxy.returningEval("max(de_paulus)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_eigen)",1);
									std = proxy.returningEval("std(rms_eigen)",1);
									max = proxy.returningEval("max(rms_eigen)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Eigen (rms/DE)-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_eigen)",1);
									std = proxy.returningEval("std(de_eigen)",1);
									max = proxy.returningEval("max(de_eigen)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
								}
				  
							break;
							//noise at 0.1, 10 samples
							case 5:
								if(alles == 0){
									proxy.eval("lambdas = 380:780;");
									proxy.eval("rec_paulus = reconstruction_paulus(signals, spectralWeights, [], 0.01);");
									proxy.eval("q = cntIllus*3");
									proxy.eval("rec_eigen = reconstruction_eigen(signals, spectralWeights, [], q);");
									proxy.eval("rec_wiener = wiener_estimation(signals, spectralWeights, 0.1, 1);");
									proxy.eval("figure()");
									proxy.eval("hold on;");
									proxy.eval("plot(lambdas, curSpec);");
									proxy.eval("plot(lambdas, rec_eigen);");
									proxy.eval("plot(lambdas, rec_wiener);");
									proxy.eval("plot(lambdas, rec_paulus);");
									proxy.eval("xlim([lambdas(1), lambdas(end)]);");
									proxy.eval("ylim([0, 1]);");
									proxy.eval("legend('Original', 'Principal Eigenvectors', 'Wiener Estimation', 'Linear Estimation')");
									proxy.eval("xlabel('Wavelength in nm')");
									proxy.eval("ylabel('spectral object reflectance')");
									
									proxy.eval("rms_paulus = ((curSpec - rec_paulus)' .* (curSpec - rec_paulus))/lambdas");
									proxy.eval("rms_wiener = ((curSpec - rec_wiener)' .* (curSpec - rec_wiener))/lambdas");
									proxy.eval("rms_eigen = ((curSpec - rec_eigen)' .* (curSpec - rec_eigen))/lambdas");
									
									mean = proxy.returningEval("mean(rms_paulus)",1);
									std = proxy.returningEval("std(rms_paulus)",1);
									max = proxy.returningEval("max(rms_paulus)",1);
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Paulus-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_wiener)",1);
									std = proxy.returningEval("std(rms_wiener)",1);
									max = proxy.returningEval("max(rms_wiener)",1);
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Wiener-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_eigen)",1);
									std = proxy.returningEval("std(rms_eigen)",1);
									max = proxy.returningEval("max(rms_eigen)",1);
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Eigen-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
								}else{
									proxy.eval("lambdas = 380:780;");
									proxy.eval("q = cntIllus*3;");
									
									int noise_flag = 0;
									
									for(int n=1;n<=100;n++){
										
										int rec_flag = 0;
										int d65_ori_flag = 0;
										int d65_rec_flag = 0;
										
										proxy.eval("noise = repmat(wgn(3, 1, 0.1), cntIllus, 1);");
											
										for(int s = 1; s <= 1600; s++){
											
											reflectance.setLength(0);
											reflectance.append("X(:,");
											reflectance.append(s);
											reflectance.append(")");
											reflectanceString = reflectance.toString();
											
											curSpec.setLength(0);
											curSpec.append("curSpec = ");
											curSpec.append(reflectanceString);
											curSpec.append(";");
											curSpecString = curSpec.toString();
											//curSpecString contains the curve spec
											proxy.eval(curSpecString);
											
											proxy.eval("signals = spectralWeights * curSpec;");
											
											
											if(d65_ori_flag == 0){
												proxy.eval("d65_triplets_original = spectralWeights2 * curSpec;");
												d65_ori_flag = 1;
											}else{
												proxy.eval("d65_triplets_original = [d65_triplets_original (spectralWeights2 * curSpec)];");
											}
											
											if(rec_flag == 0){
												proxy.eval("rec_wiener = wiener_estimation(signals + noise, spectralWeights, 0.1, 1);");
												proxy.eval("rec_paulus = reconstruction_paulus(signals + noise, spectralWeights, [], 0.01);");
												proxy.eval("rec_eigen = reconstruction_eigen(signals + noise, spectralWeights, [], q);");
											
												rec_flag = 1;
											}else{
												proxy.eval("rec_wiener = [rec_wiener wiener_estimation(signals + noise, spectralWeights, 0.1, 1)];");
												proxy.eval("rec_paulus = [rec_paulus reconstruction_paulus(signals + noise, spectralWeights, [], 0.01)];");
												proxy.eval("rec_eigen = [rec_eigen reconstruction_eigen(signals + noise, spectralWeights, [], q)];");
												
											}
											
											if(d65_rec_flag == 0){
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_wiener = spectralWeights2 * rec_wiener(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append(");");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_paulus = spectralWeights2 * rec_paulus(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append(");");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_eigen = spectralWeights2 * rec_eigen(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append(");");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												d65_rec_flag = 1;
											}else{
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_wiener = [d65_triplets_rec_wiener (spectralWeights2 * rec_wiener(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append("))];");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_paulus = [d65_triplets_rec_paulus (spectralWeights2 * rec_paulus(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append("))];");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_eigen = [d65_triplets_rec_eigen (spectralWeights2 * rec_eigen(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append("))];");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
											}
											
											System.out.println(s + "/1600 reconstruction completed - " + n + "/100\r");
									
										}
										int rms_flag = 0;
										int de2000_flag = 0;
										
										
										for(int s = 1; s <= 1600; s++){
											reflectance.setLength(0);
											reflectance.append("X(:,");
											reflectance.append(s);
											reflectance.append(")");
											reflectanceString = reflectance.toString();
											
											curSpec.setLength(0);
											curSpec.append("curSpec = ");
											curSpec.append(reflectanceString);
											curSpec.append(";");
											curSpecString = curSpec.toString();
											//curSpecString contains the curve spec
											proxy.eval(curSpecString);
											
											if(rms_flag == 0){
												//Wiener rms
												rms_wiener.setLength(0);
												rms_wiener.append("rms_wiener = sqrt(sum(((curSpec - rec_wiener(:,");
												rms_wiener.append(s);
												rms_wiener.append(")) .* (curSpec - rec_wiener(:,");
												rms_wiener.append(s);
												rms_wiener.append("))))/401);");
												rms_wienerString = rms_wiener.toString();
												proxy.eval(rms_wienerString);
												
												//Paulus rms
												rms_paulus.setLength(0);
												rms_paulus.append("rms_paulus = sqrt(sum(((curSpec - rec_paulus(:,");
												rms_paulus.append(s);
												rms_paulus.append(")) .* (curSpec - rec_paulus(:,");
												rms_paulus.append(s);
												rms_paulus.append("))))/401);");
												rms_paulusString = rms_paulus.toString();
												proxy.eval(rms_paulusString);
												
												//EigenV rms
												rms_eigen.setLength(0);
												rms_eigen.append("rms_eigen = sqrt(sum(((curSpec - rec_eigen(:,");
												rms_eigen.append(s);
												rms_eigen.append(")) .* (curSpec - rec_eigen(:,");
												rms_eigen.append(s);
												rms_eigen.append("))))/401);");
												rms_eigenString = rms_eigen.toString();
												proxy.eval(rms_eigenString);
												
												rms_flag = 1;
											}else{
												//Wiener rms
												rms_wiener.setLength(0);
												rms_wiener.append("rms_wiener = [rms_wiener sqrt(sum(((curSpec - rec_wiener(:,");
												rms_wiener.append(s);
												rms_wiener.append(")) .* (curSpec - rec_wiener(:,");
												rms_wiener.append(s);
												rms_wiener.append("))))/401)];");
												rms_wienerString = rms_wiener.toString();
												proxy.eval(rms_wienerString);
												
												//Paulus rms
												rms_paulus.setLength(0);
												rms_paulus.append("rms_paulus = [rms_paulus sqrt(sum(((curSpec - rec_paulus(:,");
												rms_paulus.append(s);
												rms_paulus.append(")) .* (curSpec - rec_paulus(:,");
												rms_paulus.append(s);
												rms_paulus.append("))))/401)];");
												rms_paulusString = rms_paulus.toString();
												proxy.eval(rms_paulusString);
												
												//EigenV rms
												rms_eigen.setLength(0);
												rms_eigen.append("rms_eigen = [rms_eigen sqrt(sum(((curSpec - rec_eigen(:,");
												rms_eigen.append(s);
												rms_eigen.append(")) .* (curSpec - rec_eigen(:,");
												rms_eigen.append(s);
												rms_eigen.append("))))/401)];");
												rms_eigenString = rms_eigen.toString();
												proxy.eval(rms_eigenString);
												
											}
											
											if(de2000_flag == 0){
												DE2000.setLength(0);
												DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_original(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_original(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_wiener = xyz2lab([d65_triplets_rec_wiener(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_wiener(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_wiener(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_paulus = xyz2lab([d65_triplets_rec_paulus(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_paulus(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_paulus(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_eigen = xyz2lab([d65_triplets_rec_eigen(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_eigen(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_eigen(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												proxy.eval("de_wiener = de2000(XYZ,XYZ_wiener)");
												proxy.eval("de_paulus = de2000(XYZ,XYZ_paulus)");
												proxy.eval("de_eigen = de2000(XYZ,XYZ_eigen)");
												
												de2000_flag = 1;
											}else{
												DE2000.setLength(0);
												DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_original(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_original(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_wiener = xyz2lab([d65_triplets_rec_wiener(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_wiener(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_wiener(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_paulus = xyz2lab([d65_triplets_rec_paulus(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_paulus(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_paulus(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_eigen = xyz2lab([d65_triplets_rec_eigen(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_eigen(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_eigen(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												proxy.eval("de_wiener = [de_wiener de2000(XYZ,XYZ_wiener)];");
												proxy.eval("de_paulus = [de_paulus de2000(XYZ,XYZ_paulus)];");
												proxy.eval("de_eigen = [de_eigen de2000(XYZ,XYZ_eigen)];");
											}
											
											System.out.println(s + "/1600 rms/DE completed - " + n  + "/100\r");
										}
									
																		
										rms_flag = 0;
										
										if(noise_flag == 0){
											proxy.eval("rms_wiener_mean = mean(rms_wiener);");
											proxy.eval("rms_wiener_std = std(rms_wiener);");
											proxy.eval("rms_wiener_max = max(rms_wiener);");
											proxy.eval("de_wiener_mean = mean(de_wiener);");
											proxy.eval("de_wiener_std = std(de_wiener);");
											proxy.eval("de_wiener_max = max(de_wiener);");
											
											proxy.eval("rms_paulus_mean = mean(rms_paulus);");
											proxy.eval("rms_paulus_std = std(rms_paulus);");
											proxy.eval("rms_paulus_max = max(rms_paulus);");
											proxy.eval("de_paulus_mean = mean(de_paulus);");
											proxy.eval("de_paulus_std = std(de_paulus);");
											proxy.eval("de_paulus_max = max(de_paulus);");
											
											proxy.eval("rms_eigen_mean = mean(rms_eigen);");
											proxy.eval("rms_eigen_std = std(rms_eigen);");
											proxy.eval("rms_eigen_max = max(rms_eigen);");
											proxy.eval("de_eigen_mean = mean(de_eigen);");
											proxy.eval("de_eigen_std = std(de_eigen);");
											proxy.eval("de_eigen_max = max(de_eigen);");
											
											noise_flag++;
										}else{
											proxy.eval("rms_wiener_mean = [rms_wiener_mean mean(rms_wiener)];");
											proxy.eval("rms_wiener_std = [rms_wiener_std std(rms_wiener)];");
											proxy.eval("rms_wiener_max = [rms_wiener_max max(rms_wiener)];");
											proxy.eval("de_wiener_mean = [de_wiener_mean mean(de_wiener)];");
											proxy.eval("de_wiener_std = [de_wiener_std std(de_wiener)];");
											proxy.eval("de_wiener_max = [de_wiener_max max(de_wiener)];");
											
											proxy.eval("rms_paulus_mean = [rms_paulus_mean mean(rms_paulus)];");
											proxy.eval("rms_paulus_std = [rms_paulus_std std(rms_paulus)];");
											proxy.eval("rms_paulus_max = [rms_paulus_max max(rms_paulus)];");
											proxy.eval("de_paulus_mean = [de_paulus_mean mean(de_paulus)];");
											proxy.eval("de_paulus_std = [de_paulus_std std(de_paulus)];");
											proxy.eval("de_paulus_max = [de_paulus_max max(de_paulus)];");
											
											proxy.eval("rms_eigen_mean = [rms_eigen_mean mean(rms_eigen)];");
											proxy.eval("rms_eigen_std = [rms_eigen_std std(rms_eigen)];");
											proxy.eval("rms_eigen_max = [rms_eigen_max max(rms_eigen)];");
											proxy.eval("de_eigen_mean = [de_eigen_mean mean(de_eigen)];");
											proxy.eval("de_eigen_std = [de_eigen_std std(de_eigen)];");
											proxy.eval("de_eigen_max = [de_eigen_max max(de_eigen)];");
										}
									}
									
									noise_flag = 0;
									
									mean = proxy.returningEval("mean(rms_wiener_mean)",1);
									std = proxy.returningEval("std(rms_wiener_std)",1);
									max = proxy.returningEval("max(rms_wiener_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Wiener (rms/DE) w/ noise 0.1-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_wiener_mean)",1);
									std = proxy.returningEval("std(de_wiener_std)",1);
									max = proxy.returningEval("max(de_wiener_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_paulus_mean)",1);
									std = proxy.returningEval("std(rms_paulus_std)",1);
									max = proxy.returningEval("max(rms_paulus_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Paulus (rms/DE) w/ noise 0.1-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_paulus_mean)",1);
									std = proxy.returningEval("std(de_paulus_std)",1);
									max = proxy.returningEval("max(de_paulus_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_eigen_mean)",1);
									std = proxy.returningEval("std(rms_eigen_std)",1);
									max = proxy.returningEval("max(rms_eigen_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Eigen (rms/DE) w/ noise 0.1-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_eigen_mean)",1);
									std = proxy.returningEval("std(de_eigen_std)",1);
									max = proxy.returningEval("max(de_eigen_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
								}
				  
							break;
							//noise at 0.01, 10 samples
							case 6:
								if(alles == 0){
									proxy.eval("lambdas = 380:780;");
									proxy.eval("rec_paulus = reconstruction_paulus(signals, spectralWeights, [], 0.01);");
									proxy.eval("q = cntIllus*3");
									proxy.eval("rec_eigen = reconstruction_eigen(signals, spectralWeights, [], q);");
									proxy.eval("rec_wiener = wiener_estimation(signals, spectralWeights, 0.1, 1);");
									proxy.eval("figure()");
									proxy.eval("hold on;");
									proxy.eval("plot(lambdas, curSpec);");
									proxy.eval("plot(lambdas, rec_eigen);");
									proxy.eval("plot(lambdas, rec_wiener);");
									proxy.eval("plot(lambdas, rec_paulus);");
									proxy.eval("xlim([lambdas(1), lambdas(end)]);");
									proxy.eval("ylim([0, 1]);");
									proxy.eval("legend('Original', 'Principal Eigenvectors', 'Wiener Estimation', 'Linear Estimation')");
									proxy.eval("xlabel('Wavelength in nm')");
									proxy.eval("ylabel('spectral object reflectance')");
									
									proxy.eval("rms_paulus = ((curSpec - rec_paulus)' .* (curSpec - rec_paulus))/lambdas");
									proxy.eval("rms_wiener = ((curSpec - rec_wiener)' .* (curSpec - rec_wiener))/lambdas");
									proxy.eval("rms_eigen = ((curSpec - rec_eigen)' .* (curSpec - rec_eigen))/lambdas");
									
									mean = proxy.returningEval("mean(rms_paulus)",1);
									std = proxy.returningEval("std(rms_paulus)",1);
									max = proxy.returningEval("max(rms_paulus)",1);
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Paulus-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_wiener)",1);
									std = proxy.returningEval("std(rms_wiener)",1);
									max = proxy.returningEval("max(rms_wiener)",1);
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Wiener-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_eigen)",1);
									std = proxy.returningEval("std(rms_eigen)",1);
									max = proxy.returningEval("max(rms_eigen)",1);
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Eigen-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
								}else{
									proxy.eval("lambdas = 380:780;");
									proxy.eval("q = cntIllus*3;");
									
									int noise_flag = 0;
									
									for(int n=1;n<=100;n++){
										
										int rec_flag = 0;
										int d65_ori_flag = 0;
										int d65_rec_flag = 0;
										
										proxy.eval("noise = repmat(wgn(3, 1, 0.01), cntIllus, 1);");
											
										for(int s = 1; s <= 1600; s++){
											
											reflectance.setLength(0);
											reflectance.append("X(:,");
											reflectance.append(s);
											reflectance.append(")");
											reflectanceString = reflectance.toString();
											
											curSpec.setLength(0);
											curSpec.append("curSpec = ");
											curSpec.append(reflectanceString);
											curSpec.append(";");
											curSpecString = curSpec.toString();
											//curSpecString contains the curve spec
											proxy.eval(curSpecString);
											
											proxy.eval("signals = spectralWeights * curSpec;");
											
											
											if(d65_ori_flag == 0){
												proxy.eval("d65_triplets_original = spectralWeights2 * curSpec;");
												d65_ori_flag = 1;
											}else{
												proxy.eval("d65_triplets_original = [d65_triplets_original (spectralWeights2 * curSpec)];");
											}
											
											if(rec_flag == 0){
												proxy.eval("rec_wiener = wiener_estimation(signals + noise, spectralWeights, 0.1, 1);");
												proxy.eval("rec_paulus = reconstruction_paulus(signals + noise, spectralWeights, [], 0.01);");
												proxy.eval("rec_eigen = reconstruction_eigen(signals + noise, spectralWeights, [], q);");
											
												rec_flag = 1;
											}else{
												proxy.eval("rec_wiener = [rec_wiener wiener_estimation(signals + noise, spectralWeights, 0.1, 1)];");
												proxy.eval("rec_paulus = [rec_paulus reconstruction_paulus(signals + noise, spectralWeights, [], 0.01)];");
												proxy.eval("rec_eigen = [rec_eigen reconstruction_eigen(signals + noise, spectralWeights, [], q)];");
												
											}
											
											if(d65_rec_flag == 0){
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_wiener = spectralWeights2 * rec_wiener(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append(");");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_paulus = spectralWeights2 * rec_paulus(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append(");");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_eigen = spectralWeights2 * rec_eigen(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append(");");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												d65_rec_flag = 1;
											}else{
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_wiener = [d65_triplets_rec_wiener (spectralWeights2 * rec_wiener(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append("))];");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_paulus = [d65_triplets_rec_paulus (spectralWeights2 * rec_paulus(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append("))];");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_eigen = [d65_triplets_rec_eigen (spectralWeights2 * rec_eigen(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append("))];");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
											}
											
											System.out.println(s + "/1600 reconstruction completed - " + n + "/100\r");
									
										}
										int rms_flag = 0;
										int de2000_flag = 0;
										
										
										for(int s = 1; s <= 1600; s++){
											reflectance.setLength(0);
											reflectance.append("X(:,");
											reflectance.append(s);
											reflectance.append(")");
											reflectanceString = reflectance.toString();
											
											curSpec.setLength(0);
											curSpec.append("curSpec = ");
											curSpec.append(reflectanceString);
											curSpec.append(";");
											curSpecString = curSpec.toString();
											//curSpecString contains the curve spec
											proxy.eval(curSpecString);
											
											if(rms_flag == 0){
												//Wiener rms
												rms_wiener.setLength(0);
												rms_wiener.append("rms_wiener = sqrt(sum(((curSpec - rec_wiener(:,");
												rms_wiener.append(s);
												rms_wiener.append(")) .* (curSpec - rec_wiener(:,");
												rms_wiener.append(s);
												rms_wiener.append("))))/401);");
												rms_wienerString = rms_wiener.toString();
												proxy.eval(rms_wienerString);
												
												//Paulus rms
												rms_paulus.setLength(0);
												rms_paulus.append("rms_paulus = sqrt(sum(((curSpec - rec_paulus(:,");
												rms_paulus.append(s);
												rms_paulus.append(")) .* (curSpec - rec_paulus(:,");
												rms_paulus.append(s);
												rms_paulus.append("))))/401);");
												rms_paulusString = rms_paulus.toString();
												proxy.eval(rms_paulusString);
												
												//EigenV rms
												rms_eigen.setLength(0);
												rms_eigen.append("rms_eigen = sqrt(sum(((curSpec - rec_eigen(:,");
												rms_eigen.append(s);
												rms_eigen.append(")) .* (curSpec - rec_eigen(:,");
												rms_eigen.append(s);
												rms_eigen.append("))))/401);");
												rms_eigenString = rms_eigen.toString();
												proxy.eval(rms_eigenString);
												
												rms_flag = 1;
											}else{
												//Wiener rms
												rms_wiener.setLength(0);
												rms_wiener.append("rms_wiener = [rms_wiener sqrt(sum(((curSpec - rec_wiener(:,");
												rms_wiener.append(s);
												rms_wiener.append(")) .* (curSpec - rec_wiener(:,");
												rms_wiener.append(s);
												rms_wiener.append("))))/401)];");
												rms_wienerString = rms_wiener.toString();
												proxy.eval(rms_wienerString);
												
												//Paulus rms
												rms_paulus.setLength(0);
												rms_paulus.append("rms_paulus = [rms_paulus sqrt(sum(((curSpec - rec_paulus(:,");
												rms_paulus.append(s);
												rms_paulus.append(")) .* (curSpec - rec_paulus(:,");
												rms_paulus.append(s);
												rms_paulus.append("))))/401)];");
												rms_paulusString = rms_paulus.toString();
												proxy.eval(rms_paulusString);
												
												//EigenV rms
												rms_eigen.setLength(0);
												rms_eigen.append("rms_eigen = [rms_eigen sqrt(sum(((curSpec - rec_eigen(:,");
												rms_eigen.append(s);
												rms_eigen.append(")) .* (curSpec - rec_eigen(:,");
												rms_eigen.append(s);
												rms_eigen.append("))))/401)];");
												rms_eigenString = rms_eigen.toString();
												proxy.eval(rms_eigenString);
												
											}
											
											if(de2000_flag == 0){
												DE2000.setLength(0);
												DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_original(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_original(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_wiener = xyz2lab([d65_triplets_rec_wiener(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_wiener(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_wiener(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_paulus = xyz2lab([d65_triplets_rec_paulus(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_paulus(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_paulus(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_eigen = xyz2lab([d65_triplets_rec_eigen(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_eigen(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_eigen(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												proxy.eval("de_wiener = de2000(XYZ,XYZ_wiener)");
												proxy.eval("de_paulus = de2000(XYZ,XYZ_paulus)");
												proxy.eval("de_eigen = de2000(XYZ,XYZ_eigen)");
												
												de2000_flag = 1;
											}else{
												DE2000.setLength(0);
												DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_original(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_original(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_wiener = xyz2lab([d65_triplets_rec_wiener(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_wiener(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_wiener(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_paulus = xyz2lab([d65_triplets_rec_paulus(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_paulus(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_paulus(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_eigen = xyz2lab([d65_triplets_rec_eigen(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_eigen(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_eigen(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												proxy.eval("de_wiener = [de_wiener de2000(XYZ,XYZ_wiener)];");
												proxy.eval("de_paulus = [de_paulus de2000(XYZ,XYZ_paulus)];");
												proxy.eval("de_eigen = [de_eigen de2000(XYZ,XYZ_eigen)];");
											}
											
											System.out.println(s + "/1600 rms/DE completed - " + n  + "/100\r");
										}
									
																		
										rms_flag = 0;
										
										if(noise_flag == 0){
											proxy.eval("rms_wiener_mean = mean(rms_wiener);");
											proxy.eval("rms_wiener_std = std(rms_wiener);");
											proxy.eval("rms_wiener_max = max(rms_wiener);");
											proxy.eval("de_wiener_mean = mean(de_wiener);");
											proxy.eval("de_wiener_std = std(de_wiener);");
											proxy.eval("de_wiener_max = max(de_wiener);");
											
											proxy.eval("rms_paulus_mean = mean(rms_paulus);");
											proxy.eval("rms_paulus_std = std(rms_paulus);");
											proxy.eval("rms_paulus_max = max(rms_paulus);");
											proxy.eval("de_paulus_mean = mean(de_paulus);");
											proxy.eval("de_paulus_std = std(de_paulus);");
											proxy.eval("de_paulus_max = max(de_paulus);");
											
											proxy.eval("rms_eigen_mean = mean(rms_eigen);");
											proxy.eval("rms_eigen_std = std(rms_eigen);");
											proxy.eval("rms_eigen_max = max(rms_eigen);");
											proxy.eval("de_eigen_mean = mean(de_eigen);");
											proxy.eval("de_eigen_std = std(de_eigen);");
											proxy.eval("de_eigen_max = max(de_eigen);");
											
											noise_flag++;
										}else{
											proxy.eval("rms_wiener_mean = [rms_wiener_mean mean(rms_wiener)];");
											proxy.eval("rms_wiener_std = [rms_wiener_std std(rms_wiener)];");
											proxy.eval("rms_wiener_max = [rms_wiener_max max(rms_wiener)];");
											proxy.eval("de_wiener_mean = [de_wiener_mean mean(de_wiener)];");
											proxy.eval("de_wiener_std = [de_wiener_std std(de_wiener)];");
											proxy.eval("de_wiener_max = [de_wiener_max max(de_wiener)];");
											
											proxy.eval("rms_paulus_mean = [rms_paulus_mean mean(rms_paulus)];");
											proxy.eval("rms_paulus_std = [rms_paulus_std std(rms_paulus)];");
											proxy.eval("rms_paulus_max = [rms_paulus_max max(rms_paulus)];");
											proxy.eval("de_paulus_mean = [de_paulus_mean mean(de_paulus)];");
											proxy.eval("de_paulus_std = [de_paulus_std std(de_paulus)];");
											proxy.eval("de_paulus_max = [de_paulus_max max(de_paulus)];");
											
											proxy.eval("rms_eigen_mean = [rms_eigen_mean mean(rms_eigen)];");
											proxy.eval("rms_eigen_std = [rms_eigen_std std(rms_eigen)];");
											proxy.eval("rms_eigen_max = [rms_eigen_max max(rms_eigen)];");
											proxy.eval("de_eigen_mean = [de_eigen_mean mean(de_eigen)];");
											proxy.eval("de_eigen_std = [de_eigen_std std(de_eigen)];");
											proxy.eval("de_eigen_max = [de_eigen_max max(de_eigen)];");
										}
									}
									
									noise_flag = 0;
									
									mean = proxy.returningEval("mean(rms_wiener_mean)",1);
									std = proxy.returningEval("std(rms_wiener_std)",1);
									max = proxy.returningEval("max(rms_wiener_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Wiener (rms/DE) w/ noise 0.01-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_wiener_mean)",1);
									std = proxy.returningEval("std(de_wiener_std)",1);
									max = proxy.returningEval("max(de_wiener_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_paulus_mean)",1);
									std = proxy.returningEval("std(rms_paulus_std)",1);
									max = proxy.returningEval("max(rms_paulus_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Paulus (rms/DE) w/ noise 0.01-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_paulus_mean)",1);
									std = proxy.returningEval("std(de_paulus_std)",1);
									max = proxy.returningEval("max(de_paulus_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_eigen_mean)",1);
									std = proxy.returningEval("std(rms_eigen_std)",1);
									max = proxy.returningEval("max(rms_eigen_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Eigen (rms/DE) w/ noise 0.01-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_eigen_mean)",1);
									std = proxy.returningEval("std(de_eigen_std)",1);
									max = proxy.returningEval("max(de_eigen_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
								}
				  
							break;
							//noise at 0.001, 10 samples
							case 7:
								if(alles == 0){
									proxy.eval("lambdas = 380:780;");
									proxy.eval("rec_paulus = reconstruction_paulus(signals, spectralWeights, [], 0.01);");
									proxy.eval("q = cntIllus*3");
									proxy.eval("rec_eigen = reconstruction_eigen(signals, spectralWeights, [], q);");
									proxy.eval("rec_wiener = wiener_estimation(signals, spectralWeights, 0.1, 1);");
									proxy.eval("figure()");
									proxy.eval("hold on;");
									proxy.eval("plot(lambdas, curSpec);");
									proxy.eval("plot(lambdas, rec_eigen);");
									proxy.eval("plot(lambdas, rec_wiener);");
									proxy.eval("plot(lambdas, rec_paulus);");
									proxy.eval("xlim([lambdas(1), lambdas(end)]);");
									proxy.eval("ylim([0, 1]);");
									proxy.eval("legend('Original', 'Principal Eigenvectors', 'Wiener Estimation', 'Linear Estimation')");
									proxy.eval("xlabel('Wavelength in nm')");
									proxy.eval("ylabel('spectral object reflectance')");
									
									proxy.eval("rms_paulus = ((curSpec - rec_paulus)' .* (curSpec - rec_paulus))/lambdas");
									proxy.eval("rms_wiener = ((curSpec - rec_wiener)' .* (curSpec - rec_wiener))/lambdas");
									proxy.eval("rms_eigen = ((curSpec - rec_eigen)' .* (curSpec - rec_eigen))/lambdas");
									
									mean = proxy.returningEval("mean(rms_paulus)",1);
									std = proxy.returningEval("std(rms_paulus)",1);
									max = proxy.returningEval("max(rms_paulus)",1);
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Paulus-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_wiener)",1);
									std = proxy.returningEval("std(rms_wiener)",1);
									max = proxy.returningEval("max(rms_wiener)",1);
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Wiener-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_eigen)",1);
									std = proxy.returningEval("std(rms_eigen)",1);
									max = proxy.returningEval("max(rms_eigen)",1);
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Eigen-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
								}else{
									proxy.eval("lambdas = 380:780;");
									proxy.eval("q = cntIllus*3;");
									
									int noise_flag = 0;
									
									for(int n=1;n<=100;n++){
										
										int rec_flag = 0;
										int d65_ori_flag = 0;
										int d65_rec_flag = 0;
										
										proxy.eval("noise = repmat(wgn(3, 1, 0.001), cntIllus, 1);");
											
										for(int s = 1; s <= 1600; s++){
											
											reflectance.setLength(0);
											reflectance.append("X(:,");
											reflectance.append(s);
											reflectance.append(")");
											reflectanceString = reflectance.toString();
											
											curSpec.setLength(0);
											curSpec.append("curSpec = ");
											curSpec.append(reflectanceString);
											curSpec.append(";");
											curSpecString = curSpec.toString();
											//curSpecString contains the curve spec
											proxy.eval(curSpecString);
											
											proxy.eval("signals = spectralWeights * curSpec;");
											
											
											if(d65_ori_flag == 0){
												proxy.eval("d65_triplets_original = spectralWeights2 * curSpec;");
												d65_ori_flag = 1;
											}else{
												proxy.eval("d65_triplets_original = [d65_triplets_original (spectralWeights2 * curSpec)];");
											}
											
											if(rec_flag == 0){
												proxy.eval("rec_wiener = wiener_estimation(signals + noise, spectralWeights, 0.1, 1);");
												proxy.eval("rec_paulus = reconstruction_paulus(signals + noise, spectralWeights, [], 0.01);");
												proxy.eval("rec_eigen = reconstruction_eigen(signals + noise, spectralWeights, [], q);");
											
												rec_flag = 1;
											}else{
												proxy.eval("rec_wiener = [rec_wiener wiener_estimation(signals + noise, spectralWeights, 0.1, 1)];");
												proxy.eval("rec_paulus = [rec_paulus reconstruction_paulus(signals + noise, spectralWeights, [], 0.01)];");
												proxy.eval("rec_eigen = [rec_eigen reconstruction_eigen(signals + noise, spectralWeights, [], q)];");
												
											}
											
											if(d65_rec_flag == 0){
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_wiener = spectralWeights2 * rec_wiener(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append(");");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_paulus = spectralWeights2 * rec_paulus(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append(");");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_eigen = spectralWeights2 * rec_eigen(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append(");");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												d65_rec_flag = 1;
											}else{
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_wiener = [d65_triplets_rec_wiener (spectralWeights2 * rec_wiener(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append("))];");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_paulus = [d65_triplets_rec_paulus (spectralWeights2 * rec_paulus(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append("))];");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
												D65_triplets_rec.setLength(0);
												D65_triplets_rec.append("d65_triplets_rec_eigen = [d65_triplets_rec_eigen (spectralWeights2 * rec_eigen(:,");
												D65_triplets_rec.append(s);
												D65_triplets_rec.append("))];");
												D65_triplets_recString = D65_triplets_rec.toString();
												proxy.eval(D65_triplets_recString);
												
											}
											
											System.out.println(s + "/1600 reconstruction completed - " + n + "/100\r");
									
										}
										int rms_flag = 0;
										int de2000_flag = 0;
										
										
										for(int s = 1; s <= 1600; s++){
											reflectance.setLength(0);
											reflectance.append("X(:,");
											reflectance.append(s);
											reflectance.append(")");
											reflectanceString = reflectance.toString();
											
											curSpec.setLength(0);
											curSpec.append("curSpec = ");
											curSpec.append(reflectanceString);
											curSpec.append(";");
											curSpecString = curSpec.toString();
											//curSpecString contains the curve spec
											proxy.eval(curSpecString);
											
											if(rms_flag == 0){
												//Wiener rms
												rms_wiener.setLength(0);
												rms_wiener.append("rms_wiener = sqrt(sum(((curSpec - rec_wiener(:,");
												rms_wiener.append(s);
												rms_wiener.append(")) .* (curSpec - rec_wiener(:,");
												rms_wiener.append(s);
												rms_wiener.append("))))/401);");
												rms_wienerString = rms_wiener.toString();
												proxy.eval(rms_wienerString);
												
												//Paulus rms
												rms_paulus.setLength(0);
												rms_paulus.append("rms_paulus = sqrt(sum(((curSpec - rec_paulus(:,");
												rms_paulus.append(s);
												rms_paulus.append(")) .* (curSpec - rec_paulus(:,");
												rms_paulus.append(s);
												rms_paulus.append("))))/401);");
												rms_paulusString = rms_paulus.toString();
												proxy.eval(rms_paulusString);
												
												//EigenV rms
												rms_eigen.setLength(0);
												rms_eigen.append("rms_eigen = sqrt(sum(((curSpec - rec_eigen(:,");
												rms_eigen.append(s);
												rms_eigen.append(")) .* (curSpec - rec_eigen(:,");
												rms_eigen.append(s);
												rms_eigen.append("))))/401);");
												rms_eigenString = rms_eigen.toString();
												proxy.eval(rms_eigenString);
												
												rms_flag = 1;
											}else{
												//Wiener rms
												rms_wiener.setLength(0);
												rms_wiener.append("rms_wiener = [rms_wiener sqrt(sum(((curSpec - rec_wiener(:,");
												rms_wiener.append(s);
												rms_wiener.append(")) .* (curSpec - rec_wiener(:,");
												rms_wiener.append(s);
												rms_wiener.append("))))/401)];");
												rms_wienerString = rms_wiener.toString();
												proxy.eval(rms_wienerString);
												
												//Paulus rms
												rms_paulus.setLength(0);
												rms_paulus.append("rms_paulus = [rms_paulus sqrt(sum(((curSpec - rec_paulus(:,");
												rms_paulus.append(s);
												rms_paulus.append(")) .* (curSpec - rec_paulus(:,");
												rms_paulus.append(s);
												rms_paulus.append("))))/401)];");
												rms_paulusString = rms_paulus.toString();
												proxy.eval(rms_paulusString);
												
												//EigenV rms
												rms_eigen.setLength(0);
												rms_eigen.append("rms_eigen = [rms_eigen sqrt(sum(((curSpec - rec_eigen(:,");
												rms_eigen.append(s);
												rms_eigen.append(")) .* (curSpec - rec_eigen(:,");
												rms_eigen.append(s);
												rms_eigen.append("))))/401)];");
												rms_eigenString = rms_eigen.toString();
												proxy.eval(rms_eigenString);
												
											}
											
											if(de2000_flag == 0){
												DE2000.setLength(0);
												DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_original(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_original(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_wiener = xyz2lab([d65_triplets_rec_wiener(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_wiener(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_wiener(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_paulus = xyz2lab([d65_triplets_rec_paulus(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_paulus(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_paulus(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_eigen = xyz2lab([d65_triplets_rec_eigen(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_eigen(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_eigen(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												proxy.eval("de_wiener = de2000(XYZ,XYZ_wiener)");
												proxy.eval("de_paulus = de2000(XYZ,XYZ_paulus)");
												proxy.eval("de_eigen = de2000(XYZ,XYZ_eigen)");
												
												de2000_flag = 1;
											}else{
												DE2000.setLength(0);
												DE2000.append("XYZ = xyz2lab([d65_triplets_original(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_original(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_original(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_wiener = xyz2lab([d65_triplets_rec_wiener(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_wiener(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_wiener(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_paulus = xyz2lab([d65_triplets_rec_paulus(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_paulus(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_paulus(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												DE2000.setLength(0);
												DE2000.append("XYZ_eigen = xyz2lab([d65_triplets_rec_eigen(1,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_eigen(2,");
												DE2000.append(s);
												DE2000.append("),d65_triplets_rec_eigen(3,");
												DE2000.append(s);
												DE2000.append(")])");
												DE2000String = DE2000.toString();
												proxy.eval(DE2000String);
												
												proxy.eval("de_wiener = [de_wiener de2000(XYZ,XYZ_wiener)];");
												proxy.eval("de_paulus = [de_paulus de2000(XYZ,XYZ_paulus)];");
												proxy.eval("de_eigen = [de_eigen de2000(XYZ,XYZ_eigen)];");
											}
											
											System.out.println(s + "/1600 rms/DE completed - " + n  + "/100\r");
										}
									
																		
										rms_flag = 0;
										
										if(noise_flag == 0){
											proxy.eval("rms_wiener_mean = mean(rms_wiener);");
											proxy.eval("rms_wiener_std = std(rms_wiener);");
											proxy.eval("rms_wiener_max = max(rms_wiener);");
											proxy.eval("de_wiener_mean = mean(de_wiener);");
											proxy.eval("de_wiener_std = std(de_wiener);");
											proxy.eval("de_wiener_max = max(de_wiener);");
											
											proxy.eval("rms_paulus_mean = mean(rms_paulus);");
											proxy.eval("rms_paulus_std = std(rms_paulus);");
											proxy.eval("rms_paulus_max = max(rms_paulus);");
											proxy.eval("de_paulus_mean = mean(de_paulus);");
											proxy.eval("de_paulus_std = std(de_paulus);");
											proxy.eval("de_paulus_max = max(de_paulus);");
											
											proxy.eval("rms_eigen_mean = mean(rms_eigen);");
											proxy.eval("rms_eigen_std = std(rms_eigen);");
											proxy.eval("rms_eigen_max = max(rms_eigen);");
											proxy.eval("de_eigen_mean = mean(de_eigen);");
											proxy.eval("de_eigen_std = std(de_eigen);");
											proxy.eval("de_eigen_max = max(de_eigen);");
											
											noise_flag++;
										}else{
											proxy.eval("rms_wiener_mean = [rms_wiener_mean mean(rms_wiener)];");
											proxy.eval("rms_wiener_std = [rms_wiener_std std(rms_wiener)];");
											proxy.eval("rms_wiener_max = [rms_wiener_max max(rms_wiener)];");
											proxy.eval("de_wiener_mean = [de_wiener_mean mean(de_wiener)];");
											proxy.eval("de_wiener_std = [de_wiener_std std(de_wiener)];");
											proxy.eval("de_wiener_max = [de_wiener_max max(de_wiener)];");
											
											proxy.eval("rms_paulus_mean = [rms_paulus_mean mean(rms_paulus)];");
											proxy.eval("rms_paulus_std = [rms_paulus_std std(rms_paulus)];");
											proxy.eval("rms_paulus_max = [rms_paulus_max max(rms_paulus)];");
											proxy.eval("de_paulus_mean = [de_paulus_mean mean(de_paulus)];");
											proxy.eval("de_paulus_std = [de_paulus_std std(de_paulus)];");
											proxy.eval("de_paulus_max = [de_paulus_max max(de_paulus)];");
											
											proxy.eval("rms_eigen_mean = [rms_eigen_mean mean(rms_eigen)];");
											proxy.eval("rms_eigen_std = [rms_eigen_std std(rms_eigen)];");
											proxy.eval("rms_eigen_max = [rms_eigen_max max(rms_eigen)];");
											proxy.eval("de_eigen_mean = [de_eigen_mean mean(de_eigen)];");
											proxy.eval("de_eigen_std = [de_eigen_std std(de_eigen)];");
											proxy.eval("de_eigen_max = [de_eigen_max max(de_eigen)];");
										}
									}
									
									noise_flag = 0;
									
									mean = proxy.returningEval("mean(rms_wiener_mean)",1);
									std = proxy.returningEval("std(rms_wiener_std)",1);
									max = proxy.returningEval("max(rms_wiener_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Wiener (rms/DE) w/ noise 0.001-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_wiener_mean)",1);
									std = proxy.returningEval("std(de_wiener_std)",1);
									max = proxy.returningEval("max(de_wiener_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_paulus_mean)",1);
									std = proxy.returningEval("std(rms_paulus_std)",1);
									max = proxy.returningEval("max(rms_paulus_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Paulus (rms/DE) w/ noise 0.001-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_paulus_mean)",1);
									std = proxy.returningEval("std(de_paulus_std)",1);
									max = proxy.returningEval("max(de_paulus_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(rms_eigen_mean)",1);
									std = proxy.returningEval("std(rms_eigen_std)",1);
									max = proxy.returningEval("max(rms_eigen_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"-------Eigen (rms/DE) w/ noise 0.001-------\n" +
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
									
									mean = proxy.returningEval("mean(de_eigen_mean)",1);
									std = proxy.returningEval("std(de_eigen_std)",1);
									max = proxy.returningEval("max(de_eigen_max)",1);
									
									meanFirst = mean[0];
									stdFirst = std[0];
									maxFirst = max[0];
									
									System.out.println(
										"mean: " + ((double[]) meanFirst)[0] + "\n" +
										"std: " + ((double[]) stdFirst)[0] + "\n" + 
										"max: " + ((double[]) maxFirst)[0] + "\n"
									);
								}
				  
							break;
							case 8:
							
							break;
							default:
								System.out.println("Invalid selection.");
								break;
						}
						
						
				  }
				  break;
				case 4:
					if(flagTriplets == 0){
						System.out.println("Invalid selection.");
						break;
					}else{
						proxy.disconnect();
						System.out.println("Finished");
						System.exit(0);
						break;
					}
				default:
				  System.out.println("Invalid selection.");
				  break;
			}      
		}while(selection != 3);
    }
}