package com.cwidanage.photoftp.photocorrupt.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;


public class DetectPhotoCorrupt {
    
    
    public static final int THRESHOLD = 50;

    private File jpegFile;
    private boolean isJPEG = false;
    private boolean isCorrupt = false;
    private boolean isFileComplete = false;
    private String hexDump = "";


    public DetectPhotoCorrupt(File jpegFile) throws IOException {
        this(jpegFile, false);
    }


    public DetectPhotoCorrupt(File jpegFile, boolean ignoreExtension) throws IOException {
        // File must not be a directory
        if (jpegFile.isDirectory())
            throw new IOException("File " + jpegFile.getCanonicalPath() + " is a directory!");

        if (!ignoreExtension) {
            if (jpegFile.getName().contains(".")) {
                String ext = jpegFile.getName().substring(jpegFile.getName().lastIndexOf("."));

                if (!(ext.equalsIgnoreCase(".jpeg") || ext.equalsIgnoreCase(".jpg")))
                    throw new IOException("Not a jpeg extension");
            }
        }

        if (!jpegFile.exists())
            throw new FileNotFoundException("File " + jpegFile.getCanonicalPath() + " is not found!");

        this.jpegFile = jpegFile;

        initialize();
    }
   
    private void initialize() throws IOException {
        setIsJpeg();
        setIsFileComplete();
        setIsCorrupt();
    }

    private void setIsJpeg() throws IOException {
        byte[] buffer = new byte[20];

        try (RandomAccessFile file = new RandomAccessFile(this.jpegFile, "r")) {
            if (file.length() > 20)
                file.read(buffer, 0, 20);
            else
                file.read(buffer, 0, (int) file.length());
        }
        this.isJPEG = matchBytes(buffer, DetectPhotoInterface.START_OF_IMAGE);
    }

    private void setIsFileComplete() throws IOException {
        byte[] buffer = new byte[DetectPhotoInterface.END_OF_IMAGE.length];
        try (RandomAccessFile file = new RandomAccessFile(this.jpegFile, "r")) {
            if (file.length() > DetectPhotoInterface.END_OF_IMAGE.length) {
                // Set the file pointer to the last value position minus the length of endBits
                file.seek((int) file.length() - DetectPhotoInterface.END_OF_IMAGE.length);
                file.read(buffer, 0, DetectPhotoInterface.END_OF_IMAGE.length);
            } else
                file.read(buffer, 0, (int) file.length());
        }
        this.isFileComplete = matchEndBytes(buffer, DetectPhotoInterface.END_OF_IMAGE);
    }
    
    private void setIsCorrupt() throws IOException {
		// If file is not complete then it is considered automatically corrupt
		if (!this.isFileComplete) {
			this.isCorrupt = true;
			return;
		}

		try (FileChannel fc = FileChannel.open(jpegFile.toPath(), StandardOpenOption.READ)) {
			ByteBuffer bBuffer = ByteBuffer.allocate(THRESHOLD);

			if (fc.size() > (THRESHOLD + DetectPhotoInterface.END_OF_IMAGE.length)) {
				fc.read(bBuffer, fc.size() - (THRESHOLD + DetectPhotoInterface.END_OF_IMAGE.length));
			}

			// Concatenate string
			StringJoiner joiner = new StringJoiner("");
			for (byte b : bBuffer.array()) {
				joiner.add(Integer.toHexString(b));
			}
			String fullStringRep = joiner.toString().toUpperCase();

			int first = bBuffer.array()[0];
			StringBuilder stringPattern = new StringBuilder();
			for (int i = 1; i < bBuffer.array().length - DetectPhotoInterface.END_OF_IMAGE.length; i++) {
				stringPattern.append(Integer.toHexString(bBuffer.get(i)).toUpperCase());
				if (first == bBuffer.get(i)) {
					break;
				}
			}

			Pattern pattern = Pattern.compile(stringPattern.toString());
			Matcher matcher = pattern.matcher(fullStringRep);

			int matchCount = 0;
			while (matcher.find()) {
				matchCount++;
			}

			if (matchCount > 2) {
				isCorrupt = true;
			}
		}
	}
    
  
    
    private boolean matchBytes(byte[] buffer, byte[] comp) {
        for (int i = 0; i < comp.length; i++) {
            if (buffer[i] != comp[i])
                return false;
        }
        return true;
    }

    private boolean matchEndBytes(byte[] buffer, byte[] comp) {
        for (int i = 1; i < comp.length; i++) {
            if (buffer[buffer.length - i] != comp[comp.length - i])
                return false;
        }
        return true;
    }

    private String implodeString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < b.length - DetectPhotoInterface.END_OF_IMAGE.length; i++) {
            sb.append(Integer.toHexString(b[i]));
        }
        return sb.toString().toUpperCase();
    }

    public String getHexDump() {
        StringBuilder sb = new StringBuilder();
        try (RandomAccessFile file = new RandomAccessFile(jpegFile, "r")) {
            int i = 0;

            for (int pos = 0; pos < file.length(); pos++) {
                String hex = Integer.toHexString(file.read()).toUpperCase();
                sb.append(hex.length() == 1 ? ("0" + hex) : hex).append(" ");
                i++;
                if (i == 8)
                    sb.append("  ");

                if (i == 16) {
                    i = 0;
                    sb.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!hexDump.equals(sb.toString()))
            hexDump = sb.toString();

        return hexDump;
    }

    public boolean isJPEG() {
        return isJPEG;
    }

    public boolean isFileComplete() {
        return isFileComplete;
    }

    public boolean isCorrupt() {
        return isCorrupt;
    }
    
}