package net.softel.ai.classify.preprocess;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.*;

import javax.imageio.ImageIO;

public class GrayScale {

   
   
   public static void convertToGrayScale(String directory, String imageName) {

       System.out.println("\nConverting " + directory + imageName + " to gray scale");

        BufferedImage  image;
        int width;
        int height;

      try {
         File input = new File(directory + imageName);
         image = ImageIO.read(input);
         width = image.getWidth();
         height = image.getHeight();
         
         for(int i=0; i< height; i++) {

            System.out.print(".");

            for(int j=0; j< width; j++) {

               Color c = new Color(image.getRGB(j, i));
               int red = (int)(c.getRed() * 0.299);
               int green = (int)(c.getGreen() * 0.587);
               int blue = (int)(c.getBlue() *0.114);
               Color newColor = new Color(red+green+blue, red+green+blue, red+green+blue);
               
               image.setRGB(j,i,newColor.getRGB());
            }
         }
         
         File ouptut = new File(directory + "grayscale_" + imageName);
         ImageIO.write(image, "jpg", ouptut);
         
         System.out.println("\nDONE !!!");
         } 
      catch (Exception e) {
         System.out.println("Unable to convert image " + imageName + ": " + e.getMessage());
         }
   }
   
//    static public void main(String args[]) throws Exception {
//       GrayScale obj = new GrayScale();
//    }
}