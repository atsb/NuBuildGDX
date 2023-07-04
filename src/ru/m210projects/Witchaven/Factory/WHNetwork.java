package ru.m210projects.Witchaven.Factory;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Witchaven.Types.Input;

public class WHNetwork extends BuildNet {

	public WHNetwork(BuildGame game) {
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
	}

}
