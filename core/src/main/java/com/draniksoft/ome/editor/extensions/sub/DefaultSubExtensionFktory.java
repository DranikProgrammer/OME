package com.draniksoft.ome.editor.extensions.sub;

import com.badlogic.gdx.utils.ObjectMap;
import com.draniksoft.ome.editor.texmgmnt.ext.b.AssetSubExtension;
import com.draniksoft.ome.editor.texmgmnt.ext.ext.UnresolvedAssetSubExt;
import com.draniksoft.ome.editor.texmgmnt.ext.gp_ext.AssetGroupSubExt;

public class DefaultSubExtensionFktory {

    /*
    	If component is unresolved and extension is additive -> use following
     */

    // tag:STATIC_MAP

    public static ObjectMap<Class, Class<? extends SubExtension>> MAP;

    static {

        MAP.put(AssetSubExtension.class, UnresolvedAssetSubExt.class);

        MAP.put(AssetGroupSubExt.class, AssetGroupSubExt.class);

    }

}
