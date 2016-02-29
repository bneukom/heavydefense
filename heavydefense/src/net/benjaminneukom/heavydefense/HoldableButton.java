package net.benjaminneukom.heavydefense;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

public class HoldableButton extends Button {

	private static final float CHECK_DELAY = 0.05f;
	private static final float TIME_UNTIL_FIRE = 0.2f;
	private float pressedTime;
	private float changedTime;

	public HoldableButton() {
		super();
	}

	public HoldableButton(Actor child, ButtonStyle style) {
		super(child, style);
	}

	public HoldableButton(Actor child, Skin skin, String styleName) {
		super(child, skin, styleName);
	}

	public HoldableButton(Actor child, Skin skin) {
		super(child, skin);
	}

	public HoldableButton(ButtonStyle style) {
		super(style);
	}

	public HoldableButton(Drawable up, Drawable down, Drawable checked) {
		super(up, down, checked);
	}

	public HoldableButton(Drawable up, Drawable down) {
		super(up, down);
	}

	public HoldableButton(Drawable up) {
		super(up);
	}

	public HoldableButton(Skin skin, String styleName) {
		super(skin, styleName);
	}

	public HoldableButton(Skin skin) {
		super(skin);
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (isPressed()) {
			pressedTime += delta;
			if (pressedTime > TIME_UNTIL_FIRE) {
				changedTime += delta;
				if (changedTime > CHECK_DELAY) {
					changedTime = 0;

					final ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
					fire(changeEvent);
					Pools.free(changeEvent);
				}
			}
		} else {
			pressedTime = 0;
			changedTime = 0;
		}
	}
}
