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


package com.tobias.vocabulary_trainer;


public class DotsList
{
Dot first;
	
	public DotsList()
	{
		first = null;
	}
	
	public boolean isEmpty()
	{
		return (first == null);
	}
	
	public void vorneEinfuegen(int x, int y, boolean zeichneLinie)
	{
		Dot newDot = new Dot(x, y, zeichneLinie);
		newDot.follower = first;
		first = newDot;
	}
}