package com.vektor.diskimagereader.common;

import java.util.HashMap;

public interface IDiskImage {
	
	public void readSector(int nSector);

	public boolean existsFile(String filePath);

	public byte[] getFile(String filePath);

	public void close();
	
	public HashMap<String, ? extends IDiskImageFS> getFiles();

}
