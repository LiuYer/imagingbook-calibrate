package imagingbook.calibration.zhang;

import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.calibration.zhang.util.MathUtil;
import imagingbook.pub.geometry.mappings.Mapping;


/** 
 * This class represents the 2D geometric transformation for an image taken 
 * with some camera A to an image taken with another camera B.
 * 
 * @author W. Burger
 * @version 2016-06-01
 */
public class InterCameraMapping extends Mapping {
	
	private final Camera camA, camB;
	private final RealMatrix Abi;	// inverse of the intrinsic camera b matrix (2 x 3)

	public InterCameraMapping (Camera camA, Camera camB) {
		this.isInverseFlag = true;	// maps target -> source
		this.camA = camA;		// camera A (used to produce the source image)
		this.camB = camB;		// camera B (determines the geometry the target image)
		this.Abi = camB.getInverseA();
	}
	
	@Override
	public double[] applyTo(double[] uv) {
		// (u,v) is an observed sensor point
		// apply the inverse camera mapping to get the distorted (x,y) point:
		double[] xy = Abi.operate(MathUtil.toHomogeneous(uv));
		
		// remove the lens distortion of camera b:
		double[] xyu = camB.unwarp(xy);
		
		// apply the lens distortion of camera a:
		double[] xyd = camA.warp(xyu);
		
		// apply the (forward) camera mapping to get the undistorted sensor point (u',v'):
		return camA.mapToSensorPlane(xyd);
	}

}
