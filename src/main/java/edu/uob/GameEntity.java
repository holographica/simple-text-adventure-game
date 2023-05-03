package edu.uob;

import com.alexmerz.graphviz.objects.Node;


// TODO
//   CHANGED THIS TO MAKE IT PROTECTED AND NOT ABSTRACT
//   CHANGE BACK IF IT BREAKS
public class GameEntity
{
    private String name;
    private String description;

    protected GameEntity(Node newEntity){
        this.setName(newEntity.getId().getId());
        this.setDescription(newEntity.getAttribute("description"));
    }

    protected GameEntity(String name, String description)
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
