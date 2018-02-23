import matlabcontrol.*;

public class MatlabControl{
	public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException
	{
		MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder().setMatlabLocation("C:/Program Files/MATLAB/R2017b/bin/matlab.exe").build();
		
		//Create a proxy, which we will use to control MATLAB
		MatlabProxyFactory factory = new MatlabProxyFactory(options);
		MatlabProxy proxy = factory.getProxy();

		
		
		proxy.eval("cd('Y:\\Spectroradiometer')");
		proxy.eval("addPathWithSubpathes");
		proxy.eval("comPort = 'COM3';");
		proxy.eval("CS2000_initConnection( comPort );");
		proxy.eval("pause(4)");
		proxy.eval("[message1, message2, cs2000Measurement, colorimetricNames] = CS2000_measure();");
		proxy.eval("pause(4)");
		//proxy.eval("meas = cs2000Measurement.spectralData;");
		
		//Retrieve MATLAB's release date by providing the -date argument
		//Object[] result = proxy.returningEval("cs2000Measurement.spectralData", 1);
		proxy.eval("cs2000Measurement.spectralData");
		Object[] res;
		res = proxy.returningEval("cs2000Measurement.spectralData", 1);
		Object argos;
		double in;
		
		for(int i=0;i<400;i++){
			argos = res[0];
			in = ((double[]) argos)[i];
			System.out.println("Result " + i + ": " + in);
		}

		proxy.eval("CS2000_terminateConnection();");
		
		//Disconnect the proxy from MATLAB
		proxy.disconnect();
	}
}