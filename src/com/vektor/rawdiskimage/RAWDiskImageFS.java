package com.vektor.rawdiskimage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;

import com.vektor.util.Utilities;

public class RAWDiskImageFS {
	private static final int SECTORSIZE = 2352;
	private boolean isDir;
	private RandomAccessFile raf;
	int startSector;
	int size;
	String name;
	private HashMap<String, RAWDiskImageFS> files;

	public RAWDiskImageFS(int startSector, int size, String name,
			boolean isDir, RandomAccessFile raf) {
		this.startSector = startSector;
		this.isDir = isDir;
		this.size = size;
		this.name = name;
		this.raf = raf;

		if (isDir)
			seekFiles();
	}

	public HashMap<String, RAWDiskImageFS> getFiles() {
		if (this.isDir) {
			return this.files;
		}
		return null;
	}

	public void addFile(int startSector, int size, String name, boolean isDir) {
		if ((this.files == null) && (isDir))
			this.files = new HashMap<String, RAWDiskImageFS>();
		this.files.put(name, new RAWDiskImageFS(startSector, size, name, isDir,
				this.raf));
	}

	public void seekFiles() {
		try {
			byte[] data = new byte[this.size];
			this.raf.seek(SECTORSIZE * this.startSector + 24);
			this.raf.read(data);
			int count = 0;
			for (int index = 0; index < data.length; index++) {
				int offset = data[index];
				if (offset == 0)
					break;
				if (count > 1) {
					parseFile(Arrays.copyOfRange(data, index, index + offset));
					index += offset - 1;
				} else {
					count++;
					index += offset - 1;
				}
			}
		} catch (IOException localIOException) {
		}
	}

	private void parseFile(byte[] data) {
		StringBuilder sb = new StringBuilder();
		String flags = String.format(
				"%8s",
				new Object[] { Integer.toBinaryString(Utilities
						.UValue(data[25])) }).replace(' ', '0');
		boolean dir = false;
		if (flags.charAt(6) == '1')
			dir = true;
		int length = Utilities.UValue(data[32]);
		for (int i = 33; i < 33 + length; i++) {
			sb.append((char) data[i]);
		}
		if ((sb.toString().length() == 1) && (sb.charAt(0) == 0))
			return;
		String nm = dir ? sb.toString() : sb.toString().substring(0,
				sb.toString().length() - 2);

		int ss = Utilities.readLittleEndianWord(Arrays.copyOfRange(data, 2, 6));
		int sz = Utilities.readLittleEndianWord(Arrays
				.copyOfRange(data, 10, 14));
		if (this.files == null)
			this.files = new HashMap<String, RAWDiskImageFS>();
		this.files.put(nm, new RAWDiskImageFS(ss, sz, nm, dir, this.raf));
	}

	public boolean existsFile(String filePath) {
		if (this.files == null)
			return false;
		if ((this.files.containsKey(filePath))
				&& (!((RAWDiskImageFS) this.files.get(filePath)).isDir))
			return true;
		if (filePath.contains(System.getProperty("file.separator"))) {
			String folder = filePath.substring(0,
					filePath.indexOf(System.getProperty("file.separator")));
			if (this.files.containsKey(folder)) {
				String file = filePath.substring(1 + filePath.indexOf(System
						.getProperty("file.separator")));
				return ((RAWDiskImageFS) this.files.get(folder))
						.existsFile(file);
			}
		}
		return false;
	}

	public byte[] getFile(String filePath) {
		if ((this.files != null) && (this.files.containsKey(filePath))
				&& (!((RAWDiskImageFS) this.files.get(filePath)).isDir)) {
			try {
				System.out.println("RAWDiskImageFS - CWD: "
						+ (this.name.isEmpty() ? "/" : this.name) + " Found:"
						+ filePath);
				byte[] fileData = new byte[((RAWDiskImageFS) this.files
						.get(filePath)).size];
				System.out
						.println("Size "
								+ ((RAWDiskImageFS) this.files.get(filePath)).size
								+ " SS: "
								+ ((RAWDiskImageFS) this.files.get(filePath)).startSector);
				this.raf.seek(((RAWDiskImageFS) this.files.get(filePath)).startSector
						* SECTORSIZE + 24);
				this.raf.read(fileData);

				return fileData;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (filePath.contains(System.getProperty("file.separator"))) {
			String folder = filePath.substring(0,
					filePath.indexOf(System.getProperty("file.separator")));
			if (this.files.containsKey(folder)) {
				String file = filePath.substring(1 + filePath.indexOf(System
						.getProperty("file.separator")));
				return ((RAWDiskImageFS) this.files.get(folder)).getFile(file);
			}
		}
		return new byte[0];
	}
}
