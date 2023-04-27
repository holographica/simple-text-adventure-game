package edu.uob;

import com.alexmerz.graphviz.objects.Graph;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

public class GameState {

    // (is it fine to use hashsets? better/worse than arraylist?)

    private HashSet<Location> locations;

    private HashSet<GameAction> actions;

    public GameState(){
        this.locations  = new HashSet<>();
        this.actions = new HashSet<>();
    }

    public HashSet<Location> getLocations(){
        return this.locations;
    }

    public HashSet<GameAction> getActions(){
        return this.actions;
    }

    public void addLocation(Location newLocation){
        this.locations.add(newLocation);
    }

    public void addAction(GameAction newAction){
        this.actions.add(newAction);
    }


//    public ArrayList<Location> loadLocations(ArrayList<Graph> locationGraphs){
//        for (Graph g: locationGraphs){
//          // write sub function to parse location, description, items, characters separately and add to overall location
//        }
//    }


}
