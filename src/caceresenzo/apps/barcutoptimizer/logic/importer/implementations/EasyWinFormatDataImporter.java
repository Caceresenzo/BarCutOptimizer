package caceresenzo.apps.barcutoptimizer.logic.importer.implementations;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import caceresenzo.apps.barcutoptimizer.logic.importer.DataImporter;
import caceresenzo.apps.barcutoptimizer.models.BarReference;
import caceresenzo.apps.barcutoptimizer.models.Cut;
import caceresenzo.apps.barcutoptimizer.models.CutGroup;
import caceresenzo.libs.list.ListUtils;

public class EasyWinFormatDataImporter implements DataImporter {
	
	public static final String NEW_BAR_REGEX = "[\\d]+x [\\d]+ .*";
	public static final String NEW_REFERENCE_SEPERATOR = "BARRE N°/REP LONG. COUP1 COUP2 P CASE CHUTE";
	public static final String PAGE_SEPARATOR_PREFIX = "DEBITS PROFILS ";
	public static final String[] BAR_REFERENCE_WORD_BLACKLIST = { "FICTIF", "FICTIVE" };

	public static final int INVALID_INDEX = -1;
	
	@Override
	public List<BarReference> loadFromFile(File file) throws Exception {
		PDDocument document = PDDocument.load(file);
		
		boolean isEncrypted = document.isEncrypted();
		List<BarReference> barReferences = null;
		
		if (!isEncrypted) {
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(document);
			
			barReferences = process(text);
		}
		
		document.close();
		
		if (isEncrypted) {
			throw new IllegalStateException("The PDF file is encrypted, please decrypt it before importing.");
		}
		
		return barReferences;
	}
	
	private List<BarReference> process(String rawText) {
		List<BarReference> barReferences = new ArrayList<>();
		final String[] lines = rawText.split("\n");
		
		autoTrim(lines);
		
		int currentIndex = 0;
		while ((currentIndex = nextReferenceIndex(lines, currentIndex)) != INVALID_INDEX) {
			String referenceName = lines[currentIndex];
			
			currentIndex++; /* Ignore the separator */
			
			if (isItemContainingInStringArray(BAR_REFERENCE_WORD_BLACKLIST, referenceName.toUpperCase())) {
				continue;
			}
			
			CutGroup currentCutGroup = null;
			
			BarReference barReference = where(barReferences, (item) -> item.getName().equals(referenceName));
			if (barReference == null) {
				barReferences.add(barReference = new BarReference(referenceName, new ArrayList<>()));
			}
			
			List<CutGroup> cutGroups = barReference.getCutGroups();
			
			for (int index = 0;; index++) {
				String line = lines[++currentIndex];
				
				if (line.matches(NEW_BAR_REGEX)) {
					addIfNotNull(cutGroups, currentCutGroup);
					
					currentCutGroup = new CutGroup(Integer.parseInt(line.split(" ")[1]), CutGroup.UNKNOWN_REMAINING, new ArrayList<>());
				} else {
					if (index == 0) {
						currentCutGroup = ListUtils.getLastestItem(cutGroups);
					}
				}
				
				if (line.endsWith("%") || line.startsWith(PAGE_SEPARATOR_PREFIX)) {
					addIfNotNull(cutGroups, currentCutGroup);
					currentCutGroup = null;
					
					break;
				} else {
					Cut cut = Cut.fromExtractedLine(line).atLine(currentIndex);
					
					currentCutGroup.getCuts().add(cut);
				}
			}
			
			addIfNotNull(cutGroups, currentCutGroup);
		}
		
		return barReferences;
	}
	
	/**
	 * Use the method {@link String#trim()} on all <code>lines</code>.
	 * 
	 * @param lines
	 *            {@link Array} of {@link String}.
	 */
	private void autoTrim(String[] lines) {
		for (int i = 0; i < lines.length; i++) {
			lines[i] = lines[i].trim();
		}
	}
	
	/**
	 * Find the next bar reference by searching for the {@link #NEW_REFERENCE_SEPERATOR} usage.
	 * 
	 * @param lines
	 *            {@link Array} of {@link String}.
	 * @param startIndex
	 *            Index of where to (re)start the search.
	 * @return The reference index if found, or {@link #INVALID_INDEX} if there are no more lines left to search into or if anything hasn't been found.
	 */
	private int nextReferenceIndex(String[] lines, int startIndex) {
		if (startIndex < lines.length) {
			for (int index = startIndex + 2; index < lines.length; index++) {
				String line = lines[index];
				
				if (NEW_REFERENCE_SEPERATOR.equals(line)) {
					return index - 1;
				}
			}
		}
		
		return INVALID_INDEX;
	}
	
	public <T> void addIfNotNull(List<T> list, T element) {
		if (element != null && !list.contains(element)) {
			list.add(element);
		}
	}
	
	private <T> T where(List<T> list, Predicate<T> validator) {
		for (T t : list) {
			if (validator.test(t)) {
				return t;
			}
		}
		
		return null;
	}
	
	static boolean isItemContainingInStringArray(String[] array, String itemToCheck) {
		for (String string : array) {
			if (itemToCheck.contains(string)) {
				return true;
			}
		}
		return false;
	}
	
}