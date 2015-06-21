package fitlibraryGeneric.specify.genericFinder;

import java.lang.reflect.Type;

import fitlibrary.traverse.DomainAdapter;

public class GenericFinderException implements DomainAdapter {
	private Pair<Integer,Integer> integerIntegerPair;
	
	public Pair<Integer,Integer> getIntegerIntegerPair() {
		return integerIntegerPair;
	}
	public void setIntegerIntegerPair(Pair<Integer,Integer> pair) {
		this.integerIntegerPair = pair;
	}
	public Pair findPair(String key, Type type) { 
		throw new RuntimeException();
	}
	public Object getSystemUnderTest() {
		return null;
	}
}