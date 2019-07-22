package caceresenzo.apps.barcutoptimizer.config;

import caceresenzo.libs.internationalization.HardInternationalization;
import caceresenzo.libs.internationalization.i18n;

public class Language {
	
	public static final String LANGUAGE_FRENCH = "Français";
	
	private static Language LANGUAGE;
	private HardInternationalization selected = null;
	
	private Language() {
		selected = new French();
	}
	
	public void initialize() {
		i18n.setSelectedLanguage(LANGUAGE_FRENCH);
	}
	
	private class French extends HardInternationalization {
		
		public French() {
			super();
			register(LANGUAGE_FRENCH);
		}
		
		@Override
		public void set() {
			o("application.title", "Optimiseur de coupe de barre");
			
			o("start-window.button.new.html", "NOUVELLE\nOPTIMISATION\n<small>Aucune sauv. possible</small>");
			o("start-window.button.import.html", "IMPORTER\nUN PDF\n<small>EasyWin</small>");

			o("editor.tree.root", "Racine");
			o("editor.tree.item.bar-reference.format", "%s");
			o("editor.tree.item.cut-group.format", "B. %s (%s coupe(s))");
			o("editor.tree.item.cut.format", "%smm %s°/%s°");

			o("editor.list.item.cut-group.title.format", "%smm - %s coupe(s) - chute de %smm");
			o("editor.list.item.cut-group.title.format.without-remaining", "%smm - %s coupe(s) - chute inconnu");

			o("editor.button.add-new-bar-reference", "AJOUTER UNE RéFéRENCE".toUpperCase());
			o("", "");
		}
		
	}
	
	public HardInternationalization getSelected() {
		return selected;
	}
	
	public static Language get() {
		if (LANGUAGE == null) {
			LANGUAGE = new Language();
		}
		
		return LANGUAGE;
	}
	
	public static HardInternationalization getActual() {
		return get().getSelected();
	}
	
}