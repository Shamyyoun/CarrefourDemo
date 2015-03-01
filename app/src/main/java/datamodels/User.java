package datamodels;

import java.io.Serializable;

/**
 * Created by Shamyyoun on 2/8/2015.
 */
public class User implements Serializable {
    private String id;
    private String name;
    private Type type;
    private String genId;
    private double Lat;
    private double lon;

    public User(String id) {
        this.id = id;
    }

    /**
     * ---Setters and Getters---
     */
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public Type getType() {
        return type;
    }

    public User setType(Type type) {
        this.type = type;
        return this;
    }

    public String getGenId() {
        return genId;
    }

    public User setGenId(String genId) {
        this.genId = genId;
        return this;
    }

    public double getLat() {
        return Lat;
    }

    public User setLat(double lat) {
        Lat = lat;
        return this;
    }

    public double getLon() {
        return lon;
    }

    public User setLon(double lon) {
        this.lon = lon;
        return this;
    }

    /**
     * sub class from user, used to represent user type
     */
    public static class Type implements Serializable {
        private String id;
        private String name;

        public Type(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Type setName(String name) {
            this.name = name;
            return this;
        }
    }
}
