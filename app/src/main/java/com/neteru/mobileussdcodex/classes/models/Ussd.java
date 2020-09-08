package com.neteru.mobileussdcodex.classes.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@SuppressWarnings("unused")
@DatabaseTable(tableName = "ussd")
public class Ussd {

    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField(columnName = "description", canBeNull = false)
    private String description;
    @DatabaseField(columnName = "code", canBeNull = false)
    private String code;
    @DatabaseField(columnName = "fragment", canBeNull = false)
    private String fragment;
    @DatabaseField(columnName = "parameters")
    private String parameters;
    @DatabaseField(columnName = "isNative", canBeNull = false)
    private boolean isNative;
    @DatabaseField(columnName = "isFavorite", canBeNull = false)
    private boolean isFavorite;

    public Ussd(){}

    public Ussd(String description, String code, String fragment, String parameters, boolean isNative, boolean isFavorite){
        this.description = description;
        this.fragment = fragment;
        this.code = code;
        this.parameters = parameters;
        this.isNative = isNative;
        this.isFavorite = isFavorite;
    }
    
    public String getDescription(){
        return description;
    }
    
    public String getCode(){
        return code;
    }
    
    public String getFragment(){
        return fragment;
    }

    public String getParameters(){ return parameters; }

    public boolean getIsNative(){
        return isNative;
    }

    public boolean getIsFavorite(){
        return isFavorite;
    }

    public long getId(){
        return id;
    }

}
