package imagingbook.extras.calibration.zhang;

import java.awt.geom.Point2D;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;



public class RadialDistortionEstimator {
	
	protected double[] estimateLensDistortion(Camera cam, ViewTransform[] views, Point2D[] modelPts, Point2D[][] obsPts) {
		final int M = views.length;
		final int N = modelPts.length;

		final double uc = cam.getUc();
		final double vc = cam.getVc();

		RealMatrix D = MatrixUtils.createRealMatrix(2 * M * N, 2);
		RealVector d = new ArrayRealVector(2 * M * N);

		int l = 0;
		for (int i = 0; i < M; i++) {
			Point2D[] obs = obsPts[i];

			for (int j = 0; j < N; j++) {
				// determine the radius in the ideal image plane
				double[] xy = cam.mapToIdealImage(views[i], modelPts[j]);
				double x = xy[0];
				double y = xy[1];
				double r2 = x * x + y * y;
				double r4 = r2 * r2;
				
				// project model point to image
				double[] uv = cam.project(views[i], modelPts[j]);
				double u = uv[0];
				double v = uv[1];
				double du = u - uc;	// distance to estim. projection center
				double dv = v - vc;
				
				D.setEntry(l * 2 + 0, 0, du * r2);
				D.setEntry(l * 2 + 0, 1, du * r4);
				D.setEntry(l * 2 + 1, 0, dv * r2);
				D.setEntry(l * 2 + 1, 1, dv * r4);
				
				// observed image point
				Point2D UV = obs[j];
				double U = UV.getX();
				double V = UV.getY();
				
				d.setEntry(l * 2 + 0, U - u);
				d.setEntry(l * 2 + 1, V - v);
				l++;
			}
		}
		
		DecompositionSolver solver = new SingularValueDecomposition(D).getSolver();
		RealVector k = solver.solve(d);
		
		double err1 = D.operate(new ArrayRealVector(new double[] {0,0})).subtract(d).getNorm();
		double err2 = D.operate(k).subtract(d).getNorm();
		System.out.format("err1=%.2f, err2=%.2f \n", err1, err2);
		
		return k.toArray();
	}

}