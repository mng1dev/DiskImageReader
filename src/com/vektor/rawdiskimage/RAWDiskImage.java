package com.vektor.rawdiskimage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.vektor.util.Utilities;

public class RAWDiskImage {
	private String isopath;
	private RAWDiskImageFS rootFS;
	private RandomAccessFile raf;
	private static final int SECTORSIZE = 2352;

	public RAWDiskImage(String isopath) {
		this.isopath = isopath;
		readSector(16);
	}

	public void readSector(int nSector) {
		try {
			this.raf = new RandomAccessFile(this.isopath, "r");
			this.raf.seek(SECTORSIZE * nSector);
			System.out.println(this.isopath + " has " + this.raf.length()
					/ SECTORSIZE + " sectors.");
			byte[] sector = new byte[SECTORSIZE];
			while (this.raf.read(sector) > 0) {
				if ((isHeader(Arrays.copyOfRange(sector, 24, 30)))
						&& (isPrimaryVolumeDescriptor(sector[24]))) {
					break;
				}
			}
			byte[] usefulData = Arrays.copyOfRange(sector, 24, 2072);
			sector = null;
			System.gc();
			this.rootFS = createRootFolder(this.raf, usefulData);
		} catch (FileNotFoundException localFileNotFoundException) {
		} catch (IOException localIOException) {
		}
	}

	private RAWDiskImageFS createRootFolder(RandomAccessFile raf, byte[] data) {
		int startSector = Utilities.readLittleEndianWord(Arrays.copyOfRange(
				data, 158, 162));
		int size = Utilities.readLittleEndianWord(Arrays.copyOfRange(data, 166,
				170));
		return new RAWDiskImageFS(startSector, size, "", true, raf);
	}

	private boolean isHeader(byte[] header) {
		return (header[1] == 67) && (header[2] == 68) && (header[3] == 48)
				&& (header[4] == 48) && (header[5] == 49);
	}

	private boolean isPrimaryVolumeDescriptor(byte b) {
		return 1 == Utilities.UValue(b);
	}

	public boolean existsFile(String filePath) {
		return this.rootFS.existsFile(filePath);
	}

	public byte[] getFile(String filePath) {
		return this.rootFS.getFile(filePath);
	}

	public void close() {
		try {
			this.raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
