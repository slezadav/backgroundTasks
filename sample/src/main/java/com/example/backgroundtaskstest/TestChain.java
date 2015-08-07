package com.example.backgroundtaskstest;

import com.github.slezadav.backgroundTasks.BgTaskChain;

/**
 * Created by david.slezak on 7.8.2015.
 */
public class TestChain extends BgTaskChain {
    @Override
    public String getTag() {
        return "chain";
    }

}
