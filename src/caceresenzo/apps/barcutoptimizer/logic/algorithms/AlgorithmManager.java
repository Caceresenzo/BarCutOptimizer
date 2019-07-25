package caceresenzo.apps.barcutoptimizer.logic.algorithms;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import caceresenzo.apps.barcutoptimizer.logic.algorithms.annotations.AlgorithmSetting;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.implementations.FillingCutAlgorithm;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.reflection.ReflectionUtils;

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
		
		Logger.info("Added algorithm: %s", cutAlgorithm.getClass().getSimpleName());
		
		algorithms.add(cutAlgorithm);
		settingEntries.put(cutAlgorithm, new ArrayList<>());
	}
	
	private void extractSettingEntries(CutAlgorithm cutAlgorithm) {
		Class<? extends CutAlgorithm> clazz = cutAlgorithm.getClass();
		Field[] fields = clazz.getFields();
		
		for (Field field : fields) {
			AlgorithmSetting algorithmSetting = field.getAnnotation(AlgorithmSetting.class);
			
			if (algorithmSetting != null) {
				Logger.info("From algorithm: %s, found setting: %s", cutAlgorithm.getClass().getSimpleName(), algorithmSetting.key());
				
				ReflectionUtils.silentlyRemoveFinalProtection(field);
				
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
		return i18n.string(getBaseTranslationKey(cutAlgorithm) + ".name");
	}
	
	public static String getTranslatedDescription(CutAlgorithm cutAlgorithm) {
		return i18n.string(getBaseTranslationKey(cutAlgorithm) + ".description");
	}
	
	public static String getBaseTranslationKey(AlgorithmSettingEntry algorithmSettingEntry) {
		return getBaseTranslationKey(algorithmSettingEntry.getAlgorithmInstance()) + ".setting." + algorithmSettingEntry.getI18nKey();
	}
	
	public static String getTranslatedName(AlgorithmSettingEntry algorithmSettingEntry) {
		return i18n.string(getBaseTranslationKey(algorithmSettingEntry) + ".name");
	}
	
	public static String getTranslatedDescription(AlgorithmSettingEntry algorithmSettingEntry) {
		return i18n.string(getBaseTranslationKey(algorithmSettingEntry) + ".description");
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
		private final CutAlgorithm algorithmInstance;
		private final Field field;
		private final AlgorithmSetting algorithmSetting;
		private final String i18nKey;
		private final Class<?> type;
		
		/* Constructor */
		public AlgorithmSettingEntry(CutAlgorithm algorithmInstance, Field field, AlgorithmSetting algorithmSetting) {
			this.algorithmInstance = algorithmInstance;
			this.field = field;
			this.algorithmSetting = algorithmSetting;
			
			this.i18nKey = algorithmSetting.key();
			this.type = field.getType();
			
			field.setAccessible(true);
		}
		
		public CutAlgorithm getAlgorithmInstance() {
			return algorithmInstance;
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
				return field.get(algorithmInstance);
			} catch (IllegalArgumentException | IllegalAccessException exception) {
				throw new RuntimeException("Unexpected exception when getting field value.", exception);
			}
		}
		
		public boolean setValue(Object value) {
			try {
				ReflectionUtils.setFinal(field, Modifier.isStatic(field.getModifiers()) ? null : algorithmInstance, value);
				// field.set(Modifier.isStatic(field.getModifiers()) ? null : algorithmInstance, value);
				
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