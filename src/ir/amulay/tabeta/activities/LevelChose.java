package ir.amulay.tabeta.activities;

import com.squareup.picasso.Picasso;

import ir.amulay.tabeta.R;
import ir.amulay.tabeta.globals.Constants;
import ir.amulay.tabeta.globals.LevelChooseAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class LevelChose extends Activity {

	private String W_TAG = Constants.W_TAG;
	private String Sh_P_Tag = Constants.SH_PREF_TAG;
	GridView grid;
	TextView worldname;
	SharedPreferences information;
	int WorldNum;
	ImageView ivLogo,ivLogo1;
	LevelChooseAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		setContentView(R.layout.levelchose);
		//hideSystemUI();
		WorldNum = getIntent().getExtras().getInt(W_TAG, 1);
		grid = (GridView) findViewById(R.id.gridView1);
		information = getSharedPreferences(Sh_P_Tag, MODE_PRIVATE);
		adapter = new LevelChooseAdapter(this, information, WorldNum);
		grid.setAdapter(adapter);

		worldname = (TextView) findViewById(R.id.worldname);

		
		ivLogo = (ImageView) findViewById(R.id.lvlbackimage);
		Picasso.with(getApplicationContext())
		   .load(R.drawable.background)
		   .fit()
		   .centerCrop()
		   .into(ivLogo);
		
		ivLogo1 = (ImageView) findViewById(R.id.select);
		Picasso.with(getApplicationContext()).load(R.drawable.levelselect)
				.into(ivLogo1);
		 
		
		Typeface tf;
		tf = Typeface.createFromAsset(getAssets(), "fonts/Dastnevis.ttf");
		worldname.setTypeface(tf);
		worldname.setText("دنیای " + WorldNum);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	public void OnDestroy(){
		
		super.onDestroy();
		grid.setAdapter(null);
		ivLogo.setImageDrawable(null);
		ivLogo.setImageResource(android.R.color.transparent);
		ivLogo1.setImageDrawable(null);
		ivLogo1.setImageResource(android.R.color.transparent);
		java.lang.System.gc();
	}

	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if ((keyCode == KeyEvent.KEYCODE_BACK)) {
		Intent it = new Intent(LevelChose.this, WorldChose.class);
		//it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(it);
		overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
		finish();
	}
	return true;
	}



}
