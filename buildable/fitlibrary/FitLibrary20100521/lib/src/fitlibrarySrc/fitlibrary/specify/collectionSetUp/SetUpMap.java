/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 24/08/2006
*/

package fitlibrary.specify.collectionSetUp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fitlibrary.object.DomainFixtured;
import fitlibrary.specify.eg.User;

@SuppressWarnings("unchecked")
public class SetUpMap implements DomainFixtured {
	private Map iOUMap = new HashMap();
	
	public User nameOwe(String name, double owe) {
		return new User(name,owe);
	}
	public Map getIOUMap() {
		return iOUMap;
	}
	public void iOUMap(List users) {
		for (Iterator it = users.iterator(); it.hasNext(); ) {
			User user = (User)it.next();
			iOUMap.put(user.getName(), user);
		}
	}
}
