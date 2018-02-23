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

import matlabcontrol.*;

public class ScreenCalibration {

    private Socket socket;
    private PrintWriter out;
	private static PrintWriter writer;
    private Scanner sc;
	private static String fromServer;
	private static String fromUser;
	private static BufferedReader file;
	private static BufferedReader in;
	private static String userInput;
	private static StringBuilder stringBuilder;
	private static String line;
	private static String fileName;
	private static File measureDir;


    /**
     * Initialize connection to the phone
     *
     */
    public void initializeConnection(String filename){
        //Create socket connection
        try{
            socket = new Socket("localhost", 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			fileName = filename;

			stringBuilder = new StringBuilder();
			
			file = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			
            
            // add a shutdown hook to close the socket if system crashes or exists unexpectedly
            Thread closeSocketOnShutdown = new Thread() {
                public void run() {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            Runtime.getRuntime().addShutdownHook(closeSocketOnShutdown);

        } catch (UnknownHostException e) {
            System.err.println("Socket connection problem (Unknown host)" + e.getStackTrace());
        } catch (IOException e) {
            System.err.println("Could not initialize I/O on socket " + e.getStackTrace());
        }
    }

    public static void main(String[] args) throws IOException, MatlabConnectionException, MatlabInvocationException {

        ScreenCalibration t = new ScreenCalibration();
        t.initializeConnection(args[0]);
		
		int pos = fileName.lastIndexOf(".");
		if (pos > 0) {
			fileName = fileName.substring(0, pos);
		}
		
		measureDir = new File(fileName);
		if (!measureDir.exists()) {
		
			try{
				measureDir.mkdir();
			}catch(SecurityException se){
			
			}        
		
		}
		
		MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder().setMatlabLocation("C:/Program Files/MATLAB/R2017b/bin/matlab.exe").build();
		
		//Create a proxy, which we will use to control MATLAB
		MatlabProxyFactory factory = new MatlabProxyFactory(options);
		MatlabProxy proxy = factory.getProxy();
		
		int numColors = 1;
		
		Object[] res;
		Object argos;
		double in;
//IF NEEDED CHANGE PATH HERE------------------------------------------------------------------------------------------------------------------------------------
		proxy.eval("cd('Y:\\Spectroradiometer')");
		proxy.eval("addPathWithSubpathes");
//IF NEEDED CHANGE PORT HERE------------------------------------------------------------------------------------------------------------------------------------		
		proxy.eval("comPort = 'COM3';");
		proxy.eval("CS2000_initConnection( comPort );");
	
		whileloop:
		while((fromServer = t.in.readLine()) != null){
			
			System.out.println("Server: " + fromServer);
			
			if(fromServer.equals("READY")){
				fromUser = file.readLine();
				if(fromUser != null){
					System.out.println("Client: " + numColors + "-" + fromUser);
				
					t.out.println(fromUser);
					numColors++;
				
				} else{
					System.out.println("acabou no primeiro");
					break whileloop;
				}
				
			}
			
			
				
			//asks matlab for measure
			if(!fromServer.equals("READY") && !fromServer.equals("ENDED") ){
				proxy.eval("[message1, message2, cs2000Measurement, colorimetricNames] = CS2000_measure();");
				res = proxy.returningEval("cs2000Measurement.spectralData", 1);
				
					try {					
						
						writer = new PrintWriter(fileName + "/" + fromUser + ".txt", "UTF-8");
				
						for(int i=0;i<=400;i++){
							argos = res[0];
							in = ((double[]) argos)[i];
							writer.println(in);
							
						}
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					System.out.println("Matlab: Measured");
					/*try{
						Thread.sleep(2000);
					}catch (InterruptedException e) {
						e.printStackTrace();
					}*/
					
					fromUser = file.readLine();
				if(fromUser != null){
					System.out.println("Client: " + numColors + "-" + fromUser);
				
					t.out.println(fromUser);
					numColors++;
				
				} else{
					System.out.println("acabou no segundo");
					break;	
				}
			}
			
			if(fromServer.equals("ENDED")){
				proxy.eval("CS2000_terminateConnection();");
				proxy.disconnect();
				System.out.println("Matlab: DISCONNECTED");
				break;
			}
			
		}
		
    }
}