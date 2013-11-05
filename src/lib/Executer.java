package lib;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 
 * @author Mohammad Khaled
 *
 */
public class Executer {
	public static double execute(String cmd, boolean waitFor)
	{
		String response = "";
		double duration = 0;
		try {
            Runtime rt = Runtime.getRuntime();
            long start = System.currentTimeMillis();
            Process pr = rt.exec(cmd);
            if(!waitFor) return 0.0;
            int exitVal = pr.waitFor();
            long end = System.currentTimeMillis();
            duration = end-start;
            
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line=null;
            while((line=input.readLine()) != null) {
                System.out.println(line);
                response += line;
            }
            //System.out.println("Exited with error code "+exitVal);
            if(exitVal != 0)
            	System.out.println("Exited with Error!!...error code "+exitVal);

        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return duration;
	}

	public static void RunMatlap(String mFile, Object[] paramList) {
		String params = "";
		for(int i = 0; i < paramList.length; i++) {
			if( i != 0) params += ",";
			if (paramList[i] instanceof String) {
				params +=  "'"+paramList[i]+"'";
			} else params +=  paramList[i];
		}
		System.out.println(params);
		execute("matlab -r "+mFile+"("+params+")", false);
	}
}
