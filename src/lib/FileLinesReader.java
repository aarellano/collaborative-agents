package lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileLinesReader {

	private String filePath;
	private FileReader fstream;
	private BufferedReader in;
	public boolean error = false;

	public FileLinesReader(String filePath)
	{
		try {
			this.filePath = filePath;
			fstream = new FileReader(filePath);
			in = new BufferedReader(fstream);
		} catch (IOException e) {
			e.printStackTrace();
			error = true;
		}
	}

	public String readLine(){
		String line = null;
		try {
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}

	public String[] readLineDelimited(String delim) {
		String line = readLine();
		return line.split(delim);
	}

	public boolean ready()
	{
		try {
			return in.ready();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getFolderPath()
	{
		String path = null;
		int index = getFilePath().length()-1;
		for(;index>0;index--)
		{
			if(getFilePath().charAt(index)==File.separatorChar)
			{
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
