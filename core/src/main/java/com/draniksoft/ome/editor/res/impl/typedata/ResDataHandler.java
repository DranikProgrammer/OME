package com.draniksoft.ome.editor.res.impl.typedata;

import com.artemis.World;
import com.draniksoft.ome.editor.res.impl.res_ifaces.Resource;
import com.draniksoft.ome.editor.res.impl.res_ifaces.WeakLinkedResource;

public interface ResDataHandler<TYPE> {

    // init along with res constructors
    void init();

    // init live mode
    void initL(WeakLinkedResource<TYPE> link);

    // deinit live mode
    void deinitL();


    // UNUSED WHEN GROUP
    Resource<TYPE> build(World w);

    // copy

    ResDataHandler<TYPE> copy();

}
