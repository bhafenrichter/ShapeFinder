/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefinder;

//Program:      EasyImageDisplayDriver.jsl
//Description:  General purpose program for testing/demonstrating EasyImageDisplay class.
//				Reads an image file stored in a special format (converted from a .BMP
//				file by the program convertBMP.exe).
//Author:       Steve Donaldson
//Revised:      4/2/09

import java.io.*;
public class EasyImageDisplayDriver {

	//*****************************************************************************************
	public static void main(String[] args) {
		int imageType = 0, width = 0, height = 0, row, column, start;
		int red[][] = null, green[][] = null, blue[][] = null, gray[][] = null;
		char c1,c2,c3,c4;
		KeyboardInputClass keyboardInput = new KeyboardInputClass();
		String userInput;
		String fileName;
		EasyImageDisplay sampleDisplayObject;

		System.out.println("Image Display Routine");
		System.out.println("---------------------\n");
		//fileName=keyboardInput.getKeyboardInput("Specify the name of the file containing the image data: ");
		fileName = "lpg2";
                if (fileName.length()>0) {
			try {
				RandomAccessFile imageFile=new RandomAccessFile(fileName,"r");
				start=0;
				imageFile.seek(start);					//move pointer to beginning of file
				//Read the file type, rows, and columns (stored as integers). This requires reading
				//four bytes per value. These bytes represent an integer stored by C++ or Basic
				//(i.e., in low byte to high byte order (not reversed bit order!)). The routine
				//converts to a Java integer representation (i.e., high byte to low byte order).
				c1=(char)imageFile.read();
				c2=(char)imageFile.read();
				c3=(char)imageFile.read();
				c4=(char)imageFile.read();
				imageType=(c4 << 24) | (c3 << 16) | (c2 << 8) | c1;
				if ((imageType!=1)&&(imageType!=2)&&(imageType!=3)) {
					userInput=keyboardInput.getKeyboardInput("Bad file type. Press ENTER to continue...");
					System.exit(0);
				}
				c1=(char)imageFile.read();
				c2=(char)imageFile.read();
				c3=(char)imageFile.read();
				c4=(char)imageFile.read();
				width=(c4 << 24) | (c3 << 16) | (c2 << 8) | c1;
				c1=(char)imageFile.read();
				c2=(char)imageFile.read();
				c3=(char)imageFile.read();
				c4=(char)imageFile.read();
				height=(c4 << 24) | (c3 << 16) | (c2 << 8) | c1;
				//set up color or grayscale array(s)
				if (imageType == 1) {
					red = new int[height][width];
					green = new int[height][width];
					blue = new int[height][width];
				}
				else
					gray = new int[height][width];

				for(row=0;row<height;row++) {
					for(column=0;column<width;column++) {
						if (imageType==1) {			//color
							blue[row][column] = (char)imageFile.read();
							green[row][column] = (char)imageFile.read();
							red[row][column] = (char)imageFile.read();
						}
						else if (imageType==2)		//grayscale
							gray[row][column]=(char)imageFile.read();
					}
				}
				imageFile.close();
			}
			catch (Exception e) {
				userInput=keyboardInput.getKeyboardInput("Error trying to read file. Press ENTER to continue...");
				System.exit(0);
			}

			sampleDisplayObject = new EasyImageDisplay(imageType, width, height, red, green, blue, gray);
			sampleDisplayObject.showImage("Image Display Routine", true);
			userInput = keyboardInput.getKeyboardInput("Press ENTER to exit...");
			sampleDisplayObject.closeImageDisplay();
		}
		System.exit(0);
	}
	//****************************************************************************************
}	//end ImageDisplayDriver
//*********************************************************************************************
//*********************************************************************************************