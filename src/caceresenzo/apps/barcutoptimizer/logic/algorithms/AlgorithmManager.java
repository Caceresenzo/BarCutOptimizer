package caceresenzo.apps.barcutoptimizer.logic.algorithms;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import caceresenzo.apps.barcutoptimizer.logic.algorithms.annotations.AlgorithmSetting;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.implementations.LowLossCutAlgorithm;

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
		register(new LowLossCutAlgorithm());
		
		algorithms.forEach((algorithm) -> extractSettingEntries(algorithm));
	}
	
	private void register(CutAlgorithm cutAlgorithm) {
		Objects.requireNonNull(cutAlgorithm);
		
		System.out.println("Added algorithm: " + cutAlgorithm.getClass().getSimpleName());
		
		algorithms.add(cutAlgorithm);
		settingEntries.put(cutAlgorithm, new ArrayList<>());
	}
	
	private void extractSettingEntries(CutAlgorithm cutAlgorithm) {
		Class<? extends CutAlgorithm> clazz = cutAlgorithm.getClass();
		Field[] fields = clazz.getFields();
		
		for (Field field : fields) {
			AlgorithmSetting algorithmSetting = field.getAnnotation(AlgorithmSetting.class);
			
			if (algorithmSetting != null) {
				System.out.println("From algorithm: " + cutAlgorithm.getClass().getSimpleName() + ", found setting: " + algorithmSetting.key());
				
				settingEntries.get(cutAlgorithm).add(new AlgorithmSettingEntry(cutAlgorithm, field, algorithmSetting));
			}
		}
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
				field.set(Modifier.isStatic(field.getModifiers()) ? null : algorithmInstance, value);
				
				return true;
			} catch (Exception exception) {
				exception.printStackTrace();
				return false;
			}
		}
		
	}
	
}