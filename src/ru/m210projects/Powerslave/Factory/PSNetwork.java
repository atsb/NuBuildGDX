// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Factory;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Powerslave.Type.Input;

public class PSNetwork extends BuildNet {

	public PSNetwork(BuildGame game) {
		super(game);
	}

	@Override
	public NetInput newInstance() {
		return new Input();
	}

	@Override
	public int GetPackets(byte[] data, int ptr, int len, int nPlayer) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void ComputerInput(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void UpdatePrediction(NetInput input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void CorrectPrediction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void CalcChecksum() {
		// TODO Auto-generated method stub
		
	}

}
