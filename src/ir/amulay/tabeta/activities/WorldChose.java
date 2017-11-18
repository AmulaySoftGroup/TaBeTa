package ir.amulay.tabeta.activities;

import com.squareup.picasso.Picasso;

import ir.amulay.tabeta.R;
import ir.amulay.tabeta.globals.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WorldChose extends Activity implements OnClickListener {
	SharedPreferences information;
	private static String Sh_P_Tag = Constants.SH_PREF_TAG;
	private static String W_TAG = Constants.W_TAG;
	private boolean[] WorldCheck;
	TextView worldname;
	ViewPager viewPager;
	ImagePagerAdapter adapter;
	GestureDetector tapGestureDetector;
	ImageView ivLogo,ivLogo1,imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.worldchose);
	//	hideSystemUI();
		information = getSharedPreferences(Sh_P_Tag, MODE_PRIVATE);
		findViewById(R.id.nextbutton).setOnClickListener(this);
		findViewById(R.id.prevbutton).setOnClickListener(this);
		findViewById(R.id.enterworld).setOnClickListener(this);
		WorldCheck = new boolean[8];
		worldname = (TextView) findViewById(R.id.worldname);

		Typeface tf;
		tf = Typeface.createFromAsset(getAssets(), "fonts/Dastnevis.ttf");
		worldname.setTypeface(tf);
		worldname.setText("دنیای 1: خرگوش!");

		ivLogo = (ImageView) findViewById(R.id.wrdbackimage);
		Picasso.with(getApplicationContext()).load(R.drawable.background)
				.into(ivLogo);
		ivLogo1 = (ImageView) findViewById(R.id.imageView1);
		Picasso.with(getApplicationContext()).load(R.drawable.worldselect)
				.into(ivLogo1);

		viewPager = (ViewPager) findViewById(R.id.view_pager);
		adapter = new ImagePagerAdapter(information);
		viewPager.setAdapter(adapter);
		viewPager.setPadding(5, 5, 5, 5);
		viewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (arg0 <= 0) {
					findViewById(R.id.prevbutton).setEnabled(false);
					findViewById(R.id.nextbutton).setEnabled(true);
				} else if (arg0 > 0 && arg0 < adapter.getCount() - 1) {
					findViewById(R.id.prevbutton).setEnabled(true);
					findViewById(R.id.nextbutton).setEnabled(true);
				} else {
					findViewById(R.id.prevbutton).setEnabled(true);
					findViewById(R.id.nextbutton).setEnabled(false);
				}
				switch (arg0) {
				case 0: {
					worldname.setText("دنیای 1: خرگوش!");
					break;
				}
				case 1: {
					worldname.setText("دنیای 2: ذرت!");
					break;
				}

				case 2: {
					worldname.setText("دنیای 3: قو!");
					break;
				}
				case 3: {
					worldname.setText("دنیای 4:  اسب!");
					break;
				}
				case 4: {
					worldname.setText("دنیای 5: بنا!");
					break;
				}
				case 5: {
					worldname.setText("دنیای 6: گل ها!");
					break;
				}
				case 6: {
					worldname.setText("دنیای 7 : فیـــل!");
					break;
				}
				case 7: {
					worldname.setText("به زودی...");
					break;
				}

				}

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		// Scroll To The fiNal Opened World
		for (int i = 5; i >= 1; i--) {
			if (information.getInt(Constants.HIGHSCORE_TAG + i + 15, 0) >= 90) {
				viewPager.setCurrentItem(i, true);
				break;
			}
		}

		tapGestureDetector = new GestureDetector(this, new TapGestureListener());

		viewPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				tapGestureDetector.onTouchEvent(arg1);
				return false;
			}
		});


	}


	public void OnStop(){
		super.onStop();

	}
	public void OnDestroy(){
		super.onDestroy();
		viewPager.setAdapter(null);
		ivLogo.setImageDrawable(null);
		ivLogo.setImageResource(android.R.color.transparent);
		ivLogo1.setImageDrawable(null);
		ivLogo1.setImageResource(android.R.color.transparent);
		imageView.setImageDrawable(null);
		imageView.setImageResource(android.R.color.transparent);
		java.lang.System.gc();
	}

	private class ImagePagerAdapter extends PagerAdapter {
		SharedPreferences information;

		private int[] mImages = new int[] { R.drawable.world1,
				R.drawable.world2, R.drawable.world3, R.drawable.world4,
				R.drawable.world5, R.drawable.world6,R.drawable.world7, R.drawable.soon };
		private int[] lImages = new int[] { R.drawable.world1_lock,
				R.drawable.world2_lock, R.drawable.world3_lock,
				R.drawable.world4_lock, R.drawable.world5_lock,R.drawable.world6_lock,
				R.drawable.world7_lock };

		public ImagePagerAdapter(SharedPreferences information) {
			this.information = information;
		}

		@Override
		public int getCount() {
			return mImages.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((ImageView) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Context context = WorldChose.this;
			imageView = new ImageView(context);
			// imageView.getScaleType();
			// imageView.setScaleType(ScaleType.CENTER_INSIDE);

			if (position == 0) {
				Picasso.with(getApplicationContext()).load(mImages[position])
						.into(imageView);
				WorldCheck[position] = true;
			} else if (position > 0
					&& information.getInt(Constants.HIGHSCORE_TAG + position
							+ 15, 0) >= 90) {
				Picasso.with(getApplicationContext()).load(mImages[position])
						.into(imageView);
				WorldCheck[position] = true;
			} else {
				if (position != 7 ) {
					Picasso.with(getApplicationContext())
							.load(mImages[position]).into(imageView);
					WorldCheck[position] = true;
				}
			}

			if (position == 7) {
				Picasso.with(getApplicationContext()).load(mImages[position])
						.into(imageView);
				WorldCheck[position] = false;
			}
			((ViewPager) container).addView(imageView, 0);

			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {

		case R.id.nextbutton: {
			if (viewPager.getCurrentItem() <= adapter.getCount() + 1)
				viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
			break;
		}
		case R.id.prevbutton: {
			if (viewPager.getCurrentItem() > 0)
				viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
			break;
		}
		case R.id.enterworld: {
			LoadLevelSelect();
			break;
		}

		}

	}

	public void LoadLevelSelect() {
		if (WorldCheck[viewPager.getCurrentItem()] == true) {
			Intent it = new Intent(WorldChose.this, LevelChose.class);
			it.putExtra(W_TAG, viewPager.getCurrentItem() + 1);
			startActivity(it);
			overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
			finish();
		} else {
			Toast.makeText(getApplicationContext(),
					"برای باز شدن این دنیا؛ دنیای قبلی را تکمیل کنید!",
					Toast.LENGTH_SHORT).show();
		}
	}

	class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			LoadLevelSelect();
			return true;
		}
	}

}