package org.springframework.samples.petclinic.owner;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CachedOwnerRepository {

	private final OwnerRepository ownerRepository;

	private final CacheService<String, List<Owner>> cacheService = new CacheService<>();

	private final CacheService<Integer, Optional<Owner>> cacheService2 = new CacheService<>();

	public CachedOwnerRepository(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	public List<Owner> findAllOwners() {
		String cacheKey = "findAllOwners";
		if (cacheService.containsKey(cacheKey)) {
			return cacheService.get(cacheKey);
		}
		List<Owner> owners = ownerRepository.findAllOwners();
		cacheService.put(cacheKey, owners);
		return owners;
	}

	public Optional<Owner> findById(Integer ownerId) {
		if (cacheService2.containsKey(ownerId)) {
			return cacheService2.get(ownerId);
		}
		Optional<Owner> owner = ownerRepository.findById(ownerId);
		cacheService2.put(ownerId, owner);
		return owner;
	}

}
