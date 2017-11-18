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

import ir.amulay.tabeta.activities.GameActivity;
import ir.amulay.tabeta.database.HintControl;
import ir.amulay.tabeta.database.InternalDB;
import ir.amulay.tabeta.globals.Constants;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * OpenGL ES View.
 * 
 * @author Alireza.Pir WWW.Andvoid.IR
 */

public class CurlView extends GLSurfaceView implements View.OnTouchListener,
		CurlRenderer.Observer {
	// Curl state. We are flipping none, left or right page.
	private int CURL_NONE = 0;
	private int CURL_RIGHT_TO_LEFT = 1;
	private int CURL_LEFT_TO_RIGHT = 2;
	private int CURL_UP_TO_DOWN = 3;
	private int CURL_DOWN_TO_UP = 4;
//	private OnlineServer Server;

	// Constants for mAnimationTargetEvent.
	// private static final int SET_CURL_BACK = 1;

	// Aya Alan daare animation pakhsh mishe ya na
	public boolean mAnimate = false;
	private long mAnimationDurationTime = 800;// toole zamane animatione harkate
												// kaaghaz
	private PointF mAnimationSource = new PointF();
	public long mAnimationStartTime;
	private PointF mAnimationTarget = new PointF();

	private PointF mCurlDir = new PointF();

	private PointF mCurlPos = new PointF();

	private PointF CurlDir = new PointF();

	private PointF CurlPos = new PointF();

	private int mCurlState = CURL_NONE;

	// Start position for dragging.
	private PointF mDragStartPos = new PointF();
	private PointF startpos = new PointF();

	// Bitmap size. These are updated from renderer once it's initialized.
	private int mPageBitmapHeight = -1;

	private int mPageBitmapWidth = -1;

	/**
	 * mPageCurl Oon meshie ke baayad taa khordegi roosh emaal beshe
	 */
	private CurlMesh mPageCurl;

	private PageProvider mPageProvider;

	private PointerPosition mPointerPos = new PointerPosition();

	private CurlRenderer mRenderer;

	/**
	 * ina baraye ine ke control konim kaaghaz bishtar az hade mojaaz taa
	 * nakhore
	 */
	private PointF CurlMins = new PointF();
	private PointF CurlMaxs = new PointF();
	private PointF CurlMins2 = new PointF();
	private PointF CurlMaxs2 = new PointF();
	// Stack For Holding CurlInformation For Each Curl
	private CurlStack stack;
	private int MaxStackItem = 8;
	// The Item that will be added to the stack
	private StackItem item = null;

	// HINT Cup Coin VARIABLES
	HintControl Hint;
	int CurrentHint;
	int AvHintCount;
	int PreHintCount;
	int TotalHintCount;

	int CoinCount;
	int Vaahed;
	int[] CUPS;
	// HINT Cup Coin VARIABLES

	SharedPreferences information;
	private String COIN_TAG = Constants.COIN_TAG;
	private String PRE_HINT_TAG = Constants.PRE_HINT_TAG;
	Editor et;

	GameActivity gameActivity;


	private int LevelNumber;

	// To Control Does The Paper Is Foldable OR Not!
	private boolean FoldAble = true;

	/**
	 * Default constructor.
	 */
	public CurlView(Context ctx) {
		super(ctx);
		init();
	}

	/**
	 * Default constructor.
	 */
	public CurlView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init();
	}

	/**
	 * Default constructor.
	 */
	public CurlView(Context ctx, AttributeSet attrs, int defStyle) {
		this(ctx, attrs);
	}

	// Get The User Score
	public int GetScore() {
		return mPageCurl.GetScore();
	}

	public void StartPNG(){
		mRenderer.StartPNG();
		requestRender();
	}
	/**
	 * Intialize Firs Variables And Sets The Dest Poly
	 * 
	 * @param db
	 *            //DataBase Passed From
	 * @param lvl
	 *            //Game Level
	 * @param inf
	 *            //Shared Perference to SAVE And UPDATE Data
	 * @param vaahed
	 *            //Hint Calculation UNIT
	 * @param Gactivity
	 *            //The Activity To be Able call The UPDATEUI method
	 */
	public void Init(InternalDB db, int lvl, SharedPreferences inf, int vaahed,
			GameActivity Gactivity, int[] cups) {
		LevelNumber = lvl;
		mPageCurl.SetDesPoly(db, LevelNumber);
		Hint = new HintControl(db, LevelNumber);
		requestRender(); 
	 
	//	Server = new OnlineServer();
		gameActivity = Gactivity;
		Vaahed = vaahed;
		CurrentHint = 0;
		CUPS = cups;
		information = inf;
		et = information.edit();


		AvHintCount = Hint.GetHitCount();

		et.putInt("LvlHintCount" + lvl, AvHintCount);
		et.commit();
		CoinCount = information.getInt(COIN_TAG, 0);

		TotalHintCount = CoinCount / Vaahed;
		PreHintCount = information.getInt(PRE_HINT_TAG + LevelNumber, 0);
		
	}

	/**
	 * Saves The Data And Updates The Activity UI
	 */
	public void SaveAndUpdate() {
		// Toast.makeText(gameActivity.getApplicationContext(), "Saving Data",
		// Toast.LENGTH_SHORT).show();

		et.putInt(PRE_HINT_TAG + LevelNumber, PreHintCount);
		et.putInt(COIN_TAG, CoinCount);
	//	Server.SubmitCoin(CoinCount, information.getString("DeviceID", "null"), et);
		et.commit();
		gameActivity.UpdateUI();
	}

	// Returns The Remain Pre Hint Cout
	public int GetRemainPreHint() {
		if (CurrentHint < PreHintCount) {
			return PreHintCount - CurrentHint;
		} else
			return 0;
	}

	public int GetLevelHintCount() {
		return AvHintCount;
	}

	public int GetCurrenHint() {
		return CurrentHint;
	}

	/**
	 * Hint Function
	 */
	public void DoHint() {

		// Cuse mayBe The User Watched The Video And So The Coin Count
		// Increased...
		CoinCount = information.getInt(COIN_TAG, 0);
		TotalHintCount = CoinCount / Vaahed;

		if (mAnimate) {
			return;
		}
		if (CurrentHint >= AvHintCount) {
			return;
		}
		// Log.e("HINT", "HINT");
		if (CurrentHint >= TotalHintCount + PreHintCount) {

			return;
		}
		if (CurrentHint == 0) {
			initial();
		}
		PointF CurlPos = Hint.GetCurlPos(CurrentHint);
		PointF CurlDir = Hint.GetCurlDir(CurrentHint);
		setCurlPos(CurlPos, CurlDir, 0);
		// If It Was Greater Than or equal PreHint Count
		// So The user is Using One of his Hints,
		// So the Coin should Be decreased
		if (CurrentHint >= PreHintCount) {
			CoinCount = CoinCount - Vaahed;
			PreHintCount++;
			TotalHintCount--;
			SaveAndUpdate();
		}
		CurrentHint++;
		long loopStart = 0;
		long loopEnd = 0;
		long loopRunTime = 0;
		final int FPS = (1000 / 10);
		requestRender();
		while (!mRenderer.RenderEnd()) {
			loopStart = System.currentTimeMillis();
			if (loopRunTime < FPS) {
				try {
					// Log.e("Sleeping", "Sleeping");
					Thread.sleep(FPS - loopRunTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			loopEnd = System.currentTimeMillis();
			loopRunTime = ((loopEnd - loopStart));
		}
		if (CurlDir.x >= 0 && CurlDir.y >= 0) {
			mAnimationTarget.x = mRenderer.getPageRect().left - 0.01f;
			mAnimationTarget.y = mRenderer.getPageRect().bottom - 0.01f;
		} else if (CurlDir.x >= 0 && CurlDir.y <= 0) {
			mAnimationTarget.x = mRenderer.getPageRect().left - 0.01f;
			mAnimationTarget.y = mRenderer.getPageRect().top + 0.01f;
		} else if (CurlDir.x <= 0 && CurlDir.y <= 0) {
			mAnimationTarget.x = mRenderer.getPageRect().right + 0.01f;
			mAnimationTarget.y = mRenderer.getPageRect().top + 0.01f;
		} else if (CurlDir.x <= 0 && CurlDir.y >= 0) {
			mAnimationTarget.x = mRenderer.getPageRect().right + 0.01f;
			mAnimationTarget.y = mRenderer.getPageRect().bottom - 0.01f;
		}

				curldone();

		// requestRender();
	}

	/**
	 * Initialize method.
	 */
	private void init() {
		super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		/**
		 * @ TODO in baraye Transparent shodane safhast ke felan commentesh
		 * kardam dar nahayat bayad commente in ro bardaram
		 */
		// getHolder().setFormat(PixelFormat.TRANSLUCENT);
		// setZOrderOnTop(true);

		setZOrderOnTop(true);

		getHolder().setFormat(PixelFormat.RGBA_8888);
		// setZOrderMediaOverlay(true);
		mRenderer = new CurlRenderer(this);
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		setOnTouchListener(this);
		mPageCurl = new CurlMesh();
		stack = new CurlStack(MaxStackItem);
	}

	@Override
	public void onDrawFrame() {
		// We are not animating.
		if (mAnimate == false) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		// If animation is done.
		if (currentTime >= mAnimationStartTime + mAnimationDurationTime) {
			mAnimate = false;
			mPageCurl.SetAnimState(mAnimate);
			mPageCurl.reset();
			requestRender();
		}
		// in Else dar heyne curlBack shodane ejraa mishe
		else {
			mPointerPos.mPos.set(mAnimationSource);
			float t = 1f - ((float) (currentTime - mAnimationStartTime) / mAnimationDurationTime);
			t = 1f - (t * t * t * (3 - 2 * t));
			mPointerPos.mPos.x += (mAnimationTarget.x - mAnimationSource.x) * t;
			mPointerPos.mPos.y += (mAnimationTarget.y - mAnimationSource.y) * t;
			updateCurlPos(mPointerPos);
		}
	}

	@Override
	public void onPageSizeChanged(int width, int height) {
		mPageBitmapWidth = width;
		mPageBitmapHeight = height;
		updatePages();
		requestRender();
	}

	@Override
	public boolean onTouch(View view, MotionEvent me) {
		// You may have to play with the value.
		// A value of two means you require the user to move twice as
		// far in the direction they intend to move than any perpendicular
		// direction.
		int threshold = 10;
		// No dragging during animation at the moment.
		// TODO: Stop animation on touch event and return to drag mode.
		if (mAnimate || mPageProvider == null) {
			return false;
		}

		// We need page rects quite extensively so get them for later use.
		RectF pageRect = mRenderer.getPageRect();

		// Store pointer position.
		mPointerPos.mPos.set(me.getX(), me.getY());
		mRenderer.translate(mPointerPos.mPos);

		switch (me.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			if (!FoldAble) {
				gameActivity.NotAgreedScore();
				return false;
			}
			// requestRender();
			// Once we receive pointer down event its position is mapped to
			// right or left edge of page and that'll be the position from where
			// user is holding the paper to make curl happen.
			mDragStartPos.set(mPointerPos.mPos);
			// Log.e("MpoinTerPos = ", "(" + mPointerPos.mPos.x +"," +
			// mPointerPos.mPos.y+")");
			startpos.x = me.getX();
			startpos.y = me.getY();
			mCurlState = CURL_NONE;
			// First we make sure it's not over or below page. Pages are
			// supposed to be same height so it really doesn't matter do we use
			// // left or right one.
			if (mDragStartPos.y > pageRect.top) {
				mDragStartPos.y = pageRect.top;
			} else if (mDragStartPos.y < pageRect.bottom) {
				mDragStartPos.y = pageRect.bottom;
			}
			CurlMins.x = (float) mPageCurl.GetMinX();
			CurlMins.y = (float) mPageCurl.GetMinY();
			CurlMaxs.x = (float) mPageCurl.GetMaxX();
			CurlMaxs.y = (float) mPageCurl.GetMaxY();
			CurlMins2.x = (float) mPageCurl.GetMinX2();
			CurlMins2.y = (float) mPageCurl.GetMinY2();
			CurlMaxs2.x = (float) mPageCurl.GetMaxX2();
			CurlMaxs2.y = (float) mPageCurl.GetMaxY2();
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			// mPageProvider.UpdateScore(mPageCurl.GetScore());
			if (mCurlState == CURL_NONE) {
				// Log.d("CURL_NONE","CURL_NONE");
				float touchActionMoveX = me.getX();
				float touchActionMoveY = me.getY();
				if (touchActionMoveX < (startpos.x - threshold)) {
					mDragStartPos.x = CurlMaxs.x;
					startCurl(CURL_RIGHT_TO_LEFT);

				} else if (touchActionMoveX > (startpos.x + threshold)) {

					mDragStartPos.x = CurlMins.x;
					startCurl(CURL_LEFT_TO_RIGHT);

				} else if (touchActionMoveY < (startpos.y - threshold)) {
					mDragStartPos.y = CurlMins.y;
					startCurl(CURL_DOWN_TO_UP);

				} else if (touchActionMoveY > (startpos.y + threshold)) {
					mDragStartPos.y = CurlMaxs.y;
					startCurl(CURL_UP_TO_DOWN);
				}

			} else {
				updateCurlPos(mPointerPos);
				CurrentHint = 0;
			}
			break;

		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			// @ TODO just for test, remove below test ke tamaam shod
			// mPageCurl.setRect();
			// mPageCurl.reset();
			// requestRender();
			// @ TODO ino hazf kardam shartesho chon estefade nemishe
			if (mCurlState == CURL_DOWN_TO_UP) {
				// Animation source is the point from where animation starts.
				// Also it's handled in a way we actually simulate touch events
				// meaning the output is exactly the same as if user drags the
				// page to other side. While not producing the best looking
				// result (which is easier done by altering curl position and/or
				// direction directly), this is done in a hope it made code a
				// bit more readable and easier to maintain.
				mAnimationTarget.set(mDragStartPos);
				mAnimationTarget.y = mRenderer.getPageRect().bottom - 0.01f;
			} else if (mCurlState == CURL_UP_TO_DOWN) {
				mAnimationTarget.set(mDragStartPos);
				mAnimationTarget.y = mRenderer.getPageRect().top + 0.01f;
			} else if (mCurlState == CURL_RIGHT_TO_LEFT) {
				mAnimationTarget.set(mDragStartPos);
				mAnimationTarget.x = mRenderer.getPageRect().right + 0.01f;
			} else if (mCurlState == CURL_LEFT_TO_RIGHT) {
				mAnimationTarget.set(mDragStartPos);
				mAnimationTarget.x = mRenderer.getPageRect().left - 0.01f;
			}
			/**
			 * @ TODO in to ehtemaalan (motmaEnan) bayad baresh daram bAdan
			 */
			curldone();

			mCurlState = CURL_NONE;
			break;
		}
		}

		return true;
	}

	/**
	 * called when Curls done and sets the texture
	 */
	private void curldone() {
		if (mRenderer.ISCapturing()) {

					mRenderer.StopCapture();
					int texture = mRenderer.getTexture();
					int FBO = mRenderer.getFBO();
					mPageCurl.SetNewPaper(null, texture);
					// create a new stack Item and push it to stack
					item = new StackItem(mPageCurl.GetMinX(), mPageCurl.GetMinY(),
							mPageCurl.GetMaxX(), mPageCurl.GetMaxY(), CurlPos, CurlDir,
							CurlPos, mAnimationTarget, texture, FBO);
					stack.push(item);
					item = null;
					requestRender();


			if (stack.IsFull()) {
				Log.e("StackEror", "Stack Is Full");
				return;
			}
			int score = mPageCurl.GetScore();
			if(stack.size() < AvHintCount && score > 95 ){
				gameActivity.CalculateScore(score,true);
			}
			
			gameActivity.SetAghrabe(1);
			// Curls Finished. so Calculate The Score And Cups
			if (stack.size() == AvHintCount || AvHintCount == 0) {
				

				SaveAndUpdate();
				if (score < 80) {
					gameActivity.NotAgreedScore();
				} else {
					gameActivity.CalculateScore(score,false);
				}
				FoldAble = false;
			}
		}
	}

	/**
	 * Called When The the Undo Button Cliks and
	 */
	public void curlback() {
		if (!FoldAble) {
			FoldAble = true;
		}
		CurrentHint = 0;
		if (stack.IsEmpty()) {
			mPageCurl.Inital();
			mAnimate = true;
			mPageCurl.SetAnimState(mAnimate);
			gameActivity.SetUndoState(false);
			mPageCurl.curl(mCurlPos, mCurlDir, 0);
			mAnimationStartTime = System.currentTimeMillis();
			requestRender();
			return;
		}
		StackItem poped = new StackItem(stack.pop());
		// Delete Previus Texture And FBO
		mRenderer.FreePre(poped.getTextureID());
		mAnimationTarget = poped.getAnimationTarget();
		mAnimationSource = poped.getAnimationSource();
		mCurlPos = poped.getCurlPos();
		mCurlDir = poped.getCurlDir();
		if (stack.IsEmpty()) {
			mPageCurl.Inital();
			gameActivity.SetUndoState(false);
			mAnimate = true;
			mPageCurl.SetAnimState(mAnimate);
			mPageCurl.curl(mCurlPos, mCurlDir, 0);
			mAnimationStartTime = System.currentTimeMillis();
			requestRender();
			return;
		}
		StackItem toped = new StackItem(stack.top());
		mAnimate = true;
		mPageCurl.SetAnimState(mAnimate);
		mPageCurl.SetNewPaper(toped, 0);
		mPageCurl.curl(poped.getCurlPos(), poped.getCurlDir(), 0);
		mAnimationStartTime = System.currentTimeMillis();
		requestRender();

	}

	/**
	 * Called When Reset BT is Clicked
	 */
	public void initial() {
		gameActivity.SetAghrabe(0);
		CurrentHint = 0;
		mAnimate = false;
		mRenderer.SetCounter(0);
		int check = -1;
		while (mRenderer.GetCounter() < stack.size()) {
			if (check != mRenderer.GetCounter() && check < stack.size() - 1) {
				check = mRenderer.GetCounter();
				mRenderer.FreePre(stack.getTexture(check));
				requestRender();
			}
		}
		stack.clear();
		mPageCurl.Inital();
		FoldAble = true;
		requestRender();
	}

	/**
	 * Sets background color - or OpenGL clear color to be more precise. Color
	 * is a 32bit value consisting of 0xAARRGGBB and is extracted using
	 * android.graphics.Color eventually.
	 */
	@Override
	public void setBackgroundColor(int color) {
		mRenderer.setBackgroundColor(color);
		requestRender();
	}

	/**
	 * Sets mPageCurl curl position.
	 */

	private void setCurlPos(PointF curlPos, PointF curlDir, double radius) {

		RectF pageRect = mRenderer.getPageRect();
		// First reposition curl so that page doesn't 'rip off' from book.
		if (mCurlState == CURL_RIGHT_TO_LEFT) {

			if (curlPos.x >= pageRect.right) {
				// mPageCurl.reset();
				requestRender();
				return;
			}
			if (curlPos.x < CurlMins.x) {
				curlPos.x = CurlMins.x + 0.02f;
			}
			if (curlPos.y < CurlMins.y) {
				curlPos.y = CurlMins.y + 0.02f;
			}
			if (curlPos.y > CurlMaxs.y) {
				curlPos.y = CurlMaxs.y - 0.02f;
			}
			if (curlPos.x > CurlMaxs.x) {
				curlPos.x = CurlMaxs.x - 0.02f;
			}
			if (curlPos.x < pageRect.left) {
				curlPos.x = pageRect.left;
			}
		} else if (mCurlState == CURL_LEFT_TO_RIGHT) {
			if (curlPos.x <= pageRect.left) {
				// mPageCurl.reset();
				requestRender();
				return;
			}
			if (curlPos.x < CurlMins.x) {
				curlPos.x = CurlMins.x + 0.02f;
			}
			if (curlPos.y < CurlMins.y) {
				curlPos.y = CurlMins.y + 0.02f;
			}
			if (curlPos.y > CurlMaxs.y) {
				curlPos.y = CurlMaxs.y - 0.02f;
			}
			if (curlPos.x > CurlMaxs.x) {
				curlPos.x = CurlMaxs.x - 0.02f;
			}
			if (curlPos.x > pageRect.right) {
				curlPos.x = pageRect.right;
			}
		} else if (mCurlState == CURL_UP_TO_DOWN) {
			if (curlPos.y >= pageRect.top) {
				requestRender();
				return;
			}
			if (curlPos.x < CurlMins.x) {
				curlPos.x = CurlMins.x + 0.02f;
			}
			if (curlPos.y < CurlMins.y) {
				curlPos.y = CurlMins.y + 0.02f;
			}
			if (curlPos.y > CurlMaxs.y) {
				curlPos.y = CurlMaxs.y - 0.02f;
			}
			if (curlPos.x > CurlMaxs.x) {
				curlPos.x = CurlMaxs.x - 0.02f;
			}
		}

		else if (mCurlState == CURL_DOWN_TO_UP) {
			if (curlPos.y <= pageRect.bottom) {
				// mPageCurl.reset();
				requestRender();
				return;
			}
			if (curlPos.x < CurlMins.x) {
				curlPos.x = CurlMins.x + 0.02f;
			}
			if (curlPos.y < CurlMins.y) {
				curlPos.y = CurlMins.y + 0.02f;
			}
			if (curlPos.y > CurlMaxs.y) {
				curlPos.y = CurlMaxs.y - 0.02f;
			}
			if (curlPos.x > CurlMaxs.x) {
				curlPos.x = CurlMaxs.x - 0.02f;
			}
			if (curlPos.y > pageRect.top) {
				curlPos.y = pageRect.top;
			}
		}

		if (!mRenderer.ISCapturing() && !mAnimate) {
			mRenderer.StartCapture();
			requestRender();
		}

		// Finally normalize direction vector and do rendering.
		double dist = Math.sqrt(curlDir.x * curlDir.x + curlDir.y * curlDir.y);
		if (dist != 0) {
			curlDir.x /= dist;
			curlDir.y /= dist;
			if( -1.009f < curlDir.x && curlDir.x < -0.999f)
				curlDir.x = -1f;
			if( -0.02f < curlDir.y && curlDir.y < 0.02f)
				curlDir.y = 0.0f;
			if( -1.06f < curlDir.y && curlDir.y < -0.94f)
				curlDir.y = -1f;
			if( -0.04f < curlDir.x && curlDir.x < 0.04f)
				curlDir.x = 0.0f;
			if( 0.999f < curlDir.x && curlDir.x < 1.009f)
				curlDir.x = 1f;
			 
			
//			if( 0.45f < curlDir.x && curlDir.x < 0.55f)
//				curlDir.x = 0.5f;
//			if( 0.45f < curlDir.y && curlDir.y < 0.55f)
//				curlDir.y = 0.5f;
			
			
//			Log.e("curlPos = ", ""+curlPos.x  + " ANd " + curlPos.y);
//			Log.e("curlDirs = ", ""+curlDir.x  + " ANd " + curlDir.y);
//			
//			if( -0.03f < curlPos.y && curlPos.y < 0.03f)
//				curlPos.y = 0.0f;
//			if( -0.03f < curlPos.x && curlPos.x < 0.03f)
//				curlPos.x = 0.0f;
//			
//			if(curlDir.x < 0 && curlDir.y <0 && Math.abs(curlDir.x - curlDir.y) < 0.05f){
//				curlDir.x = -0.7f;
//				curlDir.y = -0.7f;
//			}
//			if(curlDir.x > 0 && curlDir.y >0 && Math.abs(curlDir.x - curlDir.y) < 0.05f){
//				curlDir.x = 0.7f;
//				curlDir.y = 0.7f;
//			}
//			if(curlDir.x < 0 && curlDir.y >0 && Math.abs(curlDir.x - curlDir.y) < 0.05f){
//				curlDir.x = -0.7f;
//				curlDir.y = 0.7f;
//			}
//			if(curlDir.x > 0 && curlDir.y <0 && Math.abs(curlDir.x - curlDir.y) < 0.05f){
//				curlDir.x = 0.7f;
//				curlDir.y = -0.7f;
//			}
//			Log.e("CurlPos  = ", "" + CurlPos.x + " AND " + CurlPos.y);
//			Log.e("CurlDir  = ", "" + CurlDir.x + " AND " + CurlDir.y);
			mPageCurl.curl(curlPos, curlDir, radius);
		}
		CurlPos = curlPos;
		CurlDir = curlDir;
		requestRender();
		long loopStart = 0;
		long loopEnd = 0;
		long loopRunTime = 0;
		final int FPS = (1000 / 20);
		while (!mRenderer.RenderEnd()) {
			loopStart = System.currentTimeMillis();
			if (loopRunTime < FPS) {
				try {
					// Log.e("Sleeping", "Sleeping");
					Thread.sleep(FPS - loopRunTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			loopEnd = System.currentTimeMillis();
			loopRunTime = ((loopEnd - loopStart));
		}

	}

	/**
	 * Set margins (or padding). Note: margins are proportional. Meaning a value
	 * of .1f will produce a 10% margin.
	 */
	public void setMargins(float left, float top, float right, float bottom) {
		mRenderer.setMargins(left, top, right, bottom);
	}

	/**
	 * Update/set page provider.
	 */
	public void setPageProvider(PageProvider pageProvider) {
		mPageProvider = pageProvider;
		updatePages();
		requestRender();
	}

	/**
	 * Switches meshes and loads new bitmaps if available. Updated to support 2
	 * pages in landscape
	 */
	private void startCurl(int page) {
		mCurlState = page;
	}

	/**
	 * Updates curl position.
	 */
	private void updateCurlPos(PointerPosition pointerPos) {

		double radius = 0.0f;
		mCurlPos.set(pointerPos.mPos);
		// If curl happens on right page, or on left page on two page mode,
		// we'll calculate curl position from pointerPos.
		if (mCurlState == CURL_RIGHT_TO_LEFT
				|| mCurlState == CURL_LEFT_TO_RIGHT) {
			// if(!mAnimate){
			mCurlDir.y = mCurlPos.y - mDragStartPos.y;
			mCurlDir.x = mCurlPos.x - mDragStartPos.x;
			// }

			float dist = (float) Math.sqrt(mCurlDir.x * mCurlDir.x + mCurlDir.y
					* mCurlDir.y);

			// Adjust curl radius so that if page is dragged far enough on
			// opposite side, radius gets closer to zero.
//			float pageWidth = (float) mPageCurl.GetW();// mRenderer.getPageRect().width();

			// Actual curl position calculation.
				double translate = dist/ 2;
				mCurlPos.x -= mCurlDir.x * translate / dist;
				mCurlPos.y -= mCurlDir.y * translate / dist;
		}

		else if (mCurlState == CURL_UP_TO_DOWN || mCurlState == CURL_DOWN_TO_UP) {
			mCurlDir.y = mCurlPos.y - mDragStartPos.y;
			mCurlDir.x = mCurlPos.x - mDragStartPos.x;
			float dist = (float) Math.sqrt(mCurlDir.x * mCurlDir.x + mCurlDir.y
					* mCurlDir.y) ;

			// Adjust curl radius so that if page is dragged far enough on
			// opposite side, radius gets closer to zero.
//			float pageHeight = (float) mPageCurl.GetH();
			double curlLen =  -0.15f;
//			if (dist > (pageHeight * 2) - curlLen) {
//				curlLen = Math.max((pageHeight * 2) - dist, 0f);
//				radius = curlLen / Math.PI;
//			}
			// Actual curl position calculation.
			if (dist >= curlLen) {
				double translate = (dist - curlLen) / 2;
				mCurlPos.y -= mCurlDir.y * translate / dist;
				mCurlPos.x -= mCurlDir.x * translate / dist;
			} else {
				double angle = Math.PI * Math.sqrt(dist / curlLen);
				double translate = radius * Math.sin(angle);
				mCurlPos.x += mCurlDir.x * translate / dist;
				mCurlPos.y += mCurlDir.y * translate / dist;
			}
		}
		setCurlPos(mCurlPos, mCurlDir, radius);
	}

	/**
	 * Updates given CurlPage via PageProvider for page located at index.
	 */

	private void updatePage(CurlPage page) {

		// Ask page provider to fill it up with bitmaps and colors.
		mPageProvider.updatePage(page, mPageBitmapWidth, mPageBitmapHeight);
	}

	/**
	 * Updates bitmaps for page meshes.
	 */
	private void updatePages() {
		if (mPageProvider == null || mPageBitmapWidth <= 0
				|| mPageBitmapHeight <= 0) {
			return;
		}
		updatePage(mPageCurl.getTexturePage());
		mPageCurl.setFlipTexture();
		mPageCurl.setRect();
		mPageCurl.reset();
		mRenderer.addCurlMesh(mPageCurl);
	}

	/**
	 * Provider for feeding 'book' with bitmaps which are used for rendering
	 * pages.
	 */
	public interface PageProvider {
		/**
		 * Called once new bitmaps/textures are needed. Width and height are in
		 * pixels telling the size it will be drawn on screen and following them
		 * ensures that aspect ratio remains. But it's possible to return bitmap
		 * of any size though. You should use provided CurlPage for storing page
		 * information for requested page number.<br/>
		 * <br/>
		 * Index is a number between 0 and getBitmapCount() - 1.
		 */
		public void updatePage(CurlPage page, int width, int height);
	}

	/**
	 * Simple holder for pointer position.
	 */
	private class PointerPosition {
		PointF mPos = new PointF(); 
	}

	public void ClearMemory() {
		//gameActivity.finish();
		mPageCurl.ClearMemory(); 
		stack.clear();
		stack = null;
		mPageCurl = null;
		mAnimationSource = null;
		mAnimationTarget= null;
		mCurlDir = null;
		mCurlPos= null;
		CurlPos = null;
		CurlDir = null;
		mDragStartPos = null;
		startpos= null;
		mPointerPos= null;
		mRenderer = null;
		CurlMins = null;
		CurlMaxs = null;
		CurlMins2 = null;
		CurlMaxs2 = null;
		Hint = null;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}
