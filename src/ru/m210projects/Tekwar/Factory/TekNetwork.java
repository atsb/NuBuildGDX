package ru.m210projects.Tekwar.Factory;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Tekwar.Types.Input;

public class TekNetwork extends BuildNet {

	public TekNetwork(BuildGame game) {
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
	public void UpdatePrediction(NetInput input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void CalcChecksum() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void CorrectPrediction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ComputerInput(int i) {
		// TODO Auto-generated method stub
		
	}

}
