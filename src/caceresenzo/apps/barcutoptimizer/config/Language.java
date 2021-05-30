package caceresenzo.apps.barcutoptimizer.config;

import caceresenzo.libs.internationalization.HardInternationalization;
import caceresenzo.libs.internationalization.i18n;

public class Language {
	
	public static final String LANGUAGE_FRENCH = "Fran�ais";
	
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
			o("error.parse-cli", "Une erreur est survenu lors du traitement de l'interface de commande.\nMessage: %s");
			
			o("start-window.button.new.html", "NOUVELLE\nOPTIMISATION\n<small>Aucune sauv. possible</small>");
			o("start-window.button.import.html", "IMPORTER\nUN PDF\n<small>EasyWin</small>");
			
			o("import.dialog.title", "Importer un fichier...");
			o("import.error.file-not-accessible", "Le fichier n'est pas lisible ou accessible.");
			o("import.error.no-bar-found", "Aucune barre n'a �t� trouv�.");
			o("import.error.failed-to-import", "Erreur lors de l'importation: \n%s");
			
			o("dialog.new-bar-reference.message", "R�f�rence de la barre");
			o("dialog.loading-import.title", "Ouverture d'un fichier");
			o("dialog.loading-import.message", "Traitement du fichier...");
			
			o("editor.tree.root", "Racine");
			o("editor.tree.item.bar-reference.format", "%s");
			o("editor.tree.item.cut-group.format", "B. %s (%s coupe(s))");
			o("editor.tree.item.cut.format", "%smm %s�/%s�");
			o("editor.tree.popup-menu.item.remove", "Supprimer");
			o("editor.tree.popup-menu.item.empty", "Tout vider");
			o("editor.list.item.bar-reference-information.title", "Informations");
			o("editor.list.item.bar-reference-information.item.consumed-bar-count", "Total barre");
			o("editor.list.item.bar-reference-information.item.total-cuts-count", "Total coupe");
			o("editor.list.item.cut-group.title.format", "%smm - %s coupe(s) - chute de %smm");
			o("editor.list.item.cut-group.title.format.without-remaining", "%smm - %s coupe(s) - chute inconnue");
			o("editor.button.add-new-bar-reference", "AJOUTER UNE R�F�RENCE");
			o("editor.button.export", "EXPORTER EN PDF...");
			o("editor.button.edit-cuts", "MODIFIER LES DONN�ES");
			
			o("cut-editor.frame.title", "�dition de donn�es");
			o("cut-editor.panel.data", "Donn�es");
			o("cut-editor.panel.data.table.header.column.length", "LONGUEUR");
			o("cut-editor.panel.data.table.header.column.angle-a", "ANGLE A");
			o("cut-editor.panel.data.table.header.column.angle-b", "ANGLE B");
			o("cut-editor.panel.data.table.header.column.quantity", "QUANTIT�");
			o("cut-editor.panel.data.table.header.column.remove", "SUPPRIMER");
			o("cut-editor.panel.algorithm", "Algorithme");
			o("cut-editor.panel.algorithm-settings", "Parametres de l'Algorithme");
			o("cut-editor.panel.bar-length", "Longueur de barre");
			o("cut-editor.button.add-new-line", "AJOUTER UNE LIGNE");
			o("cut-editor.button.ok", "VALIDER");
			o("cut-editor.button.cancel", "ANNULER");
			
			o("cut-algorithm.error.failed", "L'optimisation a �chou�.\nL'optimiseur a renvoy� une erreur: %s\nVoir la console pour plus de d�tail.");
			o("cut-algorithm.filling.name", "Remplissage");
			o("cut-algorithm.filling.description", "Remplie les barres en allant de la plus long. vers la moins long.");
			o("cut-algorithm.filling.setting.start-offset.name", "D�callage de d�but");
			o("cut-algorithm.filling.setting.start-offset.description", "Longueur qui ne sera pas utilis� au d�but d'une barre.");
			o("cut-algorithm.filling.setting.end-offset.name", "D�callage de fin");
			o("cut-algorithm.filling.setting.end-offset.description", "Longueur qui ne sera pas utilis� a la fin d'une barre.");
			o("cut-algorithm.filling.setting.cut-offset.name", "D�callage de coupe");
			o("cut-algorithm.filling.setting.cut-offset.description", "Longueur qui sera consid�r� comme \"perdu\" entre 2 coupes.");
			
			o("export.frame.title", "Exportation en PDF");
			o("export.panel.destination", "Destination");
			o("export.panel.progress", "Avanc�");
			o("export.button.select-file", "Fichier...");
			o("export.button.export", "EXPORTER");
			o("export.button.close", "FERMER");
			o("export.pdf.eta.initializing", "Chargement...");
			o("export.pdf.eta.generating-pdf", "Cr�ation du PDF...");
			o("export.pdf.eta.cleaning-up", "Suppression des fichiers temporaires...");
			o("export.pdf.eta.opening-result", "Ouverture du r�sultat...");
			o("export.eta.not-even-started", "ATTENTE");
			o("export.eta.done.dialog-title", "Exportation termin�!");
			o("export.eta.done", "L'exportation est termin�.\nChemin du fichier: %s");
			o("export.error.generic", "Une erreur est survenu lors de l'exportation.\nMessage: %s\nVoir la console pour plus d'information.");
			
			o("exporter.word.bar-reference.format", "REF. %s");
			o("exporter.word.quantity", "QUANTIT�");
			o("exporter.word.leftover", "CHUTE");
			o("exporter.word.cut", "COUPE");
			
			o("exporter.column.bar", "BARRE");
			o("exporter.column.bar.item.position", "N�%s");
			o("exporter.column.bar.item.length", "%s mm");
			o("exporter.column.bar.item.size", "%s �l�ment%s");
			o("exporter.column.length", "LONGUEUR");
			o("exporter.column.angle.a", "ANGLE A");
			o("exporter.column.angle.b", "ANGLE B");
			o("exporter.footer.page", "PAGE LOCALE: %s\nPAGE GLOBAL: %s");
			o("exporter.message.consumption", "%s coupe%s dans %s barre%s");
			o("exporter.warning.unknown-remaining", "INCONNU");
			o("exporter.warning.unknown-remaining.estimated", "ESTIMATION: %-8s");
			o("exporter.warning.low-remaining", "RESTE FAIBLE");
			
			o("application.copyright.full", "OPTIMISEUR DE COUPE\nCR�E PAR ENZO CACERES POUR L'ENTREPRISE NEGRO SA");
			
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