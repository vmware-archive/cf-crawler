package org.cloudfoundry.samples.crawler.domain;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class Page implements Serializable{
	
	
	private final String url;
	private int responseCode;
	private Long crawlTime;
	private Long size;
	
	private Page ref;
	
	private int depth = 0;
	
	@JsonIgnore
	private List<Link> links;
	
	@JsonIgnore
	private CrawlerSession session;

	@JsonIgnore
	private HashFunction hf = Hashing.adler32();
	
	private final Integer id;
	
	public Page(String url){
		this(url, null);
	}
	
	public Page(String url, Page ref){
		if(ref != null){
			this.ref = ref;
			this.depth = ref.getDepth() + 1;
		}
		this.url = url;
		this.id = hf.hashString(url, Charset.defaultCharset()).asInt();
		this.links = new ArrayList<>();
	}
	
	public String getUrl() {
		return url;
	}
	
	public CrawlerSession getSession() {
		return session;
	}
	public void setSession(CrawlerSession session) {
		this.session = session;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public Long getCrawlTime() {
		return crawlTime;
	}
	public void setCrawlTime(Long crawlTime) {
		this.crawlTime = crawlTime;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}

	public void addLink(Page page){
		Link link = new Link(this,page);
		if(links.contains(link)){
			Link other = links.get(links.indexOf(link));
			other.setWeight(other.getWeight()+1);
		}else{
			links.add(link);
		}
	}
	
	public List<Link> getLinks() {
		return java.util.Collections.unmodifiableList(links);
	}

	public Page getRef() {
		return ref;
	}

	public int getDepth() {
		return depth;
	}
	
	@JsonProperty(value="numLinks")
	public int getNumLinks(){
		return links.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Page other = (Page) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Page [url=" + url + ", responseCode=" + responseCode + ", crawlTime=" + crawlTime + ", size=" + size + ", depth=" + depth + ", id=" + id + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setRef(Page ref) {
		this.ref = ref;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	
	
}
