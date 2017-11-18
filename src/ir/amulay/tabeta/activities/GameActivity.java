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

package ir.amulay.tabeta.activities;

import ir.amulay.tabeta.R;
import ir.amulay.tabeta.curl.CurlPage;
import ir.amulay.tabeta.curl.CurlView;
import ir.amulay.tabeta.database.InternalDB;
import ir.amulay.tabeta.globals.Constants;
import ir.amulay.tabeta.tourguide.ChainTourGuide;
import ir.amulay.tabeta.tourguide.Overlay;
import ir.amulay.tabeta.tourguide.Pointer;
import ir.amulay.tabeta.tourguide.Sequence;
import ir.amulay.tabeta.tourguide.ToolTip;
import ir.amulay.tabeta.tourguide.TourGuide;
import ir.tapsell.tapsellvideosdk.developer.CheckCtaAvailabilityResponseHandler;
import ir.tapsell.tapsellvideosdk.developer.DeveloperInterface;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Game Activity.
 * 
 * @author Alireza.pir WWW.AMULAY.IR
 */

public class GameActivity extends Activity implements OnClickListener {
	private CurlView mCurlView;
	private ImageButton undo;
	private ImageView Aghrabe,star1,star2,star3,gamebackIm,lvlnameIm,cupIm,scoreIm;
	private ImageView Limit;
	ImageButton HintBt;
	TextView LHCTxt;// Level Hinc Count Text
	TextView PHCTxt;// Previous Hint Count Text;
	TextView CupCountTxt;
	int CupCout;
	int CoinCout;
	TextView CoinCountTxt;
	// TextView HighScoreTxt;
	String NowDate;
	Dialog dialog, HintPanel,GetCoinPannel;
	private SharedPreferences information;
	Editor et;
	float FromDegree;
	private int Vaahed = Constants.Vaahed;// Vahede Sanjeshe tabdile Coin
	final Handler handle = new Handler(); // be HInt
	private int[] CUPS = Constants.CUPS;
	TourGuide mTourGuideHandler;
	//private OnlineServer Server;
	//private String android_id;

	int World_Number;
	int WlvlNumber;
	int LvlNumber;
	String[] LvlName;
	boolean TourActive = false;

	private boolean Gotten = false;
	TextView remainTime;
	TextView LvlNametxt;
	Typeface tf;

	int Score;
	private String Sh_P_Tag = Constants.SH_PREF_TAG;
	private String PRE_HINT_TAG = Constants.PRE_HINT_TAG;
	private String HIGHSCORE_TAG = Constants.HIGHSCORE_TAG;
	private String WL_TAG = Constants.WL_TAG;
	private String L_TAG = Constants.L_TAG;
	private String W_TAG = Constants.W_TAG;
	private String COIN_TAG = Constants.COIN_TAG;
	private String CUP_TAG = Constants.CUP_TAG;
	private String LVLHCOUNT = Constants.LVL_HINT_COUNT_TAG;
	private String EndTimeTag = Constants.ENDTIME_TAG;

	private Animation mEnterAnimation, mExitAnimation;

	private MediaPlayer[] FoldingSounds;
	private MediaPlayer[] UNFoldingSounds;
	private MediaPlayer UseHintSound;
	private MediaPlayer[] StarGotSound;

	private boolean PlaySound;

	// To Detect If Next Level BT Is Pressed. Dont Play Sounds
	private boolean stoped;
	private boolean first;
	String EndTime; // Start date
	Runnable r;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		// hideSystemUI(); 
 
		setContentView(R.layout.gamescene);
		stoped = false;
		first = true;
		// HighScoreTxt = (TextView) findViewById(R.id.highscore);
		undo = (ImageButton) findViewById(R.id.undo);
		HintBt = (ImageButton) findViewById(R.id.hint);
		Aghrabe = (ImageView) findViewById(R.id.aghrabe);
		Limit = (ImageView) findViewById(R.id.limit);
		final TextView score = (TextView) findViewById(R.id.score);

		CupCountTxt = (TextView) findViewById(R.id.cuptxt);

		mCurlView = (CurlView) findViewById(R.id.curl);
		undo.setOnClickListener(this);
		HintBt.setOnClickListener(this);
		findViewById(R.id.share).setOnClickListener(this);
		information = getSharedPreferences(Sh_P_Tag, MODE_PRIVATE);

		PlaySound = information.getBoolean("PLAYSOUND", false);
		// Set The Finded Height To CurlView Height
		final ViewGroup.LayoutParams layoutParams = mCurlView.getLayoutParams();
		// layoutParams.width = size.y;
		// layoutParams.height = size.y;

		final LinearLayout headerLayout = (LinearLayout) findViewById(R.id.curl_layout);
		ViewTreeObserver observer = headerLayout.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				layoutParams.width = headerLayout.getHeight()-0;
				// layoutParams.width =
				// findViewById(R.id.curl_layout).getMeasuredHeight();
				layoutParams.height = layoutParams.width;

			}
		});
		Score = 0;
		mCurlView.setLayoutParams(layoutParams);
		mCurlView.requestLayout();
		// imv.getLayoutParams().width=height;
		// imv.getLayoutParams().height=height;
		// imv.requestLayout();

		mCurlView.setPageProvider(new PageProvider());
		// mCurlView.setMargins(.05f, .05f, .05f, .05f);
		/**
		 * @ TODO 0xFF202830 in ro bAdan be 0x00000000 taghEeer bede ta
		 * background kaamel transparent beshe
		 */
		mCurlView.setBackgroundColor(0x00000000);

		gamebackIm = (ImageView) findViewById(R.id.gamebackimage);
		Picasso.with(getApplicationContext()).load(R.drawable.opacityback)
				.fit().centerCrop().into(gamebackIm);
		lvlnameIm = (ImageView) findViewById(R.id.lvlname);
		Picasso.with(getApplicationContext()).load(R.drawable.lvlnameboard)
				.fit().centerCrop()
				.into(lvlnameIm);
		cupIm = (ImageView) findViewById(R.id.cupcount);
		Picasso.with(getApplicationContext()).load(R.drawable.cupscountboard)
				.fit().centerCrop()
				.into(cupIm);
		scoreIm = (ImageView) findViewById(R.id.scorepanel);
		Picasso.with(getApplicationContext()).load(R.drawable.scoreboard).fit()
				.centerCrop().into(scoreIm);

		r = new Runnable() {
			@Override
			public void run() {
				int SCORE = mCurlView.GetScore();
				score.setText("" + SCORE + " %");

				// if (WlvlNumber == 11 && SCORE >= 97 && flag) {
				// flag = false;
				// CalculateScore(SCORE);
				// }
				handle.postDelayed(this, 150);
			}
		};
		handle.postDelayed(r, 150);
		
		
	//	Server = new OnlineServer();
		////android_id = Secure.getString(getApplicationContext()
		//		.getContentResolver(), Secure.ANDROID_ID);
		et = information.edit();
	//	et.putString("DeviceID", android_id);
	//	et.commit();

//		handle1 = new Handler();
//		runa = new Runnable() {
//			@Override
//			public void run() {
//				Server.GetCoin(android_id, et);
//				Server.GetCup(android_id, et);
//				CupCout = information.getInt(CUP_TAG, 0);
//				CoinCout = information.getInt(COIN_TAG, 1500);
//				// if (WlvlNumber == 11 && SCORE >= 97 && flag) {
//				// flag = false;
//				// CalculateScore(SCORE);
//				// }
//				handle1.postDelayed(this, 5000);
//			}
//		};
//		handle1.postDelayed(runa, 5000);
		
		CupCout = information.getInt(CUP_TAG, 0);
		CoinCout = information.getInt(COIN_TAG, 1500);
		
		//Level Number
		LvlNumber = getIntent().getExtras().getInt(L_TAG);
		
		//WorldLevelNumber
		WlvlNumber = getIntent().getExtras().getInt(WL_TAG);
	
		//World Number
		World_Number = getIntent().getExtras().getInt(W_TAG);
		 
		
		if (!information.contains(PRE_HINT_TAG + WlvlNumber)) {
			et.putInt(PRE_HINT_TAG + WlvlNumber, 0);
			et.putInt(HIGHSCORE_TAG + WlvlNumber, 0);
			et.commit();
		}
		 
		CupCout = information.getInt(CUP_TAG, 0);
		// As The Level Hint Count don't Change During The Level. it is not
		// necessary
		// to put it in UpdateUI method

		// HighScoreTxt.setText("HighScore: "
		// + information.getInt(HIGHSCORE_TAG + WlvlNumber, 0));


		 
		// Set The Aghrabe to Its Initial Stat...
		FromDegree = 0.0f;
		final RotateAnimation rotateAnim = new RotateAnimation(FromDegree,
				-150f, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.6f);
		FromDegree = -150f;
		rotateAnim.setDuration(1000);
		rotateAnim.setFillAfter(true);
		Aghrabe.startAnimation(rotateAnim);
		undo.setEnabled(false);
		HintPanel = new Dialog(GameActivity.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		HintPanel.setContentView(R.layout.hintpanel);
		HintPanel.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		// WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		// lp.copyFrom(dialog.getWindow().getAttributes());
		// lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		// lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		//
		// dialog.getWindow().setAttributes(lp);

		Window window = HintPanel.getWindow();
		window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.CENTER);

		// The below code is EXTRA - to dim the parent view by 70%
		LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.7f;
		lp.flags = LayoutParams.FLAG_DIM_BEHIND;
		HintPanel.getWindow().setAttributes(lp);
		// HintPanel.setCancelable(false);

		HintPanel.findViewById(R.id.getfreehint).setOnClickListener(this);
		HintPanel.findViewById(R.id.exit).setOnClickListener(this);
		HintPanel.findViewById(R.id.usehint).setOnClickListener(this);
		HintPanel.findViewById(R.id.prehint).setOnClickListener(this);
		HintPanel.findViewById(R.id.buybt).setOnClickListener(this);

		LHCTxt = (TextView) HintPanel.findViewById(R.id.lvlhintcount);
		PHCTxt = (TextView) HintPanel.findViewById(R.id.prehintcount);
		remainTime = (TextView) HintPanel.findViewById(R.id.remaintime);
		CoinCountTxt = (TextView) HintPanel.findViewById(R.id.yourcoincount);

		// Disable the Pre Hint If there is Now Pre Hints At First..
		if (information.getInt(PRE_HINT_TAG + WlvlNumber, 0) == 0) {
			SetPreEnabled(false);
			SetUseEnabled(true);
		} else {
			SetPreEnabled(true);
			SetUseEnabled(false);
		}
		
		

		
		StartHintOffer();
		// for Farsi Writing...
		tf = Typeface.createFromAsset(getAssets(), "fonts/Dastnevis.ttf");
		remainTime.setTypeface(tf);
		LHCTxt.setTypeface(tf);
		CoinCountTxt.setTypeface(tf);
		PHCTxt.setTypeface(tf);
		CupCountTxt.setTypeface(tf);
		score.setTypeface(tf);

		remainTime.setVisibility(View.GONE);

		switch (World_Number) {
		case 1: {
			LvlName = getResources().getStringArray(R.array.LEVLE1);
			break;
		}
		case 2: {
			LvlName = getResources().getStringArray(R.array.LEVLE2);
			break;
		}
		case 3: {
			LvlName = getResources().getStringArray(R.array.LEVLE3);
			break;
		}
		case 4: {
			LvlName = getResources().getStringArray(R.array.LEVLE4);
			break;
		}
		case 5: {
			LvlName = getResources().getStringArray(R.array.LEVLE5);
			break;
		}
		case 6: {
			LvlName = getResources().getStringArray(R.array.LEVLE6);
			break;
		}
		case 7: {
			LvlName = getResources().getStringArray(R.array.LEVLE6);
			break;
		}
		}
		LvlNametxt = (TextView) findViewById(R.id.lvlName);
		LvlNametxt.setTypeface(tf);
		LvlNametxt.setText(LvlName[LvlNumber - 1]);
		UpdateUI();

		/* setup enter and exit animation */
		mEnterAnimation = new AlphaAnimation(0f, 1f);
		mEnterAnimation.setDuration(10);
		mEnterAnimation.setFillAfter(true);

		mExitAnimation = new AlphaAnimation(1f, 0f);
		mExitAnimation.setDuration(10);
		mExitAnimation.setFillAfter(true);

		// /SOund Declearation Section

		FoldingSounds = new MediaPlayer[5];
		FoldingSounds[0] = MediaPlayer.create(this, R.raw.fold1);
		FoldingSounds[1] = MediaPlayer.create(this, R.raw.fold2);
		FoldingSounds[2] = MediaPlayer.create(this, R.raw.fold3);
		FoldingSounds[3] = MediaPlayer.create(this, R.raw.fold4);
		FoldingSounds[4] = MediaPlayer.create(this, R.raw.fold5);

		UNFoldingSounds = new MediaPlayer[2];
		UNFoldingSounds[0] = MediaPlayer.create(this, R.raw.unfold1);
		UNFoldingSounds[1] = MediaPlayer.create(this, R.raw.unfold2);

		UseHintSound = MediaPlayer.create(this, R.raw.usehint);

		StarGotSound = new MediaPlayer[3];
		StarGotSound[0] = MediaPlayer.create(this, R.raw.stargot);
		StarGotSound[1] = MediaPlayer.create(this, R.raw.stargot);
		StarGotSound[2] = MediaPlayer.create(this, R.raw.stargot);
		// /END Sound Declearation Section
		
		InternalDB db;
		db = InternalDB.getInstance(GameActivity.this);
		db.database();
		mCurlView.Init(db, WlvlNumber, information, Vaahed, GameActivity.this,
				CUPS);
		UpdateHintInfo();




		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// new InitDatabase().execute("");
		
		if (WlvlNumber == 11) {
			mCurlView.setZOrderOnTop(false);
			handle.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					runOverlay_ContinueMethod();
				}
			}, 1000);

			findViewById(R.id.hint).setEnabled(false);
		}

		
		

	}

	private void ReSetSounds() {

		FoldingSounds[0] = MediaPlayer.create(this, R.raw.fold1);
		FoldingSounds[1] = MediaPlayer.create(this, R.raw.fold2);
		FoldingSounds[2] = MediaPlayer.create(this, R.raw.fold3);
		FoldingSounds[3] = MediaPlayer.create(this, R.raw.fold4);
		FoldingSounds[4] = MediaPlayer.create(this, R.raw.fold5);
		UNFoldingSounds[0] = MediaPlayer.create(this, R.raw.unfold1);
		UNFoldingSounds[1] = MediaPlayer.create(this, R.raw.unfold2);
		UseHintSound = MediaPlayer.create(this, R.raw.usehint);
		StarGotSound[0] = MediaPlayer.create(this, R.raw.stargot);
		StarGotSound[1] = MediaPlayer.create(this, R.raw.stargot);
		StarGotSound[2] = MediaPlayer.create(this, R.raw.stargot);
	}

	private void ReleaseSounds() {

		FoldingSounds[0].release();
		FoldingSounds[1].release();
		FoldingSounds[2].release();
		FoldingSounds[3].release();
		FoldingSounds[4].release();

		UNFoldingSounds[0].release();
		UNFoldingSounds[1].release();

		UseHintSound.release();

		StarGotSound[0].release();
		StarGotSound[1].release();
		StarGotSound[2].release();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (requestCode == DeveloperInterface.TAPSELL_DIRECT_ADD_REQUEST_CODE) {
				if (data.hasExtra(DeveloperInterface.TAPSELL_DIRECT_AWARD_RESPONSE)) {
					// The video Has been Watched Completely
					int CoinCount = information.getInt(COIN_TAG, 0)
							+ Constants.VideoAward;

					Editor et = information.edit();
					//Server.SubmitCoin(CoinCount, android_id, et);

					et.putInt(COIN_TAG, CoinCount);

					String StartTime = EndTime;
					SimpleDateFormat sdf = new SimpleDateFormat(
							"dd/M/yyyy HH:mm:ss", java.util.Locale.getDefault());
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(EndTime));
					c.add(Calendar.HOUR, Constants.VideoWaiting); // number of
																	// Hours to
																	// add
					EndTime = sdf.format(c.getTime()); // dt is now the new date
					et.putString(EndTimeTag, EndTime);
					et.commit();

					// Now Disable The Button For 6 Hours...
					Date StartDate = StringToDate(StartTime);
					Date EndDate = StringToDate(EndTime);
					Long diffrence = EndDate.getTime() - StartDate.getTime();
					StartTimer(diffrence);

					UpdateUI();
					// HintPanel.show();

				} else {
					Toast.makeText(getApplicationContext(),
							"مراحل مشاهده ی ویدئو را تکمیل نکردید!",
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void StartTimer(Long RemainTime) {

		// first Disable The Button
		HintPanel.findViewById(R.id.getfreehint).setEnabled(false);

		new CountDownTimer(RemainTime, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				Long mili = millisUntilFinished;
				long secondsInMilli = 1000;
				long minutesInMilli = secondsInMilli * 60;
				long hoursInMilli = minutesInMilli * 60;

				long elapsedHours = mili / hoursInMilli;
				mili = mili % hoursInMilli;

				long elapsedMinutes = mili / minutesInMilli;
				mili = mili % minutesInMilli;

				long elapsedSeconds = mili / secondsInMilli;
				remainTime.setVisibility(View.VISIBLE);
				remainTime.setText("باید برای استفاده مجدد " + elapsedHours
						+ " ساعت و " + elapsedMinutes + " دقیقه و "
						+ elapsedSeconds + " ثانیه صبر کنید...");

			}

			@Override
			public void onFinish() {
				EndTimer();

			}
		}.start();

	}

	private void EndTimer() {
		// first Enable The Button
		HintPanel.findViewById(R.id.getfreehint).setEnabled(true);

		// Then Set The TextView Text To Null
		remainTime.setVisibility(View.GONE);
	}

	/**
	 * ConVerts String To Date Format
	 * 
	 * @param dtStart
	 * @return
	 */
	private Date StringToDate(String dtStart) {
		SimpleDateFormat format = new SimpleDateFormat("dd/M/yyyy HH:mm:ss",
				java.util.Locale.getDefault());
		Date date;
		try {
			date = format.parse(dtStart);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private void StartHintOffer(){
		final Handler ha = new Handler();
		final int delay = 180000; //milliseconds

		ha.postDelayed(new Runnable(){
		    public void run(){
		        RunBuyCoin();
		        ha.postDelayed(this, delay);
		    }
		}, delay);
	}
	public static String getNTPDate() {

		String[] hosts = new String[] { "ntp02.oal.ul.pt", "ntp04.oal.ul.pt",
				"ntp.xs4all.nl", "time.foo.com", "time.nist.gov" };

		NTPUDPClient client = new NTPUDPClient();
		// We want to timeout if a response takes longer than 5 seconds
		client.setDefaultTimeout(2000);
		SimpleDateFormat OutPutFormat = new SimpleDateFormat(
				"dd/M/yyyy HH:mm:ss", java.util.Locale.getDefault());
		for (String host : hosts) {
			try {
				InetAddress hostAddr = InetAddress.getByName(host);
				TimeInfo info = client.getTime(hostAddr);
				Date date = new Date(info.getReturnTime());
				String out = OutPutFormat.format(date);
				return out;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		client.close();
		return null;
	}

	public static boolean isInternetConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo ni = cm.getActiveNetworkInfo();

		if (ni == null)
			return false;
		else {
			if (ni.isConnected())
				if (isOnline(context))
					return true;
				else
					return false;
			return false;
		}
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			try {
				URL url = new URL("http://www.google.com");
				HttpURLConnection urlc = (HttpURLConnection) url
						.openConnection();
				urlc.setConnectTimeout(2000);
				urlc.connect();
				if (urlc.getResponseCode() == 200) {
					return Boolean.valueOf(true);

				}
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Returns the previous power of TOW
	 * 
	 * @param x
	 * @return
	 */

	int PrePowerOfTwo(int x) {
		x = x | (x >> 1);
		x = x | (x >> 2);
		x = x | (x >> 4);
		x = x | (x >> 8);
		x = x | (x >> 16);
		return x - (x >> 1);
	}

	public void UpdateHintpanel() {
		int PremainHint = mCurlView.GetRemainPreHint();
		int lvlHintCount = mCurlView.GetLevelHintCount();
		int CurrenHint = mCurlView.GetCurrenHint();
		CoinCout = information.getInt(COIN_TAG, 0);

		// Set to Text View
		PHCTxt.setText("" + PremainHint);
		// DisApear the Hint Box If there Is No Coin Left
		if (CoinCout == 0) {
			CoinCountTxt.setText("سکــه های شما تمام شده!");
			// if (CurrenHint > PremainHint + 1 || PremainHint == 0)
			// HintPanel.findViewById(R.id.hintusebox)
			// .setVisibility(View.GONE);
		} else {
			CoinCountTxt.setText("شما " + CoinCout + " سکه دارید. ");
			HintPanel.findViewById(R.id.hintusebox).setVisibility(View.VISIBLE);
		}
		if (PremainHint >= 1) {
			SetUseEnabled(false);
			SetPreEnabled(true);
		} else if (PremainHint == 0 && CoinCout < 500) {
			SetUseEnabled(false);
			SetPreEnabled(false);
		} else if (PremainHint == 0 && CurrenHint <= lvlHintCount) {
			SetUseEnabled(true);
			SetPreEnabled(false);
		} else {

			SetUseEnabled(false);
			SetPreEnabled(false);
		}
	}

	public void UpdateHintInfo() {
		if (first) {
			int limit = information.getInt(LVLHCOUNT + WlvlNumber, 0);
			LHCTxt.setText("این مرحله " + limit + " هینت دارد!");

			switch (limit) {
			case 1: {
				Picasso.with(getApplicationContext()).load(R.drawable.limit1)
						.into(Limit);
				break;
			}
			case 2: {
				Picasso.with(getApplicationContext()).load(R.drawable.limit2)
						.into(Limit);
				break;
			}
			case 3: {
				Picasso.with(getApplicationContext()).load(R.drawable.limit3)
						.into(Limit);
				break;
			}
			case 4: {
				Picasso.with(getApplicationContext()).load(R.drawable.limit4)
						.into(Limit);
				break;
			}
			case 5: {
				Picasso.with(getApplicationContext()).load(R.drawable.limit5)
						.into(Limit);
				break;
			}
			case 6: {
				Picasso.with(getApplicationContext()).load(R.drawable.limit6)
						.into(Limit);
				break;
			}
			case 7: {
				Picasso.with(getApplicationContext()).load(R.drawable.limit7)
						.into(Limit);
				break;
			}
			case 8: {
				Picasso.with(getApplicationContext()).load(R.drawable.limit8)
						.into(Limit);
				break;
			}
			default: {
				Picasso.with(getApplicationContext()).load(R.drawable.limit1)
						.into(Limit);
			}

			}
			first = false;
		}

	}

	public void UpdateUI() {

		UpdateHintpanel();
		CupCountTxt.setText("" + information.getInt(CUP_TAG, 0));

	}

	public void NotAgreedScore() {
		YoYo.with(Techniques.BounceIn).duration(700)
				.playOn(findViewById(R.id.undo));
		Toast.makeText(getApplicationContext(), "محدودیت تــاهایتان تمام شده!",
				Toast.LENGTH_SHORT).show();
		// mCurlView.initial();
	}

	/*
	 * CulCulate Final Score...
	 */
	public void CalculateScore(int score,boolean soon) {

		// WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		// lp.copyFrom(dialog.getWindow().getAttributes());
		// lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		// lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		//
		// dialog.getWindow().setAttributes(lp);
		dialog = new Dialog(GameActivity.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.levelcleared);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		Window window1 = dialog.getWindow();
		window1.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		window1.setGravity(Gravity.CENTER);

		// The below code is EXTRA - to dim the parent view by 70%
		LayoutParams lp1 = window1.getAttributes();
		lp1.dimAmount = 0.7f;
		lp1.flags = LayoutParams.FLAG_DIM_BEHIND;
		dialog.getWindow().setAttributes(lp1);
		// dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(true);
		dialog.findViewById(R.id.review).setVisibility(View.GONE);
		dialog.findViewById(R.id.nextlvl).setOnClickListener(this);
		if (LvlNumber == 15) {
			dialog.findViewById(R.id.nextlvl).setEnabled(false);
		}
		dialog.findViewById(R.id.levelselect).setOnClickListener(this);
		dialog.findViewById(R.id.replay).setOnClickListener(this);
		dialog.findViewById(R.id.camcomp).setOnClickListener(this);
		dialog.findViewById(R.id.exit).setOnClickListener(this);
		dialog.findViewById(R.id.review).setOnClickListener(this);

		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				dialog.findViewById(R.id.star1).setVisibility(View.GONE);
				dialog.findViewById(R.id.star2).setVisibility(View.GONE);
				dialog.findViewById(R.id.star3).setVisibility(View.GONE);

			}
		});

		// Save If its A highScore
		Score = score;
		int PreScore = information.getInt(HIGHSCORE_TAG + WlvlNumber, 0);

		dialog.show();

		handle.postDelayed(new Runnable() {

			@Override
			public void run() {
				YoYo.with(Techniques.BounceIn).duration(800)
						.playOn(dialog.findViewById(R.id.review));
				dialog.findViewById(R.id.review).setVisibility(View.VISIBLE);
			}
		}, 500);
		TextView percent = (TextView) dialog.findViewById(R.id.remaintime);
		TextView CupGot = (TextView) dialog.findViewById(R.id.cupgot);
		TextView CoinGot = (TextView) dialog.findViewById(R.id.coingot);
		TextView TotCups = (TextView) dialog.findViewById(R.id.totcups);
		// for Farsi Writing...
		percent.setTypeface(tf);
		CupGot.setTypeface(tf);
		TotCups.setTypeface(tf);
		CoinGot.setTypeface(tf);

		CoinGot.setText("");
		star1 = (ImageView) dialog.findViewById(R.id.star1);
		star2 = (ImageView) dialog.findViewById(R.id.star2);
		star3 = (ImageView) dialog.findViewById(R.id.star3);

		percent.setText(score + "%");

		if (score >= 80 && score < 90) {
			Picasso.with(getApplicationContext()).load(R.drawable.staron)
					.into(star1);
			Picasso.with(getApplicationContext()).load(R.drawable.staroff)
					.into(star2);
			Picasso.with(getApplicationContext()).load(R.drawable.staroff)
					.into(star3);
			CupGot.setText("شما " + CUPS[0] + " کاپ گرفتید!");

			handle.postDelayed(new Runnable() {

				@Override
				public void run() {
					YoYo.with(Techniques.BounceIn).duration(700)
							.playOn(dialog.findViewById(R.id.star1));
					dialog.findViewById(R.id.star1).setVisibility(View.VISIBLE);
					if (PlaySound && !stoped)
						StarGotSound[0].start();
				}
			}, 500);

		} else if (score >= 90 && score < 97) {
			Picasso.with(getApplicationContext()).load(R.drawable.staron)
					.into(star1);
			Picasso.with(getApplicationContext()).load(R.drawable.staron)
					.into(star2);
			Picasso.with(getApplicationContext()).load(R.drawable.staroff)
					.into(star3);
			if(soon)
			CupGot.setText("شما " + CUPS[1] + " کاپ گرفتید!" + "\n"
					+"+ 5 کاپ بیشتر!");
			else
				CupGot.setText("شما " + CUPS[1] + " کاپ گرفتید!");
			
			handle.postDelayed(new Runnable() {

				@Override
				public void run() {
					YoYo.with(Techniques.BounceIn).duration(700)
							.playOn(dialog.findViewById(R.id.star1));
					dialog.findViewById(R.id.star1).setVisibility(View.VISIBLE);
					if (PlaySound && !stoped)
						StarGotSound[0].start();
				}
			}, 500);

			handle.postDelayed(new Runnable() {

				@Override
				public void run() {
					YoYo.with(Techniques.BounceIn).duration(700)
							.playOn(dialog.findViewById(R.id.star2));
					dialog.findViewById(R.id.star2).setVisibility(View.VISIBLE);
					if (PlaySound && !stoped)
						StarGotSound[1].start();
				}
			}, 1200);

		} else if (score >= 97 && score <= 100) {
			Picasso.with(getApplicationContext()).load(R.drawable.staron)
					.into(star1);
			Picasso.with(getApplicationContext()).load(R.drawable.staron)
					.into(star2);
			Picasso.with(getApplicationContext()).load(R.drawable.staron)
					.into(star3);
			if(soon){
				CupGot.setText("شما " + CUPS[1] + " کاپ گرفتید!" + "\n"
						+"+ 5 کاپ بیشتر!");
			}
			else
			CupGot.setText("شما " + CUPS[2] + " کاپ گرفتید!");

			handle.postDelayed(new Runnable() {

				@Override
				public void run() {
					YoYo.with(Techniques.BounceIn).duration(700)
							.playOn(dialog.findViewById(R.id.star1));
					dialog.findViewById(R.id.star1).setVisibility(View.VISIBLE);
					if (PlaySound && !stoped)
						StarGotSound[0].start();
				}
			}, 500);

			handle.postDelayed(new Runnable() {

				@Override
				public void run() {
					YoYo.with(Techniques.BounceIn).duration(700)
							.playOn(dialog.findViewById(R.id.star2));
					dialog.findViewById(R.id.star2).setVisibility(View.VISIBLE);
					if (PlaySound && !stoped)
						StarGotSound[1].start();
				}
			}, 1200);

			handle.postDelayed(new Runnable() {

				@Override
				public void run() {
					YoYo.with(Techniques.BounceIn).duration(700)
							.playOn(dialog.findViewById(R.id.star3));
					dialog.findViewById(R.id.star3).setVisibility(View.VISIBLE);
					if (PlaySound && !stoped)
						StarGotSound[2].start();
				}
			}, 1900);

			// Check If the User Uses Hint To Complete A level Completely for
			// HintAll Achievement...
			if (mCurlView.GetLevelHintCount() == mCurlView.GetCurrenHint()) {
				et.putInt("achievement_hint_all", 1);
				et.commit();

			}
			// Check If We Are in last Level For World 3 For Game Comes TO Half!
			if (WlvlNumber == 315) {
				et.putInt("achievement_game_comes_to_half", 1);
				et.commit();
			}
			// Check If We Are in Last Level For Final Point Achievement...
			if (WlvlNumber == 615) {
				et.putInt("achievement_final_point", 1);
				et.commit();
			}

			// If We Are in Word 1 Check If All levelS Has 3 stars For First
			// Word All Achievement...
			if (World_Number == 1) {
				boolean FirstWorldAll = true;
				for (int i = 1; i <= 15; i++) {
					int z = information.getInt(HIGHSCORE_TAG + "1" + i, 0);
					if (z < 97) {
						FirstWorldAll = false;
					}
				}
				if (FirstWorldAll) {
					et.putInt("achievement_first_world_all", 1);
					et.commit();

				}

			}

		}

		// Only Increase Cups If it is a New HighScore
		if (score > PreScore) {
			// Save New HighScore
			et.putInt(HIGHSCORE_TAG + WlvlNumber, score);

			if (score >= 80 && score < 90) {
				CupCout = CupCout + CUPS[0];
			} else if (score >= 90 && score < 97) {
				CupCout = CupCout + CUPS[1];
			} else if (score >= 97 && score <= 100) {
				if (PreScore < 97) {
					int precount = information.getInt("achievement_3_stars", 0);
					et.putInt("achievement_3_stars", precount + 1);
					et.commit();
				}
				CupCout = CupCout + CUPS[2];
			}
		//	Server.SubmitCup(CupCout, android_id, et);
			if(soon)
				CupCout +=5;
			//Constants.ST.sendScore(Constants.UserName, Integer.toString(CupCout) /* Game Name*/,"ir.amulay.tabeta" /* Game Score*/);
			
		}
		// If This is Not A High Score So Set The Text To Tel User...
		else {
			CupGot.setText("کاپ جدید دریافت نشد!");
		}

		// Save New CupCount
		if(!Gotten){	
			Gotten = true;	
			CoinCout = CoinCout + 100;
			et.putInt(COIN_TAG, CoinCout);
			CoinGot.setText("100 سکـــه هم گرفتید!");
		}
		et.putInt(CUP_TAG, CupCout);
		et.commit();
		TotCups.setText("تعداد کل کاپ ها: " + information.getInt(CUP_TAG, 0)
				+ " ");
	}

	@Override
	public void onPause() {
		super.onPause();
		ReleaseSounds();

		mCurlView.setVisibility(View.GONE);

	}

	@Override
	public void onResume() {
		super.onResume();
		ReSetSounds();
		UpdateUI();
		if (mCurlView.getVisibility() == View.GONE) {
			mCurlView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handle.removeCallbacks(r);
		//handle1.removeCallbacks(runa);
		mCurlView.onPause();
		mCurlView.ClearMemory();

		// ImageView ivLogo = (ImageView) findViewById(R.id.gamebackimage);
		// ivLogo.setImageDrawable(null);
		//
		// ImageView ivLogo1 = (ImageView) findViewById(R.id.lvlname);
		// ivLogo1.setImageDrawable(null);
		//
		// ImageView ivLogo2 = (ImageView) findViewById(R.id.cupcount);
		// ivLogo2.setImageDrawable(null);
		//
		// ImageView ivLogo3 = (ImageView) findViewById(R.id.scorepanel);
		// ivLogo3.setImageDrawable(null);
		Aghrabe.setImageDrawable(null);
		Aghrabe.setImageResource(android.R.color.transparent);
		Limit.setImageDrawable(null);
		Limit.setImageResource(android.R.color.transparent);
		gamebackIm.setImageDrawable(null);
		gamebackIm.setImageResource(android.R.color.transparent);
		lvlnameIm.setImageDrawable(null);
		lvlnameIm.setImageResource(android.R.color.transparent);
		cupIm.setImageDrawable(null);
		cupIm.setImageResource(android.R.color.transparent);
		scoreIm.setImageDrawable(null);
		scoreIm.setImageResource(android.R.color.transparent);
		ReleaseSounds();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && mCurlView.getVisibility() == View.GONE) {
			mCurlView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Bitmap provider.
	 */
	private class PageProvider implements CurlView.PageProvider {

		@Override
		public void updatePage(CurlPage page, int width, int height) {

			// Bitmap front = loadBitmap(width, height, 0);
			// page.setTexture(front, CurlPage.SIDE_BOTH);
			Random r = new Random();
			int randomNum = r.nextInt(6);
			switch (randomNum) {
			case 0: {
				page.setColor(Color.argb(230, 10, 85, 230), CurlPage.SIDE_BOTH);
				break;
			}
			case 1: {
				page.setColor(Color.argb(230, 19, 142, 19), CurlPage.SIDE_BOTH);
				break;
			}
			case 2: {
				page.setColor(Color.argb(230, 51, 102, 0), CurlPage.SIDE_BOTH);
				break;
			}
			case 3: {
				page.setColor(Color.argb(230, 128, 0, 0), CurlPage.SIDE_BOTH);
				break;
			}
			case 4: {
				page.setColor(Color.argb(230, 64, 0, 128), CurlPage.SIDE_BOTH);
				break;
			}
			case 5: {
				page.setColor(Color.argb(230, 0, 102, 102), CurlPage.SIDE_BOTH);
				break;

			}
			}
		}

	}

	public void ShowVideo() {
		DeveloperInterface.getInstance(getApplicationContext()).showNewVideo(
				this, DeveloperInterface.TAPSELL_DIRECT_ADD_REQUEST_CODE, null,
				null);

	}

	// To Disable The Undo State From CurlView When The page is Initial
	public void SetUndoState(boolean state) {
		undo.setEnabled(state);
	}


	public void SetAghrabe(int count) {

		// iF Count equals zero, reset the timer
		if (count == 0) {
			UNFoldingSounds[1].start();
			final RotateAnimation rotateAnim = new RotateAnimation(FromDegree,
					-150f, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
					RotateAnimation.RELATIVE_TO_SELF, 0.6f);
			rotateAnim.setDuration(500);
			rotateAnim.setFillAfter(true);
			Aghrabe.startAnimation(rotateAnim);
			FromDegree = -150f;
			undo.setEnabled(false);
		} else if (count == 2) {
			UNFoldingSounds[0].start();
			final RotateAnimation rotateAnim = new RotateAnimation(FromDegree,
					FromDegree - 23f, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
					RotateAnimation.RELATIVE_TO_SELF, 0.6f);
			rotateAnim.setDuration(200);
			rotateAnim.setFillAfter(true);
			Aghrabe.startAnimation(rotateAnim);
			undo.setEnabled(true);
			FromDegree = FromDegree - 23f;
		} else {
			Random r = new Random();
			int x = r.nextInt(5);

			FoldingSounds[x].start();
			final RotateAnimation rotateAnim = new RotateAnimation(FromDegree,
					FromDegree + 23f, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
					RotateAnimation.RELATIVE_TO_SELF, 0.6f);
			rotateAnim.setDuration(200);
			rotateAnim.setFillAfter(true);
			Aghrabe.startAnimation(rotateAnim);
			FromDegree = FromDegree + 23f;
			undo.setEnabled(true);
		}
	}

	public void SetPreEnabled(boolean state) {
		HintPanel.findViewById(R.id.prehint).setEnabled(state);
	}

	public void SetUseEnabled(boolean state) {
		HintPanel.findViewById(R.id.usehint).setEnabled(state);
	}

	@Override
	public void onClick(View arg0) {
		//Log.e("Clicked", "Clicked");
		switch (arg0.getId()) {
		case R.id.undo: {
			SetAghrabe(2);
			mCurlView.curlback();
			break;
		}

		case R.id.hint: {
			TourActive = false;
			if(mTourGuideHandler != null)
			mTourGuideHandler.cleanUp();
			if (!HintPanel.isShowing()) {
				HintPanel.show();
			}
			UpdateUI();
			break; 
		}
		case R.id.review: {
//	        Intent intent = new Intent(Intent.ACTION_VIEW);
//	        intent.setPackage("ir.tgbs.android.iranapp");
//	        intent.setData(Uri.parse("http://iranapps.ir/app/"+ getString(R.string.package_name)+ "?a=comment&r=5"));
//			
	        
//	        String url= "myket://comment?id="+ getString(R.string.package_name);
//	        Intent intent = new Intent();
//	        intent.setAction(Intent.ACTION_VIEW);
//	        intent.setData(Uri.parse(url));
//	        startActivity(intent);
			
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setData(Uri.parse("bazaar://details?id="
					+ getString(R.string.package_name)));
			intent.setPackage("com.farsitel.bazaar");
			startActivity(intent);
			break;
		}
		case R.id.usehint: {
			UseHintSound.start(); 
			handle.postDelayed(new Runnable() {

				@Override
				public void run() {
					mCurlView.DoHint();

				}
			}, 500);

			SetUseEnabled(false);
			UpdateUI();
			HintPanel.dismiss();
			SetUseEnabled(true);
			break;
		}
		case R.id.prehint: {
			UseHintSound.start();
			handle.postDelayed(new Runnable() {

				@Override
				public void run() {
					mCurlView.DoHint();

				}
			}, 500);
			SetPreEnabled(false);
			UpdateUI();
			HintPanel.dismiss();
			SetPreEnabled(true);
			break;
		}
		case R.id.replay: {
			mCurlView.initial();
			dialog.findViewById(R.id.star1).setVisibility(View.GONE);
			dialog.findViewById(R.id.star2).setVisibility(View.GONE);
			dialog.findViewById(R.id.star3).setVisibility(View.GONE);
			dialog.dismiss();

			break;
		}
		case R.id.exit: {
			if(GetCoinPannel != null){
			if(GetCoinPannel.isShowing()){
				information.edit().putInt("RANGE", information.getInt("RANGE", 2)+1).commit();
				GetCoinPannel.dismiss();
				GetCoinPannel = null;
			}
			}
			if(HintPanel != null){
			if(HintPanel.isShowing()){
				HintPanel.dismiss();
			}
			}
			if(dialog != null){
			if(dialog.isShowing()){
			dialog.dismiss();
			star1.setImageDrawable(null);
			star1.setImageResource(android.R.color.transparent);
			star2.setImageDrawable(null);
			star2.setImageResource(android.R.color.transparent);
			star3.setImageDrawable(null);
			star3.setImageResource(android.R.color.transparent);
			}
			}
			break;
		}
		case R.id.getfreehint: {

			DeveloperInterface.getInstance(getApplicationContext())
					.checkCtaAvailability(getApplicationContext(), null, null,
							new CheckCtaAvailabilityResponseHandler() {
								@Override
								public void onResponse(Boolean isConnected,
										Boolean isAvailable) {
									if (isConnected && isAvailable) {
										// HintPanel.dismiss();
										EndTime = getNTPDate();
										ShowVideo();
									} else
										Toast.makeText(getApplicationContext(),
												R.string.VideoEror,
												Toast.LENGTH_SHORT).show();
								}
							});
			break;
		}
		case R.id.levelselect: {
			Intent it = new Intent(GameActivity.this, LevelChose.class);
			it.putExtra(W_TAG, World_Number);
			dialog.dismiss();
			startActivity(it);
			overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
			finish();
			break;
		}
		case R.id.nextlvl: {

			
			if (Score < 90) {
				Toast.makeText(
						getApplicationContext(),
						"باید حداقل 2 ستاره برای ورود به مرحله بعد به دست بیاورید!",
						Toast.LENGTH_LONG).show();
				break;
			} else {
				Intent it = new Intent(GameActivity.this, GameActivity.class);
				it.putExtra(W_TAG, World_Number);
				int newlvl = LvlNumber + 1;
				it.putExtra(L_TAG, newlvl);
				it.putExtra(WL_TAG,
						Integer.parseInt("" + World_Number + newlvl));
				dialog.dismiss();
				handle.removeCallbacks(r);
				//Causes Crash
				//dialog = null;
				stoped = true;
				startActivity(it);
				overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
				finish();
				break;
			}
		}
		case R.id.share: {
			mCurlView.StartPNG();
			String mPath = Environment.getExternalStorageDirectory().toString()
					+ "/" + "Share.jpg";
			File imageFile = new File(mPath);
			Uri uri = Uri.fromFile(imageFile);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("image/*");

			intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
			intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			try {
				startActivity(Intent.createChooser(intent, "Share Screenshot"));
			} catch (ActivityNotFoundException e) {
				Toast.makeText(getApplicationContext(), "No App Available",
						Toast.LENGTH_SHORT).show();
			}
			// takeScreenshot();
			break;
		}
		case R.id.camcomp: {
			mCurlView.StartPNG();
			String mPath = Environment.getExternalStorageDirectory().toString()
					+ "/" + "Share.jpg";
			File imageFile = new File(mPath);
			Uri uri = Uri.fromFile(imageFile);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("image/*");

			intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
			intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			try {
				startActivity(Intent.createChooser(intent, "Share Screenshot"));
			} catch (ActivityNotFoundException e) {
				Toast.makeText(getApplicationContext(), "No App Available",
						Toast.LENGTH_SHORT).show();
			}
			// takeScreenshot();
			break;
		}

		}
	}

	// private class InitDatabase extends AsyncTask<String, Void, String> {
	//
	// @Override
	// protected String doInBackground(String... urls) {
	// String response = "";
	// InternalDB db;
	// db = InternalDB.getInstance(GameActivity.this);
	// db.database();
	// mCurlView.Init(db, WlvlNumber, information, Vaahed,
	// GameActivity.this, CUPS);
	//
	// return response;
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// UpdateHintInfo();
	// }
	// }

	private void RunBuyCoin(){
		
		if(TourActive){
			return;
		}
		TourActive = true;
		Animation animation = new TranslateAnimation(0f, 0f, 200f, 0f);
		animation.setDuration(1000);
		animation.setFillAfter(true);
		animation.setInterpolator(new BounceInterpolator());

		ToolTip toolTip = new ToolTip()
		                    .setDescription("اگه بخوای میتونی این مرحله رو با کمک ما حلش کنی!")
		                    .setTextColor(Color.parseColor("#bdc3c7"))
		                    .setBackgroundColor(Color.parseColor("#e74c3c"))
		                    .setShadow(true)
		                    .setGravity(Gravity.TOP)
		                    .setEnterAnimation(mEnterAnimation);
		
		 mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
		            .setPointer(new Pointer())
		            .setToolTip(toolTip)
		            .setOverlay(new Overlay()
                    .setEnterAnimation(mEnterAnimation)
                    .setExitAnimation(mExitAnimation)
                    .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTourGuideHandler.cleanUp();
                            TourActive =false;
                        }
                    }))
		            .playOn(HintBt, tf);
	}
	private void runOverlay_ContinueMethod() {
		// the return handler is used to manipulate the cleanup of all the
		// tutorial elements
		ChainTourGuide welcome = ChainTourGuide
				.init(this)
				.setToolTip(
						new ToolTip()
								.setTitle("خوش آمدید!")
								.setDescription(
										"در چنــد مرحله کوتاه، روند بازی را در اینجا آموزش خواهید دید!"
												+ "هر مرحله یک نام دارد، آن نام اینجا نوشته می شود")
								.setGravity(Gravity.CENTER))
				// note that there is not Overlay here, so the default one will
				// be used
				.playLater(findViewById(R.id.lvlName));

		ChainTourGuide FoldoMeter = ChainTourGuide
				.init(this)
				.setToolTip(
						new ToolTip()
								.setTitle("تــا سنج!")
								.setDescription(
										"اینجا تعداد دفعاتی که میتوانید کاغذ را تا بزنید مشخص می شود! مثلا در این مرحله فقط باید با یک بار تا زدن کاغذ به شکل مشخص شده برسید!")
								.setGravity(Gravity.BOTTOM))
				// note that there is not Overlay here, so the default one will
				// be used
				.playLater(findViewById(R.id.limit));

		ChainTourGuide Hint = ChainTourGuide
				.init(this)
				.setToolTip(
						new ToolTip()
								.setTitle("هیـنت برای کمک به شما!")
								.setDescription(
										"اگر در مرحله ای گیر کردید، این دکمه به کمکتان می آید تا راه حل را پیدا کنید!")
								.setGravity(Gravity.BOTTOM | Gravity.START)
								.setBackgroundColor(Color.parseColor("#c0392b")))
				.setOverlay(
						new Overlay()
								.setBackgroundColor(
										Color.parseColor("#EE2c3e50"))
								.setEnterAnimation(mEnterAnimation)
								.setExitAnimation(mExitAnimation))
				.playLater(findViewById(R.id.hint));

		ChainTourGuide Undo = ChainTourGuide
				.init(this)
				.setToolTip(
						new ToolTip()
								.setTitle("برگرد عقب!")
								.setDescription(
										"اگر احساس کردید کاغذ را اشتباه تا زدید، این دکمه را فشار دهید تا به یک مرحله قبلتر برگردید!")
								.setGravity(Gravity.TOP))
				.setOverlay(
						new Overlay()
								.setBackgroundColor(
										Color.parseColor("#EE2c3e50"))
								.setEnterAnimation(mEnterAnimation)
								.setExitAnimation(mExitAnimation))
				// note that there is not Overlay here, so the default one will
				// be used
				.playLater(findViewById(R.id.undo));

		ChainTourGuide share = ChainTourGuide
				.init(this)
				.setToolTip(
						new ToolTip()
								.setTitle("به دوستات نشون بده!")
								.setDescription(
										"اگر خواستید میتوانید با این دکمه از کاغذ عکس بگیرید و آن را به دوستانتان نشان دهید یا از آنها کمک بگیرید!")
								.setGravity(Gravity.TOP))
				.setOverlay(
						new Overlay()
								.setBackgroundColor(
										Color.parseColor("#EE2c3e50"))
								.setEnterAnimation(mEnterAnimation)
								.setExitAnimation(mExitAnimation))
				// note that there is not Overlay here, so the default one will
				// be used
				.playLater(findViewById(R.id.share));

		ChainTourGuide Percent = ChainTourGuide
				.init(this)
				.setToolTip(
						new ToolTip()
								.setTitle("درصد امتیاز")
								.setDescription(
										"اینجا نشان داده می شود که چند درصد به شکل نهایی نزدیک شده اید، هرچه این درصد به 100 نزدیک تر شود، امتیازی که کسب می کنید بیشتر خواهد بود!")
								.setGravity(Gravity.TOP))
				.setOverlay(
						new Overlay()
								.setBackgroundColor(
										Color.parseColor("#EE2c3e50"))
								.setEnterAnimation(mEnterAnimation)
								.setExitAnimation(mExitAnimation))
				// note that there is not Overlay here, so the default one will
				// be used
				.playLater(findViewById(R.id.score));

		ChainTourGuide StartFold = ChainTourGuide
				.init(this)
				.setToolTip(
						new ToolTip()
								.setTitle("شروع کنید!")
								.setDescription(
										"شروع کنید! دستتان را بر روی کاغذ بگذارید و بکشید تا به شکلِ نهایی برسید! سعی کنید حداقل 80 درصد کسب کنید تا مجوز شروع بازی برایتان صادر شود.")
								.setGravity(Gravity.END))
				.setOverlay(
						new Overlay()
								.setBackgroundColor(
										Color.parseColor("#EE2c3e50"))
								.setEnterAnimation(mEnterAnimation)
								.setExitAnimation(mExitAnimation))
				// note that there is not Overlay here, so the default one will
				// be used
				.playLater(findViewById(R.id.textView1));

		Sequence sequence = new Sequence.SequenceBuilder()
				.add(welcome, FoldoMeter, Hint, Undo, share, Percent, StartFold)
				.setDefaultOverlay(
						new Overlay().setEnterAnimation(mEnterAnimation)
								.setExitAnimation(mExitAnimation))
				.setDefaultPointer(null)
				.setContinueMethod(Sequence.ContinueMethod.Overlay).build();
		tf = Typeface.createFromAsset(getAssets(), "fonts/Yekan.ttf");
		ChainTourGuide.init(this).playInSequence(sequence, tf);

	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if(!undo.isEnabled()){
			Intent it = new Intent(GameActivity.this, LevelChose.class);
			// it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			it.putExtra(W_TAG, World_Number);
			startActivity(it);
			overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
			finish();
			}
			else{
				SetAghrabe(2);
				mCurlView.curlback();
			}
		}
		return true;
	}

}