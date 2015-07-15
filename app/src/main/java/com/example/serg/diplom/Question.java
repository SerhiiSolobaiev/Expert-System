package com.example.serg.diplom;


public class Question {
    String name = null;
    boolean selected = false;
    boolean choose= false;
    public Question(String name, boolean selected) {
        super();
        this.name = name;
        this.selected = selected;
        this.choose = choose;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public boolean isChoose() {
        return choose;
    }
    public void setChoose(boolean choose) {
        this.choose = choose;
    }
}
