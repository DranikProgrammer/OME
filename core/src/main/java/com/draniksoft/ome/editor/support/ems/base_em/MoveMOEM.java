package com.draniksoft.ome.editor.support.ems.base_em;

import com.badlogic.gdx.Gdx;
import com.draniksoft.ome.editor.components.pos.PosBoundsC;
import com.draniksoft.ome.editor.support.actions.mapO.MoveMOA;
import com.draniksoft.ome.editor.support.container.EM_desc.EditModeDesc;
import com.draniksoft.ome.editor.support.ems.core.SimpleEditMode;
import com.draniksoft.ome.editor.support.event.__base.OmeEventSystem;
import com.draniksoft.ome.editor.support.input.back.StebIC;
import com.draniksoft.ome.editor.support.input.base_mo.MoveMOIC;
import com.draniksoft.ome.editor.support.render.base_mo.MoveMORenderer;
import com.draniksoft.ome.editor.support.render.core.OverlayPlaces;
import com.draniksoft.ome.editor.systems.gui.UiSystem;
import com.draniksoft.ome.editor.systems.pos.PositionSystem;
import com.draniksoft.ome.editor.systems.render.editor.OverlayRenderSys;
import com.draniksoft.ome.editor.systems.support.ActionSystem;
import com.draniksoft.ome.editor.systems.support.InputSys;
import com.draniksoft.ome.editor.systems.support.flows.EditorSystem;
import com.draniksoft.ome.support.ui.util.WindowAgent;
import com.draniksoft.ome.support.ui.viewsys.BaseWinView;

public class MoveMOEM extends SimpleEditMode {

    public final String tag = "MoveMOEM";

    MoveMOIC ic;
    MoveMORenderer r;

    int e = -1;

    boolean easyQ = false;

    @Override
    protected void on_attached() {

	  ic = new MoveMOIC();
	  r = new MoveMORenderer();


	  e = _w.getSystem(EditorSystem.class).sel;

	  if (e < 0) {
		Gdx.app.debug(tag, "Selection is null -> detaching");
		easyQ = true;
		_w.getSystem(EditorSystem.class).detachEditMode();
		return;
	  }


	  ic.setE(e);
	  ic.setEm(this);

	  r.setE(e);
	  r.setEm(this);


	  _w.getSystem(OverlayRenderSys.class).removeRdrByPlaceBK(new int[]{}, new int[]{OverlayPlaces.ENTITY_MAIN_BODY});

	  defalteEnv();

	  _w.getSystem(OverlayRenderSys.class).addRdr(r);
	  _w.getSystem(InputSys.class).setMainIC(ic);
	  _w.getSystem(InputSys.class).setDefIC(new StebIC());

	  _w.getSystem(OmeEventSystem.class).registerEvents(this);

	  _w.getSystem(UiSystem.class).openWin("move_mo_em", new WindowAgent() {

		@Override
		public void opened(BaseWinView vw) {

		}

		@Override
		public void notifyClosing() {

		}

		@Override
		public void closed() {

		}
	  });

    }


    @Override
    public void update() {


    }

    public void notify(int t) {

        if (t == 0) selfDestroy();

        if (t == 1) {
            save();
        }

    }

    private void save() {
        Gdx.app.debug(tag, "Committing changes");
	  PosBoundsC c = _w.getMapper(PosBoundsC.class).get(e);

	  MoveMOA a = new MoveMOA();
	  a.center = false;
	  a.x = c.x;
	  a.y = c.y;
	  a._e = e;

	  _w.getSystem(ActionSystem.class).exec(a);
	  _w.getSystem(EditorSystem.class).detachEditMode();

	  //_w.getSystem(EditorSystem.class).getComOb(MOCompositionO.IDs.MO_CO).execA(MOCompositionO.ActionCodes.FOCUS_SEL, e, false);

    }

    private void selfDestroy() {

	  _w.getSystem(PositionSystem.class).resetPos(e);

        ic.setReactOnKill(false);
        _w.getSystem(EditorSystem.class).detachEditMode();

    }


    @Override
    protected void on_detached() {
	  if (easyQ) return;

	  returnEnv();

	  _w.getSystem(InputSys.class).clearMainIC();

	  _w.getSystem(OverlayRenderSys.class).removeRdr(r);
	  _w.getSystem(OverlayRenderSys.class).restoreBK();

    }


    @Override
    public int ID() {
	  return EditModeDesc.IDS.moveMO;
    }
}
