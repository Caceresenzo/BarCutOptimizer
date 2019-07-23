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
			o("editor.button.edit-cuts", "MODIFIER LES DONNéES".toUpperCase());
			
			o("cut-editor.panel.data", "Données");
			o("cut-editor.panel.data.table.header.column.length", "LONGUEUR");
			o("cut-editor.panel.data.table.header.column.angle-a", "ANGLE A");
			o("cut-editor.panel.data.table.header.column.angle-b", "ANGLE B");
			o("cut-editor.panel.data.table.header.column.quantity", "QUANTITé".toUpperCase());
			o("cut-editor.panel.data.table.header.column.remove", "SUPPRIMER");
			o("cut-editor.panel.algorithm", "Algorithme");
			o("cut-editor.panel.algorithm-settings", "Parametres de l'Algorithme");
			o("cut-editor.panel.bar-length", "Longueur de barre");
			o("cut-editor.button.add-new-line", "AJOUTER UNE LIGNE");
			o("cut-editor.button.ok", "VALIDER");
			o("cut-editor.button.cancel", "ANNULER");

			o("cut-algorithm.filling.name", "Remplissage");
			o("cut-algorithm.filling.description", "Remplie les barres en allant de la plus long. vers la moins long.");
			o("cut-algorithm.filling.setting.start-offset.name", "Décallage de début");
			o("cut-algorithm.filling.setting.start-offset.description", "Longueur qui ne sera pas utilisé au début d'une barre.");
			o("cut-algorithm.filling.setting.end-offset.name", "Décallage de fin");
			o("cut-algorithm.filling.setting.end-offset.description", "Longueur qui ne sera pas utilisé a la fin d'une barre.");
			o("cut-algorithm.filling.setting.cut-offset.name", "Décallage de coupe");
			o("cut-algorithm.filling.setting.cut-offset.description", "Longueur qui perdu entre 2 coupes.");
			
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