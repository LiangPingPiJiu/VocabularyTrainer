//[Vocabulary Trainer]
//Copyright (C) [2012]  [Tobias Endrikat]
//
//This program is free software;
//you can redistribute it and/or modify it under the terms of the
//GNU General Public License as published by the Free Software Foundation;
//either version 3 of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
//or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, see <http://www.gnu.org/licenses/>.


package com.tobias.vocabulary_trainer.utils;

import java.util.List;
import java.util.Vector;

public class Conversion {
	
	public static Vector<Integer> toVector(List<Integer> list) {
		Vector<Integer> v;
		
		v = new Vector<Integer>();
		if (list != null) {
			for (Integer i : list) {
				v.add(i);
			}
		}
		
		return v;
	}
	
	public static String[] toStringArray(Vector<Integer> v) {
		String[] l;
		int ii;
		
		l = new String[v.size()];
		ii = 0;
		if (v != null) {
			for (Integer i : v) {
				if (i != null) {
					l[ii] = i.toString();
				}
				else {
					l[ii] = new String("");
				}
				ii++;
			}
		}
		
		return l;
	}
}
