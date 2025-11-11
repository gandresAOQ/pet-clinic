package org.springframework.samples.petclinic.owner;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CachedPetTypeRepository {

	private final PetTypeRepository petTypeRepository;

	private final CacheService<String, List<PetType>> cacheService = new CacheService<>();

	public CachedPetTypeRepository(PetTypeRepository petTypeRepository) {
		this.petTypeRepository = petTypeRepository;
	}

	public List<PetType> findPetTypes() {
		String cacheKey = "findPetTypes";
		if (cacheService.containsKey(cacheKey)) {
			return cacheService.get(cacheKey);
		}
		List<PetType> petTypes = petTypeRepository.findPetTypes();
		cacheService.put(cacheKey, petTypes);
		return petTypes;
	}

}
