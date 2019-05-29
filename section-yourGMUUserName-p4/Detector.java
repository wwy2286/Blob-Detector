import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.AbstractCollection;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.awt.geom.*;

import javax.swing.JPanel;

/**
 * 
 * @author William Yu Detector Class
 * @param <T> CS310 Fall 2018
 */
@SuppressWarnings("serial")
public class Detector extends JPanel {
	private final static int colorConvert = 195075;

	private final static Color black = new Color(0, 0, 0);
	private final static Color white = new Color(255, 255, 255);

	/**
	 * Task 2.1: get the difference between two colors (5%) Determines the distance
	 * between two colors as a value between 0 and 100.
	 * 
	 * @param c1 is the color of the first object
	 * @param c2 is the color of the second object
	 * @return the difference between the two colors
	 */
	public static int getDifference(Color c1, Color c2) {
		if (c1 == null || c2 == null) {
			throw new NullPointerException("Color arguments can not be null");
		}

		return (int) (((Math.pow(c1.getRed() - c2.getRed(), 2) + Math.pow(c1.getGreen() - c2.getGreen(), 2)
				+ Math.pow(c1.getBlue() - c2.getBlue(), 2)) * 100) / colorConvert);

	}

	/**
	 * Color the pixels white (if the pixel is not color we want) or black (if it's
	 * the color we want). okDist indicates the acceptable "distance" between the
	 * pixel and the color c (inclusive).
	 * 
	 * @param image  the image to be buffered
	 * @param c      the color to be thresshed
	 * @param okDist the distance that is acceptable between two colors
	 */
	public static void thresh(BufferedImage image, Color c, int okDist) {
		if (okDist < 0) {
			throw new IllegalArgumentException("Argument can not be negative");
		}
		Color imageColor;

		int width = image.getWidth();
		int height = image.getHeight();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				imageColor = new Color(image.getRGB(i, j));

				if (getDifference(imageColor, c) <= okDist) {

					image.setRGB(i, j, black.getRGB());
				} else {
					image.setRGB(i, j, white.getRGB());
				}
			}
		}

	}

	/**
	 * Given an image, a disjoint set, and a pixel (defined by its id), return a
	 * pair which contains (a) the blob above and (b) the blob to the left (each
	 * represented by their _root_ ids)
	 * 
	 * If there is no above/left neighbor, then the appropriate part of the pair
	 * should be null
	 * 
	 * @param image   image to be used
	 * @param ds      disjoint set
	 * @param pixelId id of the pixel
	 * @return the pair
	 */
	public static Pair<Integer, Integer> getNeighborSets(BufferedImage image, DisjointSets<Pixel> ds, int pixelId) {
		Integer above, left;

		if (image == null || ds == null || pixelId < 0) {
			throw new IllegalArgumentException("Illegal Argument");
		}

		if (pixelId < image.getWidth()) {
			above = null;
		} else {
			above = ds.find(pixelId - image.getWidth());
		}
		if (pixelId % image.getWidth() == 0) {
			left = null;
		} else {
			left = ds.find(pixelId - 1);
		}

		return new Pair<Integer, Integer>(above, left);

	}

	/**
	 * threshold the image create the DS data structure walk through and union
	 */
	public void detect() {

		ArrayList<Pixel> arr = new ArrayList<>();
		Pair<Integer, Integer> p;
		thresh(img, blobColor, okDist);

		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				arr.add(getPixel(img, getId(img, x, y)));
			}
		}
		ds = new DisjointSets<Pixel>(arr);

		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				p = getNeighborSets(img, ds, getId(img, x, y));
				if (p.b != null && getDifference(getColor(img, getPixel(img, p.b)),
						getColor(img, getPixel(img, getId(img, x, y)))) == 0) {
					ds.union(ds.find(getId(img, x, y)), ds.find(p.b));

				}
				if (p.a != null
						&& getDifference(getColor(img, getPixel(img, p.a)),
								getColor(img, getPixel(img, getId(img, x, y)))) == 0
						&& ds.find(getId(img, x, y)) != ds.find(p.a)) {
					ds.union(ds.find(getId(img, x, y)), ds.find(p.a));
				}
			}
		}

	}

	/**
	 * Recolor the kith largest blobs and saves output
	 * 
	 * @param outputFileName   the inputFile name
	 * @param outputECFileName the name of the output file
	 * @param k                is the number of the largest blobs to recolor
	 */
	public void outputResults(String outputFileName, String outputECFileName, int k) {
		if (k < 1) {
			throw new IllegalArgumentException(new String("! Error: k should be greater than 0, current k=" + k));
		}
		/**
		 * 
		 * @author Will Blob class that holds the index of the blob and the size of the
		 *         blob
		 */
		class Blob implements Comparable<Blob> {
			private int index;
			private int size;

			/**
			 * constructor of the blob
			 * 
			 * @param i is the index
			 * @param s is the size
			 */
			private Blob(int i, int s) {
				this.index = i;
				this.size = s;
			}

			/**
			 * compareTo method to sort the blob
			 */
			public int compareTo(Blob anotherBlob) throws ClassCastException {
				if (!(anotherBlob instanceof Blob)) {
					throw new ClassCastException("A blob object expecteed");
				}
				if (this.size > anotherBlob.size)
					return -1;
				if (this.size < anotherBlob.size)
					return 1;
				else
					return 0;
			}

		}
		// get all the roots from the DS
		ArrayList<Blob> blobSet = new ArrayList<Blob>();
		for (int i = 0; i < img.getWidth() * img.getHeight(); i++) {

			if (ds.find(i) == i && getDifference(getColor(img, getPixel(img, i)), black) == 0) {
				blobSet.add(new Blob(i, ds.get(i).size()));
			}

		}
		// using the roots, collect all sets of pixels and sort them by size
		Collections.sort(blobSet);
		// recolor the k-largest blobs from black to a color from getSeqColor()
		int count = blobSet.size();
		if (count <= k) {
			System.out.println("Number of blobs colored - " + count + ", Number of blobs detected - " + count);
			for (int i = 0; i < count; i++) {
				int y = blobSet.get(i).index;
				System.out.println("Blob " + (i + 1) + ": " + ds.get(y).size());
				for (Pixel t : ds.get(y)) {
					img.setRGB(t.a, t.b, getSeqColor(i, count).getRGB());
				}

			}
		} else {
			System.out.println("Number of blobs colored - " + k + ", Number of blobs detected - " + count);
			for (int i = 0; i < k; i++) {
				int y = blobSet.get(i).index;
				System.out.println("Blob " + (i + 1) + ": " + ds.get(y).size());
				for (Pixel t : ds.get(y)) {
					img.setRGB(t.a, t.b, getSeqColor(i, k).getRGB());
				}

			}
		}

		// and output all blobs to console
		try {
			File ouptut = new File(outputFileName);
			ImageIO.write(this.img, "png", ouptut);
			System.err.println("- Saved result to " + outputFileName);
		} catch (Exception e) {
			System.err.println("! Error: Failed to save image to " + outputFileName);
		}

		Set<Pixel> ec = ds.get(blobSet.get(0).index);
		int top = img.getHeight();
		int bottom = 0;
		int left = img.getWidth();
		int right = 0;
		for (Pixel t : ec) {
			if (t.b <= top) {
				top = t.b;
			}
			if (t.b >= bottom) {
				bottom = t.b;
			}
			if (t.a <= left) {
				left = t.a;
			}
			if (t.a >= right) {
				right = t.a;
			}

		}
		reloadImage();

		Graphics2D g2 = img.createGraphics();
		g2.setColor(black);
		g2.draw(new Rectangle2D.Double(left - 2, top - 2, right - left + 4, bottom - top + 4));
		g2.draw(new Rectangle2D.Double(left - 1, top - 1, right - left + 2, bottom - top + 2));

		/*
		 * //if you're doing the EC and your output image is still this.img, //you can
		 * uncomment this to save this.img to the specified outputECFileName
		 */
		try {
			File ouptut = new File(outputECFileName);
			ImageIO.write(this.img, "png", ouptut);
			System.err.println("- Saved result to " + outputECFileName);
		} catch (Exception e) {
			System.err.println("! Error: Failed to save image to " + outputECFileName);
		}

	}

	// main method just for your testing
	// edit as much as you want
	public static void main(String[] args) {

		// Some stuff to get you started...

		File imageFile = new File("./input/04_Circles.png");
		BufferedImage img = null;
		Color test = new Color(80, 165, 155);

		try {
			img = ImageIO.read(imageFile);
		} catch (IOException e) {
			System.err.println("! Error: Failed to read " + imageFile + ", error msg: " + e);
			return;
		}

		Pixel p = getPixel(img, 110); // 100x100 pixel image, pixel id 110
		System.out.println(p.a); // x = 10
		System.out.println(p.b); // y = 1
		System.out.println(getId(img, p)); // gets the id back (110)
		System.out.println(getId(img, p.a, p.b)); // gets the id back (110)
		System.out.println("__________");
		Detector d = new Detector("./input/10_13-06-28-robocup-eindhoven-025.jpg", test, 1);
		// d.thresh(img, blue, 5);
		d.detect();
		d.outputResults("blah.png", "blahec.png", 1);

	}

	// -----------------------------------------------------------------------
	//
	// Todo: Read and provide comments, but do not change the following code
	//
	// -----------------------------------------------------------------------

	// Data
	public BufferedImage img; // this is the 2D array of RGB pixels
	private Color blobColor; // the color of the blob we are detecting
	private String imgFileName; // input image file name
	private DisjointSets<Pixel> ds; // the disjoint set
	private int okDist; // the distance between blobColor and the pixel which
						// "still counts" as the
						// color

	/**
	 * Constructor
	 * 
	 * @param imgfile   image to be red
	 * @param blobColor color of the blob we're looking for
	 * @param okDist    acceptable distance
	 */
	public Detector(String imgfile, Color blobColor, int okDist) {
		this.imgFileName = imgfile;
		this.blobColor = blobColor;
		this.okDist = okDist;

		reloadImage();
	}

	/**
	 * constructor, read image from file
	 */
	public void reloadImage() {
		File imageFile = new File(this.imgFileName);

		try {
			this.img = ImageIO.read(imageFile);
		} catch (IOException e) {
			System.err.println("! Error: Failed to read " + this.imgFileName + ", error msg: " + e);
			return;
		}
	}

	/**
	 * JPanel function
	 */
	public void paint(Graphics g) {
		g.drawImage(this.img, 0, 0, this);
	}

	/**
	 * 
	 * @author Will Convenient helper class representing a pair of things
	 * @param <A> is the first pair
	 * @param <B> is the second pair
	 */
	private static class Pair<A, B> {
		A a;
		B b;

		public Pair(A a, B b) {
			this.a = a;
			this.b = b;
		}
	}

	/**
	 * 
	 * @author Will A pixel is a set of locations a (aka. x, distance from the left)
	 *         and b (aka. y, distance from the top)
	 */
	private static class Pixel extends Pair<Integer, Integer> {
		public Pixel(int x, int y) {
			super(x, y);
		}
	}

	/**
	 * Convert a pixel in an image to its ID
	 * 
	 * @param image is the image being red
	 * @param p     is the pixel argument
	 * @return the ide of the pixel
	 */
	private static int getId(BufferedImage image, Pixel p) {
		return getId(image, p.a, p.b);
	}

	/**
	 * Convert ID to Pixel
	 * 
	 * @param image being read
	 * @param id    of the pixel
	 * @return the pixel
	 */
	private static Pixel getPixel(BufferedImage image, int id) {
		int y = id / image.getWidth();
		int x = id - (image.getWidth() * y);

		if (y < 0 || y >= image.getHeight() || x < 0 || x >= image.getWidth())
			throw new ArrayIndexOutOfBoundsException();

		return new Pixel(x, y);
	}

	/**
	 * 
	 * @param image being red
	 * @param x     is the x coordinate
	 * @param y     is the y coordinate
	 * @return the id
	 */
	private static int getId(BufferedImage image, int x, int y) {
		return (image.getWidth() * y) + x;
	}

	/**
	 * 
	 * @param image being red
	 * @param p     is the pixel
	 * @return the color of the pixel
	 */
	private static Color getColor(BufferedImage image, Pixel p) {
		return new Color(image.getRGB(p.a, p.b));
	}

	/**
	 * Pass 0 -> k-1 as i to get the color for the blobs 0 -> k-1
	 * 
	 * @param i   is the k-1 amount of blobs to be recolored
	 * @param max is the max amount of blobs
	 * @return
	 */
	private Color getSeqColor(int i, int max) {
		if (i < 0)
			i = 0;
		if (i >= max)
			i = max - 1;

		int r = (int) (((max - i + 1) / (double) (max + 1)) * blobColor.getRed());
		int g = (int) (((max - i + 1) / (double) (max + 1)) * blobColor.getGreen());
		int b = (int) (((max - i + 1) / (double) (max + 1)) * blobColor.getBlue());

		if (r == 0 && g == 0 && b == 0) {
			r = g = b = 10;
		} else if (r == 255 && g == 255 && b == 255) {
			r = g = b = 245;
		}

		return new Color(r, g, b);
	}
}
