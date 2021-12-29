package caceresenzo.apps.barcutoptimizer.logic.algorithms;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import caceresenzo.apps.barcutoptimizer.config.I18n;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.annotations.AlgorithmSetting;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.impl.FillingCutAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlgorithmManager {
	
	/* Singleton */
	private static AlgorithmManager INSTANCE;
	
	/* Algorithm */
	private List<CutAlgorithm> algorithms;
	private Map<CutAlgorithm, List<AlgorithmSettingEntry>> settingEntries;
	
	/* Private Constructor */
	private AlgorithmManager() {
		this.algorithms = new ArrayList<>();
		this.settingEntries = new HashMap<>();
	}
	
	public void initialize() {
		register(new FillingCutAlgorithm());
		
		algorithms.forEach((algorithm) -> extractSettingEntries(algorithm));
	}
	
	private void register(CutAlgorithm cutAlgorithm) {
		Objects.requireNonNull(cutAlgorithm);
		
		log.info("Added algorithm: {}", cutAlgorithm.getClass().getSimpleName());
		
		algorithms.add(cutAlgorithm);
		settingEntries.put(cutAlgorithm, new ArrayList<>());
	}
	
	private void extractSettingEntries(CutAlgorithm cutAlgorithm) {
		Class<? extends CutAlgorithm> clazz = cutAlgorithm.getClass();
		Field[] fields = clazz.getDeclaredFields();
		
		for (Field field : fields) {
			AlgorithmSetting algorithmSetting = field.getAnnotation(AlgorithmSetting.class);
			
			if (algorithmSetting != null) {
				log.info("Found setting: {}", algorithmSetting.key());
				
				settingEntries.get(cutAlgorithm).add(new AlgorithmSettingEntry(cutAlgorithm, field, algorithmSetting));
			}
		}
	}
	
	public List<AlgorithmSettingEntry> getAlgorithmSettingEntries(CutAlgorithm cutAlgorithm) {
		return settingEntries.get(cutAlgorithm);
	}
	
	public static String getBaseTranslationKey(CutAlgorithm cutAlgorithm) {
		return "cut-algorithm." + cutAlgorithm.getTranslationKey();
	}
	
	public static String getTranslatedName(CutAlgorithm cutAlgorithm) {
		return I18n.string(getBaseTranslationKey(cutAlgorithm) + ".name");
	}
	
	public static String getTranslatedDescription(CutAlgorithm cutAlgorithm) {
		return I18n.string(getBaseTranslationKey(cutAlgorithm) + ".description");
	}
	
	public static String getBaseTranslationKey(AlgorithmSettingEntry algorithmSettingEntry) {
		return getBaseTranslationKey(algorithmSettingEntry.getInstance()) + ".setting." + algorithmSettingEntry.getI18nKey();
	}
	
	public static String getTranslatedName(AlgorithmSettingEntry algorithmSettingEntry) {
		return I18n.string(getBaseTranslationKey(algorithmSettingEntry) + ".name");
	}
	
	public static String getTranslatedDescription(AlgorithmSettingEntry algorithmSettingEntry) {
		return I18n.string(getBaseTranslationKey(algorithmSettingEntry) + ".description");
	}
	
	/** @return AlgorithmManager's singleton instance. */
	public static final AlgorithmManager get() {
		if (INSTANCE == null) {
			INSTANCE = new AlgorithmManager();
		}
		
		return INSTANCE;
	}
	
	public static class AlgorithmSettingEntry {
		
		/* Variables */
		private final CutAlgorithm instance;
		private final Field field;
		private final AlgorithmSetting algorithmSetting;
		private final String i18nKey;
		private final Class<?> type;
		
		/* Constructor */
		public AlgorithmSettingEntry(CutAlgorithm algorithmInstance, Field field, AlgorithmSetting algorithmSetting) {
			this.instance = algorithmInstance;
			this.field = field;
			this.algorithmSetting = algorithmSetting;
			
			this.i18nKey = algorithmSetting.key();
			this.type = field.getType();
			
			field.setAccessible(true);
		}
		
		public CutAlgorithm getInstance() {
			return instance;
		}
		
		public Field getField() {
			return field;
		}
		
		public AlgorithmSetting getAlgorithmSetting() {
			return algorithmSetting;
		}
		
		public String getI18nKey() {
			return i18nKey;
		}
		
		public Class<?> getType() {
			return type;
		}
		
		public Object getValue() {
			try {
				return field.get(instance);
			} catch (IllegalArgumentException | IllegalAccessException exception) {
				throw new RuntimeException("Unexpected exception when getting field value.", exception);
			}
		}
		
		public boolean setValue(Object value) {
			try {
				field.set(instance, value);
				
				return true;
			} catch (Exception exception) {
				exception.printStackTrace();
				return false;
			}
		}
		
		public boolean setValueWithAutoParsing(String raw) throws Exception {
			Object value = null;
			
			if (type == Double.class || type == double.class) {
				value = Double.parseDouble(raw);
			} else if (type == Integer.class || type == int.class) {
				value = Integer.parseInt(raw);
			} else {
				throw new IllegalStateException("Not handled auto-parsing class: " + type.getSimpleName());
			}
			
			return setValue(value);
		}
		
	}
	
	public List<CutAlgorithm> getCutAlgorithms() {
		return algorithms;
	}
	
}