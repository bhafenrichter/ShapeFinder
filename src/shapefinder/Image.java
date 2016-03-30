/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefinder;

/**
 *
 * @author Brandon
 */
public class Image {

    int[][] image;
    int width;
    int height;
    int[][] centroid;
    public Image() {

    }

    public Image(int[][] image, int width, int height) {
        this.image = image;
        this.width = width;
        this.height = height;
    }
}
