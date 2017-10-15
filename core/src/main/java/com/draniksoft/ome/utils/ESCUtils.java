package com.draniksoft.ome.utils;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.draniksoft.ome.editor.components.supp.SelectionC;
import com.draniksoft.ome.editor.support.event.entityy.SelectionChangeE;
import net.mostlyoriginal.api.event.common.EventSystem;

public class ESCUtils {


    public static final int EVENT_MAX_PRIORITY = 100;
    public static final int EVENT_HIGH_PRIORITY = 80;
    public static final int EVENT_MID_PRIORITY = 50;
    public static final int EVENT_DEF_PRIORITY = 0;
    public static final int EVENT_LOW_PRIORITY = -20;
    public static final int EVENT_MIN_PRIORITY = -50;

    public static void clearSelected(World w) {

        IntBag b = w.getAspectSubscriptionManager().get(Aspect.all(SelectionC.class)).getEntities();
        ComponentMapper<SelectionC> mapper = w.getMapper(SelectionC.class);

        for (int i = 0; i < b.size(); i++) {

            int e = b.get(i);

            mapper.remove(e);

        }

        SelectionChangeE e = new SelectionChangeE();
        if (b.size() > 0)
            e.old = b.get(0);
        w.getSystem(EventSystem.class).dispatch(e);


    }

    public static int getFirstSel(World w) {
        IntBag b = w.getAspectSubscriptionManager().get(Aspect.all(SelectionC.class)).getEntities();

        if (b.size() > 0) return b.get(0);

        return -1;
    }

    public static void removeSelectionBeforeRMV(int _e, World w) {

        if (w.getMapper(SelectionC.class).has(_e)) {

            SelectionChangeE e = new SelectionChangeE();
            e.old = _e;
            e.n = -1;

            w.getSystem(EventSystem.class).dispatch(e);

        }

    }
}
