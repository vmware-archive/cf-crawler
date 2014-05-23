package org.cloudfoundry.samples.crawler.domain;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CrawlerSession implements Serializable{
	
	private Integer duration = 0;
	private Integer count = 0;
	private Integer maxDepth = 0;
	private Long startTime;
	
	@JsonProperty
	private String url;
	
	@JsonProperty
	private String id;
	
	@JsonIgnore
	public boolean isActive(){
		return (System.currentTimeMillis() - startTime) < duration;
	}
	
	public CrawlerSession(){
		this.id = UUID.randomUUID().toString();
		this.startTime = System.currentTimeMillis();
	}
	
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}


	public Integer getMaxDepth() {
		return maxDepth;
	}


	public void setMaxDepth(Integer maxDepth) {
		this.maxDepth = maxDepth;
	}

	public String getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
