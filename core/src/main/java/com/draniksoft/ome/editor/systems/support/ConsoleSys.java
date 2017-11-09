package com.draniksoft.ome.editor.systems.support;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.draniksoft.ome.editor.support.CommandExecutor;
import com.draniksoft.ome.editor.support.event.base_gfx.ResizeEvent;
import com.draniksoft.ome.support.load.IntelligentLoader;
import com.draniksoft.ome.support.load.interfaces.IGLRunnable;
import com.strongjoshua.console.GUIConsole;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Subscribe;

public class ConsoleSys extends BaseSystem {

    private static final String tag = "ConsoleSys";

    @Wire
    InputMultiplexer mxp;

    GUIConsole console;


    @Wire(name = "engine_l")
    IntelligentLoader l;


    @Override
    protected void initialize() {

        l.passGLRunnable(new Gfx_ldr());

        world.getSystem(EventSystem.class).registerEvents(this);

    }

    @Override
    protected void processSystem() {


        console.draw();


    }

    @Subscribe
    public void resize(ResizeEvent e) {

        console.refresh(true);

    }

    private class Gfx_ldr implements IGLRunnable {

        @Override
        public byte run() {
            console = new GUIConsole();

            console.setCommandExecutor(new CommandExecutor(world));
            console.log("See the github wiki for more details");

            console.setDisplayKeyID(Input.Keys.F1);

            mxp.addProcessor(console.getInputProcessor());

            return IGLRunnable.READY;
        }
    }
}
