package ir.amulay.tabeta.curl;

import ir.amulay.tabeta.database.InternalDB;
import ir.amulay.tabeta.globals.Constants;
import ir.amulay.tabeta.gpc.Point2D;
import ir.amulay.tabeta.gpc.Poly;
import ir.amulay.tabeta.gpc.PolyDefault;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
/**
 * Class implementing actual curl/page rendering.
 * 
 * @author Alireza.Pir WWW.Andvoid.IR
 */
public class CurlMesh {

	public int Score = 0;
	// Flag for rendering some lines used for developing. Shows
	// curl position and one for the direction from the
	// position given. Comes handy once playing around with different
	// ways for following pointer.
	private static final boolean DRAW_CURL_POSITION = false;
 
	private static boolean DRAW_DEST_POLY = false;
	// Flag for drawing polygon outlines. Using this flag crashes on emulator
	// due to reason unknown to me. Leaving it here anyway as seeing polygon
	// outlines gives good insight how original rectangle is divided.
	private static final boolean DRAW_POLYGON_OUTLINES_BACK = false;
	private static final boolean DRAW_POLYGON_OUTLINES_FRONT = true;

	
	private boolean DRAW_TEXTURE = false;

	// Let's avoid using 'new' as much as possible. Meaning we introduce arrays
	// once here and reuse them on runtime. Doesn't really have very much effect
	// but avoids some garbage collections from happening.

	private Array<Vertex> mArrIntersections;// 2 ta rAs baraye tadakholaat
											// darnazar gereftim
	private Array<Vertex> mArrOutputVertices;
	private Array<Vertex> mArrRotatedVertices;
	private Array<Double> mArrScanLines;
	private Array<Vertex> mArrTempVertices;

	// Buffers for feeding rasterizer.
	private FloatBuffer mBufColors;// baraye zakhire sazie ranghaaye khorooji
									// baraye chap shodane nahaaE
	private FloatBuffer mBufCurlPositionLines;// baraye zakhire sazie
												// CurlPosition ke bayad chap
												// beshe dar khorooji
	private FloatBuffer mBufDestVertices;
	private FloatBuffer mBufTexCoords;
	private FloatBuffer mBufVertices;// Baraye zakhire sazie rAs haaE ke bayad
										// dar khorooji chap beshe. harchi in
										// too bere eynan dar khorooji chap
										// mishe
	private FloatBuffer mBufOutBack;
	private FloatBuffer mBufOutFront;

	// in baraye ijaade oon khat haaye neshoon dahandeye makan va jahate Curl
	// shodan hast, hamoon 3 ta khati ke ghermezan
	private int mCurlPositionLinesCount;

	// Bounding rectangle for this mesh. mRectagle[0] = top-left corner,
	// mRectangle[1] = bottom-left, mRectangle[2] = top-right and mRectangle[3]
	// bottom-right.
	private List<Vertex> mVertices = new ArrayList<Vertex>();

	private final CurlPage mPage = new CurlPage();
	private int mOutBackCount;
	private int mOutFrontCount;
	private int mVerticesCountBack;
	private int mVerticesCountFront;
	private double width;
	private double height;
	private double minX;
	private double minY;
	private double maxX;
	private double maxY;
	private double minX2;
	private double minY2;
	private double maxX2;
	private double maxY2;
	int[] renderTex = null;
	
	

	/*
	 * Variables For Implementing FORSCORE functionality
	 */
	final List<Point2D> mFinalShape = new ArrayList<Point2D>();
	List<Point2D> mRotatedShape = new ArrayList<Point2D>();
	List<Point2D> Optimize = new ArrayList<Point2D>();
	PolyDefault mDesPoly = new PolyDefault();
	Stack<PolyDefault> mTempPolyStack = new Stack<PolyDefault>();
	Stack<PolyDefault> mFinalPolyStack = new Stack<PolyDefault>();
	PolyDefault mFinalPolygon = new PolyDefault();
	PolyDefault MCopyPolygon = new PolyDefault();
	Poly LeftCheck;// for Checkig the Mineus Part of paper
	Poly RightCheck; // For Checkong the Possitive part of paper

	// Flag For mAnimateBack To Dont Calculate Final shape If It is Curling Back
	private Boolean mAnimateBack = false;

	
	/**
	 * Constructor for mesh object.
	 * 
	 * @param maxCurlSplits
	 *            Maximum number curl can be divided into. The bigger the value
	 *            the smoother curl will be. With the cost of having more
	 *            polygons for drawing.
	 */

	public CurlMesh() {

		LeftCheck = new PolyDefault();
		LeftCheck.add(new Point2D(0, -4));
		LeftCheck.add(new Point2D(0, 4));
		LeftCheck.add(new Point2D(-4, 4));
		LeftCheck.add(new Point2D(-4, -4));

		RightCheck = new PolyDefault();
		RightCheck.add(new Point2D(0, -4));
		RightCheck.add(new Point2D(0, 4));
		RightCheck.add(new Point2D(4, 4));
		RightCheck.add(new Point2D(4, -4));

		if (DRAW_POLYGON_OUTLINES_BACK) {
			ByteBuffer tbb = ByteBuffer.allocateDirect(8 * 2 * 4);
			tbb.order(ByteOrder.nativeOrder());
			mBufOutBack = tbb.asFloatBuffer();
			mBufOutBack.position(0);
			tbb.clear();
		}
		if (DRAW_POLYGON_OUTLINES_FRONT) {
			ByteBuffer tbb1 = ByteBuffer.allocateDirect(12 * 2 * 4);
			tbb1.order(ByteOrder.nativeOrder());
			mBufOutFront = tbb1.asFloatBuffer();
			mBufOutFront.position(0);
			tbb1.clear();
		}
		mArrScanLines = new Array<Double>(2);

		// @ TODO taghEEr ijaad shode:
		mArrOutputVertices = new Array<Vertex>(7);
		// baa farze inke kaaghaz mitoone 8 bar taa bokhore,
		// hadeAksar 12 ta rAs khahim dasht tooye bakhshe taa nakhorde
		// banabarEn in balaE ro comment kardam va in paEni ro jash gozashtam
		// mArrOutputVertices = new Array<Vertex>(12);

		// @ TODO maa 8 baar mitoonim taa bezanim hadeAksar kaghaz ro,
		// pas 8 ta hadeAskar az in araayehaaye mArrRoated... mikhaim
		// hala in taa khordegi haa mitoone harkodoom hadeAksar 12 ta rAs dashte
		// bashe
		// (check kardam, shayad bishtar az in ham beshe)
		// pas maa 8 ta azin arayehaaye 12 taE mikhaim, vase hamin
		// in paEni ro comment mikonam va 8 ta 12 taE ba esmaye mokhtalef
		// ijaad...
		mArrRotatedVertices = new Array<Vertex>(4);
		// mArrRotatedVertices = new Array<Vertex>(12);

		// hadeAksar 8 ta rAs khahad dasht shekle nahaaE

		mArrIntersections = new Array<Vertex>(2);
		mArrTempVertices = new Array<Vertex>(7 + 4);
		for (int i = 0; i < 7 + 4; ++i) {
			mArrTempVertices.add(new Vertex());
		}

		// Rectangle consists of 4 vertices. Index 0 = top-left, index 1 =
		// bottom-left, index 2 = top-right and index 3 = bottom-right.
		//
		for (int i = 0; i < 4; ++i) {
			// mRectangle[i] = new Vertex();
			mVertices.add(new Vertex());
		}

		if (DRAW_CURL_POSITION) {
			mCurlPositionLinesCount = 3;
			ByteBuffer hvbb = ByteBuffer
					.allocateDirect(mCurlPositionLinesCount * 2 * 2 * 4);
			hvbb.order(ByteOrder.nativeOrder());
			mBufCurlPositionLines = hvbb.asFloatBuffer();
			mBufCurlPositionLines.position(0);
			hvbb.clear();
		}

		//
		// mBufCurlPositionLines.position(0);
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(-1.0f);
		// mBufCurlPositionLines.put(-1.0f);
		// mBufCurlPositionLines.put(-1.0f);
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(-1.0f);
		//
		// mBufCurlPositionLines.put(0.25f);
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(0.25f);
		// mBufCurlPositionLines.put(-1.0f);
		//
		// mBufCurlPositionLines.put(0.5f);
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(0.5f);
		// mBufCurlPositionLines.put(-1.0f);
		//
		// mBufCurlPositionLines.put(0.75f);
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(0.75f);
		// mBufCurlPositionLines.put(-1.0f);
		//
		//
		// mBufCurlPositionLines.put(-0.25f);
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(-0.25f);
		// mBufCurlPositionLines.put(-1.0f);
		//
		// mBufCurlPositionLines.put(-0.5f);
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(-0.5f);
		// mBufCurlPositionLines.put(-1.0f);
		//
		// mBufCurlPositionLines.put(-0.75f);
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(-0.75f);
		// mBufCurlPositionLines.put(-1.0f);
		//
		//
		//
		//
		//
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(0.25f);
		// mBufCurlPositionLines.put(-1.0f);
		// mBufCurlPositionLines.put(0.25f);
		//
		//
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(0.5f);
		// mBufCurlPositionLines.put(-1.0f);
		// mBufCurlPositionLines.put(0.5f);
		//
		//
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(0.75f);
		// mBufCurlPositionLines.put(-1.0f);
		// mBufCurlPositionLines.put(0.75f);
		//
		//
		//
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(-0.25f);
		// mBufCurlPositionLines.put(-1.0f);
		// mBufCurlPositionLines.put(-0.25f);
		//
		//
		//
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(-0.5f);
		// mBufCurlPositionLines.put(-1.0f);
		// mBufCurlPositionLines.put(-0.5f);
		//
		//
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(-0.75f);
		// mBufCurlPositionLines.put(-1.0f);
		// mBufCurlPositionLines.put(-0.75f);
		//
		//
		// mBufCurlPositionLines.put(0.0f);
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(0.0f);
		// mBufCurlPositionLines.put(-1.0f);
		//
		//
		// mBufCurlPositionLines.put(1.0f);
		// mBufCurlPositionLines.put(0.0f);
		// mBufCurlPositionLines.put(-1.0f);
		// mBufCurlPositionLines.put(0.0f);
		//
		// mBufCurlPositionLines.position(0);
		//
		// }

		// There are 4 vertices from bounding rect, max 2 from adding split line
		// to two corners and curl consists of max mMaxCurlSplits lines each
		// outputting 2 vertices.
		int maxVerticesCount = 4 + 2 + 2;
		ByteBuffer vbb = ByteBuffer.allocateDirect(maxVerticesCount * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		mBufVertices = vbb.asFloatBuffer();
		mBufVertices.position(0);

		renderTex = new int[1];
		if (DRAW_TEXTURE) {
			ByteBuffer tbb = ByteBuffer
					.allocateDirect(maxVerticesCount * 2 * 4);
			tbb.order(ByteOrder.nativeOrder());
			mBufTexCoords = tbb.asFloatBuffer();
			mBufTexCoords.position(0);
		}

		ByteBuffer cbb = ByteBuffer.allocateDirect(maxVerticesCount * 4 * 4);
		cbb.order(ByteOrder.nativeOrder());
		mBufColors = cbb.asFloatBuffer();
		mBufColors.position(0);

		/**
		 * Section For Allocating First Values Of Variable
		 */
		mFinalShape.add(new Point2D(-1, 1));
		mFinalShape.add(new Point2D(1, 1));
		mFinalShape.add(new Point2D(1, -1));
		mFinalShape.add(new Point2D(-1, -1));

		if (DRAW_POLYGON_OUTLINES_BACK) {
			mOutBackCount = 0;
			mBufOutBack.position(0);
			for (int i = 0; i < mFinalShape.size(); i++) {
				mOutBackCount++;
				mBufOutBack.put((float) mFinalShape.get(i).getX());
				mBufOutBack.put((float) mFinalShape.get(i).getY());
			}
			mBufOutBack.position(0);
		}
		if (DRAW_POLYGON_OUTLINES_FRONT) {
			mOutFrontCount = 0;
			mBufOutFront.position(0);
		}

	}

	public void SetAnimState(Boolean State) {
		mAnimateBack = State;
	}
	
	/**
	 * For Clearing The memory Used
	 */
	public void ClearMemory(){
		mBufColors.clear();
		//mBufCurlPositionLines.clear();
		mBufDestVertices.clear();
		if(DRAW_TEXTURE)
		mBufTexCoords.clear();
		mBufVertices.clear();
		//mBufOutBack.clear();
		mBufOutFront.clear();
	}

	/**
	 * Adds vertex to buffers. //@ TODO baraaye zakhire kardane rAs haaE ke dar
	 * nahayat bayad Draw beshan
	 */
	private void addVertex(Vertex vertex) {
		mBufVertices.put((float) vertex.mPosX);
		mBufVertices.put((float) vertex.mPosY);
		mBufVertices.put((float) vertex.mPosZ);
		mBufColors.put(vertex.mColorFactor * Color.red(vertex.mColor) / 255f);
		mBufColors.put(vertex.mColorFactor * Color.green(vertex.mColor) / 255f);
		mBufColors.put(vertex.mColorFactor * Color.blue(vertex.mColor) / 255f);
		mBufColors.put(Color.alpha(vertex.mColor) / 255f);
		if (DRAW_TEXTURE) {
			mBufTexCoords.put((float) vertex.mTexX);
			mBufTexCoords.put((float) vertex.mTexY);
		}
	}

	public void SetDesPoly(InternalDB db, int x) {
		db.open();
		int n = db.GetPointsCount(x);
		Log.e("Count = ", " " + n);
		//Log.e("NumPoints : ", "" + n);
		mDesPoly.clear();
		for (int i = 0; i < n; i++) {
			mDesPoly.add(new Point2D(db.GetPointX(x, i), db.GetPointY(x, i)));
		}
		db.close();
		ByteBuffer hvbb = ByteBuffer.allocateDirect(n * 3 * 2 * 4);
		hvbb.order(ByteOrder.nativeOrder());
		mBufDestVertices = hvbb.asFloatBuffer();
		mBufDestVertices.position(0);
		for (int i = 0; i < mDesPoly.getNumPoints(); i++) {
			mBufDestVertices.put((float) mDesPoly.getX(i));
			mBufDestVertices.put((float) mDesPoly.getY(i));
			// Log.e(i +"X And Y = ", "" + OpenglPoly.getX(i) + " AND " +
			// OpenglPoly.getY(i));
		}
		mBufDestVertices.position(0);
		DRAW_DEST_POLY = true;
	}

	boolean first = true;

	public void SetNewPaper(StackItem item, int texture) {
		if (item == null) {
			PolyDefault a = new PolyDefault();
			if (!MCopyPolygon.isEmpty()) {
				mFinalShape.clear();
				for (int i = 0; i < MCopyPolygon.getNumPoints(); i++) {
					Point2D tmp = new Point2D(MCopyPolygon.getX(i),
							MCopyPolygon.getY(i));
					// Log.e("Vertices " + i, "" + MCopyPolygon.getX(i)+","+
					// MCopyPolygon.getY(i));
					mFinalShape.add(tmp);
					a.add(tmp);
				}
			}
			mFinalPolyStack.push(a);
			// for(int i = 0 ;i<mFinalPolyStack.size(); i++){
			// PolyDefault tmp = new PolyDefault();
			// tmp = mFinalPolyStack.get(i);
			// for(int j = 0; j<tmp.getNumPoints();j++){
			// Log.e(i +" Stack Item " + j, "" + tmp.getX(j) + " AND " +
			// tmp.getY(j));
			// }
			// }
			mVertices.get(0).mPosX = minX;
			mVertices.get(0).mPosY = maxY;
			mVertices.get(1).mPosX = minX;
			mVertices.get(1).mPosY = minY;
			mVertices.get(2).mPosX = maxX;
			mVertices.get(2).mPosY = maxY;
			mVertices.get(3).mPosX = maxX;
			mVertices.get(3).mPosY = minY;
			DRAW_TEXTURE = true;
			int maxVerticesCount = 4 + 2 + (2);
			if (DRAW_TEXTURE && first) {
				// Log.e("ALLOCATINGDIRECT", "ALLOCATINGDIRECT");
				first = false;
				ByteBuffer tbb = ByteBuffer
						.allocateDirect(maxVerticesCount * 2 * 4);
				tbb.order(ByteOrder.nativeOrder());
				mBufTexCoords = tbb.asFloatBuffer();
				mBufTexCoords.position(0);
			}
			renderTex = new int[1];
			renderTex[0] = texture;
			/**
			 * @ TODO int ro bayad bargardoonam ta dorost she, ama nemikham
			 * fElan bargardoonam mikham bebinam moshkel chie ke be in niaze
			 */
			// mPage.setColor(Color.WHITE, 3);
			reset();
		}

		else {
			PolyDefault tmp = new PolyDefault();
			if (!mFinalPolyStack.isEmpty()) {
				mFinalPolyStack.pop();
				mFinalShape.clear();
			}
			if (!mFinalPolyStack.isEmpty()) {

				tmp = mFinalPolyStack.lastElement();
			} else {
				//Log.e("MfinalPolyStack", "MfinalPolyStackEMPTY");
			}

			for (int i = 0; i < tmp.getNumPoints(); i++) {
				Point2D ptmp = new Point2D(tmp.getX(i), tmp.getY(i));
				mFinalShape.add(ptmp);
			}
			if (DRAW_POLYGON_OUTLINES_BACK) {
				mOutBackCount = 0;
				mBufOutBack.position(0);
				for (int i = 0; i < mFinalShape.size(); i++) {
					mOutBackCount++;
					mBufOutBack.put((float) mFinalShape.get(i).getX());
					mBufOutBack.put((float) mFinalShape.get(i).getY());
				}
				mBufOutBack.position(0);
			}
			if(DRAW_POLYGON_OUTLINES_FRONT){
			mOutFrontCount = 0;
			}
			mVertices.get(0).mPosX = item.getMinX();
			mVertices.get(0).mPosY = item.getMaxY();
			mVertices.get(1).mPosX = item.getMinX();
			mVertices.get(1).mPosY = item.getMinY();
			mVertices.get(2).mPosX = item.getMaxX();
			mVertices.get(2).mPosY = item.getMaxY();
			mVertices.get(3).mPosX = item.getMaxX();
			mVertices.get(3).mPosY = item.getMinY();
			DRAW_TEXTURE = true;
			renderTex[0] = item.getTextureID();
			reset();
		}

	}

	/**
	 * Sets curl for this mesh.
	 * 
	 * @param curlPos
	 *            Position for curl 'center'. Can be any point on line collinear
	 *            to curl.
	 * @param curlDir
	 *            Curl direction, should be normalized.
	 * @param radius
	 *            Radius of curl. darvaaghe Zavieye makhroot
	 */
	public synchronized void curl(PointF curlPos, PointF curlDir, double radius) {
		// Log.e("Curling", "Curling");
		// Log.e("mFinalPolySatckSize = ", "" + mFinalPolyStack.size());
		// harbaar TmRectangle Ghabli ro khaali kon va jadide ro toosh beriz
		// TmRectangle.clear();
		// //Log.d("taaCount", ""+TaaCount+"");
		// //Log.d("mArrTempVertices.size()", ""+mArrTempVertices.size()+"");
		// First add some 'helper' lines used for development.
		
		//

		
		if (DRAW_CURL_POSITION) {
			mBufCurlPositionLines.position(0);
			mBufCurlPositionLines.put(curlPos.x);
			mBufCurlPositionLines.put(curlPos.y - 1.0f);
			mBufCurlPositionLines.put(curlPos.x);
			mBufCurlPositionLines.put(curlPos.y + 1.0f);
			mBufCurlPositionLines.put(curlPos.x - 1.0f);
			mBufCurlPositionLines.put(curlPos.y);
			mBufCurlPositionLines.put(curlPos.x + 1.0f);
			mBufCurlPositionLines.put(curlPos.y);
			mBufCurlPositionLines.put(curlPos.x);
			mBufCurlPositionLines.put(curlPos.y);
			mBufCurlPositionLines.put(curlPos.x + curlDir.x * 2);
			mBufCurlPositionLines.put(curlPos.y + curlDir.y * 2);
			mBufCurlPositionLines.position(0);
		}

		// Actual 'curl' implementation starts here.
		mBufVertices.position(0);
		mBufColors.position(0);
		if (DRAW_TEXTURE) {
			mBufTexCoords.position(0);
		}

		// Calculate curl angle from direction.
		// //Log.d("XDir", ""+curlDir.x+"");
		double curlAngle = Math.acos(curlDir.x);
		// //Log.d("curlAngle", ""+curlAngle+"");
		curlAngle = curlDir.y > 0 ? -curlAngle : curlAngle;
		// Initiate rotated rectangle which's is translated to curlPos and
		// rotated so that curl direction heads to right (1,0). Vertices are
		// ordered in ascending order based on x -coordinate at the same time.
		// And using y -coordinate in very rare case in which two vertices have
		// same x -coordinate.

		if (!mAnimateBack) {
			CalculateShape(curlPos, curlAngle);
		}

		mArrTempVertices.addAll(mArrRotatedVertices);
		mArrRotatedVertices.clear();

		for (int i = 0; i < mVertices.size(); ++i) {
			Vertex v = mArrTempVertices.remove(0);
			v.set(mVertices.get(i));
			v.translate(-curlPos.x, -curlPos.y);
			v.rotateZ(-curlAngle);
			int j = 0;
			for (; j < mArrRotatedVertices.size(); ++j) {
				Vertex v2 = mArrRotatedVertices.get(j);
				if (v.mPosX > v2.mPosX) {
					break;
				}
				if (v.mPosX == v2.mPosX && v.mPosY > v2.mPosY) {
					break;
				}
			}
			mArrRotatedVertices.add(j, v);
		}

		// Rotated rectangle lines/vertex indices. We need to find bounding
		// lines for rotated rectangle. After sorting vertices according to
		// their x -coordinate we don't have to worry about vertices at indices
		// 0 and 1. But due to inaccuracy it's possible vertex 3 is not the
		// opposing corner from vertex 0. So we are calculating distance from
		// vertex 0 to vertices 2 and 3 - and altering line indices if needed.
		// Also vertices/lines are given in an order first one has x -coordinate
		// at least the latter one. This property is used in getIntersections to
		// see if there is an intersection.
		int lines[][] = { { 0, 1 }, { 0, 2 }, { 1, 3 }, { 2, 3 } };
		{
			// TODO: There really has to be more 'easier' way of doing this -
			// not including extensive use of sqrt.
			Vertex v0 = mArrRotatedVertices.get(0);
			Vertex v2 = mArrRotatedVertices.get(2);
			Vertex v3 = mArrRotatedVertices.get(3);
			double dist2 = Math.sqrt((v0.mPosX - v2.mPosX)
					* (v0.mPosX - v2.mPosX) + (v0.mPosY - v2.mPosY)
					* (v0.mPosY - v2.mPosY));
			double dist3 = Math.sqrt((v0.mPosX - v3.mPosX)
					* (v0.mPosX - v3.mPosX) + (v0.mPosY - v3.mPosY)
					* (v0.mPosY - v3.mPosY));
			if (dist2 > dist3) {
				lines[1][1] = 3;
				lines[2][1] = 2;
			}
		}

		mVerticesCountFront = mVerticesCountBack = 0;
		// Length of 'curl' curve.
		// double curlLength = Math.PI * radius;
		// Calculate scan lines.
		// TODO: Revisit this code one day. There is room for optimization here.
		mArrScanLines.clear();
		mArrScanLines.add((double) 0);
		/*
		 * @ TODO azoonja ke MaxCurlSplit tooye kaare man 1e, niaazi be in
		 * halgheye for nist
		 */
		// As mRotatedVertices is ordered regarding x -coordinate, adding
		// this scan line produces scan area picking up vertices which are
		// rotated completely. One could say 'until infinity'.
		mArrScanLines.add(mArrRotatedVertices.get(3).mPosX - 1);
		// Start from right most vertex. Pretty much the same as first scan area
		// is starting from 'infinity'.
		double scanXmax = mArrRotatedVertices.get(0).mPosX + 1;
		for (int i = 0; i < mArrScanLines.size(); ++i) {

			// Once we have scanXmin and scanXmax we have a scan area to start
			// working with.
			double scanXmin = mArrScanLines.get(i);
			// First iterate 'original' rectangle vertices within scan area.
			for (int j = 0; j < mArrRotatedVertices.size(); ++j) {
				Vertex v = mArrRotatedVertices.get(j);
				// Test if vertex lies within this scan area.
				// TODO: Frankly speaking, can't remember why equality check was
				// added to both ends. Guessing it was somehow related to case
				// where radius=0f, which, given current implementation, could
				// be handled much more effectively anyway.
				if (v.mPosX >= scanXmin && v.mPosX <= scanXmax) {
					// Pop out a vertex from temp vertices.
					Vertex n = mArrTempVertices.remove(0);
					n.set(v);
					// This is done solely for triangulation reasons. Given a
					// rotated rectangle it has max 2 vertices having
					// intersection.
					Array<Vertex> intersections = getIntersections(
							mArrRotatedVertices, lines, n.mPosX);
					// In a sense one could say we're adding vertices always in
					// two, positioned at the ends of intersecting line. And for
					// triangulation to work properly they are added based on y
					// -coordinate. And this if-else is doing it for us.
					if (intersections.size() == 1
							&& intersections.get(0).mPosY > v.mPosY) {
						// In case intersecting vertex is higher add it first.
						mArrOutputVertices.addAll(intersections);
						mArrOutputVertices.add(n);

					} else if (intersections.size() <= 1) {
						// Otherwise add original vertex first.
						mArrOutputVertices.add(n);

						mArrOutputVertices.addAll(intersections);
					} else {
						// There should never be more than 1 intersecting
						// vertex. But if it happens as a fallback simply skip
						// everything.
						mArrTempVertices.add(n);
						mArrTempVertices.addAll(intersections);
					}
				}
			}

			// Search for scan line intersections.
			Array<Vertex> intersections = getIntersections(mArrRotatedVertices,
					lines, scanXmin);

			// We expect to get 0 or 2 vertices. In rare cases there's only one
			// but in general given a scan line intersecting rectangle there
			// should be 2 intersecting vertices.
			if (intersections.size() == 2) {

				// There were two intersections, add them based on y
				// -coordinate, higher first, lower last.
				Vertex v1 = intersections.get(0);
				Vertex v2 = intersections.get(1);
				if (v1.mPosY < v2.mPosY) {
					mArrOutputVertices.add(v2);
					mArrOutputVertices.add(v1);
				} else {
					mArrOutputVertices.addAll(intersections);
				}
			} else if (intersections.size() != 0) {
				// This happens in a case in which there is a original vertex
				// exactly at scan line or something went very much wrong if
				// there are 3+ vertices. What ever the reason just return the
				// vertices to temp vertices for later use. In former case it
				// was handled already earlier once iterating through
				// mRotatedVertices, in latter case it's better to avoid doing
				// anything with them.
				mArrTempVertices.addAll(intersections);
			}

			while (mArrOutputVertices.size() > 0) {
				Vertex v = mArrOutputVertices.remove(0);
				mArrTempVertices.add(v);

				// Untouched vertices.
				if (i == 0) {
					mVerticesCountFront++;
				}
				// 'Completely' rotated vertices.
				else {
					v.mPosX = -v.mPosX;
					mVerticesCountBack++;

				}
				// We use local textureFront for flipping backside texture
				// locally. Plus additionally if mesh is in flip texture mode,
				// we'll make the procedure "backwards". Also, until this point,
				// texture coordinates are within [0, 1] range so we'll adjust
				// them to final texture coordinates too.
				// if (textureFront != mFlipTexture) {
				// v.mTexX *= mTextureRectFront.right;
				// v.mTexY *= mTextureRectFront.bottom;
				if (first)
					v.mColor = mPage.getColor(CurlPage.SIDE_FRONT);
				else
					v.mColor = Color.WHITE;

				// Move vertex back to 'world' coordinates.
				v.rotateZ(curlAngle);
				v.translate(curlPos.x, curlPos.y);

				addVertex(v);
			}

			// Switch scanXmin as scanXmax for next iteration.
			scanXmax = scanXmin;
		}
		// Calculate Final Curled Page Coordinates
		// FindVertices(OutPutVertices);

		mBufVertices.position(0);
		mBufColors.position(0);
		if (DRAW_TEXTURE) {
			mBufTexCoords.position(0);
		}

	}

	public int GetScore() {
		return Score;
	}

	private void CalculateShape(PointF curlPos, double curlAngle) {

		mRotatedShape.clear();
		// Section to Rotate And Translate The FinalShape Vertices
		for (int h = 0; h < mFinalShape.size(); h++) {
			Point2D tmp = new Point2D(mFinalShape.get(h).getX(), mFinalShape
					.get(h).getY());
			tmp.translate(-curlPos.x, -curlPos.y);
			tmp.rotateZ(-curlAngle);
			mRotatedShape.add(tmp);
		}

		if (DRAW_POLYGON_OUTLINES_BACK) {
			mOutBackCount = 0;
			// mBufOutBack.position(0);
		}
		if (DRAW_POLYGON_OUTLINES_FRONT) {
			mOutFrontCount = 0;
			//mBufOutFront.position(0);
		}
		PolyDefault test = new PolyDefault();
		for (int j = 0; j < mRotatedShape.size(); j++) {
			test.add(mRotatedShape.get(j));
		}

		Poly Leftres = new PolyDefault();
		Leftres = LeftCheck.intersection(test);

		Poly RightRes = new PolyDefault();
		RightRes = RightCheck.intersection(test);

		PolyDefault a;

		for (int i = 0; i < RightRes.getNumInnerPoly(); i++) {
			a = new PolyDefault();
			for (int j = 0; j < RightRes.getInnerPoly(i).getNumPoints(); j++) {
				Point2D tmp = new Point2D(RightRes.getInnerPoly(i).getX(j),
						RightRes.getInnerPoly(i).getY(j));
				a.add(tmp);
			}
			if (a.getNumPoints() >= 3) {
				mTempPolyStack.add(a);
			}
		}
		Poly mFrontPoly = new PolyDefault();

		for (int i = 0; i < Leftres.getNumInnerPoly(); i++) {
			a = new PolyDefault();
			for (int j = 0; j < Leftres.getInnerPoly(i).getNumPoints(); j++) {
				double x = -Leftres.getInnerPoly(i).getX(j);
				Point2D tmp = new Point2D(x, Leftres.getInnerPoly(i).getY(j));
				a.add(tmp);
			}
			if (a.getNumPoints() >= 3) {
				mTempPolyStack.add(a);
				mFrontPoly = mFrontPoly.union(a);
			}
		}

		Poly mTempPolygon = new PolyDefault();
		while (!mTempPolyStack.empty()) {
			mTempPolygon = mTempPolygon.union(mTempPolyStack.pop());
		}

		
		//ADDING PolyGon OutLine Front
		if (!mFrontPoly.isEmpty()) {
			if(DRAW_POLYGON_OUTLINES_FRONT){
				mBufOutFront.position(0);
			
			for (int i = 0; i < mFrontPoly.getNumPoints(); i++) {
				Point2D v = new Point2D(mFrontPoly.getX(i),
						mFrontPoly.getY(i));
				v.rotateZ(curlAngle);
				v.translate(curlPos.x, curlPos.y);
				mBufOutFront.put((float) v.getX());
				mBufOutFront.put((float) v.getY());
				mOutFrontCount++;
				
			}
			mBufOutFront.position(0);
			}
		} 
		mFrontPoly.clear();
		
		mRotatedShape.clear();

		// Actually i shouldnt use the mRotated shape Here again for ANother Use
		// But i do this for just here, next i can optimize my coede

		if (!mTempPolygon.isEmpty()) {
			for (int i = 0; i < mTempPolygon.getNumPoints(); i++) {
				Point2D v = new Point2D(mTempPolygon.getX(i),
						mTempPolygon.getY(i));
				v.rotateZ(curlAngle);
				v.translate(curlPos.x, curlPos.y);
				// Log.e(i +"mRotatedShape", ""+v.getX()+ " AND " +v.getY());
				mRotatedShape.add(v);
			}
		} else {
			//Log.e("mTempPolygonWasEMpty!", "mTempPolygonWasEMpty!");
			return;
		}

		// Section For Optimizing The MRotatedShape
		Optimize.clear();
		for (int i = 0; i < mRotatedShape.size() - 1; i++) {
			if (Math.abs(mRotatedShape.get(i + 1).getX()
					- mRotatedShape.get(i).getX()) > 0.01
					|| Math.abs(mRotatedShape.get(i + 1).getY()
							- mRotatedShape.get(i).getY()) > 0.01) {
				// Log.e("ADDING", "ADDING");
				Optimize.add(mRotatedShape.get(i));
			}
		}
		if (!Optimize.isEmpty()) {
			Optimize.add(mRotatedShape.get(mRotatedShape.size() - 1));
			mRotatedShape.clear();
			for (int i = 0; i < Optimize.size(); i++) {
				mRotatedShape.add(Optimize.get(i));
			}
		}
		// Section For Optimizing The MRotatedShape

		// mTempShape = SortClockWise(mRotatedShape);
		// mTempShape = quickhull.quickHull(mRotatedShape);
		// mTempShape = mRotatedShape;
		for (int i = 0; i < mRotatedShape.size(); i++) {
			mFinalPolygon.add(mRotatedShape.get(i));
			// Log.e(i +"mTempShape", ""+mTempShape.get(i).getX()+ " AND "
			// +mTempShape.get(i).getY());
		}
		// Log.d("MRotatedShapeSize =", "" + mRotatedShape.size());
		// mTempShape = quickhull.quickHull(mRotatedShape);
		// PolyDefault tmp = new PolyDefault();
		// for(int i = 0;i<mTempShape.size();i++){
		// tmp.add(mTempShape.get(i));
		// }
		mTempPolygon.clear();

		double score = mFinalPolygon.xor(mDesPoly).getArea();
		if (score <= 1 && score >= 0) {
			Score = ((int) ((1 - score) * 100));
		} else {
			Score = 0;
		}
		if (Score >= Constants.ScoreLimit) {
			Score = 100;
		}
		MCopyPolygon.clear();
		if (!mFinalPolygon.isEmpty()) {
			if (DRAW_POLYGON_OUTLINES_BACK) {
				mOutBackCount = 0;
				mBufOutBack.position(0);
			}
			for (int i = 0; i < mFinalPolygon.getNumPoints(); i++) {
				// Log.e(i +"MFinalPolygon = ", " " + mFinalPolygon.getX(i)+ " "
				// + mFinalPolygon.getY(i));
				MCopyPolygon.add(mFinalPolygon.getX(i), mFinalPolygon.getY(i));
				if (DRAW_POLYGON_OUTLINES_BACK) {
					mBufOutBack.put((float) mFinalPolygon.getX(i));
					mBufOutBack.put((float) mFinalPolygon.getY(i));
					mOutBackCount++;
				}
			}
			FindVertices(MCopyPolygon);
			if (DRAW_POLYGON_OUTLINES_BACK) {
				mBufOutBack.position(0);
			}
		}
		mFinalPolygon.clear();
		mTempPolyStack.clear();
	}
	//@ TODO Not Using This Yet
//	public boolean CheckTheCurlPosOut(PointF CurlPos) {
//
//		int counter = 0;
//		int i;
//		double xinters;
//		Point2D p1, p2;
//		int N = mFinalShape.size();
//		p1 = mFinalShape.get(0);
//		for (i = 0; i < N; i++) {
//			p2 = mFinalShape.get(i % N);
//			if (CurlPos.y > Math.min(p1.getY(), p2.getY())) {
//				if (CurlPos.y <= Math.max(p1.getY(), p2.getY())) {
//					if (CurlPos.x <= Math.max(p1.getX(), p2.getX())) {
//						if (p1.getY() != p2.getY()) {
//							xinters = (CurlPos.y - p1.getY())
//									* (p2.getX() - p1.getX())
//									/ (p2.getY() - p1.getY()) + p1.getX();
//							if (p1.getX() == p2.getX() || CurlPos.x <= xinters)
//								counter++;
//						}
//					}
//				}
//			}
//			p1 = p2;
//		}
//
//		if (counter % 2 == 0)
//			return true;
//		else
//			return false;
//
//	}
	/**
	 * Calculates intersections for given scan line.
	 */
	private Array<Vertex> getIntersections(Array<Vertex> vertices,
			int[][] lineIndices, double scanX) {
		mArrIntersections.clear();
		// Iterate through rectangle lines each re-presented as a pair of
		// vertices.
		// //Log.d("length", ""+lineIndices.length+"");
		for (int j = 0; j < lineIndices.length; j++) {
			Vertex v1 = vertices.get(lineIndices[j][0]);
			Vertex v2 = vertices.get(lineIndices[j][1]);
			// Here we expect that v1.mPosX >= v2.mPosX and wont do intersection
			// test the opposite way.
			if (v1.mPosX > scanX && v2.mPosX < scanX) {
				// There is an intersection, calculate coefficient telling 'how
				// far' scanX is from v2.
				double c = (scanX - v2.mPosX) / (v1.mPosX - v2.mPosX);
				Vertex n = mArrTempVertices.remove(0);
				n.set(v2);
				n.mPosX = scanX;
				n.mPosY += (v1.mPosY - v2.mPosY) * c;
				if (DRAW_TEXTURE) {
					n.mTexX += (v1.mTexX - v2.mTexX) * c;
					n.mTexY += (v1.mTexY - v2.mTexY) * c;
				}
				mArrIntersections.add(n);
			}
		}
		return mArrIntersections;
	}

	/**
	 * Getter for textures page for this mesh.
	 */
	public synchronized CurlPage getTexturePage() {
		return mPage;
	}

	void render(GL10 gl, boolean DrawDest) {

		if (DRAW_TEXTURE && mPage.getTexturesChanged()) {
			mPage.recycle();
			reset();
		}
		// Some 'global' settings.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		if (DRAW_TEXTURE) {
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mBufTexCoords);
		}
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufVertices);
		// Enable color array.
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mBufColors);
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		if (!DRAW_TEXTURE) {
			// inja khasiate transparent ro faAl mikonim ke betoonim too
			// CurlPage be kaaghaz alpha bedim
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, mVerticesCountFront);
		}
		// Draw front facing texture.
		if (DRAW_TEXTURE) {
			gl.glEnable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, renderTex[0]);
			gl.glBlendFunc(GL10.GL_ONE_MINUS_DST_ALPHA, GL10.GL_ZERO);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, mVerticesCountFront);
			gl.glDisable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}

		int backStartIdx = Math.max(0, mVerticesCountFront - 2);
		int backCount = mVerticesCountFront + mVerticesCountBack - backStartIdx;

		// Draw back facing blank vertices.
		if (!DRAW_TEXTURE) {
			// Added At 5/6/1394
			gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, backStartIdx, backCount);
		}

		// Draw back facing texture.
		if (DRAW_TEXTURE) {
			gl.glEnable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, renderTex[0]);
			// Changed with gl.glBlendFunc(GLES10.GL_SRC_ALPHA,
			// GLES10.GL_ONE_MINUS_SRC_ALPHA); At 5/6/1394
			gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, backStartIdx, backCount);
			gl.glDisable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}

		// Disable textures and color array.
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		
		if (DRAW_POLYGON_OUTLINES_FRONT) {
			gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
			gl.glLineWidth(3.0f);
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mBufOutFront);
			gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, mOutFrontCount);
		}
		if (DRAW_DEST_POLY && DrawDest) {
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glLineWidth(5.0f);
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mBufDestVertices);
			gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, mDesPoly.getNumPoints());

		}

		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	/**
	 * Renders our page curl mesh.
	 */
	public synchronized void onDrawFrame(GL10 gl, boolean DrawDest) {
		render(gl, DrawDest);
	}

	public synchronized void FindVertices(List<Point2D> outPutVertices) {
		int n = outPutVertices.size();
		if (outPutVertices.get(0).getX() > outPutVertices.get(1).getX()) {
			maxX = outPutVertices.get(0).getX();
			maxX2 = outPutVertices.get(1).getX();
		} else {
			maxX = outPutVertices.get(1).getX();
			maxX2 = outPutVertices.get(0).getX();
		}
		if (outPutVertices.get(0).getX() > outPutVertices.get(1).getX()) {
			minX = outPutVertices.get(1).getX();
			minX2 = outPutVertices.get(0).getX();
		} else {
			maxX = outPutVertices.get(0).getX();
			minX2 = outPutVertices.get(1).getX();
		}

		if (outPutVertices.get(0).getY() > outPutVertices.get(1).getY()) {
			maxY = outPutVertices.get(0).getY();
			maxY2 = outPutVertices.get(1).getY();
		} else {
			maxY = outPutVertices.get(1).getY();
			maxY2 = outPutVertices.get(0).getY();
		}
		if (outPutVertices.get(0).getY() > outPutVertices.get(1).getY()) {
			minY = outPutVertices.get(1).getY();
			minY2 = outPutVertices.get(0).getY();
		} else {
			minY = outPutVertices.get(0).getY();
			minY2 = outPutVertices.get(1).getY();
		}

		for (int i = 0; i < n; i++) {

			if (outPutVertices.get(i).getX() >= maxX) {
				maxX2 = maxX;
				maxX = outPutVertices.get(i).getX();
			} else if (outPutVertices.get(i).getX() > maxX2) {
				maxX2 = outPutVertices.get(i).getX();
			}

			if (outPutVertices.get(i).getX() <= minX) {
				minX2 = minX;
				minX = outPutVertices.get(i).getX();

			} else if (outPutVertices.get(i).getX() < minX2) {
				minX2 = outPutVertices.get(i).getX();
			}
			if (outPutVertices.get(i).getY() >= maxY) {
				maxY2 = maxY;
				maxY = outPutVertices.get(i).getY();
			} else if (outPutVertices.get(i).getY() > maxY2) {
				maxY2 = outPutVertices.get(i).getY();
			}
			if (outPutVertices.get(i).getY() <= minY) {
				minY2 = minY;
				minY = outPutVertices.get(i).getY();
			} else if (outPutVertices.get(i).getY() < minY2) {
				minY2 = outPutVertices.get(i).getY();
			}

		}
		height = (maxY - minY);
		width = (maxX - minX);
	}

	public synchronized void FindVertices(PolyDefault outPutVertices) {
		int n = outPutVertices.getNumPoints();
		if (outPutVertices.getX(0) > outPutVertices.getX(1)) {
			maxX = outPutVertices.getX(0);
			maxX2 = outPutVertices.getX(1);
		} else {
			maxX = outPutVertices.getX(1);
			maxX2 = outPutVertices.getX(0);
		}
		if (outPutVertices.getX(0) > outPutVertices.getX(1)) {
			minX = outPutVertices.getX(1);
			minX2 = outPutVertices.getX(0);
		} else {
			maxX = outPutVertices.getX(0);
			minX2 = outPutVertices.getX(1);
		}

		if (outPutVertices.getY(0) > outPutVertices.getY(1)) {
			maxY = outPutVertices.getY(0);
			maxY2 = outPutVertices.getY(1);
		} else {
			maxY = outPutVertices.getY(1);
			maxY2 = outPutVertices.getY(0);
		}
		if (outPutVertices.getY(0) > outPutVertices.getY(1)) {
			minY = outPutVertices.getY(1);
			minY2 = outPutVertices.getY(0);
		} else {
			minY = outPutVertices.getY(0);
			minY2 = outPutVertices.getY(1);
		}

		for (int i = 0; i < n; i++) {
			if (outPutVertices.getX(i) >= maxX) {
				maxX2 = maxX;
				maxX = outPutVertices.getX(i);
			} else if (outPutVertices.getX(i) > maxX2) {
				maxX2 = outPutVertices.getX(i);
			}

			if (outPutVertices.getX(i) <= minX) {
				minX2 = minX;
				minX = outPutVertices.getX(i);

			} else if (outPutVertices.getX(i) < minX2) {
				minX2 = outPutVertices.getX(i);
			}
			if (outPutVertices.getY(i) >= maxY) {
				maxY2 = maxY;
				maxY = outPutVertices.getY(i);
			} else if (outPutVertices.getY(i) > maxY2) {
				maxY2 = outPutVertices.getY(i);
			}
			if (outPutVertices.getY(i) <= minY) {
				minY2 = minY;
				minY = outPutVertices.getY(i);
			} else if (outPutVertices.getY(i) < minY2) {
				minY2 = outPutVertices.getY(i);
			}

		}

		height = (maxY - minY);
		width = (maxX - minX);
	}

	public double GetMinX() {
		return minX;
	}

	public double GetMinY() {
		return minY;
	}

	public double GetMaxY() {
		return maxY;
	}

	public double GetMaxX() {
		return maxX;
	}

	public double GetMinX2() {
		return minX2;
	}

	public double GetMinY2() {
		return minY2;
	}

	public double GetMaxY2() {
		return maxY2;
	}

	public double GetMaxX2() {
		return maxX2;
	}

	public int GetTexture() {
		return renderTex[0];
	}

	public double GetW() {
		return width;
	}

	public double GetH() {
		return height;
	}

	/**
	 * If true, flips texture sideways.
	 */
	public synchronized void setFlipTexture() {
		setTexCoords(0f, 0f, 1f, 1f);
	}

	/**
	 * Sets texture coordinates to mRectangle vertices.
	 */
	private synchronized void setTexCoords(float left, float top, float right,
			float bottom) {
		mVertices.get(0).mTexX = left;
		mVertices.get(0).mTexY = bottom;
		mVertices.get(1).mTexX = left;
		mVertices.get(1).mTexY = top;
		mVertices.get(2).mTexX = right;
		mVertices.get(2).mTexY = bottom;
		mVertices.get(3).mTexX = right;
		mVertices.get(3).mTexY = top;
	}

	/**
	 * Resets mesh to 'initial' state. Meaning this mesh will draw a plain
	 * textured rectangle after call to this method.
	 */
	public synchronized void Inital() {
		mFinalPolyStack.clear();
		DRAW_TEXTURE = false;
		first = true;
		setRect();
		mFinalShape.clear();
		mFinalShape.add(new Point2D(-1, 1));
		mFinalShape.add(new Point2D(1, 1));
		mFinalShape.add(new Point2D(1, -1));
		mFinalShape.add(new Point2D(-1, -1));

		if (DRAW_POLYGON_OUTLINES_FRONT) {
			mOutFrontCount = 0;
			mBufOutFront.position(0);
		}
		if (DRAW_POLYGON_OUTLINES_BACK) {
			mOutBackCount = 0;
			mBufOutBack.position(0);
			for (int i = 0; i < mFinalShape.size(); i++) {
				mOutBackCount++;
				mBufOutBack.put((float) mFinalShape.get(i).getX());
				mBufOutBack.put((float) mFinalShape.get(i).getY());
			}
			mBufOutBack.position(0);
		}

		mBufVertices.position(0);
		mBufColors.position(0);
		if (DRAW_TEXTURE) {
			mBufTexCoords.position(0);
		}
		for (int i = 0; i < 4; ++i) {
			Vertex tmp = mArrTempVertices.get(0);
			tmp.set(mVertices.get(i));
			tmp.mColor = mPage.getColor(CurlPage.SIDE_FRONT);
			addVertex(tmp);
		}

		FindVertices(mFinalShape);
		mVerticesCountFront = 4;
		mVerticesCountBack = 0;
		mBufVertices.position(0);
		mBufColors.position(0);
		if (DRAW_TEXTURE) {
			mBufTexCoords.position(0);
		}

	}

	/**
	 * Resets mesh to 'initial' state. Meaning this mesh will draw a plain
	 * textured rectangle after call to this method.
	 */
	public synchronized void reset() {
		mBufVertices.position(0);
		mBufColors.position(0);
		if (DRAW_TEXTURE) {
			mBufTexCoords.position(0);
		}
		for (int i = 0; i < 4; ++i) {
			Vertex tmp = mArrTempVertices.get(0);
			tmp.set(mVertices.get(i));
			if (first)
				tmp.mColor = mPage.getColor(CurlPage.SIDE_FRONT);
			else
				tmp.mColor = Color.WHITE;
			addVertex(tmp);
		}
		FindVertices(mFinalShape);
		mVerticesCountFront = 4;
		mVerticesCountBack = 0;
		mBufVertices.position(0);
		mBufColors.position(0);
		if (DRAW_TEXTURE) {
			mBufTexCoords.position(0);
		}
	}

	/**
	 * Update mesh bounds.
	 */
	public void setRect() {
		mVertices.get(0).mPosX = -1;
		mVertices.get(0).mPosY = 1;
		mVertices.get(1).mPosX = -1;
		mVertices.get(1).mPosY = -1;
		mVertices.get(2).mPosX = 1;
		mVertices.get(2).mPosY = 1;
		mVertices.get(3).mPosX = 1;
		mVertices.get(3).mPosY = -1;

	}

	/**
	 * Holder for vertex information.
	 */
	private class Vertex {
		public int mColor;
		public float mColorFactor;
		public double mPosX;
		public double mPosY;
		public double mPosZ;
		public double mTexX;
		public double mTexY;

		public Vertex() {
			mPosX = mPosY = mPosZ = mTexX = mTexY = 0;
			mColorFactor = 1.0f;
		}

		public void rotateZ(double theta) {
			double cos = Math.cos(theta);
			double sin = Math.sin(theta);
			double x = mPosX * cos + mPosY * sin;
			double y = mPosX * -sin + mPosY * cos;
			mPosX = x;
			mPosY = y;
		}

		public void set(Vertex vertex) {
			mPosX = vertex.mPosX;
			mPosY = vertex.mPosY;
			mPosZ = vertex.mPosZ;
			mTexX = vertex.mTexX;
			mTexY = vertex.mTexY;
			mColor = vertex.mColor;
			mColorFactor = vertex.mColorFactor;
		}

		public void translate(double dx, double dy) {
			mPosX += dx;
			mPosY += dy;
		}
	}

}