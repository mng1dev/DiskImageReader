package com.vektor.diskimagereader.common;

import java.util.HashMap;

public interface IDiskImageFS {

	
	public HashMap<String,? extends IDiskImageFS> getFiles();

	public void addFile(int startSector, int size, String name, boolean isDir);

	public void seekFiles();
	public boolean existsFile(String filePath);

	public byte[] getFile(String filePath);
	
	public boolean isDir();
	public int getStartSector();
	public int getSize();
	public String getName();
}
