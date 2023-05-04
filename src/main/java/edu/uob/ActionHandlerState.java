package edu.uob;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class to store information needed
 * when attempting to handle an action.
 */
public class ActionHandlerState {
    AtomicBoolean exceptionThrown = new AtomicBoolean(false);
    Set<GameAction> uniqueActions = new HashSet<>();
    Set<String> triggersToRemove = new HashSet<>();

    public boolean wasExceptionThrown(){
        return this.exceptionThrown.get();
    }

    public Set<String> getTriggers(){
        return this.triggersToRemove;
    }

    public Set<GameAction> getUniqueActions() {
        return this.uniqueActions;
    }

    public void setExceptionThrown(boolean value){
        this.exceptionThrown.set(value);
    }

    public void addTrigger(String trigger){
        this.triggersToRemove.add(trigger);
    }

    public void setUniqueActions(Set<GameAction> uniqueActions){
        this.uniqueActions = uniqueActions;
    }
}
