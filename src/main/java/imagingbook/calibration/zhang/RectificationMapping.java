package imagingbook.calibration.zhang;

import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.calibration.zhang.util.MathUtil;
import imagingbook.pub.geometry.mappings.Mapping;


/** 
* <p>This class represents a special geometric mapping for
* rectifying (i.e., removing the lens distortion from)
* an image, given the associated camera parameters.
* The transformation maps any position {@code x'} in the rectified image
* to the corresponding position {@code x} in the original (distorted) 
* image.</p>
* 
* <p>Typically usage (by target-to-source-mapping):</p>
* 
* <pre>
* ImageProcessor original = ... ;  // the distorted image
* ImageProcessor rectified = ... ; // the (new) rectified image
* mapping.applyTo(original, rectified, InterpolationMethod.Bicubic);
* </pre>
*/
public class RectificationMapping extends Mapping {
	private final Camera cam;
	private final RealMatrix Ai;	// inverse of the intrinsic camera matrix (2 x 3)

	public RectificationMapping (Camera cam) {
		this.isInverseFlag = true;	// maps target -> source
		this.cam = cam;
		this.Ai = cam.getInverseA();
	}

	@Override
	public double[] applyTo(double[] uv) {
		// (u,v) is an observed sensor point
		// apply the inverse camera mapping to get the normalized (x,y) point:
		double[] xy = Ai.operate(MathUtil.toHomogeneous(uv));
		// apply the camera's radial lens distortion in the normalized plane:
		double[] xyd = cam.warp(xy);
		// apply the (forward) camera mapping to get the undistorted sensor point (u',v'):
		return cam.mapToSensorPlane(xyd);
	}
}
