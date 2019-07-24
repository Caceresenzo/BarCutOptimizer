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
			o("multiple-element-letter", "s");
			
			o("application.title", "Optimiseur de coupe de barre");
			
			o("dialog.error.title", "Erreur");
			
			o("start-window.button.new.html", "NOUVELLE\nOPTIMISATION\n<small>Aucune sauv. possible</small>");
			o("start-window.button.import.html", "IMPORTER\nUN PDF\n<small>EasyWin</small>");
			
			o("import.dialog.title", "Importer un fichier...");
			o("import.error.file-not-accessible", "Le fichier n'est pas lisible ou accessible.");
			o("import.error.no-bar-found", "Aucune barre n'a été trouvé.");
			o("import.error.failed-to-import", "Erreur lors de l'importation: \n%s");
			
			o("dialog.new-bar-reference.message", "Référence de la barre");
			
			o("editor.tree.root", "Racine");
			o("editor.tree.item.bar-reference.format", "%s");
			o("editor.tree.item.cut-group.format", "B. %s (%s coupe(s))");
			o("editor.tree.item.cut.format", "%smm %s°/%s°");
			o("editor.tree.popup-menu.item.remove", "Supprimer");
			o("editor.tree.popup-menu.item.empty", "Tout vider");
			
			o("editor.list.item.cut-group.title.format", "%smm - %s coupe(s) - chute de %smm");
			o("editor.list.item.cut-group.title.format.without-remaining", "%smm - %s coupe(s) - chute inconnue");
			
			o("editor.button.add-new-bar-reference", "AJOUTER UNE RÉFÉRENCE");
			o("editor.button.export", "EXPORTER EN PDF...");
			o("editor.button.edit-cuts", "MODIFIER LES DONNÉES");
			
			o("cut-editor.frame.title", "Édition de données");
			o("cut-editor.panel.data", "Données");
			o("cut-editor.panel.data.table.header.column.length", "LONGUEUR");
			o("cut-editor.panel.data.table.header.column.angle-a", "ANGLE A");
			o("cut-editor.panel.data.table.header.column.angle-b", "ANGLE B");
			o("cut-editor.panel.data.table.header.column.quantity", "QUANTITÉ");
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
			o("cut-algorithm.filling.setting.cut-offset.description", "Longueur qui sera considéré comme \"perdu\" entre 2 coupes.");
			o("cut-algorithm.error.failed", "L'optimisation a échoué.\nL'optimiseur a renvoyé une erreur: %s\nVoir la console pour plus de détail.");
			
			o("export.frame.title", "Exportation en PDF");
			o("export.panel.destination", "Destination");
			o("export.panel.progress", "Avancé");
			o("export.button.select-file", "Fichier...");
			o("export.button.export", "EXPORTER");
			o("export.button.close", "FERMER");
			o("export.pdf.eta.initializing", "Chargement...");
			o("export.pdf.eta.generating-pdf", "Création du PDF...");
			o("export.pdf.eta.cleaning-up", "Suppression des fichiers temporaires...");
			o("export.pdf.eta.opening-result", "Ouverture du résultat...");
			o("export.eta.not-even-started", "ATTENTE");
			o("export.eta.done.dialog-title", "Exportation terminé!");
			o("export.eta.done", "L'exportation est terminé.\nChemin du fichier: %s");
			o("export.error.generic", "Une erreur est survenu lors de l'exportation.\nMessage: %s\nVoir la console pour plus d'information.");
			
			o("exporter.word.bar-reference.format", "REF. %s");
			o("exporter.word.quantity", "QUANTITÉ");
			o("exporter.word.cut", "COUPE");
			
			o("exporter.column.bar", "BARRE");
			o("exporter.column.bar.item.position", "N°%s");
			o("exporter.column.bar.item.length", "%s mm");
			o("exporter.column.bar.item.size", "%s élément%s");
			o("exporter.column.length", "LONGUEUR");
			o("exporter.column.angle.a", "ANGLE A");
			o("exporter.column.angle.b", "ANGLE B");
			o("exporter.footer.page", "PAGE LOCALE: %s\nPAGE GLOBAL: %s");
			o("exporter.warning.low-remaining", "RESTE FAIBLE");
			
			o("application.copyright.full", "OPTIMISEUR DE COUPE\nCRÉE PAR ENZO CACERES POUR L'ENTREPRISE NEGRO SA");
			
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