package com.draniksoft.ome.editor.support.event.entityy;

import net.mostlyoriginal.api.event.common.Event;

public class EntityDataChangeE implements Event {

    public int _e;

    public EntityDataChangeE(int _e) {
        this._e = _e;
    }

    public EntityDataChangeE() {
    }

    /* USAGE:
	  TODO update fist path root pos on update
     */
    public static class MOPositonChangeE extends EntityDataChangeE {
	  public MOPositonChangeE(int _e) {
		super(_e);
	  }
    }

    public static class PathCountChangeE extends EntityDataChangeE {
        public PathCountChangeE(int _e) {
            super(_e);
        }
    }

    public static class DwbChangeE extends EntityDataChangeE {

        public DwbChangeE(int _e) {
            super(_e);
        }
    }

}
