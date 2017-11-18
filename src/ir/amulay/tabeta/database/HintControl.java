package ir.amulay.tabeta.database;

import ir.amulay.tabeta.database.InternalDB;

import java.util.ArrayList;

import android.graphics.PointF;
import android.util.Log;

public class HintControl {
	int HintCount;
	
	ArrayList<PointF> CurlPos = new ArrayList<PointF>();
	ArrayList<PointF> CurlDir = new ArrayList<PointF>();
	
	
	public HintControl(InternalDB db,int x) {
db.open();
		HintCount = db.GetHintCount(x);
		
		for(int i =0;i<HintCount;i++){
			CurlPos.add(new PointF((float)db.GetCurPosX(x, i), (float)db.GetCurPosY(x, i)));
			CurlDir.add(new PointF((float)db.GetCurlDirX(x, i), (float)db.GetCurlDirY(x, i)));
		}
		db.close();
	}

	/**
	 * get The Hint Count Number
	 * @return
	 */
	public int GetHitCount(){
		return HintCount;
	}
	
	/**
	 * Retruns The Xth Value OF Hint CourlPos
	 * @param x
	 * @return
	 */
	public PointF GetCurlPos(int x){
		if(x<HintCount){
			return CurlPos.get(x);
		}
		else{
			Log.e("OverFlow", "CurlPosIndex Out OF Bound");
			return null;
		}
	}
	
	/**
	 * Returns the Xth Value OF Hint CurlDir
	 * @param x
	 * @return
	 */
	public PointF GetCurlDir(int x){
		if(x<HintCount){
			return CurlDir.get(x);
		}
		else{
			Log.e("OverFlow", "CurlDirIndex Out OF Bound");
			return null;
		}
	}
	
}
