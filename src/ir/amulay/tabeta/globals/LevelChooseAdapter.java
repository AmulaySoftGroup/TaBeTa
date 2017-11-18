package ir.amulay.tabeta.globals;

import java.util.HashMap;
import java.util.Map;

import ir.amulay.tabeta.R;
import ir.amulay.tabeta.activities.GameActivity;
import ir.amulay.tabeta.activities.LevelChose;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LevelChooseAdapter extends BaseAdapter {

	Context context;
	int[] imageId;
	private String HIGHSCORE_TAG = Constants.HIGHSCORE_TAG;
	private String WL_TAG = Constants.WL_TAG;
	private String L_TAG = Constants.L_TAG;
	private String W_TAG = Constants.W_TAG;
	SharedPreferences information;
	private int WorldNumber;
	private Map<Integer, View> myViews = new HashMap<Integer, View>();
	
	
	public LevelChooseAdapter(LevelChose mainActivity,
			SharedPreferences information, int WorldNum) {
		// TODO Auto-generated constructor stub
		// result=prgmNameList;
		context = mainActivity;
		this.information = information;
		// imageId=prgmImages;

		WorldNumber = WorldNum;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		// Each World Has 15 Levels...
		return 15;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}




	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// Holder holder = new Holder();
		// View rowView;
		// ViewHolder holder = new ViewHolder();
		View view = myViews.get(position);
		if (view == null) {
			//this Should Be false in order to disable player ability to play all levels
			//Whitout checking previous level
			boolean Playable = true;
			final int lvlNumber = position + 1;

			int PreScore = information.getInt(HIGHSCORE_TAG + WorldNumber
					+ lvlNumber, 0);
			LayoutInflater ltInflate = (LayoutInflater) context
				 	.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = ltInflate.inflate(R.layout.levelitem, null);
			TextView LvlName = (TextView) view.findViewById(R.id.levelnumber);
			ImageView Star1 = (ImageView) view.findViewById(R.id.star1);
			ImageView Star2 = (ImageView) view.findViewById(R.id.star2);
			ImageView Star3 = (ImageView) view.findViewById(R.id.star3);
			View StarPannel = view.findViewById(R.id.starspanel);
			View Lock = view.findViewById(R.id.lock);
			
			//this will Always turn the levels On
			StarPannel.setVisibility(View.VISIBLE);
			Lock.setVisibility(View.GONE);
			
			if (PreScore > 0) {
				Playable = true;
				StarPannel.setVisibility(View.VISIBLE);
				//Lock.setVisibility(View.GONE);
				if (PreScore >= 80 && PreScore < 90) {
					Star1.setVisibility(View.VISIBLE);
					Star2.setVisibility(View.GONE);
					Star3.setVisibility(View.GONE);
				} else if (PreScore >= 90 && PreScore < 97) {
					Star1.setVisibility(View.VISIBLE);
					Star2.setVisibility(View.VISIBLE);
					Star3.setVisibility(View.GONE);
				} else if (PreScore >= 97 && PreScore <= 100) {
					Star1.setVisibility(View.VISIBLE);
					Star2.setVisibility(View.VISIBLE);
					Star3.setVisibility(View.VISIBLE);
				}

			} 
//			else {
//				if (lvlNumber > 1) {
//					int prelvlNumber = lvlNumber - 1;
//					int PrelvlScore = information.getInt(HIGHSCORE_TAG
//							+ WorldNumber + prelvlNumber, 0);
//
//					if (PrelvlScore >= 90) {
//						Playable = true;
//						StarPannel.setVisibility(View.VISIBLE);
//						Lock.setVisibility(View.GONE);
//					}
//				} else {
//					StarPannel.setVisibility(View.VISIBLE);
//					Lock.setVisibility(View.GONE);
//					Playable = true;
//				}
//
//			}
			
			Typeface tf;
			tf = Typeface.createFromAsset(context.getAssets(), "fonts/ORIGAMI.TTF");
			LvlName.setTypeface(tf);
			LvlName.setText("Level: " + lvlNumber);
			
			if (!Playable) {
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(context,
								"باید حداقل 2 ستاره از مرحله قبل داشته باشید!",
								Toast.LENGTH_LONG).show();

					}
				});
			} else {
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent it = new Intent(context, GameActivity.class);
						it.putExtra(WL_TAG,
								Integer.parseInt("" + WorldNumber + lvlNumber));
						it.putExtra(L_TAG, Integer.parseInt("" + lvlNumber));
						it.putExtra(W_TAG, Integer.parseInt("" + WorldNumber));
						context.startActivity(it);
						((Activity) context).overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
						((Activity) context).finish();

					}
				});
			}
			
			myViews.put(position, view);
		}
		 return view;
	}

}
