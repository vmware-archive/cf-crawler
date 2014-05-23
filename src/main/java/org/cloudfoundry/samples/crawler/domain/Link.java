package org.cloudfoundry.samples.crawler.domain;

import java.io.Serializable;
import java.util.UUID;
public class Link implements Serializable{
	
	private Page source;
	
	private Page destination;
	
	private int weight;
	
	private String id;
	
	public Link(){}
	
	public Link(Page source, Page destination) {
		this.source = source;
		this.destination = destination;
		this.weight = 1;
		this.id = UUID.randomUUID().toString();
	}
	
	
	public Page getSource() {
		return source;
	}
	public void setSource(Page source) {
		this.source = source;
	}
	public Page getDestination() {
		return destination;
	}
	public void setDestination(Page destination) {
		this.destination = destination;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Link other = (Link) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Link [ " + source + " -->  " + destination + " {" + weight +"}";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
