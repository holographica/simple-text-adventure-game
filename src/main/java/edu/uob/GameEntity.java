package edu.uob;

import com.alexmerz.graphviz.objects.Node;

public abstract class GameEntity
{
    private String name;
    private String description;

    public GameEntity(Node newEntity){
        this.setName(newEntity.getId().getId());
        // TODO
        //  CHANGED THIS SO ITS NOT LOWERCASE
        //   MAKE SURE TO CHANGE BACK AFTER CHECKING!!!




        //  !!!!!
        this.setDescription(newEntity.getAttribute("description"));
    };

    public GameEntity(String name, String description)
    {
        setName(name);
        setDescription(description);
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
