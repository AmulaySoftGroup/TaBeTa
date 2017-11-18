/*
    Copyright 2015 Alireza.pir

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package ir.amulay.tabeta.curl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Environment;
import android.util.Log;

/**
 * Actual renderer class.
 * 
 * @author alireza.pir
 */

public class CurlRenderer implements GLSurfaceView.Renderer {

	// Set to true for checking quickly how perspective projection looks.
	private static final boolean USE_PERSPECTIVE_PROJECTION = false;
	// Background fill color.
	private int mBackgroundColor;
	// Curl meshes used for static and dynamic rendering.
	private CurlMesh mCurlMesh;

	private RectF mMargins = new RectF();
	private CurlRenderer.Observer mObserver;
	// Page rectangles.
	private RectF mPageRect;
	// View mode.
	// Screen size.

	private int mViewportWidth, mViewportHeight;
	// Rect for render area.
	private RectF mViewRect = new RectF();

	// Actual Resulotion For Texture
	int texW = 1024; // 2048;
	int texH = 1024;

	private Bitmap bmp;

	private FBO frameBuffer;
	boolean DelPrevious = false;
	public boolean CreatePNG = false;
	int DelTextureId[] = new int[1];

	/**
	 * FLAGS
	 */

	// DO I Want to Capture the screen?
	private boolean capture = false;

	// Attach the NEW Texture to FrameBuffer?
	private boolean AttachTexture = false;

	private boolean BeforeStart = true;

	// Is iT the First Time?
	private boolean first = true;

	// Variable to Check If The OnDrawFrameHaveBeen Called Succesfully
	private int Counter = -1;

	/**
	 * Basic constructor.
	 */
	public CurlRenderer(CurlRenderer.Observer observer) {
		mObserver = observer;
		mCurlMesh = new CurlMesh();
		mPageRect = new RectF();
		frameBuffer = new FBO(texW, texH);

	}

	// Getter And Setter For Counter
	public int GetCounter() {
		return Counter;
	}

	public void SetCounter(int x) {
		Counter = x;
	}

	/**
	 * Adds CurlMesh to this renderer.
	 */
	public synchronized void addCurlMesh(CurlMesh mesh) {
		mCurlMesh = mesh;
	}

	/**
	 * Returns rect reserved for left or right page. Value page should be
	 * PAGE_LEFT or PAGE_RIGHT.
	 */
	public RectF getPageRect() {
		return mPageRect;
	}

	public int getTexture() {
		return frameBuffer.getTexture();
	}

	public void saveBmp(GL10 gl) {
		int screenshotSize = mViewportWidth * mViewportHeight;
		ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
		bb.order(ByteOrder.nativeOrder());
		gl.glReadPixels(0,0, mViewportWidth, mViewportHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
				bb);
		int pixelsBuffer[] = new int[screenshotSize];
		bb.asIntBuffer().get(pixelsBuffer);
		bb = null;

		for (int i = 0; i < screenshotSize; ++i) {
			// The alpha and green channels' positions are preserved while the
			// red and blue are swapped
			pixelsBuffer[i] = ((pixelsBuffer[i] & 0xff00ff00))
					| ((pixelsBuffer[i] & 0x000000ff) << 16)
					| ((pixelsBuffer[i] & 0x00ff0000) >> 16);
		}

		Bitmap bitmap = Bitmap
				.createBitmap(mViewportWidth, mViewportHeight, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixelsBuffer, screenshotSize - mViewportWidth, -mViewportHeight, 0, 0,
				mViewportWidth, mViewportHeight);
        String mPath = Environment.getExternalStorageDirectory().toString()+ "/" + "Share.jpg";
        File imageFile = new File(mPath);

        FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(imageFile);
	        int quality = 100;
	        bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
	        try {
				outputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getFBO() {
		return frameBuffer.getFBO();
	}

	public synchronized void FreePre(int textureID) {
		DelTextureId[0] = textureID;
		DelPrevious = true;
	}

	private boolean DoneRender = false;

	public void StartPNG(){
		CreatePNG = true;
	}
	@Override
	public synchronized void onDrawFrame(GL10 gl) {

		if (CreatePNG) {
			saveBmp(gl);
			CreatePNG = false;

		}
		mObserver.onDrawFrame();
		// glClearColor miad rangi ke maa entekhaab kardim ro tooye carde
		// Graphic register mikone
		gl.glClearColor(Color.red(mBackgroundColor) / 255f,
				Color.green(mBackgroundColor) / 255f,
				Color.blue(mBackgroundColor) / 255f,
				Color.alpha(mBackgroundColor) / 255f);
		// glClear miad oon rangi ke bala register karde boodim ro dige az
		// buffer paak mikone
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// miad matris ro be MabdaEsh barmigardoone, ke bAd baraye glRotate va
		// glTranslate moshkeli ijaad nashe
		// chon maa asle jaabejaa kardan hamoon baraye safhe, baste be makaane
		// avalieye
		// kaaghazemoon hast, na oon makani ke dar haale hazer gharaar dare
		gl.glLoadIdentity();
		if (capture) {
			// Log.e("Capturing..", "Capturing..");
			if (BeforeStart) {
				// Log.e("BeforeStartCalled", "BeforeStartCalled");
				frameBuffer.BeforeStart(gl);
				BeforeStart = false;
			}
			if (AttachTexture) {
				// Log.e("SETUP", "SETUP Ran");
				int h = gl.glGetError();
				frameBuffer.setup(gl);
				if (h != 0) {
					// Log.d("ERROR", "ERROR Happend" + h + "");
				}
				AttachTexture = false;
			}
			frameBuffer.RenderStart(gl);
			if (USE_PERSPECTIVE_PROJECTION) {
				double x = mCurlMesh.GetMinX() * mCurlMesh.GetMinY();
				gl.glTranslatef((float) (-1.000 - mCurlMesh.GetMinX()),
						(float) (-1.000 - mCurlMesh.GetMinY()), -6f);
			}
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			GLU.gluOrtho2D(gl, (float) mCurlMesh.GetMinX(),
					(float) mCurlMesh.GetMaxX(), (float) mCurlMesh.GetMinY(),
					(float) mCurlMesh.GetMaxY());
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glViewport(0, 0, texW, texH);
			mCurlMesh.onDrawFrame(gl, false);

			frameBuffer.RenderEnd(gl);

		}

		// Reset Every Thing to Its Normal State
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, -1.2f, 1.2f, -1.2f, 1.2f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, mViewportWidth, mViewportHeight);

		mCurlMesh.onDrawFrame(gl, true);
		// Log.e("Rendered", "Rendered");

		if (DelPrevious) {
			// Log.e("DeleTing", "Deleting Texture = " +DelTextureId[0]);
			gl.glDeleteTextures(1, DelTextureId, 0);
			DelPrevious = false;
			Counter++;
		}
		DoneRender = true;
	}

	public boolean RenderEnd() {
		boolean tmp = DoneRender;
		DoneRender = false;
		return tmp;
	}

	public void StopCapture() {
		// createPNG = true;
		capture = false;

		/**
		 * Just Activate this commented part if want to save rendered scene to A
		 * PNG file format to The SD Card;
		 */
		// createPNG = true;
		// updatePageRects();
	}

	public void StartCapture() {
		capture = true;
		AttachTexture = true;
	}

	public boolean ISCapturing() {
		return capture;
	}

	/**
	 * Getter for Bitmap Generated By SavePixels function
	 * 
	 * @return
	 */
	public Bitmap GetBMP() {
		return bmp;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, height, height);
		mViewportWidth = height;
		mViewportHeight = height;
		Log.d("MviewPorTWidth", "" + mViewportWidth);
		/**
		 * @ TODO inja mViewRect Mige man che baazeE az OpenGL ro mitoonam
		 * bebinam, alan ino gozashtam 1.1 hamasho, chon mikham kami bozorgtar
		 * az kaaghaz bashe view ye page, yekam fazaaye khaali dashte bashe
		 */
		if (first) {
			mViewRect.top = 1.2f;
			mViewRect.bottom = -1.2f;
			// -ratio
			mViewRect.left = -1.2f;
			mViewRect.right = 1.2f;
			updatePageRects();

			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			if (USE_PERSPECTIVE_PROJECTION) {
				GLU.gluPerspective(gl, 20f, (float) width / height, .1f, 100f);
			} else {
				GLU.gluOrtho2D(gl, mViewRect.left, mViewRect.right,
						mViewRect.bottom, mViewRect.top);
			}
			first = false;
		}

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0f, 0f, 0f, 0f);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
		gl.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);
		// gl.glEnable(GL10.GL_LINE_SMOOTH);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_DITHER);
	}

	/**
	 * Change background/clear color.
	 */
	public void setBackgroundColor(int color) {
		mBackgroundColor = color;
	}

	/**
	 * Set margins or padding. Note: margins are proportional. Meaning a value
	 * of .1f will produce a 10% margin.
	 */
	public synchronized void setMargins(float left, float top, float right,
			float bottom) {
		mMargins.left = left;
		mMargins.top = top;
		mMargins.right = right;
		mMargins.bottom = bottom;
		updatePageRects();
	}

	/**
	 * Translates screen coordinates into view coordinates. mokhtassate ye
	 * noghte (masalan pointer Position) roye safhe ro, be moAdele mokhtasaatesh
	 * rooye CurlView Tabdil mikene
	 */
	public void translate(PointF pt) {
		pt.x = mViewRect.left + (mViewRect.width() * pt.x / mViewportWidth);
		pt.y = mViewRect.top - (-mViewRect.height() * pt.y / mViewportHeight);
	}

	/**
	 * Recalculates page rectangles.
	 */
	private void updatePageRects() {
		if (mViewRect.width() == 0 || mViewRect.height() == 0) {
			return;
		}
		/**
		 * @ TODO inja daghighan hamnoon kaari ke mikham, yAni size dadan be
		 * Page ro anjaam mide mpageRect... khode meshe va mViewRect view E
		 * layout
		 */
		mPageRect.set(mViewRect);
		mPageRect.left += mViewRect.width() * mMargins.left;
		mPageRect.right -= mViewRect.width() * mMargins.right;
		mPageRect.top += mViewRect.height() * mMargins.top;
		mPageRect.bottom -= mViewRect.height() * mMargins.bottom;

		int bitmapW = (int) ((mPageRect.width() * mViewportWidth) / mViewRect
				.width());
		int bitmapH = (int) ((mPageRect.height() * mViewportHeight) / mViewRect
				.height());
		mObserver.onPageSizeChanged(bitmapW, bitmapH);

	}

	/**
	 * Observer for waiting render engine/state updates.
	 */
	public interface Observer {
		/**
		 * Called from onDrawFrame called before rendering is started. This is
		 * intended to be used for animation purposes.
		 */
		public void onDrawFrame();

		/**
		 * Called once page size is changed. Width and height tell the page size
		 * in pixels making it possible to update textures accordingly.
		 */
		public void onPageSizeChanged(int width, int height);

	}

}
