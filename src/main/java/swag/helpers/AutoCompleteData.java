package swag.helpers;


public class AutoCompleteData {
	private String label;
	private String value;

	public AutoCompleteData(String _label, String _value) {
		super();
		this.label = _label;
		this.value = _value;
	}

	public String getLabel() {
		return this.label;
	}

	public String getValue() {
		return this.value;
	}
}