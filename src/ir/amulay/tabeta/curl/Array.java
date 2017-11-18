package ir.amulay.tabeta.curl;


/**
 * Simple fixed size array implementation.
 */
public class Array<T> {
	private Object[] mArray;
	private int mCapacity;
	private int mSize;

	public Array(int capacity) {
		mCapacity = capacity;
		mArray = new Object[capacity];
	}

	public void add(int index, T item) {
		if (index < 0 || index > mSize || mSize >= mCapacity) {
			//Log.d("EROOR ADDINGs", "EROOR ADDING");
			throw new IndexOutOfBoundsException();
		}
		for (int i = mSize; i > index; --i) {
			mArray[i] = mArray[i - 1];
		}
		mArray[index] = item;
		++mSize;
	}

	public void add(T item) {
		if (mSize >= mCapacity) {
			//Log.d("EROOR ADDINGs", "EROOR ADDING");
			throw new IndexOutOfBoundsException();
		}
		mArray[mSize++] = item;
	}

	public void addAll(Array<T> array) {
		if (mSize + array.size() > mCapacity) {
			throw new IndexOutOfBoundsException();
		}
		for (int i = 0; i < array.size(); ++i) {
			mArray[mSize++] = array.get(i);
		}
	}

	public void clear() {
		mSize = 0;
	}

	@SuppressWarnings("unchecked")
	public T get(int index) {
		if (index < 0 || index >= mSize) {
			throw new IndexOutOfBoundsException();
		}
		return (T) mArray[index];
	}

	@SuppressWarnings("unchecked")
	public T remove(int index) {
		if (index < 0 || index >= mSize) {
			throw new IndexOutOfBoundsException();
		}
		T item = (T) mArray[index];
		for (int i = index; i < mSize - 1; ++i) {
			mArray[i] = mArray[i + 1];
		}
		--mSize;
		return item;
	}

	public int size() {
		return mSize;
	}

}