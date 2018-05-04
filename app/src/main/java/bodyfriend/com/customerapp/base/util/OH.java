package bodyfriend.com.customerapp.base.util;

import java.util.Observable;

public class OH extends Observable {

	public enum TYPE {
		LOGIN
		, EXIT
		, FIND_ID
		;

		public int what;
		public int arg1;
		public int arg2;
		public Object obj;
		public TYPE obtain(int what, int arg1, int arg2, Object obj) {
			this.what = what;
			this.arg1 = arg1;
			this.arg2 = arg2;
			this.obj = obj;
			return this;
		}
		public TYPE obtain(int what) {
			this.what = what;
			return this;
		}
		public TYPE obtain(int what, int arg1) {
			this.what = what;
			this.arg1 = arg1;
			return this;
		}
		public TYPE obtain(int what, int arg1, int arg2) {
			this.what = what;
			this.arg1 = arg1;
			this.arg2 = arg2;
			return this;
		}
		public TYPE obtain(int what, Object obj) {
			this.what = what;
			return this;
		}
		public TYPE obtain(int what, int arg1, Object obj) {
			this.what = what;
			this.arg1 = arg1;
			this.obj = obj;
			return this;
		}
	};

	private OH() {
	}

	private static OH INSTANCE = new OH();

	public static OH c() {
		return INSTANCE;
	}

	@Override
	public void notifyObservers(Object data) {
		setChanged();
		super.notifyObservers(data);
	}

//	@Override
//	public void addObserver(Observer observer) {
//		Log.l(observer.getClass());
//		super.addObserver(observer);
//	}
//
//	@Override
//	public synchronized void deleteObserver(Observer observer) {
//		Log.l(observer.getClass());
//		super.deleteObserver(observer);
//	}
}
