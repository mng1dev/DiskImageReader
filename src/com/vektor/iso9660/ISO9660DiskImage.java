package com.vektor.iso9660;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.vektor.util.Utilities;

public class ISO9660DiskImage {
	private String isopath;
	private ISO9660DiskImageFS rootFS;
	private RandomAccessFile raf;
	private static final int SECTORSIZE = 2048;

	public ISO9660DiskImage(String isopath) {
		this.isopath = isopath;
		readSector(16);
	}

	public void readSector(int nSector) {
		try {
			this.raf = new RandomAccessFile(this.isopath, "r");
			this.raf.seek(SECTORSIZE * nSector);
			byte[] sector = new byte[SECTORSIZE];
			while (this.raf.read(sector) > 0) {
				if ((isHeader(Arrays.copyOfRange(sector, 0, 6)))
						&& (isPrimaryVolumeDescriptor(sector[0]))) {
					break;
				}

			}

			System.gc();
			this.rootFS = createRootFolder(this.raf, sector);
		} catch (FileNotFoundException localFileNotFoundException) {
		} catch (IOException localIOException) {
		}
	}

	private ISO9660DiskImageFS createRootFolder(RandomAccessFile raf,
			byte[] data) {
		int startSector = Utilities.readLittleEndianWord(Arrays.copyOfRange(
				data, 158, 162));
		int size = Utilities.readLittleEndianWord(Arrays.copyOfRange(data, 166,
				170));
		return new ISO9660DiskImageFS(startSector, size, "", true, raf);
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
