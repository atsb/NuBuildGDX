package ru.m210projects.Build.desktop.Controllers;

import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;

import java.util.Arrays;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;
import net.java.games.input.Controller;
import ru.m210projects.Build.Architecture.BuildController;

public class JController implements BuildController {

	private final float[] directions = {
		Component.POV.UP,
		Component.POV.DOWN,
		Component.POV.LEFT,
		Component.POV.RIGHT,
		Component.POV.UP_LEFT,
		Component.POV.UP_RIGHT,
		Component.POV.DOWN_LEFT,
		Component.POV.DOWN_RIGHT,
	};

	protected Controller controller;
	
	protected Array<Component> povs;
	protected Array<Component> buttons;
	protected Array<Component> axises;
	
	protected boolean[] buttonStatus;
	protected boolean[] hitButton;
	protected int allButtonsCount;
	protected boolean buttonPressed = false;
	protected Vector2 stickVector = new Vector2();
	
	public JController(Controller src)
	{
		this.controller = src;
		
		Component[] components = controller.getComponents();
		
		povs = new Array<Component>();
		buttons = new Array<Component>();
		axises = new Array<Component>();
		
		for(int i = 0; i < components.length; i++)
        {
			Component component = components[i];
            Identifier componentIdentifier = component.getIdentifier();

            if(componentIdentifier instanceof Button)
            	buttons.add(component);
            if(componentIdentifier == Axis.POV)
                povs.add(component);
            else if(component.isAnalog())
            	axises.add(component);
        }
		
		allButtonsCount = buttons.size + povs.size * 4 + ((axises.size > 3) ? 2 : 0); //4 - buttons in DPad(pov) and 2 - triggers (axis 4)
		buttonStatus = new boolean[allButtonsCount];
		hitButton = new boolean[allButtonsCount];
	}

	@Override
	public int getButtonCount() {
		return allButtonsCount;
	}

	@Override
	public int getAxisCount() {
		return axises.size;
	}

	@Override
	public int getPovCount() {
		return povs.size;
	}

	@Override
	public Vector2 getStickValue(int aCode1, int aCode2, float deadZone) {
		if(aCode1 >= axises.size || aCode2 >= axises.size) 
			return stickVector.set(0.0f, 0.0f);
		
		float lx = axises.get(aCode1).getPollData();
		float ly = axises.get(aCode2).getPollData();
		float mag = (float) Math.sqrt(lx*lx + ly*ly);
		float nlx = lx / mag;
		float nly = ly / mag;
		float nlm;
		if (mag > deadZone)
		{
			if (mag > 1.0f)
				mag = 1.0f;

			mag -= deadZone;
			nlm = mag / (1.0f - deadZone);
			float x1 = nlx * nlm;
			float y1 = nly * nlm;
			return stickVector.set(x1, y1);
		}
		else
			return stickVector.set(0.0f, 0.0f);
	}

	@Override
	public String getName() {
		return controller.getName();
	}

	@Override
	public void update() {
		if(!controller.poll())
			System.err.println("Disconnected");

		buttonPressed = false;
		dPadHandler();
		triggerHandler();
		for(int i = 0; i < buttons.size; i++)
		{
			if (buttons.get(i).getPollData() == 1.0f) {
				buttonPressed = true;
				if (!hitButton[i]) {
					getInput().setKey(ANYKEY, 1);
					buttonStatus[i] = true;
					hitButton[i] = true;
				}
			} else {
				buttonStatus[i] = false;
				hitButton[i] = false;
			}
		}
	}
	
	@Override
	public boolean buttonPressed()
	{
		return buttonPressed;
	}

	@Override
	public void resetButtonStatus()
	{
		Arrays.fill(buttonStatus, false);
	}

	@Override
	public boolean buttonPressed(int buttonCode)
	{
		if(buttonCode >= 0 && buttonCode < allButtonsCount)
			return hitButton[buttonCode];
		
		return false;
	}
	
	@Override
	public boolean buttonStatusOnce(int buttonCode)
	{
		if(buttonCode >= 0 && buttonCode < allButtonsCount && buttonStatus[buttonCode]) {
			buttonStatus[buttonCode] = false;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean buttonStatus(int buttonCode)
	{
		return buttonCode >= 0 && buttonCode < allButtonsCount && buttonStatus[buttonCode];
	}
	
	private void dPadHandler()
	{
		for(int i = 0; i < povs.size; i++)
		{
			float dir = povs.get(i).getPollData();
			if (Float.compare(dir, Component.POV.OFF) != 0) 
			{
				int num = buttons.size + (4 * i);
				for(int d = 0; d < 4; d++) 
				{
					if(Float.compare(dir, directions[d]) == 0)
					{
						buttonPressed = true;
						if(!hitButton[num + d]) {
							getInput().setKey(ANYKEY, 1);
							buttonStatus[num + d] = true;
							hitButton[num + d] = true;
						}
					} else {
						buttonStatus[num + d] = false;
						hitButton[num + d] = false;
					}
				}

				for(int d = 0; d < 4; d++) 
				{
					int fbut = num + d / 2; //up down
					int sbut = (num + 2) + d % 2; //left right
					if(Float.compare(dir, directions[d + 4]) == 0)
					{
						getInput().setKey(ANYKEY, 1);
						buttonStatus[fbut] = true;
						hitButton[fbut] = true;
						
						buttonStatus[sbut] = true;
						hitButton[sbut] = true;
					}
				}
			} else {
				for(int b = 0; b < 4; b++) {
					int num = buttons.size + (4 * i) + b;
					buttonStatus[num] = false;
					hitButton[num] = false;
				}
			}
		}
	}
	
	private void triggerHandler()
	{
		if(axises.size < 5) return;
		
		float value = axises.get(4).getPollData();
		int num = buttons.size + (4 * povs.size);
		
		if(value >= 0.9f) {
			buttonPressed = true;
			if(!hitButton[num]) {
				getInput().setKey(ANYKEY, 1);
				buttonStatus[num] = true;
				hitButton[num] = true;
			}
		} else {
			buttonStatus[num] = false;
			hitButton[num] = false;
		}
		
		if(value <= -0.9f) {
			buttonPressed = true;
			if(!hitButton[num + 1]) {
				getInput().setKey(ANYKEY, 1);
				buttonStatus[num + 1] = true;
				hitButton[num + 1] = true;
			}
		} else {
			buttonStatus[num + 1] = false;
			hitButton[num + 1] = false;
		}
	}

}
