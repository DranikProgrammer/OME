package com.draniksoft.ome.editor.ui.proj;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.draniksoft.ome.editor.manager.ResourceManager;
import com.draniksoft.ome.editor.res.impl.types.ResTypes;
import com.draniksoft.ome.editor.support.event.__base.OmeEventSystem;
import com.draniksoft.ome.editor.support.event.projectVals.ProjectValEvent;
import com.draniksoft.ome.support.ui.viewsys.BaseWinView;
import com.draniksoft.ome.ui_addons.ColoredCirlceWget;
import com.github.czyzby.lml.annotation.LmlActor;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.widget.*;
import net.mostlyoriginal.api.event.common.Subscribe;

public class ColorListT1View extends BaseWinView {

    private static final String tag = "ColorListT1View";

    @LmlActor("root")
    VisTable root;

    @LmlActor("spane")
    VisScrollPane pane;

    @LmlActor("lwt")
    VisTable lwt;

    @LmlActor("searchf")
    VisTextField searchF;

    ColorAd a;
    ListView<Integer> lw;


    private class ColorVW extends VisTable {

	  ColoredCirlceWget ccl;
	  VisLabel name;

	  int id;

	  public void ifor(int id) {
		this.id = id;

		ccl = new ColoredCirlceWget();
		name = new VisLabel("lel");

	  }

	  public void update() {

		name.setText(_w.getSystem(ResourceManager.class).getName(ResTypes.COLOR, id));

	  }

	  // CLEAR LINKED RESOURCES
	  public void removeing() {


	  }
    }

    private class ColorAd extends ArrayAdapter<Integer, ColorVW> {

	  ColorAd() {
		super(new Array<Integer>());
	  }

	  @Override
	  protected ColorVW createView(Integer item) {
		return null;
	  }

	  @Override
	  protected void selectView(ColorVW view) {
		super.selectView(view);
	  }

	  @Override
	  protected void deselectView(ColorVW view) {
		super.deselectView(view);
	  }

	  @Override
	  protected void updateView(ColorVW view, Integer item) {
		view.update();
	  }

	  @Override
	  protected void itemRemoved(Integer item) {
		getSelectionManager().deselect(item);
		getViews().get(item).removeing();
		getViews().remove(item);
		viewListener.invalidateDataSet();

	  }
    }

    @Subscribe
    public void colorEV(ProjectValEvent ce) {
	  if (ce.t != ResTypes.COLOR) return;
	  a.itemsDataChanged();
    }

    @Override
    public void preinit() {

	  a = new ColorAd();

	  lw = new ListView<Integer>(a);

    }

    @Override
    public void postinit() {
	  lwt.add(lw.getMainTable()).expand().fill();
	  fullDataUpdate();
	  _w.getSystem(OmeEventSystem.class).registerEvents(this);
    }

    private void fullDataUpdate() {
	  a.clear();
	  Array<Integer> ia = new Array<Integer>();
	  for (int i : _w.getSystem(ResourceManager.class).getKeys(ResTypes.COLOR).toArray()) ia.add(i);
	  a.addAll(ia);
    }

    @Override
    public Actor get() {
	  return root;
    }

}
