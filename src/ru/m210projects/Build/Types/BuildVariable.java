// This file is part of BuildGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Types;

public abstract class BuildVariable<T> {
	
	public enum RespondType { Success, Fail, Description }
	
	private T value;
	private final String description;
	
	protected abstract void execute(T value);
	
	protected abstract T check(Object value);

	public BuildVariable(T set, String description)
	{
		this.value = set;
		this.description = description;
	}
	
	public RespondType set(Object i)
	{
		if(i == null) return RespondType.Description;
		
		T val = check(i);
		if(val != null) {
			execute(value = val);
			return RespondType.Success;
		} 
		
		return RespondType.Fail;
	}
	
	public T get() {
		return value;
	}

	public String getDescription()
	{
		return description;
	}
}
