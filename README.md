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
	String isopath = "/Users/user/Downloads/DiskImage.iso";
	String isopath2 = "/Users/user/Downloads/DiskImage.bin"; //or .img
	
	ISO9660DiskImage f = new ISO9660DiskImage(isopath);
		File output = new File("/Users/user/Documents/test.txt");
		FileOutputStream out;
		try {
			out = new FileOutputStream(output);
			out.write(f.getFile("/path/to/file.ext"));
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {}
	RAWDiskImage g = new RAWDiskImage(isopath2);
		File output2 = new File("/Users/user/Documents/test2.txt");
		FileOutputStream out2;
		try {
			out2 = new FileOutputStream(output2);
			out2.write(f.getFile("/path/to/file.ext"));
			out2.flush();
			out2.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {}
}
```