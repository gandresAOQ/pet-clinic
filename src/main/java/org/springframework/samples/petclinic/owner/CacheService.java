package org.springframework.samples.petclinic.owner;

import java.util.HashMap;
import java.util.Map;

public class CacheService<K, V> {

	private final Map<K, V> cache = new HashMap<>();

	// Check if the cache contains a key
	public boolean containsKey(K key) {
		return cache.containsKey(key);
	}

	// Retrieve a value from the cache
	public V get(K key) {
		return cache.get(key);
	}

	// Add a value to the cache
	public void put(K key, V value) {
		cache.put(key, value);
	}

	// Clear the cache
	public void clear() {
		cache.clear();
	}

}
