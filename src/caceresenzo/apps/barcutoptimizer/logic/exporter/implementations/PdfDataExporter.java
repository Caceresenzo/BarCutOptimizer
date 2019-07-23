package caceresenzo.apps.barcutoptimizer.logic.exporter.implementations;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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

import caceresenzo.apps.barcutoptimizer.assets.Assets;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.implementations.FillingCutAlgorithm;
import caceresenzo.apps.barcutoptimizer.logic.exporter.DataExporter;
import caceresenzo.apps.barcutoptimizer.models.BarReference;
import caceresenzo.apps.barcutoptimizer.models.Cut;
import caceresenzo.apps.barcutoptimizer.models.CutGroup;
import caceresenzo.apps.barcutoptimizer.models.CutTableInput;
import caceresenzo.apps.barcutoptimizer.models.UnoptimizedCutList;
import caceresenzo.apps.barcutoptimizer.ui.components.BarCutPanel;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.random.Randomizer;
import caceresenzo.libs.string.StringUtils;

public class PdfDataExporter implements DataExporter {
	
	/* Constants */
	public static final int BAR_CUT_RENDER_WIDTH = 800;
	public static final int BAR_CUT_RENDER_HEIGHT = 40;
	public static final double BAR_CUT_IMAGE_HEIGHT = BAR_CUT_RENDER_HEIGHT * 0.65;
	
	public static final int PAGE_MARGIN_HORIZONTAL = 40;
	public static final int PAGE_MARGIN_VERTICAL = 40;
	
	public static final int SPACE_BETWEEN_COLUMN = (int) (PAGE_MARGIN_HORIZONTAL * 3);
	
	public static final int FONT_SIZE = 16;
	
	/* Variables */
	private PDDocument document;
	private PDFont font;
	private PDPage lastestPage;
	
	private final List<File> temporaryFiles;
	
	/* Constructor */
	public PdfDataExporter() {
		this.temporaryFiles = new ArrayList<>();
	}
	
	@Override
	public void exportToFile(List<BarReference> barReferences, File file) throws Exception {
		File tempFolder = new File("temp", UUID.randomUUID().toString());
		
		prepareNewDocument();
		
		for (BarReference barReference : barReferences) {
			File barReferenceBaseFolder = new File(tempFolder, FileUtils.replaceIllegalChar(barReference.getName()));
			
			List<CutGroup> cutGroups = barReference.getCutGroups();
			ListIterator<CutGroup> iterator = cutGroups.listIterator();
			while (iterator.hasNext()) {
				PDPage page = createPage();
				PDRectangle mediaBox = page.getMediaBox();
				final int maxY = (int) (mediaBox.getHeight() - PAGE_MARGIN_VERTICAL);
				int currentY = PAGE_MARGIN_VERTICAL;
				
				int localIndex = 0;
				while (currentY < maxY) {
					CutGroup cutGroup = iterator.next();
					List<Cut> cuts = cutGroup.getCuts();
					
					if (!haveEnoughSpace(cutGroup, currentY, maxY)) {
						iterator.previous();
						
						break;
					}
					
					File cutGroupFile = new File(barReferenceBaseFolder, "group-" + localIndex + ".png");
					
					try {
						saveCutGroupPicture(cutGroup, cutGroupFile);
						
						PDImageXObject pdImage = PDImageXObject.createFromFile(cutGroupFile.getAbsolutePath(), document);
						
						try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false)) {
							if (localIndex == 0) {
								contentStream.beginText();
								contentStream.setFont(font, (float) (FONT_SIZE * 1.5));
								contentStream.setLeading(14.5f);
								contentStream.newLineAtOffset(PAGE_MARGIN_HORIZONTAL, mediaBox.getHeight() - PAGE_MARGIN_VERTICAL - FONT_SIZE);
								contentStream.showText("REF. " + barReference.getName());
								contentStream.endText();
								
								currentY += FONT_SIZE * 2;
							}
							
							currentY += BAR_CUT_IMAGE_HEIGHT;
							float inversedY = mediaBox.getHeight() - currentY;
							
							contentStream.drawImage(pdImage, PAGE_MARGIN_HORIZONTAL, inversedY, mediaBox.getWidth() - PAGE_MARGIN_HORIZONTAL * 2, (int) BAR_CUT_IMAGE_HEIGHT);
							
							int usedY1 = printTextColumn(contentStream, PAGE_MARGIN_HORIZONTAL, inversedY, "BARRE", Arrays.asList("N°" + (localIndex + 1), cutGroup.getBarLength() + "mm", cutGroup.getCutCount() + " elements"));
							
							int usedY2 = 0; /* Set 3 times just for the look ;) */
							usedY2 = printTextColumn(contentStream, PAGE_MARGIN_HORIZONTAL + SPACE_BETWEEN_COLUMN, inversedY, "LONGUEUR", cutListToLines(cuts, (cut) -> StringUtils.prefill(String.valueOf(cut.getLength()), " ", 8)));
							usedY2 = printTextColumn(contentStream, PAGE_MARGIN_HORIZONTAL + SPACE_BETWEEN_COLUMN * 2, inversedY, "ANGLE A", cutListToLines(cuts, (cut) -> StringUtils.prefill(cut.getCutAngleA() + "°", " ", 7)));
							usedY2 = printTextColumn(contentStream, PAGE_MARGIN_HORIZONTAL + SPACE_BETWEEN_COLUMN * 3, inversedY, "ANGLE B", cutListToLines(cuts, (cut) -> StringUtils.prefill(cut.getCutAngleB() + "°", " ", 7)));
							
							int usedY = Math.max(usedY1, usedY2);
							
							contentStream.moveTo(PAGE_MARGIN_HORIZONTAL, (float) (inversedY - (FONT_SIZE * 1.4)));
							contentStream.lineTo(mediaBox.getWidth() - PAGE_MARGIN_HORIZONTAL, (float) (inversedY - (FONT_SIZE * 1.4)));
							contentStream.stroke();
							
							contentStream.moveTo((float) ((PAGE_MARGIN_HORIZONTAL + SPACE_BETWEEN_COLUMN) * 0.91), (float) (inversedY - 5));
							contentStream.lineTo((float) ((PAGE_MARGIN_HORIZONTAL + SPACE_BETWEEN_COLUMN) * 0.91), (float) (inversedY - usedY + 10));
							contentStream.stroke();
							
							currentY += usedY;
						}
						
						System.out.println("Image inserted at Y " + currentY + ": " + cutGroupFile.getAbsolutePath());
					} catch (Exception exception) {
						exception.printStackTrace();
					}
					
					if (!iterator.hasNext()) {
						break;
					}
					
					localIndex++;
				}
			}
			
			temporaryFiles.add(barReferenceBaseFolder);
		}
		
		finishDocument(new File(tempFolder, "exported.pdf"));
		cleanUpTemporaryFiles();
		
		Runtime.getRuntime().exec("cmd /c " + new File(tempFolder, "exported.pdf").getAbsolutePath());
	}
	
	private boolean haveEnoughSpace(CutGroup cutGroup, int currentY, int maxY) {
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
		
		return currentY + theoreticalUsedY < maxY;
	}
	
	private PDDocument createDocument() {
		return document = new PDDocument();
	}
	
	private void loadFont() throws IOException {
		font = PDType0Font.load(document, PdfDataExporter.class.getResourceAsStream(Assets.FONT_CONSOLA));
	}
	
	private void prepareNewDocument() throws IOException {
		createDocument();
		loadFont();
	}
	
	private PDPage createPage() {
		PDPage page = lastestPage = new PDPage();
		document.addPage(page);
		
		return page;
	}
	
	private BufferedImage renderCutGroup(CutGroup cutGroup) throws IOException {
		JPanel panel = new BarCutPanel(cutGroup);
		
		panel.updateUI();
		panel.setSize(BAR_CUT_RENDER_WIDTH, BAR_CUT_RENDER_HEIGHT);
		
		BufferedImage bufferedImage = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = bufferedImage.getGraphics();
		
		panel.paint(graphics);
		
		graphics.dispose();
		
		return bufferedImage;
	}
	
	private void saveCutGroupPicture(CutGroup cutGroup, File file) throws IOException {
		FileUtils.forceFileCreation(file);
		ImageIO.write(renderCutGroup(cutGroup), "png", file);
		
		temporaryFiles.add(file);
	}
	
	private void printTextHeader(PDPageContentStream contentStream, float x, float inversedY, String columnText) throws IOException {
		contentStream.beginText();
		contentStream.setFont(font, FONT_SIZE);
		contentStream.setLeading(14.5f);
		contentStream.newLineAtOffset(x, inversedY - FONT_SIZE);
		contentStream.showText(columnText);
		contentStream.endText();
	}
	
	private int printTextColumn(PDPageContentStream contentStream, float x, float inversedY, String columnText, List<String> lines) throws IOException {
		printTextHeader(contentStream, x, inversedY, columnText);
		
		int usedY = FONT_SIZE * 2;
		
		{
			contentStream.beginText();
			contentStream.setFont(font, FONT_SIZE);
			contentStream.setLeading(14.5f);
			contentStream.newLineAtOffset(x, inversedY - usedY - (FONT_SIZE / 2));
			
			for (String line : lines) {
				contentStream.showText(line);
				contentStream.newLine();
			}
			
			contentStream.endText();
		}
		
		usedY += lines.size() * FONT_SIZE;
		
		return usedY;
	}
	
	private List<String> cutListToLines(List<Cut> cuts, Function<Cut, String> stringProvider) {
		List<String> lines = new ArrayList<>();
		
		cuts.forEach((cut) -> lines.add(stringProvider.apply(cut)));
		
		return lines;
	}
	
	private void cleanUpTemporaryFiles() {
		for (File file : temporaryFiles) {
			file.delete();
		}
	}
	
	private void finishDocument(File file) throws IOException {
		document.save(file);
		document.close();
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
		BarReference dummy = new BarReference("Hello", new ArrayList<>());
		dummy.optimize(UnoptimizedCutList.fromCutTableInputs(getRandomCuts(), 6500.0), new FillingCutAlgorithm());
		
		BarReference dummy2 = new BarReference("Hello 2", new ArrayList<>());
		dummy2.optimize(UnoptimizedCutList.fromCutTableInputs(getRandomCuts(), 4000.0), new FillingCutAlgorithm());
		
		new PdfDataExporter().exportToFile(Arrays.asList(dummy, dummy2), null);
	}
	
	static List<CutTableInput> getRandomCuts() {
		List<CutTableInput> cutTableInputs = new ArrayList<>();
		
		for (int i = 0; i < 10; i++) {
			CutTableInput cutTableInput = new CutTableInput();
			
			Random random = new Random();
			
			cutTableInput.setLength(Randomizer.nextRangeInt(500, 2500));
			cutTableInput.setQuantity(Randomizer.nextRangeInt(1, 8));
			cutTableInput.setCutAngles(new int[] { random.nextBoolean() ? 90 : 45, random.nextBoolean() ? 90 : 45 });
			
			cutTableInputs.add(cutTableInput);
		}
		
		return cutTableInputs;
	}
	
}