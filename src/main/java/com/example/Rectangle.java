package com.example;

public class Rectangle {
    private Integer ID;
    private String name;
    private Float width;
    private Float height;
    private String color;
    private String borderColor;
    private Float borderWidth;

    public Integer getID(){
        return this.ID;
    }

    public String getName(){
        return this.name;
    }

    public Float getWidth(){
        return this.width;
    }

    public Float getHeight(){
        return this.height;
    }

    public String getColor(){
        return this.color;
    }

    public String getBorderColor(){
        return this.borderColor;
    }

    public Float getBorderWidth(){
        return this.borderWidth;
    }

    public void setID(Integer ID){
         this.ID = ID;
    }

    public void setName(String name){
         this.name =name;
    }

    public void setWidth(Float width){
         this.width = width;
    }

    public void setHeight(Float height){
         this.height = height;
    }

    public void setColor(String color){
         this.color = color;
    }

    public void setBorderColor(String borderColor){
         this.borderColor = borderColor;
    }

    public void setBorderWidth(Float borderWidth){
        this.borderWidth = borderWidth;
    }


}
