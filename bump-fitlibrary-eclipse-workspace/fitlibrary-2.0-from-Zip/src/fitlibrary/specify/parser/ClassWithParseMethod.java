/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.specify.parser;

import fitlibrary.ArrayFixture;
import fitlibrary.DoFixture;

public class ClassWithParseMethod extends DoFixture {
	public ArrayFixture aValueSetWithItsOwnParseMethod() {
		MyValueHolder[] values = {new MyValueHolder(new MyValue(1)),
				new MyValueHolder(new MyValue(2))};
		return new ArrayFixture(values);
	}
	public static class MyValue {
		private int i;

		public MyValue(int i) {
			this.i = i;
		}
		@Override
		public String toString() {
			return ""+i;
		}
		@Override
		public boolean equals(Object other) {
			if (!(other instanceof MyValue))
				return false;
			return i == ((MyValue)other).i;
		}
		@Override
		public int hashCode() {
			return super.hashCode();
		}
		public static MyValue parse(String s) {
			return new MyValue(Integer.parseInt(s));
	    }		
	}
	public static class MyValueHolder {
		public MyValue value;

		public MyValueHolder(MyValue value) {
			this.value = value;
		}
		public MyValue getValue() {
			return value;
		}
	}
}
