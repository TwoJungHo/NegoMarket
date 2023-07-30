package kr.co.tj.imgfileservice.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

public class ImageResize {
	
	public static byte[] getResizedImageData(MultipartFile imageFile, int maxWidth, int maxHeight, double quality) {
		
		InputStream fileStream = null;
	    ByteArrayOutputStream outputStream = null;
	    byte[] imageData = null;
	   
		try {
			
			fileStream = imageFile.getInputStream();
			outputStream = new ByteArrayOutputStream();
			
			Thumbnails.of(fileStream)
			.size(maxWidth, maxHeight)
			.outputQuality(quality)
			.toOutputStream(outputStream);

			imageData = outputStream.toByteArray();
			
						
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		return imageData;
	}

}
