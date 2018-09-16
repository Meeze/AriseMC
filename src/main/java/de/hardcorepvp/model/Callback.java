package de.hardcorepvp.model;

public interface Callback<T> {

	void onResult(T type);
	void onFailure(Throwable cause);
}
