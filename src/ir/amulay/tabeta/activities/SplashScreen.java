package ir.amulay.tabeta.activities;


import com.squareup.picasso.Picasso;

import ir.amulay.tabeta.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashScreen extends Activity {
	final Handler handle = new Handler(); 
	ImageView ivLogo ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.splashscreen);
		ivLogo = (ImageView) findViewById(R.id.imageView1);
		Picasso.with(getApplicationContext()).load(R.drawable.splashimage)
				.into(ivLogo);
		
		handle.postDelayed(new Runnable() {
 
			@Override
			public void run() {				
				Intent it = new Intent(SplashScreen.this, WorldChose.class);
				startActivity(it); 
				overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
				((BitmapDrawable)ivLogo.getDrawable()).getBitmap().recycle();
				java.lang.System.gc();
			}
		}, 4000);
	}

}
