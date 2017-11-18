package ir.amulay.tabeta.curl;

public class CurlStack {
	private StackItem[] items;
	private int top;
	private int MaxItems;

	CurlStack(int MaxItems) {
		top = -1;
		this.MaxItems = MaxItems;
		items = new StackItem[MaxItems];
	}

	/**
	 * Ads An Item To the top Of the Stack
	 * 
	 * @param item
	 * @return
	 */
	public boolean push(StackItem item) {
		// Log.d("PUSH", "Push Called");
		if (IsFull())
			return false;
		else {
			items[++top] = new StackItem(item);
			// for(int i = 0; i<=top;i++){
			// Log.d("SourceItemS", ""+items[i].getAnimationSource().x
			// +" -And- "+items[i].getAnimationSource().y);
			// Log.d("TargetItemS", ""+items[i].getAnimationTarget().x
			// +" -And- "+items[i].getAnimationTarget().y);
			// }
			return true;
		}

	}

	public void clear() {
		top = -1;
	}
	public int size(){
		return top+1;
	}
	public int getTexture(int index){
		return items[index].getTextureID();

	}

	/**
	 * Removes An Element Of the Stack And Returns It
	 * 
	 * @return
	 */
	public StackItem pop() {
		if (IsEmpty()) {
			//Log.d("Stack", "Stack Is Empty");
			return null;
		} else
			return items[top--];
	}

	/**
	 * Returns The Top Element Of Stack Whitout Removing It
	 * 
	 * @return
	 */
	public StackItem top() {
		if (IsEmpty()) {
			//Log.d("Stack", "Stack Is Empty");
			return null;
		} else
			return items[top];
	}

	/**
	 * method to Cheak IS Stack Full Or Not
	 * 
	 * @return
	 */
	public boolean IsFull() {
		if (top > MaxItems)
			return true;
		else
			return false;
	}

	public boolean IsEmpty() {
		if (top == -1)
			return true;
		else
			return false;
	}
}
