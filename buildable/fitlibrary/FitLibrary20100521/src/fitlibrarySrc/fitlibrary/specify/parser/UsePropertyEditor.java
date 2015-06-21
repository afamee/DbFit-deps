package fitlibrary.specify.parser;

import java.beans.PropertyEditorSupport;

import fitlibrary.traverse.FitLibrarySelector;
import fitlibrary.traverse.Traverse;

public class UsePropertyEditor {
	public MyPoint aPoint(MyPoint point) {
		return point;
	}
	public Traverse aPointAsDomainObject(MyPoint point) {
		return FitLibrarySelector.selectDomainCheck(point);
	}
	public static class MyPoint {
		private int x, y;

		public MyPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		@Override
		public boolean equals(Object object) {
			if (!(object instanceof MyPoint))
				return false;
			MyPoint other = (MyPoint) object;
			return x == other.x && y == other.y;
		}
		@Override
		public int hashCode() {
			return x+y;
		}
		@Override
		public String toString() {
			return "("+x+","+y+")";
		}
	}
	public static class MyPointEditor extends PropertyEditorSupport {
		@Override
		public void setAsText(String s) throws IllegalArgumentException {
			if (!s.startsWith("(") || !s.endsWith(")"))
				throw new RuntimeException("Badly formatted point");
			int comma = s.indexOf(",");
			if (comma < 0)
				throw new RuntimeException("Badly formatted point");
			int x = Integer.parseInt(s.substring(1,comma));
			int y = Integer.parseInt(s.substring(comma+1,s.length()-1));
			setValue(new MyPoint(x,y));
		}
		@Override
		public String getAsText() {
			MyPoint value = (MyPoint) getValue();
			return "["+value.getX()+","+value.getY()+"]";
		}
	}
}
