/**
 * 
 */
package userservice.User.entities;

/**
 * created by Christian Zen 
 * christian.zen@outlook.de 
 * Date of creation:
 * 26.04.2016
 */
public class User {

	private String id;
	private String name;
	private String uri;

	public User(String id, String name, String uri) {
		this.id = id;
		this.name = name;
		this.uri = uri;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
