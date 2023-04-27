package edu.uob;


import com.alexmerz.graphviz.objects.Node;

public class Artefact extends GameEntity{
    public Artefact(String name, String description) {
        super(name, description);
    }

    public Artefact(Node newArtefact){
        super(newArtefact);
    }
}
