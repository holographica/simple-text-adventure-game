package edu.uob;

interface EntityChecker <T extends GameEntity> {
    T getEntityByName(String name);
}
