package fitbook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fitbook.chat.ChatServer;
import fitbook.chat.Room;
import fitbook.chat.User;

/*
 * @author Rick Mugridge 6/12/2003
 *
 * Copyright (c) 2003 Rick Mugridge, University of Auckland, NZ
 * Released under the terms of the GNU General Public License version 2 or later.
 *
 */

@SuppressWarnings("unchecked")
public class OccupantListInRoom extends fit.RowFixture { //COPY:ALL
	private ChatServer chat = new ChatServer(); //COPY:ALL
	//COPY:ALL
	@Override
	public Object[] query() throws Exception { //COPY:ALL
		List occupancies = new ArrayList(); //COPY:ALL
		collectOccupants(occupancies,chat.room(getArgs()[0])); //COPY:ALL
		return occupancies.toArray(); //COPY:ALL
	} //COPY:ALL
	@Override
	public Class<?> getTargetClass() { //COPY:ALL
		return Occupancy.class; //COPY:ALL
	} //COPY:ALL
	private void collectOccupants(List occupancies, Room room) { //COPY:ALL
		for (Iterator it = room.users(); it.hasNext(); ) { //COPY:ALL
			User user = (User)it.next(); //COPY:ALL
			Occupancy occupant = new Occupancy(room.getName(), //COPY:ALL
			                                   user.getName()); //COPY:ALL
			occupancies.add(occupant);//COPY:ALL
		} //COPY:ALL
	} //COPY:ALL
} //COPY:ALL
