package dev.caceresenzo.barcutoptimizer.logic.algorithms;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dev.caceresenzo.barcutoptimizer.language.I18n;
import dev.caceresenzo.barcutoptimizer.logic.algorithms.annotations.AlgorithmSetting;
import dev.caceresenzo.barcutoptimizer.logic.algorithms.impl.FillingCutAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlgorithmManager {
	
	/* Singleton */
	private static AlgorithmManager INSTANCE;
	
	/* Algorithm */
	private List<CutAlgorithm> algorithms;
	
	/* Private Constructor */
	private AlgorithmManager() {
		this.algorithms = new ArrayList<>();
	}
	
	public void initialize() {
		register(new FillingCutAlgorithm());
	}
	
	private void register(CutAlgorithm cutAlgorithm) {
		Objects.requireNonNull(cutAlgorithm);
		
		log.info("Added algorithm: {}", cutAlgorithm.getClass().getSimpleName());
		
		algorithms.add(cutAlgorithm);
	}
	
	public List<AlgorithmSettingEntry> getAlgorithmSettingEntries(CutAlgorithm cutAlgorithm) {
		return Arrays.asList(cutAlgorithm.getClass().getDeclaredFields())
				.stream()
				.filter((field) -> field.isAnnotationPresent(AlgorithmSetting.class))
				.map((field) -> new AlgorithmSettingEntry(cutAlgorithm, field, field.getAnnotation(AlgorithmSetting.class)))
				.collect(Collectors.toList());
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
//		return I18n.string(getBaseTranslationKey(algorithmSettingEntry) + ".name");
		return getBaseTranslationKey(algorithmSettingEntry);
	}
	
	public static String getTranslatedDescription(AlgorithmSettingEntry algorithmSettingEntry) {
//		return I18n.string(getBaseTranslationKey(algorithmSettingEntry) + ".description");
		return getBaseTranslationKey(algorithmSettingEntry);
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