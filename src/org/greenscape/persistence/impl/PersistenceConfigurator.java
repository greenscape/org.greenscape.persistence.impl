package org.greenscape.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.greenscape.persistence.ModelRegistryEntry;
import org.greenscape.persistence.PersistenceConstants;
import org.greenscape.persistence.PersistenceService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = PersistenceConstants.CONFIG_NAME)
public class PersistenceConfigurator {
	private static final String PERSISTENCE_MODEL_AUTOCREATE = "persistence.model.autocreate";
	private boolean autocreate;
	private final List<ModelRegistryEntry> modelList = new ArrayList<>();
	private PersistenceService persistenceService;

	@Activate
	private void activate(Map<String, Object> properties) {
		String strAutocreate = (String) properties.get(PERSISTENCE_MODEL_AUTOCREATE);
		autocreate = Boolean.valueOf(strAutocreate);
		if (autocreate) {
			createModel();
		}
	}

	@Modified
	private void modified(Map<String, Object> properties) {
		String strAutocreate = (String) properties.get(PERSISTENCE_MODEL_AUTOCREATE);
		autocreate = Boolean.valueOf(strAutocreate);
		if (autocreate) {
			createModel();
		}
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void setModelRegistryEntry(ModelRegistryEntry entry) {
		modelList.add(entry);
	}

	public void unsetModelRegistryEntry(ModelRegistryEntry entry) {
		modelList.remove(entry);
	}

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public void unsetPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = null;
	}

	private void createModel() {
		for (ModelRegistryEntry entry : modelList) {
			persistenceService.addModel(entry.getModelName());
		}
	}
}
