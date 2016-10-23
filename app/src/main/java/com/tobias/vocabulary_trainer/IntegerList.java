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


public class IntegerList
{
	public IntegerListElement first;
	public IntegerList()
	{
		first = null;
	}
	public void addAtBegin(int n)
	{
		IntegerListElement integerListElement = new IntegerListElement(n);
		integerListElement.follower = first;
		first = integerListElement;
	}
	public int count()
	{
		int counter = 0;
		IntegerListElement integerListElement = first;
		while(integerListElement != null)
		{
			counter++;
			integerListElement = integerListElement.follower;
		}
		return counter;
	}
	public boolean delete(int n)
	{
		IntegerListElement forerunner;
		IntegerListElement actual;
		if(first.n == n)
		{
			first = first.follower;
			return true;
		}
		else
		{
			forerunner = first;
			actual = first.follower;
			while(actual.n != n)
			{
				forerunner = actual;
				if(actual.follower == null)
				{
					return false;
				}
				else
				{
					actual = actual.follower;
				}
			}
			forerunner.follower = actual.follower;
			return true;
		}
	}
	public boolean contains(int n)
	{
		IntegerListElement actual;
		actual = first;
		while(actual.n != n )
		{
			if(actual.follower == null)
			{
				return false;
			}
			else
			{
				actual = actual.follower;
			}
		}
		return true;
	}
}
