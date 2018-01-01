package com.draniksoft.ome.editor.ui.proj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.draniksoft.ome.editor.manager.ColorManager;
import com.draniksoft.ome.editor.support.actions.color.RemoveColorA;
import com.draniksoft.ome.editor.systems.support.ActionSystem;
import com.draniksoft.ome.support.ui.viewsys.BaseWinView;
import com.draniksoft.ome.utils.struct.EColor;
import com.draniksoft.ome.utils.struct.MtPair;
import com.github.czyzby.lml.annotation.LmlActor;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.color.BasicColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;

public class ColorEditView extends BaseWinView {


    VisTextField nameF;
    VisTextButton delB;

    BasicColorPicker picker;
    EColor c;

    int curID;

    public void initfor(int id) {
	  this.curID = id;
	  picker.setVisible(id >= 0);

	  if (id < 0) {
		getActor().setVisible(false);
		return;
	  }

	  getActor().setVisible(true);

	  MtPair<EColor, String> p = _w.getSystem(ColorManager.class).getP(id);
	  nameF.setText(p.V());

	  _w.getSystem(ColorManager.class).reg(c, id);
	  picker.setColor(c);

    }

    @LmlActor("root")
    VisTable t;

    @Override
    public Actor getActor() {
	  return t;
    }

    @Override
    public void preinit() {
	  c = new EColor();

	  nameF = new VisTextField("", "big");
	  delB = new VisTextButton("X", "big");

	  Gdx.app.postRunnable(new Runnable() {
		@Override
		public void run() {
		    picker = new BasicColorPicker();

		    picker.setListener(new ColorPickerListener() {
			  @Override
			  public void canceled(Color oldColor) {

			  }

			  @Override
			  public void changed(Color newColor) {
				_w.getSystem(ColorManager.class).change(curID, newColor);
			  }

			  @Override
			  public void reset(Color previousColor, Color newColor) {

			  }

			  @Override
			  public void finished(Color newColor) {
				_w.getSystem(ColorManager.class).change(curID, newColor);
			  }
		    });
		}
	  });

	  nameF.addListener(new InputListener() {
		@Override
		public boolean keyDown(InputEvent event, int keycode) {
		    if (nameF.getStage().getKeyboardFocus() == nameF && keycode == Input.Keys.ESCAPE) {
			  nameF.getStage().setKeyboardFocus(null);
		    }
		    return super.keyDown(event, keycode);
		}
	  });

	  nameF.setTextFieldListener(new VisTextField.TextFieldListener() {
		@Override
		public void keyTyped(VisTextField textField, char c) {
		    if (c == '\n') _w.getSystem(ColorManager.class).changeName(nameF.getText(), curID);
		}
	  });

	  delB.addListener(new ChangeListener() {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
		    RemoveColorA a = new RemoveColorA();
		    a.id = curID;
		    _w.getSystem(ActionSystem.class).exec(a);
		}
	  });

    }

    @Override
    public void postinit() {
	  while (picker == null) Thread.yield();

	  t.add(nameF).expandX().fillX();
	  t.add(delB).padLeft(10).padRight(10);
	  t.row();

	  t.add(picker).expandX().left().padTop(5);
    }
}
