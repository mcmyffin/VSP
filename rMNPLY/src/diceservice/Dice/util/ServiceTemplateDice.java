/**
 * 
 */
package diceservice.Dice.util;

/**
 * created by Christian Zen 
 * christian.zen@outlook.de 
 * Date of creation:
 * 26.04.2016
 */
public class ServiceTemplateDice {

	String name;
	String description;
	String service;
	String uri;

	public ServiceTemplateDice(String name, String description, String service, String uri) {
		this.name = name;
		this.description = description;
		this.service = service;
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
