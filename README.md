DiskImageReader
===============

##Disclaimer
The code is not optimized, it doesn't check image validity and doesn't parse the entire data. It just offers essential informations as it is a proof of concept.

##Description
A Java implementation of a reader for Disk Images complying the ISO9660 standard, working with images of 2048 and 2352 bytes per sector.

##Features
DiskImageReader creates a map representing a hierarchical File System and allows the user to extract files from the disk image.

##Usage
Sample class:



```java
public static void main(String [] args){
	String isopath = "/Users/alessandro/Downloads/Space Hulk.iso";
	ISO9660DiskImage f = new ISO9660DiskImage(isopath);
		File output = new File("/Users/alessandro/Documents/test.txt");
		FileOutputStream out;
		try {
			out = new FileOutputStream(output);
			out.write(f.getFile("SYSTEM.CNF"));
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {}
	}
```