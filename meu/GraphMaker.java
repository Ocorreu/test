import java.io.*;

import org.jfree.chart.JFreeChart; 
import org.jfree.chart.ChartFactory; 
import org.jfree.chart.ChartUtilities; 
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.io.BufferedReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.math.*;
import java.io.File;
import java.lang.*;

public class GraphMaker {

	private static BufferedReader file;
	private static String word;
	private static int i;
	private static String patternStr;
	private static double value;
	private static String fileName;
	private static File folder;
	private static File[] listOfFiles;
	
   public static void main( String[ ] args ) throws Exception {
	   
	   folder = new File(args[0]);
	   listOfFiles = folder.listFiles();
	   
		DefaultCategoryDataset line_chart_dataset;
		patternStr = "\\d.[0-9]*E\\w*";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher;
		BigDecimal bigD;
				
		i = 0;
		File dir = new File(args[0] + "/JPEG");
		if(!dir.exists())
			dir.mkdir();
		for(int n = 0; n < listOfFiles.length; n++){
			line_chart_dataset = new DefaultCategoryDataset();
			if(listOfFiles[n].isFile()){
				file = new BufferedReader(new InputStreamReader(new FileInputStream(listOfFiles[n])));
				fileName = listOfFiles[n].getName();
				fileName = fileName.replace(".txt", "");
				
				while((word = file.readLine()) != null){
					matcher = pattern.matcher(word);
					if(matcher.find()){
						value = (Double.parseDouble(word));
					}else{
						value = Double.parseDouble(word);
					}
					line_chart_dataset.addValue( value , fileName , Integer.toString(i));
					i++;
				} 
			  

				JFreeChart lineChartObject = ChartFactory.createLineChart(
					fileName,"nM",
					"Value",
					line_chart_dataset,PlotOrientation.VERTICAL,
					true,true,false);

				int width = 1920;    /* Width of the image */
				int height = 1080;   /* Height of the image */ 
				File lineChart = new File( args[0] + "/JPEG/" + fileName + ".jpeg" );
				
				ChartUtilities.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
			}
		}
   }
}