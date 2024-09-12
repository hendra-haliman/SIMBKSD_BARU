// Photoshop JavaScript to remove the background of an image

// Ensure a document is open
if (app.documents.length > 0) {
    var doc = app.activeDocument;

    // Set the background color to select, typically white
    var whiteColor = new SolidColor();
    whiteColor.rgb.red = 255;
    whiteColor.rgb.green = 255;
    whiteColor.rgb.blue = 255;

    // Select the background with the magic wand tool
    var wandTool = doc.selection;
    wandTool.selectSimilar(whiteColor);

    // Expand the selection by a few pixels to ensure the complete background is selected
    wandTool.expand(5);

    // Delete the selected area (background)
    wandTool.clear();

    // Deselect the current selection
    doc.selection.deselect();

    // Optional: Save the document or do further processing
    // doc.saveAs(new File("/path/to/save/image.psd"));
} else {
    alert("No document is open!");
}
