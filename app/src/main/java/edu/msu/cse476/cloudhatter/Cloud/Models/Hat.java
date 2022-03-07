package edu.msu.cse476.cloudhatter.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "hatting")
public class Hat {
    @Attribute
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Attribute
    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Attribute
    private Float x;

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    @Attribute
    private Float y;

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    @Attribute
    private Float angle;

    public Float getAngle() {
        return angle;
    }

    public void setAngle(Float angle) {
        this.angle = angle;
    }

    @Attribute
    private Float scale;

    public Float getScale() {
        return scale;
    }

    public void setScale(Float scale) {
        this.scale = scale;
    }

    @Attribute
    private Integer color;

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    @Attribute
    private Integer type;


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Attribute
    private String feather;

    public String getFeather() {
        return feather;
    }

    public void setFeather(String feather) {
        this.feather = feather;
    }

    @Attribute
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Hat() {}

    public Hat(Integer id, String name, String uri, Float x, Float y, Float angle, Float scale, Integer color, Integer type, String feather) {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.scale = scale;
        this.color = color;
        this.type = type;
        this.feather = feather;
    }
}
