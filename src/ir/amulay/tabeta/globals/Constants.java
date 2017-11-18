package ir.amulay.tabeta.globals;


public  class Constants {
	// chand darsad bayad beshe hadeAghal ke 100 mahsoob beshe
	public static final int ScoreLimit = 97;
	public static final boolean DEBUG = false;
	public static final String DBPath = "data/data/ir.amulay.tabeta/databases/";
	public static final String DBName = "LevelDva";
	public static final String PreDBName = "Remade";

	public static final String FIRST_BOX = "3taee";
	public static final String SECOND_BOX = "5Taee";
	public static final String THIRD_BOX = "8taee";
	public static final String AdRemove = "adremove";

//	public static String UserName = "";
//	public static ScoreTransmitter ST ;
	// Shared Perferenses Tags...
	public static final String SH_PREF_TAG = "Information";
	public static final String COIN_TAG = "CoinCount";
	public static final String CUP_TAG = "CupCount";
	public static final String PRE_HINT_TAG = "PreHintCount";
	public static final String HIGHSCORE_TAG = "HighScore";//Note That This Will Be Numbrized For Each Level By WOrldLevel On Its front..
	public static final String LVL_HINT_COUNT_TAG = "LvlHintCount";//NOte That This Will Be Numbrized For Each Level By WOrldLevel On Its front..
	public static final String ENDTIME_TAG = "EndTime";
	// Shared Perferenses Tags...
	
	
	//Intent TAGS
	public static final String W_TAG = "World";//From World Choose To Level Choose
	public static final String WL_TAG = "WorldLevel";//From level Choose To Game Activity
	public static final String L_TAG = "Level";//From level Choose To Game Activity
	//Intent TAGS
	
	
	public static final int RC_PURCHASE_REQUEST = 10001;
	public static final String LOG_IAB = "TAGHASTAM";
	// IAB error
	public static final int[] CUPS = { 1, 4, 12 };
	// Each Vaahed Coin ---> 1 Hint
	public static final int Vaahed = 500;

	public static final int TapselCoinAward = 1000;
	public static final int TapselCupAward = 10;

	// Hours Till User can Watch A Add to Achive the Award Again
	public static final int VideoWaiting = 4;
	public static final int VideoAward = 500;

	public static final int CoinFirstValue = 1500;
	public static final int[] ShopCoins = { 2500, 5000, 25000 };
	public static final int IAB_PURCHASE_FAILED = 101;
	public static final int IAB_PURCHASE_FAILED_PAYLOAD_PROBLEM = 102;
}
