package org.ocpteam.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

public class ImageCreator {
	public static void main(String[] args) {
		int width = 100;
		int height = 100;

		// Create buffered image that does not support transparency
		BufferedImage bimage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D g = bimage.createGraphics();
		g.setPaint(Color.red);
		g.drawRect(2, 2, width - 4, height - 4);
		g.fill(new Rectangle(5, 5, width - 10, height - 10));

		g.setColor(Color.WHITE);
		Font font = new Font("Sherif", Font.PLAIN, 10);
		g.setFont(font);
		g.drawString("Coucou Didi", 0, 80);

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bimage, "png", baos);
			byte[] imageInByte = baos.toByteArray();
			File outputfile = new File("test.png");
			//ImageIO.write(bimage, "png", outputfile);
			FileOutputStream fos = new FileOutputStream(outputfile);
			fos.write(imageInByte);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Image toImage(BufferedImage bufferedImage) {
		return Toolkit.getDefaultToolkit().createImage(
				bufferedImage.getSource());
	}
}
