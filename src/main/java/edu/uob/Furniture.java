package edu.uob;

import com.alexmerz.graphviz.objects.Node;

public class Furniture extends GameEntity{
    public Furniture(String name, String description) {
        super(name, description);
    }

    public Furniture(Node newFurniture){
        super(newFurniture);
    }
}
