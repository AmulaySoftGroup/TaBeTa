package ir.amulay.tabeta.curl;

import android.graphics.PointF;

public class StackItem {

	// Variables for Bounded Rectangle For Each Step
	private double minX;
	private double minY;
	private double maxX;
	private double maxY;
	// Variables for Curl Information For Each Step
	private PointF curlPos;
	private PointF curlDir;
	// Variables For Animation Start And End Position
	private PointF mAnimationTarget;
	private PointF mAnimationSource;

	// Variable For Texture ID For Each Step
	private int TextureID;
	private int FBOId;

	/**
	 * Stack Item Constructor
	 * 
	 * @param minX
	 * @param minY
	 * @param maxX
	 * @param maxY
	 * @param curlPos
	 * @param curlDir
	 * @param AnimationSource
	 * @param AnimationTarget
	 * @param textureID
	 */
	public StackItem(double minX, double minY, double maxX, double maxY,
			PointF curlPos, PointF curlDir, PointF AnimationSource,
			PointF AnimationTarget, int textureID, int frameBuffer) {
		this.curlPos = new PointF();
		this.curlDir = new PointF();
		this.mAnimationSource = new PointF();
		this.mAnimationTarget = new PointF();
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		this.curlPos.set(curlPos);
		this.curlDir.set(curlDir);
		this.mAnimationSource.set(AnimationSource);
		this.mAnimationTarget.set(AnimationTarget);
		TextureID = textureID;
		FBOId = frameBuffer;
	}

	public StackItem(StackItem x) {
		curlPos = new PointF();
		curlDir = new PointF();
		mAnimationSource = new PointF();
		mAnimationTarget = new PointF();
		minX = x.getMinX();
		minY = x.getMinY();
		maxX = x.getMaxX();
		maxY = x.getMaxY();
		curlPos.set(x.getCurlPos());
		curlDir.set(x.getCurlDir());
		mAnimationSource.set(x.getAnimationSource());
		mAnimationTarget.set(x.getAnimationTarget());
		TextureID = x.getTextureID();
		FBOId = x.getFBOId();
	}

	public double getMinX() {
		return minX;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public double getMinY() {
		return minY;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}

	public double getMaxX() {
		return maxX;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public PointF getCurlPos() {
		return curlPos;
	}

	public void setCurlPos(PointF curlPos) {
		this.curlPos = curlPos;
	}

	public PointF getCurlDir() {
		return curlDir;
	}

	public void setCurlDir(PointF curlDir) {
		this.curlDir = curlDir;
	}

	public int getTextureID() {
		return TextureID;
	}

	public void setTextureID(int textureID) {
		TextureID = textureID;
	}

	public PointF getAnimationTarget() {
		return mAnimationTarget;
	}

	public void setAnimationTarget(PointF animationTarget) {
		mAnimationTarget = animationTarget;
	}

	public PointF getAnimationSource() {
		return mAnimationSource;
	}

	public void setAnimationSource(PointF animationSource) {
		mAnimationSource = animationSource;
	}

	public int getFBOId() {
		// TODO Auto-generated method stub
		return FBOId;
	}

}
