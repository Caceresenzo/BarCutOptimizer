package caceresenzo.apps.barcutoptimizer.logic.exporter.implementations;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import caceresenzo.apps.barcutoptimizer.BarCutOptimizer;
import caceresenzo.apps.barcutoptimizer.assets.Assets;
import caceresenzo.apps.barcutoptimizer.config.Language;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.implementations.FillingCutAlgorithm;
import caceresenzo.apps.barcutoptimizer.logic.exporter.DataExporter;
import caceresenzo.apps.barcutoptimizer.logic.exporter.ExporterCallback;
import caceresenzo.apps.barcutoptimizer.models.BarReference;
import caceresenzo.apps.barcutoptimizer.models.Cut;
import caceresenzo.apps.barcutoptimizer.models.CutGroup;
import caceresenzo.apps.barcutoptimizer.models.CutTableInput;
import caceresenzo.apps.barcutoptimizer.models.UnoptimizedCutList;
import caceresenzo.apps.barcutoptimizer.ui.components.BarCutPanel;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.random.Randomizer;
import caceresenzo.libs.string.StringUtils;

public class PdfDataExporter implements DataExporter {
	
	/* Callback Constants */
	public static final String ETA_INITIALIZING = "initializing";
	public static final String ETA_GENERATING_PDF = "generating-pdf";
	public static final String ETA_CLEANING_UP = "cleaning-up";
	public static final String ETA_OPENING_RESULT = "opening-result";
	
	public static final int ETA_COUNT = 4;
	
	/* Constants */
	public static final String CUT_GROUP_FILE_FORMAT = "group-%s.png";
	
	public static final int BAR_CUT_RENDER_WIDTH = 800;
	public static final int BAR_CUT_RENDER_HEIGHT = 20;
	public static final double BAR_CUT_IMAGE_HEIGHT = BAR_CUT_RENDER_HEIGHT * 0.65;
	
	public static final int PAGE_MARGIN_HORIZONTAL = 40;
	public static final int PAGE_MARGIN_VERTICAL = 40;
	
	public static final int FONT_SIZE = 12;
	
	public static final int SPACE_BETWEEN_COLUMN = (int) (PAGE_MARGIN_HORIZONTAL * (FONT_SIZE / 4f));
	public static final int SMALL_SPACE_BETWEEN_COLUMN = (int) (PAGE_MARGIN_HORIZONTAL * (FONT_SIZE / 8f));
	
	public static final boolean ADD_LINE_BETWEEN_CELL_IN_COUNT_TABLE = true;
	public static final boolean CLEANUP_AFTER_EXPORTING = true;
	public static final boolean OPEN_FILE_AT_END = true;
	public static final boolean ENABLE_WARNING = true;
	
	public static final float LOW_REMAINING_THRESHOLD = 10.0f;
	
	/* Callback */
	private ExporterCallback callback;
	
	/* Variables */
	private PDDocument document;
	private PDFont font;
	private PDPage lastestPage;
	
	private final List<File> temporaryFiles;
	
	/* Constructor */
	public PdfDataExporter() {
		this.temporaryFiles = new ArrayList<File>() {
			@Override
			public boolean add(File file) {
				file.deleteOnExit();
				return super.add(file);
			}
		};
	}
	
	@SuppressWarnings("unused")
	@Override
	public void exportToFile(List<BarReference> barReferences, File file) throws Exception {
		notifyInitialization();
		
		File exportCacheFolder = new File(BarCutOptimizer.CACHE_FOLDER, "export");
		File exportTemporaryFolder = new File(exportCacheFolder, UUID.randomUUID().toString());
		
		prepareNewDocument();
		
		notifyNextEta(ETA_GENERATING_PDF);
		int etaCurrentMax = BarReference.countAllCutGroupInList(barReferences) + barReferences.size();
		int etaCurrentProgress = 0;
		
		int globalPageCounter = 0;
		for (BarReference barReference : barReferences) {
			File barReferenceBaseFolder = new File(exportTemporaryFolder, FileUtils.replaceIllegalChar(barReference.getName()));
			
			List<CutGroup> cutGroups = barReference.getCutGroups();
			ListIterator<CutGroup> iterator = cutGroups.listIterator();
			
			int localIndex = 0;
			int localPageCounter = 0;
			while (iterator.hasNext()) {
				PDPage page = createPage();
				PDRectangle mediaBox = page.getMediaBox();
				final int maxY = (int) (mediaBox.getHeight() - PAGE_MARGIN_VERTICAL);
				int currentY = PAGE_MARGIN_VERTICAL;
				
				int evenLocalIndex = 0;
				while (currentY < maxY) {
					CutGroup cutGroup = iterator.next();
					List<Cut> cuts = cutGroup.getCuts();
					
					if (!haveOptimizationEnoughSpace(cutGroup, currentY, maxY)) {
						iterator.previous();
						break;
					}
					
					File cutGroupFile = new File(barReferenceBaseFolder, String.format(CUT_GROUP_FILE_FORMAT, localIndex));
					
					try {
						saveCutGroupPicture(cutGroup, cutGroupFile);
						
						PDImageXObject pdImage = PDImageXObject.createFromFile(cutGroupFile.getAbsolutePath(), document);
						
						try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false)) {
							if (evenLocalIndex == 0) {
								printHeader(contentStream, mediaBox, barReference);
								printFooter(contentStream, mediaBox);
								
								printPageFooter(contentStream, mediaBox, globalPageCounter, localPageCounter);
								
								contentStream.moveTo(PAGE_MARGIN_HORIZONTAL, (float) (mediaBox.getHeight() - PAGE_MARGIN_VERTICAL - (FONT_SIZE * 1.4)));
								contentStream.lineTo(mediaBox.getWidth() - PAGE_MARGIN_HORIZONTAL, (float) (mediaBox.getHeight() - PAGE_MARGIN_VERTICAL - (FONT_SIZE * 1.4)));
								contentStream.stroke();
								
								currentY += FONT_SIZE;
							}
							
							currentY += BAR_CUT_IMAGE_HEIGHT + (FONT_SIZE);
							float inversedY = mediaBox.getHeight() - currentY;
							
							contentStream.drawImage(pdImage, PAGE_MARGIN_HORIZONTAL, inversedY, mediaBox.getWidth() - PAGE_MARGIN_HORIZONTAL * 2, (int) BAR_CUT_IMAGE_HEIGHT);
							
							int startX = PAGE_MARGIN_HORIZONTAL;
							
							int usedY1 = printTextColumn(contentStream, startX, inversedY, i18n.string("exporter.column.bar"), Arrays.asList(
									i18n.string("exporter.column.bar.item.position", (localIndex + 1)),
									i18n.string("exporter.column.bar.item.length", cutGroup.getBarLength()),
									i18n.string("exporter.column.bar.item.size", cutGroup.getCutCount(), cutGroup.getCutCount() > 1 ? i18n.string("multiple-element-letter") : "")));
							
							int usedY2 = 0; /* Set 3 times just for the look ;) */
							usedY2 = printTextColumn(contentStream, startX + SPACE_BETWEEN_COLUMN, inversedY, i18n.string("exporter.column.length"), cutListToLines(cuts, (cut) -> StringUtils.prefill(String.valueOf(cut.getLength()), " ", 8)));
							usedY2 = printTextColumn(contentStream, startX + SPACE_BETWEEN_COLUMN * 2, inversedY, i18n.string("exporter.column.angle.a"), cutListToLines(cuts, (cut) -> StringUtils.prefill(cut.getCutAngleA() + "°", " ", 7)));
							usedY2 = printTextColumn(contentStream, startX + SPACE_BETWEEN_COLUMN * 3, inversedY, i18n.string("exporter.column.angle.b"), cutListToLines(cuts, (cut) -> StringUtils.prefill(cut.getCutAngleB() + "°", " ", 7)));
							
							int usedY = Math.max(usedY1, usedY2);
							
							printSimpleHorizontalLine(contentStream, startX, mediaBox.getWidth() - PAGE_MARGIN_HORIZONTAL, (float) (inversedY - (FONT_SIZE * 1.4f)));
							printSimpleVerticalLine(contentStream, (float) ((startX + SPACE_BETWEEN_COLUMN) * 0.91), inversedY, (float) (inversedY - usedY - (FONT_SIZE * 1.4f)));
							
							/* Printing the remaining bar length */
							printSimpleHorizontalLine(contentStream, startX, mediaBox.getWidth() - PAGE_MARGIN_HORIZONTAL, (float) (inversedY - usedY));
							printSimpleHorizontalLine(contentStream, startX, mediaBox.getWidth() - PAGE_MARGIN_HORIZONTAL, (float) (inversedY - (FONT_SIZE * 1.4f) - usedY));
							printSimpleText(contentStream, startX, (float) (inversedY - FONT_SIZE - usedY), FONT_SIZE, "CHUTE");
							printSimpleText(contentStream, startX + SPACE_BETWEEN_COLUMN, (float) (inversedY - FONT_SIZE - usedY), FONT_SIZE, StringUtils.prefill(String.valueOf(cutGroup.getRemainingBarLength()), " ", 8));
							if (ENABLE_WARNING && cutGroup.getRemainingBarLength() < LOW_REMAINING_THRESHOLD) {
								String warningMessage = i18n.string("exporter.warning.low-remaining");
								float textWidth = warningMessage.length() * (font.getAverageFontWidth() / 1000) * FONT_SIZE;
								
								printSimpleText(contentStream, mediaBox.getWidth() - startX - textWidth, (float) (inversedY - FONT_SIZE - usedY), FONT_SIZE, warningMessage);
							}
							
							usedY += (FONT_SIZE * 1.4);
							currentY += usedY;
						}
						
						Logger.info("Y: %-4s -> inserted image: %s", currentY, cutGroupFile.getPath());
					} catch (Exception exception) {
						exception.printStackTrace();
					}
					
					publishProgress(++etaCurrentProgress, etaCurrentMax);
					
					if (!iterator.hasNext()) {
						break;
					}
					
					localIndex++;
					evenLocalIndex++;
				}
				
				globalPageCounter++;
				localPageCounter++;
			}
			
			Map<Cut, Integer> countMap = barReference.computeCutCountMap();
			ListIterator<Cut> cutIterator = new ArrayList<Cut>(countMap.keySet()).listIterator();
			int alreadyCountedCut = 0;
			
			while (cutIterator.hasNext()) {
				PDPage page = createPage();
				PDRectangle mediaBox = page.getMediaBox();
				final int maxY = (int) (mediaBox.getHeight() - PAGE_MARGIN_VERTICAL);
				int currentY = PAGE_MARGIN_VERTICAL;
				
				int evenLocalIndex = 0;
				while (currentY < maxY) {
					Cut cut = cutIterator.next();
					int count = countMap.get(cut);
					
					alreadyCountedCut++;
					
					try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false)) {
						float cellHeight = (float) (FONT_SIZE * 1.5);
						
						if (evenLocalIndex == 0) {
							printHeader(contentStream, mediaBox, barReference);
							printFooter(contentStream, mediaBox);
							
							printPageFooter(contentStream, mediaBox, globalPageCounter, localPageCounter);
							
							currentY += FONT_SIZE * 2;
							
							float textY = mediaBox.getHeight() - PAGE_MARGIN_VERTICAL - (FONT_SIZE * 3f);
							
							printSimpleText(contentStream, PAGE_MARGIN_HORIZONTAL, textY, FONT_SIZE, i18n.string("exporter.word.quantity").toUpperCase());
							printSimpleText(contentStream, PAGE_MARGIN_HORIZONTAL + SMALL_SPACE_BETWEEN_COLUMN + FONT_SIZE / 2f, textY, FONT_SIZE, i18n.string("exporter.word.cut"));
							
							printSimpleHorizontalLine(contentStream, PAGE_MARGIN_HORIZONTAL, mediaBox.getWidth() - PAGE_MARGIN_HORIZONTAL, textY - (FONT_SIZE / 2f));
							
							int maxLineY = (int) Math.max((textY - FONT_SIZE / 2f - cellHeight * (countMap.size() - alreadyCountedCut + 1)), PAGE_MARGIN_HORIZONTAL);
							
							printSimpleVerticalLine(contentStream, PAGE_MARGIN_HORIZONTAL + SMALL_SPACE_BETWEEN_COLUMN, (float) (textY + FONT_SIZE), maxLineY);
							
							currentY += FONT_SIZE * 2.5;
						}
						
						float inversedY = mediaBox.getHeight() - currentY;
						
						printSimpleText(contentStream, PAGE_MARGIN_HORIZONTAL, inversedY, FONT_SIZE, StringUtils.prefill(String.format("%sx", count), " ", 8));
						printSimpleText(contentStream, PAGE_MARGIN_HORIZONTAL + SMALL_SPACE_BETWEEN_COLUMN + FONT_SIZE / 2f, inversedY, FONT_SIZE, String.format("%-7s    %2s° / %2s°", StringUtils.prefill(String.valueOf(cut.getLength()), " ", 7), cut.getCutAngleA(), cut.getCutAngleB()));
						
						if (ADD_LINE_BETWEEN_CELL_IN_COUNT_TABLE && cutIterator.hasNext()) {
							printSimpleHorizontalLine(contentStream, PAGE_MARGIN_HORIZONTAL, mediaBox.getWidth() - PAGE_MARGIN_HORIZONTAL, (float) (inversedY - FONT_SIZE * 0.4f), 0.1f);
						}
						
						currentY += cellHeight;
					}
					
					if (!cutIterator.hasNext()) {
						break;
					}
					
					evenLocalIndex++;
				}
				
				globalPageCounter++;
				localPageCounter++;
			}
			
			publishProgress(etaCurrentMax, ++etaCurrentProgress);
			
			temporaryFiles.add(barReferenceBaseFolder);
		}
		
		finishDocument(file);
		
		if (CLEANUP_AFTER_EXPORTING) {
			notifyNextEta(ETA_CLEANING_UP);
			cleanUpTemporaryFiles(exportTemporaryFolder);
		}
		
		if (OPEN_FILE_AT_END) {
			notifyNextEta(ETA_OPENING_RESULT);
			Runtime.getRuntime().exec("cmd /c \"" + file.getAbsolutePath() + "\"");
		}
		
		notifyFinish(file);
	}
	
	@Override
	public DataExporter attachCallback(ExporterCallback exporterCallback) {
		this.callback = exporterCallback;
		
		return this;
	}
	
	/**
	 * Create a {@link PDDocument} and store it in private class variable.
	 * 
	 * @return The instance just created.
	 */
	private PDDocument createDocument() {
		return document = new PDDocument();
	}
	
	/**
	 * Load the <code>Consola</code> font and store it in a private class variable.
	 * 
	 * @throws IOException
	 *             If there is an error reading the font stream.
	 */
	private void loadFont() throws IOException {
		font = PDType0Font.load(document, PdfDataExporter.class.getResourceAsStream(Assets.FONT_CONSOLA));
	}
	
	/**
	 * Prepare the document.<br>
	 * This mean calling {@link #createDocument()} and {@link #loadFont()}.
	 * 
	 * @throws IOException
	 *             If there is an error reading the font stream.
	 */
	private void prepareNewDocument() throws IOException {
		createDocument();
		loadFont();
	}
	
	/**
	 * Create a new {@link PDPage page} and automatically add it to the current {@link PDDocument document}.
	 * 
	 * @return The {@link PDPage page} just created.
	 */
	private PDPage createPage() {
		PDPage page = lastestPage = new PDPage();
		document.addPage(page);
		
		return page;
	}
	
	/**
	 * Render a {@link BarCutPanel bar cut} to a {@link BufferedImage buffered image}.
	 * 
	 * @param cutGroup
	 *            {@link CutGroup Cut group} that need to be render.
	 * @return A {@link BufferedImage buffered image} where a {@link BarCutPanel} painted on it.
	 */
	private BufferedImage renderCutGroup(CutGroup cutGroup) {
		while (true) {
			try {
				JPanel panel = new BarCutPanel(cutGroup);
				
				panel.updateUI();
				panel.setSize(BAR_CUT_RENDER_WIDTH, BAR_CUT_RENDER_HEIGHT);
				
				BufferedImage bufferedImage = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics graphics = bufferedImage.getGraphics();
				
				panel.paint(graphics);
				
				graphics.dispose();
				
				return bufferedImage;
			} catch (Exception exception) {
				Logger.warning("javax.swing.RepaintManager.getVolatileOffscreenBuffer() -> java.lang.NullPointerException");
				// exception.printStackTrace();
			}
		}
	}
	
	/**
	 * Save the {@link BufferedImage buffered image} returned by {@link #renderCutGroup(CutGroup)} to a <code>file</code>.<br>
	 * Also the <code>file</code> will be added to the {@link List list} of temporary files.
	 * 
	 * @param cutGroup
	 *            {@link CutGroup Cut group} that will be render.
	 * @param file
	 *            Target file to save it.
	 * @throws IOException
	 *             If an error occurs during writing.
	 */
	private void saveCutGroupPicture(CutGroup cutGroup, File file) throws IOException {
		FileUtils.forceFileCreation(file);
		ImageIO.write(renderCutGroup(cutGroup), "png", file);
		
		temporaryFiles.add(file);
	}
	
	/**
	 * Print a simple text at a specified coordinate.<br>
	 * The text will be split in lines that will be each rendered one after the other.
	 * 
	 * @param contentStream
	 *            The {@link PDPage page}'s writable steam.
	 * @param x
	 *            X position to print the text.
	 * @param y
	 *            Y position to print the text.
	 * @param fontSize
	 *            Specified font size.
	 * @param text
	 *            Text to print.
	 * @throws IOException
	 *             If there is an error writing to the stream.
	 */
	private void printSimpleText(PDPageContentStream contentStream, float x, float y, float fontSize, String text) throws IOException {
		contentStream.beginText();
		contentStream.setFont(font, fontSize);
		contentStream.setLeading(fontSize);
		contentStream.newLineAtOffset(x, y);
		
		String[] lines = text.split("\n");
		for (int index = 0; index < lines.length; index++) {
			contentStream.showText(lines[index]);
			
			if (index != lines.length) {
				contentStream.newLine();
			}
		}
		
		contentStream.endText();
	}
	
	/**
	 * Print a column header at a specified coordinate.<br>
	 * 
	 * @param contentStream
	 *            The {@link PDPage page}'s writable steam.
	 * @param x
	 *            X position to print the column text.
	 * @param inversedY
	 *            Y position to print the column text.
	 * @param columnText
	 *            Column name.
	 * @throws IOException
	 *             If there is an error writing to the stream.
	 */
	private void printColumnHeader(PDPageContentStream contentStream, float x, float inversedY, String columnText) throws IOException {
		contentStream.beginText();
		contentStream.setFont(font, FONT_SIZE);
		contentStream.setLeading(FONT_SIZE);
		contentStream.newLineAtOffset(x, inversedY - FONT_SIZE);
		contentStream.showText(columnText);
		contentStream.endText();
	}
	
	/**
	 * Print a column, including its header and its lines at a specified coordinate.
	 * 
	 * @param contentStream
	 *            The {@link PDPage page}'s writable steam.
	 * @param x
	 *            X position to print the column.
	 * @param inversedY
	 *            Y position to print the column.
	 * @param columnText
	 *            Column name.
	 * @param lines
	 *            {@link List} of {@link String string} representing lines of the column.
	 * @return How much y has been used.
	 * @throws IOException
	 *             If there is an error writing to the stream.
	 * @see #printColumnHeader(PDPageContentStream, float, float, String) Printing the column header.
	 */
	private int printTextColumn(PDPageContentStream contentStream, float x, float inversedY, String columnText, List<String> lines) throws IOException {
		printColumnHeader(contentStream, x, inversedY, columnText);
		
		int usedY = FONT_SIZE * 2;
		
		contentStream.beginText();
		contentStream.setFont(font, FONT_SIZE);
		contentStream.setLeading(FONT_SIZE);
		contentStream.newLineAtOffset(x, inversedY - usedY - (FONT_SIZE / 2f));
		
		for (String line : lines) {
			contentStream.showText(line);
			contentStream.newLine();
		}
		
		contentStream.endText();
		
		usedY += lines.size() * FONT_SIZE;
		
		return usedY;
	}
	
	/**
	 * Compute how much y will be used by an item. This computation is very simplified compared to all the print.
	 * 
	 * @param cutGroup
	 *            {@link CutGroup Cut group} to test.
	 * @param currentY
	 *            Y used to far.
	 * @param maxY
	 *            Maximum y value.
	 * @return Weather or not the computer value and the current y value are still under the max y value.
	 */
	private boolean haveOptimizationEnoughSpace(CutGroup cutGroup, int currentY, int maxY) {
		int theoreticalUsedY = 0;
		
		/* Header */
		// theoreticalUsedY += FONT_SIZE * 2;
		
		/* Bar Cut Image */
		theoreticalUsedY += BAR_CUT_IMAGE_HEIGHT;
		
		/* Text Column Header */
		theoreticalUsedY += FONT_SIZE * 2;
		theoreticalUsedY += FONT_SIZE * 0.5;
		
		/* Text Column Content: max of lines between the information column and the list column */
		theoreticalUsedY += Math.max(3, cutGroup.getCutCount()) * FONT_SIZE;
		
		/* Extra Line after the Column */
		theoreticalUsedY += FONT_SIZE * 1.4;
		
		return currentY + theoreticalUsedY < maxY;
	}
	
	/**
	 * Print a simple horizontal line.
	 * 
	 * @param contentStream
	 *            The {@link PDPage page}'s writable steam.
	 * @param x1
	 *            X start position.
	 * @param x2
	 *            X end position.
	 * @param y
	 *            Horizontal y.
	 * @throws IOException
	 *             If there is an error writing to the stream.
	 * @see #printSimpleHorizontalLine(PDPageContentStream, float, float, float, float) Print a horizontal line with a specified thickness.
	 */
	private void printSimpleHorizontalLine(PDPageContentStream contentStream, float x1, float x2, float y) throws IOException {
		printSimpleHorizontalLine(contentStream, x1, x2, y, -1);
	}
	
	/**
	 * Print a simple horizontal line with a specified <code>thickness</code>.
	 * 
	 * @param contentStream
	 *            The {@link PDPage page}'s writable steam.
	 * @param x1
	 *            X start position.
	 * @param x2
	 *            X end position.
	 * @param y
	 *            Horizontal y.
	 * @throws IOException
	 *             If there is an error writing to the stream.
	 * @see #printSimpleHorizontalLine(PDPageContentStream, float, float, float) Print a horizontal line without specifying a thickness.
	 */
	private void printSimpleHorizontalLine(PDPageContentStream contentStream, float x1, float x2, float y, float thickness) throws IOException {
		if (thickness > 0) {
			contentStream.setLineWidth(thickness);
		}
		
		contentStream.moveTo(x1, y);
		contentStream.lineTo(x2, y);
		contentStream.stroke();
	}
	
	/**
	 * Print a simple vertical line.
	 * 
	 * @param contentStream
	 *            The {@link PDPage page}'s writable steam.
	 * @param x
	 *            Vertical x.
	 * @param y1
	 *            Y start position.
	 * @param y2
	 *            Y end position.
	 * @throws IOException
	 *             If there is an error writing to the stream.
	 * @see #printSimpleVerticalLine(PDPageContentStream, float, float, float, float) Print a vertical line with a specified thickness.
	 */
	private void printSimpleVerticalLine(PDPageContentStream contentStream, float x, float y1, float y2) throws IOException {
		printSimpleVerticalLine(contentStream, x, y1, y2, -1);
	}
	
	/**
	 * Print a simple vertical line with a specified <code>thickness</code>.
	 * 
	 * @param contentStream
	 *            The {@link PDPage page}'s writable steam.
	 * @param x
	 *            Vertical x.
	 * @param y1
	 *            Y start position.
	 * @param y2
	 *            Y end position.
	 * @param thickness
	 *            Line's thickness.
	 * @throws IOException
	 *             If there is an error writing to the stream.
	 * @see #printSimpleVerticalLine(PDPageContentStream, float, float, float) Print a vertical line without specifying a thickness.
	 */
	private void printSimpleVerticalLine(PDPageContentStream contentStream, float x, float y1, float y2, float thickness) throws IOException {
		if (thickness > 0) {
			contentStream.setLineWidth(thickness);
		}
		
		contentStream.moveTo(x, y1);
		contentStream.lineTo(x, y2);
		contentStream.stroke();
	}
	
	/**
	 * Print the current {@link BarReference bar reference} of this page in the header. This function also add a line under the text.
	 * 
	 * @param contentStream
	 *            The {@link PDPage page}'s writable steam.
	 * @param mediaBox
	 *            {@link PDPage Page}'s {@link PDRectangle bound}.
	 * @param barReference
	 *            Target {@link BarReference bar reference} used to print this page.
	 * @throws IOException
	 *             If there is an error writing to the stream.
	 */
	private void printHeader(PDPageContentStream contentStream, PDRectangle mediaBox, BarReference barReference) throws IOException {
		printSimpleText(contentStream, PAGE_MARGIN_HORIZONTAL, mediaBox.getHeight() - PAGE_MARGIN_VERTICAL - FONT_SIZE, (float) (FONT_SIZE * 1.5), i18n.string("exporter.word.bar-reference.format", barReference.getName()));
		printSimpleHorizontalLine(contentStream, PAGE_MARGIN_HORIZONTAL, mediaBox.getWidth() - PAGE_MARGIN_HORIZONTAL, (float) (mediaBox.getHeight() - PAGE_MARGIN_VERTICAL - (FONT_SIZE * 1.4)));
	}
	
	/**
	 * Print the copyright in the footer.
	 * 
	 * @param contentStream
	 *            The {@link PDPage page}'s writable steam.
	 * @param mediaBox
	 *            {@link PDPage Page}'s {@link PDRectangle bound}.
	 * @throws IOException
	 *             If there is an error writing to the stream.
	 */
	private void printFooter(PDPageContentStream contentStream, PDRectangle mediaBox) throws IOException {
		float baseY = PAGE_MARGIN_VERTICAL / 2f;
		float fontSize = (float) (FONT_SIZE * 0.6);
		
		String[] lines = i18n.string("application.copyright.full").split("\n");
		for (int index = 0; index < lines.length; index++) {
			String line = lines[index];
			
			/* Approximately */
			float textWidth = line.length() * (font.getAverageFontWidth() / 1000) * fontSize;
			float x = (mediaBox.getWidth() - textWidth) / 2f;
			float y = (float) (baseY - (fontSize * index * 1.5));
			
			printSimpleText(contentStream, x, y, fontSize, line);
		}
	}
	
	/**
	 * Print the current global and local page in the page's footer.
	 * 
	 * @param contentStream
	 *            The {@link PDPage page}'s writable steam.
	 * @param mediaBox
	 *            {@link PDPage Page}'s {@link PDRectangle bound}.
	 * @param globalPageCounter
	 *            Glocal page counter.
	 * @param localPageCounter
	 *            Local page counter.
	 * @throws IOException
	 *             If there is an error writing to the stream.
	 */
	private void printPageFooter(PDPageContentStream contentStream, PDRectangle mediaBox, int globalPageCounter, int localPageCounter) throws IOException {
		printSimpleText(contentStream, PAGE_MARGIN_HORIZONTAL, PAGE_MARGIN_VERTICAL * 0.8f, (float) (FONT_SIZE * 0.8), i18n.string("exporter.footer.page", localPageCounter + 1, globalPageCounter + 1));
	}
	
	/**
	 * "Convert" a {@link List list} of {@link Cut cut} to a {@link List list} of {@link String string}.
	 * 
	 * @param cuts
	 *            {@link List} of {@link Cut}.
	 * @param stringProvider
	 *            {@link Function} that must a {@link String} corresponding to a specific {@link Cut cut}.
	 * @return A {@link List list} of {@link String string}.
	 */
	private List<String> cutListToLines(List<Cut> cuts, Function<Cut, String> stringProvider) {
		List<String> lines = new ArrayList<>();
		
		cuts.forEach((cut) -> lines.add(stringProvider.apply(cut)));
		
		return lines;
	}
	
	/**
	 * Delete all {@link File} previously added in the temporary file {@link List list}.
	 * 
	 * @param temporaryFolder
	 *            Temporary folder used to store bar reference folder.
	 */
	private void cleanUpTemporaryFiles(File temporaryFolder) {
		int total = temporaryFiles.size() + 1;
		
		for (int index = 0; index < total - 1; index++) {
			temporaryFiles.get(index).delete();
			
			publishProgress(index, total);
		}
		
		FileUtils.deleteTree(temporaryFolder);
		publishProgress(total, total);
	}
	
	/**
	 * Save the {@link PDDocument} to a {@link File} and close the object.
	 * 
	 * @param file
	 *            Destination file to save to.
	 * @throws IOException
	 *             If the output could not be written.
	 */
	private void finishDocument(File file) throws IOException {
		document.save(file);
		document.close();
	}
	
	private void notifyInitialization() {
		if (callback != null) {
			callback.onInitialization(ETA_COUNT);
			notifyNextEta(ETA_INITIALIZING);
		}
	}
	
	private void notifyNextEta(String eta) {
		if (callback != null) {
			callback.onNextEta(eta);
		}
	}
	
	private void publishProgress(int current, int max) {
		if (callback != null) {
			callback.onProgressPublished(current, max);
		}
	}
	
	private void notifyFinish(File file) {
		if (callback != null) {
			callback.onFinished(file);
		}
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public String formatFilename(File originalFile) {
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		Language.get().initialize();
		File file = new File("null.pdf");
		
		BarReference dummy = new BarReference("Hello", new ArrayList<>());
		dummy.optimize(UnoptimizedCutList.fromCutTableInputs(getRandomCuts(), 6500.0), new FillingCutAlgorithm());
		
		BarReference dummy2 = new BarReference("Hello 2", new ArrayList<>());
		dummy2.optimize(UnoptimizedCutList.fromCutTableInputs(getRandomCuts(), 4000.0), new FillingCutAlgorithm());
		
		new PdfDataExporter().exportToFile(Arrays.asList(dummy, dummy2), file);
		
		// BarReference dummy3 = new BarReference("Hello 2", new ArrayList<>());
		// dummy3.optimize(UnoptimizedCutList.fromCutTableInputs(getCutsAt(265.3, 22), 6500.0), new FillingCutAlgorithm());
		
		// new PdfDataExporter().exportToFile(Arrays.asList(dummy3), file);
		
		// file.deleteOnExit();
	}
	
	static List<CutTableInput> getRandomCuts() {
		List<CutTableInput> cutTableInputs = new ArrayList<>();
		
		for (int i = 0; i < 100; i++) {
			CutTableInput cutTableInput = new CutTableInput();
			
			Random random = new Random();
			
			cutTableInput.setLength(Randomizer.nextRangeInt(500, 2500));
			cutTableInput.setQuantity(Randomizer.nextRangeInt(1, 8));
			cutTableInput.setCutAngles(new int[] { random.nextBoolean() ? 90 : 45, random.nextBoolean() ? 90 : 45 });
			
			cutTableInputs.add(cutTableInput);
		}
		
		return cutTableInputs;
	}
	
	static List<CutTableInput> getCutsAt(double length, int quantity) {
		List<CutTableInput> cutTableInputs = new ArrayList<>();
		
		Random random = new Random();
		
		CutTableInput cutTableInput = new CutTableInput();
		
		cutTableInput.setLength(length);
		cutTableInput.setQuantity(quantity);
		cutTableInput.setCutAngles(new int[] { random.nextBoolean() ? 90 : 45, random.nextBoolean() ? 90 : 45 });
		
		cutTableInputs.add(cutTableInput);
		
		return cutTableInputs;
	}
	
}