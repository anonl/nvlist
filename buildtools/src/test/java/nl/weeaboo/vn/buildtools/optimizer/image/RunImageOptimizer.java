package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.badlogic.gdx.graphics.Pixmap;
import com.google.common.io.Files;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.buildtools.gdx.HeadlessGdx;
import nl.weeaboo.vn.buildtools.optimizer.image.encoder.JngEncoder;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.gdx.graphics.PixmapLoader;
import nl.weeaboo.vn.impl.InitConfig;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;

final class RunImageOptimizer {

    /**
     * Test runner for the image optimizer pipeline.
     */
    public static void main(String[] args) throws IOException {
        InitConfig.init();
        HeadlessGdx.init();

        File dstFolder = new File("tmp");
        dstFolder.mkdirs();

        // NVList root project
        ProjectFolderConfig folderConfig = new ProjectFolderConfig(Paths.get("."), Paths.get("."));
        try (NvlistProjectConnection connection = NvlistProjectConnection.openProject(folderConfig)) {
            IFileSystem resFileSystem = connection.getResFileSystem();

            String filename = "testjng.jng";
            Pixmap pixmap = PixmapLoader.load(resFileSystem, FilePath.of("img/" + filename));
            ImageWithDef imageWithDef = new ImageWithDef(pixmap, new ImageDefinition(filename,
                    Dim.of(pixmap.getWidth(), pixmap.getHeight())));

            ImageResizer resizer = new ImageResizer(Dim.of(1920, 1080), Dim.of(960, 540));
            ImageWithDef optimized = resizer.process(imageWithDef);
            System.out.println(optimized.getDef());

            JngEncoder jngEncoder = new JngEncoder();
            EncodedImage encoded = jngEncoder.encode(optimized);

            Files.write(encoded.readBytes(), new File(dstFolder, "out.jng"));
        }
    }

}
