package models;

import com.google.code.morphia.annotations.Entity;

@Entity
public class SubItem extends Item {

	public String subName;
	public String subDesc;

}