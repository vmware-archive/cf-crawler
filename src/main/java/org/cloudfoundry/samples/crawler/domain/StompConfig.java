package org.cloudfoundry.samples.crawler.domain;

public class StompConfig {
	private String username;
	private String password;
	private String host;
	private String vhost;
	private Integer port;
	private String uri;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getVhost() {
		return vhost;
	}
	public void setVhost(String vhost) {
		this.vhost = vhost;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
}
