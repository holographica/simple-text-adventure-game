package edu.uob;

import com.alexmerz.graphviz.objects.Node;

public class GameCharacter extends GameEntity{

    public GameCharacter(String name, String description) {
        super(name, description);
    }

    public GameCharacter(Node newCharacter){
        super(newCharacter);
    }
}
