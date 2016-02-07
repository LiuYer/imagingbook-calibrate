package imagingbook.extras.calibration.zhang;

import imagingbook.extras.calibration.zhang.util.MathUtil;
import imagingbook.lib.math.Matrix;

import java.awt.geom.Point2D;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateDifferentiableSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;


/**
 * A camera model with parameters as specified in Zhang's paper.
 * @author WB
 *
 */
public class Camera {
	
	/**  
	 * <pre> 
	 * alpha  gamma  uc
	 *     0   beta  vc 
	 * </pre>
	 */
	private final double[][] A;	// 2 x 3

	private final double[] K;
		
	// for the standard Zhang camera
	public Camera(double alpha, double beta, double gamma, double uc, double vc, double k1, double k2) {
		this.A = makeA(alpha, beta, gamma, uc, vc);
		this.K = new double[] {k1, k2};
	}
	
	public Camera (double[] s) {
		//s = (alpha, beta, gamma, uc, vc, k1, k2);	
		this.A = makeA(s[0], s[1], s[2], s[3], s[4]);
		this.K = new double[] {s[5], s[6]};
	}
	
//	public Camera(double alpha, double beta, double gamma, double uc, double vc, double[] K) {
//		this.A = makeA(alpha, beta, gamma, uc, vc);
//		this.K = (K == null) ? new double[0] : K.clone();
//	}
	
	/**
	 * Create a standard camera. 
	 * @param A the (min) 2x3 matrix holding the intrinsic camera parameters.
	 * @param distort radial distortion coefficients k1, k2, ...
	 */
	public Camera(RealMatrix A, double[] K) {
		this.K = (K == null) ? new double[0] : K.clone();
		this.A = A.getSubMatrix(0, 1, 0, 2).getData();
	}
	
	/**
	 * Create a simple pinhole model (no distortion whatsoever).
	 * @param f the focal length (in pixel units).
	 * @param uc the x-position of the optical axis (in pixel units).
	 * @param vc the y-position of the optical axis (in pixel units).
	 */
	public Camera(double f, double uc, double vc) {
		this.K = new double[0];
		this.A = makeA(f, f, 0, uc, vc);	// TODO: need to be care about flipping the vertical coordinate??
	}
	
	// --------------------------------------------------------------------------
	
	private double[][] makeA(double alpha, double beta, double gamma, double uc, double vc) {
		return new double[][] {
				{alpha, gamma, uc},
				{    0,  beta, vc}};
	}
	
	// P is assumed to be a X/Y point in the Z = 0 plane
	public double[] project(ViewTransform view, Point2D P) {
		double[] XY0 = new double[] {P.getX(), P.getY(), 0}; 
		return project(view, XY0);
	}
	
	/**
	 * Utility method. Projects a set of 3D model points to the image of this
	 * camera for the given view.
	 * @param cam The camera.
	 * @param view The extrinsic view parameters.
	 * @param modelPoints 3D points on the Z = 0 plane. 
	 * @return The projected image points.
	 */
	public Point2D[] project(ViewTransform view, Point2D[] modelPoints) {
		Point2D[] imagePoints = new Point2D[modelPoints.length];
		for (int j = 0; j < modelPoints.length; j++) {
			double[] uv = project(view, modelPoints[j]);
			imagePoints[j] = MathUtil.toPoint2D(uv);
		}
		return imagePoints;
	}
	
	/**
	 * Projects the given 3D point onto the image plane of this camera
	 * for the provided extrinsic view parameters.
	 * @param view The extrinsic camera (view) parameters.
	 * @param XYZw A point in 3D world coordinates.
	 * @return The continuous 2D image coordinates of the projected point.
	 */
	public double[] project(ViewTransform view, double[] XYZw) {
		// does the radial distortion on the "ideal" projection (f = 1)
		// BEFORE applying the intrinsic camera transformation
		// this is the correct version!
		
		double[] xy = mapToIdealImage(view, XYZw);
		
		// apply radial lens distortion to the normalized projection
		double[] xyd = warp(xy);
		
		// apply intrinsic camera transformation:
		double[] uv = mapToSensorPlane(xyd);
		return uv;
	}
	
	public double[] mapToIdealImage(ViewTransform view, Point2D P) {
		double[] XY0 = new double[] {P.getX(), P.getY(), 0};
		return mapToIdealImage(view, XY0);
	}
	
	public double[] mapToIdealImage(ViewTransform view, double[] XYZw) {
		double[] XYZc = view.applyTo(XYZw);
		// Compute normalized projection coordinates (f = 1):
		final double x = XYZc[0] / XYZc[2];
		final double y = XYZc[1] / XYZc[2];
		return new double[] {x, y};
	}
	
	// not used in this form, just for symmetry
	private double fRad(double r) {
		return r * (1 + D(r));
	}
	
	// applies radial distortion in the normalized projection
	public double[] warp(double[] xy) {
		final double x = xy[0];
		final double y = xy[1];
		final double r = Math.sqrt(x * x + y * y);
		double d = (1 + D(r));
		return new double[] {d * x, d * y};
	}
	
	/**
	 * Inverse radial distortion function. Finds the original
	 * (undistorted) radius r from the distorted radius rr.
	 * Finds r as the root of the polynomial
	 * p(r) = - R + r + k1 * r^3 + k_2 * r^5,
	 * where R is constant.
	 * @param R The distorted radius.
	 * @return The undistorted radius.
	 */
	public double fRadInv(double R) {
		double k1 = K[0];
		double k2 = K[1];
		double[] coefficients = {-R, 1, 0, k1, 0, k2};
		PolynomialFunction p = new PolynomialFunction(coefficients);
		UnivariateDifferentiableSolver solver = new NewtonRaphsonSolver();
		double rInit = R;
		int maxEval = 20;
		double r = solver.solve(maxEval, p, rInit);
//		System.out.format("** solver iterations = %d\n", solver.getEvaluations());
		return r;
	}
	
	// applies radial distortion in the ideal image plane
	public double[] unwarp(double[] xyd) {
		final double xd = xyd[0];
		final double yd = xyd[1];
		final double rr = Math.sqrt(xd * xd + yd * yd);	// distorted radius
		final double r = fRadInv(rr);	// undistorted radius
//		System.out.format("undistort: rr=%.4f r=%.4f\n",rr, r);
		final double s = r / rr;
		return new double[] {s * xd, s * yd};
	}
	
	// radial distortion function: r' = r * (1 + D(r))
	public double D(double r) {
		final double k1 = (K.length > 0) ? K[0] : 0;
		final double k2 = (K.length > 1) ? K[1] : 0;
		final double r2 = r * r;
		return (k1 + k2 * r2) * r2;		// D(r) = k1 * r^2 + k2 * r^4
	}
	
	
	// xyd is the distorted 2D-point on the ideal projection plane
	public double[] mapToSensorPlane(double[] xyd) {
		final double x = xyd[0];
		final double y = xyd[1];
		final double u = A[0][0] * x + A[0][1] * y + A[0][2];
		final double v =               A[1][1] * y + A[1][2];
		return new double[] {u, v};	
	}
	
	
	// r in [0,1]
//	public static double getRadialDistortion(double r, double[] dCoeff) {
//		double r2 = r * r;
//		double d = 0;							// TODO: needs fixing!!
//		double rk = r2;
//		for (int k = 0; k < dCoeff.length; k++) {
//			d = d + dCoeff[k] * rk;
//			rk = rk * r2;
//		}
//		return d;
//	}
	// -------------------------------------------------------------------
	
	public double[] getParameterVector() {
		return new double[] 
				{getAlpha(), getBeta(),	getGamma(), getUc(), getVc(), K[0], K[1]};	
	}

	public double getAlpha() {
		return A[0][0];
	}

	public double getBeta() {
		return A[1][1];
	}

	public double getGamma() {
		return A[0][1];
	}

	public double getUc() {
		return A[0][2];
	}

	public double getVc() {
		return A[1][2];
	}

	public double[] getK() {
		return K;
	}
	
	public RealMatrix getA() {
		return MatrixUtils.createRealMatrix(A);
	}
	
//	/**
//	 * Returns the inverse of the camera intrinsic matrix A
//	 * as a 2x3 matrix (without the last row {0,0,1}).
//	 * This version uses numerical matrix inversion.
//	 * @return
//	 */
//	public RealMatrix getInverseA() {
//		RealMatrix Af = MatrixUtils.createRealMatrix(3, 3);
//		Af.setSubMatrix(A, 0, 0);
//		Af.setEntry(2, 2, 1);
//		return MatrixUtils.inverse(Af).getSubMatrix(0, 1, 0, 2);
//	}
	
	/**
	 * Returns the inverse of the camera intrinsic matrix A
	 * as a 3x3 matrix (without the last row {0,0,1}).
	 * This version uses closed form matrix inversion.
	 * Used for rectifying images (removing lens distortion).
	 * @return
	 */
	public RealMatrix getInverseA() {
		double alpha = A[0][0];
		double beta = A[1][1];
		double gamma = A[0][1];
		double uc = A[0][2];
		double vc = A[1][2];
		double[][] Ai = {
			{1.0/alpha, -gamma/(alpha*beta), (gamma*vc - beta*uc)/(alpha*beta)},
			{0,         1.0/beta,            -vc/beta}};
		return MatrixUtils.createRealMatrix(Ai);
	}
	
	public RealMatrix getHomography(ViewTransform view) {
		RealMatrix RT = view.getRotationMatrix();
		RealVector T = view.getTranslationVector();
		RT.setColumnVector(2, T);
		
		RealMatrix AM = MatrixUtils.createRealMatrix(3, 3); 
		AM.setSubMatrix(A, 0, 0);
		AM.setEntry(2, 2, 1);
		
		RealMatrix H = AM.multiply(RT);	
		return H.scalarMultiply(1.0 / H.getEntry(2, 2));
	}
	
	// -------------------------------------------------------------------
		
	public void print(String title) {
		System.out.format("%s: alpha = %.9f, beta = %.9f, gamma = %.9f, uc = %.9f, vc = %.9f\n",
				title, getAlpha(), getBeta(), getGamma(), getUc(), getVc());
		for (int i = 0; i < K.length; i++) {
			System.out.format("    k%d = %.9f\n", (i+1), K[i]);
		}
	}
	
	public String toString() {
		return String.format("alpha=%.4f, beta=%.4f, gamma=%.4f, uc=%.4f, vc=%.4f", 
				getAlpha(), getBeta(), getGamma(), getUc(), getVc());
	}
	
	//---------------------------------------------------------------------
	
	public static void main(String[] args) {
		ViewTransform view = new ViewTransform();
		
		System.out.println("Camera 1:");
		Camera camera1 = new Camera (
				832.5, 832.53, 0.204494, 	// alpha, beta, gamma, 
				303.959, 206.585,			// c_x, c_y
				-0.228601, 0.190353);		// k1, k2
		System.out.format("k1=%.4f, k2=%.4f\n", camera1.getK()[0], camera1.getK()[1]);
		double[] XYZ1 = {40, 70, 800};
		double[] uv1 = camera1.project(view, XYZ1);
		System.out.print(Matrix.toString(XYZ1) + " -> ");
		System.out.format("u=%.4f, u=%.4f\n", uv1[0], uv1[1]);
		
		double r = 0.95;
		double rr = camera1.fRad(r);
		System.out.format("radial distortion: r=%.4f -> rr=%.4f\n", r, rr);
		r = camera1.fRadInv(rr);
		System.out.format("inv. radial distortion: rr=%.4f -> r=%.4f\n", rr, r);
		
		System.out.println();
		
		// distorted camera
		System.out.println("Camera 2:");
		RealMatrix A = MatrixUtils.createRealMatrix(new double[][] {
				{832.5, 0.204494, 303.959},
				{  0.0, 832.53, 206.585},
				{  0.0,   0.0,     1.0}});
		Camera camera2 = new Camera(A, new double[] {-0.2, 0.190353});
		System.out.format("k1=%.4f, k2=%.4f\n", camera2.getK()[0], camera2.getK()[1]);
		
		double[] XYZ2 = {40, 70, 800};
		double[] uv2 = camera2.project(view, XYZ2);
		System.out.print(Matrix.toString(XYZ2) + " -> ");
		System.out.format("u=%.4f, u=%.4f\n", uv2[0], uv2[1]);
		
		r = 0.95;
		rr = camera2.fRad(r);
		System.out.format("radial distortion: r=%.4f -> rr=%.4f\n", r, rr);
		r = camera2.fRadInv(rr);
		System.out.format("inv. radial distortion: rr=%.4f -> r=%.4f\n", rr, r);
		
		System.out.println("\nTesting radial lens distortion:");
		double[] xy2 = {0.3, -0.7};
		System.out.format("original x=%.4f, y=%.4f\n", xy2[0], xy2[1]);
		double[] xy2d = camera2.warp(xy2);
		System.out.format("distorted x=%.4f, y=%.4f\n", xy2d[0], xy2d[1]);
		double[] xy2u = camera2.unwarp(xy2d);
		System.out.format("undistorted x=%.4f, y=%.4f\n", xy2u[0], xy2u[1]);
		
		System.out.println("\nTesting only radial lens distortion fun:");
		double ra = 0.10;
		double rb = camera2.fRad(ra);
		double rc = camera2.fRadInv(rb);
		System.out.format("ra=%.4f, rb=%.4f, rc=%.4f\n", ra, rb, rc);
	}
	

	

}


