package imagingbook.extras.calibration.zhang.testdata;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


/*
 * This class holds the coordinates of the standard planar calibration
 * model used by Zhangs. The original can be found here:
 * http://research.microsoft.com/en-us/um/people/zhang/Calib/Calibration/Model.txt
 * 
 * Each line describes the 2D (X, Y) coordinates of the corners of a planar square. 
 * The points are assumed to lie in the Z = 0 plane in 3D.
 * All units are inches!
 */

class ZhangStandardModel {
	
	static final double[][] data = {	
			{ 0, -0.5, 0.5, -0.5, 0.5, 0, 0, 0 },
			{ 0.888889, -0.5, 1.38889, -0.5, 1.38889, 0, 0.888889, 0 },
			{ 1.77778, -0.5, 2.27778, -0.5, 2.27778, 0, 1.77778, 0 },
			{ 2.66667, -0.5, 3.16667, -0.5, 3.16667, 0, 2.66667, 0 },
			{ 3.55556, -0.5, 4.05556, -0.5, 4.05556, 0, 3.55556, 0 },
			{ 4.44444, -0.5, 4.94444, -0.5, 4.94444, 0, 4.44444, 0 },
			{ 5.33333, -0.5, 5.83333, -0.5, 5.83333, 0, 5.33333, 0 },
			{ 6.22222, -0.5, 6.72222, -0.5, 6.72222, 0, 6.22222, 0 },
			{ 0, -1.38889, 0.5, -1.38889, 0.5, -0.888889, 0, -0.888889 },
			{ 0.888889, -1.38889, 1.38889, -1.38889, 1.38889, -0.888889, 0.888889, -0.888889 },
			{ 1.77778, -1.38889, 2.27778, -1.38889, 2.27778, -0.888889, 1.77778, -0.888889 },
			{ 2.66667, -1.38889, 3.16667, -1.38889, 3.16667, -0.888889, 2.66667, -0.888889 },
			{ 3.55556, -1.38889, 4.05556, -1.38889, 4.05556, -0.888889, 3.55556, -0.888889 },
			{ 4.44444, -1.38889, 4.94444, -1.38889, 4.94444, -0.888889, 4.44444, -0.888889 },
			{ 5.33333, -1.38889, 5.83333, -1.38889, 5.83333, -0.888889, 5.33333, -0.888889 },
			{ 6.22222, -1.38889, 6.72222, -1.38889, 6.72222, -0.888889, 6.22222, -0.888889 },
			{ 0, -2.27778, 0.5, -2.27778, 0.5, -1.77778, 0, -1.77778 },
			{ 0.888889, -2.27778, 1.38889, -2.27778, 1.38889, -1.77778, 0.888889, -1.77778 },
			{ 1.77778, -2.27778, 2.27778, -2.27778, 2.27778, -1.77778, 1.77778, -1.77778 },
			{ 2.66667, -2.27778, 3.16667, -2.27778, 3.16667, -1.77778, 2.66667, -1.77778 },
			{ 3.55556, -2.27778, 4.05556, -2.27778, 4.05556, -1.77778, 3.55556, -1.77778 },
			{ 4.44444, -2.27778, 4.94444, -2.27778, 4.94444, -1.77778, 4.44444, -1.77778 },
			{ 5.33333, -2.27778, 5.83333, -2.27778, 5.83333, -1.77778, 5.33333, -1.77778 },
			{ 6.22222, -2.27778, 6.72222, -2.27778, 6.72222, -1.77778, 6.22222, -1.77778 },
			{ 0, -3.16667, 0.5, -3.16667, 0.5, -2.66667, 0, -2.66667 },
			{ 0.888889, -3.16667, 1.38889, -3.16667, 1.38889, -2.66667, 0.888889, -2.66667 },
			{ 1.77778, -3.16667, 2.27778, -3.16667, 2.27778, -2.66667, 1.77778, -2.66667 },
			{ 2.66667, -3.16667, 3.16667, -3.16667, 3.16667, -2.66667, 2.66667, -2.66667 },
			{ 3.55556, -3.16667, 4.05556, -3.16667, 4.05556, -2.66667, 3.55556, -2.66667 },
			{ 4.44444, -3.16667, 4.94444, -3.16667, 4.94444, -2.66667, 4.44444, -2.66667 },
			{ 5.33333, -3.16667, 5.83333, -3.16667, 5.83333, -2.66667, 5.33333, -2.66667 },
			{ 6.22222, -3.16667, 6.72222, -3.16667, 6.72222, -2.66667, 6.22222, -2.66667 },
			{ 0, -4.05556, 0.5, -4.05556, 0.5, -3.55556, 0, -3.55556 },
			{ 0.888889, -4.05556, 1.38889, -4.05556, 1.38889, -3.55556, 0.888889, -3.55556 },
			{ 1.77778, -4.05556, 2.27778, -4.05556, 2.27778, -3.55556, 1.77778, -3.55556 },
			{ 2.66667, -4.05556, 3.16667, -4.05556, 3.16667, -3.55556, 2.66667, -3.55556 },
			{ 3.55556, -4.05556, 4.05556, -4.05556, 4.05556, -3.55556, 3.55556, -3.55556 },
			{ 4.44444, -4.05556, 4.94444, -4.05556, 4.94444, -3.55556, 4.44444, -3.55556 },
			{ 5.33333, -4.05556, 5.83333, -4.05556, 5.83333, -3.55556, 5.33333, -3.55556 },
			{ 6.22222, -4.05556, 6.72222, -4.05556, 6.72222, -3.55556, 6.22222, -3.55556 },
			{ 0, -4.94444, 0.5, -4.94444, 0.5, -4.44444, 0, -4.44444 },
			{ 0.888889, -4.94444, 1.38889, -4.94444, 1.38889, -4.44444, 0.888889, -4.44444 },
			{ 1.77778, -4.94444, 2.27778, -4.94444, 2.27778, -4.44444, 1.77778, -4.44444 },
			{ 2.66667, -4.94444, 3.16667, -4.94444, 3.16667, -4.44444, 2.66667, -4.44444 },
			{ 3.55556, -4.94444, 4.05556, -4.94444, 4.05556, -4.44444, 3.55556, -4.44444 },
			{ 4.44444, -4.94444, 4.94444, -4.94444, 4.94444, -4.44444, 4.44444, -4.44444 },
			{ 5.33333, -4.94444, 5.83333, -4.94444, 5.83333, -4.44444, 5.33333, -4.44444 },
			{ 6.22222, -4.94444, 6.72222, -4.94444, 6.72222, -4.44444, 6.22222, -4.44444 },
			{ 0, -5.83333, 0.5, -5.83333, 0.5, -5.33333, 0, -5.33333 },
			{ 0.888889, -5.83333, 1.38889, -5.83333, 1.38889, -5.33333, 0.888889, -5.33333 },
			{ 1.77778, -5.83333, 2.27778, -5.83333, 2.27778, -5.33333, 1.77778, -5.33333 },
			{ 2.66667, -5.83333, 3.16667, -5.83333, 3.16667, -5.33333, 2.66667, -5.33333 },
			{ 3.55556, -5.83333, 4.05556, -5.83333, 4.05556, -5.33333, 3.55556, -5.33333 },
			{ 4.44444, -5.83333, 4.94444, -5.83333, 4.94444, -5.33333, 4.44444, -5.33333 },
			{ 5.33333, -5.83333, 5.83333, -5.83333, 5.83333, -5.33333, 5.33333, -5.33333 },
			{ 6.22222, -5.83333, 6.72222, -5.83333, 6.72222, -5.33333, 6.22222, -5.33333 },
			{ 0, -6.72222, 0.5, -6.72222, 0.5, -6.22222, 0, -6.22222 },
			{ 0.888889, -6.72222, 1.38889, -6.72222, 1.38889, -6.22222, 0.888889, -6.22222 },
			{ 1.77778, -6.72222, 2.27778, -6.72222, 2.27778, -6.22222, 1.77778, -6.22222 },
			{ 2.66667, -6.72222, 3.16667, -6.72222, 3.16667, -6.22222, 2.66667, -6.22222 },
			{ 3.55556, -6.72222, 4.05556, -6.72222, 4.05556, -6.22222, 3.55556, -6.22222 },
			{ 4.44444, -6.72222, 4.94444, -6.72222, 4.94444, -6.22222, 4.44444, -6.22222 },
			{ 5.33333, -6.72222, 5.83333, -6.72222, 5.83333, -6.22222, 5.33333, -6.22222 },
			{ 6.22222, -6.72222, 6.72222, -6.72222, 6.72222, -6.22222, 6.22222, -6.22222 }
	};
	
	private ZhangStandardModel() {
	}

	private static Point2D[] makePointArray() {
		int n = data.length;
		List<Point2D> points = new ArrayList<Point2D>();
		for (int i = 0; i < n; i++) {
			double[] sq = data[i];
//			points.add(new Point2D.Double(sq[0], sq[1]));	// p0
//			points.add(new Point2D.Double(sq[2], sq[3]));	// p1
//			points.add(new Point2D.Double(sq[4], sq[5]));	// p2
//			points.add(new Point2D.Double(sq[6], sq[7]));	// p3

			for (int j = 0; j < sq.length; j += 2) {
				double x = sq[j];
				double y = sq[j + 1];
				points.add(new Point2D.Double(x, y));
			}
		}
		return points.toArray(new Point2D[points.size()]);
	}
	
	protected static Point2D[] getPoints() {
		return makePointArray();
	}
	
}