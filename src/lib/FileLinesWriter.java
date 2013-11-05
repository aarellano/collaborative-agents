package lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLinesWriter {

	private String filePath;
	private FileWriter fstream;
	private BufferedWriter out;
	
	public FileLinesWriter(String filePath, boolean append)
	{
		try {
			this.filePath = filePath;
			File file = new File(filePath);
			if(file.getParent() != null)
				file.getParentFile().mkdirs();
			fstream = new FileWriter(file, append);
			out = new BufferedWriter(fstream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void writeLine(String line) 
	{
		boolean retry = false;
		int trials = 1;
		do {
			try {
				out.write(line);
				out.newLine();
			} catch (IOException e) {
				if(trials > 5) {
					retry = false;
					e.printStackTrace();
				} else {
					retry = true;
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			} finally {
				trials ++;
			}
		}while(retry);
	}

	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getFolderPath()
	{
		String path = null;
		int index = getFilePath().length()-1;
		for(;index>0;index--) {
			if(getFilePath().charAt(index)==File.separatorChar) {
				break;
			}
		}
		if(index == 0)path = "";
		else path = getFilePath().substring(0,index)+"";
		return path;
	}

	public String getFilePath() {
		return filePath;
	}
	
	
}
