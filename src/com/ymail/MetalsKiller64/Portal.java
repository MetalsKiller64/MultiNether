package com.ymail.MetalsKiller64;

import org.bukkit.Location;
//import java.util.ArrayList;
//import java.util.List;

public class Portal
{
    private Location _location;
	private Integer _x;
	private Integer _y;
	private Integer _z;
	private String _world;
    private String _link_world;
	private Integer _id;
	private Integer _width;
	private Integer _height;
	private Integer _corresponding_id;
	//TODO: ausrichtung speichern
    
    public Portal()
    {
	
    }
	
	//Setter
    public void setLocation(Location l)
    {
		this._location = l;
    }
    
    public void setLinkTo(String world)
    {
		this._link_world = world;
    }
	
	public void setID(int id)
	{
		this._id = id;
	}
	
	public void setX(Integer x)
	{
		this._x = x;
	}
	
	public void setY(Integer y)
	{
		this._y = y;
	}
	
	public void setZ(Integer z)
	{
		this._z = z;
	}
	
	public void setWorld(String world)
	{
		this._world = world;
	}
	
	public void setWidth(Integer width)
	{
		this._width = width;
	}
	
	public void setHeight(Integer height)
	{
		this._height = height;
	}
	public void setCorrespondingID(Integer id)
	{
		this._corresponding_id = id;
	}
	
	//Getter
    public Location getLocation()
    {
		return this._location;
    }
    
    public String getLinkTo()
    {
		return this._link_world;
    }
	
	public Integer getID()
	{
		return this._id;
	}
	
	public Integer getX()
	{
		return this._x;
	}
	
	public Integer getY()
	{
		return this._y;
	}
	
	public Integer getZ()
	{
		return this._z;
	}
	
	public String getWorld()
	{
		return this._world;
	}
	
	public Integer getWidth()
	{
		return this._width;
	}
	
	public Integer getHeight()
	{
		return this._height;
	}
	public Integer getCorrespondingID()
	{
		return this._corresponding_id;
	}
}
