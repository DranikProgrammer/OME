package com.draniksoft.ome.editor.ui.proj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.draniksoft.ome.editor.base_gfx.drawable.utils.GdxCompatibleDrawable;
import com.draniksoft.ome.editor.base_gfx.drawable.utils.RootDrawable;
import com.draniksoft.ome.editor.manager.ProjValsManager;
import com.draniksoft.ome.editor.support.event.__base.OmeEventSystem;
import com.draniksoft.ome.editor.support.event.projectVals.DrawableEvent;
import com.draniksoft.ome.editor.systems.gui.UiSystem;
import com.draniksoft.ome.editor.ui.edit.EditDwbView;
import com.draniksoft.ome.support.ui.util.WindowAgent;
import com.draniksoft.ome.support.ui.viewsys.BaseWinView;
import com.draniksoft.ome.utils.struct.MtPair;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.kotcrab.vis.ui.util.adapter.AbstractListAdapter;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.widget.*;
import net.mostlyoriginal.api.event.common.Subscribe;

import java.util.Iterator;

public class DrawableListT1View extends BaseWinView implements ActionContainer {

    private static final String tag = "DrawableListT1View";

    @LmlActor("root")
    VisTable root;

    @LmlActor("lwt")
    VisTable listViewT;

    @LmlActor("search")
    VisTextField searchField;

    @LmlActor("namef")
    VisTextField nameF;

    @LmlActor("edit")
    VisTextButton editB;

    @LmlActor("remove")
    VisTextButton removeB;

    ListView<Integer> lw;
    LWAdapter a;

    ProjValsManager m;

    boolean newS = true;

    float textWidth = 200;
    float dwbSize = 60;
    float pad = 10;

    int selid = -1;


    @Subscribe
    public void dwbEvent(DrawableEvent e) {

	  if (e instanceof DrawableEvent.DrawableAddedE) {

		a.add(e.id);

		selid = e.id;

	  } else if (e instanceof DrawableEvent.DrawableRemovedE) {

		a.removeValue(e.id, false);

		selid = -1;

	  }

	  a.itemsChanged();

	  a.getSelectionManager().select(selid);
	  updateSelection(selid);
    }

    public void refresh() {

	  Iterator<IntMap.Entry<MtPair<RootDrawable, String>>> it = m.getDrawableItAll();

	  while (it.hasNext()) {
		a.add(it.next().key);
	  }

	  a.itemsChanged();

    }

    public void updateSelection(int id) {

	  if (id < 1) {
		selid = -1;
		updateBottomT(-1);
		return;
	  }

	  selid = id;

	  updateBottomT(id);


    }

    private void updateBottomT(int id) {

	  Gdx.app.debug(tag, "Updatin table for sel id " + id);

	  if (id < 0) {
		nameF.clearText();
		editB.setVisible(false);
		removeB.setVisible(false);
	  } else {
		nameF.setText(m.getDrawableName(id));
		editB.setVisible(true);
		removeB.setVisible(true);
	  }

    }

    @Override
    public void opened() {
	  super.opened();
	  if (newS) {
		newS = false;
		refresh();
	  }
	  a.getSelectionManager().deselectAll();
	  updateSelection(-1);
    }

    @Override
    public void postinit() {


	  searchField.setTextFieldListener(new VisTextField.TextFieldListener() {
		@Override
		public void keyTyped(VisTextField textField, char c) {
		    a.itemsChanged();
		}
	  });

	  searchField.addListener(new InputListener() {
		@Override
		public boolean keyDown(InputEvent event, int keycode) {
		    if (keycode == Input.Keys.ESCAPE) {
			  searchField.getStage().setKeyboardFocus(null);
			  return true;
		    }
		    return super.keyDown(event, keycode);

		}
	  });

	  nameF.addListener(new InputListener() {
		@Override
		public boolean keyDown(InputEvent event, int keycode) {

		    if (selid >= 0 && keycode == Input.Keys.ENTER) {
			  m.setDrawableName(selid, nameF.getText());
		    }

		    return super.keyDown(event, keycode);

		}
	  });

	  listViewT.add(lw.getMainTable()).expand().fill();

	  lw.setUpdatePolicy(ListView.UpdatePolicy.IMMEDIATELY);
	  lw.setItemClickListener(new ListView.ItemClickListener<Integer>() {
		@Override
		public void clicked(Integer item) {
		    updateSelection(item);
		}
	  });

	  a.setSelectionMode(AbstractListAdapter.SelectionMode.SINGLE);
	  a.getSelectionManager().setProgrammaticChangeEvents(true);

    }


    @Override
    public void preinit() {

	  m = _w.getSystem(ProjValsManager.class);
	  _w.getSystem(OmeEventSystem.class).registerEvents(this);


	  a = new LWAdapter(new Array<Integer>());

	  lw = new ListView<Integer>(a);
	  lw.setUpdatePolicy(ListView.UpdatePolicy.ON_DRAW);


    }

    private class DrawableView extends VisTable {

	  VisLabel name;

	  Container<VisImage> c;
	  VisImage img;

	  int id;

	  public void create(int id) {
		this.id = id;

		name = new VisLabel(m.getDrawableName(id));

		img = new VisImage(GdxCompatibleDrawable.from(m.getRawDrawable(id)));

		add(img).minSize(dwbSize).padRight(pad).padLeft(pad);
		add(name).expandX();

	  }

	  public void setSelected(boolean s) {

		if (s) setBackground("shd_grey");
		else setBackground("grey");

	  }

	  public void update() {

		name.setText(m.getDrawableName(id));

		img.setDrawable(GdxCompatibleDrawable.from(m.getRawDrawable(id)));

	  }

	  public int getId() {
		return id;
	  }

	  public boolean matches(String filter) {

		return m.getDrawableName(id).contains(filter);
	  }

    }

    private class LWAdapter extends ArrayAdapter<Integer, DrawableView> {

	  public LWAdapter(Array<Integer> array) {
		super(array);
		//gp = new HorizontalFlowGroup();
	  }

	  @Override
	  public void fillTable(VisTable itemsTable) {


		String f = "";
		if (searchField != null && searchField.getText() != null) f = searchField.getText().trim();

		for (final Integer item : iterable()) {
		    final DrawableView view = getView(item);

		    prepareViewBeforeAddingToTable(item, view);

		    if (!view.matches(f)) continue;

		    itemsTable.add(view).growX();
		    itemsTable.row();
		}

	  }

	  @Override
	  protected void selectView(DrawableView view) {
		view.setSelected(true);
		Gdx.app.debug(tag, "View selected");

	  }

	  @Override
	  protected void deselectView(DrawableView view) {
		view.setSelected(false);
		Gdx.app.debug(tag, "View deselected");

	  }

	  @Override
	  protected void updateView(DrawableView view, Integer item) {

		view.update();

	  }

	  @Override
	  protected DrawableView createView(Integer item) {

		DrawableView v = new DrawableView();

		v.create(item);

		return v;

	  }
    }


    @LmlAction("add_dwb")
    public void add_dwb() {

	  int i = _w.getSystem(ProjValsManager.class).createNewDrawable(nameF.getText());

	  a.add(i);
	  a.itemsChanged();


	  lw.getScrollPane().setScrollPercentY(1);

    }

    @LmlAction("edit")
    public void edit() {

	  _w.getSystem(UiSystem.class).openWin("dwb_edit_vw", new WindowAgent() {
		@Override
		public <T extends BaseWinView> void opened(T vw) {

		    EditDwbView dw = (EditDwbView) vw;
		    dw.ifor(new EditDwbView.ProjDwbHandler(selid));

		}

		@Override
		public void notifyClosing() {
		    _w.getSystem(UiSystem.class).openWin(DrawableListT1View.this.ID);
		    a.itemsDataChanged();
		}

		@Override
		public void closed() {

		}
	  });


    }


    @Override
    public void prepareParser(LmlParser p) {
	  super.prepareParser(p);
	  p.getData().addActionContainer("l", this);
    }

    @Override
    public void clearParser(LmlParser p) {
	  super.clearParser(p);
	  textWidth = p.parseFloat(p.getData().getArgument("_1"));
	  dwbSize = p.parseFloat(p.getData().getArgument("_2"));
	  pad = p.parseFloat(p.getData().getArgument("_3"));

	  p.getData().removeActionContainer("l");
    }

    @Override
    public Actor get() {
	  return root;
    }
}
