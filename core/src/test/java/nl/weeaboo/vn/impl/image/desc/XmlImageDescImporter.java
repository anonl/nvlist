package nl.weeaboo.vn.impl.image.desc;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.image.desc.GLScaleFilter;
import nl.weeaboo.vn.image.desc.GLTilingMode;
import nl.weeaboo.vn.image.desc.IImageDefinition;

/**
 * Imports NVList3 img.xml
 */
public class XmlImageDescImporter {

    /**
     * Use: XmlImageDescImporter path1 [path2] ... [pathN]
     * <p>
     * Reads any number of NVList 3 img.xml files and converts them to NVList 4 img.json files.
     *
     * @throws Exception If an error occurs.
     */
    public static void main(String[] args) throws Exception {
        XmlImageDescImporter importer = new XmlImageDescImporter();
        for (String path : args) {
            Map<FilePath, IImageDefinition> result = importer.importXml(new File(path));
            String json = ImageDefinitionIO.serialize(result.values());

            System.out.println("=== Parse result for " + path + " ===");
            System.out.println(json);
        }
    }

    private Map<FilePath, IImageDefinition> importXml(File xmlFile)
            throws ParserConfigurationException, SAXException, IOException {

        Map<FilePath, IImageDefinition> result = Maps.newHashMap();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse(xmlFile);
        Element root = dom.getDocumentElement();
        NodeList imageEList = root.getElementsByTagName("image");
        for (int a = 0; a < imageEList.getLength(); a++) {
            Element imageE = (Element)imageEList.item(a);

            final FilePath filePath = FilePath.of(imageE.getAttribute("filename"));

            // Image dimensions
            int width = Integer.parseInt(imageE.getAttribute("width"));
            int height = Integer.parseInt(imageE.getAttribute("height"));
            final Dim size = Dim.of(width, height);

            ImageDefinitionBuilder b = new ImageDefinitionBuilder(filePath.toString(), size);

            // Get scale filters
            List<String> scaleFilters = Splitter.on(',')
                    .trimResults()
                    .omitEmptyStrings()
                    .splitToList(Strings.nullToEmpty(imageE.getAttribute("scaleFilter")));

            if (scaleFilters.size() >= 1) {
                GLScaleFilter filter = GLScaleFilter.fromString(scaleFilters.get(0));
                b.setMinifyFilter(filter);
                b.setMagnifyFilter(filter);
            }

            if (scaleFilters.size() >= 2) {
                b.setMagnifyFilter(GLScaleFilter.fromString(scaleFilters.get(1)));
            }

            // Get tiling mode
            List<String> tileModes = Splitter.on(',')
                    .trimResults()
                    .omitEmptyStrings()
                    .splitToList(Strings.nullToEmpty(imageE.getAttribute("tile")));

            if (tileModes.size() >= 1) {
                GLTilingMode tileMode = GLTilingMode.fromString(tileModes.get(0));
                b.setTilingModeX(tileMode);
                b.setTilingModeY(tileMode);
            }

            if (tileModes.size() >= 2) {
                b.setTilingModeY(GLTilingMode.fromString(tileModes.get(1)));
            }

            // Get crop rects
            NodeList subrectEList = imageE.getElementsByTagName("subrect");
            for (int r = 0; r < subrectEList.getLength(); r++) {
                Element subrectE = (Element)subrectEList.item(r);
                String id = subrectE.getAttribute("id");

                List<String> rectString = Splitter.on(',')
                        .trimResults()
                        .splitToList(Strings.nullToEmpty(subrectE.getAttribute("rect")));

                int x = Integer.parseInt(rectString.get(0));
                int y = Integer.parseInt(rectString.get(1));
                int w = Integer.parseInt(rectString.get(2));
                int h = Integer.parseInt(rectString.get(3));
                b.addSubRect(new ImageSubRect(id, Area.of(x, y, w, h)));
            }

            result.put(filePath, b.build());
        }

        return result;
    }
}
